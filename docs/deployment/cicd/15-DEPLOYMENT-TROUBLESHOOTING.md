# 🔧 배포 문제 해결 및 애플리케이션 정상화

> **작업 일시**: 2025-10-30 00:00 - 00:30  
> **작업 내용**: ECS 배포 후 발생한 문제 진단 및 해결  
> **결과**: 애플리케이션 정상 작동 확인 ✅

---

## 📋 목차

1. [문제 발견](#문제-발견)
2. [문제 진단](#문제-진단)
3. [해결 과정](#해결-과정)
4. [테스트 및 검증](#테스트-및-검증)
5. [추가 개선 사항](#추가-개선-사항)
6. [교훈 및 체크리스트](#교훈-및-체크리스트)

---

## 🔍 문제 발견

### 초기 상황

**ECS Task 상태:**
```
✅ Task 상태: RUNNING
✅ Container 상태: Running
❌ Health 상태: unhealthy
❌ Target Group: unhealthy
```

**접속 테스트 결과:**
```
✅ /actuator/health → 성공 (200 OK)
❌ / (홈페이지) → 에러 페이지
❌ /boards (게시판) → 에러 페이지
```

### 증상 분석

- Health Check 엔드포인트는 정상 응답
- 하지만 일반 페이지는 모두 에러
- CloudWatch Logs에는 애플리케이션 시작 로그가 정상적으로 출력됨

---

## 🔬 문제 진단

### Step 1: CloudWatch Logs 분석

**로그 확인 결과:**

```bash
# 홈 컨트롤러 접근 로그
2025-10-29T14:41:24.183Z INFO --- [nio-8081-exec-4] 
com.library.controller.HomeController : 홈페이지 접근 [Profile: dev]
```

**문제점 발견!**
```
Profile: dev  ← 개발 프로파일로 실행 중!
```

### Step 2: 프로파일 설정 확인

**기대값:**
```
Profile: prod  (운영 환경)
```

**실제값:**
```
Profile: dev  (개발 환경)
```

### Step 3: 근본 원인 파악

#### Task Definition 환경변수 확인
```json
"environment": [
  {
    "name": "SPRING_PROFILES_ACTIVE",
    "value": "prod"
  }
]
```
→ Task Definition은 정상 ✅

#### application.yml 확인
```yaml
spring:
  application:
    name: library-management-system

  profiles:
    active: dev   # ← 문제 발견!
```

**근본 원인:**
- `application.yml`에 `profiles.active: dev`가 하드코딩됨
- Spring Boot의 속성 우선순위에서 **application.yml > 환경변수**
- 따라서 환경변수 `SPRING_PROFILES_ACTIVE=prod`가 무시됨

### Step 4: 영향 분석

**dev 프로파일로 실행되면서 발생한 문제:**

```yaml
# dev 프로파일 설정
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/librarydb
    username: root
    password: 12345
```

**문제점:**
1. ❌ `localhost:3306` MySQL 접속 시도
   - ECS Task에는 MySQL이 없음
   - 데이터베이스 연결 실패

2. ❌ Parameter Store의 RDS 정보 무시
   - `SPRING_DATASOURCE_URL` (RDS 엔드포인트)
   - `SPRING_DATASOURCE_USERNAME` (admin)
   - `SPRING_DATASOURCE_PASSWORD` (암호화된 비밀번호)

3. ❌ 결과적으로 DB 의존 페이지 모두 오류
   - 홈페이지 (통계 데이터 조회)
   - 게시판 (DB 쿼리)

---

## 🛠️ 해결 과정

### Step 1: application.yml 수정

**변경 전:**
```yaml
spring:
  application:
    name: library-management-system

  profiles:
    active: dev   # 고정값
```

**변경 후:**
```yaml
spring:
  application:
    name: library-management-system

  # profiles.active는 환경변수로 주입 (로컬: dev, ECS: prod)
  # 로컬 실행: -Dspring.profiles.active=dev
  # ECS 실행: SPRING_PROFILES_ACTIVE=prod (Task Definition)
```

**변경 이유:**
- 환경변수로 프로파일을 제어하도록 변경
- 로컬 개발과 ECS 운영 환경을 환경변수로 구분
- application.yml에서 하드코딩 제거

**파일 경로:**
```
src/main/resources/application.yml
```

### Step 2: Git 커밋 및 푸시

```bash
git add .
git commit -m "Fix: Remove hardcoded spring.profiles.active from application.yml"
git push origin feature/cicd-ecs-blue-green-deployment
```

### Step 3: Docker 이미지 재빌드

```bash
# 1. Gradle 빌드
./gradlew clean build -x test

# 2. Docker 이미지 빌드
docker build -t library-management-system .

# 3. ECR 로그인
aws ecr get-login-password --region ap-northeast-2 | \
  docker login --username AWS --password-stdin \
  011587325937.dkr.ecr.ap-northeast-2.amazonaws.com

# 4. 태그 지정
docker tag library-management-system:latest \
  011587325937.dkr.ecr.ap-northeast-2.amazonaws.com/library-management-system:latest

# 5. ECR 푸시
docker push 011587325937.dkr.ecr.ap-northeast-2.amazonaws.com/library-management-system:latest
```

**결과:**
```
✅ Docker 이미지 빌드 성공
✅ ECR 푸시 완료
✅ latest 태그 업데이트
```

### Step 4: ECS Service 강제 재배포

**AWS Console 작업:**
```
1. ECS → Clusters → library-management-cluster
2. Services → library-service → "업데이트" 버튼
3. "강제로 새 배포" 체크
4. "업데이트" 클릭
```

**배포 프로세스:**
```
1. 새로운 Task 시작 (latest 이미지 pull)
2. Health Check 대기 (60초 grace period)
3. 새 Task가 healthy 되면 이전 Task 종료
4. 무중단 배포 완료
```

---

## ✅ 테스트 및 검증

### Step 1: CloudWatch Logs 확인

**검색 키워드:** `profile`

**확인 결과:**
```bash
2025-10-29T15:17:20.580Z INFO 1 --- [main] 
c.l.LibraryManagementSystemApplication : 
The following 1 profile is active: "prod"
```

✅ **prod 프로파일로 정상 실행 확인!**

### Step 2: 애플리케이션 시작 확인

**검색 키워드:** `Started LibraryManagementSystemApplication`

**확인 결과:**
```bash
2025-10-29T15:18:40.188Z INFO 1 --- [main] 
c.l.LibraryManagementSystemApplication : 
Started LibraryManagementSystemApplication in 88.002 seconds 
(process running for 94.565)
```

✅ **애플리케이션 시작 완료 (88초 소요)**

### Step 3: 웹 접속 테스트

#### Test 1: Health Check
```bash
URL: http://library-alb-1681303708.ap-northeast-2.elb.amazonaws.com/actuator/health
```

**결과:**
```json
{
  "status": "UP"
}
```
✅ **성공**

#### Test 2: 홈페이지
```bash
URL: http://library-alb-1681303708.ap-northeast-2.elb.amazonaws.com/
```

**결과:**
- 도서관 관리 시스템 홈페이지 정상 로딩
- 통계 데이터 표시 (총 도서, 회원, 대여 등)
- 프로파일 정보 표시 없음 (prod 환경)

✅ **성공**

#### Test 3: 게시판
```bash
URL: http://library-alb-1681303708.ap-northeast-2.elb.amazonaws.com/boards
```

**결과:**
- 게시판 목록 페이지 정상 표시
- 페이징 기능 작동
- RDS MySQL 쿼리 정상 실행

✅ **성공**

### Step 4: 보안 검증

#### Test 4: Public IP 직접 접속
```bash
URL: http://57.180.148.163:8081/actuator/health
```

**결과:**
```
연결 시간 초과 (Connection Timeout)
```

✅ **정상** - Security Group이 ALB에서만 접근 허용

**보안 아키텍처 확인:**
```
인터넷 → ALB (library-alb-sg) ✅
       → ECS Task (library-ecs-task-sg) ✅
       → RDS MySQL (library-rds-sg) ✅

인터넷 → ECS Task 직접 접근 ❌ (차단됨)
```

---

## 🔧 추가 개선 사항

### 문제: Health Check 초기 실패

**증상:**
```
Task 상태: RUNNING
Container 상태: Running
Health 상태: unhealthy (초기)
```

**원인 분석:**
```
Health Check startPeriod: 60초
애플리케이션 시작 시간: 88초

→ 60초 후부터 Health Check 시작
→ 하지만 88초에 애플리케이션 완료
→ 60~88초 사이 Health Check 실패
```

### 해결: task-definition.json 수정

**변경 전:**
```json
"healthCheck": {
  "command": [
    "CMD-SHELL",
    "curl -f http://localhost:8081/actuator/health || exit 1"
  ],
  "interval": 30,
  "timeout": 5,
  "retries": 3,
  "startPeriod": 60
}
```

**변경 후:**
```json
"healthCheck": {
  "command": [
    "CMD-SHELL",
    "curl -f http://localhost:8081/actuator/health || exit 1"
  ],
  "interval": 30,
  "timeout": 5,
  "retries": 3,
  "startPeriod": 120
}
```

**변경 사항:**
- `startPeriod`: 60 → 120 (초)
- 애플리케이션 시작에 충분한 시간 확보
- 초기 Health Check 실패 방지

**적용 방법:**
1. AWS Console → ECS → Task Definitions
2. library-task → "새 개정 생성"
3. JSON 탭에서 수정된 내용 붙여넣기
4. "생성" → library-task:3 생성
5. Service 업데이트 시 개정 3 사용

---

## 📚 교훈 및 체크리스트

### 핵심 교훈

**1. 환경별 설정 관리**
```
❌ 나쁜 방법: application.yml에 하드코딩
✅ 좋은 방법: 환경변수로 주입
```

**2. Spring Boot 속성 우선순위 이해**
```
우선순위 (높음 → 낮음):
1. 커맨드 라인 인자
2. Java 시스템 속성
3. OS 환경 변수
4. application.yml/properties
```

**3. 로컬 개발 환경 설정**

**IntelliJ에서 dev 프로파일 실행:**

**방법 1: Environment Variables**
```
Run → Edit Configurations
→ Environment variables: SPRING_PROFILES_ACTIVE=dev
```

**방법 2: VM Options**
```
Run → Edit Configurations
→ VM options: -Dspring.profiles.active=dev
```

**방법 3: Application Arguments**
```
Run → Edit Configurations
→ Program arguments: --spring.profiles.active=dev
```

### 배포 전 체크리스트

**코드 레벨:**
```
✅ application.yml에 환경별 하드코딩 없음
✅ 환경변수로 설정 관리
✅ 민감 정보는 Parameter Store/Secrets Manager 사용
✅ 로깅 레벨 적절히 설정 (dev: DEBUG, prod: INFO)
```

**Docker 레벨:**
```
✅ Dockerfile에서 환경변수 전달 확인
✅ 로컬에서 docker-compose로 테스트
✅ 환경변수로 프로파일 제어 검증
```

**AWS 레벨:**
```
✅ Task Definition의 environment 설정 확인
✅ secrets로 민감 정보 주입 확인
✅ Health Check 시간 설정 적절한지 확인
✅ Security Group 규칙 검증
```

**배포 후 검증:**
```
✅ CloudWatch Logs에서 프로파일 확인
✅ 애플리케이션 시작 시간 확인
✅ Health Check 통과 확인
✅ 실제 기능 접속 테스트
✅ 보안 설정 검증 (Public IP 차단 등)
```

### 디버깅 팁

**1. 프로파일 확인:**
```bash
# CloudWatch Logs 검색
"profile" 또는 "active"
```

**2. 애플리케이션 시작 확인:**
```bash
# CloudWatch Logs 검색
"Started LibraryManagementSystemApplication"
```

**3. 데이터베이스 연결 확인:**
```bash
# CloudWatch Logs 검색
"HikariPool" 또는 "database"
```

**4. 오류 확인:**
```bash
# CloudWatch Logs 검색
"ERROR" 또는 "Exception"
```

---

## 📊 최종 상태

### 리소스 현황

**ECS Task:**
```
Task ID: f4952b8c5c074300b47de4b82f2c3cdb
상태: RUNNING ✅
Profile: prod ✅
시작 시간: 88초
RDS 연결: 성공 ✅
```

**Target Group:**
```
Target Group: library-blue-tg
등록된 대상: 1개
Health 상태: healthy (시간 경과 후)
Health Check: /actuator/health
```

**애플리케이션 접속:**
```
✅ /actuator/health → 200 OK
✅ / → 홈페이지 정상
✅ /boards → 게시판 정상
```

### 수정된 파일 목록

```
1. src/main/resources/application.yml
   - profiles.active 하드코딩 제거
   - 환경변수로 프로파일 제어

2. task-definition.json
   - healthCheck.startPeriod: 60 → 120
   - 다음 배포 시 library-task:3로 적용 예정
```

---

## 🎯 다음 단계

### 즉시 작업

**1. Task Definition 개정 3 등록**
```
- startPeriod: 120으로 수정된 버전
- Health Check 안정성 확보
```

**2. ECS Service 업데이트**
```
- library-task:3으로 변경
- 강제 재배포
- Health Check 성공 확인
```

### Phase 4 완료를 위한 작업

**3. GitHub Actions 워크플로우 작성**
```
파일: .github/workflows/deploy-to-ecs.yml
- 자동 빌드
- ECR 푸시
- ECS 배포
```

**4. CI/CD 파이프라인 테스트**
```
- 코드 수정
- Git Push
- 자동 배포 검증
```

---

## 📝 참고 자료

### Spring Boot 문서
- [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)

### AWS 문서
- [ECS Task Definition Parameters](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_definition_parameters.html)
- [ECS Health Checks](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_definition_parameters.html#container_definition_healthcheck)

### 관련 문서
- [14-CURRENT-PROGRESS.md](./14-CURRENT-PROGRESS.md) - 전체 진행 상황
- [00-MASTER-PLAN.md](./00-MASTER-PLAN.md) - 마스터 플랜

---

**작성일**: 2025-10-30  
**작성자**: Hojin + Claude  
**상태**: ✅ 완료  
**다음 작업**: Task Definition 개정 3 등록 및 GitHub Actions 작성
