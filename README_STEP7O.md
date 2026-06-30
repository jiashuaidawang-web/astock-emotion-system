# 第七步第十五段：BacktestExecutor 回测真实算法

生成日期：2026-06-29

## 本次完成内容

这次把 `BacktestExecutor` 从执行骨架推进到真实算法落地。

## 新增类

```text
BacktestReplaySample
BacktestReplayResult
BacktestLayerStat
BacktestFailureCase
BacktestReplaySampleRepository
BacktestReplaySampleRepositoryImpl
BacktestReplaySampleSql
BacktestFutureWindowGuardService
BacktestReplaySampleBuilder
BacktestSignalReplayService
BacktestPerformanceCalculationService
BacktestOutputRowBuilder
```

## 已落地真实链路

```text
JavaBacktestExecutor
-> EngineExecutionTemplate
-> RuleVersionGuardService
-> BacktestReplaySampleRepository
   -> historical_cycle_sample
   -> buy_pattern_signal_snapshot
   -> risk_signal_snapshot
   -> stock_daily_kline
-> BacktestReplaySampleBuilder
-> BacktestFutureWindowGuardService
-> BacktestSignalReplayService
-> BacktestPerformanceCalculationService
-> BacktestOutputRowBuilder
-> EngineSnapshotWriteService
-> ClickHouse:
   - backtest_signal_detail
   - backtest_performance_detail
   - backtest_layer_stat
   - backtest_failure_case
-> algorithm_task_log
```

## 回测样本读取已落地

样本读取规则：

```text
historical_cycle_sample.trade_date < backtest_end_date
buy_pattern_signal_snapshot.trade_date < backtest_end_date
risk_signal_snapshot.trade_date < backtest_end_date
stock_daily_kline.trade_date < backtest_end_date
```

当前默认最多读取：

```text
sampleLimit = 3000
```

后续可从 `paramJson` 扩展：

```text
startDate
endDate
sampleLimit
patternCode
ruleVersionId
holdingWindow
```

## 信号回放已落地

回放逻辑：

```text
1. 根据历史样本日 sample_date 找到当时的模式信号；
2. 根据历史样本日 sample_date 找到当时的风控信号；
3. 若 risk_action 为 RISK_VETO / PATTERN_INVALIDATED / DATA_BLOCK，则记为 RISK_FILTERED；
4. 若 signal_score >= 55 且未被风控否决，记为有效信号；
5. 在回测窗口读取 future_* 后续表现；
6. 生成 replay_status、failure_type、replay_return、replay_drawdown。
```

## future_* 读取红线已加固

新增：

```text
BacktestFutureWindowGuardService
```

严格保证：

```text
future_1d_return
future_3d_return
future_5d_return
future_10d_return
max_drawdown
```

只能在：

```text
sample_date < backtest_end_date
```

的历史回测窗口读取。

信号回放阶段读取模式条件、风险条件时，禁止读取 future_* 字段。

## 输出表已落地

```text
backtest_signal_detail
backtest_performance_detail
backtest_layer_stat
backtest_failure_case
```

## 统计指标已落地

```text
sample_count
effective_signal_count
risk_veto_count
win_rate
avg_return
avg_drawdown
profit_loss_ratio
```

## 失败样本归因已落地

```text
RISK_VETO_FILTERED
WEAK_SIGNAL
HIGH_DRAWDOWN
NEGATIVE_RETURN
```

## 合规边界

```text
1. 回测只做历史验证，不输出交易建议。
2. future_* 只在回测窗口读取。
3. future_* 不参与T日信号生成。
4. future_* 不参与T日相似度匹配。
5. future_* 不参与买点条件判定。
6. 回测结果只写统计与样本归因。
```

## 新增 SQL 契约说明

```text
sql/15_backtest_executor_output_contract.sql
```

## 下一步建议

继续第七步第十六段：

```text
补齐 AgentAuditExecutor 真实算法：
代码红线扫描、字段血缘审计、Mock检测、交易建议词检测、future函数检测、发布闸门检查，
并写入 agent_audit_* 相关快照。
```
