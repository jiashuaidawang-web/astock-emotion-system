package com.astock.module.leader.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * LeaderProfilePageVO 数据载体。
 */
@Data
public class LeaderProfilePageVO {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** stockCode 字段。 */
    private String stockCode;
    /** stockName 字段。 */
    private String stockName;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** identity 字段。 */
    private LeaderIdentityVO identity;
    /** scoreBreakdown 字段。 */
    private LeaderScoreBreakdownVO scoreBreakdown;
    /** marketData 字段。 */
    private LeaderMarketDataVO marketData;
    /** mainlineRelation 字段。 */
    private LeaderMainlineRelationVO mainlineRelation;
    /** boardStructure 字段。 */
    private LeaderBoardStructureVO boardStructure;
    /** trendStructure 字段。 */
    private LeaderTrendStructureVO trendStructure;
    /** driveAnalysis 字段。 */
    private LeaderDriveAnalysisVO driveAnalysis;
    /** negativeFeedback 字段。 */
    private LeaderNegativeFeedbackAnalysisVO negativeFeedback;
    /** lifecycle 字段。 */
    private List<LeaderLifecyclePointVO> lifecycle;
    /** relatedPatternSignals 字段。 */
    private List<LeaderRelatedPatternSignalVO> relatedPatternSignals;
    /** relatedRiskSignals 字段。 */
    private List<LeaderRelatedRiskSignalVO> relatedRiskSignals;
    /** similarLeaderSamples 字段。 */
    private List<HistoricalLeaderSampleVO> similarLeaderSamples;
    /** evidenceChain 字段。 */
    private LeaderEvidenceChainVO evidenceChain;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;


    @Data
    public static class LeaderIdentityVO {
        /** leaderType 字段。 */
        private String leaderType;
        /** leaderStatus 字段。 */
        private String leaderStatus;
        /** roleInMainline 字段。 */
        private String roleInMainline;
        /** inPatternWatchPool 字段。 */
        private Boolean inPatternWatchPool;
        /** riskVeto 字段。 */
        private Boolean riskVeto;
        /** identityText 字段。 */
        private String identityText;

    }

    @Data
    public static class LeaderScoreBreakdownVO {
        /** leaderScore 字段。 */
        private BigDecimal leaderScore;
        /** recognitionScore 字段。 */
        private BigDecimal recognitionScore;
        /** mainlineRelationScore 字段。 */
        private BigDecimal mainlineRelationScore;
        /** driveScore 字段。 */
        private BigDecimal driveScore;
        /** strengthScore 字段。 */
        private BigDecimal strengthScore;
        /** supportScore 字段。 */
        private BigDecimal supportScore;
        /** continuityScore 字段。 */
        private BigDecimal continuityScore;
        /** negativeFeedbackScore 字段。 */
        private BigDecimal negativeFeedbackScore;

    }

    @Data
    public static class LeaderMarketDataVO {
        /** closePrice 字段。 */
        private BigDecimal closePrice;
        /** pctChange 字段。 */
        private BigDecimal pctChange;
        /** turnoverAmount 字段。 */
        private BigDecimal turnoverAmount;
        /** turnoverRate 字段。 */
        private BigDecimal turnoverRate;
        /** volumeRatio 字段。 */
        private BigDecimal volumeRatio;

    }

    @Data
    public static class LeaderMainlineRelationVO {
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** mainlineName 字段。 */
        private String mainlineName;
        /** lifecycleStage 字段。 */
        private String lifecycleStage;
        /** relationScore 字段。 */
        private BigDecimal relationScore;
        /** relationText 字段。 */
        private String relationText;

    }

    @Data
    public static class LeaderBoardStructureVO {
        /** boardHeight 字段。 */
        private Integer boardHeight;
        /** limitUp 字段。 */
        private Boolean limitUp;
        /** brokenBoard 字段。 */
        private Boolean brokenBoard;
        /** reversalBoard 字段。 */
        private Boolean reversalBoard;
        /** boardText 字段。 */
        private String boardText;

    }

    @Data
    public static class LeaderTrendStructureVO {
        /** trendLeaderType 字段。 */
        private String trendLeaderType;
        /** trendPosition 字段。 */
        private String trendPosition;
        /** trendStrengthScore 字段。 */
        private BigDecimal trendStrengthScore;
        /** trendBroken 字段。 */
        private Boolean trendBroken;
        /** trendText 字段。 */
        private String trendText;

    }

    @Data
    public static class LeaderDriveAnalysisVO {
        /** leaderDriveScore 字段。 */
        private BigDecimal leaderDriveScore;
        /** sectorDriveScore 字段。 */
        private BigDecimal sectorDriveScore;
        /** mainlineDriveScore 字段。 */
        private BigDecimal mainlineDriveScore;
        /** emotionDriveScore 字段。 */
        private BigDecimal emotionDriveScore;
        /** fundDriveScore 字段。 */
        private BigDecimal fundDriveScore;
        /** driveText 字段。 */
        private String driveText;

    }

    @Data
    public static class LeaderNegativeFeedbackAnalysisVO {
        /** negativeFeedbackScore 字段。 */
        private BigDecimal negativeFeedbackScore;
        /** brokenBoard 字段。 */
        private Boolean brokenBoard;
        /** limitDown 字段。 */
        private Boolean limitDown;
        /** impactMainline 字段。 */
        private Boolean impactMainline;
        /** impactEmotionCycle 字段。 */
        private Boolean impactEmotionCycle;
        /** feedbackText 字段。 */
        private String feedbackText;

    }

    @Data
    public static class LeaderLifecyclePointVO {
        /** 交易日。 */
        private LocalDate tradeDate;
        /** leaderType 字段。 */
        private String leaderType;
        /** leaderStatus 字段。 */
        private String leaderStatus;
        /** leaderScore 字段。 */
        private BigDecimal leaderScore;
        /** pointText 字段。 */
        private String pointText;

    }

    @Data
    public static class LeaderRelatedPatternSignalVO {
        /** signalId 字段。 */
        private Long signalId;
        /** patternCode 字段。 */
        private String patternCode;
        /** conditionStatus 字段。 */
        private String conditionStatus;
        /** conditionScore 字段。 */
        private BigDecimal conditionScore;
        /** riskVeto 字段。 */
        private Boolean riskVeto;

    }

    @Data
    public static class LeaderRelatedRiskSignalVO {
        /** riskSignalId 字段。 */
        private Long riskSignalId;
        /** riskCode 字段。 */
        private String riskCode;
        /** riskLevel 字段。 */
        private String riskLevel;
        /** riskScore 字段。 */
        private BigDecimal riskScore;
        /** riskText 字段。 */
        private String riskText;

    }

    @Data
    public static class HistoricalLeaderSampleVO {
        /** sampleId 字段。 */
        private Long sampleId;
        /** 交易日。 */
        private LocalDate tradeDate;
        /** historicalStockName 字段。 */
        private String historicalStockName;
        /** leaderType 字段。 */
        private String leaderType;
        /** similarityScore 字段。 */
        private BigDecimal similarityScore;
        /** future3dReturn 字段。 */
        private BigDecimal future3dReturn;
        /** maxDrawdown 字段。 */
        private BigDecimal maxDrawdown;

    }

    @Data
    public static class LeaderEvidenceChainVO {
        /** identityEvidences 字段。 */
        private List<String> identityEvidences;
        /** driveEvidences 字段。 */
        private List<String> driveEvidences;
        /** riskEvidences 字段。 */
        private List<String> riskEvidences;
        /** evidenceSummary 字段。 */
        private String evidenceSummary;

    }

}
