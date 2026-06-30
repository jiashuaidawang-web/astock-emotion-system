package com.astock.module.market.infrastructure.clickhouse;

public final class MarketDashboardClickHouseSql {
    private MarketDashboardClickHouseSql() {}

    public static final String SELECT_PRIMARY_ROWS =
            "select * from market_factor_snapshot where trade_date = :tradeDate limit :limit";
}
