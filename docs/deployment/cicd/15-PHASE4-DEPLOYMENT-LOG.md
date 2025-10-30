# 📋 Phase 4 배포 작업 로그

> **작업일**: 2025-10-29  
> **작업 시간**: 18:00 ~ 23:36 (약 5시간 36분)  
> **작업자**: Hojin + Claude

---

## 🎯 작업 목표

Phase 4 배포 자동화 구축:
- ECS Task Definition 생성
- ECS Service 생성 및 배포
- IAM 권한 설정
- Docker 이미지 ECR 푸시
- 첫 배포 테스트

---

## ✅ 완료된 작업 상세

### 1. ECS Task Definition 생성 (18:05)

**파일 위치**: 프로젝트 루트/task-definition.json

**생성 방법**: AWS Console → ECS → Task Definitions → JSON 직접 입력

**설정 내용**:
```json
{
  "family": "library-task",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "executionRoleArn": "arn:aws:iam::011587325937:role/ecsTaskExecutionRole",
  "containerDefinitions": [
    {
      "name": "library-app",
      "image": "011587325937.dkr.ecr.ap-northeast-2.amazonaws.com/library-management-system:latest",
      "portMappings": [
        {
          "containerPort": 8081,
          "protocol": "tcp"
        }
      ],
      "essential": true,
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        },
        {
          "name": "SPRING_JPA_HIBERNATE_DDL_AUTO",
          "value": "update"
        }
      ],
      "secrets": [
        {
          "name": "SPRING_DATASOURCE_URL",
          "valueFrom": "arn:aws:ssm:ap-northeast-2:011587325937:parameter/library/db/url"
        },
        {
          "name": "SPRING_DATASOURCE_USERNAME",
          "valueFrom": "arn:aws:ssm:ap-northeast-2:011587325937:parameter/library/db/username"
        },
        {
          "name": "SPRING_DATASOURCE_PASSWORD",
          "valueFrom": "arn:aws:ssm:ap-northeast-2:011587325937:parameter/library/db/password"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/library-management-task",
          "awslogs-region": "ap-northeast-2",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "healthCheck": {
        "command": [
          "CMD-SHELL",
          "curl -f http://localhost:8081/actuator/health || exit 1"
        ],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      }
    }
  ]
}
```

**결과**:
- Task Definition: library-task:2 (ACTIVE)
- 생성일시: 2025-10-29 18:05

---

### 2. ECS Service 생성 (18:15)

**생성 방법**: AWS Console → ECS → Clusters → library-management-cluster → Create Service

**설정 내용**:

**기본 설정**:
```
Service name: library-service
Cluster: library-management-cluster
Task Definition: library-task:2
Desired tasks: 1
```

**Compute Configuration**:
```
Launch type: FARGATE
Platform version: LATEST
```

**네트워크 설정**:
```
VPC: vpc-07dd414387be45a0f
Subnets:
  - ap-northeast-2a (subnet-0eb8d0b96a8e96f23)
  - ap-northeast-2c (subnet-08580256bc5bd0ab1)
Security Group: library-ecs-task-sg
Public IP: ENABLED
```

**Load Balancing**:
```
Type: Application Load Balancer
Load Balancer: library-alb (기존)
Listener: 80:HTTP (기존)
Target Group: library-blue-tg (기존)
Health Check Grace Period: 60초
```

**결과**:
- Service: library-service (활성)
- 생성일시: 2025-10-29 18:15

---

### 3. ecsTaskExecutionRole 생성 및 권한 설정 (18:54 ~ 22:48)

#### 3.1 문제 발견
```
AccessDeniedException: User arn:aws:sts::011587325937:assumed-role/ecsTaskExecutionRole 
is not authorized to perform: ssm:GetParameters
```

**원인**: ecsTaskExecutionRole이 존재하지 않음

---

#### 3.2 ecsTaskExecutionRole 생성 (22:48)

**생성 방법**: IAM → 역할 → 역할 생성

**1단계: 신뢰할 수 있는 엔터티**:
```
엔터티 유형: AWS 서비스
서비스: Elastic Container Service
사용 사례: Elastic Container Service Task
```

**신뢰 정책**:
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllowAccessToECSForTaskExecution",
      "Effect": "Allow",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
```

**2단계: 권한 정책**:
```
- AmazonECSTaskExecutionRolePolicy (AWS 관리형)
```

**역할 이름**:
```
ecsTaskExecutionRole
```

**결과**: 역할 생성 완료 (22:48)

---

#### 3.3 추가 권한 설정 - SSM ReadOnly (22:50)

**추가 정책**:
```
- AmazonSSMReadOnlyAccess (AWS 관리형)
```

**방법**: 역할 → 권한 추가 → 정책 연결

---

#### 3.4 인라인 정책 추가 - KMS & SSM (23:01)

**문제**: Parameter Store의 SecureString 복호화 권한 부족

**인라인 정책**: ECSTaskParameterStoreAccess

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllowKMSDecrypt",
      "Effect": "Allow",
      "Action": [
        "kms:Decrypt",
        "kms:DescribeKey"
      ],
      "Resource": "*"
    },
    {
      "Sid": "AllowSSMParameters",
      "Effect": "Allow",
      "Action": [
        "ssm:GetParameters",
        "ssm:GetParameter"
      ],
      "Resource": [
        "arn:aws:ssm:ap-northeast-2:011587325937:parameter/library/*"
      ]
    }
  ]
}
```

**최종 권한 구성**:
```
1. AmazonECSTaskExecutionRolePolicy (AWS 관리형)
2. AmazonSSMReadOnlyAccess (AWS 관리형)
3. ECSTaskParameterStoreAccess (고객 인라인)
```

**결과**: 모든 권한 설정 완료 (23:01)

---

### 4. Docker 이미지 빌드 및 ECR 푸시 (23:20 ~ 23:29)

#### 4.1 문제 발견
```
CannotPullContainerError: 
failed to resolve ref 011587325937.dkr.ecr.ap-northeast-2.amazonaws.com/library-management-system:latest
not found
```

**원인**: ECR Repository에 Docker 이미지가 없음

---

#### 4.2 Docker 이미지 푸시

**작업 위치**:
```
C:\Users\USER\Documents\choongang\Project\lecture\trainer\library-management-system (27)
```

**실행 명령어**:

```bash
# 1. ECR 로그인
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 011587325937.dkr.ecr.ap-northeast-2.amazonaws.com

# 2. 이미지 빌드
docker build -t library-management-system .

# 3. 이미지 태그
docker tag library-management-system:latest 011587325937.dkr.ecr.ap-northeast-2.amazonaws.com/library-management-system:latest

# 4. ECR 푸시
docker push 011587325937.dkr.ecr.ap-northeast-2.amazonaws.com/library-management-system:latest
```

**푸시 결과**:
```
latest: digest: sha256:f26cfb5b7843fcbb9de3a0ef01e273c75c58cd0a848bea771a8881c45188f150
size: 856
```

**완료 시간**: 23:29

---

### 5. ECS Task 실행 (23:31 ~ 현재)

#### 5.1 첫 번째 배포 시도 (실패)
```
Task 상태: STOPPED
에러: ecsTaskExecutionRole 권한 없음
→ 해결: 역할 생성 및 권한 추가
```

#### 5.2 두 번째 배포 시도 (실패)
```
Task 상태: STOPPED
에러: ECR 이미지 없음
→ 해결: Docker 이미지 빌드 및 푸시
```

#### 5.3 세 번째 배포 (진행 중)
```
Task ID: f4952b8c5c074300b47de4b82f2c3cdb
상태: 실행 중 (비정상)
퍼블릭 IP: 54.180.148.163
프라이빗 IP: 172.31.12.252

Task 정보:
- 시작 시간: 2025-10-29 23:32
- CPU: .25 vCPU
- Memory: .5 GB
- 플랫폼 버전: 1.4.0
```

**현재 문제**:
- 컨테이너는 실행 중
- Health Check 실패 (비정상 상태)
- 원인 확인 필요 (로그 확인 대기)

---

## 🔍 트러블슈팅 과정

### 문제 1: ecsTaskExecutionRole 없음
```
시간: 18:54
에러: ECS was unable to assume the role 'ecsTaskExecutionRole'
해결: IAM에서 역할 생성 (22:48)
소요: 약 4시간 (문제 파악 및 해결)
```

### 문제 2: Parameter Store 접근 권한 없음
```
시간: 22:50
에러: AccessDeniedException: not authorized to perform ssm:GetParameters
해결: AmazonSSMReadOnlyAccess 추가
소요: 약 10분
```

### 문제 3: KMS 복호화 권한 없음
```
시간: 22:55
문제: SecureString 파라미터 복호화 실패 (예상)
해결: 인라인 정책으로 KMS Decrypt 권한 추가
소요: 약 10분
```

### 문제 4: ECR 이미지 없음
```
시간: 23:19
에러: CannotPullContainerError - image not found
해결: Docker 빌드 및 ECR 푸시
소요: 약 10분
```

### 문제 5: Health Check 실패 (진행 중)
```
시간: 23:35
상태: 컨테이너 실행 중, Health Check 비정상
다음: 로그 확인 및 원인 파악
```

---

## 📊 생성된 리소스

### ECS Resources
```
Task Definition: library-task:2
ECS Service: library-service
Task: f4952b8c5c074300b47de4b82f2c3cdb (실행 중)
```

### IAM Resources
```
Role: ecsTaskExecutionRole
Policies:
  - AmazonECSTaskExecutionRolePolicy
  - AmazonSSMReadOnlyAccess
  - ECSTaskParameterStoreAccess (인라인)
```

### ECR Resources
```
Image: 011587325937.dkr.ecr.ap-northeast-2.amazonaws.com/library-management-system:latest
Digest: sha256:f26cfb5b7843fcbb9de3a0ef01e273c75c58cd0a848bea771a8881c45188f150
Size: 856
```

---

## 💰 비용 발생 현황

### 과금 진행 중
```
ALB: 2025-10-29 15:27부터 과금 (약 8시간)
  - 시간당: ~30원
  - 현재까지: 약 240원

RDS: 프리 티어 (무료)

ECS Fargate: 2025-10-29 23:32부터 과금 (약 4분)
  - 시간당: ~27원
  - 현재까지: 약 2원

현재 총 비용: 약 242원
```

---

## 🎯 다음 작업 (남은 40%)

### 즉시 해야 할 작업
```
1. ECS Task 로그 확인
2. Health Check 실패 원인 파악
3. 애플리케이션 정상 작동 확인
```

### Phase 4 완료를 위한 작업
```
4. ALB DNS로 접속 테스트
5. Target Group Health Check 통과 확인
6. GitHub Actions 워크플로우 작성
7. CI/CD 자동 배포 테스트
8. Blue-Green 배포 테스트
```

---

## 📝 교훈 및 인사이트

### 1. IAM 권한 설정의 중요성
```
ecsTaskExecutionRole은 필수:
- ECS Task 실행을 위한 기본 역할
- Parameter Store 접근 권한
- KMS 복호화 권한
- CloudWatch Logs 권한

미리 생성하지 않으면 Task 실행 실패
```

### 2. ECR 이미지 푸시 필수
```
Task Definition에서 ECR 이미지 참조:
- 실제 이미지가 ECR에 있어야 함
- 로컬 빌드만으로는 부족
- 반드시 ECR에 푸시 필요
```

### 3. Parameter Store + KMS
```
SecureString 파라미터 사용 시:
- SSM 읽기 권한 필요
- KMS 복호화 권한 추가 필요
- 두 권한 모두 있어야 작동
```

### 4. Health Check 설정
```
/actuator/health 엔드포인트:
- Spring Boot Actuator 필요
- 의존성 확인 필요
- 포트 및 경로 정확해야 함
```

### 5. 네트워크 설정
```
Public IP ENABLED:
- 인터넷 게이트웨이 통해 외부 접근
- ECR 이미지 다운로드 가능
- RDS 접근 가능 (같은 VPC)
```

---

## 🔄 세션 복구용 요약

**다음 Claude 세션에서 사용할 프롬프트**:

```
library-management-system Phase 4 진행 중.

15-PHASE4-DEPLOYMENT-LOG.md 읽고 상황 파악 후,
ECS Task 로그 확인부터 계속해줘.

현재 상황:
- Task 실행 중 (비정상 상태)
- Health Check 실패
- 로그 확인 필요
```

---

**작성일**: 2025-10-29 23:40  
**버전**: 1.0.0  
**작성자**: Hojin + Claude  
**문서 유형**: 작업 로그
