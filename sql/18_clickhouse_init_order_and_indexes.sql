-- 第七步第十七段：ClickHouse初始化顺序与ORDER BY建议
-- 说明：ClickHouse MergeTree 不使用传统二级索引作为主路径。
-- 核心优化点是 PARTITION BY / ORDER BY / TTL / projection。
-- 以下为各类快照表推荐排序键，实际DDL请与你现有字段对齐。

-- 推荐数据库
create database if not exists astock_analysis;

-- 行情与市场快照类
-- stock_daily_kline:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, stock_code)

-- market_factor_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope)

-- limit_up_down_ecology_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope)

-- 情绪周期类
-- emotion_stage_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, rule_version_id)

-- emotion_stage_score_detail:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, stage_code, rule_version_id)

-- stage_transition_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, from_stage, to_stage)

-- 相似行情类
-- historical_similarity_match:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, match_type, total_similarity_score)

-- historical_similarity_factor_detail:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, match_type, sample_id, dimension_code)

-- historical_cycle_sample:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, stage_code, sample_id)

-- 主线题材类
-- theme_daily_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, theme_code)

-- theme_strength_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, theme_code, rank_no)

-- mainline_daily_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, mainline_code, rank_no)

-- mainline_switch_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, old_mainline_code, new_mainline_code)

-- 板块类
-- sector_strength_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, sector_code, rank_no)

-- 龙头类
-- leader_daily_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, stock_code, leader_type, rank_no)

-- leader_ladder_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, board_height)

-- leader_drive_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, stock_code)

-- leader_negative_feedback:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, stock_code)

-- 模式条件类
-- buy_pattern_signal_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, pattern_code, stock_code, condition_status)

-- pattern_risk_veto_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, pattern_code, stock_code, risk_action)

-- 风控类
-- risk_signal_snapshot:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, risk_code)

-- risk_signal_detail:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, risk_source, risk_code)

-- 回测类
-- backtest_signal_detail:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, task_id, sample_date, sample_id)

-- backtest_performance_detail:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, task_id, layer_code, metric_name)

-- backtest_layer_stat:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, task_id, layer_code)

-- backtest_failure_case:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, market_scope, task_id, failure_type, sample_id)

-- Agent审计类
-- agent_audit_code_scan_detail:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, audit_task_id, issue_code, module_name)

-- agent_audit_data_lineage_detail:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, audit_task_id, page_code, vo_class_name, field_name)

-- agent_audit_rule_hit_detail:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, audit_task_id, rule_code)

-- agent_audit_release_gate_detail:
-- PARTITION BY toYYYYMM(trade_date)
-- ORDER BY (trade_date, audit_task_id, gate_code)
