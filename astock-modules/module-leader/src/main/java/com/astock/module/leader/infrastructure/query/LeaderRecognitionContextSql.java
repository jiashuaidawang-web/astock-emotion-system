package com.astock.module.leader.infrastructure.query;

public final class LeaderRecognitionContextSql {
    private LeaderRecognitionContextSql() {}

    public static final String SELECT_PREVIOUS_LEADER_ROWS =
            "select * from leader_daily_snapshot where trade_date < :tradeDate order by trade_date desc limit 100";

    public static final String SELECT_RISK_ROWS =
            "select * from risk_signal_snapshot where trade_date = :tradeDate limit 500";

    public static final String SELECT_PATTERN_ROWS =
            "select * from buy_pattern_signal_snapshot where trade_date = :tradeDate limit 500";
}
