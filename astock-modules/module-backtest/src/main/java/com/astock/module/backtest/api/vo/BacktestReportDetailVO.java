package com.astock.module.backtest.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * BacktestReportDetailVO 数据载体。
 */
@Data
public class BacktestReportDetailVO {
    /** reportId 字段。 */
    private Long reportId;
    /** 任务ID。 */
    private Long taskId;
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** header 字段。 */
    private BacktestReportHeaderVO header;
    /** taskParam 字段。 */
    private BacktestTaskParamVO taskParam;
    /** metricSummary 字段。 */
    private BacktestMetricSummaryVO metricSummary;
    /** metricCards 字段。 */
    private List<BacktestMetricCardVO> metricCards;
    /** equityCurve 字段。 */
    private List<BacktestEquityCurvePointVO> equityCurve;
    /** layerStats 字段。 */
    private List<BacktestLayerStatVO> layerStats;
    /** riskEffect 字段。 */
    private BacktestRiskEffectVO riskEffect;
    /** failureCases 字段。 */
    private List<BacktestFailureCaseVO> failureCases;
    /** ruleVersionSnapshot 字段。 */
    private BacktestRuleVersionSnapshotVO ruleVersionSnapshot;
    /** ruleVersionDiff 字段。 */
    private BacktestRuleVersionDiffVO ruleVersionDiff;
    /** dataQuality 字段。 */
    private BacktestDataQualitySummaryVO dataQuality;
    /** futureLeakageSummary 字段。 */
    private BacktestFutureLeakageSummaryVO futureLeakageSummary;
    /** optimizationAdvices 字段。 */
    private List<BacktestOptimizationAdviceVO> optimizationAdvices;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;


    @Data
    public static class BacktestReportHeaderVO {
        /** reportId 字段。 */
        private Long reportId;
        /** reportName 字段。 */
        private String reportName;
        /** reportStatus 字段。 */
        private String reportStatus;
        /** objectType 字段。 */
        private String objectType;
        /** credibilityLevel 字段。 */
        private String credibilityLevel;
        /** dataCheckPassed 字段。 */
        private Boolean dataCheckPassed;
        /** 未来函数检查是否通过。 */
        private Boolean futureLeakageCheckPassed;
        /** generatedAt 字段。 */
        private LocalDateTime generatedAt;
        /** reportSummary 字段。 */
        private String reportSummary;

    }

    @Data
    public static class BacktestTaskParamVO {
        /** 任务ID。 */
        private Long taskId;
        /** objectType 字段。 */
        private String objectType;
        /** 规则版本ID。 */
        private Long ruleVersionId;
        /** compareRuleVersionId 字段。 */
        private Long compareRuleVersionId;
        /** startDate 字段。 */
        private LocalDate startDate;
        /** endDate 字段。 */
        private LocalDate endDate;
        /** layerDimensions 字段。 */
        private List<String> layerDimensions;
        /** metrics 字段。 */
        private List<String> metrics;
        /** paramText 字段。 */
        private String paramText;

    }

    @Data
    public static class BacktestMetricSummaryVO {
        /** signalCount 字段。 */
        private Integer signalCount;
        /** validSignalCount 字段。 */
        private Integer validSignalCount;
        /** failureCaseCount 字段。 */
        private Integer failureCaseCount;
        /** future1dAvgReturn 字段。 */
        private BigDecimal future1dAvgReturn;
        /** future3dAvgReturn 字段。 */
        private BigDecimal future3dAvgReturn;
        /** future5dAvgReturn 字段。 */
        private BigDecimal future5dAvgReturn;
        /** maxDrawdown 字段。 */
        private BigDecimal maxDrawdown;
        /** winRate 字段。 */
        private BigDecimal winRate;
        /** riskVetoEffectScore 字段。 */
        private BigDecimal riskVetoEffectScore;

    }

    @Data
    public static class BacktestMetricCardVO {
        /** metricCode 字段。 */
        private String metricCode;
        /** metricName 字段。 */
        private String metricName;
        /** metricValue 字段。 */
        private String metricValue;
        /** metricLevel 字段。 */
        private String metricLevel;
        /** metricText 字段。 */
        private String metricText;

    }

    @Data
    public static class BacktestEquityCurvePointVO {
        /** 交易日。 */
        private LocalDate tradeDate;
        /** equityValue 字段。 */
        private BigDecimal equityValue;
        /** drawdown 字段。 */
        private BigDecimal drawdown;
        /** pointText 字段。 */
        private String pointText;

    }

    @Data
    public static class BacktestLayerStatVO {
        /** layerDimension 字段。 */
        private String layerDimension;
        /** layerValue 字段。 */
        private String layerValue;
        /** sampleCount 字段。 */
        private Integer sampleCount;
        /** future3dAvgReturn 字段。 */
        private BigDecimal future3dAvgReturn;
        /** maxDrawdown 字段。 */
        private BigDecimal maxDrawdown;
        /** winRate 字段。 */
        private BigDecimal winRate;
        /** layerText 字段。 */
        private String layerText;

    }

    @Data
    public static class BacktestRiskEffectVO {
        /** riskVetoCount 字段。 */
        private Integer riskVetoCount;
        /** vetoEffectScore 字段。 */
        private BigDecimal vetoEffectScore;
        /** riskEffectText 字段。 */
        private String riskEffectText;

    }

    @Data
    public static class BacktestFailureCaseVO {
        /** caseId 字段。 */
        private Long caseId;
        /** 交易日。 */
        private LocalDate tradeDate;
        /** stockCode 字段。 */
        private String stockCode;
        /** 失败原因。 */
        private String failureReason;
        /** maxDrawdown 字段。 */
        private BigDecimal maxDrawdown;
        /** caseText 字段。 */
        private String caseText;

    }

    @Data
    public static class BacktestRuleVersionSnapshotVO {
        /** 规则版本ID。 */
        private Long ruleVersionId;
        /** 规则编码。 */
        private String ruleCode;
        /** 版本号。 */
        private String versionNo;
        /** configSnapshotText 字段。 */
        private String configSnapshotText;

    }

    @Data
    public static class BacktestRuleVersionDiffVO {
        /** baseVersionId 字段。 */
        private Long baseVersionId;
        /** compareVersionId 字段。 */
        private Long compareVersionId;
        /** diffText 字段。 */
        private String diffText;

    }

    @Data
    public static class BacktestDataQualitySummaryVO {
        /** dataCheckPassed 字段。 */
        private Boolean dataCheckPassed;
        /** 完整率。 */
        private BigDecimal completenessRatio;
        /** qualityText 字段。 */
        private String qualityText;

    }

    @Data
    public static class BacktestFutureLeakageSummaryVO {
        /** 未来函数检查是否通过。 */
        private Boolean futureLeakageCheckPassed;
        /** failedCheckCount 字段。 */
        private Integer failedCheckCount;
        /** 摘要文本。 */
        private String summaryText;

    }

    @Data
    public static class BacktestOptimizationAdviceVO {
        /** adviceCode 字段。 */
        private String adviceCode;
        /** adviceType 字段。 */
        private String adviceType;
        /** priority 字段。 */
        private String priority;
        /** adviceText 字段。 */
        private String adviceText;
        /** createRuleDraftAllowed 字段。 */
        private Boolean createRuleDraftAllowed;

    }

}
