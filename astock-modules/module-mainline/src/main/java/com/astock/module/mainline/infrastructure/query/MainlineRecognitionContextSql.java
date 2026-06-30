package com.astock.module.mainline.infrastructure.query;

public final class MainlineRecognitionContextSql {
    private MainlineRecognitionContextSql() {}

    public static final String SELECT_SECTOR_ROWS =
            "select * from sector_strength_snapshot where trade_date = :tradeDate limit 1000";

    public static final String SELECT_LEADER_ROWS =
            "select * from leader_daily_snapshot where trade_date = :tradeDate limit 1000";

    public static final String SELECT_PREVIOUS_MAINLINE_ROWS =
            "select * from mainline_daily_snapshot where trade_date < :tradeDate order by trade_date desc limit 50";
}
