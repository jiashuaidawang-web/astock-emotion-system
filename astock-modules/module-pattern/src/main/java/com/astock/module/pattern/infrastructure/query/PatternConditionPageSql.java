package com.astock.module.pattern.infrastructure.query;

public final class PatternConditionPageSql {
    private PatternConditionPageSql() {}

    public static final String CK_BUY_PATTERN_SIGNAL_SNAPSHOT = "select * from buy_pattern_signal_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_PATTERN_RISK_VETO_SNAPSHOT = "select * from pattern_risk_veto_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_RISK_SIGNAL_SNAPSHOT = "select * from risk_signal_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_RISK_SIGNAL_DETAIL = "select * from risk_signal_detail where trade_date = :tradeDate limit :limit";
    public static final String CK_LEADER_DAILY_SNAPSHOT = "select * from leader_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_MAINLINE_DAILY_SNAPSHOT = "select * from mainline_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_TREND_LEADER_SNAPSHOT = "select * from trend_leader_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_PATTERN_BACKTEST_RESULT = "select * from pattern_backtest_result where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_BUY_PATTERN_DEFINITION = "select * from buy_pattern_definition where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_BUY_PATTERN_STAGE_MATRIX = "select * from buy_pattern_stage_matrix where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_BUY_PATTERN_RULE_CONFIG = "select * from buy_pattern_rule_config where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_PATTERN_RISK_BINDING = "select * from pattern_risk_binding where is_deleted = 0 order by id desc limit :limit";
}
