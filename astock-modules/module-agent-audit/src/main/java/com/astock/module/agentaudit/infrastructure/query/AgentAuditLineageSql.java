package com.astock.module.agentaudit.infrastructure.query;

public final class AgentAuditLineageSql {
    private AgentAuditLineageSql() {}

    public static final String SELECT_PAGE_CONTRACT_FIELD_LINEAGE =
            "select page_code, vo_class_name, field_name, source_type, source_table, source_column, " +
            "calculation_formula, required, audit_passed " +
            "from page_contract_field_lineage " +
            "where is_deleted = 0 limit 5000";
}
