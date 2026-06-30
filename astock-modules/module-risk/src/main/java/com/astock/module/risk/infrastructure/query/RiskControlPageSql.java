package com.astock.module.risk.infrastructure.query;

public final class RiskControlPageSql {
    private RiskControlPageSql() {}

    public static final String CK_RISK_SIGNAL_SNAPSHOT = "select * from risk_signal_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_RISK_SIGNAL_DETAIL = "select * from risk_signal_detail where trade_date = :tradeDate limit :limit";
    public static final String CK_PATTERN_RISK_VETO_SNAPSHOT = "select * from pattern_risk_veto_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_LEADER_NEGATIVE_FEEDBACK = "select * from leader_negative_feedback where trade_date = :tradeDate limit :limit";
    public static final String CK_MAINLINE_DAILY_SNAPSHOT = "select * from mainline_daily_snapshot where trade_date = :tradeDate limit :limit";
    public static final String CK_HISTORICAL_CYCLE_SAMPLE = "select * from historical_cycle_sample where trade_date = :tradeDate limit :limit";
    public static final String CK_RISK_SIMILARITY_MATCH = "select * from risk_similarity_match where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_RISK_ACTION_MATRIX = "select * from risk_action_matrix where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_RISK_RULE_VERSION = "select * from risk_rule_version where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_DATA_QUALITY_CHECK_LOG = "select * from data_quality_check_log where trade_date = :tradeDate limit :limit";
}
