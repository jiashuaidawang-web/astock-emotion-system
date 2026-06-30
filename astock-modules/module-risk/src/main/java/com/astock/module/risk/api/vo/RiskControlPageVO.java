package com.astock.module.risk.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * RiskControlPageVO 数据载体。
 */
@Data
public class RiskControlPageVO {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** overview 字段。 */
    private RiskControlOverviewVO overview;
    /** scoreBreakdown 字段。 */
    private RiskScoreBreakdownVO scoreBreakdown;
    /** riskSourceGroups 字段。 */
    private List<RiskSourceGroupVO> riskSourceGroups;
    /** riskSignals 字段。 */
    private List<RiskSignalVO> riskSignals;
    /** patternVetos 字段。 */
    private List<PatternRiskVetoVO> patternVetos;
    /** invalidations 字段。 */
    private List<PatternInvalidationVO> invalidations;
    /** leaderFeedbacks 字段。 */
    private List<LeaderNegativeFeedbackVO> leaderFeedbacks;
    /** mainlineRisks 字段。 */
    private List<MainlineRiskVO> mainlineRisks;
    /** dataIntegrityRisks 字段。 */
    private List<DataIntegrityRiskVO> dataIntegrityRisks;
    /** actionMatrix 字段。 */
    private List<RiskActionMatrixVO> actionMatrix;
    /** historicalRiskSamples 字段。 */
    private List<HistoricalRiskSampleVO> historicalRiskSamples;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;


    @Data
    public static class RiskControlOverviewVO {
        /** riskLevel 字段。 */
        private String riskLevel;
        /** riskScore 字段。 */
        private BigDecimal riskScore;
        /** riskAction 字段。 */
        private String riskAction;
        /** emotionStage 字段。 */
        private String emotionStage;
        /** retreatStopTriggered 字段。 */
        private Boolean retreatStopTriggered;
        /** climaxNoChaseTriggered 字段。 */
        private Boolean climaxNoChaseTriggered;
        /** oneVoteVetoTriggered 字段。 */
        private Boolean oneVoteVetoTriggered;
        /** totalRiskSignalCount 字段。 */
        private Integer totalRiskSignalCount;
        /** vetoedPatternCount 字段。 */
        private Integer vetoedPatternCount;
        /** overviewText 字段。 */
        private String overviewText;

    }

    @Data
    public static class RiskScoreBreakdownVO {
        /** riskScore 字段。 */
        private BigDecimal riskScore;
        /** emotionCycleRiskScore 字段。 */
        private BigDecimal emotionCycleRiskScore;
        /** lossEffectRiskScore 字段。 */
        private BigDecimal lossEffectRiskScore;
        /** limitEcoRiskScore 字段。 */
        private BigDecimal limitEcoRiskScore;
        /** leaderFeedbackRiskScore 字段。 */
        private BigDecimal leaderFeedbackRiskScore;
        /** mainlineDecayRiskScore 字段。 */
        private BigDecimal mainlineDecayRiskScore;
        /** indexFundRiskScore 字段。 */
        private BigDecimal indexFundRiskScore;
        /** dataIntegrityRiskScore 字段。 */
        private BigDecimal dataIntegrityRiskScore;
        /** formulaText 字段。 */
        private String formulaText;

    }

    @Data
    public static class RiskSourceGroupVO {
        /** riskSource 字段。 */
        private String riskSource;
        /** sourceRiskScore 字段。 */
        private BigDecimal sourceRiskScore;
        /** riskLevel 字段。 */
        private String riskLevel;
        /** signalCount 字段。 */
        private Integer signalCount;
        /** impactPatternSignal 字段。 */
        private Boolean impactPatternSignal;
        /** groupText 字段。 */
        private String groupText;

    }

    @Data
    public static class RiskSignalVO {
        /** riskSignalId 字段。 */
        private Long riskSignalId;
        /** riskCode 字段。 */
        private String riskCode;
        /** riskName 字段。 */
        private String riskName;
        /** riskSource 字段。 */
        private String riskSource;
        /** signalLevel 字段。 */
        private String signalLevel;
        /** riskLevel 字段。 */
        private String riskLevel;
        /** riskScore 字段。 */
        private BigDecimal riskScore;
        /** riskAction 字段。 */
        private String riskAction;
        /** oneVoteVeto 字段。 */
        private Boolean oneVoteVeto;
        /** riskText 字段。 */
        private String riskText;
        /** evidences 字段。 */
        private List<String> evidences;
        /** impacts 字段。 */
        private List<String> impacts;

    }

    @Data
    public static class PatternRiskVetoVO {
        /** vetoId 字段。 */
        private Long vetoId;
        /** signalId 字段。 */
        private Long signalId;
        /** patternCode 字段。 */
        private String patternCode;
        /** stockCode 字段。 */
        private String stockCode;
        /** riskCode 字段。 */
        private String riskCode;
        /** riskLevel 字段。 */
        private String riskLevel;
        /** riskScore 字段。 */
        private BigDecimal riskScore;
        /** riskAction 字段。 */
        private String riskAction;
        /** oneVoteVeto 字段。 */
        private Boolean oneVoteVeto;
        /** vetoReason 字段。 */
        private String vetoReason;

    }

    @Data
    public static class PatternInvalidationVO {
        /** invalidationId 字段。 */
        private Long invalidationId;
        /** signalId 字段。 */
        private Long signalId;
        /** patternCode 字段。 */
        private String patternCode;
        /** stockCode 字段。 */
        private String stockCode;
        /** invalidationType 字段。 */
        private String invalidationType;
        /** invalidationReason 字段。 */
        private String invalidationReason;
        /** savedAsFailureCase 字段。 */
        private Boolean savedAsFailureCase;

    }

    @Data
    public static class LeaderNegativeFeedbackVO {
        /** stockCode 字段。 */
        private String stockCode;
        /** stockName 字段。 */
        private String stockName;
        /** leaderType 字段。 */
        private String leaderType;
        /** negativeFeedbackScore 字段。 */
        private BigDecimal negativeFeedbackScore;
        /** triggerRiskControl 字段。 */
        private Boolean triggerRiskControl;
        /** feedbackText 字段。 */
        private String feedbackText;

    }

    @Data
    public static class MainlineRiskVO {
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** mainlineName 字段。 */
        private String mainlineName;
        /** riskType 字段。 */
        private String riskType;
        /** riskLevel 字段。 */
        private String riskLevel;
        /** riskScore 字段。 */
        private BigDecimal riskScore;
        /** riskText 字段。 */
        private String riskText;

    }

    @Data
    public static class DataIntegrityRiskVO {
        /** dataDomain 字段。 */
        private String dataDomain;
        /** 检查状态。 */
        private String checkStatus;
        /** 是否关键数据。 */
        private Boolean critical;
        /** 完整率。 */
        private BigDecimal completenessRatio;
        /** dataRiskText 字段。 */
        private String dataRiskText;

    }

    @Data
    public static class RiskActionMatrixVO {
        /** riskAction 字段。 */
        private String riskAction;
        /** impactObjectType 字段。 */
        private String impactObjectType;
        /** forbidConditionMetDisplay 字段。 */
        private Boolean forbidConditionMetDisplay;
        /** triggerRiskVeto 字段。 */
        private Boolean triggerRiskVeto;
        /** stopObserving 字段。 */
        private Boolean stopObserving;
        /** actionText 字段。 */
        private String actionText;

    }

    @Data
    public static class HistoricalRiskSampleVO {
        /** sampleId 字段。 */
        private Long sampleId;
        /** 交易日。 */
        private LocalDate tradeDate;
        /** emotionStage 字段。 */
        private String emotionStage;
        /** riskLevel 字段。 */
        private String riskLevel;
        /** riskSimilarityScore 字段。 */
        private BigDecimal riskSimilarityScore;
        /** future3dReturn 字段。 */
        private BigDecimal future3dReturn;
        /** maxDrawdown 字段。 */
        private BigDecimal maxDrawdown;
        /** sampleText 字段。 */
        private String sampleText;

    }

}
