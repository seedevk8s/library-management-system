# 📚 Library Management System - 문서 디렉토리

이 디렉토리는 Library Management System 프로젝트의 모든 문서를 포함합니다.

## 📂 디렉토리 구조

```
docs/
├── README.md                           # 이 파일 (문서 네비게이션)
├── deployment/                         # 배포 관련 문서
│   └── cicd/                          # CI/CD 및 AWS 배포 문서
│       ├── .aws-credentials.txt       # ⚠️ AWS 자격증명 (Git 제외)
│       ├── 00-MASTER-PLAN.md          # 전체 배포 마스터 플랜
│       ├── 01-AWS-RESOURCES-CHECKLIST.md  # AWS 리소스 체크리스트
│       ├── 02-AWS-IAM-USER-CREATION-GUIDE.md  # IAM 사용자 생성 가이드
│       ├── 03-AWS-IAM-SETUP-GUIDE.md  # IAM 설정 상세 가이드
│       ├── 04-NEXT-STEPS-GUIDE.md    # 향후 진행 단계 가이드 ⭐
│       ├── 05-PROJECT-ROADMAP.svg    # 프로젝트 로드맵 (시각화) 🎨
│       ├── QUICK-RECOVERY-GUIDE.md    # 빠른 복구 가이드
│       ├── README.md                  # CI/CD 디렉토리 설명
│       └── SESSION-RECOVERY.md        # 세션 복구 가이드
└── screenshots/                        # 스크린샷 및 이미지
    └── iam-creation/                  # IAM 생성 과정 스크린샷
        ├── step01-iam-console.svg
        ├── step02-users-page.svg
        ├── step03-user-details.svg
        ├── step04-set-permissions.svg
        ├── step05-review-create.svg
        ├── step06-security-credentials.svg
        ├── step07-access-key-usecase.svg
        ├── step08-description-tag.svg
        └── step09-access-key-created.svg
```

---

## 📖 문서 읽는 순서

### 🎯 AWS 배포를 처음 시작하는 경우

1. **[00-MASTER-PLAN.md](deployment/cicd/00-MASTER-PLAN.md)**
   - 전체 배포 계획과 아키텍처 이해
   - Phase 1~5 전체 흐름 파악

2. **[05-PROJECT-ROADMAP.svg](deployment/cicd/05-PROJECT-ROADMAP.svg)** 🎨
   - 시각적으로 프로젝트 진행 상황 확인
   - 현재 완료된 단계와 남은 단계 파악

3. **[04-NEXT-STEPS-GUIDE.md](deployment/cicd/04-NEXT-STEPS-GUIDE.md)** ⭐ **[가장 중요!]**
   - **현재 작업해야 할 단계 상세 가이드**
   - 방법 A (AWS 콘솔) vs 방법 B (코드 우선) 선택
   - 각 단계별 상세 설명 및 예상 시간
   - 문제 해결 가이드 포함

4. **[01-AWS-RESOURCES-CHECKLIST.md](deployment/cicd/01-AWS-RESOURCES-CHECKLIST.md)**
   - AWS 리소스 생성 체크리스트
   - 생성해야 할 모든 리소스 목록

5. **[02-AWS-IAM-USER-CREATION-GUIDE.md](deployment/cicd/02-AWS-IAM-USER-CREATION-GUIDE.md)**
   - IAM 사용자 생성 기본 가이드
   - 스크린샷과 함께 단계별 설명

6. **[03-AWS-IAM-SETUP-GUIDE.md](deployment/cicd/03-AWS-IAM-SETUP-GUIDE.md)**
   - IAM 상세 설정 가이드
   - 권한 정책 및 보안 설정

---

### 🔧 배포 중 문제가 발생한 경우

1. **[QUICK-RECOVERY-GUIDE.md](deployment/cicd/QUICK-RECOVERY-GUIDE.md)**
   - 자주 발생하는 문제와 해결책
   - 빠른 복구 방법

2. **[SESSION-RECOVERY.md](deployment/cicd/SESSION-RECOVERY.md)**
   - 작업 중단 후 재개 방법
   - 세션 복구 가이드

---

### 🔄 작업 재개 시

1. **[05-PROJECT-ROADMAP.svg](deployment/cicd/05-PROJECT-ROADMAP.svg)**
   - 현재 진행 상황 확인

2. **[04-NEXT-STEPS-GUIDE.md](deployment/cicd/04-NEXT-STEPS-GUIDE.md)**
   - 다음 단계 확인 및 진행

---

## ⭐ 핵심 문서

### 지금 당장 읽어야 할 문서
| 문서 | 설명 | 우선순위 |
|------|------|----------|
| **[04-NEXT-STEPS-GUIDE.md](deployment/cicd/04-NEXT-STEPS-GUIDE.md)** | 현재 단계 상세 가이드 | 🔴 최우선 |
| **[05-PROJECT-ROADMAP.svg](deployment/cicd/05-PROJECT-ROADMAP.svg)** | 프로젝트 진행 상황 시각화 | 🟡 중요 |
| **[00-MASTER-PLAN.md](deployment/cicd/00-MASTER-PLAN.md)** | 전체 계획 | 🟡 중요 |

### 필요할 때 참고할 문서
| 문서 | 설명 | 용도 |
|------|------|------|
| [01-AWS-RESOURCES-CHECKLIST.md](deployment/cicd/01-AWS-RESOURCES-CHECKLIST.md) | AWS 리소스 체크리스트 | 리소스 생성 시 |
| [02-AWS-IAM-USER-CREATION-GUIDE.md](deployment/cicd/02-AWS-IAM-USER-CREATION-GUIDE.md) | IAM 사용자 생성 | IAM 설정 시 |
| [03-AWS-IAM-SETUP-GUIDE.md](deployment/cicd/03-AWS-IAM-SETUP-GUIDE.md) | IAM 상세 설정 | 권한 설정 시 |
| [QUICK-RECOVERY-GUIDE.md](deployment/cicd/QUICK-RECOVERY-GUIDE.md) | 문제 해결 | 에러 발생 시 |
| [SESSION-RECOVERY.md](deployment/cicd/SESSION-RECOVERY.md) | 세션 복구 | 작업 중단 후 재개 시 |

---

## 🎯 현재 단계: Phase 3 (AWS 인프라 구축)

### 현재 상태
- ✅ Phase 1: 애플리케이션 개발 (100%)
- ✅ Phase 2: 컨테이너화 (100%)
- 🔄 Phase 3: AWS 인프라 구축 (20% - **진행 중**)
- ⏳ Phase 4: CI/CD 파이프라인 (0%)
- ⏳ Phase 5: 모니터링 및 최적화 (0%)

### 다음 작업
👉 **[04-NEXT-STEPS-GUIDE.md](deployment/cicd/04-NEXT-STEPS-GUIDE.md)** 문서를 열어서 **방법 A** 또는 **방법 B**를 선택하고 진행하세요!

---

## 📁 각 디렉토리 설명

### `/deployment/cicd/`
AWS ECS Blue/Green 배포와 CI/CD 파이프라인 구축에 필요한 모든 문서가 포함되어 있습니다.

**주요 내용:**
- AWS 인프라 구축 가이드
- IAM 설정 및 권한 관리
- ECS, ECR, CodeDeploy 설정
- GitHub Actions 워크플로우
- 문제 해결 및 복구 가이드

### `/screenshots/`
문서에서 참조하는 스크린샷과 이미지 파일들이 저장되어 있습니다.

**주요 내용:**
- AWS Console 스크린샷
- 설정 과정 이미지
- 아키텍처 다이어그램

---

## 🔒 보안 주의사항

### ⚠️ Git에 절대 커밋하지 말 것
```
docs/deployment/cicd/.aws-credentials.txt  ← 이 파일!
```

이 파일에는 AWS Access Key와 Secret Key가 포함되어 있습니다.
- `.gitignore`에 이미 추가되어 있습니다.
- 절대로 Git에 커밋하지 마세요!
- GitHub에 공개되면 즉시 키를 비활성화하고 재발급해야 합니다.

---

## 🆘 도움이 필요한 경우

### 문서 관련 문의
1. 각 문서의 하단에 생성일과 버전 정보가 있습니다.
2. 문서가 오래되었거나 정보가 부정확한 경우 업데이트가 필요할 수 있습니다.

### 기술 지원
- AWS 공식 문서: https://docs.aws.amazon.com/
- GitHub Actions 문서: https://docs.github.com/actions
- Spring Boot 문서: https://spring.io/projects/spring-boot

---

## 📝 문서 업데이트 이력

| 날짜 | 버전 | 변경 내용 |
|------|------|-----------|
| 2025-10-26 | 1.0.0 | 초기 문서 구조 생성 및 정리 |

---

**마지막 업데이트:** 2025-10-26  
**관리자:** Library Management Team
