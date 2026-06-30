# 第七步第十一段：MainlineRecognitionEngine 主线识别真实算法

生成日期：2026-06-29

## 本次完成内容

这次把 `MainlineRecognitionEngine` 从执行骨架推进到真实算法落地。

## 新增类

```text
MainlineThemeFeature
MainlineRecognitionContext
MainlineScore
MainlineRecognitionContextRepository
MainlineRecognitionContextRepositoryImpl
MainlineRecognitionContextSql
MainlineFeatureExtractor
MainlineStrengthScoringService
MainlineOutputRowBuilder
```

## 已落地真实链路

```text
JavaMainlineRecognitionEngine
-> EngineExecutionTemplate
-> RuleVersionGuardService
-> EngineDataQualityGuard
-> EngineSnapshotLoadService
-> MainlineRecognitionContextRepository
   -> sector_strength_snapshot
   -> leader_daily_snapshot
   -> 过去N日 mainline_daily_snapshot
-> MainlineFeatureExtractor
-> MainlineStrengthScoringService
-> MainlineOutputRowBuilder
-> EngineSnapshotWriteService
-> ClickHouse:
   - theme_strength_snapshot
   - mainline_daily_snapshot
   - mainline_switch_snapshot
-> algorithm_task_log
```

## mainline_strength_score 公式已落地

```text
mainline_strength_score =
涨停聚集强度 * 20%
+ 成交额集中强度 * 20%
+ 持续性强度 * 15%
+ 梯队完整度 * 15%
+ 龙头带动性 * 20%
+ 情绪周期匹配度 * 10%
```

## 本实现明确禁止两类伪主线判定

```text
1. 涨幅第一 = 主线
2. 涨停最多 = 主线
```

当前实现不是取单字段第一，而是：

```text
1. 每个题材都提取连续特征；
2. 每个题材都计算六维评分；
3. 再按综合 mainline_strength_score 排序；
4. 生成 theme_strength_snapshot；
5. 生成 mainline_daily_snapshot；
6. 与上一期主线对比生成 mainline_switch_snapshot。
```

## 输出表

```text
theme_strength_snapshot
mainline_daily_snapshot
mainline_switch_snapshot
```

## 主线切换逻辑

当前版本会读取过去 `mainline_daily_snapshot` 的最近主线：

```text
old_mainline
vs
current_top_mainline
```

然后输出：

```text
NO_PREVIOUS_MAINLINE
NO_SWITCH
SUSPECTED_SWITCH
```

注意：当前是切换识别骨架，不会直接用一次排名变化认定“切换确认”。后续可以叠加连续天数、强度差、风险确认。

## 关键输入

强制输入：

```text
theme_daily_snapshot
emotion_stage_snapshot
```

增强上下文：

```text
sector_strength_snapshot
leader_daily_snapshot
过去N日 mainline_daily_snapshot
```

增强上下文缺失不阻断基础识别，但会影响对应维度得分。

## 新增SQL说明

```text
sql/11_mainline_recognition_engine_output_contract.sql
```

## 下一步建议

继续第七步第十二段：

```text
补齐 LeaderRecognitionEngine 真实算法：
leader_score公式、leader_daily_snapshot写入、leader_ladder_snapshot写入、leader_drive_snapshot写入、leader_negative_feedback写入。
继续禁止“最高板=市场总龙头”的静态硬判定。
```
