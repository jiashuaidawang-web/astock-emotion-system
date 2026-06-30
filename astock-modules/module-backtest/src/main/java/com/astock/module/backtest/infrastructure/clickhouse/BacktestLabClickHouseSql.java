package com.astock.module.backtest.infrastructure.clickhouse;

public final class BacktestLabClickHouseSql {
    private BacktestLabClickHouseSql() {}

    public static final String SELECT_PRIMARY_ROWS =
            "select * from backtest_signal_detail where trade_date = :tradeDate limit :limit";
}
