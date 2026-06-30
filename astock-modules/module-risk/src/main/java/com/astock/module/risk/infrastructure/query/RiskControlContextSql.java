package com.astock.module.risk.infrastructure.query;

public final class RiskControlContextSql {
    private RiskControlContextSql() {}

    public static final String SELECT_PATTERN_SIGNAL_ROWS =
            "select * from buy_pattern_signal_snapshot where trade_date = :tradeDate limit 2000";

    public static final String SELECT_DATA_QUALITY_ROWS =
            "select * from data_quality_check_log where trade_date = :tradeDate and is_deleted = 0 limit 500";

    public static final String SELECT_RISK_ACTION_MATRIX =
            "select * from risk_action_matrix where is_deleted = 0 limit 500";
}
