# 🚨 토큰 끊김 시 빠른 복구 가이드

> **이 문서는 1분 안에 읽고 작업을 재개할 수 있도록 설계되었습니다.**

---

## 🔍 현재 상태 확인 (30초)

### 1단계: 프로젝트 위치
```
C:\Users\USER\Documents\choongang\Project\lecture\trainer\library-management-system (27)
```

### 2단계: Git 상태 확인
IntelliJ 터미널에서:
```bash
git status
git log --oneline -5
```

### 3단계: 현재 브랜치 확인
```bash
git branch
```
- 예상: `feature/cicd-ecs-blue-green-deployment`

---

## 📋 작업 단계 확인 (30초)

### Phase별 체크포인트

| Phase | 핵심 파일 | 확인 방법 |
|-------|----------|----------|
| **Phase 1** | `00-MASTER-PLAN.md` | ✅ 이 문서 있음 |
| **Phase 2** | `.github/workflows/deploy-to-ecs.yml` | 파일 존재 여부 |
| **Phase 3** | AWS Console에 리소스 | ECR/ECS/ALB 확인 |
| **Phase 4** | GitHub Actions 실행 | GitHub 사이트 확인 |
| **Phase 5** | CloudWatch Logs | AWS Console 확인 |

### 빠른 확인 명령어
```bash
# Phase 2 확인
ls .github/workflows/deploy-to-ecs.yml
ls aws/ecs-task-definition.json
ls aws/appspec.yml

# Phase 3는 AWS Console에서 확인
# ECR: https://ap-northeast-2.console.aws.amazon.com/ecr/repositories
# ECS: https://ap-northeast-2.console.aws.amazon.com/ecs/home
```

---

## 🔄 AI에게 작업 복구 요청 (즉시)

### 시나리오 1: Phase 1 중단
```
library-management-system 프로젝트의
docs/deployment/cicd/00-MASTER-PLAN.md를 확인했어.

Phase 1 작업 중 세션이 끊겼어.
다음 작업: [Git 브랜치 생성 / AWS IAM 설정 / 문서 작성]

이어서 진행해줘.
```

### 시나리오 2: Phase 2 중단 (파일 작성 중)
```
GitHub Actions 워크플로우 작성 중이었어.
.github/workflows/deploy-to-ecs.yml 파일 확인하고,
[어느 부분까지 작성됨]부터 이어서 완성해줘.

현재 브랜치: feature/cicd-ecs-blue-green-deployment
```

### 시나리오 3: Phase 3 중단 (AWS 리소스 생성 중)
```
AWS ECS 인프라 구축 중이었어.

완료한 리소스:
- ECR: [생성완료/미생성]
- VPC/보안그룹: [생성완료/미생성]
- ALB: [생성완료/미생성]
- ECS 클러스터: [생성완료/미생성]

docs/deployment/cicd/01-AWS-RESOURCES-CHECKLIST.md 확인하고
다음 단계 안내해줘.
```

### 시나리오 4: Phase 4 중단 (배포 테스트 중)
```
GitHub Actions로 첫 배포 테스트 중이었어.

현재 상태:
- GitHub Actions: [성공/실패/진행중]
- ECS 서비스: [Running/Stopped/등록안됨]
- 에러 메시지: [있으면 붙여넣기]

다음 단계 안내해줘.
```

---

## 📁 핵심 문서 위치 (즉시 참조)

### 반드시 확인할 문서
1. **마스터 플랜**
   ```
   docs/deployment/cicd/00-MASTER-PLAN.md
   ```
   - 전체 로드맵
   - 비용 전략
   - 도메인 가이드

2. **AWS 리소스 체크리스트**
   ```
   docs/deployment/cicd/01-AWS-RESOURCES-CHECKLIST.md
   ```
   - 생성할 리소스 목록
   - 생성 순서
   - 체크리스트

3. **GitHub Actions 가이드**
   ```
   docs/deployment/cicd/02-GITHUB-ACTIONS-SETUP.md
   ```
   - 워크플로우 설명
   - Secrets 설정
   - 트러블슈팅

---

## 🎯 각 Phase별 다음 단계

### Phase 1 완료 후 → Phase 2
```bash
# 1. 브랜치 확인
git branch

# 2. 다음 작업
"GitHub Actions 워크플로우 작성부터 시작할게.
.github/workflows/deploy-to-ecs.yml 파일을 만들어줘."
```

### Phase 2 완료 후 → Phase 3
```bash
# 1. 파일 확인
ls .github/workflows/deploy-to-ecs.yml
ls aws/ecs-task-definition.json
ls aws/appspec.yml

# 2. 다음 작업
"로컬 검증 완료했어. 
AWS 인프라 구축 가이드를 시작해줘.
ECR 생성부터 단계별로 안내해줘."
```

### Phase 3 완료 후 → Phase 4
```bash
# 1. AWS 리소스 확인 (AWS Console)
- ECR 리포지토리 존재 확인
- ECS 클러스터 실행 확인
- ALB DNS 이름 복사

# 2. 다음 작업
"AWS 인프라 구축 완료했어.
ECR URI: [복사한 URI]
ALB DNS: [복사한 DNS]

GitHub Secrets 설정부터 시작해줘."
```

---

## ⚡ 긴급 상황별 대응

### 상황 1: AWS 리소스 생성 중 에러
```
AWS [서비스명] 생성 중 에러가 발생했어.
에러 메시지: [에러 메시지 복사]

어떻게 해결할까?
```

### 상황 2: GitHub Actions 실패
```
GitHub Actions 배포가 실패했어.
실패한 단계: [Build / Push / Deploy]
에러 로그: [로그 일부 복사]

원인 분석하고 해결 방법 알려줘.
```

### 상황 3: 비용이 예상보다 높음
```
AWS 비용이 예상보다 높게 나왔어.
현재 비용: $[금액]/월

docs/deployment/cicd/00-MASTER-PLAN.md의 
비용 최적화 섹션을 기반으로 조치 방법 알려줘.
```

---

## 🔗 유용한 링크 (즉시 접근)

### AWS Console
- ECR: https://ap-northeast-2.console.aws.amazon.com/ecr/repositories
- ECS: https://ap-northeast-2.console.aws.amazon.com/ecs/home
- CodeDeploy: https://ap-northeast-2.console.aws.amazon.com/codesuite/codedeploy/applications
- CloudWatch: https://ap-northeast-2.console.aws.amazon.com/cloudwatch/home

### GitHub
- 저장소: https://github.com/seedevk8s/library-management-system
- Actions: https://github.com/seedevk8s/library-management-system/actions
- Settings: https://github.com/seedevk8s/library-management-system/settings

---

## 📞 도움 요청 템플릿

### 일반 복구
```
library-management-system의 CI/CD 구축 작업 중이야.

현재 상태:
- 브랜치: [브랜치명]
- Phase: [번호]
- 마지막 완료 작업: [작업명]
- 다음 해야 할 작업: [작업명]

docs/deployment/cicd/00-MASTER-PLAN.md 확인하고
이어서 진행해줘.
```

### 구체적 문제
```
[작업 내용] 중 [문제 발생].

에러 메시지:
[에러 메시지]

시도한 해결 방법:
1. [시도1]
2. [시도2]

다음 조치 방법 알려줘.
```

---

## ✅ 체크리스트 (진행 상황 표시)

### Phase 1: 준비
- [ ] 마스터 플랜 확인
- [ ] Git 브랜치 생성
- [ ] AWS 계정 로그인

### Phase 2: 로컬 검증
- [ ] GitHub Actions 워크플로우
- [ ] ECS Task Definition
- [ ] AppSpec 작성

### Phase 3: AWS 인프라
- [ ] ECR
- [ ] VPC/보안그룹
- [ ] ALB
- [ ] ECS 클러스터

### Phase 4: 배포
- [ ] GitHub Secrets
- [ ] 첫 배포
- [ ] Blue-Green 테스트

### Phase 5: 모니터링
- [ ] CloudWatch
- [ ] 알람 설정

---

**이 가이드를 즉시 참조하여 작업을 재개하세요!** 🚀
