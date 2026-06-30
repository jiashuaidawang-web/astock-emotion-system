# 前后端联调清单

生成日期：2026-06-29

## 必须启动

```text
MySQL: astock_business
ClickHouse: astock_analysis
Spring Boot: astock-app
Vue: astock-frontend
```

## 必须执行脚本

```text
sql/20_mysql_full_schema.sql
sql/22_rule_version_seed.sql
sql/23_mysql_engine_batch_schema.sql
sql/21_clickhouse_full_schema.sql
```

## 跑批接口

```http
POST /api/engines/batch/daily/run
```

验收：

```text
1. batchStatus 有明确状态
2. steps 不为空
3. 每个 step 有 success / outputRowCount / failureReason
4. Risk 后置二次覆盖步骤存在
```

## 15页面接口

```text
GET /api/dashboard/market
GET /api/similarity/market
GET /api/emotion-cycle/state-machine
GET /api/cycle-samples/page
GET /api/mainlines/radar
GET /api/sectors/strength
GET /api/leaders/ladder
GET /api/leaders/{stockCode}/profile
GET /api/patterns/conditions
GET /api/risks/control
GET /api/backtests/lab
GET /api/backtests/reports/{reportId}
GET /api/reviews/daily/workbench
GET /api/rules/versions/page
GET /api/agent-audit/dashboard
```

每个页面验收：

```text
1. HTTP 200
2. ApiResult.data 不为空
3. dataComplete 正确展示
4. KPI / 排行 / 表格 / 状态标签可渲染
5. 浏览器控制台无 JS 错误
```

## 合规验收

```text
1. Pattern 页面不得出现交易建议。
2. Backtest 页面只展示历史统计。
3. Similarity 页面不得将 future_* 用作 T 日匹配。
4. AgentAudit BLOCKED 时必须阻断发布。
```
