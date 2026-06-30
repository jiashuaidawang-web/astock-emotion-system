# 第七步第九段：EmotionStageRecognitionEngine 历史增强

生成日期：2026-06-29

## 本次完成内容

这次继续增强 `EmotionStageRecognitionEngine`，把历史库、人工确认、人工修正、过去N日路径真正接入评分过程。

## 新增类

```text
EmotionHistoricalContext
EmotionHistoricalContextRepository
EmotionHistoricalContextRepositoryImpl
EmotionHistoricalContextSql
EmotionHistoricalScoreEnhancer
```

## 增强后的真实链路

```text
JavaEmotionStageRecognitionEngine
-> EngineExecutionTemplate
-> 当日关键数据完整性校验
-> 加载 market_factor_snapshot
-> 加载 limit_up_down_ecology_snapshot
-> EmotionHistoricalContextRepository
   -> historical_cycle_sample
   -> cycle_sample_confirm
   -> manual_stage_adjustment
   -> 过去N日 stage_transition_snapshot
-> EmotionMarketFeatureExtractor
-> EmotionStageScoringService
-> EmotionHistoricalScoreEnhancer
-> EmotionStageOutputRowBuilder
-> 写入:
   - emotion_stage_score_detail
   - emotion_stage_snapshot
   - stage_transition_snapshot
```

## 这次增强的评分项

### 1. historical_sample_similarity_score

现在从 `historical_cycle_sample` 中读取同阶段历史样本：

```text
同阶段样本 sample_confidence
+ similarity_score
+ cycle_sample_confirm 人工确认加权
```

如果历史样本为空，不伪造历史结果，而是退化为基于因子匹配分的保守分。

### 2. stage_path_match_score

现在从过去N日 `stage_transition_snapshot` 读取历史路径：

```text
to_stage 命中当前候选阶段比例
+ transition_score / transition_probability 均值
```

如果过去N日路径为空，不伪造路径，而是使用当前市场连续性特征做保守路径分。

### 3. following_validation_score

现在从历史样本中的：

```text
future_3d_return
max_drawdown
```

做历史演化验证分。

注意：这些字段只来自历史样本库，不读取当前T日之后的数据，不构成未来函数。

### 4. manual_sample_correction_score

现在从：

```text
manual_stage_adjustment
cycle_sample_confirm
```

读取人工修正与确认信号，对对应阶段做有限修正。

## stage_score 仍保持原公式

```text
stage_score =
因子分位匹配分 * 30%
+ 历史样本相似分 * 35%
+ 阶段路径匹配分 * 20%
+ 后续演化验证分 * 10%
+ 人工确认样本修正 * 5%
```

## 关键边界

1. 当日基础判断只强制依赖：
   - market_factor_snapshot
   - limit_up_down_ecology_snapshot

2. 历史增强项缺失不阻断识别：
   - historical_cycle_sample
   - cycle_sample_confirm
   - manual_stage_adjustment
   - stage_transition_snapshot

3. 历史增强项缺失时：
   - 不Mock
   - 不伪造样本
   - 在 evidence_json / risk_json 里体现样本缺失状态

4. 仍然不使用静态阈值硬判定：
   - 不写 `if(limitUpCount <= 30) return ICE_POINT`
   - 仍然对10个阶段全部评分

## 新增SQL说明

```text
sql/09_emotion_stage_history_enhancement_contract.sql
```

## 下一步建议

继续第七步第十段：

```text
补齐 SimilarityMatchEngine 真实算法：
九维相似度计算、historical_similarity_match 写入、historical_similarity_factor_detail 写入，严格防止 future_* 参与T日匹配。
```
