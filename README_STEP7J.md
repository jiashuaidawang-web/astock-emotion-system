# 第七步第十段：SimilarityMatchEngine 九维相似度真实算法

生成日期：2026-06-29

## 本次完成内容

这次把 `SimilarityMatchEngine` 从“执行骨架”推进到真实算法落地。

## 新增类

```text
SimilarityDimensionType
SimilarityFeatureVector
SimilarityDimensionScore
SimilarityMatchCandidate
FutureFieldGuardService
SimilarityFeatureVectorExtractor
SimilarityNineDimensionScoringService
SimilarityOutputRowBuilder
```

## 已落地真实链路

```text
JavaSimilarityMatchEngine
-> EngineExecutionTemplate
-> RuleVersionGuardService
-> EngineDataQualityGuard
-> EngineSnapshotLoadService
-> SimilarityFeatureVectorExtractor
-> SimilarityNineDimensionScoringService
-> SimilarityOutputRowBuilder
-> EngineSnapshotWriteService
-> ClickHouse:
   - historical_similarity_match
   - historical_similarity_factor_detail
-> algorithm_task_log
```

## 九维相似度已落地

```text
市场环境相似度：
1. MARKET_BREADTH         市场宽度相似 10%
2. TURNOVER_VOLUME        成交额/量能相似 10%
3. INDEX_POSITION         指数位置相似 10%

情绪周期结构相似度：
4. LIMIT_ECOLOGY          涨跌停生态相似 15%
5. LEADER_LADDER          连板梯队相似 10%
6. LOSS_EFFECT            亏钱效应相似 10%
7. STAGE_PATH             阶段演化路径相似 10%

主线龙头行为相似度：
8. MAINLINE_STRUCTURE     主线结构相似 13%
9. LEADER_FEEDBACK        龙头反馈相似 12%
```

## 总分公式已落地

```text
total_similarity_score =
市场环境相似度 * 30%
+ 情绪周期结构相似度 * 45%
+ 主线龙头行为相似度 * 25%
```

## 输出表

```text
historical_similarity_match
historical_similarity_factor_detail
```

每个历史样本会输出：

```text
1. historical_similarity_match 一行总分
2. historical_similarity_factor_detail 九行维度明细
```

## 防未来函数已经加硬守卫

新增：

```text
FutureFieldGuardService
```

匹配计算阶段禁止使用：

```text
future_1d_return
future_3d_return
future_5d_return
future_10d_return
max_drawdown
following_return
following_3d_return
following_5d_return
```

这些字段只能用于：

```text
1. 历史后续表现展示
2. 回测统计
3. 失败样本分析
```

不能参与 T 日相似度计算。

## 当前算法边界

1. 当前从 `historical_cycle_sample` 读取历史样本。
2. 当前只保留历史日期早于当前交易日的样本。
3. 当前默认取相似度 Top 30。
4. 当前按 `sample_type` 写入 match_type；缺失时默认为 `SINGLE_DAY`。
5. 当前不会伪造 future_* 后续表现字段。
6. 当前不会返回 Mock 相似样本。

## 新增SQL说明

```text
sql/10_similarity_match_engine_output_contract.sql
```

## 下一步建议

继续第七步第十一段：

```text
补齐 MainlineRecognitionEngine 真实算法：
mainline_strength_score公式、theme_strength_snapshot写入、mainline_daily_snapshot写入、mainline_switch_snapshot写入。
禁止“涨幅第一=主线”“涨停最多=主线”的静态硬判定。
```
