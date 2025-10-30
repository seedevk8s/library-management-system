소스 = "누구한테서 오는 트래픽을 허용할 것인가?"

- 0.0.0.0/0        → 전세계 모든 IP (인터넷 공개)
- library-alb-sg   → ALB에서만 허용
- library-ecs-task-sg → ECS에서만 허용
```

#### 2. **왜 체인 구조인가?** (보안 강화)
```
❌ 나쁜 예: RDS를 0.0.0.0/0으로 열기
→ 전세계 누구나 DB 접근 가능 (해킹 위험!)

✅ 좋은 예: 계층별로 제한
→ RDS는 ECS에서만 접근 가능 (안전!)
```

#### 3. **트래픽 흐름**
```
① Internet → ALB (Port 80, 443)
소스: 0.0.0.0/0 (누구나 웹사이트 접속 가능)

② ALB → ECS (Port 8081)
소스: library-alb-sg (ALB만 접근 가능)
→ 인터넷에서 직접 ECS 접근 불가! 반드시 ALB를 거쳐야 함

③ ECS → RDS (Port 3306)
소스: library-ecs-task-sg (ECS만 접근 가능)
→ 인터넷이나 ALB에서 직접 DB 접근 불가! 반드시 ECS를 거쳐야 함