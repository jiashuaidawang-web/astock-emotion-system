# 第七步第六段：页面专属多表Repository查询

生成日期：2026-06-29

## 本次完成内容

1. 新增多表快照聚合对象：
   - `PageSnapshotBundle`

2. 新增Bundle级Converter接口：
   - `PageBundleConverter<Q, V>`

3. 新增MySQL通用查询器：
   - `MysqlQueryExecutor`
   - `MysqlQueryExecutorImpl`

4. 15个页面全部补齐页面专属多表Repository：
   - `*PageRepository`
   - `*MultiTableRepository`
   - `*PageSql`

5. 15个页面Converter全部升级为：
   - 从 `PageSnapshotBundle` 读取多表数据
   - 顶层字段从主表读取
   - 嵌套VO按业务含义分派到对应表
   - List<NestedVO> 从对应表多行转换
   - 没有源字段时保持空，不Mock

6. Aggregator升级为：
   - 先做数据完整性检查
   - 再查询页面专属多表Repository
   - 再交给Converter填充完整PageVO

7. 两个详情页同步升级：
   - `LeaderProfilePageVO`
   - `BacktestReportDetailVO`

## 15个页面多表映射

| 页面 | PageVO | ClickHouse表 | MySQL表 |
|---|---|---|---|
| PAGE_01_MARKET_DASHBOARD | MarketDashboardVO | market_factor_snapshot, limit_up_down_ecology_snapshot, emotion_stage_snapshot, risk_signal_snapshot, historical_similarity_match, mainline_daily_snapshot, leader_daily_snapshot, buy_pattern_signal_snapshot, pattern_risk_veto_snapshot | - |
| PAGE_02_HISTORICAL_SIMILARITY | HistoricalSimilarityPageVO | market_factor_snapshot, emotion_stage_snapshot, historical_similarity_match, historical_similarity_factor_detail, historical_following_performance, historical_cycle_sample | similarity_rule_version, cycle_sample_confirm |
| PAGE_03_EMOTION_STATE_MACHINE | EmotionCycleStateMachineVO | emotion_stage_snapshot, emotion_stage_score_detail, stage_transition_snapshot, historical_similarity_match, historical_cycle_sample, historical_following_performance | manual_stage_adjustment, emotion_stage_rule_version |
| PAGE_04_CYCLE_SAMPLE_LIBRARY | HistoricalCycleSamplePageVO | historical_cycle_sample, historical_cycle_sample_factor, historical_following_performance, emotion_stage_snapshot, mainline_daily_snapshot, leader_daily_snapshot, risk_signal_snapshot | cycle_mining_task, cycle_sample_confirm |
| PAGE_05_MAINLINE_RADAR | MainlineRadarPageVO | mainline_daily_snapshot, theme_strength_snapshot, theme_daily_snapshot, mainline_switch_snapshot, leader_daily_snapshot, leader_drive_snapshot, risk_signal_detail, mainline_similarity_match | theme_definition, theme_stock_mapping, mainline_rule_version |
| PAGE_06_SECTOR_STRENGTH | SectorStrengthPageVO | sector_strength_snapshot, sector_daily_snapshot, sector_stock_mapping_snapshot, mainline_daily_snapshot, theme_strength_snapshot, leader_daily_snapshot, risk_signal_detail | sector_rule_version, theme_definition |
| PAGE_07_LEADER_LADDER | LeaderLadderPageVO | leader_daily_snapshot, leader_ladder_snapshot, leader_drive_snapshot, leader_negative_feedback, trend_leader_snapshot, mainline_daily_snapshot, risk_signal_snapshot, buy_pattern_signal_snapshot | leader_rule_version, leader_type_definition, leader_manual_confirm |
| PAGE_08_LEADER_PROFILE | LeaderProfilePageVO | leader_daily_snapshot, leader_ladder_snapshot, leader_drive_snapshot, leader_negative_feedback, trend_leader_snapshot, leader_similarity_match, mainline_daily_snapshot, stock_daily_kline, buy_pattern_signal_snapshot, risk_signal_detail | leader_manual_confirm, leader_type_definition |
| PAGE_09_PATTERN_CONDITION | PatternConditionPageVO | buy_pattern_signal_snapshot, pattern_risk_veto_snapshot, risk_signal_snapshot, risk_signal_detail, leader_daily_snapshot, mainline_daily_snapshot, trend_leader_snapshot, pattern_backtest_result | buy_pattern_definition, buy_pattern_stage_matrix, buy_pattern_rule_config, pattern_risk_binding |
| PAGE_10_RISK_CONTROL | RiskControlPageVO | risk_signal_snapshot, risk_signal_detail, pattern_risk_veto_snapshot, leader_negative_feedback, mainline_daily_snapshot, historical_cycle_sample, risk_similarity_match | risk_action_matrix, risk_rule_version, data_quality_check_log |
| PAGE_11_BACKTEST_LAB | BacktestLabPageVO | backtest_signal_detail, backtest_performance_detail, backtest_layer_stat, backtest_failure_case, historical_cycle_sample | backtest_task, backtest_task_param, backtest_task_progress, backtest_preset_template, rule_version, data_quality_check_log |
| PAGE_12_BACKTEST_REPORT | BacktestReportDetailVO | backtest_signal_detail, backtest_performance_detail, backtest_layer_stat, backtest_failure_case, pattern_backtest_result | backtest_report, backtest_task, backtest_task_param, backtest_rule_binding, rule_version, data_quality_check_log |
| PAGE_13_DAILY_REVIEW | DailyReviewWorkbenchVO | market_factor_snapshot, emotion_stage_snapshot, historical_similarity_match, mainline_daily_snapshot, sector_strength_snapshot, leader_daily_snapshot, buy_pattern_signal_snapshot, risk_signal_snapshot, backtest_layer_stat | daily_review_record, daily_review_section, daily_review_checklist, daily_review_audit_log, manual_stage_adjustment, cycle_sample_confirm |
| PAGE_14_RULE_VERSION | RuleVersionManagePageVO | backtest_layer_stat, backtest_failure_case | rule_definition, rule_version, rule_publish_check_log, rule_version_audit_log, backtest_report, agent_audit_result |
| PAGE_15_AGENT_AUDIT | AgentAuditDashboardVO | agent_audit_code_scan_detail, agent_audit_data_lineage_detail, agent_audit_rule_hit_detail, agent_audit_release_gate_detail | agent_audit_task, agent_audit_result, agent_audit_issue, agent_audit_rule_version, agent_release_gate_check |

## 当前真实链路

```text
Controller
-> QueryService
-> Aggregator / DetailService
-> DataQualityQueryService
-> PageRepository
-> PageSnapshotBundle
-> PageBundleConverter
-> 完整 PageVO
```

## 关键红线

1. Repository只查真实表，不返回Mock。
2. Converter只做字段映射，不写评分公式。
3. Aggregator只做编排，不写核心业务判断。
4. future_*字段只允许用于历史统计、回测报告、历史样本展示。
5. 不输出买入、卖出、持有、推荐、目标价。
6. 没有源字段时保持空，不准用假数据补齐。
7. 字段必须写入 `page_contract_field_lineage` 血缘表。

## 下一步建议

进入第七步第七段：

```text
补齐核心Engine的真实执行骨架：
EmotionStageRecognitionEngine、SimilarityMatchEngine、MainlineRecognitionEngine、LeaderRecognitionEngine、PatternConditionEngine、RiskControlEngine、BacktestExecutor、AgentAuditExecutor。
要求每个Engine读取rule_version，做数据完整性校验，执行结果落库，不允许只定义接口。
```
