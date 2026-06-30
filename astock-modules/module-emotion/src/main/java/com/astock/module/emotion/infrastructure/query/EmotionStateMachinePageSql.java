package com.astock.module.emotion.infrastructure.query;

public final class EmotionStateMachinePageSql {
    private EmotionStateMachinePageSql() {}

    public static final String CK_EMOTION_STAGE_SNAPSHOT = "select * from emotion_stage_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_EMOTION_STAGE_SCORE_DETAIL = "select * from emotion_stage_score_detail where trade_date = :tradeDate limit :limit";
    public static final String CK_STAGE_TRANSITION_SNAPSHOT = "select * from stage_transition_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_HISTORICAL_SIMILARITY_MATCH = "select * from historical_similarity_match where trade_date = :tradeDate limit :limit";
    public static final String CK_HISTORICAL_CYCLE_SAMPLE = "select * from historical_cycle_sample where trade_date = :tradeDate limit :limit";
    public static final String CK_HISTORICAL_FOLLOWING_PERFORMANCE = "select * from historical_following_performance where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_MANUAL_STAGE_ADJUSTMENT = "select * from manual_stage_adjustment where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_EMOTION_STAGE_RULE_VERSION = "select * from emotion_stage_rule_version where is_deleted = 0 order by id desc limit :limit";
}
