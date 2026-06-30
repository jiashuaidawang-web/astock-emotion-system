package com.astock.module.rule.infrastructure.query;

public final class RuleVersionPageSql {
    private RuleVersionPageSql() {}

    public static final String CK_BACKTEST_LAYER_STAT = "select * from backtest_layer_stat where trade_date = :tradeDate limit :limit";
    public static final String CK_BACKTEST_FAILURE_CASE = "select * from backtest_failure_case where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_RULE_DEFINITION = "select * from rule_definition where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_RULE_VERSION = "select * from rule_version where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_RULE_PUBLISH_CHECK_LOG = "select * from rule_publish_check_log where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_RULE_VERSION_AUDIT_LOG = "select * from rule_version_audit_log where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_BACKTEST_REPORT = "select * from backtest_report where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_AGENT_AUDIT_RESULT = "select * from agent_audit_result where is_deleted = 0 order by id desc limit :limit";
}
