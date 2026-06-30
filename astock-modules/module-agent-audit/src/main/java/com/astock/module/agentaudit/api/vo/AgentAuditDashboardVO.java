package com.astock.module.agentaudit.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * AgentAuditDashboardVO 数据载体。
 */
@Data
public class AgentAuditDashboardVO {
    /** auditDate 字段。 */
    private LocalDate auditDate;
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** overview 字段。 */
    private AgentAuditOverviewVO overview;
    /** ruleVersionSummary 字段。 */
    private AgentAuditRuleVersionSummaryVO ruleVersionSummary;
    /** recentAuditTasks 字段。 */
    private List<AgentAuditTaskVO> recentAuditTasks;
    /** pageAuditMatrix 字段。 */
    private List<PageAuditMatrixVO> pageAuditMatrix;
    /** moduleAuditStats 字段。 */
    private List<ModuleAuditStatVO> moduleAuditStats;
    /** redLineIssues 字段。 */
    private List<AgentAuditIssueVO> redLineIssues;
    /** releaseBlockerIssues 字段。 */
    private List<AgentAuditIssueVO> releaseBlockerIssues;
    /** resolvedIssues 字段。 */
    private List<AgentAuditIssueVO> resolvedIssues;
    /** ruleCoverages 字段。 */
    private List<AgentAuditRuleCoverageVO> ruleCoverages;
    /** releaseGateChecks 字段。 */
    private List<AgentReleaseGateCheckVO> releaseGateChecks;
    /** fixSuggestions 字段。 */
    private List<AgentAuditFixSuggestionVO> fixSuggestions;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;


    @Data
    public static class AgentAuditOverviewVO {
        /** totalAuditTaskCount 字段。 */
        private Integer totalAuditTaskCount;
        /** passedTaskCount 字段。 */
        private Integer passedTaskCount;
        /** failedTaskCount 字段。 */
        private Integer failedTaskCount;
        /** blockerIssueCount 字段。 */
        private Integer blockerIssueCount;
        /** fatalIssueCount 字段。 */
        private Integer fatalIssueCount;
        /** releaseAllowed 字段。 */
        private Boolean releaseAllowed;
        /** mergeAllowed 字段。 */
        private Boolean mergeAllowed;
        /** auditPassRate 字段。 */
        private BigDecimal auditPassRate;
        /** overviewText 字段。 */
        private String overviewText;

    }

    @Data
    public static class AgentAuditRuleVersionSummaryVO {
        /** auditRuleVersionId 字段。 */
        private Long auditRuleVersionId;
        /** 版本号。 */
        private String versionNo;
        /** 版本名称。 */
        private String versionName;
        /** active 字段。 */
        private Boolean active;
        /** auditRuleCount 字段。 */
        private Integer auditRuleCount;
        /** redLineRuleCount 字段。 */
        private Integer redLineRuleCount;
        /** requireBacktestCheck 字段。 */
        private Boolean requireBacktestCheck;
        /** requireFutureLeakageCheck 字段。 */
        private Boolean requireFutureLeakageCheck;
        /** requireTradingInstructionCheck 字段。 */
        private Boolean requireTradingInstructionCheck;

    }

    @Data
    public static class AgentAuditTaskVO {
        /** auditTaskId 字段。 */
        private Long auditTaskId;
        /** 任务名称。 */
        private String taskName;
        /** auditObjectType 字段。 */
        private String auditObjectType;
        /** auditObjectName 字段。 */
        private String auditObjectName;
        /** 页面编码。 */
        private String pageCode;
        /** moduleName 字段。 */
        private String moduleName;
        /** auditStatus 字段。 */
        private String auditStatus;
        /** issueCount 字段。 */
        private Integer issueCount;
        /** blockerIssueCount 字段。 */
        private Integer blockerIssueCount;
        /** mergeAllowed 字段。 */
        private Boolean mergeAllowed;
        /** releaseAllowed 字段。 */
        private Boolean releaseAllowed;
        /** auditSummary 字段。 */
        private String auditSummary;

    }

    @Data
    public static class PageAuditMatrixVO {
        /** 页面编码。 */
        private String pageCode;
        /** 页面名称。 */
        private String pageName;
        /** pageVoDefined 字段。 */
        private Boolean pageVoDefined;
        /** apiContractDefined 字段。 */
        private Boolean apiContractDefined;
        /** aggregatorDefined 字段。 */
        private Boolean aggregatorDefined;
        /** mysqlMappingDefined 字段。 */
        private Boolean mysqlMappingDefined;
        /** clickhouseMappingDefined 字段。 */
        private Boolean clickhouseMappingDefined;
        /** 审计是否通过。 */
        private Boolean auditPassed;
        /** blockerIssueCount 字段。 */
        private Integer blockerIssueCount;
        /** auditText 字段。 */
        private String auditText;

    }

    @Data
    public static class ModuleAuditStatVO {
        /** moduleName 字段。 */
        private String moduleName;
        /** auditTaskCount 字段。 */
        private Integer auditTaskCount;
        /** issueCount 字段。 */
        private Integer issueCount;
        /** blockerIssueCount 字段。 */
        private Integer blockerIssueCount;
        /** passRate 字段。 */
        private BigDecimal passRate;
        /** releaseAllowed 字段。 */
        private Boolean releaseAllowed;
        /** moduleRiskText 字段。 */
        private String moduleRiskText;

    }

    @Data
    public static class AgentAuditIssueVO {
        /** issueId 字段。 */
        private Long issueId;
        /** auditTaskId 字段。 */
        private Long auditTaskId;
        /** issueCode 字段。 */
        private String issueCode;
        /** issueName 字段。 */
        private String issueName;
        /** issueType 字段。 */
        private String issueType;
        /** issueLevel 字段。 */
        private String issueLevel;
        /** blockMerge 字段。 */
        private Boolean blockMerge;
        /** blockRelease 字段。 */
        private Boolean blockRelease;
        /** filePath 字段。 */
        private String filePath;
        /** startLine 字段。 */
        private Integer startLine;
        /** issueDescription 字段。 */
        private String issueDescription;
        /** fixSuggestion 字段。 */
        private String fixSuggestion;
        /** issueStatus 字段。 */
        private String issueStatus;

    }

    @Data
    public static class AgentAuditRuleCoverageVO {
        /** auditRuleCode 字段。 */
        private String auditRuleCode;
        /** auditRuleName 字段。 */
        private String auditRuleName;
        /** auditRuleType 字段。 */
        private String auditRuleType;
        /** enabled 字段。 */
        private Boolean enabled;
        /** redLineRule 字段。 */
        private Boolean redLineRule;
        /** blockRelease 字段。 */
        private Boolean blockRelease;
        /** coveredObjectCount 字段。 */
        private Integer coveredObjectCount;
        /** hitIssueCount 字段。 */
        private Integer hitIssueCount;
        /** coverageRate 字段。 */
        private BigDecimal coverageRate;

    }

    @Data
    public static class AgentReleaseGateCheckVO {
        /** checkCode 字段。 */
        private String checkCode;
        /** checkName 字段。 */
        private String checkName;
        /** checkType 字段。 */
        private String checkType;
        /** 是否必填。 */
        private Boolean required;
        /** passed 字段。 */
        private Boolean passed;
        /** blockRelease 字段。 */
        private Boolean blockRelease;
        /** failedReason 字段。 */
        private String failedReason;
        /** fixSuggestion 字段。 */
        private String fixSuggestion;

    }

    @Data
    public static class AgentAuditFixSuggestionVO {
        /** suggestionId 字段。 */
        private Long suggestionId;
        /** issueId 字段。 */
        private Long issueId;
        /** fixType 字段。 */
        private String fixType;
        /** priority 字段。 */
        private String priority;
        /** targetFilePath 字段。 */
        private String targetFilePath;
        /** suggestedFixText 字段。 */
        private String suggestedFixText;
        /** acceptanceCriteria 字段。 */
        private String acceptanceCriteria;
        /** autoFixAllowed 字段。 */
        private Boolean autoFixAllowed;

    }

}
