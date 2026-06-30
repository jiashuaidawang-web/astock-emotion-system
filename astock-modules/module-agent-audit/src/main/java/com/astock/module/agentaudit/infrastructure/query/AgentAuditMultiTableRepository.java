package com.astock.module.agentaudit.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.agentaudit.domain.repository.AgentAuditPageRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class AgentAuditMultiTableRepository implements AgentAuditPageRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public AgentAuditMultiTableRepository(ClickHouseQueryExecutor clickHouseQueryExecutor, MysqlQueryExecutor mysqlQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
        this.mysqlQueryExecutor = mysqlQueryExecutor;
    }

    @Override
    public PageSnapshotBundle queryPage(LocalDate tradeDate, String marketScope, int limit) {
        Map<String, Object> params = Map.of(
                "tradeDate", tradeDate,
                "marketScope", marketScope == null ? "" : marketScope,
                "limit", limit
        );
        PageSnapshotBundle bundle = new PageSnapshotBundle();
                bundle.putRows("agent_audit_code_scan_detail", clickHouseQueryExecutor.queryForList(AgentAuditPageSql.CK_AGENT_AUDIT_CODE_SCAN_DETAIL, params));
        bundle.putRows("agent_audit_data_lineage_detail", clickHouseQueryExecutor.queryForList(AgentAuditPageSql.CK_AGENT_AUDIT_DATA_LINEAGE_DETAIL, params));
        bundle.putRows("agent_audit_rule_hit_detail", clickHouseQueryExecutor.queryForList(AgentAuditPageSql.CK_AGENT_AUDIT_RULE_HIT_DETAIL, params));
        bundle.putRows("agent_audit_release_gate_detail", clickHouseQueryExecutor.queryForList(AgentAuditPageSql.CK_AGENT_AUDIT_RELEASE_GATE_DETAIL, params));
        bundle.putRows("agent_audit_task", mysqlQueryExecutor.queryForList(AgentAuditPageSql.MYSQL_AGENT_AUDIT_TASK, params));
        bundle.putRows("agent_audit_result", mysqlQueryExecutor.queryForList(AgentAuditPageSql.MYSQL_AGENT_AUDIT_RESULT, params));
        bundle.putRows("agent_audit_issue", mysqlQueryExecutor.queryForList(AgentAuditPageSql.MYSQL_AGENT_AUDIT_ISSUE, params));
        bundle.putRows("agent_audit_rule_version", mysqlQueryExecutor.queryForList(AgentAuditPageSql.MYSQL_AGENT_AUDIT_RULE_VERSION, params));
        bundle.putRows("agent_release_gate_check", mysqlQueryExecutor.queryForList(AgentAuditPageSql.MYSQL_AGENT_RELEASE_GATE_CHECK, params));
        return bundle;
    }
}
