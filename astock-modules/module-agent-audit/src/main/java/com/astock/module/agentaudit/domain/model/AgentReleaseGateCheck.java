package com.astock.module.agentaudit.domain.model;

import lombok.Data;

/**
 * AgentReleaseGateCheck 数据载体。
 */
@Data
public class AgentReleaseGateCheck {
    /** gateCode 字段。 */
    private String gateCode;
    /** gateName 字段。 */
    private String gateName;
    /** gateStatus 字段。 */
    private String gateStatus;
    /** passed 字段。 */
    private Boolean passed;
    /** issueCount 字段。 */
    private Integer issueCount;
    /** blockerCount 字段。 */
    private Integer blockerCount;
    /** evidenceJson 字段。 */
    private String evidenceJson;
}
