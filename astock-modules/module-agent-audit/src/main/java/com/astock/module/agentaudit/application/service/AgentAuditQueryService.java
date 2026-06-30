package com.astock.module.agentaudit.application.service;

import com.astock.module.agentaudit.api.vo.AgentAuditDashboardVO;
import com.astock.module.agentaudit.application.query.AgentAuditDashboardQuery;

public interface AgentAuditQueryService {
    AgentAuditDashboardVO queryDashboard(AgentAuditDashboardQuery query);
}
