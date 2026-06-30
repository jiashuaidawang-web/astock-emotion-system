package com.astock.module.leader.infrastructure.clickhouse;

public final class LeaderLadderClickHouseSql {
    private LeaderLadderClickHouseSql() {}

    public static final String SELECT_PRIMARY_ROWS =
            "select * from leader_daily_snapshot where trade_date = :tradeDate limit :limit";
}
