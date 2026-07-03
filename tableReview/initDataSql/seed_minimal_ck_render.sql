-- =========================================================
-- A股情绪周期系统：最小页面渲染样本数据 ClickHouse
-- 说明：仅用于开发/联调页面渲染，不代表真实行情数据或任何交易建议。
-- 覆盖日期：2026-06-30 和 today()
-- 执行：docker exec -i astock-clickhouse clickhouse-client --user default --password pamirs@123 --multiquery < sql/seed_minimal_ck_render.sql
-- =========================================================
CREATE DATABASE IF NOT EXISTS astock_analysis;

-- 清理 astock_analysis.stock_daily_kline 历史样本
ALTER TABLE astock_analysis.stock_daily_kline DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.market_factor_snapshot 历史样本
ALTER TABLE astock_analysis.market_factor_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.limit_up_down_ecology_snapshot 历史样本
ALTER TABLE astock_analysis.limit_up_down_ecology_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.emotion_stage_snapshot 历史样本
ALTER TABLE astock_analysis.emotion_stage_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.emotion_stage_score_detail 历史样本
ALTER TABLE astock_analysis.emotion_stage_score_detail DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.stage_transition_snapshot 历史样本
ALTER TABLE astock_analysis.stage_transition_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.historical_cycle_sample 历史样本
ALTER TABLE astock_analysis.historical_cycle_sample DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.historical_similarity_match 历史样本
ALTER TABLE astock_analysis.historical_similarity_match DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.historical_similarity_factor_detail 历史样本
ALTER TABLE astock_analysis.historical_similarity_factor_detail DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.theme_daily_snapshot 历史样本
ALTER TABLE astock_analysis.theme_daily_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.theme_strength_snapshot 历史样本
ALTER TABLE astock_analysis.theme_strength_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.mainline_daily_snapshot 历史样本
ALTER TABLE astock_analysis.mainline_daily_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.mainline_switch_snapshot 历史样本
ALTER TABLE astock_analysis.mainline_switch_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.sector_strength_snapshot 历史样本
ALTER TABLE astock_analysis.sector_strength_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.leader_daily_snapshot 历史样本
ALTER TABLE astock_analysis.leader_daily_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.leader_ladder_snapshot 历史样本
ALTER TABLE astock_analysis.leader_ladder_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.leader_drive_snapshot 历史样本
ALTER TABLE astock_analysis.leader_drive_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.leader_negative_feedback 历史样本
ALTER TABLE astock_analysis.leader_negative_feedback DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.buy_pattern_signal_snapshot 历史样本
ALTER TABLE astock_analysis.buy_pattern_signal_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.pattern_risk_veto_snapshot 历史样本
ALTER TABLE astock_analysis.pattern_risk_veto_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.risk_signal_snapshot 历史样本
ALTER TABLE astock_analysis.risk_signal_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.risk_signal_detail 历史样本
ALTER TABLE astock_analysis.risk_signal_detail DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.backtest_signal_detail 历史样本
ALTER TABLE astock_analysis.backtest_signal_detail DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.backtest_performance_detail 历史样本
ALTER TABLE astock_analysis.backtest_performance_detail DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.backtest_layer_stat 历史样本
ALTER TABLE astock_analysis.backtest_layer_stat DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.backtest_failure_case 历史样本
ALTER TABLE astock_analysis.backtest_failure_case DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.agent_audit_code_scan_detail 历史样本
ALTER TABLE astock_analysis.agent_audit_code_scan_detail DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.agent_audit_data_lineage_detail 历史样本
ALTER TABLE astock_analysis.agent_audit_data_lineage_detail DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.agent_audit_rule_hit_detail 历史样本
ALTER TABLE astock_analysis.agent_audit_rule_hit_detail DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.agent_audit_release_gate_detail 历史样本
ALTER TABLE astock_analysis.agent_audit_release_gate_detail DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.historical_cycle_sample_factor 历史样本
ALTER TABLE astock_analysis.historical_cycle_sample_factor DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.historical_following_performance 历史样本
ALTER TABLE astock_analysis.historical_following_performance DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.leader_similarity_match 历史样本
ALTER TABLE astock_analysis.leader_similarity_match DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.mainline_similarity_match 历史样本
ALTER TABLE astock_analysis.mainline_similarity_match DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.pattern_backtest_result 历史样本
ALTER TABLE astock_analysis.pattern_backtest_result DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.risk_similarity_match 历史样本
ALTER TABLE astock_analysis.risk_similarity_match DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.sector_daily_snapshot 历史样本
ALTER TABLE astock_analysis.sector_daily_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.sector_stock_mapping_snapshot 历史样本
ALTER TABLE astock_analysis.sector_stock_mapping_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';
-- 清理 astock_analysis.trend_leader_snapshot 历史样本
ALTER TABLE astock_analysis.trend_leader_snapshot DELETE WHERE trade_date IN (toDate('2026-06-30'), today()) AND market_scope = 'A_SHARE';

-- astock_analysis.stock_daily_kline
INSERT INTO astock_analysis.stock_daily_kline (trade_date, market_scope, stock_code, stock_name, code, name, exchange, sector_code, sector_name, industry_code, industry_name, theme_code, theme_name, mainline_code, mainline_name, open_price, high_price, low_price, close_price, pre_close_price, pct_change, change_pct, change_amount, volume, turnover_amount, amount, amplitude, volume_ratio, turnover_rate, pe_dynamic, pb, roe, total_market_value, float_market_value, is_limit_up, limit_up, is_limit_down, is_broken_board, broken_board, board_height, consecutive_board_height, limit_up_days, negative_feedback_score, drawdown_score, features, created_at, updated_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', '000001', '平安银行', '000001', '平安银行', 'SZSE', 'SECTOR_AI_APP', 'AI应用', 'STOCK_DAILY_KLINE_CODE', 'stock_daily_kline样本', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 12.340000, 12.340000, 12.340000, 12.340000, 12.340000, 2.350000, 2.350000, 120000000.000000, 1000000, 120000000.000000, 120000000.000000, 1.000000, 0.860000, 120000000.000000, 1.000000, 1.000000, 1.000000, 1.000000, 1.000000, 1, 100, 1, 3, 3, 3, 3, 100, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', now(), now()),
(today(), 'A_SHARE', '300750', '宁德时代', '300750', '宁德时代', 'SZSE', 'SECTOR_AI_APP', 'AI应用', 'STOCK_DAILY_KLINE_CODE', 'stock_daily_kline样本', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 12.340000, 12.340000, 12.340000, 12.340000, 12.340000, 2.350000, 2.350000, 120000000.000000, 1000000, 120000000.000000, 120000000.000000, 1.000000, 0.860000, 120000000.000000, 1.000000, 1.000000, 1.000000, 1.000000, 1.000000, 1, 100, 1, 3, 3, 3, 3, 100, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', now(), now());

-- astock_analysis.market_factor_snapshot
INSERT INTO astock_analysis.market_factor_snapshot (trade_date, market_scope, rise_count, up_count, rising_count, fall_count, down_count, falling_count, flat_count, unchanged_count, market_breadth_score, breadth_score, profit_effect_score, earning_effect_score, loss_effect_score, loss_pressure_score, turnover_percentile, amount_percentile, turnover_heat_score, index_position_score, index_percentile, index_location_score, index_fund_risk_score, index_pressure_score, features, created_at, updated_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 12, 12, 12, 12, 12, 12, 12, 12, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 120000000.000000, 120000000.000000, 120000000.000000, 82.500000, 1.000000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', now(), now()),
(today(), 'A_SHARE', 12, 12, 12, 12, 12, 12, 12, 12, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 120000000.000000, 120000000.000000, 120000000.000000, 82.500000, 1.000000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', now(), now());

-- astock_analysis.limit_up_down_ecology_snapshot
INSERT INTO astock_analysis.limit_up_down_ecology_snapshot (trade_date, market_scope, limit_up_count, zt_count, limit_down_count, dt_count, break_board_count, broken_board_count, max_board_height, highest_board_height, max_board_height_score, ladder_height_score, limit_up_ecology_score, limit_eco_score, limit_ecology_score, limit_down_pressure_score, break_board_pressure_score, features, created_at, updated_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 12, 12, 12, 12, 12, 12, 3, 3, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', now(), now()),
(today(), 'A_SHARE', 12, 12, 12, 12, 12, 12, 3, 3, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', now(), now());

-- astock_analysis.emotion_stage_snapshot
INSERT INTO astock_analysis.emotion_stage_snapshot (trade_date, market_scope, task_id, rule_version_id, primary_stage, primary_stage_name, stage_code, emotion_stage, stage_confidence, stage_score, second_candidate_stage, second_candidate_stage_name, third_candidate_stage, third_candidate_stage_name, evidence_json, risk_json, features, data_complete, created_at, updated_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'seed_primary_stage', 'emotion_stage_snapshot样本', 'STAGE_REPAIR', 'REPAIR', 1.000000, 82.500000, 'seed_second_candidate_stage', 'emotion_stage_snapshot样本', 'seed_third_candidate_stage', 'emotion_stage_snapshot样本', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', 100, now(), now()),
(today(), 'A_SHARE', 100, 100, 'seed_primary_stage', 'emotion_stage_snapshot样本', 'STAGE_REPAIR', 'REPAIR', 1.000000, 82.500000, 'seed_second_candidate_stage', 'emotion_stage_snapshot样本', 'seed_third_candidate_stage', 'emotion_stage_snapshot样本', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', 100, now(), now());

-- astock_analysis.emotion_stage_score_detail
INSERT INTO astock_analysis.emotion_stage_score_detail (trade_date, market_scope, task_id, rule_version_id, stage_code, stage_name, stage_score, rank_no, factor_percentile_match_score, historical_sample_similarity_score, stage_path_match_score, following_validation_score, manual_sample_correction_score, evidence_json, risk_json, features, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'STAGE_REPAIR', 'emotion_stage_score_detail样本', 82.500000, 100, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 'STAGE_REPAIR', 'emotion_stage_score_detail样本', 82.500000, 100, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.stage_transition_snapshot
INSERT INTO astock_analysis.stage_transition_snapshot (trade_date, market_scope, task_id, rule_version_id, from_stage, to_stage, primary_stage, transition_probability, transition_score, evidence_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'seed_from_stage', 'seed_to_stage', 'seed_primary_stage', 1.000000, 82.500000, '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 'seed_from_stage', 'seed_to_stage', 'seed_primary_stage', 1.000000, 82.500000, '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.historical_cycle_sample
INSERT INTO astock_analysis.historical_cycle_sample (trade_date, market_scope, id, sample_id, stage_code, stage_type, emotion_stage, primary_stage, sample_type, sample_confidence, confidence, stage_confidence, similarity_score, sample_similarity_score, stage_score, market_breadth_score, breadth_score, turnover_percentile, turnover_score, turnover_heat_score, index_position_score, index_percentile, limit_ecology_score, limit_up_ecology_score, leader_ladder_score, ladder_height_score, loss_effect_score, loss_pressure_score, stage_path_score, mainline_structure_score, mainline_strength_score, leader_feedback_score, negative_feedback_score, pattern_code, stock_code, stock_name, mainline_code, theme_code, mainline_name, theme_name, future_1d_return, future1d_return, future_3d_return, future3d_return, following_3d_return, future_5d_return, future5d_return, future_10d_return, future10d_return, max_drawdown, following_max_drawdown, evidence_json, feature_json, features, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'STAGE_REPAIR', 'REPAIR', 'REPAIR', 'seed_primary_stage', 'HISTORICAL_REPLAY', 1.000000, 1.000000, 1.000000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 120000000.000000, 120000000.000000, 120000000.000000, 82.500000, 1.000000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 'PATTERN_RESEARCH_01', '000001', '平安银行', 'ML_AI_APP', 'THEME_AI_APP', 'AI应用主线', 'AI应用', 2.350000, 2.350000, 2.350000, 2.350000, 2.350000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 'STAGE_REPAIR', 'REPAIR', 'REPAIR', 'seed_primary_stage', 'HISTORICAL_REPLAY', 1.000000, 1.000000, 1.000000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 120000000.000000, 120000000.000000, 120000000.000000, 82.500000, 1.000000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 'PATTERN_RESEARCH_01', '300750', '宁德时代', 'ML_AI_APP', 'THEME_AI_APP', 'AI应用主线', 'AI应用', 2.350000, 2.350000, 2.350000, 2.350000, 2.350000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.historical_similarity_match
INSERT INTO astock_analysis.historical_similarity_match (trade_date, market_scope, task_id, rule_version_id, match_type, sample_id, historical_trade_date, historical_stage, market_environment_similarity_score, emotion_cycle_similarity_score, theme_leader_similarity_score, total_similarity_score, dimension_score_json, reference_text, risk_text, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'MARKET', 100, toDate('2026-06-30'), 'REPAIR', 82.500000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', 'seed_reference_text', 'seed_risk_text', now()),
(today(), 'A_SHARE', 100, 100, 'MARKET', 100, today(), 'REPAIR', 82.500000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', 'seed_reference_text', 'seed_risk_text', now());

-- astock_analysis.historical_similarity_factor_detail
INSERT INTO astock_analysis.historical_similarity_factor_detail (trade_date, market_scope, task_id, rule_version_id, match_type, sample_id, historical_trade_date, dimension_code, dimension_name, dimension_group_code, dimension_weight, current_value, historical_value, dimension_similarity_score, evidence_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'MARKET', 100, toDate('2026-06-30'), 'DIM_SENTIMENT', '情绪维度', 'HISTORICAL_SIMILARITY_FA_CODE', 1.000000, 1.000000, 1.000000, 82.500000, '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 'MARKET', 100, today(), 'DIM_SENTIMENT', '情绪维度', 'HISTORICAL_SIMILARITY_FA_CODE', 1.000000, 1.000000, 1.000000, 82.500000, '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.theme_daily_snapshot
INSERT INTO astock_analysis.theme_daily_snapshot (trade_date, market_scope, theme_code, theme_name, theme_type, sector_code, sector_name, mainline_code, mainline_name, pct_change, change_pct, limit_up_count, zt_count, stock_count, component_count, turnover_amount, amount, turnover_ratio, turnover_amount_ratio, continuity_days, continuous_days, active_days, max_board_height, highest_board_height, leader_count, core_stock_count, features, created_at, updated_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 'THEME_AI_APP', 'AI应用', 'APPLICATION', 'SECTOR_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 2.350000, 2.350000, 1.000000, 1.000000, 1.000000, 1.000000, 120000000.000000, 120000000.000000, 120000000.000000, 120000000.000000, 1.000000, 1.000000, 1.000000, 1.000000, 1.000000, 1.000000, 1.000000, '{"seed":true,"purpose":"page_render"}', now(), now()),
(today(), 'A_SHARE', 'THEME_AI_APP', 'AI应用', 'APPLICATION', 'SECTOR_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 2.350000, 2.350000, 1.000000, 1.000000, 1.000000, 1.000000, 120000000.000000, 120000000.000000, 120000000.000000, 120000000.000000, 1.000000, 1.000000, 1.000000, 1.000000, 1.000000, 1.000000, 1.000000, '{"seed":true,"purpose":"page_render"}', now(), now());

-- astock_analysis.theme_strength_snapshot
INSERT INTO astock_analysis.theme_strength_snapshot (trade_date, market_scope, task_id, rule_version_id, theme_code, theme_name, theme_type, theme_strength_score, strength_score, rank_no, limit_up_cluster_score, turnover_concentration_score, continuity_score, ladder_integrity_score, leader_drive_score, emotion_match_score, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'THEME_AI_APP', 'AI应用', 'APPLICATION', 82.500000, 82.500000, 100, 82.500000, 120000000.000000, 82.500000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 'THEME_AI_APP', 'AI应用', 'APPLICATION', 82.500000, 82.500000, 100, 82.500000, 120000000.000000, 82.500000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.mainline_daily_snapshot
INSERT INTO astock_analysis.mainline_daily_snapshot (trade_date, market_scope, task_id, rule_version_id, mainline_id, mainline_code, mainline_name, theme_code, theme_name, mainline_status, lifecycle_stage, theme_role, mainline_strength_score, strength_score, rank_no, limit_up_cluster_score, turnover_concentration_score, continuity_score, ladder_integrity_score, leader_drive_score, emotion_match_score, mainline_decay_risk_score, risk_score, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 'ML_AI_APP', 'AI应用主线', 'THEME_AI_APP', 'AI应用', 'seed_mainline_status', 'seed_lifecycle_stage', 'seed_theme_role', 82.500000, 82.500000, 100, 82.500000, 120000000.000000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 'ML_AI_APP', 'AI应用主线', 'THEME_AI_APP', 'AI应用', 'seed_mainline_status', 'seed_lifecycle_stage', 'seed_theme_role', 82.500000, 82.500000, 100, 82.500000, 120000000.000000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.mainline_switch_snapshot
INSERT INTO astock_analysis.mainline_switch_snapshot (trade_date, market_scope, task_id, rule_version_id, old_mainline_code, old_mainline_name, new_mainline_code, new_mainline_name, switch_status, switch_score, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'ML_OLD', '旧主线样本', 'ML_AI_APP', 'AI应用主线', 'seed_switch_status', 82.500000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 'ML_OLD', '旧主线样本', 'ML_AI_APP', 'AI应用主线', 'seed_switch_status', 82.500000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.sector_strength_snapshot
INSERT INTO astock_analysis.sector_strength_snapshot (trade_date, market_scope, sector_code, sector_name, theme_code, theme_name, sector_type, pct_change, change_pct, limit_up_count, stock_count, turnover_amount, turnover_ratio, continuity_days, max_board_height, leader_count, sector_strength_score, strength_score, rank_no, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'seed_sector_type', 2.350000, 2.350000, 1.000000, 1.000000, 120000000.000000, 120000000.000000, 1.000000, 1.000000, 1.000000, 82.500000, 82.500000, 100, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'seed_sector_type', 2.350000, 2.350000, 1.000000, 1.000000, 120000000.000000, 120000000.000000, 1.000000, 1.000000, 1.000000, 82.500000, 82.500000, 100, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.leader_daily_snapshot
INSERT INTO astock_analysis.leader_daily_snapshot (trade_date, market_scope, task_id, rule_version_id, stock_code, stock_name, sector_code, sector_name, mainline_code, mainline_name, theme_code, theme_name, leader_type, leader_status, leader_score, rank_no, recognition_score, mainline_relation_score, drive_score, leader_drive_score, strength_score, support_score, continuity_score, risk_feedback_score, negative_feedback_score, leader_negative_feedback_score, board_height, limit_up, broken_board, pct_change, turnover_amount, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, '000001', '平安银行', 'SECTOR_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'THEME_AI_APP', 'AI应用', 'TREND_LEADER', 'seed_leader_status', 82.500000, 100, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 100, 3, 2.350000, 120000000.000000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, '300750', '宁德时代', 'SECTOR_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'THEME_AI_APP', 'AI应用', 'TREND_LEADER', 'seed_leader_status', 82.500000, 100, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 100, 3, 2.350000, 120000000.000000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.leader_ladder_snapshot
INSERT INTO astock_analysis.leader_ladder_snapshot (trade_date, market_scope, task_id, rule_version_id, board_height, stock_count, top_stock_code, top_stock_name, top_leader_score, leader_type, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 1.000000, 12, 'LEADER_LADDER_SNAPSHOT_CODE', 'leader_ladder_snapshot样本', 82.500000, 'TREND_LEADER', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 1.000000, 12, 'LEADER_LADDER_SNAPSHOT_CODE', 'leader_ladder_snapshot样本', 82.500000, 'TREND_LEADER', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.leader_drive_snapshot
INSERT INTO astock_analysis.leader_drive_snapshot (trade_date, market_scope, task_id, rule_version_id, stock_code, stock_name, sector_drive_score, mainline_drive_score, emotion_drive_score, fund_drive_score, leader_drive_score, drive_score, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, '000001', '平安银行', 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, '300750', '宁德时代', 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.leader_negative_feedback
INSERT INTO astock_analysis.leader_negative_feedback (trade_date, market_scope, task_id, rule_version_id, stock_code, stock_name, leader_type, negative_feedback_score, broken_board, limit_down, impact_mainline, impact_emotion_cycle, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, '000001', '平安银行', 'TREND_LEADER', 82.500000, 3, 100, 100, 100, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, '300750', '宁德时代', 'TREND_LEADER', 82.500000, 3, 100, 100, 100, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.buy_pattern_signal_snapshot
INSERT INTO astock_analysis.buy_pattern_signal_snapshot (trade_date, market_scope, task_id, rule_version_id, pattern_code, pattern_name, stock_code, stock_name, watch_object_type, leader_type, leader_status, mainline_code, mainline_name, emotion_stage, condition_status, condition_score, pattern_condition_score, cycle_admission_score, mainline_valid_score, leader_position_score, trigger_score, backtest_support_score, manual_correction_score, risk_veto, risk_veto_reason, invalidated, invalidated_reason, allow_condition_met_display, signal_text, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'PATTERN_RESEARCH_01', '条件观察模式', '000001', '平安银行', 'seed_watch_object_type', 'TREND_LEADER', 'seed_leader_status', 'ML_AI_APP', 'AI应用主线', 'REPAIR', 'seed_condition_status', 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 100, 'seed_risk_veto_reason', 100, 'seed_invalidated_reason', 100, 'seed_signal_text', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 'PATTERN_RESEARCH_01', '条件观察模式', '300750', '宁德时代', 'seed_watch_object_type', 'TREND_LEADER', 'seed_leader_status', 'ML_AI_APP', 'AI应用主线', 'REPAIR', 'seed_condition_status', 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 82.500000, 100, 'seed_risk_veto_reason', 100, 'seed_invalidated_reason', 100, 'seed_signal_text', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.pattern_risk_veto_snapshot
INSERT INTO astock_analysis.pattern_risk_veto_snapshot (trade_date, market_scope, task_id, rule_version_id, pattern_code, pattern_name, stock_code, stock_name, leader_type, condition_status, condition_score, risk_veto, risk_veto_reason, invalidated, invalidated_reason, risk_action, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'PATTERN_RESEARCH_01', '条件观察模式', '000001', '平安银行', 'TREND_LEADER', 'seed_condition_status', 82.500000, 100, 'seed_risk_veto_reason', 100, 'seed_invalidated_reason', 'seed_risk_action', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 'PATTERN_RESEARCH_01', '条件观察模式', '300750', '宁德时代', 'TREND_LEADER', 'seed_condition_status', 82.500000, 100, 'seed_risk_veto_reason', 100, 'seed_invalidated_reason', 'seed_risk_action', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.risk_signal_snapshot
INSERT INTO astock_analysis.risk_signal_snapshot (trade_date, market_scope, task_id, rule_version_id, risk_code, risk_name, risk_source, risk_score, risk_level, signal_level, risk_action, one_vote_veto, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'RISK_SENTIMENT', '情绪波动风险', 'MARKET_ECOLOGY', 82.500000, 'MEDIUM', 'MEDIUM', 'seed_risk_action', 100, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 'RISK_SENTIMENT', '情绪波动风险', 'MARKET_ECOLOGY', 82.500000, 'MEDIUM', 'MEDIUM', 'seed_risk_action', 100, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.risk_signal_detail
INSERT INTO astock_analysis.risk_signal_detail (trade_date, market_scope, task_id, rule_version_id, risk_code, risk_name, risk_source, signal_level, risk_level, risk_score, risk_action, one_vote_veto, risk_text, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'RISK_SENTIMENT', '情绪波动风险', 'MARKET_ECOLOGY', 'MEDIUM', 'MEDIUM', 82.500000, 'seed_risk_action', 100, 'seed_risk_text', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 'RISK_SENTIMENT', '情绪波动风险', 'MARKET_ECOLOGY', 'MEDIUM', 'MEDIUM', 82.500000, 'seed_risk_action', 100, 'seed_risk_text', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.backtest_signal_detail
INSERT INTO astock_analysis.backtest_signal_detail (trade_date, market_scope, task_id, rule_version_id, sample_id, sample_date, stage_code, pattern_code, stock_code, stock_name, mainline_code, mainline_name, signal_score, risk_score, risk_action, signal_effective, risk_vetoed, replay_status, replay_return, replay_drawdown, future_1d_return, future_3d_return, future_5d_return, future_10d_return, max_drawdown, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, toDate('2026-06-30'), 'STAGE_REPAIR', 'PATTERN_RESEARCH_01', '000001', '平安银行', 'ML_AI_APP', 'AI应用主线', 82.500000, 82.500000, 'seed_risk_action', 100, 100, 'seed_replay_status', 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, today(), 'STAGE_REPAIR', 'PATTERN_RESEARCH_01', '300750', '宁德时代', 'ML_AI_APP', 'AI应用主线', 82.500000, 82.500000, 'seed_risk_action', 100, 100, 'seed_replay_status', 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.backtest_performance_detail
INSERT INTO astock_analysis.backtest_performance_detail (trade_date, market_scope, task_id, rule_version_id, layer_code, layer_name, metric_name, metric_value, sample_count, evidence_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'LAYER_ALL', '全样本层', 'sample_count', 1.000000, 12, '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 'LAYER_ALL', '全样本层', 'sample_count', 1.000000, 12, '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.backtest_layer_stat
INSERT INTO astock_analysis.backtest_layer_stat (trade_date, market_scope, task_id, rule_version_id, layer_code, layer_name, sample_count, effective_signal_count, risk_veto_count, win_rate, avg_return, avg_drawdown, profit_loss_ratio, evidence_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 'LAYER_ALL', '全样本层', 12, 12, 12, 1.000000, 2.350000, 3.200000, 0.860000, '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 'LAYER_ALL', '全样本层', 12, 12, 12, 1.000000, 2.350000, 3.200000, 0.860000, '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.backtest_failure_case
INSERT INTO astock_analysis.backtest_failure_case (trade_date, market_scope, task_id, rule_version_id, sample_id, sample_date, pattern_code, stock_code, stock_name, failure_type, failure_reason, replay_status, replay_return, replay_drawdown, future_3d_return, max_drawdown, evidence_json, risk_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, toDate('2026-06-30'), 'PATTERN_RESEARCH_01', '000001', '平安银行', 'seed_failure_type', '开发样本，无失败归因', 'seed_replay_status', 2.350000, 3.200000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, today(), 'PATTERN_RESEARCH_01', '300750', '宁德时代', 'seed_failure_type', '开发样本，无失败归因', 'seed_replay_status', 2.350000, 3.200000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.agent_audit_code_scan_detail
INSERT INTO astock_analysis.agent_audit_code_scan_detail (trade_date, market_scope, audit_task_id, task_id, rule_version_id, issue_code, issue_name, issue_level, issue_type, module_name, file_path, line_no, release_blocker, evidence_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 'NO_ISSUE', '未发现阻断问题', 'INFO', 'seed_issue_type', 'agent_audit_code_scan_detail样本', 'seed_file_path', 100, 100, '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 'NO_ISSUE', '未发现阻断问题', 'INFO', 'seed_issue_type', 'agent_audit_code_scan_detail样本', 'seed_file_path', 100, 100, '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.agent_audit_data_lineage_detail
INSERT INTO astock_analysis.agent_audit_data_lineage_detail (trade_date, market_scope, audit_task_id, task_id, rule_version_id, page_code, vo_class_name, field_name, source_table, source_column, lineage_status, issue_level, evidence_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 'PAGE_RENDER_SEED', 'RenderSeedVO', 'seedField', 'seed_source_table', 'seed_source_column', 'seed_lineage_status', 'INFO', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 'PAGE_RENDER_SEED', 'RenderSeedVO', 'seedField', 'seed_source_table', 'seed_source_column', 'seed_lineage_status', 'INFO', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.agent_audit_rule_hit_detail
INSERT INTO astock_analysis.agent_audit_rule_hit_detail (trade_date, market_scope, audit_task_id, task_id, rule_version_id, rule_code, rule_name, hit_status, hit_count, blocker_count, evidence_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 'RULE_RENDER', '渲染样本规则', 'PASSED', 12, 12, '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 'RULE_RENDER', '渲染样本规则', 'PASSED', 12, 12, '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.agent_audit_release_gate_detail
INSERT INTO astock_analysis.agent_audit_release_gate_detail (trade_date, market_scope, audit_task_id, task_id, rule_version_id, gate_code, gate_name, gate_status, passed, issue_count, blocker_count, evidence_json, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 'GATE_NO_MOCK', '无Mock闸门', 'PASSED', 100, 12, 12, '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 'GATE_NO_MOCK', '无Mock闸门', 'PASSED', 100, 12, 12, '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.historical_cycle_sample_factor
INSERT INTO astock_analysis.historical_cycle_sample_factor (trade_date, market_scope, task_id, rule_version_id, sample_id, report_id, stock_code, stock_name, sector_code, sector_name, theme_code, theme_name, mainline_code, mainline_name, pattern_code, pattern_name, risk_code, risk_name, stage_code, leader_type, match_type, status, rank_no, score, strength_score, similarity_score, risk_score, win_rate, avg_return, avg_drawdown, future_1d_return, future_3d_return, future_5d_return, future_10d_return, max_drawdown, evidence_json, risk_json, features, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 100, '000001', '平安银行', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 100, '300750', '宁德时代', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.historical_following_performance
INSERT INTO astock_analysis.historical_following_performance (trade_date, market_scope, task_id, rule_version_id, sample_id, report_id, stock_code, stock_name, sector_code, sector_name, theme_code, theme_name, mainline_code, mainline_name, pattern_code, pattern_name, risk_code, risk_name, stage_code, leader_type, match_type, status, rank_no, score, strength_score, similarity_score, risk_score, win_rate, avg_return, avg_drawdown, future_1d_return, future_3d_return, future_5d_return, future_10d_return, max_drawdown, evidence_json, risk_json, features, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 100, '000001', '平安银行', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 100, '300750', '宁德时代', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.leader_similarity_match
INSERT INTO astock_analysis.leader_similarity_match (trade_date, market_scope, task_id, rule_version_id, sample_id, report_id, stock_code, stock_name, sector_code, sector_name, theme_code, theme_name, mainline_code, mainline_name, pattern_code, pattern_name, risk_code, risk_name, stage_code, leader_type, match_type, status, rank_no, score, strength_score, similarity_score, risk_score, win_rate, avg_return, avg_drawdown, future_1d_return, future_3d_return, future_5d_return, future_10d_return, max_drawdown, evidence_json, risk_json, features, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 100, '000001', '平安银行', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 100, '300750', '宁德时代', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.mainline_similarity_match
INSERT INTO astock_analysis.mainline_similarity_match (trade_date, market_scope, task_id, rule_version_id, sample_id, report_id, stock_code, stock_name, sector_code, sector_name, theme_code, theme_name, mainline_code, mainline_name, pattern_code, pattern_name, risk_code, risk_name, stage_code, leader_type, match_type, status, rank_no, score, strength_score, similarity_score, risk_score, win_rate, avg_return, avg_drawdown, future_1d_return, future_3d_return, future_5d_return, future_10d_return, max_drawdown, evidence_json, risk_json, features, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 100, '000001', '平安银行', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 100, '300750', '宁德时代', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.pattern_backtest_result
INSERT INTO astock_analysis.pattern_backtest_result (trade_date, market_scope, task_id, rule_version_id, sample_id, report_id, stock_code, stock_name, sector_code, sector_name, theme_code, theme_name, mainline_code, mainline_name, pattern_code, pattern_name, risk_code, risk_name, stage_code, leader_type, match_type, status, rank_no, score, strength_score, similarity_score, risk_score, win_rate, avg_return, avg_drawdown, future_1d_return, future_3d_return, future_5d_return, future_10d_return, max_drawdown, evidence_json, risk_json, features, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 100, '000001', '平安银行', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 100, '300750', '宁德时代', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.risk_similarity_match
INSERT INTO astock_analysis.risk_similarity_match (trade_date, market_scope, task_id, rule_version_id, sample_id, report_id, stock_code, stock_name, sector_code, sector_name, theme_code, theme_name, mainline_code, mainline_name, pattern_code, pattern_name, risk_code, risk_name, stage_code, leader_type, match_type, status, rank_no, score, strength_score, similarity_score, risk_score, win_rate, avg_return, avg_drawdown, future_1d_return, future_3d_return, future_5d_return, future_10d_return, max_drawdown, evidence_json, risk_json, features, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 100, '000001', '平安银行', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 100, '300750', '宁德时代', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.sector_daily_snapshot
INSERT INTO astock_analysis.sector_daily_snapshot (trade_date, market_scope, task_id, rule_version_id, sample_id, report_id, stock_code, stock_name, sector_code, sector_name, theme_code, theme_name, mainline_code, mainline_name, pattern_code, pattern_name, risk_code, risk_name, stage_code, leader_type, match_type, status, rank_no, score, strength_score, similarity_score, risk_score, win_rate, avg_return, avg_drawdown, future_1d_return, future_3d_return, future_5d_return, future_10d_return, max_drawdown, evidence_json, risk_json, features, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 100, '000001', '平安银行', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 100, '300750', '宁德时代', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.sector_stock_mapping_snapshot
INSERT INTO astock_analysis.sector_stock_mapping_snapshot (trade_date, market_scope, task_id, rule_version_id, sample_id, report_id, stock_code, stock_name, sector_code, sector_name, theme_code, theme_name, mainline_code, mainline_name, pattern_code, pattern_name, risk_code, risk_name, stage_code, leader_type, match_type, status, rank_no, score, strength_score, similarity_score, risk_score, win_rate, avg_return, avg_drawdown, future_1d_return, future_3d_return, future_5d_return, future_10d_return, max_drawdown, evidence_json, risk_json, features, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 100, '000001', '平安银行', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 100, '300750', '宁德时代', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- astock_analysis.trend_leader_snapshot
INSERT INTO astock_analysis.trend_leader_snapshot (trade_date, market_scope, task_id, rule_version_id, sample_id, report_id, stock_code, stock_name, sector_code, sector_name, theme_code, theme_name, mainline_code, mainline_name, pattern_code, pattern_name, risk_code, risk_name, stage_code, leader_type, match_type, status, rank_no, score, strength_score, similarity_score, risk_score, win_rate, avg_return, avg_drawdown, future_1d_return, future_3d_return, future_5d_return, future_10d_return, max_drawdown, evidence_json, risk_json, features, created_at) VALUES
(toDate('2026-06-30'), 'A_SHARE', 100, 100, 100, 100, '000001', '平安银行', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now()),
(today(), 'A_SHARE', 100, 100, 100, 100, '300750', '宁德时代', 'SECTOR_AI_APP', 'AI应用', 'THEME_AI_APP', 'AI应用', 'ML_AI_APP', 'AI应用主线', 'PATTERN_RESEARCH_01', '条件观察模式', 'RISK_SENTIMENT', '情绪波动风险', 'STAGE_REPAIR', 'TREND_LEADER', 'MARKET', 'ACTIVE', 100, 82.500000, 82.500000, 82.500000, 82.500000, 1.000000, 2.350000, 3.200000, 2.350000, 2.350000, 2.350000, 2.350000, 3.200000, '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', '{"seed":true,"purpose":"page_render"}', now());

-- 验证示例：
-- SELECT count() FROM astock_analysis.market_factor_snapshot WHERE trade_date IN (toDate('2026-06-30'), today());
-- SELECT count() FROM astock_analysis.leader_daily_snapshot WHERE trade_date IN (toDate('2026-06-30'), today());
