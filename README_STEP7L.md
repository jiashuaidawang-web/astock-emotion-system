# 第七步第十二段：LeaderRecognitionEngine 龙头识别真实算法

生成日期：2026-06-29

## 本次完成内容

这次把 `LeaderRecognitionEngine` 从执行骨架推进到真实算法落地。

## 新增类

```text
LeaderCandidateFeature
LeaderRecognitionContext
LeaderScore
LeaderRecognitionContextRepository
LeaderRecognitionContextRepositoryImpl
LeaderRecognitionContextSql
LeaderFeatureExtractor
LeaderScoreCalculationService
LeaderOutputRowBuilder
```

## 已落地真实链路

```text
JavaLeaderRecognitionEngine
-> EngineExecutionTemplate
-> RuleVersionGuardService
-> EngineDataQualityGuard
-> EngineSnapshotLoadService
-> LeaderRecognitionContextRepository
   -> 过去N日 leader_daily_snapshot
   -> risk_signal_snapshot
   -> buy_pattern_signal_snapshot
-> LeaderFeatureExtractor
-> LeaderScoreCalculationService
-> LeaderOutputRowBuilder
-> EngineSnapshotWriteService
-> ClickHouse:
   - leader_daily_snapshot
   - leader_ladder_snapshot
   - leader_drive_snapshot
   - leader_negative_feedback
-> algorithm_task_log
```

## leader_score 公式已落地

```text
leader_score =
辨识度评分 * 20%
+ 主线关联评分 * 20%
+ 带动性评分 * 20%
+ 强度评分 * 15%
+ 承接评分 * 10%
+ 持续性评分 * 10%
+ 风险反馈评分 * 5%
```

## leader_drive_score 公式已落地

```text
leader_drive_score =
板块带动 * 35%
+ 主线带动 * 30%
+ 情绪带动 * 20%
+ 资金带动 * 15%
```

## negative_feedback_score 公式已落地

```text
negative_feedback_score =
断板跌幅 * 25%
+ 断板后板块跌幅 * 25%
+ 后排亏钱效应 * 20%
+ 高标晋级率下降 * 15%
+ 情绪温度下降 * 15%
```

## 输出表

```text
leader_daily_snapshot
leader_ladder_snapshot
leader_drive_snapshot
leader_negative_feedback
```

## 这版明确禁止

```text
最高板 = 市场总龙头
```

当前逻辑是：

```text
1. 提取每只股票的基础强度、板高、成交、承接；
2. 接入 mainline_daily_snapshot 计算主线关联；
3. 接入 sector_strength_snapshot 计算板块带动；
4. 接入过去 leader_daily_snapshot 计算持续性；
5. 接入 risk_signal_snapshot 计算后排亏钱效应；
6. 计算七维 leader_score；
7. 按综合分排序生成市场龙头候选；
8. 板高只进入辨识度和梯队维度，不是唯一判定。
```

## 新增 SQL 契约说明

```text
sql/12_leader_recognition_engine_output_contract.sql
```

## 下一步建议

继续第七步第十三段：

```text
补齐 PatternConditionEngine 真实算法：
pattern_condition_score公式、watch_pool筛选、buy_pattern_signal_snapshot写入、pattern_risk_veto_snapshot写入。
严格保证“买点条件判定”不是交易建议。
```
