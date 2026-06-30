# 最终发布检查清单

生成日期：2026-06-29

## 1. 后端构建

```bash
mvn -U clean package -DskipTests
```

验收：

```text
1. Maven 编译通过
2. astock-app target jar 生成
3. 无重复 Bean 冲突
4. 无 Mapper 注入失败
5. 无接口签名不一致
```

## 2. 前端构建

```bash
cd astock-frontend
npm install
npm run build
```

验收：

```text
1. vue-tsc --noEmit 通过
2. vite build 通过
3. dist 目录生成
4. 页面路由懒加载正常
5. 无 alias import 失败
```

## 3. Docker Compose

```bash
docker compose config
docker compose up -d mysql clickhouse
bash scripts/init-mysql.sh
bash scripts/init-clickhouse.sh
docker compose up -d backend frontend
```

验收：

```text
1. mysql healthy
2. clickhouse healthy
3. backend healthy
4. frontend 可访问
```

## 4. 数据库

验收表：

```text
MySQL:
rule_version
algorithm_task_log
engine_batch_run_log
engine_batch_step_log
page_contract_field_lineage

ClickHouse:
emotion_stage_snapshot
historical_similarity_match
mainline_daily_snapshot
leader_daily_snapshot
buy_pattern_signal_snapshot
risk_signal_snapshot
backtest_signal_detail
agent_audit_release_gate_detail
```

## 5. 一键跑批

```bash
bash scripts/run-daily-batch.sh 2026-06-30
```

验收：

```text
1. 返回 batchId
2. step 列表不为空
3. RiskControlEngine 至少执行一次
4. Pattern 后 Risk 二次覆盖存在
5. AgentAudit 默认执行
```

## 6. 合规检查

```text
1. Pattern 页面不得出现交易建议词。
2. Similarity 不得使用 future_* 做T日匹配。
3. Backtest future_* 只在回测窗口读取。
4. RiskControlEngine 必须能覆盖 PatternConditionEngine。
5. AgentAudit 出现 BLOCKED 必须阻断发布。
```
