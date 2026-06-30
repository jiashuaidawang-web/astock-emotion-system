package com.astock.module.agentaudit.application.service.impl;

import com.astock.module.agentaudit.api.vo.AgentAuditDashboardVO;
import com.astock.module.agentaudit.application.aggregator.AgentAuditDashboardAggregator;
import com.astock.module.agentaudit.application.query.AgentAuditDashboardQuery;
import com.astock.module.agentaudit.application.service.AgentAuditQueryService;
import org.springframework.stereotype.Service;

@Service
public class AgentAuditQueryServiceImpl implements AgentAuditQueryService {

    private final AgentAuditDashboardAggregator aggregator;

    public AgentAuditQueryServiceImpl(AgentAuditDashboardAggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public AgentAuditDashboardVO queryDashboard(AgentAuditDashboardQuery query) {
        return aggregator.aggregate(query);
    }
}
