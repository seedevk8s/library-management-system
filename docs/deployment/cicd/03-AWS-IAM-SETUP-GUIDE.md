# AWS IAM 사용자 생성 가이드

> GitHub Actions에서 ECS 배포를 위한 IAM 사용자를 직접 만드는 방법

---

## 1단계: AWS Console 로그인

1. 브라우저에서 https://aws.amazon.com/console/ 접속
2. 본인의 AWS 계정으로 로그인
3. 리전이 **서울 (ap-northeast-2)** 인지 확인 (우측 상단)

---

## 2단계: IAM 서비스로 이동

1. 상단 검색창에 **"IAM"** 입력
2. **IAM** 클릭하여 서비스 이동
3. 왼쪽 메뉴에서 **"사용자(Users)"** 클릭

---

## 3단계: 새 사용자 생성

### 3-1. 사용자 생성 시작
1. 우측 상단 **"사용자 추가(Create user)"** 버튼 클릭

### 3-2. 사용자 세부 정보 입력
```
사용자 이름: github-actions-deploy
```
- 이 이름은 GitHub Actions에서 사용할 계정임을 나타냄
- 다른 이름을 사용해도 무방하지만 목적을 명확히 알 수 있는 이름 권장

### 3-3. AWS 액세스 유형 선택
- ✅ **"Programmatic access"** 체크
  - Access key ID와 Secret access key를 받음
  - CLI, SDK, API 호출에 사용
- ❌ **"AWS Management Console access"** 체크 해제
  - 웹 콘솔 로그인은 필요 없음

**Next** 클릭

---

## 4단계: 권한 설정

### 4-1. 권한 추가 방법 선택
- **"Attach existing policies directly"** 선택
- 기존 정책을 직접 첨부하는 방식

### 4-2. 필요한 정책 검색 및 첨부

아래 6개 정책을 **하나씩 검색**하여 체크:

#### 1) AmazonEC2ContainerRegistryPowerUser
```
검색: ECR
찾기: AmazonEC2ContainerRegistryPowerUser
역할: Docker 이미지를 ECR에 업로드하고 관리
```

#### 2) AmazonECS_FullAccess
```
검색: ECS
찾기: AmazonECS_FullAccess
역할: ECS 클러스터, 서비스, Task Definition 관리
```

#### 3) AmazonEC2FullAccess
```
검색: EC2
찾기: AmazonEC2FullAccess
역할: ECS 클러스터용 EC2 인스턴스 관리
```

#### 4) ElasticLoadBalancingFullAccess
```
검색: LoadBalancing
찾기: ElasticLoadBalancingFullAccess
역할: Application Load Balancer 관리
```

#### 5) AWSCodeDeployFullAccess
```
검색: CodeDeploy
찾기: AWSCodeDeployFullAccess
역할: Blue-Green 배포 자동화
```

#### 6) CloudWatchLogsFullAccess
```
검색: CloudWatch Logs
찾기: CloudWatchLogsFullAccess
역할: 애플리케이션 로그 확인 및 모니터링
```

**총 6개 정책이 선택되었는지 확인**

**Next** 클릭

---

## 5단계: 태그 추가 (선택사항)

태그는 선택사항이지만 권장:
```
Key: Purpose
Value: GitHub-Actions-ECS-Deploy
```

**Next** 클릭

---

## 6단계: 검토 및 생성

### 확인 사항:
- 사용자 이름: `github-actions-deploy`
- AWS 액세스 유형: Programmatic access
- 권한 정책: 6개

**모든 내용이 맞으면 "Create user" 클릭**

---

## 7단계: Access Key 저장 (중요!)

### ⚠️ 이 화면은 단 한 번만 표시됩니다!

화면에 표시되는 정보:
```
Access key ID: AKIA로 시작하는 20자 문자열
Secret access key: 40자 문자열 (숨겨져 있음, "Show" 클릭 필요)
```

### 저장 방법:

#### 방법 1: 직접 복사하여 저장
1. **"Show"** 클릭하여 Secret access key 표시
2. 메모장 열기
3. 다음 형식으로 저장:
```
AWS_ACCESS_KEY_ID=여기에_Access_Key_ID_붙여넣기
AWS_SECRET_ACCESS_KEY=여기에_Secret_Access_Key_붙여넣기
AWS_REGION=ap-northeast-2
```

4. 파일을 프로젝트 루트에 `.aws-credentials.txt` 이름으로 저장

#### 방법 2: CSV 다운로드
1. **"Download .csv"** 버튼 클릭
2. CSV 파일 안전한 곳에 보관
3. 나중에 필요할 때 열어서 확인

### ⚠️ 보안 주의사항:
- ❌ 이 정보를 Git에 절대 커밋하지 마세요
- ❌ 공개 채널에 공유하지 마세요
- ❌ 스크린샷을 SNS에 올리지 마세요
- ✅ `.gitignore`에 이미 추가되어 있어 안전합니다

**"Close"** 클릭

---

## 8단계: .aws-credentials.txt 파일 생성

프로젝트 루트에 `.aws-credentials.txt` 파일 생성:

```bash
# Windows
notepad .aws-credentials.txt

# 내용:
AWS_ACCESS_KEY_ID=AKIA로_시작하는_키
AWS_SECRET_ACCESS_KEY=40자_시크릿_키
AWS_REGION=ap-northeast-2
```

**저장 후 닫기**

---

## 9단계: .gitignore 확인

`.gitignore` 파일에 이미 추가되어 있는지 확인:

```gitignore
### AWS Credentials (NEVER COMMIT!) ###
.aws-credentials.txt
**/aws-credentials*
**/.aws-credentials*
*.pem
*.key
```

✅ 이미 추가되어 있으면 안전합니다.

---

## 10단계: 작업 완료 확인

### 생성된 것:
- ✅ IAM 사용자: `github-actions-deploy`
- ✅ Access Key 1개
- ✅ 권한 정책 6개 첨부
- ✅ `.aws-credentials.txt` 파일 생성
- ✅ `.gitignore`에 credentials 보호 설정

### 다음 단계:
이 Access Key는 나중에 GitHub Secrets에 등록하여 사용합니다.
(Phase 4에서 진행)

---

## 권한 정책 상세 설명

### 왜 이 6개 정책이 필요한가?

#### 1. AmazonEC2ContainerRegistryPowerUser
**목적:** Docker 이미지 관리
**사용 시점:** 
- GitHub Actions에서 Docker 이미지 빌드
- ECR에 이미지 푸시
**권한:**
- ECR 리포지토리 생성/삭제
- 이미지 업로드/다운로드
- 이미지 태그 관리

#### 2. AmazonECS_FullAccess
**목적:** ECS 리소스 관리
**사용 시점:**
- Task Definition 등록/업데이트
- ECS 서비스 업데이트
- 클러스터 관리
**권한:**
- 클러스터, 서비스, 태스크 생성/수정/삭제
- Task Definition 등록
- 서비스 배포

#### 3. AmazonEC2FullAccess
**목적:** EC2 인스턴스 관리
**사용 시점:**
- ECS 클러스터의 EC2 인스턴스 관리
- Auto Scaling 설정
**권한:**
- EC2 인스턴스 시작/중지
- Security Group 관리
- Auto Scaling 그룹 관리

#### 4. ElasticLoadBalancingFullAccess
**목적:** 로드 밸런서 관리
**사용 시점:**
- ALB 설정 변경
- Target Group 관리
- Blue-Green 배포 시 트래픽 전환
**권한:**
- ALB 생성/수정/삭제
- Target Group 관리
- 리스너 규칙 설정

#### 5. AWSCodeDeployFullAccess
**목적:** Blue-Green 배포
**사용 시점:**
- CodeDeploy로 배포 시작
- 배포 상태 모니터링
- 롤백 실행
**권한:**
- 배포 그룹 관리
- 배포 시작/중지
- 배포 히스토리 조회

#### 6. CloudWatchLogsFullAccess
**목적:** 로그 및 모니터링
**사용 시점:**
- 배포 중 로그 확인
- 애플리케이션 에러 디버깅
- 성능 모니터링
**권한:**
- 로그 그룹 생성/삭제
- 로그 스트림 조회
- 메트릭 확인

---

## 최소 권한 원칙

### 왜 FullAccess가 아닌 제한된 권한을 사용하지 않나?

**학습 단계에서는 FullAccess 권장:**
- 처음에는 어떤 권한이 필요한지 정확히 모름
- 권한 부족 에러로 시간 낭비 방지
- 빠른 프로토타이핑 가능

**프로덕션에서는:**
- 실제로 사용한 권한만 추출
- 커스텀 정책 생성
- 최소 권한만 부여

---

## 트러블슈팅

### Q1: Access Key를 저장하지 못하고 화면을 닫았어요
**A:** Access Key를 삭제하고 새로 생성해야 합니다.
1. IAM → Users → github-actions-deploy
2. Security credentials 탭
3. Access keys 섹션에서 기존 키 "Delete"
4. "Create access key" 다시 클릭

### Q2: 정책을 찾을 수 없어요
**A:** 검색어를 정확히 입력했는지 확인
- 대소문자 구분 없음
- 부분 검색 가능 (예: "ECR"만 입력해도 됨)

### Q3: Git에 실수로 커밋했어요
**A:** 즉시 조치 필요
1. AWS Console에서 Access Key 삭제
2. 새 Access Key 생성
3. Git history에서 파일 완전 제거
```bash
git filter-branch --force --index-filter \
"git rm --cached --ignore-unmatch .aws-credentials.txt" \
--prune-empty --tag-name-filter cat -- --all
```

---

## 다음 단계

✅ IAM 사용자 생성 완료
⬜ Phase 2: GitHub Actions 워크플로우 작성
⬜ Phase 3: AWS 인프라 구축
⬜ Phase 4: GitHub Secrets 등록

---

**작성일:** 2025-10-25
**문서 버전:** 1.0
