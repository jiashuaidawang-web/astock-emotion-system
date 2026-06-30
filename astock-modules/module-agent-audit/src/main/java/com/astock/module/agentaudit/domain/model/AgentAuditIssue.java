package com.astock.module.agentaudit.domain.model;

import lombok.Data;

/**
 * AgentAuditIssue 数据载体。
 */
@Data
public class AgentAuditIssue {
    /** issueCode 字段。 */
    private String issueCode;
    /** issueName 字段。 */
    private String issueName;
    /** issueLevel 字段。 */
    private String issueLevel;
    /** issueType 字段。 */
    private String issueType;
    /** moduleName 字段。 */
    private String moduleName;
    /** filePath 字段。 */
    private String filePath;
    /** lineNo 字段。 */
    private Integer lineNo;
    /** evidenceText 字段。 */
    private String evidenceText;
    /** fixSuggestion 字段。 */
    private String fixSuggestion;
    /** releaseBlocker 字段。 */
    private Boolean releaseBlocker;
}
