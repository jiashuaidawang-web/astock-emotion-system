package com.astock.module.rule.api.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * RuleVersionManagePageVO 数据载体。
 */
@Data
public class RuleVersionManagePageVO {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** overview 字段。 */
    private RuleVersionOverviewVO overview;
    /** ruleDefinitions 字段。 */
    private List<RuleDefinitionVO> ruleDefinitions;
    /** versions 字段。 */
    private List<RuleVersionVO> versions;
    /** activeVersions 字段。 */
    private List<RuleVersionVO> activeVersions;
    /** publishChecks 字段。 */
    private List<RulePublishCheckVO> publishChecks;
    /** backtestChecks 字段。 */
    private List<RuleBacktestCheckVO> backtestChecks;
    /** auditLogs 字段。 */
    private List<RuleVersionAuditLogVO> auditLogs;
    /** compareResults 字段。 */
    private List<RuleVersionCompareVO> compareResults;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;


    @Data
    public static class RuleVersionOverviewVO {
        /** ruleCount 字段。 */
        private Integer ruleCount;
        /** versionCount 字段。 */
        private Integer versionCount;
        /** draftCount 字段。 */
        private Integer draftCount;
        /** publishedCount 字段。 */
        private Integer publishedCount;
        /** activeCount 字段。 */
        private Integer activeCount;
        /** publishBlockedCount 字段。 */
        private Integer publishBlockedCount;
        /** overviewText 字段。 */
        private String overviewText;

    }

    @Data
    public static class RuleDefinitionVO {
        /** ruleId 字段。 */
        private Long ruleId;
        /** 规则编码。 */
        private String ruleCode;
        /** 规则名称。 */
        private String ruleName;
        /** ruleType 字段。 */
        private String ruleType;
        /** enabled 字段。 */
        private Boolean enabled;
        /** backtestRequired 字段。 */
        private Boolean backtestRequired;
        /** agentAuditRequired 字段。 */
        private Boolean agentAuditRequired;

    }

    @Data
    public static class RuleVersionVO {
        /** versionId 字段。 */
        private Long versionId;
        /** 规则编码。 */
        private String ruleCode;
        /** 版本号。 */
        private String versionNo;
        /** 版本名称。 */
        private String versionName;
        /** 版本状态。 */
        private String versionStatus;
        /** active 字段。 */
        private Boolean active;
        /** backtestCheckPassed 字段。 */
        private Boolean backtestCheckPassed;
        /** publishCheckPassed 字段。 */
        private Boolean publishCheckPassed;
        /** agentAuditPassed 字段。 */
        private Boolean agentAuditPassed;
        /** versionDescription 字段。 */
        private String versionDescription;

    }

    @Data
    public static class RulePublishCheckVO {
        /** versionId 字段。 */
        private Long versionId;
        /** checkCode 字段。 */
        private String checkCode;
        /** checkName 字段。 */
        private String checkName;
        /** passed 字段。 */
        private Boolean passed;
        /** blockPublish 字段。 */
        private Boolean blockPublish;
        /** failedReason 字段。 */
        private String failedReason;
        /** fixSuggestion 字段。 */
        private String fixSuggestion;

    }

    @Data
    public static class RuleBacktestCheckVO {
        /** versionId 字段。 */
        private Long versionId;
        /** backtestPassed 字段。 */
        private Boolean backtestPassed;
        /** latestReportId 字段。 */
        private Long latestReportId;
        /** checkText 字段。 */
        private String checkText;

    }

    @Data
    public static class RuleVersionAuditLogVO {
        /** versionId 字段。 */
        private Long versionId;
        /** operationType 字段。 */
        private String operationType;
        /** operator 字段。 */
        private String operator;
        /** operatedAt 字段。 */
        private LocalDateTime operatedAt;
        /** operationRemark 字段。 */
        private String operationRemark;

    }

    @Data
    public static class RuleVersionCompareVO {
        /** baseVersionId 字段。 */
        private Long baseVersionId;
        /** compareVersionId 字段。 */
        private Long compareVersionId;
        /** diffText 字段。 */
        private String diffText;
        /** riskText 字段。 */
        private String riskText;

    }

}
