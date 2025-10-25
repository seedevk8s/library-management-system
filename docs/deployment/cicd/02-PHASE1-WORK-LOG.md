# 📝 Phase 1 작업 로그 - 준비 단계

> **작업 기간**: 2025-10-25  
> **작업자**: Hojin  
> **브랜치**: feature/cicd-ecs-blue-green-deployment

---

## 📋 목차
- [작업 개요](#작업-개요)
- [Phase 1-1: Git 브랜치 생성](#phase-1-1-git-브랜치-생성)
- [Phase 1-2: AWS 계정 설정](#phase-1-2-aws-계정-설정)
- [다음 단계](#다음-단계)

---

## 작업 개요

### 목표
CI/CD + ECS Blue-Green 배포를 위한 준비 단계 완료

### 전체 Phase 1 체크리스트
- [x] Phase 1-1: Git 브랜치 생성
- [ ] Phase 1-2: AWS 계정 설정 (IAM 사용자 생성)
- [ ] Phase 1-3: 비용 알림 설정
- [ ] Phase 1-4: 추가 문서 작성

---

## Phase 1-1: Git 브랜치 생성

### ✅ 완료 시간
2025-10-25 21:10 - 21:15 (약 5분 소요)

### 작업 내용

#### 1. 현재 상태 확인
```bash
# 현재 브랜치: feature/board-comment
git status
git branch -a
```

**확인 결과:**
- 현재 브랜치: `feature/board-comment`
- Untracked 파일: `docs/deployment/cicd/` (마스터 플랜 문서)

#### 2. 마스터 플랜 문서 커밋
```bash
git add docs/deployment/cicd/
git commit -m "docs: Add CI/CD ECS Blue-Green deployment master plan"
```

**커밋 결과:**
- Commit Hash: `b7ba3a4`
- 파일 변경: 4개 파일 생성
- 추가된 줄: 2,205줄
- 생성된 파일:
  - `docs/deployment/cicd/00-MASTER-PLAN.md`
  - `docs/deployment/cicd/01-AWS-RESOURCES-CHECKLIST.md`
  - `docs/deployment/cicd/QUICK-RECOVERY-GUIDE.md`
  - `docs/deployment/cicd/README.md`

#### 3. Main 브랜치로 이동
```bash
git checkout main
```

**결과:** ✅ Switched to branch 'main'

#### 4. Main 브랜치 업데이트
```bash
git pull origin main
```

**결과:** ✅ Already up to date.

#### 5. 새 브랜치 생성
```bash
git checkout -b feature/cicd-ecs-blue-green-deployment
```

**결과:** ✅ 새 브랜치 `feature/cicd-ecs-blue-green-deployment` 생성 완료

#### 6. 원격 저장소에 푸시
```bash
git push -u origin feature/cicd-ecs-blue-green-deployment
```

**결과:** ✅ 원격 브랜치 생성 및 추적 설정 완료

#### 7. 마스터 플랜 문서 Cherry-pick
```bash
git cherry-pick b7ba3a4
```

**결과:**
- 새 Commit Hash: `f521801`
- 4개 파일 성공적으로 이동
- Cherry-pick 완료

#### 8. 최종 푸시
```bash
git push origin feature/cicd-ecs-blue-green-deployment
```

**결과:** ✅ 모든 변경사항 원격 저장소에 반영 완료

### 최종 상태

```bash
$ git log --oneline -5
f521801 docs: Add CI/CD ECS Blue-Green deployment master plan
c0638b2 게시물 목록 화면 처리 완성
7d54fa0 feat: 게시판 시스템 기본 설정 및 Backend 구현
e040cc7 feat: 게시판 시스템 기본 설정 및 Backend 구현
7cf4236 chore: 프로젝트 초기 설정 및 버그 수정 완료
```

**현재 브랜치:** `feature/cicd-ecs-blue-green-deployment`  
**원격 동기화:** ✅ Up to date with 'origin/feature/cicd-ecs-blue-green-deployment'

### 주요 학습 포인트

1. **Cherry-pick의 장점**
   - 특정 커밋만 선택적으로 다른 브랜치로 가져올 수 있음
   - Git 히스토리가 깔끔하게 유지됨
   - 다른 브랜치의 작업을 선택적으로 적용 가능

2. **브랜치 전략**
   - 새로운 기능은 항상 main에서 분기
   - 명확한 브랜치명 사용 (`feature/기능명`)
   - 원격 저장소에 즉시 푸시하여 백업

3. **커밋 메시지 규칙**
   - `docs:` 접두사로 문서 작업 명시
   - 간결하고 명확한 설명

---

## Phase 1-2: AWS 계정 설정

### 📍 현재 진행 상황

**작업 시작 예정:** 2025-10-25 21:15~

### 사전 확인 사항

**✅ 확인 완료:**
- AWS 계정 보유: 있음
- IAM 사용자 생성 경험: 있음 (기억 가물가물)

### 작업 계획

#### 1. IAM 사용자 생성
- **사용자 이름**: `github-actions-deploy`
- **목적**: GitHub Actions에서 AWS 리소스 관리
- **액세스 유형**: 프로그래매틱 액세스 (Access Key)

#### 2. 필요한 권한 (Policies)
```
✓ AmazonEC2ContainerRegistryPowerUser
✓ AmazonECS_FullAccess
✓ AmazonEC2FullAccess (EC2 인스턴스 관리용)
✓ ElasticLoadBalancingFullAccess
✓ AWSCodeDeployFullAccess
✓ CloudWatchLogsFullAccess
```

#### 3. Access Key 생성
- Access Key ID 생성
- Secret Access Key 생성
- **⚠️ 중요**: Secret Key는 한 번만 표시됨 - 안전하게 보관

#### 4. GitHub Secrets 등록 예정
나중에 GitHub Repository Settings에 등록할 항목:
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_REGION` (ap-northeast-2)

### 작업 로그

#### [작업 중] IAM 사용자 생성

**진행 방법:**
AWS CLI를 사용하여 자동으로 생성 시도 중...

---

## 다음 단계

### 🎯 Phase 1 남은 작업
- [ ] Phase 1-2: AWS 계정 설정 (IAM 사용자 생성) - **진행 중**
- [ ] Phase 1-3: 비용 알림 설정
- [ ] Phase 1-4: 추가 문서 작성

### 📌 Phase 2 준비 사항
Phase 1이 완료되면 다음 작업 시작:
- GitHub Actions 워크플로우 파일 작성
- ECS Task Definition 작성
- CodeDeploy AppSpec 작성

---

## 📊 작업 진행률

```
Phase 1 준비 단계: 25% 완료
├── [✅] 1-1: Git 브랜치 생성
├── [🔄] 1-2: AWS 계정 설정 (진행 중)
├── [⬜] 1-3: 비용 알림 설정
└── [⬜] 1-4: 추가 문서 작성

전체 Step 5 진행률: 5% 완료
```

---

## 🔄 세션 복구 정보

만약 세션이 끊긴다면 다음 정보로 복구:

**현재 위치:**
- 브랜치: `feature/cicd-ecs-blue-green-deployment`
- Phase: 1-2 (AWS 계정 설정)
- 최신 커밋: `f521801`

**다음 작업:**
- IAM 사용자 생성 완료
- Access Key 발급 및 저장
- 비용 알림 설정

**복구 명령어:**
```bash
cd "C:\Users\USER\Documents\choongang\Project\lecture\trainer\library-management-system (27)"
git checkout feature/cicd-ecs-blue-green-deployment
git pull origin feature/cicd-ecs-blue-green-deployment
```

---

**작업 로그 작성:** 2025-10-25 21:15  
**최종 업데이트:** 2025-10-25 21:15
