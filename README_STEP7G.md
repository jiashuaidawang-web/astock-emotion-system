# 第七步第七段：核心Engine真实执行骨架

生成日期：2026-06-29

## 本次完成内容

这次补齐了8个核心Engine的真实执行基础闭环，不再只是接口定义。

## 已补齐的8个Engine

| 引擎 | 实现类 | 输入表 | 输出表 | 规则编码 |
|---|---|---|---|---|
| ENGINE_EMOTION_STAGE | JavaEmotionStageRecognitionEngine | market_factor_snapshot, limit_up_down_ecology_snapshot, historical_similarity_match, historical_cycle_sample | emotion_stage_snapshot, emotion_stage_score_detail, stage_transition_snapshot | EMOTION_STAGE_CORE |
| ENGINE_SIMILARITY_MATCH | JavaSimilarityMatchEngine | market_factor_snapshot, limit_up_down_ecology_snapshot, emotion_stage_snapshot, mainline_daily_snapshot, leader_daily_snapshot, historical_cycle_sample | historical_similarity_match, historical_similarity_factor_detail | SIMILARITY_MATCH_CORE |
| ENGINE_MAINLINE | JavaMainlineRecognitionEngine | theme_daily_snapshot, theme_strength_snapshot, sector_strength_snapshot, leader_daily_snapshot, emotion_stage_snapshot | theme_strength_snapshot, mainline_daily_snapshot, mainline_switch_snapshot | MAINLINE_RECOGNITION_CORE |
| ENGINE_LEADER | JavaLeaderRecognitionEngine | stock_daily_kline, mainline_daily_snapshot, theme_strength_snapshot, sector_strength_snapshot, limit_up_down_ecology_snapshot | leader_daily_snapshot, leader_ladder_snapshot, leader_drive_snapshot, leader_negative_feedback, trend_leader_snapshot | LEADER_RECOGNITION_CORE |
| ENGINE_PATTERN | JavaPatternConditionEngine | emotion_stage_snapshot, mainline_daily_snapshot, leader_daily_snapshot, risk_signal_snapshot | buy_pattern_signal_snapshot, pattern_risk_veto_snapshot | PATTERN_CONDITION_CORE |
| ENGINE_RISK | JavaRiskControlEngine | emotion_stage_snapshot, market_factor_snapshot, limit_up_down_ecology_snapshot, leader_negative_feedback, mainline_daily_snapshot | risk_signal_snapshot, risk_signal_detail, pattern_risk_veto_snapshot | RISK_CONTROL_CORE |
| ENGINE_BACKTEST | JavaBacktestExecutor | historical_cycle_sample, buy_pattern_signal_snapshot, risk_signal_snapshot, stock_daily_kline | backtest_signal_detail, backtest_performance_detail, backtest_layer_stat, backtest_failure_case | BACKTEST_EXECUTION_CORE |
| ENGINE_AGENT_AUDIT | JavaAgentAuditExecutor | agent_audit_code_scan_detail, agent_audit_data_lineage_detail, agent_audit_rule_hit_detail, agent_audit_release_gate_detail | agent_audit_code_scan_detail, agent_audit_data_lineage_detail, agent_audit_rule_hit_detail, agent_audit_release_gate_detail | AGENT_AUDIT_CORE |

## 新增公共执行闭环

```text
EngineRunController
-> Java*Engine
-> EngineExecutionTemplate
-> RuleVersionGuardService
-> EngineDataQualityGuard
-> EngineSnapshotLoadService
-> EngineAlgorithm.compute
-> EngineSnapshotWriteService
-> EngineTaskLogService
-> algorithm_task_log
```

## 新增基础设施类

```text
EngineRunCommand
EngineRunResult
EngineAlgorithm
EngineExecutionTemplate
EngineTaskLogService
AlgorithmTaskLogMapper
RuleVersionGuardService
EngineDataQualityGuard
EngineSnapshotLoadService
EngineSnapshotWriteService
EngineConstants
```

## 当前执行行为

1. 读取或解析 `rule_version`。
2. 检查输入ClickHouse快照是否存在。
3. 写入 `algorithm_task_log` RUNNING。
4. 加载真实输入快照到 `PageSnapshotBundle`。
5. 调用每个Engine自己的 `compute()`。
6. 如果 `compute()` 返回真实输出行，则通过 `EngineSnapshotWriteService` 写入ClickHouse目标表。
7. 如果当前算法还没实现，不会造假输出行，而是标记 `PARTIAL_SUCCESS`，并写入任务日志说明。
8. 异常时标记 `FAILED`，写入失败原因。

## 为什么当前compute返回空

第七步第七段只做“真实执行骨架”，不是把8套算法公式全部写完。

所以每个Engine的 `compute()` 当前明确返回 `Map.of()`，代表：

```text
规则版本校验完成
数据完整性检查完成
任务日志完成
输入快照加载完成
输出写入通道完成
但不使用Mock伪造业务快照
```

下一步进入算法实现时，只需要把 `compute()` 替换为真实公式计算，并返回目标表行即可。

## 手动触发接口

```http
POST /api/engines/emotion-stage/run
POST /api/engines/similarity/run
POST /api/engines/mainline/run
POST /api/engines/leader/run
POST /api/engines/pattern/run
POST /api/engines/risk/run
POST /api/engines/backtest/run
POST /api/engines/agent-audit/run
```

请求示例：

```json
{
  "tradeDate": "2026-06-30",
  "marketScope": "A_SHARE",
  "ruleVersionId": 1,
  "dataCheckEnabled": true,
  "paramJson": "{}"
}
```

## 红线

1. Engine不允许不绑定规则版本。
2. Engine不允许跳过数据完整性检查。
3. Engine不允许返回Mock输出行。
4. Engine不允许失败后标记SUCCESS。
5. Engine不允许写非白名单输出表。
6. `future_*` 字段不能参与T日判断。
7. 买点、回测、复盘、审计不得输出交易指令。
8. Controller只触发Engine，不写任何算法逻辑。

## 下一步建议

进入第七步第八段：

```text
开始补齐第一个真实算法：EmotionStageRecognitionEngine。
要求落地10阶段评分明细、stage_score公式、emotion_stage_score_detail写入、emotion_stage_snapshot写入，仍然禁止静态阈值硬判定。
```
