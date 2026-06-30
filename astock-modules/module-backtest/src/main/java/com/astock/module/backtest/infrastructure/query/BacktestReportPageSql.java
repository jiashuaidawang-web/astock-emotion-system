package com.astock.module.backtest.infrastructure.query;

public final class BacktestReportPageSql {
    private BacktestReportPageSql() {}

    public static final String CK_BACKTEST_SIGNAL_DETAIL = "select * from backtest_signal_detail where trade_date = :tradeDate limit :limit";
    public static final String CK_BACKTEST_PERFORMANCE_DETAIL = "select * from backtest_performance_detail where trade_date = :tradeDate limit :limit";
    public static final String CK_BACKTEST_LAYER_STAT = "select * from backtest_layer_stat where trade_date = :tradeDate limit :limit";
    public static final String CK_BACKTEST_FAILURE_CASE = "select * from backtest_failure_case where trade_date = :tradeDate limit :limit";
    public static final String CK_PATTERN_BACKTEST_RESULT = "select * from pattern_backtest_result where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_BACKTEST_REPORT = "select * from backtest_report where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_BACKTEST_TASK = "select * from backtest_task where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_BACKTEST_TASK_PARAM = "select * from backtest_task_param where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_BACKTEST_RULE_BINDING = "select * from backtest_rule_binding where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_RULE_VERSION = "select * from rule_version where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_DATA_QUALITY_CHECK_LOG = "select * from data_quality_check_log where trade_date = :tradeDate limit :limit";
}
