-- 第七步第九段：EmotionStageRecognitionEngine 历史增强输入契约
-- 本文件用于说明历史增强算法读取的真实表与字段建议。

-- ClickHouse：historical_cycle_sample
-- 建议字段：
-- id / sample_id
-- trade_date
-- stage_code / stage_type / emotion_stage / primary_stage
-- sample_confidence
-- similarity_score
-- future_3d_return
-- max_drawdown
-- evidence_json
-- feature_json

-- MySQL：cycle_sample_confirm
-- 建议字段：
-- id
-- sample_id / cycle_sample_id
-- confirmed_stage / stage_code
-- confirm_status
-- updated_at
-- is_deleted

-- MySQL：manual_stage_adjustment
-- 建议字段：
-- id
-- trade_date
-- manual_stage / adjusted_stage / stage_code
-- adjust_reason
-- updated_at
-- is_deleted

-- ClickHouse：stage_transition_snapshot
-- 建议字段：
-- trade_date
-- from_stage
-- to_stage
-- primary_stage
-- transition_score
-- transition_probability
-- evidence_json

-- 输出增强字段：
-- emotion_stage_score_detail.historical_sample_similarity_score
-- emotion_stage_score_detail.stage_path_match_score
-- emotion_stage_score_detail.following_validation_score
-- emotion_stage_score_detail.manual_sample_correction_score
-- emotion_stage_score_detail.features
-- emotion_stage_snapshot.features
