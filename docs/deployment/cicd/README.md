# 🚀 CI/CD + AWS ECS Blue-Green 배포 문서 가이드

> **이 폴더는 Library Management System의 AWS ECS Blue-Green 배포를 위한 완전한 가이드를 제공합니다.**

---

## 📚 문서 구조

### 1. 🎯 [마스터 플랜](./00-MASTER-PLAN.md) - **가장 먼저 읽으세요!**
**파일**: `00-MASTER-PLAN.md`

**포함 내용:**
- ✅ 전체 작업 로드맵 (Phase 1-7)
- ✅ 비용 최적화 전략 (월 $0-20 목표)
- ✅ 도메인 구매 가이드 (가장 저렴한 방법)
- ✅ 토큰 끊김 시 복구 시나리오
- ✅ 생성할 파일 목록
- ✅ AWS 리소스 목록
- ✅ 트러블슈팅 가이드

**이런 분께 추천:**
- 처음 시작하는 분
- 전체 흐름을 이해하고 싶은 분
- 비용과 시간을 계획하고 싶은 분

---

### 2. 🚨 [빠른 복구 가이드](./QUICK-RECOVERY-GUIDE.md) - **토큰 끊김 시 여기부터!**
**파일**: `QUICK-RECOVERY-GUIDE.md`

**포함 내용:**
- ✅ 1분 안에 상황 파악
- ✅ 현재 진행 상황 확인 방법
- ✅ AI에게 작업 복구 요청 템플릿
- ✅ Phase별 복구 키워드
- ✅ 긴급 상황별 대응 방법

**이런 상황에 사용:**
- ⚡ 세션이 끊긴 후 재시작할 때
- ⚡ 어디까지 했는지 기억이 안 날 때
- ⚡ 다음에 뭘 해야 할지 모를 때

---

### 3. 📋 [AWS 리소스 체크리스트](./01-AWS-RESOURCES-CHECKLIST.md) - **실무 작업용**
**파일**: `01-AWS-RESOURCES-CHECKLIST.md`

**포함 내용:**
- ✅ AWS 리소스 생성 단계별 가이드
- ✅ 각 리소스별 상세 설정 방법
- ✅ 체크박스로 진행 상황 추적
- ✅ 생성한 정보 저장 템플릿
- ✅ 검증 방법

**이런 상황에 사용:**
- 🔧 AWS Console에서 리소스 생성할 때
- 🔧 설정값을 확인하고 싶을 때
- 🔧 어디까지 했는지 체크하고 싶을 때

---

## 🎯 사용 시나리오

### 시나리오 1: 처음 시작
```
1. 00-MASTER-PLAN.md 읽기 (30분)
   → 전체 흐름 이해
   → 비용 계획 수립
   
2. Git 브랜치 생성
   git checkout -b feature/cicd-ecs-blue-green-deployment
   
3. 01-AWS-RESOURCES-CHECKLIST.md 열기
   → Phase별로 체크하면서 진행
   
4. AWS Console에서 리소스 생성
   → 체크리스트 보면서 하나씩
```

### 시나리오 2: 세션이 끊김
```
1. QUICK-RECOVERY-GUIDE.md 열기 (1분)
   → 현재 상태 확인
   → 복구 템플릿 복사
   
2. AI에게 복구 요청
   → 템플릿 사용
   
3. 이어서 작업
   → 01-AWS-RESOURCES-CHECKLIST.md 참조
```

### 시나리오 3: 특정 문제 발생
```
1. 00-MASTER-PLAN.md → 트러블슈팅 섹션
   → 일반적인 문제 해결
   
2. QUICK-RECOVERY-GUIDE.md → 긴급 상황별 대응
   → 즉시 도움 요청
   
3. AI에게 구체적 질문
   → 에러 메시지 포함
```

### 시나리오 4: Phase별 작업
```
Phase 1: 준비
└─ 00-MASTER-PLAN.md 읽기
└─ AWS 계정 로그인
└─ Git 브랜치 생성

Phase 2: 로컬 검증
└─ GitHub Actions 워크플로우 작성
└─ Task Definition 작성
└─ AppSpec 작성

Phase 3: AWS 인프라
└─ 01-AWS-RESOURCES-CHECKLIST.md 체크하면서 진행
└─ IAM → ECR → Network → ALB → ECS → CodeDeploy

Phase 4: 배포 테스트
└─ GitHub Secrets 설정
└─ 첫 배포 실행
└─ Blue-Green 전환 테스트

Phase 5: 모니터링
└─ CloudWatch 설정
└─ 알람 설정
```

---

## 📁 이 폴더의 파일 목록

```
cicd/
├── README.md                          # 📖 이 파일
├── 00-MASTER-PLAN.md                  # 🎯 마스터 플랜 (가장 먼저)
├── QUICK-RECOVERY-GUIDE.md            # 🚨 빠른 복구 (토큰 끊김 시)
├── 01-AWS-RESOURCES-CHECKLIST.md     # 📋 AWS 리소스 체크리스트
├── 02-GITHUB-ACTIONS-SETUP.md         # (곧 생성 예정)
├── 03-DEPLOYMENT-GUIDE.md             # (곧 생성 예정)
└── diagrams/                          # (곧 생성 예정)
    ├── blue-green-architecture.svg
    ├── cicd-pipeline-flow.svg
    └── aws-network-topology.svg
```

---

## 🔗 관련 문서

### 이전 단계 문서
- `docs/deployment/04-DOCKER-LOCAL-DEPLOYMENT-LOG.md`
  - Step 1-4: 로컬 Docker 배포 (완료됨)

### 프로젝트 루트 파일
- `.github/workflows/deploy-to-ecs.yml` (곧 생성)
- `aws/ecs-task-definition.json` (곧 생성)
- `aws/appspec.yml` (곧 생성)

---

## 💡 작업 팁

### 효율적인 작업 방법
1. **문서 2개 동시에 열기**
   - 왼쪽: `00-MASTER-PLAN.md` (전체 맥락)
   - 오른쪽: `01-AWS-RESOURCES-CHECKLIST.md` (현재 작업)

2. **체크박스 활용**
   - [ ] → ⬜ (시작 전)
   - [x] → ✅ (완료)
   - 진행 상황 실시간 표시

3. **정보 저장**
   - ARN, ID, URI 등을 체크리스트에 바로 기록
   - 나중에 필요할 때 빠르게 참조

4. **Git 커밋 주기**
   - Phase별로 커밋
   - 체크리스트도 함께 커밋
   - 이력 추적 용이

### 시간 절약 팁
- ✅ 문서를 먼저 읽고 시작 (시행착오 줄임)
- ✅ 체크리스트 순서대로 진행 (의존성 문제 방지)
- ✅ 생성한 정보 즉시 기록 (나중에 찾지 않아도 됨)
- ✅ 에러 발생 시 즉시 문서화 (같은 실수 방지)

---

## 🎯 학습 목표

이 문서를 완료하면 다음을 배우게 됩니다:

### 기술 스킬
- ✅ AWS ECS 아키텍처 이해
- ✅ Blue-Green 배포 전략
- ✅ GitHub Actions CI/CD 구축
- ✅ CloudWatch 모니터링
- ✅ IAM 권한 관리

### 실무 스킬
- ✅ 비용 최적화 전략
- ✅ 인프라 문서화
- ✅ 트러블슈팅 능력
- ✅ 단계별 검증 방법

---

## 📞 도움이 필요할 때

### AI 활용 방법
```
[상황 설명]
현재 [Phase/작업]을 진행 중입니다.

[문제]
[구체적인 문제나 에러]

[참조 문서]
docs/deployment/cicd/[문서명]을 확인했습니다.

[질문]
다음 단계를 어떻게 진행해야 할까요?
```

### 자주 묻는 질문
**Q: Phase를 건너뛰어도 되나요?**
A: 안 됩니다. 각 Phase는 이전 Phase의 결과물에 의존합니다.

**Q: 실수로 리소스를 잘못 만들었어요.**
A: 해당 리소스를 삭제하고 다시 만드세요. 체크리스트 재확인 필수.

**Q: 비용이 너무 높게 나와요.**
A: `00-MASTER-PLAN.md`의 비용 최적화 섹션을 다시 확인하세요.

**Q: 배포가 실패했어요.**
A: CloudWatch Logs를 확인하고, 에러 메시지를 AI에게 공유하세요.

---

## 🎖️ 완료 조건

다음을 모두 확인하면 Step 5 완료!

### Phase별 완료 조건
- [ ] Phase 1: IAM 사용자, 역할 모두 생성
- [ ] Phase 2: GitHub Actions 워크플로우 작성
- [ ] Phase 3: 모든 AWS 리소스 생성 및 검증
- [ ] Phase 4: 자동 배포 성공
- [ ] Phase 5: 모니터링 설정 완료

### 최종 검증
- [ ] ALB DNS로 애플리케이션 접속 가능
- [ ] Blue-Green 배포 성공
- [ ] 롤백 테스트 성공
- [ ] CloudWatch 로그 수집 중
- [ ] 알람 정상 작동

---

## 📅 예상 일정

| Phase | 소요 시간 | 누적 시간 |
|-------|----------|-----------|
| Phase 1 | 1-2시간 | 1-2시간 |
| Phase 2 | 2-3시간 | 3-5시간 |
| Phase 3 | 3-4시간 | 6-9시간 |
| Phase 4 | 2-3시간 | 8-12시간 |
| Phase 5 | 1-2시간 | 9-14시간 |

**권장 일정:**
- 하루 2-3시간씩 작업
- 총 5-7일 소요 예상

---

## 🎉 다음 단계 (Step 6)

Step 5 완료 후:
- [ ] RDS MySQL 마이그레이션
- [ ] 로컬 MySQL → RDS 데이터 이전
- [ ] ECS 서비스 업데이트
- [ ] 완전한 클라우드 환경 구축

---

**시작할 준비가 되셨나요?**

1️⃣ `00-MASTER-PLAN.md`를 열고 전체 흐름을 파악하세요.
2️⃣ Git 브랜치를 생성하세요.
3️⃣ `01-AWS-RESOURCES-CHECKLIST.md`로 작업을 시작하세요.

**화이팅! 🚀**
