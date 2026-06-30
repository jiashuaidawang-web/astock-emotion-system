package com.astock.module.emotion.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * EmotionCycleStateMachineVO 数据载体。
 */
@Data
public class EmotionCycleStateMachineVO {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** currentStage 字段。 */
    private CurrentEmotionStageVO currentStage;
    /** stageScores 字段。 */
    private List<EmotionStageScoreVO> stageScores;
    /** recentStagePath 字段。 */
    private List<EmotionStagePathPointVO> recentStagePath;
    /** possibleTransitions 字段。 */
    private List<EmotionStageTransitionVO> possibleTransitions;
    /** evidence 字段。 */
    private EmotionStageEvidenceVO evidence;
    /** similarSamples 字段。 */
    private List<HistoricalStageSampleVO> similarSamples;
    /** followingStat 字段。 */
    private StageFollowingStatVO followingStat;
    /** contradictions 字段。 */
    private List<StageContradictionVO> contradictions;
    /** manualAdjustment 字段。 */
    private ManualStageAdjustmentVO manualAdjustment;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;


    @Data
    public static class CurrentEmotionStageVO {
        /** stageCode 字段。 */
        private String stageCode;
        /** stageName 字段。 */
        private String stageName;
        /** stageScore 字段。 */
        private BigDecimal stageScore;
        /** confidence 字段。 */
        private BigDecimal confidence;
        /** stageText 字段。 */
        private String stageText;

    }

    @Data
    public static class EmotionStageScoreVO {
        /** stageCode 字段。 */
        private String stageCode;
        /** stageName 字段。 */
        private String stageName;
        /** stageScore 字段。 */
        private BigDecimal stageScore;
        /** rankNo 字段。 */
        private Integer rankNo;
        /** factorPercentileMatchScore 字段。 */
        private BigDecimal factorPercentileMatchScore;
        /** historicalSampleSimilarityScore 字段。 */
        private BigDecimal historicalSampleSimilarityScore;
        /** stagePathMatchScore 字段。 */
        private BigDecimal stagePathMatchScore;
        /** followingValidationScore 字段。 */
        private BigDecimal followingValidationScore;
        /** manualSampleCorrectionScore 字段。 */
        private BigDecimal manualSampleCorrectionScore;
        /** primary 字段。 */
        private Boolean primary;
        /** candidate 字段。 */
        private Boolean candidate;
        /** scoreExplanation 字段。 */
        private String scoreExplanation;

    }

    @Data
    public static class EmotionStagePathPointVO {
        /** 交易日。 */
        private LocalDate tradeDate;
        /** stageCode 字段。 */
        private String stageCode;
        /** stageName 字段。 */
        private String stageName;
        /** confidence 字段。 */
        private BigDecimal confidence;

    }

    @Data
    public static class EmotionStageTransitionVO {
        /** fromStage 字段。 */
        private String fromStage;
        /** toStage 字段。 */
        private String toStage;
        /** transitionProbability 字段。 */
        private BigDecimal transitionProbability;
        /** transitionScore 字段。 */
        private BigDecimal transitionScore;
        /** transitionText 字段。 */
        private String transitionText;

    }

    @Data
    public static class EmotionStageEvidenceVO {
        /** factorEvidences 字段。 */
        private List<String> factorEvidences;
        /** sampleEvidences 字段。 */
        private List<String> sampleEvidences;
        /** pathEvidences 字段。 */
        private List<String> pathEvidences;
        /** evidenceSummary 字段。 */
        private String evidenceSummary;

    }

    @Data
    public static class HistoricalStageSampleVO {
        /** sampleId 字段。 */
        private Long sampleId;
        /** 交易日。 */
        private LocalDate tradeDate;
        /** stageCode 字段。 */
        private String stageCode;
        /** similarityScore 字段。 */
        private BigDecimal similarityScore;
        /** manuallyConfirmed 字段。 */
        private Boolean manuallyConfirmed;
        /** sampleText 字段。 */
        private String sampleText;

    }

    @Data
    public static class StageFollowingStatVO {
        /** sampleCount 字段。 */
        private Integer sampleCount;
        /** future3dAvgReturn 字段。 */
        private BigDecimal future3dAvgReturn;
        /** future5dAvgReturn 字段。 */
        private BigDecimal future5dAvgReturn;
        /** maxDrawdown 字段。 */
        private BigDecimal maxDrawdown;
        /** statText 字段。 */
        private String statText;

    }

    @Data
    public static class StageContradictionVO {
        /** contradictionCode 字段。 */
        private String contradictionCode;
        /** contradictionName 字段。 */
        private String contradictionName;
        /** description 字段。 */
        private String description;
        /** impact 字段。 */
        private String impact;

    }

    @Data
    public static class ManualStageAdjustmentVO {
        /** adjusted 字段。 */
        private Boolean adjusted;
        /** systemStage 字段。 */
        private String systemStage;
        /** manualStage 字段。 */
        private String manualStage;
        /** adjustReason 字段。 */
        private String adjustReason;
        /** adjustedBy 字段。 */
        private String adjustedBy;

    }

}
