# 最终交付目录结构

生成日期：2026-06-29

```text
astock-emotion-system
├── astock-app
│   └── Spring Boot 启动模块、Controller、一键跑批编排
├── astock-common
│   └── 通用返回、异常、转换、数据结构
├── astock-infrastructure
│   └── MySQL / ClickHouse / Engine执行模板
├── astock-api
│   └── API契约
├── astock-modules
│   ├── module-emotion
│   ├── module-similarity
│   ├── module-mainline
│   ├── module-sector
│   ├── module-leader
│   ├── module-pattern
│   ├── module-risk
│   ├── module-backtest
│   └── module-agent-audit
├── astock-frontend
│   └── Vue3 + Vite + TypeScript 前端
├── sql
│   ├── 20_mysql_full_schema.sql
│   ├── 21_clickhouse_full_schema.sql
│   ├── 22_rule_version_seed.sql
│   └── 23_mysql_engine_batch_schema.sql
├── scripts
│   └── 本地启动、初始化、构建、检查、跑批脚本
├── docker
│   ├── backend/Dockerfile
│   ├── frontend/Dockerfile
│   ├── frontend/nginx.conf
│   ├── mysql/conf.d
│   └── clickhouse/config.d
├── docs
│   ├── LOCAL_DEV_ENV.md
│   ├── FRONTEND_BACKEND_INTEGRATION_CHECKLIST.md
│   ├── FINAL_RELEASE_CHECKLIST.md
│   └── API_SMOKE_TESTS.md
├── docker-compose.yml
├── Makefile
├── .env.example
└── README_FINAL_DELIVERY.md
```
