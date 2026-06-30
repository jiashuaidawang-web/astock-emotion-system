# A股情绪周期复盘与历史相似行情回测系统：全栈最终交付包

生成日期：2026-06-29

## 交付范围

本交付包已经合并：

```text
1. Java 21 + Spring Boot 3.x 后端宏单体工程
2. Vue 3 + Vite + TypeScript 前端工程
3. MySQL 业务库完整DDL
4. ClickHouse 分析库完整DDL
5. 规则版本初始化脚本
6. Engine一键跑批批次日志脚本
7. Docker Compose 本地开发环境
8. 后端 Dockerfile
9. 前端 Dockerfile + Nginx
10. 本地启动脚本
11. 数据库初始化脚本
12. 前后端联调清单
13. 最终发布检查清单
```

## 顶层目录结构

```text
astock-emotion-system
├── astock-app                         # Spring Boot 启动模块
├── astock-common                      # 公共模块
├── astock-infrastructure              # 基础设施模块
├── astock-api                         # API契约模块
├── astock-modules                     # 业务模块聚合
├── astock-frontend                    # Vue3前端工程
├── sql                                # MySQL + ClickHouse DDL/Seed
├── scripts                            # 本地开发/初始化/检查脚本
├── docker                             # Dockerfile/Nginx/ClickHouse配置
├── docs                               # 联调、发布、环境文档
├── docker-compose.yml                 # 全栈本地开发编排
├── .env.example                       # 全栈环境变量模板
├── Makefile                           # 常用命令聚合
└── README_FINAL_DELIVERY.md           # 最终交付说明
```

## 快速启动

### 1. 复制环境变量

```bash
cp .env.example .env
```

### 2. 启动基础设施

```bash
docker compose up -d mysql clickhouse
```

### 3. 初始化数据库

```bash
bash scripts/init-mysql.sh
bash scripts/init-clickhouse.sh
```

### 4. 启动后端

本机方式：

```bash
bash scripts/run-backend-local.sh
```

Docker方式：

```bash
docker compose up -d backend
```

### 5. 启动前端

本机方式：

```bash
bash scripts/run-frontend-local.sh
```

Docker方式：

```bash
docker compose up -d frontend
```

## 一键跑批接口

```http
POST /api/engines/batch/daily/run
```

示例：

```json
{
  "tradeDate": "2026-06-30",
  "marketScope": "A_SHARE",
  "ruleVersionId": 1,
  "dataCheckEnabled": true,
  "continueOnFailure": false,
  "runBacktest": false,
  "runAgentAudit": true,
  "rerunRiskAfterPattern": true,
  "paramJson": "{}"
}
```

## 前端入口

```text
http://127.0.0.1:5173
```

Docker Nginx 前端入口：

```text
http://127.0.0.1:18080
```

## 后端入口

```text
http://127.0.0.1:8080
```

## 数据库入口

```text
MySQL:       127.0.0.1:3306 / astock_business
ClickHouse: 127.0.0.1:8123 / astock_analysis
```

## 最终红线

```text
1. Controller 不返回 Mock。
2. Aggregator 不注水。
3. 前端不计算核心业务结论。
4. Pattern 页面不输出交易建议。
5. Similarity T日匹配不使用 future_*。
6. Backtest 才能读取 future_*。
7. RiskControlEngine 是 PatternConditionEngine 上级保护层。
8. AgentAuditExecutor 是发布闸门。
```

## 构建说明

当前沙盒环境未安装 Maven 和前端 node_modules，不能执行真实全量构建。

已完成：

```text
1. Java源码静态清算
2. 前端源码静态清算
3. 文件结构完整性检查
4. Docker / 脚本 / README / SQL 合并
```

正式环境必须执行：

```bash
mvn -U clean package -DskipTests
cd astock-frontend && npm install && npm run build
docker compose config
```


## V2 对齐修正

本包已修正旧版最终包 SQL 初始化入口问题。

必须使用：

```text
sql/init_mysql.sql  -> 42 张 MySQL 表
sql/init_ck.sql     -> 39 张 ClickHouse 表
```

初始化脚本已经改为：

```bash
bash scripts/init-mysql.sh
bash scripts/init-clickhouse.sh
```

两个脚本会做表数量硬校验，低于 42 / 39 会直接拒绝执行。

详见：

```text
docs/FINAL_V2_ALIGNMENT_CORRECTION_REPORT.md
```
