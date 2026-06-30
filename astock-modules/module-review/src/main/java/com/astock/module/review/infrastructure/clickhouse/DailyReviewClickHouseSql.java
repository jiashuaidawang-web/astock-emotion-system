package com.astock.module.review.infrastructure.clickhouse;

public final class DailyReviewClickHouseSql {
    private DailyReviewClickHouseSql() {}

    public static final String SELECT_PRIMARY_ROWS =
            "select * from market_factor_snapshot where trade_date = :tradeDate limit :limit";
}
