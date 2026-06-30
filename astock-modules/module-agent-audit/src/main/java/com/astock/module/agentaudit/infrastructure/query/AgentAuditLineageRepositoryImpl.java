package com.astock.module.agentaudit.infrastructure.query;

import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.agentaudit.domain.repository.AgentAuditLineageRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AgentAuditLineageRepositoryImpl implements AgentAuditLineageRepository {
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public AgentAuditLineageRepositoryImpl(MysqlQueryExecutor mysqlQueryExecutor) {
        this.mysqlQueryExecutor = mysqlQueryExecutor;
    }

    @Override
    public List<Map<String, Object>> selectLineageRows() {
        return mysqlQueryExecutor.queryForList(AgentAuditLineageSql.SELECT_PAGE_CONTRACT_FIELD_LINEAGE, Map.of());
    }
}
