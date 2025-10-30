# AWS 배포 구성 요소 관계도

## 개요
이 문서는 AWS 기반 배포에서 핵심이 되는 세 가지 구성 요소인 **보안 그룹(Security Groups)**, **로드밸런서(Load Balancer)**, **대상 그룹(Target Groups)**의 유기적 관계를 설명합니다.

## 주요 구성 요소

### 1. 보안 그룹 (Security Groups)

VPC 수준의 가상 방화벽으로, 인바운드/아웃바운드 트래픽을 제어합니다.

#### library-alb-sg (ALB 보안 그룹)
- **역할**: 인터넷에서 Application Load Balancer로의 트래픽 허용
- **인바운드 규칙**: 
  - HTTP (80) - 전체 허용 (0.0.0.0/0)
  - HTTPS (443) - 전체 허용 (0.0.0.0/0)
- **적용 대상**: Application Load Balancer

#### library-ecs-task-sg (ECS Task 보안 그룹)
- **역할**: ALB에서 ECS Task로의 트래픽만 허용
- **인바운드 규칙**: 
  - Port 8081 - library-alb-sg에서만 허용
- **적용 대상**: ECS Fargate Task

#### library-rds-sg (RDS 보안 그룹)
- **역할**: ECS Task에서 RDS로의 데이터베이스 연결만 허용
- **인바운드 규칙**: 
  - MySQL (3306) - library-ecs-task-sg에서만 허용
- **적용 대상**: RDS MySQL 인스턴스

### 2. 로드밸런서 (Application Load Balancer)

트래픽을 받아 대상 그룹으로 분산하는 Layer 7 로드밸런서입니다.

#### library-management-alb
- **리스너**: HTTP:80
- **보안 그룹**: library-alb-sg
- **기능**:
  - 인터넷에서 들어오는 HTTP 요청 수신
  - Blue/Green 대상 그룹으로 트래픽 라우팅
  - 헬스 체크를 통한 정상 타겟으로만 트래픽 전달

### 3. 대상 그룹 (Target Groups)

ALB가 트래픽을 전달할 타겟들의 그룹입니다.

#### library-blue-tg (Blue 대상 그룹)
- **타겟 타입**: IP (Fargate용)
- **프로토콜**: HTTP
- **포트**: 8081
- **용도**: 현재 운영 중인 버전

#### library-green-tg (Green 대상 그룹)
- **타겟 타입**: IP (Fargate용)
- **프로토콜**: HTTP
- **포트**: 8081
- **용도**: 새 버전 배포 및 테스트

## 트래픽 흐름

```
인터넷 (사용자)
    ↓ (80/443)
Application Load Balancer (library-alb-sg)
    ↓ (라우팅)
   ┌─────────┬─────────┐
   ↓         ↓         ↓
Blue TG   Green TG   (전환 가능)
   ↓         ↓
ECS Task (8081, library-ecs-task-sg)
    ↓ (3306)
RDS MySQL (library-rds-sg)
```

## 계층별 보안 전략

### 계층 1: 인터넷 → ALB
- **보안 그룹**: library-alb-sg
- **허용 포트**: 80, 443
- **출처**: 전체 인터넷 (0.0.0.0/0)
- **목적**: 공개 웹 서비스 제공

### 계층 2: ALB → ECS Task
- **보안 그룹**: library-ecs-task-sg
- **허용 포트**: 8081
- **출처**: library-alb-sg만
- **목적**: ALB를 통해서만 애플리케이션 접근 가능

### 계층 3: ECS Task → RDS
- **보안 그룹**: library-rds-sg
- **허용 포트**: 3306
- **출처**: library-ecs-task-sg만
- **목적**: 애플리케이션에서만 데이터베이스 접근 가능

## Blue-Green 배포 전략

### 배포 프로세스

1. **초기 상태**: Blue TG에서 운영 중
   - ALB 리스너가 Blue TG로 100% 트래픽 전달
   - Green TG는 대기 상태

2. **새 버전 배포**: Green TG에 배포
   - Green TG에 새 버전의 ECS Task 시작
   - 헬스 체크 통과 확인

3. **트래픽 전환**: Green TG로 전환
   - ALB 리스너 규칙을 Green TG로 변경
   - 즉시 또는 점진적 전환 가능

4. **롤백 준비**: Blue TG 유지
   - 문제 발생 시 Blue TG로 즉시 롤백 가능
   - Blue TG의 이전 버전은 일정 시간 유지

### 장점

- **무중단 배포**: 트래픽 전환 시 서비스 중단 없음
- **빠른 롤백**: 문제 발생 시 이전 버전으로 즉시 복구
- **안전한 테스트**: Green 환경에서 프로덕션과 동일한 조건으로 테스트 가능

## 주요 설계 원칙

### 최소 권한 원칙
- 각 계층은 필요한 최소한의 접근만 허용
- 보안 그룹은 출처를 명시적으로 제한
- 직접 접근 경로를 차단하고 정해진 경로만 허용

### 계층 분리
- 각 계층은 독립적인 보안 그룹으로 보호
- 계층을 건너뛴 직접 접근 불가
- 명확한 트래픽 흐름 경로 유지

### 고가용성
- 다중 AZ 배포로 장애 대응
- ALB의 자동 헬스 체크와 트래픽 분산
- Blue-Green 배포로 안전한 업데이트

## 다이어그램

전체 관계도는 [18-aws-security-and-loadbalancing.svg](./18-aws-security-and-loadbalancing.svg) 파일을 참조하세요.

## 관련 문서

- [Phase 3 진행 상황](./03-phase3-progress.md)
- [AWS 인프라 설정 가이드](./aws-infrastructure-setup.md)
