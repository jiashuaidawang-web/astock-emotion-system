package com.astock.module.sector.infrastructure.clickhouse;

public final class SectorStrengthClickHouseSql {
    private SectorStrengthClickHouseSql() {}

    public static final String SELECT_PRIMARY_ROWS =
            "select * from sector_strength_snapshot where trade_date = :tradeDate limit :limit";
}
