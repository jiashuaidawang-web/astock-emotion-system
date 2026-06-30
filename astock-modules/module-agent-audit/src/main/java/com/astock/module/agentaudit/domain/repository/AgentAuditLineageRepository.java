package com.astock.module.agentaudit.domain.repository;

import java.util.List;
import java.util.Map;

public interface AgentAuditLineageRepository {
    List<Map<String, Object>> selectLineageRows();
}
