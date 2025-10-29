# 🚀 CI/CD 및 AWS 배포 문서

이 디렉토리는 Library Management System의 AWS ECS Blue/Green 배포와 CI/CD 파이프라인 구축에 필요한 모든 문서를 포함합니다.

---

## 📂 파일 목록

### 📋 기본 가이드 문서
| 순서 | 파일명 | 설명 | 상태 |
|------|--------|------|------|
| 00 | [00-MASTER-PLAN.md](00-MASTER-PLAN.md) | 전체 배포 마스터 플랜 및 아키텍처 | ✅ 완료 |
| 01 | [01-AWS-RESOURCES-CHECKLIST.md](01-AWS-RESOURCES-CHECKLIST.md) | AWS 리소스 생성 체크리스트 | ✅ 완료 |
| 02 | [02-AWS-IAM-USER-CREATION-GUIDE.md](02-AWS-IAM-USER-CREATION-GUIDE.md) | IAM 사용자 생성 기본 가이드 | ✅ 완료 |
| 03 | [03-AWS-IAM-SETUP-GUIDE.md](03-AWS-IAM-SETUP-GUIDE.md) | IAM 설정 상세 가이드 | ✅ 완료 |

### ⭐ 핵심 작업 문서
| 파일명 | 설명 | 우선순위 |
|--------|------|----------|
| **[04-NEXT-STEPS-GUIDE.md](04-NEXT-STEPS-GUIDE.md)** | **향후 진행 단계 상세 가이드** | 🔴 **최우선** |
| [05-PROJECT-ROADMAP.svg](05-PROJECT-ROADMAP.svg) | 프로젝트 로드맵 시각화 | 🟡 중요 |

### 🔧 참고 및 복구 문서
| 파일명 | 설명 | 용도 |
|--------|------|------|
| [QUICK-RECOVERY-GUIDE.md](QUICK-RECOVERY-GUIDE.md) | 빠른 문제 해결 가이드 | 에러 발생 시 |
| [SESSION-RECOVERY.md](SESSION-RECOVERY.md) | 세션 복구 가이드 | 작업 중단 후 재개 시 |

### 🔒 보안 파일
| 파일명 | 설명 | 주의사항 |
|--------|------|----------|
| `.aws-credentials.txt` | AWS 자격증명 저장 | ⚠️ **Git 제외** |

---

## 🎯 문서 읽는 순서

### 🆕 처음 시작하는 경우

```
1. 00-MASTER-PLAN.md
   ↓ (전체 계획 이해)
   
2. 05-PROJECT-ROADMAP.svg  
   ↓ (현재 위치 파악)
   
3. 04-NEXT-STEPS-GUIDE.md ⭐ 
   ↓ (상세 작업 시작)
   
4. 01-AWS-RESOURCES-CHECKLIST.md
   ↓ (리소스 생성)
   
5. 02-AWS-IAM-USER-CREATION-GUIDE.md
   ↓ (IAM 설정)
   
6. 03-AWS-IAM-SETUP-GUIDE.md
   (권한 설정)
```

### 🔄 작업 재개 시

```
1. 05-PROJECT-ROADMAP.svg
   ↓ (진행 상황 확인)
   
2. 04-NEXT-STEPS-GUIDE.md
   (다음 단계 진행)
```

### 🆘 문제 발생 시

```
1. QUICK-RECOVERY-GUIDE.md
   ↓ (빠른 해결)
   
2. SESSION-RECOVERY.md
   (세션 복구)
```

---

## 📊 현재 진행 상황

### Phase 3: AWS 인프라 구축 (진행 중)

**완료된 작업:**
- ✅ 문서 작성 및 정리
- ✅ 배포 계획 수립
- ✅ IAM 가이드 작성

**다음 작업:**
👉 **[04-NEXT-STEPS-GUIDE.md](04-NEXT-STEPS-GUIDE.md)** 열어서 진행!

**선택지:**
1. **방법 A**: AWS 콘솔에서 직접 리소스 생성 (초보자 추천, 3-4시간)
2. **방법 B**: 코드 파일 먼저 작성 후 배포 (경험자 추천, 2-3시간)

---

## 📖 각 문서 상세 설명

### 00-MASTER-PLAN.md
**목적:** 전체 배포 계획의 청사진
**내용:**
- Phase 1~5 전체 로드맵
- AWS 아키텍처 설계
- 기술 스택 및 도구
- Blue/Green 배포 전략

**읽어야 할 때:**
- 프로젝트 시작 전 전체 계획 이해
- 아키텍처 설계 리뷰
- 팀원에게 프로젝트 설명

---

### 01-AWS-RESOURCES-CHECKLIST.md
**목적:** AWS 리소스 생성 체크리스트
**내용:**
- 생성해야 할 모든 AWS 리소스 목록
- 각 리소스별 설정 값
- 리소스 간 연결 관계

**읽어야 할 때:**
- AWS 리소스 생성 전
- 생성된 리소스 확인
- 누락된 리소스 파악

---

### 02-AWS-IAM-USER-CREATION-GUIDE.md
**목적:** IAM 사용자 생성 기본 가이드
**내용:**
- IAM 사용자 생성 단계
- Access Key 발급 방법
- 스크린샷 포함 설명

**읽어야 할 때:**
- 처음 IAM 사용자 만들 때
- Access Key 재발급 시
- 권한 문제 발생 시

---

### 03-AWS-IAM-SETUP-GUIDE.md
**목적:** IAM 설정 상세 가이드
**내용:**
- 필요한 IAM 권한 정책
- Role 및 Policy 설정
- 보안 best practices

**읽어야 할 때:**
- 세밀한 권한 설정 필요 시
- 보안 강화 작업 시
- 권한 에러 디버깅 시

---

### 04-NEXT-STEPS-GUIDE.md ⭐
**목적:** 현재 단계 상세 작업 가이드
**내용:**
- 방법 A (AWS 콘솔) 12단계 상세 가이드
- 방법 B (코드 우선) 3단계 가이드
- 각 단계별 예상 소요 시간
- 체크리스트
- 문제 해결 가이드
- 비용 및 보안 주의사항

**읽어야 할 때:**
- 지금 당장! (가장 중요한 문서)
- 다음 작업할 내용 확인
- 작업 중 참고

---

### 05-PROJECT-ROADMAP.svg
**목적:** 프로젝트 진행 상황 시각화
**내용:**
- Phase 1~5 진행률
- 완료/진행 중/대기 단계 구분
- 전체 진행률 표시

**읽어야 할 때:**
- 현재 위치 파악
- 팀 미팅 시 진행 상황 공유
- 남은 작업량 확인

---

### QUICK-RECOVERY-GUIDE.md
**목적:** 빠른 문제 해결 가이드
**내용:**
- 자주 발생하는 에러
- 해결 방법
- 로그 확인 방법

**읽어야 할 때:**
- 배포 실패 시
- 서비스 장애 시
- 에러 메시지 발생 시

---

### SESSION-RECOVERY.md
**목적:** 작업 중단 후 재개 가이드
**내용:**
- 중단 시점 파악 방법
- 재개 절차
- 데이터 복구 방법

**읽어야 할 때:**
- 작업 중단 후 재개 시
- 환경 재설정 필요 시
- 진행 상황 불명확할 때

---

## 🔒 보안 중요 사항

### ⚠️ .aws-credentials.txt 관리

**이 파일에는 다음 정보가 포함됩니다:**
```
AWS_ACCESS_KEY_ID=AKIA...
AWS_SECRET_ACCESS_KEY=wJalr...
AWS_REGION=ap-northeast-2
```

**절대 하지 말아야 할 것:**
- ❌ Git에 커밋
- ❌ 스크린샷 찍기
- ❌ 타인과 공유
- ❌ 공개 장소에 게시

**해야 할 것:**
- ✅ 로컬에만 보관
- ✅ 정기적으로 키 rotation
- ✅ 사용하지 않을 때 비활성화
- ✅ `.gitignore`에 포함 확인

---

## 📞 도움말

### AWS 관련 문의
- AWS 공식 문서: https://docs.aws.amazon.com/
- AWS ECS: https://docs.aws.amazon.com/ecs/
- AWS CodeDeploy: https://docs.aws.amazon.com/codedeploy/

### Spring Boot 관련
- Spring Boot 문서: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security

### Docker 관련
- Docker 문서: https://docs.docker.com/
- Docker Compose: https://docs.docker.com/compose/

---

## 🔄 다음 단계

### 현재 해야 할 일
1. **[NEXT-STEPS-GUIDE.md](NEXT-STEPS-GUIDE.md)** 문서 열기
2. 방법 A 또는 방법 B 선택
3. 단계별로 진행

### Phase 3 완료 후
- Phase 4: GitHub Actions CI/CD 파이프라인 구축
- Phase 5: 모니터링 및 최적화

---

**생성일:** 2025-10-26  
**최종 수정일:** 2025-10-26  
**버전:** 1.0.0  
**관리자:** Library Management Team
