# 第八步：数据库 DDL 最终对齐

生成日期：2026-06-29

## 本次完成内容

这次把第七步所有 Engine 输出契约 SQL 合并为完整可执行 DDL：

```text
1. MySQL 业务库完整建表脚本
2. ClickHouse 分析库完整建表脚本
3. 规则版本默认初始化脚本
4. 执行顺序说明
5. Engine 写入字段与目标表字段对齐
```

## 新增文件

```text
sql/20_mysql_full_schema.sql
sql/21_clickhouse_full_schema.sql
sql/22_rule_version_seed.sql
README_STEP8_DDL.md
```

## MySQL 覆盖表

```text
rule_definition
rule_version
rule_version_audit_log
algorithm_task_log
data_quality_check_log
page_contract_field_lineage
cycle_sample_confirm
manual_stage_adjustment
pattern_risk_binding
buy_pattern_stage_matrix
buy_pattern_rule_config
risk_action_matrix
```

## ClickHouse 覆盖表

### 基础输入快照

```text
stock_daily_kline
market_factor_snapshot
limit_up_down_ecology_snapshot
theme_daily_snapshot
sector_strength_snapshot
historical_cycle_sample
```

### Engine 输出快照

```text
emotion_stage_snapshot
emotion_stage_score_detail
stage_transition_snapshot
historical_similarity_match
historical_similarity_factor_detail
theme_strength_snapshot
mainline_daily_snapshot
mainline_switch_snapshot
leader_daily_snapshot
leader_ladder_snapshot
leader_drive_snapshot
leader_negative_feedback
buy_pattern_signal_snapshot
pattern_risk_veto_snapshot
risk_signal_snapshot
risk_signal_detail
backtest_signal_detail
backtest_performance_detail
backtest_layer_stat
backtest_failure_case
agent_audit_code_scan_detail
agent_audit_data_lineage_detail
agent_audit_rule_hit_detail
agent_audit_release_gate_detail
```

## 推荐执行顺序

### 1. MySQL

```bash
mysql -uroot -p -e "create database if not exists astock_business default character set utf8mb4 collate utf8mb4_unicode_ci;"
mysql -uroot -p astock_business < sql/20_mysql_full_schema.sql
mysql -uroot -p astock_business < sql/22_rule_version_seed.sql
```

如果你已经生成了字段血缘种子：

```bash
mysql -uroot -p astock_business < sql/07_page_field_lineage_seed.sql
```

### 2. ClickHouse

```bash
clickhouse-client --multiquery < sql/21_clickhouse_full_schema.sql
```

## 对齐边界

这次DDL保证：

```text
1. EngineOutputRowBuilder 写入字段在DDL中存在。
2. Repository 查询用到的核心字段在DDL中存在。
3. future_* 字段只放在 historical_cycle_sample 和 backtest_* 相关表中。
4. 模式条件表只输出条件状态，不包含交易建议字段。
5. Agent审计表覆盖代码扫描、字段血缘、规则命中、发布闸门。
```

## 后续仍需你真实数据库验证

```text
1. ClickHouse Decimal / UInt / String 类型是否与你实际数据源一致。
2. 真实行情采集字段是否需要更多兼容字段。
3. 是否要把部分 String JSON 改成 Object('json') 或 JSON 类型。
4. 是否要对大表增加 TTL。
5. 是否要补充 projection。
6. 是否需要按沪深北市场拆分 market_scope。
```

## 下一步建议

进入第九步：

```text
生成真实数据初始化与最小跑通样本：
插入一组 MySQL 规则版本、字段血缘、ClickHouse T日市场快照、题材快照、个股快照，
然后按 Engine 顺序跑出完整 15 页面可查询数据。
```
