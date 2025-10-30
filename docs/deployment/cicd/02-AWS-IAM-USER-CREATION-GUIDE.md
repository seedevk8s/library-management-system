# AWS IAM 사용자 생성 완벽 가이드
## GitHub Actions 배포용 IAM 사용자 단계별 생성

---

## 🎯 목표
- **사용자 이름**: `github-actions-deployer`
- **목적**: GitHub Actions에서 AWS ECR/ECS 배포 자동화
- **필요 권한**: ECR Push, ECS 배포
- **소요 시간**: 약 5-10분

---

## 📋 전체 프로세스 개요

```
IAM 콘솔 접속 → Users 메뉴 → Create user → 
사용자 정보 입력 → 권한 설정 → 생성 완료 → 
Access Key 생성 → 키 저장
```

---

# STEP 1: IAM 서비스로 이동

![Step 1](screenshots/iam-creation/step01-iam-console.svg)

### 작업:
1. AWS Console 상단 검색창 클릭
2. `IAM` 입력
3. **"IAM"** 서비스 클릭

### 화면 위치:
- 검색창: 상단 중앙
- IAM 서비스: 검색 결과 첫 번째 (Services 섹션 아래)

---

# STEP 2: Users 페이지로 이동

![Step 2](screenshots/iam-creation/step02-users-page.svg)

### 작업:
1. 왼쪽 사이드바에서 **"Users"** 클릭

### 화면 위치:
- 사이드바: 화면 왼쪽
- Users 메뉴: "Access management" 섹션 아래
- 파란색으로 강조 표시됨

---

# STEP 3: Create user 시작

![Step 2 - Create Button](screenshots/iam-creation/step02-users-page.svg)

### 작업:
1. 오른쪽 상단 **"Create user"** 버튼 클릭 (주황색 버튼)

### 화면 위치:
- 버튼 위치: 페이지 우측 상단
- 버튼 색상: AWS 오렌지색 (#FF9900)

---

# STEP 4: 사용자 정보 입력

![Step 3](screenshots/iam-creation/step03-user-details.svg)

## 4-1. User name 입력

### 입력값:
```
github-actions-deployer
```

### 주의사항:
- ✅ 정확히 위와 같이 입력 (복사-붙여넣기 권장)
- ✅ 소문자, 하이픈만 사용
- ❌ 공백, 특수문자 불가

## 4-2. AWS Management Console access

### 작업:
- **체크 해제** (콘솔 접근 불필요)

### 이유:
- 이 사용자는 프로그래매틱 접근(API)만 사용
- GitHub Actions에서 사용하므로 사람이 직접 로그인할 필요 없음

## 4-3. Next 클릭

### 작업:
- 하단 우측 **"Next"** 버튼 클릭

---

# STEP 5: 권한 설정

![Step 4](screenshots/iam-creation/step04-set-permissions.svg)

## 5-1. Permission options 선택

### 작업:
- **"Attach policies directly"** 라디오 버튼 선택
- 3가지 옵션 중 두 번째 옵션

## 5-2. 첫 번째 정책 추가

### 검색 및 선택:
1. 검색창에 입력:
```
AmazonEC2ContainerRegistryPowerUser
```

2. **정확한 이름 확인:**
   - `AmazonEC2ContainerRegistryPowerUser`
   - Type: AWS managed
   
3. 체크박스 선택

### 이 정책이 필요한 이유:
- ECR(Elastic Container Registry)에 Docker 이미지 Push
- ECR 저장소 관리
- 이미지 태그 관리

## 5-3. 두 번째 정책 추가

### 검색 및 선택:
1. 검색창 내용 지우기 (X 버튼 또는 전체 선택 후 삭제)

2. 새로 입력:
```
AmazonECS_FullAccess
```

3. **정확한 이름 확인:**
   - `AmazonECS_FullAccess`
   - Type: AWS managed
   
4. 체크박스 선택

### 이 정책이 필요한 이유:
- ECS(Elastic Container Service) 서비스 배포
- Task Definition 업데이트
- 서비스 재시작 및 관리

## 5-4. 선택 확인

### 확인 사항:
- ✅ 총 **2개의 정책**이 선택되어야 함
- ✅ 체크박스에 파란색 체크 표시 확인
- ✅ 하단에 "2 policies selected" 표시 확인

## 5-5. Next 클릭

### 작업:
- 하단 우측 **"Next"** 버튼 클릭

---

# STEP 6: Review and create

![Step 5](screenshots/iam-creation/step05-review-create.svg)

## 6-1. 설정 최종 확인

### 확인 항목:

#### User details:
- **User name**: `github-actions-deployer` ✓
- **AWS Management Console access**: Disabled ✓
- **Permissions boundary**: Not set ✓

#### Permissions:
- **Permissions policies**: 2 policies ✓
  1. `AmazonEC2ContainerRegistryPowerUser`
  2. `AmazonECS_FullAccess`

## 6-2. Create user 클릭

### 작업:
- 모든 정보 확인 후
- 하단 우측 **"Create user"** 버튼 클릭 (주황색 버튼)

## 6-3. 성공 메시지 확인

### 확인 사항:
- ✅ "User github-actions-deployer created successfully" 메시지 표시
- ✅ 녹색 성공 배너 표시

---

# STEP 7: Access Key 생성 시작

![Step 6](screenshots/iam-creation/step06-security-credentials.svg)

## 7-1. 사용자 상세 페이지로 이동

### 방법 (둘 중 하나):

**방법 A**: 성공 메시지 화면에서
1. **"View user"** 버튼 클릭

**방법 B**: Users 목록에서
1. 왼쪽 사이드바 **"Users"** 클릭
2. 목록에서 **"github-actions-deployer"** 클릭

## 7-2. Security credentials 탭 클릭

### 작업:
1. 페이지 상단 탭 메뉴에서
2. **"Security credentials"** 탭 클릭

### 화면 위치:
- 탭 위치: 사용자 이름 아래
- 탭 순서: Permissions → **Security credentials** → Tags

## 7-3. Access keys 섹션 찾기

### 작업:
1. 아래로 스크롤
2. "Access keys" 섹션 찾기

### 화면 내용:
- 제목: "Access keys"
- 설명: "Access keys are used to sign programmatic requests to AWS"
- 현재 상태: "No access keys" (처음 생성하는 경우)

## 7-4. Create access key 시작

### 작업:
- **"Create access key"** 버튼 클릭 (주황색 버튼)

---

# STEP 8: Access key 설정

![Step 7](screenshots/iam-creation/step07-access-key-usecase.svg)

## 8-1. Use case 선택

### 작업:
- **"Command Line Interface (CLI)"** 라디오 버튼 선택

### 화면 내용:
여러 옵션이 표시되는데, 다음을 선택:
- ✅ **Command Line Interface (CLI)**
- ❌ Local code
- ❌ Application running on an AWS compute service
- ❌ Application running outside AWS
- ❌ Third-party service
- ❌ Other

### 선택 이유:
- GitHub Actions는 CLI 기반 스크립트 실행 환경
- AWS CLI 명령어를 사용하여 배포 수행

## 8-2. 확인 체크박스

### 작업:
1. 페이지 하단 체크박스 선택
2. 체크박스 텍스트:
   ```
   I understand the above recommendation and want to proceed to create an access key.
   ```

### 주의:
- ⚠️ 이 체크박스를 선택해야 Next 버튼 활성화됨

## 8-3. Next 클릭

### 작업:
- 하단 우측 **"Next"** 버튼 클릭

---

# STEP 9: Description tag 입력

![Step 8](screenshots/iam-creation/step08-description-tag.svg)

## 9-1. Description 입력 (선택사항이지만 강력 권장)

### 권장 입력값:
```
GitHub Actions deployment key for library-management-system
```

### 또는 다음 형식으로 커스터마이즈:
```
GitHub Actions deployment key for [프로젝트명]
```

### Description 작성 가이드:

#### 좋은 예시 ✅:
- `GitHub Actions deployment key for library-management-system`
- `CI/CD pipeline key for production deployment`
- `Automated ECS deployment key - DO NOT DELETE`

#### 나쁜 예시 ❌:
- `key` (너무 간단)
- `test` (목적 불명확)
- `123` (의미 없음)

### 설명이 중요한 이유:
1. **다중 키 관리**: 나중에 여러 Access key를 만들 때 구분 가능
2. **팀 협업**: 다른 개발자가 키의 용도를 즉시 파악
3. **보안 감사**: 사용하지 않는 키를 식별하고 삭제할 때 유용
4. **문제 해결**: 키 관련 오류 발생 시 빠른 디버깅

## 9-2. Create access key 클릭

### 작업:
- 하단 우측 **"Create access key"** 버튼 클릭 (주황색 버튼)

---

# STEP 10: Access Key 저장 (🚨 매우 중요!)

![Step 9](screenshots/iam-creation/step09-access-key-created.svg)

## ⚠️ 중대 경고

```
이 화면을 벗어나면 Secret access key를 다시 볼 수 없습니다!
반드시 지금 저장하세요!
```

## 10-1. 키 정보 확인

화면에 다음 두 가지 정보가 표시됩니다:

### Access key ID:
- 형식: `AKIA...`로 시작하는 20자 문자열
- 예시: `AKIAIOSFODNN7EXAMPLE`
- 특징: 공개되어도 상대적으로 안전 (하지만 노출 지양)

### Secret access key:
- 형식: 40자의 랜덤 문자열
- 예시: `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY`
- 특징: **절대로 공개되어서는 안 됨!**

## 10-2. 키 저장 방법

### 🥇 방법 1: CSV 다운로드 (가장 권장!)

#### 작업:
1. **"Download .csv file"** 버튼 클릭 (녹색 버튼)
2. 파일이 다운로드 폴더에 저장됨
3. 파일명: `[username]_accessKeys.csv`

#### CSV 파일 내용:
```csv
User name,Access key ID,Secret access key
github-actions-deployer,AKIAIOSFODNN7EXAMPLE,wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
```

#### 파일 보관 방법:
- ✅ 암호화된 USB에 저장
- ✅ 비밀번호 관리 프로그램에 저장 (1Password, LastPass 등)
- ✅ 암호화된 클라우드 저장소 (암호 걸린 ZIP)
- ❌ 데스크탑에 그대로 보관
- ❌ 이메일로 전송
- ❌ Git에 커밋

### 🥈 방법 2: 개별 복사

#### Access key ID 복사:
1. Access key ID 옆 **"Copy"** 아이콘 클릭
2. 메모장 열기 (Win + R → notepad → Enter)
3. 메모장에 붙여넣기 (Ctrl + V)
4. 레이블 추가:
```
AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
```

#### Secret access key 복사:
1. Secret access key 옆 **"Show"** 버튼 클릭 (키가 표시됨)
2. Secret access key 옆 **"Copy"** 아이콘 클릭
3. 메모장에 붙여넣기
4. 레이블 추가:
```
AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
```

#### 최종 메모장 내용:
```
=== AWS IAM Access Keys ===
프로젝트: library-management-system
사용자: github-actions-deployer
생성일: 2025-10-25

AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
AWS_REGION=ap-northeast-2
```

## 10-3. 키 저장 확인

### 확인 체크리스트:
- [ ] Access key ID를 안전하게 저장했다
- [ ] Secret access key를 안전하게 저장했다
- [ ] CSV 파일을 다운로드했다 (권장)
- [ ] 저장 위치를 기억하고 있다
- [ ] 메모장/파일을 백업했다

## 10-4. Done 클릭

### ⚠️ 주의사항:
- Done을 클릭하기 전에 반드시 키를 저장했는지 재확인!
- Done 클릭 후에는 Secret access key를 다시 볼 수 없음!

### 작업:
- 키 저장 완료 확인 후
- **"Done"** 버튼 클릭

---

## ✅ 생성 완료!

축하합니다! IAM 사용자와 Access key 생성이 완료되었습니다.

### 생성된 항목 확인:
- [x] IAM 사용자 `github-actions-deployer` 생성
- [x] 2개의 정책 연결
  - [x] AmazonEC2ContainerRegistryPowerUser
  - [x] AmazonECS_FullAccess
- [x] Access key 생성
- [x] Access key ID 저장
- [x] Secret access key 저장

---

## 📝 다음 단계: GitHub Secrets 등록

이제 저장한 키를 GitHub Secrets에 등록해야 합니다.

### GitHub Repository 설정:
1. GitHub 저장소로 이동
2. **Settings** 탭 클릭
3. 왼쪽 사이드바에서 **Secrets and variables** → **Actions** 클릭
4. **New repository secret** 버튼 클릭

### 등록할 Secrets:

#### Secret 1: AWS_ACCESS_KEY_ID
- Name: `AWS_ACCESS_KEY_ID`
- Value: `[저장한 Access key ID]`
- 예: `AKIAIOSFODNN7EXAMPLE`

#### Secret 2: AWS_SECRET_ACCESS_KEY
- Name: `AWS_SECRET_ACCESS_KEY`
- Value: `[저장한 Secret access key]`
- 예: `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY`

#### Secret 3: AWS_REGION
- Name: `AWS_REGION`
- Value: `ap-northeast-2`
- (서울 리전 사용 시)

### GitHub Secrets 등록 완료 확인:
- [ ] AWS_ACCESS_KEY_ID 등록됨
- [ ] AWS_SECRET_ACCESS_KEY 등록됨
- [ ] AWS_REGION 등록됨
- [ ] 각 Secret에 녹색 체크 표시 확인

---

## 🚨 보안 주의사항

### ⚠️ 절대 하지 말아야 할 것:

#### 1. Git에 키 커밋 금지
```bash
# ❌ 절대 안 됨!
git add .env
git commit -m "Add AWS keys"
git push
```

#### 2. 코드에 하드코딩 금지
```python
# ❌ 절대 안 됨!
AWS_ACCESS_KEY_ID = "AKIAIOSFODNN7EXAMPLE"
AWS_SECRET_ACCESS_KEY = "wJalrXUtnFEMI/K7MDENG..."
```

#### 3. 공개 장소에 노출 금지
- ❌ Slack, Discord 등 채팅에 붙여넣기
- ❌ 이메일로 전송
- ❌ 스크린샷 찍어서 공유
- ❌ 블로그나 포럼에 게시

### ✅ 안전한 사용 방법:

#### 1. 환경 변수 사용
```bash
# .env 파일 (Git에 커밋하지 않음!)
AWS_ACCESS_KEY_ID=AKIA...
AWS_SECRET_ACCESS_KEY=wJal...
```

#### 2. GitHub Secrets 사용
- GitHub Actions에서만 사용 가능
- 로그에 마스킹 처리됨
- 안전하게 암호화되어 저장됨

#### 3. AWS Secrets Manager 사용
- 프로덕션 환경에서 권장
- 자동 로테이션 가능
- 감사 로그 제공

---

## 🔧 문제 해결

### 문제 1: "User already exists" 에러

#### 원인:
- 이미 같은 이름의 사용자가 존재함

#### 해결 방법:

**옵션 A**: 기존 사용자 사용
1. Users 목록에서 `github-actions-deployer` 찾기
2. Security credentials 탭에서 Access key 확인
3. 기존 키 사용 또는 새 키 생성

**옵션 B**: 기존 사용자 삭제 후 재생성
1. Users 목록에서 사용자 선택
2. **Delete** 버튼 클릭
3. 확인 메시지에서 사용자 이름 입력
4. **Delete** 클릭
5. 가이드 처음부터 다시 진행

**옵션 C**: 다른 이름으로 생성
1. 사용자 이름 변경: `github-actions-deployer-v2`
2. 나머지 과정 동일하게 진행
3. GitHub Secrets는 동일한 이름으로 등록

---

### 문제 2: 정책을 찾을 수 없음

#### 원인:
- 검색어 오타
- Filter 설정 문제

#### 해결 방법:

1. **검색어 정확히 복사-붙여넣기**
   ```
   AmazonEC2ContainerRegistryPowerUser
   AmazonECS_FullAccess
   ```

2. **Filter 확인**
   - 검색창 위의 Filter 드롭다운 확인
   - "AWS managed" 또는 "All types" 선택
   - "Customer managed" 선택 시 AWS 정책이 안 보임

3. **대소문자 확인**
   - 정책 이름은 대소문자 구분
   - 정확히 일치해야 함

---

### 문제 3: Access key 생성 버튼 비활성화

#### 원인:
- 이미 2개의 Access key가 존재함 (최대 제한)

#### 해결 방법:

1. **기존 키 확인**
   - Security credentials 탭에서 Access keys 섹션 확인
   - 사용 중인 키 개수 확인

2. **사용하지 않는 키 삭제**
   - 사용하지 않는 키의 Actions 메뉴 클릭
   - **Deactivate** (비활성화) 또는
   - **Delete** (삭제) 선택
   - 확인 메시지에서 **Deactivate** 또는 **Delete** 클릭

3. **새 키 생성**
   - Create access key 버튼이 활성화됨
   - STEP 7부터 다시 진행

---

### 문제 4: Secret access key를 저장하지 못하고 Done을 눌렀음

#### 상황:
- Done을 클릭한 후 Secret access key 화면이 사라짐
- 키를 저장하지 못함

#### 해결 방법:

**불가능한 것:**
- ❌ Secret access key는 다시 볼 수 없음
- ❌ AWS에서도 저장하지 않음

**해야 할 것:**
1. 생성한 Access key 삭제
   - Security credentials 탭으로 이동
   - 방금 생성한 Access key 찾기
   - Actions → **Delete** 클릭

2. 새 Access key 생성
   - STEP 7부터 다시 시작
   - 이번엔 반드시 키 저장 후 Done 클릭!

---

### 문제 5: GitHub Secrets에 등록했는데 GitHub Actions가 실패함

#### 원인 가능성:

1. **Secret 이름 오타**
   - `AWS_ACCESS_KEY_ID` (올바름)
   - `AWS_ACESS_KEY_ID` (오타)

2. **키 값에 공백 포함**
   - 복사할 때 공백이나 개행 문자 포함됨

3. **키 값 잘못 입력**
   - Access key ID와 Secret access key를 바꿔서 입력

#### 해결 방법:

1. **GitHub Secrets 재확인**
   - Settings → Secrets and variables → Actions
   - 각 Secret 이름 정확히 확인
   - 필요시 Delete 후 재등록

2. **키 값 재확인**
   - 저장한 CSV 파일 또는 메모장 열기
   - 정확한 값 복사 (공백 없이!)
   - Secret 업데이트

3. **AWS CLI로 테스트**
   ```bash
   aws sts get-caller-identity
   ```
   - 키가 유효하면 사용자 정보 출력
   - 에러 발생 시 키가 잘못됨

---

## 📚 참고 자료

### AWS 공식 문서:
- [IAM 사용자 생성](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html)
- [Access Key 관리](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html)
- [IAM 정책 이해하기](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies.html)

### GitHub 공식 문서:
- [GitHub Secrets 설정](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [GitHub Actions에서 AWS 사용](https://github.com/aws-actions)

### 보안 Best Practices:
- [AWS 보안 Best Practices](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html)
- [Access Key 로테이션](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html#Using_RotateAccessKey)

---

## 💡 추가 팁

### Tip 1: 정기적인 키 로테이션
- 3개월마다 Access key 재생성 권장
- 새 키 생성 → GitHub Secrets 업데이트 → 기존 키 삭제

### Tip 2: 최소 권한 원칙
- 필요한 권한만 부여
- 현재 설정: ECR + ECS만
- 추가 권한 필요시 정책 추가

### Tip 3: CloudTrail로 사용 추적
- AWS CloudTrail에서 키 사용 로그 확인 가능
- 의심스러운 활동 감지
- 보안 감사에 활용

### Tip 4: 여러 환경에서 사용
- 개발: `github-actions-deployer-dev`
- 스테이징: `github-actions-deployer-staging`
- 프로덕션: `github-actions-deployer-prod`
- 환경별로 분리하면 보안성 향상

---

## 📞 도움이 필요하면

### 각 단계에서 문제 발생 시:

1. **현재 어느 단계인지 확인**
   - STEP 번호 확인
   - 해당 섹션 다시 읽기

2. **화면 상태 확인**
   - 버튼이 비활성화되어 있나요?
   - 에러 메시지가 표시되나요?
   - 예상과 다른 화면이 보이나요?

3. **문제 상황 설명**
   - 어떤 작업을 하려고 했나요?
   - 무엇이 예상과 달랐나요?
   - 어떤 에러 메시지가 나타났나요?

4. **Claude에게 질문**
   - 구체적인 상황 설명
   - 스크린샷이 있으면 도움됨
   - 정확한 안내를 받을 수 있음

---

## ✅ 완료 체크리스트

### IAM 사용자 생성:
- [ ] IAM 콘솔 접속 완료
- [ ] Users 페이지 이동 완료
- [ ] Create user 시작 완료
- [ ] 사용자 이름 `github-actions-deployer` 입력 완료
- [ ] Console access 체크 해제 완료
- [ ] Permission option "Attach policies directly" 선택 완료
- [ ] `AmazonEC2ContainerRegistryPowerUser` 정책 추가 완료
- [ ] `AmazonECS_FullAccess` 정책 추가 완료
- [ ] 사용자 생성 완료
- [ ] 성공 메시지 확인 완료

### Access Key 생성:
- [ ] Security credentials 탭 이동 완료
- [ ] Create access key 시작 완료
- [ ] Use case "CLI" 선택 완료
- [ ] 확인 체크박스 선택 완료
- [ ] Description 입력 완료
- [ ] Access key 생성 완료
- [ ] Access key ID 저장 완료
- [ ] Secret access key 저장 완료
- [ ] CSV 파일 다운로드 완료 (권장)

### GitHub Secrets 등록:
- [ ] GitHub Repository Settings 접속 완료
- [ ] Secrets and variables → Actions 이동 완료
- [ ] AWS_ACCESS_KEY_ID Secret 등록 완료
- [ ] AWS_SECRET_ACCESS_KEY Secret 등록 완료
- [ ] AWS_REGION Secret 등록 완료

### 보안 확인:
- [ ] 키를 안전한 곳에 저장 완료
- [ ] 키가 Git에 커밋되지 않음 확인
- [ ] 키가 코드에 하드코딩되지 않음 확인
- [ ] 키 저장 위치를 기억하고 있음

---

**🎉 모든 단계를 완료하셨습니다!**

이제 GitHub Actions에서 AWS에 자동 배포할 준비가 되었습니다.

다음 단계인 **ECR 저장소 생성**으로 진행하시면 됩니다!

---

**작성일**: 2025-10-25  
**프로젝트**: library-management-system  
**Phase**: 1 - 준비 단계  
**버전**: 2.0 - 완전판 (시각 자료 포함)
