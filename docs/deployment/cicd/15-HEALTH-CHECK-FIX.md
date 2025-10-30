# Health Check 문제 해결 및 성공적인 배포

**날짜**: 2025-10-30  
**상태**: ✅ 완료  
**결과**: AWS ECS 배포 성공, Target Group Health Check 통과

---

## 📋 목차

1. [문제 발견](#문제-발견)
2. [원인 분석](#원인-분석)
3. [해결 과정](#해결-과정)
4. [수정된 파일](#수정된-파일)
5. [배포 절차](#배포-절차)
6. [검증 결과](#검증-결과)

---

## 🚨 문제 발견

### 증상
```
Target Group (library-blue-tg) Health Check 실패
- Task 상태: Container는 정상이지만 Target은 Unhealthy
- 에러: Health checks failed with these codes: [302]
- Task가 반복적으로 중지됨
```

### 로그 분석
```
반복되는 패턴 (매 30초):
1. ALB가 "/" 경로로 Health Check 요청
2. Spring Security가 인증되지 않은 요청 감지
3. "/auth/login"으로 리다이렉트 (HTTP 302)
4. ALB는 200 코드 기대 → 302 수신 → Health Check 실패
```

---

## 🔍 원인 분석

### 문제 1: Task Definition Health Check 명령어 불일치

**Dockerfile**:
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1
```
✅ **wget 사용**

**task-definition.json** (초기 버전):
```json
"healthCheck": {
  "command": [
    "CMD-SHELL",
    "curl -f http://localhost:8081/actuator/health || exit 1"
  ]
}
```
❌ **curl 사용** (Alpine Linux에 미설치)

### 문제 2: Target Group Health Check 경로

**Target Group 설정**:
```
Health Check Path: /
Success codes: 200
```

**실제 동작**:
```
ALB → "/" 요청 → Spring Security 인증 필요 → 302 Redirect → 실패
```

### 문제 3: Spring Security 설정

**SecurityConfig.java** (초기 버전):
```java
.requestMatchers("/", "/home").permitAll()
.requestMatchers("/css/**", "/images/**").permitAll()
.requestMatchers("/auth/**", "/register", "/login").permitAll()
// /actuator/** 경로가 없음!
.requestMatchers("/boards/**").permitAll()
.anyRequest().authenticated(); // /actuator/health도 인증 필요!
```

---

## 🔧 해결 과정

### 1단계: Task Definition Health Check 수정 ✅

**파일**: `task-definition.json`

**변경 내용**:
```diff
"healthCheck": {
  "command": [
    "CMD-SHELL",
-   "curl -f http://localhost:8081/actuator/health || exit 1"
+   "wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1"
  ],
  "interval": 30,
  "timeout": 5,
  "retries": 3,
  "startPeriod": 120
}
```

**이유**: Alpine Linux 이미지에는 curl이 설치되어 있지 않지만 wget은 기본 포함

---

### 2단계: Target Group Health Check 경로 변경 ✅

**AWS Console**: EC2 → Target Groups → library-blue-tg → 상태 검사 → 편집

**변경 내용**:
```diff
프로토콜: HTTP
- 경로: /
+ 경로: /actuator/health
포트: 트래픽 포트 (8081)
```

**이유**: 
- `/` 경로는 Spring Security 인증 필요 (302 Redirect)
- `/actuator/health`는 인증 불필요하도록 설정 예정

---

### 3단계: Spring Security 설정 수정 ✅

**파일**: `src/main/java/com/library/config/SecurityConfig.java`

**변경 내용**:
```diff
.authorizeHttpRequests(authz -> {
    authz
        // 누구나 접근 가능 (로그인 불필요)
        .requestMatchers("/", "/home").permitAll()
        .requestMatchers("/css/**", "/images/**").permitAll()
        .requestMatchers("/auth/**", "/register", "/login").permitAll()
+       // Actuator Health Check (ALB용)
+       .requestMatchers("/actuator/**").permitAll()
        // 게시판 URL (목록/상세 조회는 모두 허용)
        .requestMatchers("/boards/**").permitAll()
```

**이유**: ALB Health Check가 인증 없이 `/actuator/health`에 접근할 수 있도록 허용

---

## 📄 수정된 파일

### 1. task-definition.json
```json
{
  "family": "library-management-task",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "executionRoleArn": "arn:aws:iam::011587325937:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "app",
      "image": "011587325937.dkr.ecr.ap-northeast-2.amazonaws.com/library-management-system:latest",
      "portMappings": [
        {
          "containerPort": 8081,
          "protocol": "tcp"
        }
      ],
      "essential": true,
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "secrets": [
        {
          "name": "SPRING_DATASOURCE_URL",
          "valueFrom": "arn:aws:ssm:ap-northeast-2:011587325937:parameter/library/db/url"
        },
        {
          "name": "SPRING_DATASOURCE_USERNAME",
          "valueFrom": "arn:aws:ssm:ap-northeast-2:011587325937:parameter/library/db/username"
        },
        {
          "name": "SPRING_DATASOURCE_PASSWORD",
          "valueFrom": "arn:aws:ssm:ap-northeast-2:011587325937:parameter/library/db/password"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/library-management-task",
          "awslogs-region": "ap-northeast-2",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "healthCheck": {
        "command": [
          "CMD-SHELL",
          "wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1"
        ],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 120
      }
    }
  ]
}
```

### 2. SecurityConfig.java (관련 부분만)
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    log.info("=== Spring Security 필터 체인 설정 시작 === ");

    http
        .authenticationProvider(authenticationProvider())
        .csrf(csrf -> {
            csrf.ignoringRequestMatchers("/api/**");
            log.info("1. CSRF 보호 활성화 (REST API는 제외)");
        })
        .authorizeHttpRequests(authz -> {
            authz
                // 누구나 접근 가능 (로그인 불필요)
                .requestMatchers("/", "/home").permitAll()
                .requestMatchers("/css/**", "/images/**").permitAll()
                .requestMatchers("/auth/**", "/register", "/login").permitAll()
                // Actuator Health Check (ALB용)
                .requestMatchers("/actuator/**").permitAll()
                // 게시판 URL (목록/상세 조회는 모두 허용)
                .requestMatchers("/boards/**").permitAll()
                
                // 댓글 API 권한 설정
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/comments/**").permitAll()
                .requestMatchers("/api/comments/**").authenticated()
                
                // 좋아요 API 권한 설정
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/boards/*/like").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/boards/*/like").authenticated()

                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated();
            log.info("2. URL 권한 설정 완료");
        })
        // ... 나머지 설정
    
    log.info("=== Spring Security 필터 체인 설정 완료 ===");
    return http.build();
}
```

---

## 🚀 배포 절차

### 1. Docker 이미지 빌드 및 ECR 푸시

```powershell
# 프로젝트 디렉토리 이동
cd "C:\Users\USER\Documents\choongang\Project\lecture\trainer\library-management-system (27)"

# ECR 로그인
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 011587325937.dkr.ecr.ap-northeast-2.amazonaws.com

# Docker 이미지 빌드
docker build -t library-management-system .

# 이미지 태그
docker tag library-management-system:latest 011587325937.dkr.ecr.ap-northeast-2.amazonaws.com/library-management-system:latest

# ECR 푸시
docker push 011587325937.dkr.ecr.ap-northeast-2.amazonaws.com/library-management-system:latest
```

**결과**:
```
Pushed to: 011587325937.dkr.ecr.ap-northeast-2.amazonaws.com/library-management-system:latest
Digest: sha256:2cfa4dcc7883cdea8dcfa54a77f10846848a21655170f3ee2a5aa893bb8887d7
Size: 856 MB
```

### 2. Task Definition 등록

**AWS Console**: ECS → Task Definitions → library-task → Create new revision

- JSON 탭 선택
- task-definition.json 내용 붙여넣기
- Create 버튼 클릭

**결과**: library-task:3 생성됨

### 3. Target Group Health Check 경로 변경

**AWS Console**: EC2 → Target Groups → library-blue-tg → 상태 검사 → 편집

```
경로: / → /actuator/health
```

### 4. ECS Service 업데이트

**AWS Console**: ECS → Clusters → library-management-cluster → Services → library-service

```
Update service
→ Task Definition: library-task:3 선택
→ Force new deployment: ✅
→ Update
```

---

## ✅ 검증 결과

### Container Health Check
```
Task ID: 91a7a0534bc44235a02c4042fa4590b6
상태: ✅ 정상
Health Check: wget 명령어 성공
```

### Target Group Health Check
```
Target: 172.31.7.45:8081
상태: ✅ Healthy
Health Check 경로: /actuator/health
응답 코드: 200 OK
```

### 애플리케이션 로그
```
2025-10-30T02:32:29.330Z INFO Started LibraryManagementSystemApplication in 84.304 seconds
2025-10-30T02:32:29.124Z INFO Tomcat started on port 8081 (http) with context path '/'
2025-10-30T02:32:21.824Z INFO Exposing 2 endpoints beneath base path '/actuator'
```

### ALB 접속 테스트
```
✅ ALB DNS를 통한 접속 성공
✅ 애플리케이션 홈페이지 정상 표시
✅ Health Check 지속적으로 통과
```

---

## 📊 최종 구성

### 인프라 구성도
```
Internet
    ↓
Application Load Balancer (library-alb)
    ↓ (Health Check: /actuator/health)
Target Group (library-blue-tg)
    ↓ (port 8081)
ECS Service (library-service)
    ↓
ECS Task (library-task:3)
    ↓
Docker Container (Spring Boot)
    ↓
RDS MySQL (library-database)
```

### Health Check 흐름
```
1. Container Health Check (Docker)
   - 명령어: wget --spider http://localhost:8081/actuator/health
   - 간격: 30초
   - 시작 유예: 60초
   
2. Target Group Health Check (ALB)
   - 경로: /actuator/health
   - 간격: 30초
   - 성공 임계값: 5회 연속
   - Spring Security: permitAll() 설정
```

---

## 🎓 교훈

### 1. Alpine Linux 환경 고려
- curl이 기본 설치되어 있지 않음
- wget 사용 권장
- 또는 Dockerfile에서 curl 명시적 설치 필요

### 2. Health Check 경로 선택
- 인증이 필요 없는 경로 사용
- Actuator health endpoint 활용
- Spring Security 설정에서 명시적 허용

### 3. 다층 Health Check
- Container Health Check: Docker 레벨
- Target Group Health Check: ALB 레벨
- 둘 다 통과해야 정상 작동

### 4. 이미지 재빌드 필수
- 소스 코드 수정 시 Docker 이미지 재빌드 필수
- Force new deployment만으로는 코드 변경 반영 안 됨
- ECR 푸시까지 완료해야 함

---

## 🔜 다음 단계

### Phase 4: GitHub Actions CI/CD 구성
1. GitHub Actions Workflow 작성
2. AWS 자격 증명 설정
3. 자동 빌드 및 배포 파이프라인
4. Blue-Green 배포 자동화

---

## 📚 참고 자료

- [AWS ECS Task Definition Health Check](https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_HealthCheck.html)
- [Spring Boot Actuator Health Endpoint](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.health)
- [Spring Security Configuration](https://docs.spring.io/spring-security/reference/servlet/configuration/java.html)
- [Docker HEALTHCHECK](https://docs.docker.com/engine/reference/builder/#healthcheck)
