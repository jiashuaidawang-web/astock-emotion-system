# 第七步第十四段：RiskControlEngine 风控真实算法

生成日期：2026-06-29

## 本次完成内容

这次把 `RiskControlEngine` 从执行骨架推进到真实算法落地，并让风控成为模式条件的上级保护层。

## 新增类

```text
RiskFactorSnapshot
RiskSignalScore
RiskControlContext
RiskControlContextRepository
RiskControlContextRepositoryImpl
RiskControlContextSql
RiskFactorExtractor
RiskScoreCalculationService
RiskOutputRowBuilder
```

## 已落地真实链路

```text
JavaRiskControlEngine
-> EngineExecutionTemplate
-> RuleVersionGuardService
-> EngineDataQualityGuard
-> EngineSnapshotLoadService
-> RiskControlContextRepository
   -> buy_pattern_signal_snapshot
   -> data_quality_check_log
   -> risk_action_matrix
-> RiskFactorExtractor
-> RiskScoreCalculationService
-> RiskOutputRowBuilder
-> EngineSnapshotWriteService
-> ClickHouse:
   - risk_signal_snapshot
   - risk_signal_detail
   - pattern_risk_veto_snapshot
-> algorithm_task_log
```

## risk_score 公式已落地

```text
risk_score =
情绪周期风险 * 20%
+ 亏钱效应风险 * 20%
+ 涨跌停生态风险 * 20%
+ 龙头负反馈风险 * 20%
+ 主线衰退风险 * 10%
+ 指数资金风险 * 5%
+ 数据完整性风险 * 5%
```

## 风险因子来源

```text
emotion_stage_snapshot:
- emotion_stage
- stage_confidence

market_factor_snapshot:
- loss_effect_score
- index_fund_risk_score

limit_up_down_ecology_snapshot:
- limit_down_count
- limit_up_count
- break_board_count

leader_negative_feedback:
- negative_feedback_score

mainline_daily_snapshot:
- mainline_strength_score
- mainline_decay_risk_score

data_quality_check_log:
- completeness_ratio
- critical
```

## 输出表

```text
risk_signal_snapshot
risk_signal_detail
pattern_risk_veto_snapshot
```

## 风控动作已落地

```text
NORMAL
CAUTION
REQUIRE_CONFIRMATION
PATTERN_INVALIDATED
RISK_VETO
DATA_BLOCK
```

## 上级保护层逻辑

当综合风险触发：

```text
RISK_VETO
PATTERN_INVALIDATED
DATA_BLOCK
```

RiskControlEngine 会读取当天 `buy_pattern_signal_snapshot`，并对相关信号写入 `pattern_risk_veto_snapshot`，形成二次覆盖。

这样页面上的模式条件即使已达到条件状态，也必须被风控上级保护层覆盖。

## 合规红线

这版继续保证：

```text
1. 风控只输出风险状态和条件否决
2. 不输出交易建议
3. 不输出目标价
4. 不把风控动作包装成操作指令
5. 风控优先级高于PatternConditionEngine
```

## 新增 SQL 契约说明

```text
sql/14_risk_control_engine_output_contract.sql
```

## 下一步建议

继续第七步第十五段：

```text
补齐 BacktestExecutor 真实算法：
回测样本读取、信号回放、future_*只在回测窗口读取、backtest_signal_detail写入、backtest_performance_detail写入、backtest_layer_stat写入、backtest_failure_case写入。
```
