package com.astock.module.backtest.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * BacktestLabPageVO 数据载体。
 */
@Data
public class BacktestLabPageVO {
    /** defaultStartDate 字段。 */
    private LocalDate defaultStartDate;
    /** defaultEndDate 字段。 */
    private LocalDate defaultEndDate;
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** overview 字段。 */
    private BacktestLabOverviewVO overview;
    /** createConfig 字段。 */
    private BacktestTaskCreateConfigVO createConfig;
    /** supportedObjectTypes 字段。 */
    private List<BacktestObjectTypeVO> supportedObjectTypes;
    /** ruleVersions 字段。 */
    private List<RuleVersionBriefVO> ruleVersions;
    /** recentTasks 字段。 */
    private List<BacktestTaskVO> recentTasks;
    /** runningTasks 字段。 */
    private List<BacktestTaskVO> runningTasks;
    /** dataCoverages 字段。 */
    private List<BacktestDataCoverageVO> dataCoverages;
    /** futureLeakageChecks 字段。 */
    private List<FutureLeakageCheckVO> futureLeakageChecks;
    /** presetTemplates 字段。 */
    private List<BacktestPresetTemplateVO> presetTemplates;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;


    @Data
    public static class BacktestLabOverviewVO {
        /** totalTaskCount 字段。 */
        private Integer totalTaskCount;
        /** runningTaskCount 字段。 */
        private Integer runningTaskCount;
        /** successTaskCount 字段。 */
        private Integer successTaskCount;
        /** failedTaskCount 字段。 */
        private Integer failedTaskCount;
        /** latestSuccessTaskId 字段。 */
        private Long latestSuccessTaskId;
        /** latestSuccessReportId 字段。 */
        private Long latestSuccessReportId;
        /** dataCoverageStartDate 字段。 */
        private LocalDate dataCoverageStartDate;
        /** dataCoverageEndDate 字段。 */
        private LocalDate dataCoverageEndDate;
        /** overviewText 字段。 */
        private String overviewText;

    }

    @Data
    public static class BacktestTaskCreateConfigVO {
        /** createAllowed 字段。 */
        private Boolean createAllowed;
        /** disabledReason 字段。 */
        private String disabledReason;
        /** defaultTaskName 字段。 */
        private String defaultTaskName;
        /** defaultObjectType 字段。 */
        private String defaultObjectType;
        /** defaultRuleVersionId 字段。 */
        private Long defaultRuleVersionId;
        /** defaultStartDate 字段。 */
        private LocalDate defaultStartDate;
        /** defaultEndDate 字段。 */
        private LocalDate defaultEndDate;
        /** defaultFutureLeakageCheckEnabled 字段。 */
        private Boolean defaultFutureLeakageCheckEnabled;
        /** defaultSaveFailureCases 字段。 */
        private Boolean defaultSaveFailureCases;
        /** defaultGenerateReport 字段。 */
        private Boolean defaultGenerateReport;
        /** configText 字段。 */
        private String configText;

    }

    @Data
    public static class BacktestObjectTypeVO {
        /** objectType 字段。 */
        private String objectType;
        /** objectTypeName 字段。 */
        private String objectTypeName;
        /** enabled 字段。 */
        private Boolean enabled;
        /** disabledReason 字段。 */
        private String disabledReason;
        /** requireCycleSample 字段。 */
        private Boolean requireCycleSample;
        /** requirePatternSignalSnapshot 字段。 */
        private Boolean requirePatternSignalSnapshot;
        /** requireRiskSignalSnapshot 字段。 */
        private Boolean requireRiskSignalSnapshot;
        /** supportedLayerDimensions 字段。 */
        private List<String> supportedLayerDimensions;
        /** supportedMetrics 字段。 */
        private List<String> supportedMetrics;

    }

    @Data
    public static class RuleVersionBriefVO {
        /** 规则版本ID。 */
        private Long ruleVersionId;
        /** 规则编码。 */
        private String ruleCode;
        /** 规则名称。 */
        private String ruleName;
        /** ruleType 字段。 */
        private String ruleType;
        /** 版本号。 */
        private String versionNo;
        /** 版本名称。 */
        private String versionName;
        /** active 字段。 */
        private Boolean active;
        /** backtestEnabled 字段。 */
        private Boolean backtestEnabled;
        /** latestBacktestStatus 字段。 */
        private String latestBacktestStatus;

    }

    @Data
    public static class BacktestTaskVO {
        /** 任务ID。 */
        private Long taskId;
        /** 任务名称。 */
        private String taskName;
        /** objectType 字段。 */
        private String objectType;
        /** 规则版本ID。 */
        private Long ruleVersionId;
        /** startDate 字段。 */
        private LocalDate startDate;
        /** endDate 字段。 */
        private LocalDate endDate;
        /** sampleCount 字段。 */
        private Integer sampleCount;
        /** 任务状态。 */
        private String taskStatus;
        /** progressPercent 字段。 */
        private BigDecimal progressPercent;
        /** 未来函数检查是否通过。 */
        private Boolean futureLeakageCheckPassed;
        /** dataCheckPassed 字段。 */
        private Boolean dataCheckPassed;
        /** reportGenerated 字段。 */
        private Boolean reportGenerated;
        /** reportId 字段。 */
        private Long reportId;
        /** taskSummary 字段。 */
        private String taskSummary;

    }

    @Data
    public static class BacktestDataCoverageVO {
        /** dataDomain 字段。 */
        private String dataDomain;
        /** coverageStartDate 字段。 */
        private LocalDate coverageStartDate;
        /** coverageEndDate 字段。 */
        private LocalDate coverageEndDate;
        /** expectedTradeDayCount 字段。 */
        private Integer expectedTradeDayCount;
        /** actualTradeDayCount 字段。 */
        private Integer actualTradeDayCount;
        /** 完整率。 */
        private BigDecimal completenessRatio;
        /** backtestAvailable 字段。 */
        private Boolean backtestAvailable;
        /** coverageText 字段。 */
        private String coverageText;

    }

    @Data
    public static class FutureLeakageCheckVO {
        /** checkCode 字段。 */
        private String checkCode;
        /** checkName 字段。 */
        private String checkName;
        /** passed 字段。 */
        private Boolean passed;
        /** riskLevel 字段。 */
        private String riskLevel;
        /** relatedField 字段。 */
        private String relatedField;
        /** relatedTable 字段。 */
        private String relatedTable;
        /** 失败原因。 */
        private String failureReason;
        /** fixSuggestion 字段。 */
        private String fixSuggestion;

    }

    @Data
    public static class BacktestPresetTemplateVO {
        /** templateId 字段。 */
        private Long templateId;
        /** templateCode 字段。 */
        private String templateCode;
        /** templateName 字段。 */
        private String templateName;
        /** objectType 字段。 */
        private String objectType;
        /** defaultRuleVersionId 字段。 */
        private Long defaultRuleVersionId;
        /** systemBuiltIn 字段。 */
        private Boolean systemBuiltIn;
        /** templateText 字段。 */
        private String templateText;

    }

}
