# 🎯 CI/CD + AWS ECS Blue-Green 배포 마스터 플랜

> **중요**: 이 문서는 토큰 세션이 끊기더라도 작업을 이어갈 수 있도록 설계된 마스터 플랜입니다.
> 각 단계는 독립적으로 실행 가능하며, 체크리스트로 진행 상황을 추적할 수 있습니다.

---

## 📋 목차
- [프로젝트 개요](#프로젝트-개요)
- [전체 작업 로드맵](#전체-작업-로드맵)
- [비용 최적화 전략](#비용-최적화-전략)
- [도메인 구매 가이드](#도메인-구매-가이드)
- [토큰 끊김 시 복구 시나리오](#토큰-끊김-시-복구-시나리오)
- [단계별 상세 계획](#단계별-상세-계획)
- [생성할 파일 목록](#생성할-파일-목록)
- [AWS 리소스 목록](#aws-리소스-목록)
- [트러블슈팅 가이드](#트러블슈팅-가이드)

---

## 프로젝트 개요

### 목표
Library Management System을 AWS ECS에 Blue-Green 배포 방식으로 배포하며, GitHub Actions를 통한 자동화된 CI/CD 파이프라인 구축

### 현재 상태
- ✅ Step 1-4: 로컬 Docker 배포 완료
- ✅ Git 저장소: https://github.com/seedevk8s/library-management-system
- ✅ 모든 변경사항 커밋 완료
- 🔄 **Step 5: CI/CD + ECS 배포 진행 중**
  - ✅ **Phase 1 완료**: IAM 사용자 생성 및 설정
  - ✅ **Phase 2 완료**: 로컬 Docker 환경 구축 및 테스트
    - ✅ Dockerfile 작성 (Multi-stage build, 보안 설정)
    - ✅ .dockerignore 생성
    - ✅ docker-compose.yml 작성 (MySQL 8.0 + App)
    - ✅ application.yml prod 프로파일 설정
    - ✅ 로컬 빌드 및 테스트 성공
    - ✅ 완전한 소스 코드 반영
  - ✅ **Phase 3 부분 완료**: ECR Repository, ECS Cluster 생성
  - ✅ **GitHub Secrets** 설정 (5/6 완료)
  - 🔄 **현재 작업**: ECS Task Definition 및 GitHub Actions 워크플로우 작성

### 기술 스택
- **소스 코드**: Spring Boot 3.5.6, Java 17, Gradle
- **컨테이너**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
- **클라우드**: AWS (ECS, ECR, ALB, CodeDeploy, RDS)
- **데이터베이스**: MySQL 8.0
- **배포 전략**: Blue-Green Deployment

### 작업 환경
- **OS**: Windows 11
- **IDE**: IntelliJ IDEA
- **AWS 리전**: ap-northeast-2 (서울)
- **프로젝트 경로**: `C:\Users\USER\Documents\choongang\Project\lecture\trainer\library-management-system (27)`

---

## 전체 작업 로드맵

### Phase 1: 준비 단계 ✅ **완료**
- [x] 마스터 플랜 문서 작성
- [x] AWS 계정 설정 및 IAM 사용자 생성
  - [x] IAM 사용자: `github-actions-deploy2` 생성
  - [x] 필수 권한 5개 정책 연결
  - [x] Access Key 생성 및 안전하게 저장
  - [x] IAM 사용자 생성 가이드 문서 작성 (`AWS_IAM_USER_CREATION_GUIDE.md`)
- [ ] 비용 알림 설정 (선택사항)
- [x] Git 브랜치 생성: `feature/cicd-ecs-blue-green-deployment`

### Phase 2: 로컬 검증 단계 ✅ **완료**
- [x] Dockerfile 작성
  - [x] Multi-stage build 구성 (builder + runtime)
  - [x] 보안 설정 (non-root user)
  - [x] Health check 설정
- [x] .dockerignore 생성
- [x] docker-compose.yml 작성
  - [x] MySQL 8.0 컨테이너 추가
  - [x] 애플리케이션 컨테이너 설정
  - [x] 볼륨 마운트 (uploads, logs)
  - [x] 네트워크 설정
  - [x] Health check 및 depends_on 설정
- [x] application.yml 운영 환경 설정
  - [x] prod 프로파일 추가
  - [x] 환경 변수 설정 (DB_URL, DB_USERNAME, DB_PASSWORD)
  - [x] HikariCP 연결 풀 설정
- [x] 로컬 Docker 이미지 빌드 및 테스트
  - [x] Docker 이미지 빌드 성공 (483.26 MB)
  - [x] MySQL + App 컨테이너 동시 실행
  - [x] 애플리케이션 정상 동작 확인
  - [x] 게시판 CRUD 기능 테스트
- [x] 완전한 소스 코드 반영
  - [x] BoardController 전체 기능 구현
  - [x] 파일 첨부, 좋아요, 댓글 기능 포함
- [ ] GitHub Actions 워크플로우 작성 (초안) - **다음 단계**
- [ ] ECS Task Definition 작성 - **다음 단계**
- [ ] CodeDeploy AppSpec 작성 - **다음 단계**

### Phase 3: AWS 인프라 구축 🔄 **진행 중**
- [x] ECR 리포지토리 생성
  - [x] Repository: `library-management-system`
  - [x] Region: `ap-northeast-2` (서울)
  - [x] GitHub Secret 등록: `ECR_REPOSITORY`
- [ ] VPC 및 서브넷 구성 (기본 VPC 사용 가능)
- [ ] Security Groups 생성
- [ ] Application Load Balancer 생성
- [ ] Target Groups 생성 (Blue/Green)
- [x] ECS 클러스터 생성
  - [x] Cluster: `library-management-cluster`
  - [x] 유형: AWS Fargate (serverless)
  - [x] Region: `ap-northeast-2`
  - [x] GitHub Secret 등록: `ECS_CLUSTER`
- [ ] ECS Task Definition 등록 (진행 중)
- [ ] ECS 서비스 생성
- [ ] CodeDeploy 애플리케이션 및 배포 그룹 생성

### Phase 4: GitHub Actions 연동 🔄 **일부 완료**
- [x] GitHub Secrets 설정 (5/6 완료)
  - [x] `AWS_ACCESS_KEY_ID`
  - [x] `AWS_SECRET_ACCESS_KEY`
  - [x] `AWS_REGION` (ap-northeast-2)
  - [x] `ECR_REPOSITORY` (library-management-system)
  - [x] `ECS_CLUSTER` (library-management-cluster)
  - [ ] `ECS_SERVICE` (ECS 서비스 생성 후 등록)
- [ ] GitHub Actions 워크플로우 최종 수정
- [ ] 첫 배포 테스트
- [ ] Blue-Green 전환 테스트
- [ ] 롤백 테스트

### Phase 5: 모니터링 및 최적화
- [ ] CloudWatch Logs 설정
- [ ] CloudWatch Alarms 설정
- [ ] SNS 알림 설정
- [ ] 비용 모니터링 대시보드 구성

### Phase 6: RDS 마이그레이션 (Step 6)
- [ ] RDS MySQL 인스턴스 생성
- [ ] 데이터 마이그레이션
- [ ] ECS 서비스 업데이트

### Phase 7: 도메인 및 HTTPS (선택)
- [ ] 도메인 구매
- [ ] Route 53 설정
- [ ] ACM 인증서 발급
- [ ] ALB에 HTTPS 리스너 추가

---

## 비용 최적화 전략

### 🎯 목표: 월 $10-20 이하로 운영 (무료 티어 최대 활용)

### AWS 무료 티어 (12개월)
- **EC2**: 750시간/월 (t2.micro 또는 t3.micro)
- **RDS**: 750시간/월 (db.t2.micro 또는 db.t3.micro)
- **ALB**: 750시간/월 (새 계정만 해당, 주의 필요)
- **ECR**: 500MB 스토리지/월
- **CloudWatch**: 10개 지표, 10개 알람, 5GB 로그

### 비용 최적화 구성

#### 1. ECS 클러스터 구성
```yaml
구성: Fargate 대신 EC2 사용
인스턴스: t3.micro (2 vCPU, 1GB RAM)
개수: 1개 (개발/테스트용)
예상 비용: 무료 티어 활용 시 $0/월
```

**💡 Fargate vs EC2 비교:**
- **Fargate**: 관리 불필요, 하지만 비용 높음 (~$15-30/월)
- **EC2**: 관리 필요, 하지만 무료 티어 사용 가능 ✅ **선택**

#### 2. RDS 구성 (Step 6)
```yaml
인스턴스 유형: db.t3.micro
스토리지: 20GB (무료 티어 한도)
백업 보관 기간: 1일 (최소)
Multi-AZ: 비활성화 (개발/테스트용)
예상 비용: 무료 티어 활용 시 $0/월
```

#### 3. Application Load Balancer
```yaml
유형: Application Load Balancer
가용 영역: 2개 (최소 요구사항)
⚠️ 주의: ALB는 신규 계정만 무료 티어 적용
예상 비용: ~$16/월 (무료 티어 없는 경우)
```

**💡 ALB 비용 절감 방법:**
- **옵션 1**: NLB 사용 (약간 저렴, ~$15/월)
- **옵션 2**: ALB 1개로 여러 서비스 공유
- **옵션 3**: 로컬 개발은 ALB 없이 직접 ECS 접근

#### 4. ECR (Container Registry)
```yaml
이미지 크기: ~500MB
보관 이미지: 최신 3개 버전만 유지
예상 비용: $0/월 (무료 티어 500MB)
```

#### 5. 기타 서비스
```yaml
CloudWatch Logs: 기본 수준 로깅
S3 (파일 업로드용): 5GB 무료 티어
Data Transfer: 최소화 (같은 리전 내)
```

### 💰 총 예상 비용 (월별)

#### 시나리오 1: 무료 티어 최대 활용 (신규 계정)
```
ECS (EC2): $0 (무료 티어)
RDS: $0 (무료 티어)
ALB: $0 (무료 티어, 신규 계정)
ECR: $0 (무료 티어)
CloudWatch: $0 (무료 티어)
---------------------
총계: $0/월
```

#### 시나리오 2: ALB 무료 티어 없음
```
ECS (EC2): $0 (무료 티어)
RDS: $0 (무료 티어)
ALB: $16/월
ECR: $0 (무료 티어)
CloudWatch: $0 (무료 티어)
---------------------
총계: ~$16-20/월
```

#### 시나리오 3: 무료 티어 종료 후
```
ECS (EC2 t3.micro): ~$7-8/월
RDS (db.t3.micro): ~$13-15/월
ALB: $16/월
기타: $2-3/월
---------------------
총계: ~$38-42/월
```

### 🎯 권장 구성 (개발/테스트 환경)

**Phase 1 (Step 5): ECS만 배포**
- EC2 기반 ECS: t3.micro (무료 티어)
- ALB: Application Load Balancer
- Database: 로컬 MySQL 계속 사용 (비용 $0)
- **예상 비용**: $0-16/월

**Phase 2 (Step 6): RDS 추가**
- RDS db.t3.micro 추가
- **예상 비용**: $0-16/월 (무료 티어) 또는 $16-31/월

### 📊 비용 모니터링 설정

**AWS Budgets 설정 (필수!):**
```yaml
예산: $20/월
알림 임계값:
  - 50% 도달 시: 이메일 알림
  - 80% 도달 시: 이메일 알림
  - 100% 도달 시: 이메일 + SMS 알림
```

**CloudWatch 비용 알람:**
```yaml
지표: EstimatedCharges
임계값: $15
작업: SNS 알림
```

### 💡 비용 절감 팁

1. **개발 시간에만 실행**
   - 저녁/주말에는 ECS 서비스 스케일링 0으로 설정
   - 스케줄러로 자동화 가능

2. **이미지 최적화**
   - Multi-stage build 사용 (이미 적용됨)
   - 이미지 크기 최소화
   - 오래된 이미지 자동 삭제

3. **로그 관리**
   - 로그 보관 기간: 7일
   - 불필요한 로그 레벨 제한

4. **리소스 태깅**
   - 모든 리소스에 태그 추가
   - 비용 추적 용이

---

## 도메인 구매 가이드

### 🌐 저렴한 도메인 구매 방법

#### 1. Namecheap (추천 🥇)
**URL**: https://www.namecheap.com

**장점:**
- ✅ 가장 저렴한 가격 (.com $8.88/년)
- ✅ 무료 WHOIS 개인정보 보호
- ✅ 직관적인 인터페이스
- ✅ 한국 신용카드 결제 가능

**가격 예시:**
- `.com`: $8.88-$12.98/년
- `.net`: $11.98/년
- `.xyz`: $1.99/년 (첫해), $10.98/년 (갱신)
- `.tech`: $4.98/년 (첫해), $49.98/년 (갱신)

**구매 절차:**
1. Namecheap 회원가입
2. 원하는 도메인 검색
3. 장바구니 추가
4. WHOIS 개인정보 보호 활성화 (무료)
5. 결제 (신용카드, PayPal)

#### 2. Cloudflare Registrar
**URL**: https://www.cloudflare.com/products/registrar/

**장점:**
- ✅ 도매가 그대로 제공 (마진 없음)
- ✅ 무료 WHOIS 개인정보 보호
- ✅ Cloudflare DNS 무료 사용

**가격 예시:**
- `.com`: ~$9.77/년
- `.dev`: ~$15/년

**주의사항:**
- Cloudflare 계정 필요
- 일부 TLD만 지원

#### 3. Porkbun
**URL**: https://porkbun.com

**장점:**
- ✅ 경쟁력 있는 가격
- ✅ 무료 WHOIS 개인정보 보호
- ✅ 무료 SSL 인증서

**가격 예시:**
- `.com`: $9.13/년
- `.dev`: $13.49/년

#### 4. Google Domains (Squarespace Domains)
**URL**: https://domains.squarespace.com

**장점:**
- ✅ 깔끔한 인터페이스
- ✅ Google 통합

**단점:**
- ❌ 가격이 다소 높음 (~$12/년)

**가격 예시:**
- `.com`: $12/년
- `.dev`: $20/년

#### 5. 한국 도메인 등록업체
**가비아**: https://www.gabia.com
- `.com`: 15,400원/년 (~$11.50)
- `.kr`: 17,600원/년 (~$13)
- 장점: 한글 지원, 국내 결제
- 단점: 가격이 해외보다 높음

### 🎯 추천 도메인 전략

#### 예산별 추천

**초저예산 ($2-5/년):**
- `.xyz`: $1.99/년 (첫해)
- `.icu`: $1.99/년 (첫해)
- ⚠️ 주의: 갱신 비용 높음, 신뢰도 낮음

**합리적 예산 ($9-13/년):**
- `.com`: $8.88-$12/년 ✅ **가장 추천**
- `.net`: $11-13/년
- 신뢰도 높고, 기억하기 쉬움

**개발자 도메인 ($15-20/년):**
- `.dev`: $13-20/년
- `.io`: $35-40/년 (비쌈, 비추천)
- `.tech`: $5/년 (첫해) → $50/년 (갱신, 비추천)

### 📝 구매 체크리스트

```markdown
- [ ] 도메인 이름 결정
  - 예시: library-management-demo.com
  - 짧고 기억하기 쉽게
  - 하이픈 최소화
  
- [ ] 도메인 등록업체 선택
  - 추천: Namecheap 또는 Cloudflare
  
- [ ] WHOIS 개인정보 보호 확인
  - 무료로 제공되는지 확인 필수!
  
- [ ] 자동 갱신 설정
  - 도메인 만료 방지
  
- [ ] DNS 설정 계획
  - AWS Route 53 사용 예정
  - 또는 등록업체 DNS 사용
```

### 🔗 도메인 → AWS 연동

**Step 7에서 상세 진행 예정:**
1. Route 53에서 Hosted Zone 생성
2. 도메인 등록업체에서 네임서버 변경
3. Route 53에서 ALB로 A 레코드 생성
4. ACM에서 SSL 인증서 발급
5. ALB에 HTTPS 리스너 추가

**비용:**
- Route 53 Hosted Zone: $0.50/월
- ACM 인증서: 무료 (AWS 서비스 내 사용)

---

## 토큰 끊김 시 복구 시나리오

### 🔄 세션 복구 프로토콜

#### 시나리오 1: 문서 작성 중 끊김

**복구 방법:**
1. 이 문서 확인: `docs/deployment/cicd/00-MASTER-PLAN.md`
2. 작업 진행 상황 확인:
   ```bash
   git status
   git log --oneline -10
   ```
3. 체크리스트에서 마지막 완료 항목 확인
4. 다음 단계 이어서 진행

**AI에게 요청할 메시지:**
```
현재 library-management-system 프로젝트의 
docs/deployment/cicd/00-MASTER-PLAN.md 파일을 확인하고,
[마지막 완료한 단계]부터 이어서 작업을 진행해줘.

현재 Git 브랜치는 [브랜치명]이고,
Phase [N]의 [단계명]까지 완료했어.
```

#### 시나리오 2: AWS 리소스 생성 중 끊김

**복구 방법:**
1. AWS Console에서 생성된 리소스 확인
2. `docs/deployment/cicd/01-AWS-RESOURCES-CHECKLIST.md` 확인
3. 체크리스트 업데이트
4. 다음 리소스 생성 이어서 진행

**AI에게 요청할 메시지:**
```
AWS ECS Blue-Green 배포 작업 중이야.
docs/deployment/cicd/01-AWS-RESOURCES-CHECKLIST.md 파일을 확인하고,
[마지막 생성한 리소스]까지 완료했어.
다음 단계를 이어서 진행해줘.

AWS 리전: ap-northeast-2
```

#### 시나리오 3: GitHub Actions 워크플로우 작성 중 끊김

**복구 방법:**
1. `.github/workflows/deploy-to-ecs.yml` 파일 확인
2. 어디까지 작성되었는지 파악
3. 이어서 작성

**AI에게 요청할 메시지:**
```
GitHub Actions 워크플로우 작성 중이야.
.github/workflows/deploy-to-ecs.yml 파일을 확인하고,
[어디까지 작성됨]부터 이어서 완성해줘.
```

#### 시나리오 4: 배포 테스트 중 끊김

**복구 방법:**
1. GitHub Actions 실행 로그 확인
2. ECS 콘솔에서 서비스 상태 확인
3. CloudWatch Logs 확인
4. 문제 진단 및 해결

**AI에게 요청할 메시지:**
```
ECS Blue-Green 배포 테스트 중 세션이 끊겼어.
현재 상태:
- GitHub Actions: [상태]
- ECS Service: [상태]
- 발생한 에러: [에러 메시지]

다음 단계를 진행하려면 어떻게 해야 할까?
```

### 📋 각 Phase별 복구 키워드

| Phase | 복구 키워드 | 확인 파일 |
|-------|------------|----------|
| Phase 1 | "마스터 플랜" | `00-MASTER-PLAN.md` |
| Phase 2 | "로컬 검증" | `.github/workflows/`, `aws/` |
| Phase 3 | "AWS 인프라" | `01-AWS-RESOURCES-CHECKLIST.md` |
| Phase 4 | "GitHub Actions 연동" | `02-GITHUB-ACTIONS-SETUP.md` |
| Phase 5 | "모니터링" | `03-MONITORING-SETUP.md` |

---

## 단계별 상세 계획

### Phase 1: 준비 단계 (예상 시간: 1-2시간)

#### 작업 1.1: Git 브랜치 생성
```bash
git checkout -b feature/cicd-ecs-blue-green-deployment
git push -u origin feature/cicd-ecs-blue-green-deployment
```

#### 작업 1.2: AWS 계정 설정

**IAM 사용자 생성:**
1. AWS Console → IAM → Users → Create user
2. 사용자 이름: `github-actions-deploy`
3. 권한:
   - `AmazonEC2ContainerRegistryPowerUser`
   - `AmazonECS_FullAccess`
   - `AmazonEC2FullAccess`
   - `ElasticLoadBalancingFullAccess`
   - `AWSCodeDeployFullAccess`
   - `CloudWatchLogsFullAccess`
4. Access Key 생성 및 저장 (나중에 GitHub Secrets에 등록)

**비용 알림 설정:**
1. AWS Console → Billing → Budgets
2. Create budget → Cost budget
3. 월 예산: $20
4. 알림: 50%, 80%, 100%

#### 작업 1.3: 문서 구조 생성
- `docs/deployment/cicd/00-MASTER-PLAN.md` ✅ (현재 파일)
- `docs/deployment/cicd/01-AWS-RESOURCES-CHECKLIST.md`
- `docs/deployment/cicd/02-GITHUB-ACTIONS-SETUP.md`
- `docs/deployment/cicd/03-DEPLOYMENT-GUIDE.md`

### Phase 2: 로컬 검증 단계 (예상 시간: 2-3시간)

#### 작업 2.1: GitHub Actions 워크플로우 작성

**파일**: `.github/workflows/deploy-to-ecs.yml`

**주요 작업:**
- [ ] Checkout 코드
- [ ] Java 17 설정
- [ ] Gradle 빌드
- [ ] 테스트 실행
- [ ] Docker 이미지 빌드
- [ ] ECR 로그인
- [ ] ECR 푸시
- [ ] ECS Task Definition 업데이트
- [ ] CodeDeploy 배포 트리거

#### 작업 2.2: ECS Task Definition 작성

**파일**: `aws/ecs-task-definition.json`

**주요 설정:**
- Family: `library-management-system`
- CPU: 256 (0.25 vCPU)
- Memory: 512 (0.5 GB)
- Container:
  - Image: `<ECR_URI>:latest`
  - Port: 8081
  - Environment Variables
  - Health Check

#### 작업 2.3: CodeDeploy AppSpec 작성

**파일**: `aws/appspec.yml`

**주요 설정:**
- Version: 0.0
- Resources:
  - TargetService
- Hooks:
  - BeforeInstall
  - AfterInstall
  - ApplicationStart
  - ValidateService

### Phase 3: AWS 인프라 구축 (예상 시간: 3-4시간)

#### 작업 3.1: ECR 생성
```bash
aws ecr create-repository \
  --repository-name library-management-system \
  --region ap-northeast-2
```

#### 작업 3.2: VPC 및 보안 그룹

**기본 VPC 사용 또는 새로 생성:**
- [ ] VPC 선택/생성
- [ ] Public Subnets (2개 이상, 다른 AZ)
- [ ] Internet Gateway

**Security Groups:**
- [ ] ALB Security Group (80, 443 → Internet)
- [ ] ECS Security Group (8081 → ALB)

#### 작업 3.3: Application Load Balancer

**생성 단계:**
1. ALB 생성
2. Listener: HTTP (80)
3. Target Group 1 (Blue): `library-tg-blue`
4. Target Group 2 (Green): `library-tg-green`
5. Health Check 설정

#### 작업 3.4: ECS 클러스터 및 서비스

**ECS 클러스터:**
- 이름: `library-cluster`
- 유형: EC2 Launch Type
- 인스턴스: t3.micro

**ECS Task Definition:**
- Family: `library-management-system`
- Launch Type: EC2

**ECS 서비스:**
- 이름: `library-service`
- Desired count: 1
- Load Balancer: ALB 연결

#### 작업 3.5: CodeDeploy 설정

**CodeDeploy 애플리케이션:**
- 이름: `library-app`
- Compute platform: ECS

**CodeDeploy 배포 그룹:**
- 이름: `library-deploy-group`
- Service role: ECS CodeDeploy 역할
- Target Groups: Blue/Green
- Traffic shifting: AllAtOnce (빠른 테스트용)

### Phase 4: GitHub Actions 연동 (예상 시간: 2-3시간)

#### 작업 4.1: GitHub Secrets 설정

**Settings → Secrets and variables → Actions:**
```
AWS_ACCESS_KEY_ID: <IAM Access Key>
AWS_SECRET_ACCESS_KEY: <IAM Secret Key>
AWS_REGION: ap-northeast-2
ECR_REPOSITORY: library-management-system
ECS_CLUSTER: library-cluster
ECS_SERVICE: library-service
CODEDEPLOY_APPLICATION: library-app
CODEDEPLOY_DEPLOYMENT_GROUP: library-deploy-group
```

#### 작업 4.2: 첫 배포 테스트

**절차:**
1. 코드 변경 (README 수정 등)
2. Commit & Push
3. GitHub Actions 자동 트리거
4. 배포 진행 상황 모니터링
5. ALB DNS로 접속 테스트

#### 작업 4.3: Blue-Green 전환 테스트

**절차:**
1. 코드 변경 (버전 업데이트)
2. 배포 시작
3. Green 환경에 새 버전 배포
4. Health Check 통과 확인
5. Traffic Shift (Blue → Green)
6. 검증

#### 작업 4.4: 롤백 테스트

**절차:**
1. 의도적으로 에러 발생시키기
2. Health Check 실패
3. 자동 롤백 확인

### Phase 5: 모니터링 설정 (예상 시간: 1-2시간)

#### 작업 5.1: CloudWatch Logs

**Log Groups:**
- `/ecs/library-management-system`

**설정:**
- Retention: 7 days

#### 작업 5.2: CloudWatch Alarms

**알람 설정:**
- CPU Utilization > 80%
- Memory Utilization > 80%
- Target Response Time > 2s
- Unhealthy Host Count > 0

#### 작업 5.3: SNS 알림

**SNS Topic:**
- 이름: `library-alerts`
- 구독: 이메일 또는 SMS

---

## 생성할 파일 목록

### 📁 Git Repository 파일

#### 1. GitHub Actions 워크플로우
```
.github/
└── workflows/
    └── deploy-to-ecs.yml         # CI/CD 파이프라인
```

#### 2. AWS 설정 파일
```
aws/
├── ecs-task-definition.json      # ECS Task 정의
├── appspec.yml                    # CodeDeploy 설정
└── taskdef-template.json          # Task Definition 템플릿
```

#### 3. 문서
```
docs/
├── AWS_IAM_USER_CREATION_GUIDE.md              # ✅ IAM 사용자 생성 가이드
├── screenshots/                                 # ✅ 작업 스크린샷
└── deployment/
    └── cicd/
        ├── 00-MASTER-PLAN.md                    # ✅ 현재 파일 (마스터 플랜)
        ├── 01-AWS-RESOURCES-CHECKLIST.md        # ✅ AWS 리소스 체크리스트
        ├── 03-AWS-IAM-SETUP-GUIDE.md            # ✅ IAM 설정 가이드
        ├── QUICK-RECOVERY-GUIDE.md              # ✅ 빠른 복구 가이드
        ├── README.md                            # ✅ 문서 가이드
        ├── SESSION-RECOVERY.md                  # ✅ 세션 복구 가이드
        ├── 02-GITHUB-ACTIONS-SETUP.md           # (곧 생성 예정)
        ├── 03-DEPLOYMENT-GUIDE.md               # (곧 생성 예정)
        ├── 04-TROUBLESHOOTING.md                # (곧 생성 예정)
        └── diagrams/
            ├── blue-green-architecture.svg      # (곧 생성 예정)
            ├── cicd-pipeline-flow.svg           # (곧 생성 예정)
            └── aws-network-topology.svg         # (곧 생성 예정)
```

#### 4. 환경 설정 (검토)
```
src/main/resources/
├── application.yml                # 기본 설정
├── application-dev.yml           # 개발 환경
└── application-prod.yml          # 운영 환경 (AWS ECS용)
```

### 📋 각 파일 작성 순서

**우선순위 1 (Phase 1):**
1. ✅ `docs/deployment/cicd/00-MASTER-PLAN.md` (현재 파일)
2. ✅ `docs/deployment/cicd/01-AWS-RESOURCES-CHECKLIST.md`
3. ✅ `docs/deployment/cicd/03-AWS-IAM-SETUP-GUIDE.md`
4. ✅ `docs/deployment/cicd/README.md`
5. ✅ `docs/deployment/cicd/QUICK-RECOVERY-GUIDE.md`
6. ✅ `docs/deployment/cicd/SESSION-RECOVERY.md`
7. ✅ `docs/AWS_IAM_USER_CREATION_GUIDE.md`

**우선순위 2 (Phase 2):**
3. `.github/workflows/deploy-to-ecs.yml`
4. `aws/ecs-task-definition.json`
5. `aws/appspec.yml`
6. `docs/deployment/cicd/02-GITHUB-ACTIONS-SETUP.md`

**우선순위 3 (Phase 3):**
7. AWS 콘솔에서 리소스 생성 (코드 아님)
8. `docs/deployment/cicd/diagrams/*.svg`

**우선순위 4 (Phase 4-5):**
9. `docs/deployment/cicd/03-DEPLOYMENT-GUIDE.md`
10. `docs/deployment/cicd/04-TROUBLESHOOTING.md`

---

## AWS 리소스 목록

### 🏗️ 생성할 AWS 리소스

#### 1. IAM (Identity and Access Management)
- [ ] IAM User: `github-actions-deploy`
- [ ] IAM Role: `ecsTaskExecutionRole`
- [ ] IAM Role: `ecsTaskRole`
- [ ] IAM Role: `ecsCodeDeployRole`

#### 2. ECR (Elastic Container Registry)
- [ ] Repository: `library-management-system`

#### 3. VPC (Virtual Private Cloud)
- [ ] VPC: 기본 VPC 사용 또는 신규 생성
- [ ] Subnets: Public Subnets (최소 2개, 다른 AZ)
- [ ] Internet Gateway

#### 4. Security Groups
- [ ] ALB Security Group
  - Inbound: HTTP (80), HTTPS (443) from 0.0.0.0/0
  - Outbound: All traffic
- [ ] ECS Security Group
  - Inbound: 8081 from ALB Security Group
  - Outbound: All traffic

#### 5. Elastic Load Balancing
- [ ] Application Load Balancer: `library-alb`
- [ ] Listener: HTTP (80)
- [ ] Target Group (Blue): `library-tg-blue`
- [ ] Target Group (Green): `library-tg-green`

#### 6. ECS (Elastic Container Service)
- [ ] ECS Cluster: `library-cluster`
  - EC2 Launch Type
  - Instance Type: t3.micro
  - Desired capacity: 1
- [ ] Task Definition: `library-management-system`
  - CPU: 256
  - Memory: 512
  - Container Port: 8081
- [ ] Service: `library-service`
  - Desired count: 1
  - Load Balancer 연결

#### 7. CodeDeploy
- [ ] Application: `library-app`
  - Compute platform: ECS
- [ ] Deployment Group: `library-deploy-group`
  - Service role: ecsCodeDeployRole
  - Target Groups: Blue/Green
  - Traffic routing: AllAtOnce

#### 8. CloudWatch
- [ ] Log Group: `/ecs/library-management-system`
- [ ] Alarms:
  - CPU Utilization
  - Memory Utilization
  - Target Response Time
  - Unhealthy Host Count

#### 9. SNS (Simple Notification Service)
- [ ] Topic: `library-alerts`
- [ ] Subscription: Email

#### 10. RDS (Phase 6, Step 6)
- [ ] DB Instance: `library-db`
  - Engine: MySQL 8.0
  - Instance Class: db.t3.micro
  - Storage: 20 GB

### 📊 리소스 의존성 다이어그램

```
IAM Users/Roles
    ↓
ECR Repository
    ↓
VPC & Security Groups
    ↓
ALB & Target Groups
    ↓
ECS Cluster (EC2)
    ↓
ECS Task Definition
    ↓
ECS Service
    ↓
CodeDeploy Application
    ↓
CodeDeploy Deployment Group
    ↓
CloudWatch Logs & Alarms
    ↓
SNS Topic
```

---

## 트러블슈팅 가이드

### 일반적인 문제와 해결 방법

#### 문제 1: ECR 푸시 실패
**증상:**
```
denied: Your authorization token has expired. Reauthenticate and retry.
```

**해결:**
```bash
aws ecr get-login-password --region ap-northeast-2 | \
  docker login --username AWS --password-stdin <ECR_URI>
```

#### 문제 2: ECS 서비스 시작 실패
**증상:**
- Task가 계속 PENDING 상태

**확인 사항:**
1. ECS 클러스터에 EC2 인스턴스가 있는지
2. Security Group 설정 확인
3. Task Definition의 CPU/Memory 설정
4. IAM Role 권한 확인

#### 문제 3: Health Check 실패
**증상:**
- Target Group에서 Unhealthy 상태

**해결:**
1. Health Check 경로 확인 (`/actuator/health`)
2. Security Group에서 ALB → ECS 통신 허용 확인
3. 애플리케이션이 8081 포트에서 리스닝하는지 확인

#### 문제 4: Blue-Green 배포 실패
**증상:**
- CodeDeploy에서 "InvalidInput" 에러

**해결:**
1. `appspec.yml` 형식 확인
2. Task Definition ARN 확인
3. Container 이름 일치 확인

#### 문제 5: GitHub Actions 실패
**증상:**
- "Credentials could not be loaded"

**해결:**
1. GitHub Secrets 확인
2. IAM 사용자 권한 확인
3. Access Key 유효성 확인

---

## 다음 단계 체크리스트

### ✅ 즉시 진행할 작업

**Phase 2 완료 항목:**
- [x] Dockerfile 생성 및 최적화
- [x] .dockerignore 생성
- [x] docker-compose.yml 작성 (MySQL + App)
- [x] application.yml prod 프로파일 설정
- [x] 로컬 Docker 빌드 및 테스트 성공
- [x] 완전한 소스 코드 반영

**Phase 3 진행 중:**
- [x] IAM 사용자 생성 (`github-actions-deploy2`)
- [x] ECR Repository 생성 (`library-management-system`)
- [x] ECS Cluster 생성 (`library-management-cluster`, Fargate)
- [x] GitHub Secrets 설정 (5/6 완료)

**다음 작업 (우선순위):**
- [ ] **Git 커밋**: 현재까지 작업 내용 커밋
- [ ] **ECS Task Definition 작성**: `aws/ecs-task-definition.json`
- [ ] **GitHub Actions 워크플로우 작성**: `.github/workflows/deploy-to-ecs.yml`
- [ ] **CodeDeploy AppSpec 작성**: `aws/appspec.yml`
- [ ] **VPC 및 보안 그룹 설정**
- [ ] **Application Load Balancer 생성**
- [ ] **ECS Service 생성**
- [ ] **CodeDeploy 설정**
- [ ] **첫 배포 테스트**

### 📅 단계별 예상 소요 시간

| Phase | 작업 내용 | 예상 시간 | 난이도 |
|-------|----------|----------|--------|
| Phase 1 | 준비 | 1-2시간 | 쉬움 ⭐ |
| Phase 2 | 로컬 검증 | 2-3시간 | 보통 ⭐⭐ |
| Phase 3 | AWS 인프라 | 3-4시간 | 어려움 ⭐⭐⭐ |
| Phase 4 | GitHub Actions | 2-3시간 | 보통 ⭐⭐ |
| Phase 5 | 모니터링 | 1-2시간 | 쉬움 ⭐ |
| **총계** | | **9-14시간** | |

---

## 참고 자료

### AWS 공식 문서
- [ECS Blue/Green Deployment](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/deployment-type-bluegreen.html)
- [CodeDeploy for ECS](https://docs.aws.amazon.com/codedeploy/latest/userguide/deployment-groups-create-ecs.html)
- [ECR User Guide](https://docs.aws.amazon.com/AmazonECR/latest/userguide/what-is-ecr.html)

### GitHub Actions
- [GitHub Actions for AWS](https://github.com/aws-actions)
- [Deploy to Amazon ECS](https://github.com/aws-actions/amazon-ecs-deploy-task-definition)

### 비용 관련
- [AWS Pricing Calculator](https://calculator.aws/)
- [AWS Free Tier](https://aws.amazon.com/free/)

---

## 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2025-10-26 | 1.3 | Git 브랜치 생성 완료 반영, 문서 파일 목록 업데이트 (AWS_IAM_USER_CREATION_GUIDE.md 등 6개 문서 추가), 스크린샷 디렉토리 추가 | Claude |
| 2025-10-26 | 1.2 | Phase 2 완료 - Docker 환경 구축 및 로컬 테스트 성공, 완전한 소스 반영 | Claude |
| 2025-10-26 | 1.1 | Phase 1 완료, Phase 3 부분 완료, GitHub Secrets 설정 내용 반영 | Claude |
| 2025-10-24 | 1.0 | 마스터 플랜 초안 작성 | Claude |

---

**🎯 이 문서의 목적**

이 마스터 플랜 문서는:
1. ✅ 전체 작업의 청사진 제공
2. ✅ 토큰 끊김 시 복구 가능하도록 설계
3. ✅ 각 단계를 독립적으로 실행 가능하게 구성
4. ✅ 비용 최적화 전략 제시
5. ✅ 트러블슈팅 가이드 포함

**📌 현재 진행 상황 (2025-10-26 v1.3):**
- ✅ **Phase 1 완료**: IAM 사용자 생성 및 Git 브랜치 설정
  - ✅ IAM 사용자: `github-actions-deploy2`
  - ✅ Git 브랜치: `feature/cicd-ecs-blue-green-deployment` 생성 완료
  - ✅ 관련 문서 6개 작성 완료 (README, QUICK-RECOVERY-GUIDE 등)
- ✅ **Phase 2 완료**: 로컬 Docker 환경 구축
  - ✅ Dockerfile 작성 (Multi-stage build, 보안 설정, Health check)
  - ✅ .dockerignore 생성
  - ✅ docker-compose.yml 작성 (MySQL 8.0 + App)
  - ✅ application.yml prod 프로파일 설정
  - ✅ 로컬 빌드 성공 (이미지 크기: 483.26 MB)
  - ✅ MySQL + App 컨테이너 동시 실행 성공
  - ✅ 웹 애플리케이션 정상 동작 확인 (http://localhost:8081)
  - ✅ 완전한 소스 코드 반영 (BoardController, 파일첨부, 좋아요 등)
- ✅ **Phase 3 부분 완료**: 
  - ✅ ECR Repository 생성 (`library-management-system`)
  - ✅ ECS Cluster 생성 (`library-management-cluster`, Fargate)
  - ✅ GitHub Secrets 5/6 완료
- 🔄 **현재 작업**: Git 커밋 후 ECS Task Definition 및 GitHub Actions 워크플로우 작성

**📌 다음 작업:**
- Git 커밋 및 푸시
- ECS Task Definition 생성 (`aws/ecs-task-definition.json`)
- GitHub Actions 워크플로우 작성 (`.github/workflows/deploy-to-ecs.yml`)
- CodeDeploy AppSpec 작성 (`aws/appspec.yml`)

---

**문서 작성 완료** ✅
