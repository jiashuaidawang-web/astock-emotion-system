package com.astock.module.pattern.infrastructure.clickhouse;

public final class PatternConditionClickHouseSql {
    private PatternConditionClickHouseSql() {}

    public static final String SELECT_PRIMARY_ROWS =
            "select * from buy_pattern_signal_snapshot where trade_date = :tradeDate limit :limit";
}
