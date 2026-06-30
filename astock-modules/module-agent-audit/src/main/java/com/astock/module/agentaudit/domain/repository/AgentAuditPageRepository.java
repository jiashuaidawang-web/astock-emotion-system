package com.astock.module.agentaudit.domain.repository;

import com.astock.common.data.PageSnapshotBundle;
import java.time.LocalDate;

public interface AgentAuditPageRepository {
    PageSnapshotBundle queryPage(LocalDate tradeDate, String marketScope, int limit);
}
