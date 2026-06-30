# 第十步：Engine一键跑批与端到端闭环验证

生成日期：2026-06-29

## 本次完成内容

你已经有真实数据，所以跳过“最小样本初始化”，本次直接进入下一步：一键跑批编排。

新增能力：

```text
1. Engine依赖顺序编排
2. 一键跑完日内全链路
3. 支持Pattern之后二次执行Risk
4. 支持失败即停或失败继续
5. 支持是否执行Backtest
6. 支持是否执行AgentAudit
7. 批次日志落MySQL
8. 步骤日志落MySQL
9. 返回每个Engine输出表、输出行数、失败原因
```

## 新增接口

```http
POST /api/engines/batch/daily/run
```

请求示例：

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

## 默认执行顺序

```text
1. EmotionStageRecognitionEngine
2. MainlineRecognitionEngine
3. LeaderRecognitionEngine
4. RiskControlEngine              风控预检查
5. PatternConditionEngine
6. RiskControlEngine              Pattern后风控二次覆盖
7. SimilarityMatchEngine
8. BacktestExecutor               可选
9. AgentAuditExecutor             默认执行
```

## 为什么Risk要执行两次

```text
第一次 Risk：
在Pattern之前生成当天基础风险状态。

第二次 Risk：
在Pattern之后读取 buy_pattern_signal_snapshot，
对模式条件执行上级风控覆盖，
写入 pattern_risk_veto_snapshot。
```

## 新增Java文件

```text
EngineBatchRunRequest
EngineBatchStepResult
EngineBatchRunResult
EngineBatchRunLogMapper
EngineBatchOrchestrationService
EngineBatchRunController
```

## 新增MySQL表

```text
engine_batch_run_log
engine_batch_step_log
```

脚本：

```text
sql/23_mysql_engine_batch_schema.sql
```

## 执行前必须补充DDL

```bash
mysql -uroot -p astock_business < sql/23_mysql_engine_batch_schema.sql
```

## 返回结果

返回结构包含：

```text
batchId
tradeDate
marketScope
batchStatus
success
totalStepCount
successStepCount
failedStepCount
steps[]
```

每个step包含：

```text
stepNo
stepCode
stepName
engineName
success
taskId
outputRowCount
outputTables
failureReason
summaryText
costMillis
```

## 红线保持

```text
1. Controller只触发编排，不写算法。
2. 编排层只负责顺序、失败策略、日志，不篡改Engine结果。
3. RiskControlEngine仍是PatternConditionEngine上级保护层。
4. BacktestExecutor默认不跑，避免日常跑批误触发大回测。
5. AgentAuditExecutor默认跑，作为发布闸门。
```

## 下一步建议

进入第十一步：

```text
生成前端15页面真实API联调层：
Vue3 API Client、统一PageVO类型、页面loading/error/dataComplete状态、
以及一键跑批按钮与Engine执行进度面板。
```
