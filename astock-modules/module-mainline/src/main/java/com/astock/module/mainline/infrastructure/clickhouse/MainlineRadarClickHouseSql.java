package com.astock.module.mainline.infrastructure.clickhouse;

public final class MainlineRadarClickHouseSql {
    private MainlineRadarClickHouseSql() {}

    public static final String SELECT_PRIMARY_ROWS =
            "select * from mainline_daily_snapshot where trade_date = :tradeDate limit :limit";
}
