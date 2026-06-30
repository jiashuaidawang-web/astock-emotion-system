package com.astock.module.sample.infrastructure.query;

public final class HistoricalCycleSamplePageSql {
    private HistoricalCycleSamplePageSql() {}

    public static final String CK_HISTORICAL_CYCLE_SAMPLE = "select * from historical_cycle_sample where trade_date = :tradeDate limit :limit";
    public static final String CK_HISTORICAL_CYCLE_SAMPLE_FACTOR = "select * from historical_cycle_sample_factor where trade_date = :tradeDate limit :limit";
    public static final String CK_HISTORICAL_FOLLOWING_PERFORMANCE = "select * from historical_following_performance where trade_date = :tradeDate limit :limit";
    public static final String CK_EMOTION_STAGE_SNAPSHOT = "select * from emotion_stage_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_MAINLINE_DAILY_SNAPSHOT = "select * from mainline_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_LEADER_DAILY_SNAPSHOT = "select * from leader_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_RISK_SIGNAL_SNAPSHOT = "select * from risk_signal_snapshot where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_CYCLE_MINING_TASK = "select * from cycle_mining_task where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_CYCLE_SAMPLE_CONFIRM = "select * from cycle_sample_confirm where trade_date = :tradeDate limit :limit";
}
