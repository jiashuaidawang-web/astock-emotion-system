package com.astock.module.backtest.infrastructure.query;

public final class BacktestReplaySampleSql {
    private BacktestReplaySampleSql() {}

    public static final String SELECT_HISTORICAL_CYCLE_SAMPLE =
            "select * from historical_cycle_sample " +
            "where trade_date < :endDate " +
            "order by trade_date desc limit :sampleLimit";

    public static final String SELECT_BUY_PATTERN_SIGNAL =
            "select * from buy_pattern_signal_snapshot " +
            "where trade_date < :endDate " +
            "order by trade_date desc limit :sampleLimit";

    public static final String SELECT_RISK_SIGNAL =
            "select * from risk_signal_snapshot " +
            "where trade_date < :endDate " +
            "order by trade_date desc limit :sampleLimit";

    public static final String SELECT_STOCK_DAILY_KLINE =
            "select * from stock_daily_kline " +
            "where trade_date < :endDate " +
            "order by trade_date desc limit :sampleLimit";
}
