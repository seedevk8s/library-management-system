# 📋 AWS 리소스 생성 체크리스트

> **이 문서는 AWS 인프라 구축 시 단계별로 체크하면서 진행하는 실무 가이드입니다.**

---

## 🎯 전체 진행 상황

| Phase | 상태 | 시작일 | 완료일 |
|-------|------|--------|--------|
| Phase 1: IAM 설정 | ⬜ 시작 전 | | |
| Phase 2: ECR 설정 | ⬜ 시작 전 | | |
| Phase 3: 네트워크 설정 | ⬜ 시작 전 | | |
| Phase 4: ALB 설정 | ⬜ 시작 전 | | |
| Phase 5: ECS 설정 | ⬜ 시작 전 | | |
| Phase 6: CodeDeploy 설정 | ⬜ 시작 전 | | |
| Phase 7: 모니터링 설정 | ⬜ 시작 전 | | |

**상태 표시:**
- ⬜ 시작 전
- 🟡 진행 중
- ✅ 완료
- ❌ 실패 (재시도 필요)

---

## Phase 1: IAM 설정 ⬜

### 1.1 GitHub Actions용 IAM 사용자 생성

**생성 경로:**
AWS Console → IAM → Users → Create user

**체크리스트:**
- [ ] 사용자 이름: `github-actions-deploy`
- [ ] Access type: Programmatic access
- [ ] 권한 설정:
  - [ ] `AmazonEC2ContainerRegistryPowerUser`
  - [ ] `AmazonECS_FullAccess`
  - [ ] `AmazonEC2FullAccess`
  - [ ] `ElasticLoadBalancingFullAccess`
  - [ ] `AWSCodeDeployFullAccess`
  - [ ] `CloudWatchLogsFullAccess`
- [ ] Access Key 생성
- [ ] **중요**: Access Key ID와 Secret Access Key 안전하게 저장

**저장할 정보:**
```
AWS_ACCESS_KEY_ID: [기록]
AWS_SECRET_ACCESS_KEY: [기록]
```

### 1.2 ECS Task Execution Role 생성

**생성 경로:**
AWS Console → IAM → Roles → Create role

**체크리스트:**
- [ ] Trusted entity type: AWS service
- [ ] Use case: Elastic Container Service → Elastic Container Service Task
- [ ] 권한: `AmazonECSTaskExecutionRolePolicy`
- [ ] Role 이름: `ecsTaskExecutionRole`
- [ ] 생성 완료

**저장할 정보:**
```
Role ARN: [기록]
```

### 1.3 ECS Task Role 생성 (애플리케이션용)

**체크리스트:**
- [ ] Trusted entity type: AWS service
- [ ] Use case: Elastic Container Service → Elastic Container Service Task
- [ ] 권한:
  - [ ] `CloudWatchLogsFullAccess`
  - [ ] S3 접근 필요 시 추가 정책
- [ ] Role 이름: `ecsTaskRole`
- [ ] 생성 완료

**저장할 정보:**
```
Role ARN: [기록]
```

### 1.4 CodeDeploy Service Role 생성

**체크리스트:**
- [ ] Trusted entity type: AWS service
- [ ] Use case: CodeDeploy → CodeDeploy - ECS
- [ ] 권한: `AWSCodeDeployRoleForECS`
- [ ] Role 이름: `ecsCodeDeployRole`
- [ ] 생성 완료

**저장할 정보:**
```
Role ARN: [기록]
```

---

## Phase 2: ECR 설정 ⬜

### 2.1 ECR 리포지토리 생성

**생성 경로:**
AWS Console → ECR → Repositories → Create repository

**체크리스트:**
- [ ] Visibility: Private
- [ ] Repository name: `library-management-system`
- [ ] Tag immutability: Disabled (개발 환경)
- [ ] Scan on push: Enabled (보안 스캔)
- [ ] Encryption: AES-256 (기본값)
- [ ] 생성 완료

**저장할 정보:**
```
ECR URI: [기록]
예시: 123456789012.dkr.ecr.ap-northeast-2.amazonaws.com/library-management-system
```

### 2.2 Lifecycle Policy 설정 (선택)

**비용 절감을 위한 설정:**

**체크리스트:**
- [ ] Repository 선택
- [ ] Lifecycle policies → Create rule
- [ ] Rule priority: 1
- [ ] Rule description: "Keep only last 3 images"
- [ ] Image status: Any
- [ ] Count type: Image count more than
- [ ] Count: 3
- [ ] 저장

---

## Phase 3: 네트워크 설정 ⬜

### 3.1 VPC 확인/생성

**기본 VPC 사용 (권장):**
- [ ] VPC 콘솔에서 기본 VPC 확인
- [ ] 기본 VPC ID 기록

**저장할 정보:**
```
VPC ID: [기록]
```

**신규 VPC 생성 (선택):**
- [ ] Name: `library-vpc`
- [ ] IPv4 CIDR: 10.0.0.0/16
- [ ] Public Subnets: 2개 (다른 AZ)
- [ ] Internet Gateway 연결

### 3.2 Subnets 확인

**체크리스트:**
- [ ] Public Subnet 1: `ap-northeast-2a`
- [ ] Public Subnet 2: `ap-northeast-2c`
- [ ] 각 서브넷이 Internet Gateway와 연결되어 있는지 확인

**저장할 정보:**
```
Subnet 1 ID: [기록]
Subnet 2 ID: [기록]
```

### 3.3 Security Group - ALB

**생성 경로:**
EC2 → Security Groups → Create security group

**체크리스트:**
- [ ] Name: `library-alb-sg`
- [ ] Description: "Security group for Library Management System ALB"
- [ ] VPC: 위에서 선택한 VPC
- [ ] Inbound rules:
  - [ ] Type: HTTP, Port: 80, Source: 0.0.0.0/0
  - [ ] Type: HTTPS, Port: 443, Source: 0.0.0.0/0 (나중에)
- [ ] Outbound rules:
  - [ ] Type: All traffic, Destination: 0.0.0.0/0
- [ ] 생성 완료

**저장할 정보:**
```
ALB Security Group ID: [기록]
```

### 3.4 Security Group - ECS

**체크리스트:**
- [ ] Name: `library-ecs-sg`
- [ ] Description: "Security group for Library Management System ECS"
- [ ] VPC: 위에서 선택한 VPC
- [ ] Inbound rules:
  - [ ] Type: Custom TCP, Port: 8081, Source: [ALB Security Group ID]
- [ ] Outbound rules:
  - [ ] Type: All traffic, Destination: 0.0.0.0/0
- [ ] 생성 완료

**저장할 정보:**
```
ECS Security Group ID: [기록]
```

---

## Phase 4: Application Load Balancer 설정 ⬜

### 4.1 Target Group - Blue 생성

**생성 경로:**
EC2 → Target Groups → Create target group

**체크리스트:**
- [ ] Target type: IP addresses
- [ ] Target group name: `library-tg-blue`
- [ ] Protocol: HTTP
- [ ] Port: 8081
- [ ] VPC: 위에서 선택한 VPC
- [ ] Protocol version: HTTP1
- [ ] Health check:
  - [ ] Path: `/actuator/health`
  - [ ] Port: Traffic port
  - [ ] Healthy threshold: 2
  - [ ] Unhealthy threshold: 2
  - [ ] Timeout: 5
  - [ ] Interval: 30
  - [ ] Success codes: 200
- [ ] 생성 완료 (타겟은 나중에 자동 등록)

**저장할 정보:**
```
Blue Target Group ARN: [기록]
```

### 4.2 Target Group - Green 생성

**체크리스트:**
- [ ] Target type: IP addresses
- [ ] Target group name: `library-tg-green`
- [ ] Protocol: HTTP
- [ ] Port: 8081
- [ ] VPC: 위에서 선택한 VPC
- [ ] Protocol version: HTTP1
- [ ] Health check: (Blue와 동일 설정)
  - [ ] Path: `/actuator/health`
  - [ ] Port: Traffic port
  - [ ] Healthy threshold: 2
  - [ ] Unhealthy threshold: 2
  - [ ] Timeout: 5
  - [ ] Interval: 30
  - [ ] Success codes: 200
- [ ] 생성 완료

**저장할 정보:**
```
Green Target Group ARN: [기록]
```

### 4.3 Application Load Balancer 생성

**생성 경로:**
EC2 → Load Balancers → Create Load Balancer → Application Load Balancer

**체크리스트:**
- [ ] Name: `library-alb`
- [ ] Scheme: Internet-facing
- [ ] IP address type: IPv4
- [ ] Network mapping:
  - [ ] VPC: 위에서 선택한 VPC
  - [ ] Mappings: 2개 이상의 AZ 선택
    - [ ] ap-northeast-2a + Public Subnet
    - [ ] ap-northeast-2c + Public Subnet
- [ ] Security groups:
  - [ ] `library-alb-sg` 선택
- [ ] Listeners and routing:
  - [ ] Protocol: HTTP
  - [ ] Port: 80
  - [ ] Default action: Forward to `library-tg-blue`
- [ ] 생성 완료 (생성에 2-3분 소요)

**저장할 정보:**
```
ALB ARN: [기록]
ALB DNS name: [기록]
예시: library-alb-123456789.ap-northeast-2.elb.amazonaws.com
```

### 4.4 ALB 상태 확인

**체크리스트:**
- [ ] ALB State: Active
- [ ] DNS name으로 접속 시도 (503 에러 정상 - 아직 타겟 없음)

---

## Phase 5: ECS 설정 ⬜

### 5.1 ECS 클러스터 생성

**생성 경로:**
ECS → Clusters → Create cluster

**체크리스트:**
- [ ] Cluster name: `library-cluster`
- [ ] Infrastructure:
  - [ ] Amazon EC2 instances 선택
  - [ ] Provisioning model: On-Demand
  - [ ] Operating system: Amazon Linux 2
  - [ ] EC2 instance type: `t3.micro`
  - [ ] Desired capacity: 1
  - [ ] Minimum: 1
  - [ ] Maximum: 1
- [ ] Networking:
  - [ ] VPC: 위에서 선택한 VPC
  - [ ] Subnets: 위에서 선택한 Public Subnets
  - [ ] Security group: `library-ecs-sg`
  - [ ] Auto-assign public IP: Enabled
- [ ] Monitoring: (기본값)
- [ ] Tags: (선택)
  - [ ] Key: `Environment`, Value: `dev`
  - [ ] Key: `Project`, Value: `library-management-system`
- [ ] 생성 완료 (EC2 인스턴스 생성에 5분 소요)

**저장할 정보:**
```
Cluster ARN: [기록]
```

### 5.2 클러스터 인스턴스 확인

**체크리스트:**
- [ ] Cluster 선택 → Infrastructure 탭
- [ ] Container instances: 1개 등록 확인
- [ ] Status: ACTIVE
- [ ] Agent connected: True

### 5.3 Task Definition 생성

**생성 경로:**
ECS → Task Definitions → Create new task definition

**체크리스트:**
- [ ] Task definition family: `library-management-system`
- [ ] Launch type: EC2
- [ ] Task role: `ecsTaskRole`
- [ ] Task execution role: `ecsTaskExecutionRole`
- [ ] Task size:
  - [ ] CPU: 256 (.25 vCPU)
  - [ ] Memory: 512 (0.5 GB)
- [ ] Container definitions:
  - [ ] Container name: `library-app`
  - [ ] Image: `[ECR URI]:latest`
  - [ ] Memory limits:
    - [ ] Soft limit: 512
  - [ ] Port mappings:
    - [ ] Container port: 8081
    - [ ] Protocol: tcp
  - [ ] Environment variables:
    - [ ] `SPRING_PROFILES_ACTIVE`: `prod`
    - [ ] `SPRING_DATASOURCE_URL`: `jdbc:mysql://host.docker.internal:3306/librarydb?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8`
    - [ ] `SPRING_DATASOURCE_USERNAME`: `root`
    - [ ] `SPRING_DATASOURCE_PASSWORD`: `12345`
  - [ ] Log configuration:
    - [ ] Log driver: awslogs
    - [ ] Log options:
      - [ ] `awslogs-group`: `/ecs/library-management-system`
      - [ ] `awslogs-region`: `ap-northeast-2`
      - [ ] `awslogs-stream-prefix`: `ecs`
      - [ ] `awslogs-create-group`: `true`
- [ ] 생성 완료

**저장할 정보:**
```
Task Definition ARN: [기록]
Container Name: library-app
```

### 5.4 CloudWatch Log Group 생성 (자동 또는 수동)

**수동 생성 (선택):**
- [ ] CloudWatch → Logs → Log groups → Create log group
- [ ] Name: `/ecs/library-management-system`
- [ ] Retention: 7 days
- [ ] 생성 완료

### 5.5 ECS 서비스 생성

**⚠️ 중요: 첫 번째는 수동으로 생성, 이후 CodeDeploy가 관리**

**생성 경로:**
ECS → Clusters → library-cluster → Services → Create

**체크리스트:**
- [ ] Launch type: EC2
- [ ] Task Definition: `library-management-system:1`
- [ ] Service name: `library-service`
- [ ] Service type: REPLICA
- [ ] Desired tasks: 1
- [ ] Deployment options:
  - [ ] Deployment type: Blue/green deployment (powered by AWS CodeDeploy)
  - [ ] Deployment configuration: CodeDeployDefault.ECSAllAtOnce
  - [ ] Service role for CodeDeploy: `ecsCodeDeployRole`
- [ ] Networking:
  - [ ] VPC: 위에서 선택한 VPC
  - [ ] Subnets: Public Subnets 선택
  - [ ] Security groups: `library-ecs-sg`
  - [ ] Public IP: ENABLED
- [ ] Load balancing:
  - [ ] Load balancer type: Application Load Balancer
  - [ ] Load balancer: `library-alb`
  - [ ] Listener: 80:HTTP
  - [ ] Target group 1 name: `library-tg-blue`
  - [ ] Target group 2 name: `library-tg-green`
  - [ ] Production listener port: 80
  - [ ] Test listener port: 8080 (선택)
  - [ ] Health check grace period: 60 seconds
- [ ] Service auto scaling: (나중에)
- [ ] 생성 완료 (서비스 생성에 5분 소요)

**저장할 정보:**
```
Service ARN: [기록]
```

### 5.6 서비스 배포 확인

**체크리스트:**
- [ ] Service 선택 → Deployments 탭
- [ ] Status: PRIMARY 배포 완료 확인
- [ ] Desired tasks: 1
- [ ] Running tasks: 1
- [ ] Tasks 탭에서 Task 상태: RUNNING
- [ ] Target Groups에서 Health status: healthy

### 5.7 애플리케이션 접속 테스트

**테스트:**
```
http://[ALB DNS name]
```

**체크리스트:**
- [ ] 로그인 페이지 로드
- [ ] 로그인 기능 테스트
- [ ] 게시판 접근 테스트

---

## Phase 6: CodeDeploy 설정 ⬜

### 6.1 CodeDeploy 애플리케이션 확인

**⚠️ ECS 서비스 생성 시 자동 생성됨**

**확인 경로:**
CodeDeploy → Applications

**체크리스트:**
- [ ] Application name: `AppECS-library-cluster-library-service`
- [ ] Compute platform: Amazon ECS
- [ ] 생성 확인

**저장할 정보:**
```
Application name: [기록]
```

### 6.2 CodeDeploy 배포 그룹 확인

**확인 경로:**
CodeDeploy → Applications → [Application] → Deployment groups

**체크리스트:**
- [ ] Deployment group name: `DgpECS-library-cluster-library-service`
- [ ] Service role: `ecsCodeDeployRole`
- [ ] ECS cluster: `library-cluster`
- [ ] ECS service: `library-service`
- [ ] Load balancer: `library-alb`
- [ ] Target groups: Blue & Green
- [ ] 확인 완료

**저장할 정보:**
```
Deployment group name: [기록]
```

### 6.3 AppSpec 파일 준비

**로컬 프로젝트에 파일 생성:**
- [ ] `aws/appspec.yml` 파일 존재 확인
- [ ] Task Definition ARN 업데이트
- [ ] Container name 확인
- [ ] Port 8081 확인

---

## Phase 7: 모니터링 설정 ⬜

### 7.1 CloudWatch Logs 확인

**체크리스트:**
- [ ] Log group: `/ecs/library-management-system` 존재 확인
- [ ] Log streams 생성 확인
- [ ] 로그 조회 테스트
- [ ] Retention: 7 days 설정

### 7.2 CloudWatch Alarms 생성

#### Alarm 1: CPU Utilization

**체크리스트:**
- [ ] Metric: ECS > ClusterName > CPUUtilization
- [ ] Cluster: library-cluster
- [ ] Statistic: Average
- [ ] Period: 5 minutes
- [ ] Threshold: > 80%
- [ ] Actions: SNS Topic (나중에 생성)
- [ ] Alarm name: `library-cpu-high`

#### Alarm 2: Memory Utilization

**체크리스트:**
- [ ] Metric: ECS > ClusterName > MemoryUtilization
- [ ] Cluster: library-cluster
- [ ] Statistic: Average
- [ ] Period: 5 minutes
- [ ] Threshold: > 80%
- [ ] Alarm name: `library-memory-high`

#### Alarm 3: Unhealthy Hosts

**체크리스트:**
- [ ] Metric: ALB > TargetGroup > UnHealthyHostCount
- [ ] Target Group: library-tg-blue
- [ ] Statistic: Maximum
- [ ] Period: 1 minute
- [ ] Threshold: > 0
- [ ] Alarm name: `library-unhealthy-hosts`

### 7.3 SNS Topic 생성 (선택)

**체크리스트:**
- [ ] Name: `library-alerts`
- [ ] Display name: Library System Alerts
- [ ] Subscription:
  - [ ] Protocol: Email
  - [ ] Endpoint: [이메일 주소]
- [ ] 이메일 구독 확인
- [ ] 생성 완료

---

## 💰 비용 확인 ⬜

### AWS Cost Explorer 설정

**체크리스트:**
- [ ] Cost Explorer 활성화
- [ ] 일일 비용 확인
- [ ] 서비스별 비용 분석

### AWS Budgets 설정

**체크리스트:**
- [ ] Budget name: `library-system-budget`
- [ ] Period: Monthly
- [ ] Budget amount: $20
- [ ] Alerts:
  - [ ] 50% threshold: Email
  - [ ] 80% threshold: Email
  - [ ] 100% threshold: Email + SMS

---

## 🎯 최종 검증 체크리스트

### 인프라 검증

- [ ] ECR에 이미지 푸시 가능
- [ ] ECS 클러스터 활성 상태
- [ ] ECS 서비스 RUNNING 상태
- [ ] Task RUNNING 상태
- [ ] Target Group healthy 상태
- [ ] ALB DNS로 접속 가능
- [ ] 로그인 기능 작동
- [ ] 게시판 접근 가능
- [ ] CloudWatch 로그 수집 중

### Blue-Green 배포 검증

- [ ] 새 이미지 빌드 및 ECR 푸시
- [ ] CodeDeploy 배포 트리거
- [ ] Green 환경에 새 Task 배포
- [ ] Green Health Check 통과
- [ ] Traffic Shift (Blue → Green)
- [ ] Green 환경 정상 작동 확인
- [ ] Blue 환경 종료

### 모니터링 검증

- [ ] CloudWatch Logs 실시간 수집
- [ ] CloudWatch Alarms 정상 작동
- [ ] SNS 알림 수신 테스트

---

## 📝 저장한 정보 요약

### IAM
```
GitHub Actions User:
- Access Key ID: 
- Secret Access Key: 

Task Execution Role ARN: 
Task Role ARN: 
CodeDeploy Role ARN: 
```

### ECR
```
Repository URI: 
```

### Network
```
VPC ID: 
Subnet 1 ID: 
Subnet 2 ID: 
ALB Security Group ID: 
ECS Security Group ID: 
```

### ALB
```
ALB ARN: 
ALB DNS: 
Blue Target Group ARN: 
Green Target Group ARN: 
```

### ECS
```
Cluster ARN: 
Task Definition ARN: 
Service ARN: 
Container Name: library-app
```

### CodeDeploy
```
Application Name: 
Deployment Group Name: 
```

---

## 🔄 다음 단계

- [ ] GitHub Secrets 설정
- [ ] GitHub Actions 워크플로우 완성
- [ ] 첫 자동 배포 테스트
- [ ] Blue-Green 배포 테스트
- [ ] 문서화 완료

---

**✅ 모든 체크리스트 완료 시 Phase 3 완료!**
