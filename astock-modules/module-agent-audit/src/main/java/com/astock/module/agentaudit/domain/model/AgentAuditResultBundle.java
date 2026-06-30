package com.astock.module.agentaudit.domain.model;

import java.util.List;
import lombok.Data;

/**
 * AgentAuditResultBundle 数据载体。
 */
@Data
public class AgentAuditResultBundle {
    /** issues 字段。 */
    private List<AgentAuditIssue> issues;
    /** ruleHits 字段。 */
    private List<AgentAuditRuleHit> ruleHits;
    /** lineageIssues 字段。 */
    private List<AgentAuditLineageIssue> lineageIssues;
    /** gateChecks 字段。 */
    private List<AgentReleaseGateCheck> gateChecks;
}
