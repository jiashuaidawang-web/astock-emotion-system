package com.astock.module.agentaudit.domain.model;

import lombok.Data;

/**
 * AgentAuditRuleHit 数据载体。
 */
@Data
public class AgentAuditRuleHit {
    /** 规则编码。 */
    private String ruleCode;
    /** 规则名称。 */
    private String ruleName;
    /** hitStatus 字段。 */
    private String hitStatus;
    /** hitCount 字段。 */
    private Integer hitCount;
    /** blockerCount 字段。 */
    private Integer blockerCount;
    /** evidenceJson 字段。 */
    private String evidenceJson;
}
