package com.astock.module.agentaudit.controller;

import com.astock.common.api.ApiResult;
import com.astock.module.agentaudit.api.vo.AgentAuditDashboardVO;
import com.astock.module.agentaudit.application.query.AgentAuditDashboardQuery;
import com.astock.module.agentaudit.application.service.AgentAuditQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent-audit")
public class AgentAuditDashboardController {

    private final AgentAuditQueryService queryService;

    public AgentAuditDashboardController(AgentAuditQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/dashboard")
    public ApiResult<AgentAuditDashboardVO> dashboard(AgentAuditDashboardQuery query) {
        return ApiResult.success(queryService.queryDashboard(query));
    }
}
