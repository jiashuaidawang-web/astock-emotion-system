package com.astock.module.market.infrastructure.query;

public final class MarketDashboardPageSql {
    private MarketDashboardPageSql() {}

    public static final String CK_MARKET_FACTOR_SNAPSHOT = "select * from market_factor_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_LIMIT_UP_DOWN_ECOLOGY_SNAPSHOT = "select * from limit_up_down_ecology_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_EMOTION_STAGE_SNAPSHOT = "select * from emotion_stage_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_RISK_SIGNAL_SNAPSHOT = "select * from risk_signal_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_HISTORICAL_SIMILARITY_MATCH = "select * from historical_similarity_match where trade_date = :tradeDate limit :limit";
    public static final String CK_MAINLINE_DAILY_SNAPSHOT = "select * from mainline_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_LEADER_DAILY_SNAPSHOT = "select * from leader_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_BUY_PATTERN_SIGNAL_SNAPSHOT = "select * from buy_pattern_signal_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_PATTERN_RISK_VETO_SNAPSHOT = "select * from pattern_risk_veto_snapshot where trade_date = :tradeDate limit :limit";
}
