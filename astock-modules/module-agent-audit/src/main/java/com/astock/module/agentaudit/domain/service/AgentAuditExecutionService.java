package com.astock.module.agentaudit.domain.service;

import com.astock.module.agentaudit.domain.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentAuditExecutionService {
    private final AgentAuditProjectScanner projectScanner;
    private final AgentAuditRedLineScanner redLineScanner;
    private final AgentAuditLineageChecker lineageChecker;
    private final AgentAuditRuleHitAggregator ruleHitAggregator;
    private final AgentReleaseGateService releaseGateService;

    public AgentAuditExecutionService(AgentAuditProjectScanner projectScanner,
                                      AgentAuditRedLineScanner redLineScanner,
                                      AgentAuditLineageChecker lineageChecker,
                                      AgentAuditRuleHitAggregator ruleHitAggregator,
                                      AgentReleaseGateService releaseGateService) {
        this.projectScanner = projectScanner;
        this.redLineScanner = redLineScanner;
        this.lineageChecker = lineageChecker;
        this.ruleHitAggregator = ruleHitAggregator;
        this.releaseGateService = releaseGateService;
    }

    public AgentAuditResultBundle execute(String projectRootPath) {
        List<AgentAuditFileSnapshot> files = projectScanner.scan(projectRootPath);
        List<AgentAuditIssue> issues = redLineScanner.scan(files);
        List<AgentAuditLineageIssue> lineageIssues = lineageChecker.check();
        List<AgentAuditRuleHit> ruleHits = ruleHitAggregator.aggregate(issues, lineageIssues);
        List<AgentReleaseGateCheck> gateChecks = releaseGateService.check(issues, lineageIssues);

        AgentAuditResultBundle result = new AgentAuditResultBundle();
        result.setIssues(issues);
        result.setLineageIssues(lineageIssues);
        result.setRuleHits(ruleHits);
        result.setGateChecks(gateChecks);
        return result;
    }
}
