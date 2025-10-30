# GitHub Actions 워크플로우 작성 및 첫 실행 기록

## 📅 작성일: 2025-10-30 오후

## 🎯 작업 내용

### 1. GitHub Actions 워크플로우 파일 생성

**파일**: `.github/workflows/deploy-to-ecs.yml`

#### 초기 생성
- 경로: `.github/workflows/deploy-to-ecs.yml`
- 트리거: main, feature/cicd-ecs-blue-green-deployment 브랜치
- 7개 Step으로 구성된 ECS 배포 파이프라인

#### 워크플로우 구조

```yaml
name: Deploy to Amazon ECS

on:
  push:
    branches:
      - main
      - feature/cicd-ecs-blue-green-deployment
  workflow_dispatch:

env:
  AWS_REGION: ap-northeast-2
  ECR_REPOSITORY: library-management-system
  ECS_CLUSTER: library-management-cluster
  ECS_SERVICE: library-service
  ECS_TASK_DEFINITION: task-definition.json
  CONTAINER_NAME: library-app

jobs:
  deploy:
    name: Deploy to ECS
    runs-on: ubuntu-latest
    environment: production
    
    steps:
      1. Checkout code
      2. Configure AWS credentials
      3. Login to Amazon ECR
      4. Build, tag, and push image
      5. Fill in the new image ID in Task Definition
      6. Deploy to ECS
      7. Deployment Summary
```

### 2. 브랜치 전략

**생성한 브랜치**: `feature/phase4-github-actions-workflow`

**이유**:
- Phase 4 작업을 독립적으로 진행
- 기존 브랜치와 분리하여 테스트
- 워크플로우 검증 후 메인 브랜치로 병합 예정

### 3. 파일 위치 수정

**문제 발견**:
- 초기 생성 시 `.github/deploy-to-ecs.yml`에 생성됨
- 올바른 위치: `.github/workflows/deploy-to-ecs.yml`

**해결**:
```bash
# workflows 디렉토리 생성
mkdir .github/workflows

# 파일 이동
mv .github/deploy-to-ecs.yml .github/workflows/deploy-to-ecs.yml
```

### 4. 상세 주석 추가

**목적**: 워크플로우 파일의 가독성 및 유지보수성 향상

**추가된 주석**:
- 각 섹션별 설명 (on, env, jobs, steps)
- 라인별 인라인 주석
- 파라미터 및 환경 변수 의미 설명
- 각 Step의 동작 원리 설명

**예시**:
```yaml
- name: Checkout code  # 단계 이름
  uses: actions/checkout@v4  # GitHub Actions 공식 체크아웃 액션 v4 사용
  # 현재 브랜치의 소스 코드를 워크플로우 실행 환경으로 가져옴
```

### 5. 트리거 브랜치 추가

**문제 발견**:
- 현재 브랜치: `feature/phase4-github-actions-workflow`
- 워크플로우 트리거 브랜치: main, feature/cicd-ecs-blue-green-deployment
- 결과: 현재 브랜치에서 push해도 워크플로우가 실행되지 않음

**해결**:
```yaml
on:
  push:
    branches:
      - main
      - feature/cicd-ecs-blue-green-deployment
      - feature/phase4-github-actions-workflow  # 추가
```

### 6. Git 작업 기록

#### 첫 번째 커밋
```bash
# 핵심 파일만 선택적으로 추가
git add .github/workflows/deploy-to-ecs.yml
git add docs/deployment/cicd/20-PHASE4-COMPLETE-GITHUB-ACTIONS.md
git add docs/deployment/cicd/14-CURRENT-PROGRESS.md

# 커밋
git commit -m "Phase 4 완료: GitHub Actions 워크플로우 및 배포 문서 추가"

# Push
git push origin feature/phase4-github-actions-workflow
```

**커밋 해시**: `9a20c49`  
**변경 사항**: 3 files changed, 579 insertions(+), 57 deletions(-)

#### 두 번째 커밋
```bash
# 워크플로우 파일 업데이트
git add .github/workflows/deploy-to-ecs.yml

# 커밋
git commit -m "Add current branch to workflow trigger and detailed comments"

# Push
git push origin feature/phase4-github-actions-workflow
```

**커밋 해시**: `91e3546`  
**변경 사항**: 1 file changed, 79 insertions(+), 57 deletions(-)

## 🚀 워크플로우 실행 결과

### 첫 번째 실행 (26분 전)
- **브랜치**: main
- **트리거**: Merge pull request #2
- **커밋**: cb5f0ae
- **결과**: ❌ 실패
- **소요 시간**: 1m 27s

### 두 번째 실행 (4분 전) - 최신
- **브랜치**: feature/phase4-github-actions-workflow
- **트리거**: Push (Add current branch to workflow trigger...)
- **커밋**: 91e3546
- **결과**: ❌ 실패
- **소요 시간**: 1m 28s

## ❌ 워크플로우 실패 분석

### 실패 확인 시점
- GitHub Actions 탭에서 2개의 워크플로우 실행 확인
- 모두 빨간색 X 표시 (실패)

### 예상 실패 원인

**1. AWS Secrets 미설정 (가능성 높음)**
```
필요한 Secrets:
- AWS_ACCESS_KEY_ID
- AWS_SECRET_ACCESS_KEY

확인 방법:
Settings → Secrets and variables → Actions
```

**2. task-definition.json 파일 문제**
```
위치: 프로젝트 루트/task-definition.json
워크플로우에서 참조: ${{ env.ECS_TASK_DEFINITION }}
```

**3. Dockerfile 문제**
```
Docker 이미지 빌드 단계에서 실패 가능성
```

**4. AWS 권한 문제**
```
IAM User: github-actions-deploy2
필요한 권한:
- ECR 푸시 권한
- ECS 배포 권한
```

## 📋 다음 작업 계획

### 즉시 해야 할 일

1. **워크플로우 실패 원인 확인**
   - GitHub Actions에서 첫 번째 워크플로우 클릭
   - 실패한 Step 확인
   - 에러 로그 분석

2. **GitHub Secrets 확인/설정**
   ```
   경로: Settings → Secrets and variables → Actions
   
   확인할 것:
   ✓ AWS_ACCESS_KEY_ID 존재 여부
   ✓ AWS_SECRET_ACCESS_KEY 존재 여부
   ```

3. **문제 해결 후 재실행**
   - 문제 수정
   - 다시 커밋 & 푸시
   - 워크플로우 자동 재실행

### 추가 개선 사항

1. **워크플로우 개선**
   - 에러 핸들링 추가
   - 알림 기능 추가 (Slack, Email)
   - 롤백 전략 구현

2. **보안 강화**
   - OIDC 인증 방식으로 전환 (Access Key 대신)
   - ECR 이미지 스캔 자동화

3. **모니터링 추가**
   - CloudWatch 알람 설정
   - 배포 성공/실패 알림

## 🔍 학습 내용

### GitHub Actions 기본 개념

**워크플로우 구조**:
```
Workflow (워크플로우)
└── Job (작업)
    └── Step (단계)
        └── Action (액션)
```

**트리거 방식**:
- `on.push.branches`: 특정 브랜치에 push 시 실행
- `workflow_dispatch`: 수동 실행 버튼 추가

**환경 변수 사용**:
```yaml
env:
  MY_VAR: value

steps:
  - run: echo ${{ env.MY_VAR }}
```

**Secrets 사용**:
```yaml
steps:
  - run: echo ${{ secrets.MY_SECRET }}
```

### AWS ECS 배포 프로세스

**전체 흐름**:
```
1. 코드 체크아웃
2. AWS 인증
3. ECR 로그인
4. Docker 이미지 빌드
5. ECR에 이미지 푸시
6. Task Definition 업데이트
7. ECS Service 배포
```

**주요 AWS Actions**:
- `aws-actions/configure-aws-credentials@v4`: AWS 인증
- `aws-actions/amazon-ecr-login@v2`: ECR 로그인
- `aws-actions/amazon-ecs-render-task-definition@v1`: Task Definition 업데이트
- `aws-actions/amazon-ecs-deploy-task-definition@v1`: ECS 배포

## 📊 현재 상태 요약

### 완료된 작업 ✅
- [x] GitHub Actions 워크플로우 파일 작성
- [x] 상세 주석 추가 (가독성 향상)
- [x] 브랜치 생성 및 전환
- [x] 트리거 브랜치 설정
- [x] Git 커밋 및 푸시
- [x] 워크플로우 자동 실행 확인

### 진행 중인 작업 🔄
- [ ] 워크플로우 실패 원인 분석
- [ ] 에러 수정
- [ ] 재실행 및 성공 확인

### 대기 중인 작업 ⏳
- [ ] Blue-Green 배포 테스트
- [ ] 프로덕션 배포
- [ ] 모니터링 설정

## 🎓 얻은 교훈

### 1. 워크플로우 파일 위치의 중요성
- `.github/workflows/` 디렉토리에 정확히 위치해야 함
- 경로가 틀리면 GitHub이 인식하지 못함

### 2. 트리거 브랜치 설정
- 워크플로우 파일의 트리거 브랜치와 실제 작업 브랜치가 일치해야 함
- 불일치 시 push해도 워크플로우가 실행되지 않음

### 3. GitHub Secrets의 필수성
- AWS 인증에 필요한 Secrets 사전 설정 필수
- Secrets 없이는 배포 불가능

### 4. 단계적 접근의 중요성
- 한 번에 모든 것을 시도하지 말 것
- 각 단계마다 확인하고 다음 단계로 진행

## 📁 관련 파일

**생성된 파일**:
- `.github/workflows/deploy-to-ecs.yml` - GitHub Actions 워크플로우
- `docs/deployment/cicd/20-PHASE4-COMPLETE-GITHUB-ACTIONS.md` - Phase 4 완료 문서
- `docs/deployment/cicd/21-GITHUB-ACTIONS-WORKFLOW-CREATION.md` - 본 문서

**업데이트된 파일**:
- `docs/deployment/cicd/14-CURRENT-PROGRESS.md` - 진행 상황

**참조 문서**:
- `docs/deployment/cicd/00-MASTER-PLAN.md` - 전체 계획
- `docs/deployment/cicd/17-MORNING-FIXES.md` - 헬스체크 수정

---

**작성일**: 2025-10-30 오후  
**작성자**: Hojin + Claude  
**상태**: 워크플로우 실패 원인 분석 중  
**다음 단계**: GitHub Actions 에러 로그 확인
