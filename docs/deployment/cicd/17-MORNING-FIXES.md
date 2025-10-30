# Phase 4 배포 성공 후 헬스체크 개선 작업

**작업일시**: 2025년 10월 30일 오전  
**작업 브랜치**: feature/cicd-ecs-blue-green-deployment  
**작업 상태**: 완료

---

## 작업 개요

배포는 성공했으나 ECS Task의 Health Check가 실패하는 문제가 발견되어, 헬스체크 경로를 개선하고 컨테이너 헬스체크 도구를 변경하는 작업을 수행했습니다.

---

## 변경 사항 상세

### 1. SecurityConfig.java 수정

**파일 경로**: `src/main/java/com/library/config/SecurityConfig.java`

**변경 내용**: Actuator 엔드포인트 접근 허용

```java
.authorizeHttpRequests(authz -> {
    authz
            // 누구나 접근 가능 (로그인 불필요)
            .requestMatchers("/", "/home").permitAll()
            .requestMatchers("/css/**", "/images/**").permitAll()
            .requestMatchers("/auth/**", "/register", "/login").permitAll()
            // Actuator Health Check (ALB용) - 추가
            .requestMatchers("/actuator/**").permitAll()
            // 게시판 URL (목록/상세 조회는 모두 허용)
            .requestMatchers("/boards/**").permitAll()
            // ... (이하 생략)
```

**변경 이유**:
- ALB Health Check가 `/actuator/health` 경로에 접근할 수 있도록 Spring Security 인증 우회 설정
- 인증 없이 헬스체크 엔드포인트에 접근 가능하도록 허용

---

### 2. task-definition.json 수정

**파일 경로**: `task-definition.json`

**변경 내용**: 헬스체크 명령어를 curl에서 wget으로 변경

**변경 전**:
```json
"healthCheck": {
    "command": [
        "CMD-SHELL",
        "curl -f http://localhost:8081/ || exit 1"
    ],
    "interval": 30,
    "timeout": 5,
    "retries": 3,
    "startPeriod": 60
}
```

**변경 후**:
```json
"healthCheck": {
    "command": [
        "CMD-SHELL",
        "wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1"
    ],
    "interval": 30,
    "timeout": 5,
    "retries": 3,
    "startPeriod": 60
}
```

**변경 이유**:
- Alpine Linux 기반 이미지에는 curl이 기본 설치되어 있지 않음
- wget은 Alpine Linux에 기본 포함되어 있어 추가 패키지 설치 불필요
- 헬스체크 경로를 루트(`/`)에서 Spring Boot Actuator 헬스체크 엔드포인트(`/actuator/health`)로 변경하여 더 정확한 상태 확인 가능

**wget 옵션 설명**:
- `--no-verbose`: 출력 최소화
- `--tries=1`: 한 번만 시도
- `--spider`: 파일 다운로드 없이 존재 여부만 확인

---

### 3. AWS Target Group Health Check 경로 변경

**변경 위치**: AWS Console → EC2 → Target Groups

**Target Group**: 
- library-blue-tg
- library-green-tg

**변경 내용**:

| 항목 | 변경 전 | 변경 후 |
|------|---------|---------|
| Health check path | `/` | `/actuator/health` |
| Health check protocol | HTTP | HTTP (동일) |
| Health check port | Traffic port | Traffic port (동일) |

**변경 이유**:
- Spring Boot Actuator의 `/actuator/health` 엔드포인트는 애플리케이션의 실제 상태를 체크하는 전용 엔드포인트
- 루트 경로(`/`)보다 더 정확한 헬스체크 가능
- 데이터베이스 연결 상태 등 애플리케이션의 종속성까지 확인 가능

---

### 4. ECS Task Definition 개정 3 생성

**작업 위치**: AWS Console → ECS → Task Definitions → library-management-task

**생성 방법**: 
1. 기존 개정 2를 기반으로 새 개정 생성
2. Container definitions에서 healthCheck 섹션 수정
3. wget 기반 헬스체크 명령어로 업데이트
4. 개정 3으로 저장

**결과**: 
- Task Definition: library-management-task:3
- 상태: ACTIVE

---

## 기술적 배경

### Alpine Linux와 헬스체크 도구

**Alpine Linux 특징**:
- 경량 Linux 배포판 (약 5MB)
- 최소한의 패키지만 포함
- curl 미포함, wget 기본 포함

**도구 비교**:

| 특징 | curl | wget |
|------|------|------|
| Alpine 기본 포함 | ❌ | ✅ |
| 추가 설치 필요 | RUN apk add --no-cache curl | 불필요 |
| 이미지 크기 증가 | 약 2-3MB | 0MB |
| 헬스체크 용도 적합성 | ✅ | ✅ |

### Spring Boot Actuator Health Check

**Actuator Health Endpoint 특징**:
- 경로: `/actuator/health`
- 응답 형식: JSON
- 상태 코드: 
  - 200 OK: 정상
  - 503 Service Unavailable: 비정상

**응답 예시**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

---

## 헬스체크 흐름

```
[ALB Health Check]
    ↓ HTTP GET /actuator/health
[ECS Task - Port 8081]
    ↓ Spring Security permitAll
[Spring Boot Actuator]
    ↓ DB, DiskSpace 등 확인
[200 OK 응답]
    ↓
[Target Healthy 상태 유지]
```

**동시 헬스체크**:
1. **ECS Container Health Check**: 컨테이너 내부에서 wget으로 확인
2. **ALB Target Health Check**: ALB에서 HTTP로 확인

둘 다 정상이어야 Task가 Healthy 상태 유지

---

## 검증 방법

### 1. 로컬에서 헬스체크 테스트
```bash
# 애플리케이션 실행 후
curl http://localhost:8081/actuator/health

# 예상 응답
{"status":"UP"}
```

### 2. ECS Task 로그 확인
```
AWS Console → ECS → Clusters → library-management-cluster
→ Tasks → [실행 중인 Task] → Logs 탭
→ Health check 관련 로그 확인
```

### 3. ALB Target Group 상태 확인
```
AWS Console → EC2 → Target Groups
→ library-blue-tg 또는 library-green-tg
→ Targets 탭 → Health status 확인
```

---

## 향후 개선 사항

1. **Dockerfile 최적화**:
   - curl 설치 제거 (wget 사용으로 불필요)
   - 이미지 크기 최소화

2. **헬스체크 세밀화**:
   - `/actuator/health/readiness`: 트래픽 수신 준비 상태
   - `/actuator/health/liveness`: 애플리케이션 생존 상태
   - 각각 다른 용도로 활용 가능

3. **모니터링 강화**:
   - CloudWatch Metrics로 헬스체크 실패 횟수 추적
   - 알림 설정 (Health Check 실패 시)

---

## 참고 문서

- Spring Boot Actuator 공식 문서: https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html
- ECS Health Check 설정: https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_definition_parameters.html#container_definition_healthcheck
- Alpine Linux wget: https://wiki.alpinelinux.org/wiki/Wget

---

## 작업 완료 체크리스트

- [x] SecurityConfig.java 수정 (actuator permitAll 추가)
- [x] task-definition.json 수정 (wget 헬스체크)
- [x] Target Group Health Check 경로 변경 (/actuator/health)
- [x] ECS Task Definition 개정 3 생성
- [x] 로컬 테스트 완료
- [ ] Git commit 및 push (대기 중)
- [ ] GitHub Actions 배포 트리거
- [ ] 배포 후 검증

---

**작성일**: 2025년 10월 30일  
**작성자**: Hojin  
**문서 버전**: 1.0
