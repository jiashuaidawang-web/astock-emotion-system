package com.astock.module.agentaudit.infrastructure.query;

public final class AgentAuditPageSql {
    private AgentAuditPageSql() {}

    public static final String CK_AGENT_AUDIT_CODE_SCAN_DETAIL = "select * from agent_audit_code_scan_detail where trade_date = :tradeDate limit :limit";
    public static final String CK_AGENT_AUDIT_DATA_LINEAGE_DETAIL = "select * from agent_audit_data_lineage_detail where trade_date = :tradeDate limit :limit";
    public static final String CK_AGENT_AUDIT_RULE_HIT_DETAIL = "select * from agent_audit_rule_hit_detail where trade_date = :tradeDate limit :limit";
    public static final String CK_AGENT_AUDIT_RELEASE_GATE_DETAIL = "select * from agent_audit_release_gate_detail where trade_date = :tradeDate limit :limit";
    public static final String MYSQL_AGENT_AUDIT_TASK = "select * from agent_audit_task where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_AGENT_AUDIT_RESULT = "select * from agent_audit_result where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_AGENT_AUDIT_ISSUE = "select * from agent_audit_issue where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_AGENT_AUDIT_RULE_VERSION = "select * from agent_audit_rule_version where is_deleted = 0 order by id desc limit :limit";
    public static final String MYSQL_AGENT_RELEASE_GATE_CHECK = "select * from agent_release_gate_check where is_deleted = 0 order by id desc limit :limit";
}
