# 第七步第八段：EmotionStageRecognitionEngine 真实算法落地

生成日期：2026-06-29

## 本次完成内容

这次把第一个核心算法 `EmotionStageRecognitionEngine` 从“执行骨架”推进到了“真实计算并落库”。

## 新增领域类

```text
EmotionStageType
EmotionMarketFeature
EmotionStageScore
EmotionMarketFeatureExtractor
EmotionStageScoringService
EmotionStageOutputRowBuilder
```

## 已落地的真实算法链路

```text
JavaEmotionStageRecognitionEngine
-> EngineExecutionTemplate
-> RuleVersionGuardService
-> EngineDataQualityGuard
-> EngineSnapshotLoadService
-> EmotionMarketFeatureExtractor
-> EmotionStageScoringService
-> EmotionStageOutputRowBuilder
-> EngineSnapshotWriteService
-> ClickHouse:
   - emotion_stage_score_detail
   - emotion_stage_snapshot
   - stage_transition_snapshot
-> algorithm_task_log
```

## 10阶段评分已落地

固定输出10个阶段：

```text
ICE_POINT       冰点
REPAIR          修复
TRIAL           试错
STARTUP         启动
FERMENTATION    发酵
MAIN_RISE       主升
CLIMAX          高潮
DIVERGENCE      分歧
RETREAT         退潮
CHAOS           混沌
```

每个阶段都会输出一行 `emotion_stage_score_detail`，包括：

```text
stage_score
factor_percentile_match_score
historical_sample_similarity_score
stage_path_match_score
following_validation_score
manual_sample_correction_score
rank_no
evidence_json
risk_json
```

## stage_score 公式已落地

```text
stage_score =
因子分位匹配分 * 30%
+ 历史样本相似分 * 35%
+ 阶段路径匹配分 * 20%
+ 后续演化验证分 * 10%
+ 人工确认样本修正 * 5%
```

## 当前算法不是静态阈值硬判定

本实现没有使用：

```java
if (limitUpCount <= 30) return ICE_POINT;
if (limitDownCount > 20) return RETREAT;
```

而是：

```text
1. 从真实 market_factor_snapshot 读取市场宽度、赚钱效应、亏钱效应、成交额热度；
2. 从真实 limit_up_down_ecology_snapshot 读取涨停生态、跌停压力、炸板压力、梯队高度；
3. 构建连续特征向量；
4. 对10个阶段都计算连续评分；
5. 排序得到主阶段、第二候选、第三候选；
6. 全量写入 score_detail；
7. 主阶段摘要写入 snapshot；
8. 可能转移方向写入 transition_snapshot。
```

## 输出表

```text
emotion_stage_score_detail
emotion_stage_snapshot
stage_transition_snapshot
```

## 新增SQL说明

```text
sql/08_emotion_stage_engine_output_contract.sql
```

## 重要边界

1. 当前版本只强制依赖当日 `market_factor_snapshot` 和 `limit_up_down_ecology_snapshot`。
2. 历史样本、人工修正、相似样本会在后续增强中接入为非关键输入。
3. 没有任何Mock输出行。
4. 如果ClickHouse目标表缺字段，写入会失败并记录到 `algorithm_task_log`。
5. `future_*` 字段没有参与T日阶段判断。

## 下一步建议

继续第七步第九段：

```text
增强 EmotionStageRecognitionEngine：
接入历史周期样本 historical_cycle_sample、
人工确认 cycle_sample_confirm/manual_stage_adjustment、
stage_transition_snapshot 过去N日路径，
让 historical_sample_similarity_score、manual_sample_correction_score、stage_path_match_score 从真实历史库进一步增强。
```
