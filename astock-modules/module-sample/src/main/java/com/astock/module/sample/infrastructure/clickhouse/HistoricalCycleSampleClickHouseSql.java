package com.astock.module.sample.infrastructure.clickhouse;

public final class HistoricalCycleSampleClickHouseSql {
    private HistoricalCycleSampleClickHouseSql() {}

    public static final String SELECT_PRIMARY_ROWS =
            "select * from historical_cycle_sample where trade_date = :tradeDate limit :limit";
}
