package com.astock.module.similarity.infrastructure.clickhouse;

public final class HistoricalSimilarityClickHouseSql {
    private HistoricalSimilarityClickHouseSql() {}

    public static final String SELECT_PRIMARY_ROWS =
            "select * from market_factor_snapshot where trade_date = :tradeDate limit :limit";
}
