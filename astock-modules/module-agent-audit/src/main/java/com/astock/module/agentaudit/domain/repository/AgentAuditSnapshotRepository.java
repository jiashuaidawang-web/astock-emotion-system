package com.astock.module.agentaudit.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AgentAuditSnapshotRepository {
    List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit);
}
