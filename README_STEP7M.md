# 第七步第十三段：PatternConditionEngine 买点条件判定真实算法

生成日期：2026-06-29

## 本次完成内容

这次把 `PatternConditionEngine` 从执行骨架推进到真实算法落地。

## 新增类

```text
PatternType
PatternWatchObject
PatternEngineSupportContext
PatternSignalScore
PatternEngineSupportContextRepository
PatternEngineSupportContextRepositoryImpl
PatternEngineSupportContextSql
PatternWatchPoolBuilder
PatternConditionScoringService
PatternOutputRowBuilder
```

## 已落地真实链路

```text
JavaPatternConditionEngine
-> EngineExecutionTemplate
-> RuleVersionGuardService
-> EngineDataQualityGuard
-> EngineSnapshotLoadService
-> PatternEngineSupportContextRepository
   -> pattern_backtest_result
   -> pattern_risk_binding
   -> buy_pattern_stage_matrix
   -> buy_pattern_rule_config
-> PatternWatchPoolBuilder
-> PatternConditionScoringService
-> PatternOutputRowBuilder
-> EngineSnapshotWriteService
-> ClickHouse:
   - buy_pattern_signal_snapshot
   - pattern_risk_veto_snapshot
-> algorithm_task_log
```

## pattern_condition_score 公式已落地

```text
pattern_condition_score =
周期准入分 * 25%
+ 主线有效分 * 20%
+ 龙头地位分 * 20%
+ 模式触发分 * 20%
+ 历史回测支持分 * 10%
+ 人工确认修正 * 5%
```

## 观察池筛选已落地

允许进入观察池：

```text
MARKET_LEADER
MAINLINE_LEADER
TREND_LEADER
MIDDLE_ARMY
COMPENSATION_LEADER
SWITCH_LEADER
```

默认排除：

```text
FOLLOWER
无主线普通强势股
数据不完整对象
被风险停止观察对象
```

## 固定六类模式已落地

```text
ICE_REPAIR
MAINLINE_STARTUP
LEADER_DIVERGENCE_TO_CONSISTENCY
TREND_LEADER_PULLBACK
CLIMAX_NO_CHASE
RETREAT_STOP
```

## 风控否决已落地

只要出现：

```text
risk_veto = true
```

最终状态必须变成：

```text
RISK_VETO
```

并写入：

```text
pattern_risk_veto_snapshot
```

## 状态输出

支持状态：

```text
WAITING
WEAK_MATCH
OBSERVING
CONDITION_MET
RISK_VETO
INVALIDATED
NOT_APPLICABLE
```

## 合规红线

这版严格保证：

```text
1. 只输出“条件状态”
2. 不输出买入
3. 不输出卖出
4. 不输出持有
5. 不输出推荐
6. 不输出目标价
7. signal_text 明确标记“不是交易建议”
```

## 输出表

```text
buy_pattern_signal_snapshot
pattern_risk_veto_snapshot
```

## 新增 SQL 契约说明

```text
sql/13_pattern_condition_engine_output_contract.sql
```

## 下一步建议

继续第七步第十四段：

```text
补齐 RiskControlEngine 真实算法：
risk_score公式、risk_signal_snapshot写入、risk_signal_detail写入、pattern_risk_veto_snapshot二次覆盖写入。
让风控成为买点条件的上级保护层。
```
