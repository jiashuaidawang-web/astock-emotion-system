package com.astock.module.risk.infrastructure.clickhouse;

public final class RiskControlClickHouseSql {
    private RiskControlClickHouseSql() {}

    public static final String SELECT_PRIMARY_ROWS =
            "select * from risk_signal_snapshot where trade_date = :tradeDate limit :limit";
}
