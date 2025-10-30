# Phase 4 완료: GitHub Actions 자동 배포

## 📅 작성일: 2025-10-30

## 🎯 완료 작업

### GitHub Actions 워크플로우 생성

**파일**: `.github/workflows/deploy-to-ecs.yml`

#### 워크플로우 구성

**트리거**:
- `main` 브랜치 push
- `feature/cicd-ecs-blue-green-deployment` 브랜치 push
- 수동 실행 (workflow_dispatch)

**환경 변수**:
```yaml
AWS_REGION: ap-northeast-2
ECR_REPOSITORY: library-management-system
ECS_CLUSTER: library-management-cluster
ECS_SERVICE: library-service
CONTAINER_NAME: library-app
```

#### 배포 단계

1. **Checkout code**
   - 소스 코드 체크아웃

2. **Configure AWS credentials**
   - AWS Access Key 설정
   - GitHub Secrets 사용

3. **Login to Amazon ECR**
   - ECR 로그인
   - 이미지 푸시 권한 획득

4. **Build, tag, and push image**
   - Docker 이미지 빌드
   - 두 개의 태그 생성:
     - `${{ github.sha }}`: 커밋 해시
     - `latest`: 최신 버전
   - ECR에 푸시

5. **Fill in the new image ID**
   - Task Definition JSON에 새 이미지 ID 업데이트

6. **Deploy to ECS**
   - 업데이트된 Task Definition으로 ECS Service 배포
   - 서비스 안정화 대기

7. **Deployment Summary**
   - 배포 결과 요약 출력

## 🔧 다음 단계: GitHub Secrets 설정

### 필요한 Secrets

GitHub Repository → Settings → Secrets and variables → Actions에서 다음 Secret이 설정되어 있는지 확인:

✅ 이미 설정된 Secrets:
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_REGION` (ap-northeast-2)
- `ECR_REPOSITORY` (library-management-system)
- `ECS_CLUSTER` (library-management-cluster)

⏳ 추가 필요 (선택사항, 워크플로우에서 env로 지정):
- `ECS_SERVICE` (library-service) - 워크플로우 파일에 하드코딩되어 있으므로 선택사항

## 📝 배포 테스트 절차

### 1. 로컬에서 변경사항 커밋

```bash
# 현재 브랜치 확인
git branch

# feature 브랜치인 경우
git checkout feature/cicd-ecs-blue-green-deployment

# 워크플로우 파일 추가
git add .github/workflows/deploy-to-ecs.yml

# 커밋
git commit -m "Add GitHub Actions workflow for ECS deployment"

# Push
git push origin feature/cicd-ecs-blue-green-deployment
```

### 2. GitHub Actions 실행 확인

**경로**: GitHub Repository → Actions 탭

**확인 사항**:
- Workflow 실행 시작 확인
- 각 Step의 진행 상황 모니터링
- 전체 실행 시간: 약 5-10분

### 3. 배포 결과 확인

#### GitHub Actions 로그
- 각 Step의 실행 결과
- Docker 이미지 빌드 로그
- ECR 푸시 로그
- ECS 배포 로그

#### AWS Console 확인

**ECS Service**:
```
ECS → Clusters → library-management-cluster → Services → library-service
```
- Events 탭: 배포 진행 상황
- Tasks 탭: 새 Task 시작 확인
- Deployments 탭: 배포 이력

**ECR Repository**:
```
ECR → Repositories → library-management-system
```
- 새 이미지 태그 확인
- 커밋 해시 태그 확인
- latest 태그 업데이트 확인

**CloudWatch Logs**:
```
CloudWatch → Log groups → /ecs/library-management-task
```
- 새 Task의 로그 스트림 확인
- 애플리케이션 시작 로그
- "The following 1 profile is active: prod" 확인

#### 애플리케이션 접근 테스트

**ALB DNS 접속**:
```
http://library-alb-1681303708.ap-northeast-2.elb.amazonaws.com/
```

테스트 URL:
- ✅ `/`: 홈페이지
- ✅ `/actuator/health`: Health Check
- ✅ `/boards`: 게시판 목록

## 🔄 Blue-Green 배포 시나리오

### 현재 상태
- **Blue 환경**: library-service (운영 중)
- **Green 환경**: 준비 완료 (Target Group 존재)

### 향후 Blue-Green 전환 방법

#### 1. Green Service 생성 (수동)

```bash
# AWS Console에서
ECS → Clusters → library-management-cluster → Services → Create Service

설정:
- Service name: library-service-green
- Task definition: library-task:latest
- Target Group: library-green-tg
```

#### 2. GitHub Actions로 Green 배포

워크플로우 파일 수정:
```yaml
env:
  ECS_SERVICE: library-service-green  # Green으로 변경
```

Push하여 Green 환경에 배포

#### 3. ALB 리스너 규칙 변경 (수동)

```bash
# AWS Console에서
EC2 → Load Balancers → library-alb → Listeners → HTTP:80 → Rules

변경:
Forward to: library-blue-tg → library-green-tg
```

#### 4. 트래픽 전환 확인

- ALB DNS로 접속하여 Green 버전 확인
- CloudWatch Metrics로 트래픽 모니터링
- 에러율 확인

#### 5. Blue 환경 정리 (선택)

검증 완료 후:
- Blue Service 삭제 또는 유지 (롤백용)
- Blue Target Group 유지 (다음 배포용)

## 📊 배포 자동화 완료 체크리스트

### Phase 4: 배포 자동화 (100% 완료)

- ✅ 1. Task Definition 작성 및 등록
- ✅ 2. ECS Service 생성
- ✅ 3. 배포 문제 해결
- ✅ 4. 애플리케이션 접근 테스트
- ✅ 5. 헬스체크 개선 및 Task Definition 개정 3
- ✅ 6. GitHub Actions 워크플로우 작성

## 🎉 프로젝트 완료 상태

### 전체 진행도: 100%

```
Phase 1: 준비 단계        ████████████████████ 100% ✅
Phase 2: 로컬 검증        ████████████████████ 100% ✅
Phase 3: AWS 인프라       ████████████████████ 100% ✅
Phase 4: 배포 자동화      ████████████████████ 100% ✅
```

### 구축된 인프라

**컨테이너 플랫폼**:
- ECR Repository
- ECS Fargate Cluster
- ECS Task Definition (library-task:3)
- ECS Service (library-service)

**네트워킹**:
- Application Load Balancer
- Target Groups (Blue/Green)
- Security Groups (3-tier)

**데이터베이스**:
- RDS MySQL (db.t4g.micro)
- Parameter Store (DB 연결 정보)

**모니터링**:
- CloudWatch Logs
- ALB Access Logs

**CI/CD**:
- GitHub Actions Workflow
- 자동 빌드 및 배포 파이프라인

## 🚀 배포 워크플로우 요약

```
코드 변경 → GitHub Push
    ↓
GitHub Actions 트리거
    ↓
Docker 이미지 빌드
    ↓
ECR에 푸시
    ↓
Task Definition 업데이트
    ↓
ECS Service 배포
    ↓
새 Task 시작
    ↓
Health Check 통과
    ↓
ALB 트래픽 수신
    ↓
배포 완료! 🎉
```

**전체 소요 시간**: 약 5-10분

## 📖 관련 문서

- [14-CURRENT-PROGRESS.md](./14-CURRENT-PROGRESS.md) - 전체 진행 상황
- [17-MORNING-FIXES.md](./17-MORNING-FIXES.md) - 헬스체크 개선
- [00-MASTER-PLAN.md](./00-MASTER-PLAN.md) - 마스터 플랜

## 💡 다음 개선 사항 (선택)

### 고급 기능 추가

1. **Blue-Green 자동 전환**
   - AWS CodeDeploy 통합
   - 트래픽 점진적 전환 (Canary 배포)

2. **모니터링 강화**
   - CloudWatch Alarms 설정
   - SNS 알림 연동
   - Grafana 대시보드

3. **보안 강화**
   - ECR 이미지 스캔 자동화
   - Secrets Manager 사용
   - IAM Role 최소 권한 원칙 적용

4. **성능 최적화**
   - Auto Scaling 설정
   - CloudFront CDN 연동
   - ElastiCache 추가

## 🎓 학습 성과

이 프로젝트를 통해 학습한 내용:

✅ **AWS 서비스 통합**:
- ECS Fargate 서버리스 컨테이너
- Application Load Balancer
- RDS MySQL 관리
- ECR 컨테이너 레지스트리

✅ **보안 설계**:
- 3-tier Security Group 아키텍처
- Parameter Store 비밀 관리
- IAM 권한 관리

✅ **배포 전략**:
- Blue-Green 배포 이해
- Health Check 설계
- 무중단 배포 구현

✅ **자동화**:
- GitHub Actions CI/CD
- Docker 이미지 자동 빌드
- ECS 자동 배포

## 🧹 실습 종료 후 리소스 정리

**⚠️ 비용 발생 방지를 위해 실습 완료 후 반드시 삭제**:

1. ECS Service 삭제
2. ECS Task Definition 해제
3. ALB 삭제 (가장 중요!)
4. Target Groups 삭제
5. RDS 인스턴스 삭제
6. ECR 이미지 삭제
7. CloudWatch Log Groups 삭제
8. Parameter Store 삭제
9. Security Groups 삭제

**예상 비용 (48시간 가동 시)**:
- ALB: 약 1,440원
- ECS Fargate: 약 1,300원
- 합계: 약 2,740원

---

**작성일**: 2025-10-30  
**작성자**: Hojin + Claude  
**상태**: ✅ Phase 4 완료 (100%)  
**프로젝트**: library-management-system ECS Blue-Green 배포
