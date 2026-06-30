package com.astock.module.agentaudit.domain.model;

import lombok.Data;

/**
 * AgentAuditLineageIssue 数据载体。
 */
@Data
public class AgentAuditLineageIssue {
    /** 页面编码。 */
    private String pageCode;
    /** VO类名。 */
    private String voClassName;
    /** 字段名。 */
    private String fieldName;
    /** 来源表。 */
    private String sourceTable;
    /** 来源列。 */
    private String sourceColumn;
    /** lineageStatus 字段。 */
    private String lineageStatus;
    /** issueLevel 字段。 */
    private String issueLevel;
    /** evidenceText 字段。 */
    private String evidenceText;
}
