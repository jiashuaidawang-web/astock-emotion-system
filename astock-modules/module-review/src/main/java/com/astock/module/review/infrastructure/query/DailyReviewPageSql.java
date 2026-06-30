package com.astock.module.review.infrastructure.query;

public final class DailyReviewPageSql {
    private DailyReviewPageSql() {}

    public static final String CK_MARKET_FACTOR_SNAPSHOT = "select * from market_factor_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_EMOTION_STAGE_SNAPSHOT = "select * from emotion_stage_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_HISTORICAL_SIMILARITY_MATCH = "select * from historical_similarity_match where trade_date = :tradeDate limit :limit";
    public static final String CK_MAINLINE_DAILY_SNAPSHOT = "select * from mainline_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_SECTOR_STRENGTH_SNAPSHOT = "select * from sector_strength_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_LEADER_DAILY_SNAPSHOT = "select * from leader_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_BUY_PATTERN_SIGNAL_SNAPSHOT = "select * from buy_pattern_signal_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_RISK_SIGNAL_SNAPSHOT = "select * from risk_signal_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_BACKTEST_LAYER_STAT = "select * from backtest_layer_stat where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_DAILY_REVIEW_RECORD = "select * from daily_review_record where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_DAILY_REVIEW_SECTION = "select * from daily_review_section where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_DAILY_REVIEW_CHECKLIST = "select * from daily_review_checklist where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_DAILY_REVIEW_AUDIT_LOG = "select * from daily_review_audit_log where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_MANUAL_STAGE_ADJUSTMENT = "select * from manual_stage_adjustment where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_CYCLE_SAMPLE_CONFIRM = "select * from cycle_sample_confirm where trade_date = :tradeDate limit :limit";
}
