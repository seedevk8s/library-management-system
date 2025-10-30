# ECS Service와 Target Group 연결 메커니즘

## 개요
이 문서는 **대상 그룹(Target Group)에 ECS Task들을 연결하는 설정**이 어디서, 어떻게 이루어지는지 상세히 설명합니다.

## 핵심 개념

### 연결 설정 위치
**ECS Service 생성 시** Target Group을 연결합니다.

```
Target Group 생성 (Phase 3)
    ↓
빈 Target Group 존재
    ↓
ECS Service 생성 (Phase 4)
    ↓
Load Balancing 설정에서 Target Group 선택
    ↓
ECS Service가 Task들을 자동으로 등록/해제
```

### 중요한 구분

| 단계 | 작업 | 수행 주체 | 시기 |
|------|------|----------|------|
| Phase 3 | Target Group 생성 | 사용자 (수동) | 인프라 구축 시 |
| Phase 4 | Service에 TG 연결 | 사용자 (수동) | Service 생성 시 |
| Phase 4 | Task IP 등록/해제 | ECS Service (자동) | Task 생명주기 동안 |

## 1. ECS Service 생성 시 Target Group 연결

### 설정 경로

**AWS Console 경로**:
```
ECS → Clusters → library-management-cluster → Services → Create Service
```

### 설정 단계

#### Step 1: Basic Configuration
- **Task Definition**: 사용할 Task Definition 선택
- **Service Name**: 예) `library-service-blue`
- **Number of tasks**: 원하는 Task 개수 (예: 2)

#### Step 2: Networking
- **VPC**: vpc-07dd414387be45a0f
- **Subnets**: Private 서브넷 선택
- **Security Group**: library-ecs-task-sg

#### Step 3: Load Balancing (핵심 단계)

이 섹션에서 Target Group을 연결합니다:

1. **Load balancer type**: Application Load Balancer 선택
2. **Load balancer**: 기존 ALB 선택 (library-management-alb)
3. **Container to load balance**: 
   - Container name: app
   - Container port: 8081
4. **Target group**: 
   - **여기서 선택**: `library-blue-tg` 또는 `library-green-tg`

#### Step 4: Review and Create
- 설정 검토 후 Service 생성

### 생성 후 자동 동작

Service가 생성되면 ECS가 자동으로:
1. 지정된 수만큼 Task 시작
2. 각 Task의 Private IP를 Target Group에 등록
3. Health Check 시작
4. Healthy 상태가 되면 ALB가 트래픽 전달 시작

## 2. Task 자동 등록/해제 메커니즘

### Task 시작 시

```
ECS Service가 Task 시작
    ↓
Task에 Private IP 할당 (예: 10.0.1.100)
    ↓
ECS Service가 자동으로 Target Group에 등록
    ↓
Target Group: 10.0.1.100:8081 추가
    ↓
Health Check 시작
    ↓
Healthy 되면 트래픽 수신
```

### 자동 등록 상세

**ECS Service의 역할**:
- Task 시작 → 즉시 Target Group에 IP:Port 등록
- Task의 ENI Private IP 사용
- Health Check 통과 확인
- 등록 상태 지속 모니터링

**Target Group의 역할**:
- 등록된 IP:Port로 Health Check 수행
- Healthy 상태의 Target에만 트래픽 전달
- Unhealthy Target은 트래픽 제외

### Task 종료 시

```
Task 종료 (스케일 다운, 업데이트 등)
    ↓
ECS Service가 Target Group에서 자동 제거
    ↓
Connection Draining (기존 연결 처리 대기)
    ↓
완전히 제거
```

### Connection Draining

Task 제거 시 진행 중인 요청을 안전하게 처리:
- **Deregistration delay**: 300초 (기본값)
- 새 요청은 받지 않음
- 기존 연결은 완료될 때까지 유지
- 시간 초과 시 강제 종료

## 3. Blue-Green 배포 시나리오

### 초기 상태: Blue 환경 운영

```
ECS Service: library-service-blue
    ↓
Target Group: library-blue-tg
    ↓
Tasks: 2개 (10.0.1.100, 10.0.1.101)
    ↓
ALB 리스너: Blue TG로 트래픽 100%
```

**상태**:
- Blue TG: 2개 Task healthy, 트래픽 수신 중
- Green TG: 등록된 Target 없음, 대기 상태

### Step 1: Green 환경 배포

**새 ECS Service 생성**:
```
Service Name: library-service-green
Task Definition: library-app:2 (새 버전)
Target Group: library-green-tg
Desired Tasks: 2
```

**자동으로 발생**:
- ECS가 Green TG에 새 Task 2개 시작
- 각 Task IP 자동 등록 (10.0.2.100, 10.0.2.101)
- Health Check 시작
- Healthy 되면 준비 완료

**현재 상태**:
- Blue TG: 트래픽 수신 중 (v1.0)
- Green TG: 준비 완료, 대기 중 (v2.0)

### Step 2: 트래픽 전환

**ALB 리스너 규칙 변경** (수동):
```
ALB → Listeners → HTTP:80 → Rules
기존: Forward to library-blue-tg
변경: Forward to library-green-tg
```

**즉시 효과**:
- 모든 새 요청이 Green TG로 라우팅
- Blue TG는 새 요청 받지 않음
- 기존 연결은 Blue에서 계속 처리

### Step 3: 검증 및 정리

**검증**:
- Green 환경에서 정상 동작 확인
- 모니터링 지표 확인
- 사용자 피드백 수집

**Blue 환경 유지 또는 제거**:
- **유지**: 빠른 롤백을 위해 일정 시간 유지
- **제거**: 검증 완료 후 Blue Service 삭제
  - Task들이 자동으로 Blue TG에서 제거됨
  - Blue TG는 빈 상태로 남음 (다음 배포 대기)

### 롤백 시나리오

문제 발견 시 즉시 롤백:
```
ALB 리스너 규칙 변경
Forward to library-green-tg
    ↓
Forward to library-blue-tg (원복)
    ↓
즉시 Blue 환경으로 트래픽 복귀
```

**롤백 소요 시간**: 1-2초 이내

## 4. 수동 vs 자동 작업 구분

### 사용자가 수동으로 하는 작업

#### Phase 3: 인프라 구축
- ✅ Target Group 생성 (빈 상태)
- ✅ ALB 생성
- ✅ Security Group 생성
- ✅ ECS Cluster 생성

#### Phase 4: 배포
- ✅ ECS Service 생성
- ✅ Service에 Target Group 지정
- ✅ ALB 리스너 규칙 변경 (Blue-Green 전환)

### ECS Service가 자동으로 하는 작업

#### Task 생명주기 관리
- ⚙️ Task 시작/중지
- ⚙️ Task IP를 Target Group에 등록
- ⚙️ Task IP를 Target Group에서 제거
- ⚙️ Desired count 유지
- ⚙️ Health Check 기반 자동 복구

#### 스케일링
- ⚙️ Auto Scaling 시 Task 추가/제거
- ⚙️ 새 Task를 Target Group에 자동 등록
- ⚙️ 제거된 Task를 Target Group에서 자동 해제

## 5. 실제 설정 예시

### Blue Service 생성

```yaml
Service Configuration:
  name: library-service-blue
  cluster: library-management-cluster
  task-definition: library-app:1
  desired-count: 2
  
  network:
    vpc: vpc-07dd414387be45a0f
    subnets: [private-subnet-a, private-subnet-c]
    security-groups: [library-ecs-task-sg]
  
  load-balancer:
    type: application
    name: library-management-alb
    target-group: library-blue-tg
    container:
      name: app
      port: 8081
```

### Green Service 생성 (새 버전 배포)

```yaml
Service Configuration:
  name: library-service-green
  cluster: library-management-cluster
  task-definition: library-app:2  # 새 버전
  desired-count: 2
  
  network:
    vpc: vpc-07dd414387be45a0f
    subnets: [private-subnet-a, private-subnet-c]
    security-groups: [library-ecs-task-sg]
  
  load-balancer:
    type: application
    name: library-management-alb
    target-group: library-green-tg  # Green TG로 변경
    container:
      name: app
      port: 8081
```

## 6. 모니터링 및 확인

### ECS Console에서 확인

**Service 상태**:
```
ECS → Clusters → library-management-cluster → Services → library-service-blue
```
- Running tasks: 2 / 2
- Desired tasks: 2
- Health: Healthy

**Task 상태**:
```
Tasks 탭 → Task ID 클릭
```
- Status: RUNNING
- Health status: HEALTHY
- Private IP: 10.0.1.100
- Target Group: library-blue-tg

### EC2 Console에서 확인

**Target Group 상태**:
```
EC2 → Target Groups → library-blue-tg → Targets 탭
```

등록된 Targets:
| Target | Port | Status | Health |
|--------|------|--------|--------|
| 10.0.1.100 | 8081 | healthy | ✓ |
| 10.0.1.101 | 8081 | healthy | ✓ |

### CloudWatch Logs 확인

**Task 로그**:
```
CloudWatch → Log groups → /ecs/library-management
```
- Task 시작 로그
- 애플리케이션 로그
- Health Check 로그

## 7. 트러블슈팅

### Task가 Target Group에 등록되지 않는 경우

**원인 1: Security Group 미설정**
- ECS Task SG가 ALB SG로부터 8081 포트 허용 확인
- 해결: library-ecs-task-sg Inbound에 library-alb-sg:8081 추가

**원인 2: Health Check 실패**
- Target Group Health Check 경로 확인 (/)
- 애플리케이션이 8081 포트에서 정상 응답하는지 확인
- 해결: 애플리케이션 로그 확인, Health Check 경로 수정

**원인 3: 서브넷 설정 오류**
- ECS Task가 Private 서브넷에 배포되는지 확인
- Target Group의 VPC가 동일한지 확인
- 해결: Service 네트워크 설정 재확인

### Task가 Unhealthy 상태인 경우

**Health Check 로그 확인**:
```
Target Group → Health checks 탭
- Health check protocol: HTTP
- Health check path: /
- Success codes: 200
- Timeout: 5 seconds
- Interval: 30 seconds
- Healthy threshold: 2
- Unhealthy threshold: 2
```

**해결 방법**:
1. 애플리케이션이 Health Check 경로에 응답하는지 확인
2. Security Group 규칙 확인
3. Task 로그에서 에러 확인
4. Database 연결 문제 확인

## 다이어그램

전체 연결 메커니즘은 [19-ecs-service-targetgroup-connection.svg](./19-ecs-service-targetgroup-connection.svg) 파일을 참조하세요.

## 관련 문서

- [AWS 배포 구성 요소 관계도](./18-aws-components-relationship.md)
- [Phase 4 배포 로그](./15-PHASE4-DEPLOYMENT-LOG.md)
- [현재 진행 상황](./14-CURRENT-PROGRESS.md)

## 요약

### Phase 3에서 할 일
- ✅ Target Group 생성 (빈 상태로)
- ✅ ALB 생성 및 리스너 설정

### Phase 4에서 할 일
- ✅ ECS Service 생성 시 Target Group 선택
- ✅ Service가 자동으로 Task를 Target Group에 등록
- ✅ Blue-Green 전환 시 ALB 리스너 규칙 변경

### 자동으로 이루어지는 일
- ⚙️ Task IP 등록/해제
- ⚙️ Health Check
- ⚙️ 트래픽 라우팅
- ⚙️ Connection Draining
