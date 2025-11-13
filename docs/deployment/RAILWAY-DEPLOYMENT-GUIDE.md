# Railway 배포 가이드

## 📋 목차
1. [Railway란?](#railway란)
2. [사전 준비](#사전-준비)
3. [Railway 프로젝트 설정](#railway-프로젝트-설정)
4. [MySQL 데이터베이스 추가](#mysql-데이터베이스-추가)
5. [환경 변수 설정](#환경-변수-설정)
6. [GitHub Actions 설정](#github-actions-설정)
7. [배포 및 확인](#배포-및-확인)
8. [비용 정보](#비용-정보)

---

## Railway란?

**Railway**는 현대적인 클라우드 플랫폼으로, 다음과 같은 특징이 있습니다:

✅ **간편한 배포**: GitHub와 연동하여 Push 시 자동 배포
✅ **무료 티어**: 월 $5 크레딧 제공 (소규모 프로젝트 충분)
✅ **데이터베이스 포함**: MySQL, PostgreSQL, Redis 등 원클릭 추가
✅ **Docker 지원**: Dockerfile 자동 감지 및 빌드
✅ **쉬운 환경 변수 관리**: GUI로 간편하게 설정

---

## 사전 준비

### 1. Railway 계정 생성

1. [Railway 홈페이지](https://railway.app) 접속
2. **"Start a New Project"** 또는 **"Login"** 클릭
3. GitHub 계정으로 로그인 (권장)

### 2. 필요한 파일 확인

프로젝트에 다음 파일들이 준비되어 있어야 합니다:

```
✅ Dockerfile                           # Docker 이미지 빌드 파일
✅ railway.json                         # Railway 설정 파일
✅ .github/workflows/deploy-to-railway.yml  # GitHub Actions 워크플로우
✅ src/main/resources/application.yml  # Spring Boot 설정
```

---

## Railway 프로젝트 설정

### Step 1: 새 프로젝트 생성

1. Railway 대시보드에서 **"New Project"** 클릭
2. **"Deploy from GitHub repo"** 선택
3. 저장소 선택: `seedevk8s/library-management-system`
4. 브랜치 선택: `main` 또는 `claude/deploy-to-aws-011CV5PvDYXxRMgE1vDEaxCo`

### Step 2: 프로젝트 설정 확인

Railway가 자동으로 Dockerfile을 감지하여 빌드 설정을 생성합니다.

**확인 사항**:
- ✅ Builder: Dockerfile
- ✅ Build Command: (자동)
- ✅ Start Command: (자동, railway.json 참조)

---

## MySQL 데이터베이스 추가

### Step 1: MySQL 서비스 추가

1. Railway 프로젝트 대시보드에서 **"+ New"** 클릭
2. **"Database"** 선택
3. **"Add MySQL"** 클릭

Railway가 자동으로 MySQL 인스턴스를 생성하고 다음 환경 변수를 제공합니다:

```
MYSQL_URL
MYSQL_HOST
MYSQL_PORT
MYSQL_USER
MYSQL_PASSWORD
MYSQL_DATABASE
MYSQLHOST
MYSQLDATABASE
MYSQLPASSWORD
MYSQLPORT
MYSQLUSER
```

### Step 2: 데이터베이스 초기화 (선택사항)

Railway MySQL 콘솔에서 초기 스키마를 생성할 수 있습니다:

1. MySQL 서비스 클릭
2. **"Data"** 탭 클릭
3. **"Query"** 탭에서 SQL 실행

```sql
-- 필요한 경우 초기 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS librarydb;
USE librarydb;

-- JPA가 자동으로 테이블을 생성하므로 별도 스키마 불필요
```

---

## 환경 변수 설정

### Step 1: 애플리케이션 서비스 환경 변수

Railway 대시보드에서 애플리케이션 서비스를 선택하고 **"Variables"** 탭으로 이동:

#### 필수 환경 변수

```bash
# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# MySQL 연결 정보 (Railway MySQL 서비스의 변수 참조)
# 방법 1: Railway 변수 참조 (권장)
DB_URL=${{MySQL.MYSQL_URL}}
DB_USERNAME=${{MySQL.MYSQLUSER}}
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}

# 방법 2: 직접 입력
# DB_URL=jdbc:mysql://mysql:3306/railway?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
# DB_USERNAME=root
# DB_PASSWORD=<Railway에서 자동 생성된 비밀번호>

# 파일 업로드 디렉토리
FILE_UPLOAD_DIR=/app/uploads

# 서버 포트 (Railway는 PORT 환경 변수를 제공)
SERVER_PORT=${PORT:-8081}
```

#### Railway 변수 참조 문법

Railway는 다른 서비스의 환경 변수를 참조할 수 있습니다:

```
${{서비스이름.변수이름}}
```

예시:
```
${{MySQL.MYSQLHOST}}
${{MySQL.MYSQLDATABASE}}
```

### Step 2: 환경 변수 적용 확인

환경 변수 설정 후 Railway가 자동으로 재배포됩니다.

---

## GitHub Actions 설정

### Step 1: Railway Token 발급

1. Railway 대시보드 우측 상단 프로필 아이콘 클릭
2. **"Account Settings"** 선택
3. **"Tokens"** 탭 클릭
4. **"Create a Token"** 클릭
5. Token 이름 입력 (예: `github-actions`)
6. 생성된 토큰 복사 (한 번만 표시됨!)

### Step 2: GitHub Secrets 설정

1. GitHub 저장소 페이지로 이동
2. **Settings** → **Secrets and variables** → **Actions** 클릭
3. **"New repository secret"** 클릭
4. Secret 추가:

```
Name: RAILWAY_TOKEN
Value: <Railway에서 복사한 토큰>
```

### Step 3: 워크플로우 파일 확인

`.github/workflows/deploy-to-railway.yml` 파일이 다음 브랜치에서 트리거되도록 설정되어 있습니다:

```yaml
on:
  push:
    branches:
      - main
      - claude/deploy-to-aws-011CV5PvDYXxRMgE1vDEaxCo
  workflow_dispatch:
```

---

## 배포 및 확인

### 자동 배포 (GitHub Actions)

1. 코드 변경 후 커밋:
```bash
git add .
git commit -m "feat: Update application for Railway deployment"
git push origin claude/deploy-to-aws-011CV5PvDYXxRMgE1vDEaxCo
```

2. GitHub Actions 실행 확인:
```
https://github.com/seedevk8s/library-management-system/actions
```

3. 워크플로우가 다음 단계를 수행합니다:
   - ✅ 소스 코드 체크아웃
   - ✅ Railway CLI 설치
   - ✅ Railway에 배포
   - ✅ 배포 요약 출력

### 수동 배포 (Railway 대시보드)

Railway는 GitHub와 연동되어 있어 Push 시 자동으로 배포되기도 합니다:

1. Railway 프로젝트 대시보드 접속
2. 최근 배포 상태 확인
3. **"Deployments"** 탭에서 배포 로그 확인

### 배포 확인

#### 1. Railway 대시보드에서 확인

- **Deployments** 탭: 배포 진행 상황 및 로그
- **Metrics** 탭: CPU, 메모리 사용량
- **Logs** 탭: 애플리케이션 로그

#### 2. 애플리케이션 URL 확인

Railway가 자동으로 도메인을 생성합니다:

```
https://<프로젝트명>-production.up.railway.app
```

**설정 방법**:
1. 애플리케이션 서비스 클릭
2. **"Settings"** 탭
3. **"Public Networking"** → **"Generate Domain"** 클릭

#### 3. Health Check 확인

```bash
# Railway 도메인으로 Health Check
curl https://<프로젝트명>-production.up.railway.app/actuator/health

# 예상 응답
{
  "status": "UP"
}
```

#### 4. 애플리케이션 접속 테스트

브라우저에서 다음 URL 접속:

```
https://<프로젝트명>-production.up.railway.app/
https://<프로젝트명>-production.up.railway.app/boards
https://<프로젝트명>-production.up.railway.app/actuator/health
```

---

## 비용 정보

### Railway 무료 티어

Railway는 다음과 같은 무료 크레딧을 제공합니다:

- 💵 **월 $5 크레딧** (Hobby Plan)
- ⏱️ **500 실행 시간** (약 21일 24시간 가동)
- 💾 **1GB 디스크 스토리지**
- 🔄 **무제한 배포**

### 예상 비용 (소규모 프로젝트)

**무료 티어 범위 내**:
- Spring Boot 애플리케이션: ~$2-3/월
- MySQL 데이터베이스: ~$1-2/월
- **총합**: $3-5/월 (무료 크레딧으로 충분)

**무료 크레딧 초과 시**:
- vCPU: $0.000463/vCPU/분
- Memory: $0.000231/GB/분
- 예상 비용: $5-10/월

### AWS ECS와 비교

| 항목 | Railway | AWS ECS |
|------|---------|---------|
| **월 비용** | $0-5 | $10-20 |
| **설정 시간** | 10분 | 1-2시간 |
| **복잡도** | ⭐ | ⭐⭐⭐⭐ |
| **데이터베이스** | 포함 | 별도 RDS |
| **자동 배포** | 기본 제공 | 별도 설정 |

---

## 트러블슈팅

### 1. 배포 실패 - "Build failed"

**원인**: Dockerfile 빌드 오류

**해결 방법**:
```bash
# 로컬에서 Docker 빌드 테스트
docker build -t test-app .

# 빌드 성공 시 Railway에서 재배포
```

### 2. 애플리케이션 시작 실패 - "Application failed to start"

**원인**: 환경 변수 누락 또는 잘못된 설정

**해결 방법**:
1. Railway 대시보드 → Variables 확인
2. 필수 환경 변수 확인:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
3. Logs 탭에서 에러 메시지 확인

### 3. 데이터베이스 연결 실패 - "Cannot connect to MySQL"

**원인**: MySQL 연결 정보 오류

**해결 방법**:
```bash
# Railway MySQL 변수 참조 문법 사용
DB_URL=${{MySQL.MYSQL_URL}}
DB_USERNAME=${{MySQL.MYSQLUSER}}
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}

# 또는 Railway MySQL 서비스의 MYSQL_URL 값을 직접 복사
```

### 4. Health Check 실패 - "Service unhealthy"

**원인**: Health Check 경로 오류 또는 타임아웃

**해결 방법**:
1. `railway.json` 확인:
```json
{
  "deploy": {
    "healthcheckPath": "/actuator/health",
    "healthcheckTimeout": 300
  }
}
```

2. Spring Boot Actuator 활성화 확인:
```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health
```

### 5. GitHub Actions 실패 - "Railway token invalid"

**원인**: RAILWAY_TOKEN Secret 미설정 또는 만료

**해결 방법**:
1. Railway에서 새 토큰 발급
2. GitHub Secrets 업데이트
3. 워크플로우 재실행

---

## 추가 리소스

### Railway 공식 문서
- [Railway Docs](https://docs.railway.app/)
- [Railway CLI](https://docs.railway.app/develop/cli)
- [Environment Variables](https://docs.railway.app/develop/variables)

### GitHub Actions
- [GitHub Actions 문서](https://docs.github.com/en/actions)
- [Railway GitHub Action](https://github.com/marketplace/actions/railway)

### Spring Boot
- [Spring Boot Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

---

## 다음 단계

Railway 배포 완료 후:

1. ✅ **커스텀 도메인 연결**
   - Railway Settings → Domains → Add Custom Domain

2. ✅ **모니터링 설정**
   - Railway Metrics 대시보드 활용
   - CloudWatch 또는 Grafana 연동 (선택)

3. ✅ **백업 설정**
   - MySQL 데이터베이스 정기 백업
   - Railway CLI로 백업 스크립트 작성

4. ✅ **로그 관리**
   - Railway Logs 탭 활용
   - 외부 로그 수집 도구 연동 (선택)

---

**작성일**: 2025-11-13
**작성자**: Claude
**프로젝트**: library-management-system Railway 배포
**상태**: ✅ 배포 준비 완료
