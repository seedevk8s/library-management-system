# 🚀 다음 단계 가이드 (NEXT STEPS)

> **마지막 업데이트**: 2025-10-26  
> **현재 진행 상황 기준**: Phase 3 부분 완료

---

## 📊 현재 진행 상황 요약

### ✅ 완료된 작업

#### Phase 1: 준비 단계 (100% 완료)
- ✅ IAM 사용자 생성: `github-actions-deploy2`
- ✅ IAM 사용자 권한 설정 (5개 정책 연결)
- ✅ Access Key 생성 및 저장
- ✅ Git 브랜치 생성: `feature/cicd-ecs-blue-green-deployment`
- ✅ 마스터 플랜 및 관련 문서 6개 작성

#### Phase 2: 로컬 검증 단계 (100% 완료)
- ✅ **Dockerfile 작성**
  - Multi-stage build (builder + runtime)
  - 보안 설정 (non-root user: appuser)
  - Health check 설정
- ✅ **.dockerignore 생성**
- ✅ **docker-compose.yml 작성**
  - MySQL 8.0 컨테이너
  - 애플리케이션 컨테이너
  - 볼륨 마운트 (uploads, logs)
  - Health check 및 네트워크 설정
- ✅ **application.yml 운영 환경 설정**
  - prod 프로파일 추가
  - 환경 변수로 DB 설정 관리
  - HikariCP 연결 풀 최적화
- ✅ **로컬 빌드 및 테스트 성공**
  - Docker 이미지 빌드 (483.26 MB)
  - MySQL + App 컨테이너 동시 실행
  - 웹 애플리케이션 정상 동작 확인 (http://localhost:8081)
- ✅ **완전한 소스 코드 반영**
  - BoardController 전체 기능 구현
  - 파일 첨부, 좋아요, 댓글 기능 포함

#### Phase 3: AWS 인프라 구축 (30% 완료)
- ✅ **ECR Repository 생성**
  - Repository: `library-management-system`
  - Region: `ap-northeast-2`
- ✅ **ECS Cluster 생성**
  - Cluster: `library-management-cluster`
  - 유형: AWS Fargate
  - Region: `ap-northeast-2`
- ✅ **GitHub Secrets 설정** (5/6 완료)
  - `AWS_ACCESS_KEY_ID` ✅
  - `AWS_SECRET_ACCESS_KEY` ✅
  - `AWS_REGION` ✅
  - `ECR_REPOSITORY` ✅
  - `ECS_CLUSTER` ✅
  - `ECS_SERVICE` ⏳ (ECS 서비스 생성 후 등록 예정)

---

## 🎯 지금 바로 해야 할 작업 (우선순위 순)

### 🔴 최우선 작업 1: Git 커밋 및 푸시

**현재 상황**: Phase 2 완료 후 아직 커밋하지 않음

```bash
# 1. 현재 변경사항 확인
git status

# 2. 모든 변경사항 추가
git add .

# 3. 커밋
git commit -m "feat: Phase 2 완료 - Docker 환경 구축 및 로컬 테스트 성공

- Dockerfile 작성 (Multi-stage build, 보안 설정)
- docker-compose.yml 작성 (MySQL 8.0 + App)
- application.yml prod 프로파일 추가
- 로컬 빌드 및 테스트 완료
- 완전한 소스 코드 반영 (BoardController 전체 기능)
- 문서 업데이트 (MASTER-PLAN, IAM 가이드 등)
"

# 4. 원격 저장소에 푸시
git push origin feature/cicd-ecs-blue-green-deployment
```

**예상 소요 시간**: 5분

---

### 🔴 최우선 작업 2: ECS Task Definition 작성

**목적**: ECS에서 실행할 컨테이너 작업 정의

#### 파일 생성: `aws/ecs-task-definition.json`

```json
{
  "family": "library-management-task",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::<AWS_ACCOUNT_ID>:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::<AWS_ACCOUNT_ID>:role/ecsTaskRole",
  "containerDefinitions": [
    {
      "name": "library-app",
      "image": "<ECR_REPOSITORY_URI>:latest",
      "cpu": 512,
      "memory": 1024,
      "essential": true,
      "portMappings": [
        {
          "containerPort": 8081,
          "hostPort": 8081,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        },
        {
          "name": "AWS_REGION",
          "value": "ap-northeast-2"
        }
      ],
      "secrets": [
        {
          "name": "SPRING_DATASOURCE_URL",
          "valueFrom": "arn:aws:ssm:ap-northeast-2:<AWS_ACCOUNT_ID>:parameter/library/db/url"
        },
        {
          "name": "SPRING_DATASOURCE_USERNAME",
          "valueFrom": "arn:aws:ssm:ap-northeast-2:<AWS_ACCOUNT_ID>:parameter/library/db/username"
        },
        {
          "name": "SPRING_DATASOURCE_PASSWORD",
          "valueFrom": "arn:aws:ssm:ap-northeast-2:<AWS_ACCOUNT_ID>:parameter/library/db/password"
        }
      ],
      "healthCheck": {
        "command": [
          "CMD-SHELL",
          "curl -f http://localhost:8081/actuator/health || exit 1"
        ],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      },
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/library-management-task",
          "awslogs-region": "ap-northeast-2",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

**⚠️ 주의사항**:
- `<AWS_ACCOUNT_ID>`: 본인의 AWS 계정 ID로 교체
- `<ECR_REPOSITORY_URI>`: ECR 리포지토리 URI로 교체
  - 예시: `123456789012.dkr.ecr.ap-northeast-2.amazonaws.com/library-management-system`

**예상 소요 시간**: 15분

---

### 🔴 최우선 작업 3: GitHub Actions 워크플로우 작성

**목적**: 코드 푸시 시 자동으로 빌드 → ECR 푸시 → ECS 배포

#### 파일 생성: `.github/workflows/deploy-to-ecs.yml`

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
  ECS_TASK_DEFINITION: aws/ecs-task-definition.json
  CONTAINER_NAME: library-app

jobs:
  deploy:
    name: Deploy to ECS
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Build with Gradle
        run: ./gradlew clean build -x test

      - name: Run tests
        run: ./gradlew test

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ env.AWS_REGION }}

      - name: Login to Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v2

      - name: Build, tag, and push image to Amazon ECR
        id: build-image
        env:
          ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
          IMAGE_TAG: ${{ github.sha }}
        run: |
          # Docker 이미지 빌드
          docker build -t $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG .
          docker build -t $ECR_REGISTRY/$ECR_REPOSITORY:latest .
          
          # ECR에 푸시
          docker push $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG
          docker push $ECR_REGISTRY/$ECR_REPOSITORY:latest
          
          echo "image=$ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG" >> $GITHUB_OUTPUT

      - name: Fill in the new image ID in the Amazon ECS task definition
        id: task-def
        uses: aws-actions/amazon-ecs-render-task-definition@v1
        with:
          task-definition: ${{ env.ECS_TASK_DEFINITION }}
          container-name: ${{ env.CONTAINER_NAME }}
          image: ${{ steps.build-image.outputs.image }}

      - name: Deploy Amazon ECS task definition
        uses: aws-actions/amazon-ecs-deploy-task-definition@v2
        with:
          task-definition: ${{ steps.task-def.outputs.task-definition }}
          service: ${{ env.ECS_SERVICE }}
          cluster: ${{ env.ECS_CLUSTER }}
          wait-for-service-stability: true

      - name: Deployment success notification
        if: success()
        run: |
          echo "✅ Deployment successful!"
          echo "Cluster: ${{ env.ECS_CLUSTER }}"
          echo "Service: ${{ env.ECS_SERVICE }}"
          echo "Image: ${{ steps.build-image.outputs.image }}"
```

**예상 소요 시간**: 20분

---

### 🟡 중요 작업 4: AWS Systems Manager Parameter Store 설정

**목적**: 민감한 정보(DB 비밀번호 등)를 안전하게 저장

#### AWS Console에서 작업:

1. **AWS Console 로그인** → **Systems Manager** 서비스 이동
2. **Parameter Store** → **Create parameter**

**파라미터 3개 생성**:

```plaintext
1. DB URL
   - Name: /library/db/url
   - Type: SecureString
   - Value: jdbc:mysql://localhost:3306/library_db
   (RDS 생성 후 엔드포인트로 교체 예정)

2. DB Username
   - Name: /library/db/username
   - Type: SecureString
   - Value: root

3. DB Password
   - Name: /library/db/password
   - Type: SecureString
   - Value: rootpassword
```

**예상 소요 시간**: 10분

---

### 🟡 중요 작업 5: CloudWatch Logs 그룹 생성

**목적**: ECS 컨테이너 로그를 저장할 위치 생성

#### AWS Console에서 작업:

1. **CloudWatch** 서비스 이동
2. **Logs** → **Log groups** → **Create log group**

**설정**:
```plaintext
- Log group name: /ecs/library-management-task
- Retention setting: 7 days (비용 절감)
```

**예상 소요 시간**: 5분

---

### 🟡 중요 작업 6: IAM Role 생성 (ECS Task용)

**목적**: ECS Task가 다른 AWS 서비스에 접근할 수 있도록 권한 부여

#### 필요한 Role 2개:

**1) ecsTaskExecutionRole** (이미 존재할 수 있음)
```plaintext
- Use case: Elastic Container Service Task
- Permissions policies:
  - AmazonECSTaskExecutionRolePolicy
  - CloudWatchLogsFullAccess (로그 쓰기)
  - AmazonSSMReadOnlyAccess (Parameter Store 읽기)
```

**2) ecsTaskRole** (새로 생성)
```plaintext
- Use case: Elastic Container Service Task
- Permissions policies:
  - AmazonS3FullAccess (파일 업로드용, 나중에 필요)
  - SecretsManagerReadWrite (Secrets Manager 사용 시)
```

**예상 소요 시간**: 15분

---

### 🟢 다음 단계 작업 7: VPC 및 네트워크 설정

**목적**: ECS Task와 ALB가 통신할 네트워크 환경 구성

#### 기본 VPC 사용 또는 새로 생성

**옵션 1: 기본 VPC 사용** (권장 - 빠름)
- 기존 VPC 확인: **VPC** → **Your VPCs**
- 기본 VPC가 있으면 그대로 사용
- Public Subnets 2개 이상 확인

**옵션 2: 새 VPC 생성** (더 복잡하지만 권장 구성)

```plaintext
VPC 생성:
- Name: library-vpc
- IPv4 CIDR: 10.0.0.0/16

Public Subnets:
- library-public-subnet-1a (10.0.1.0/24) - AZ: ap-northeast-2a
- library-public-subnet-1c (10.0.2.0/24) - AZ: ap-northeast-2c

Private Subnets:
- library-private-subnet-1a (10.0.11.0/24) - AZ: ap-northeast-2a
- library-private-subnet-1c (10.0.12.0/24) - AZ: ap-northeast-2c

Internet Gateway:
- Name: library-igw
- Attach to VPC

Route Tables:
- Public Route Table: 0.0.0.0/0 → Internet Gateway
- Private Route Table: 로컬 트래픽만
```

**예상 소요 시간**: 30분 (기본 VPC 사용 시 5분)

---

### 🟢 다음 단계 작업 8: Security Groups 생성

**목적**: 네트워크 트래픽 제어

#### 3개의 Security Group 생성:

**1) ALB Security Group**
```plaintext
Name: library-alb-sg
Description: Security group for Application Load Balancer

Inbound Rules:
- Type: HTTP, Port: 80, Source: 0.0.0.0/0
- Type: HTTPS, Port: 443, Source: 0.0.0.0/0

Outbound Rules:
- Type: All traffic, Destination: 0.0.0.0/0
```

**2) ECS Task Security Group**
```plaintext
Name: library-ecs-task-sg
Description: Security group for ECS tasks

Inbound Rules:
- Type: Custom TCP, Port: 8081, Source: library-alb-sg

Outbound Rules:
- Type: All traffic, Destination: 0.0.0.0/0
```

**3) RDS Security Group** (나중에 RDS 생성 시 필요)
```plaintext
Name: library-rds-sg
Description: Security group for RDS MySQL

Inbound Rules:
- Type: MySQL/Aurora, Port: 3306, Source: library-ecs-task-sg

Outbound Rules:
- Type: All traffic, Destination: 0.0.0.0/0
```

**예상 소요 시간**: 20분

---

### 🟢 다음 단계 작업 9: Application Load Balancer 생성

**목적**: 인터넷 트래픽을 ECS Task로 전달

#### ALB 생성:

```plaintext
1. EC2 → Load Balancers → Create Load Balancer
2. Application Load Balancer 선택

기본 구성:
- Name: library-alb
- Scheme: Internet-facing
- IP address type: IPv4

네트워크 매핑:
- VPC: library-vpc (또는 기본 VPC)
- Mappings: ap-northeast-2a, ap-northeast-2c
  - Public Subnets 선택

보안 그룹:
- library-alb-sg

리스너 및 라우팅:
- Protocol: HTTP
- Port: 80
- Default action: Forward to target group (다음 단계에서 생성)
```

#### Target Group 2개 생성 (Blue/Green 배포용):

**Blue Target Group:**
```plaintext
- Target type: IP
- Name: library-blue-tg
- Protocol: HTTP
- Port: 8081
- VPC: library-vpc
- Health check path: /actuator/health
- Health check interval: 30 seconds
- Healthy threshold: 2
- Unhealthy threshold: 3
```

**Green Target Group:**
```plaintext
- Name: library-green-tg
- 나머지 설정은 Blue와 동일
```

**예상 소요 시간**: 30분

---

### 🟢 다음 단계 작업 10: ECS Service 생성

**목적**: ECS Cluster에서 Task를 실행하는 서비스 생성

#### ECS Service 생성:

```plaintext
1. ECS → Clusters → library-management-cluster
2. Services → Create

컴퓨팅 구성:
- Capacity provider: FARGATE
- Platform version: LATEST

배포 구성:
- Application type: Service
- Task definition: library-management-task:1
- Service name: library-service
- Desired tasks: 1 (초기 테스트용)

네트워킹:
- VPC: library-vpc
- Subnets: Public Subnets 선택 (또는 Private + NAT Gateway)
- Security group: library-ecs-task-sg
- Public IP: ENABLED (Public Subnet 사용 시)

로드 밸런싱:
- Load balancer type: Application Load Balancer
- Load balancer: library-alb
- Listener: HTTP:80
- Target group: library-blue-tg
- Health check grace period: 60 seconds
```

**⚠️ 주의**: 
- 첫 배포 전에는 ECS Service가 제대로 시작되지 않을 수 있음
- ECR에 이미지가 없기 때문
- GitHub Actions로 첫 배포 후 정상 작동

**예상 소요 시간**: 20분

**서비스 생성 후 할 일**:
```bash
# GitHub Secret에 ECS_SERVICE 추가
# GitHub → Settings → Secrets → New repository secret
Name: ECS_SERVICE
Value: library-service
```

---

## 📋 전체 작업 체크리스트

### 🔴 즉시 진행 (Phase 3 완료)
- [ ] **작업 1**: Git 커밋 및 푸시 (5분)
- [ ] **작업 2**: ECS Task Definition 작성 (15분)
- [ ] **작업 3**: GitHub Actions 워크플로우 작성 (20분)
- [ ] **작업 4**: AWS Systems Manager Parameter Store 설정 (10분)
- [ ] **작업 5**: CloudWatch Logs 그룹 생성 (5분)
- [ ] **작업 6**: IAM Role 생성 (15분)

### 🟡 중요 작업 (Phase 3 완료)
- [ ] **작업 7**: VPC 및 네트워크 설정 (30분 또는 5분)
- [ ] **작업 8**: Security Groups 생성 (20분)
- [ ] **작업 9**: Application Load Balancer 생성 (30분)
- [ ] **작업 10**: ECS Service 생성 (20분)

### 🟢 이후 작업 (Phase 4)
- [ ] **첫 번째 배포 테스트**
  - GitHub에 코드 푸시 → GitHub Actions 자동 실행
  - ECR에 이미지 푸시
  - ECS Service 업데이트
  - ALB DNS로 접속 테스트
- [ ] **CodeDeploy 설정** (Blue/Green 배포)
- [ ] **모니터링 설정** (CloudWatch Alarms)

---

## 💡 작업 순서 권장 사항

### 시나리오 1: 빠른 검증 (최소 구성)
```
1. Git 커밋
2. ECS Task Definition 작성
3. GitHub Actions 워크플로우 작성
4. Parameter Store 설정
5. CloudWatch Logs 생성
6. IAM Role 생성
7. 기본 VPC 사용 (새로 생성하지 않음)
8. Security Groups 생성
9. ALB + Target Groups 생성
10. ECS Service 생성
11. 첫 배포 테스트

예상 소요 시간: 2-3시간
```

### 시나리오 2: 제대로 된 구성 (권장)
```
1-6번 동일
7. 새 VPC 생성 (Public + Private Subnets)
8-11번 동일

예상 소요 시간: 3-4시간
```

---

## 🚨 주의사항 및 팁

### Git 커밋 전 확인사항
```bash
# 민감한 정보가 포함되지 않았는지 확인
git diff

# .gitignore에 제외되어야 할 파일들:
# - .env
# - aws-credentials.txt
# - *.pem
# - *.key
```

### AWS 리소스 생성 시 주의사항
```
⚠️ 비용 발생 가능:
- ECS Fargate: vCPU, 메모리 사용량에 따라 과금
- ALB: 시간당 과금 + 데이터 처리 비용
- NAT Gateway (Private Subnet 사용 시): 시간당 과금

💡 비용 절감 팁:
- ECS Service Desired tasks: 1로 시작
- Fargate Spot 사용 고려 (70% 할인)
- 사용하지 않을 때는 Desired tasks를 0으로 설정
```

### 트러블슈팅 준비
```
📝 각 단계에서 기록해야 할 정보:
- ECR Repository URI
- VPC ID, Subnet IDs
- Security Group IDs
- ALB DNS Name
- Target Group ARNs
- ECS Service ARN
```

---

## 🎯 다음 문서

작업 완료 후:
- **05-DEPLOYMENT-TEST-GUIDE.md** - 배포 테스트 가이드
- **06-BLUE-GREEN-DEPLOYMENT-GUIDE.md** - Blue/Green 배포 설정
- **07-MONITORING-SETUP-GUIDE.md** - 모니터링 및 알람 설정

---

## 📞 도움이 필요한 경우

### Claude에게 다시 요청하기

**세션이 끊긴 경우:**
```
현재 library-management-system 프로젝트의 
Phase 3 작업을 진행 중이야.

docs/deployment/cicd/00-MASTER-PLAN.md와 
docs/deployment/cicd/04-NEXT-STEPS-GUIDE.md를 확인하고,
[마지막 완료한 작업]부터 이어서 진행해줘.
```

**특정 작업에서 막힌 경우:**
```
[작업 N] 진행 중 [문제 설명]가 발생했어.
[에러 메시지 또는 상황 설명]

어떻게 해결하면 될까?
```

---

**생성일**: 2025-10-26  
**최종 수정일**: 2025-10-26  
**버전**: 2.0.0  
**상태**: Phase 3 부분 완료, 다음 10개 작업 명시
