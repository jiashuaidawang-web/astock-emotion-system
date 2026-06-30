package com.astock.module.emotion.infrastructure.query;

public final class EmotionHistoricalContextSql {
    private EmotionHistoricalContextSql() {}

    public static final String SELECT_HISTORICAL_CYCLE_SAMPLE = 
            "select * from historical_cycle_sample " +
            "where trade_date < :tradeDate " +
            "order by trade_date desc limit 1000";

    public static final String SELECT_RECENT_STAGE_TRANSITION =
            "select * from stage_transition_snapshot " +
            "where trade_date < :tradeDate " +
            "order by trade_date desc limit :pathWindowDays";

    public static final String SELECT_CYCLE_SAMPLE_CONFIRM =
            "select * from cycle_sample_confirm " +
            "where is_deleted = 0 " +
            "order by updated_at desc limit 1000";

    public static final String SELECT_MANUAL_STAGE_ADJUSTMENT =
            "select * from manual_stage_adjustment " +
            "where trade_date <= :tradeDate and is_deleted = 0 " +
            "order by trade_date desc, updated_at desc limit 200";
}
