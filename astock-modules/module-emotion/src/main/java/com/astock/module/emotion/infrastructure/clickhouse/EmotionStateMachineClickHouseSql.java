package com.astock.module.emotion.infrastructure.clickhouse;

public final class EmotionStateMachineClickHouseSql {
    private EmotionStateMachineClickHouseSql() {}

    public static final String SELECT_PRIMARY_ROWS =
            "select * from emotion_stage_snapshot where trade_date = :tradeDate limit :limit";
}
