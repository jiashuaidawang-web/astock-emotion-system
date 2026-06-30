package com.astock.module.pattern.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * PatternConditionPageVO 数据载体。
 */
@Data
public class PatternConditionPageVO {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** overview 字段。 */
    private PatternConditionOverviewVO overview;
    /** watchPool 字段。 */
    private List<PatternWatchObjectVO> watchPool;
    /** patternTypeStatistics 字段。 */
    private List<PatternTypeStatisticsVO> patternTypeStatistics;
    /** signals 字段。 */
    private List<PatternSignalVO> signals;
    /** conditionMetSignals 字段。 */
    private List<PatternSignalVO> conditionMetSignals;
    /** observingSignals 字段。 */
    private List<PatternSignalVO> observingSignals;
    /** riskVetoSignals 字段。 */
    private List<PatternSignalVO> riskVetoSignals;
    /** invalidatedSignals 字段。 */
    private List<PatternSignalVO> invalidatedSignals;
    /** stageMatrix 字段。 */
    private List<PatternStageMatrixVO> stageMatrix;
    /** backtestSupports 字段。 */
    private List<PatternBacktestSupportVO> backtestSupports;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;


    @Data
    public static class PatternConditionOverviewVO {
        /** watchObjectCount 字段。 */
        private Integer watchObjectCount;
        /** calculatedObjectCount 字段。 */
        private Integer calculatedObjectCount;
        /** totalSignalCount 字段。 */
        private Integer totalSignalCount;
        /** conditionMetCount 字段。 */
        private Integer conditionMetCount;
        /** riskVetoCount 字段。 */
        private Integer riskVetoCount;
        /** invalidatedCount 字段。 */
        private Integer invalidatedCount;
        /** emotionStage 字段。 */
        private String emotionStage;
        /** riskLevel 字段。 */
        private String riskLevel;
        /** retreatStopTriggered 字段。 */
        private Boolean retreatStopTriggered;
        /** climaxNoChaseTriggered 字段。 */
        private Boolean climaxNoChaseTriggered;
        /** overviewText 字段。 */
        private String overviewText;

    }

    @Data
    public static class PatternWatchObjectVO {
        /** stockCode 字段。 */
        private String stockCode;
        /** stockName 字段。 */
        private String stockName;
        /** watchObjectType 字段。 */
        private String watchObjectType;
        /** leaderType 字段。 */
        private String leaderType;
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** mainlineName 字段。 */
        private String mainlineName;
        /** leaderScore 字段。 */
        private BigDecimal leaderScore;
        /** patternCalculationAllowed 字段。 */
        private Boolean patternCalculationAllowed;
        /** hasRiskVeto 字段。 */
        private Boolean hasRiskVeto;
        /** watchObjectText 字段。 */
        private String watchObjectText;

    }

    @Data
    public static class PatternTypeStatisticsVO {
        /** patternCode 字段。 */
        private String patternCode;
        /** patternName 字段。 */
        private String patternName;
        /** applicableObjectCount 字段。 */
        private Integer applicableObjectCount;
        /** conditionMetCount 字段。 */
        private Integer conditionMetCount;
        /** riskVetoCount 字段。 */
        private Integer riskVetoCount;
        /** invalidatedCount 字段。 */
        private Integer invalidatedCount;
        /** avgConditionScore 字段。 */
        private BigDecimal avgConditionScore;
        /** globallyRestricted 字段。 */
        private Boolean globallyRestricted;
        /** statisticsText 字段。 */
        private String statisticsText;

    }

    @Data
    public static class PatternSignalVO {
        /** signalId 字段。 */
        private Long signalId;
        /** patternCode 字段。 */
        private String patternCode;
        /** patternName 字段。 */
        private String patternName;
        /** stockCode 字段。 */
        private String stockCode;
        /** stockName 字段。 */
        private String stockName;
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** mainlineName 字段。 */
        private String mainlineName;
        /** leaderType 字段。 */
        private String leaderType;
        /** emotionStage 字段。 */
        private String emotionStage;
        /** conditionStatus 字段。 */
        private String conditionStatus;
        /** conditionScore 字段。 */
        private BigDecimal conditionScore;
        /** cycleAdmissionScore 字段。 */
        private BigDecimal cycleAdmissionScore;
        /** mainlineValidScore 字段。 */
        private BigDecimal mainlineValidScore;
        /** leaderPositionScore 字段。 */
        private BigDecimal leaderPositionScore;
        /** triggerScore 字段。 */
        private BigDecimal triggerScore;
        /** backtestSupportScore 字段。 */
        private BigDecimal backtestSupportScore;
        /** riskVeto 字段。 */
        private Boolean riskVeto;
        /** riskVetoReason 字段。 */
        private String riskVetoReason;
        /** invalidated 字段。 */
        private Boolean invalidated;
        /** invalidatedReason 字段。 */
        private String invalidatedReason;
        /** allowConditionMetDisplay 字段。 */
        private Boolean allowConditionMetDisplay;
        /** signalText 字段。 */
        private String signalText;

    }

    @Data
    public static class PatternStageMatrixVO {
        /** patternCode 字段。 */
        private String patternCode;
        /** stageCode 字段。 */
        private String stageCode;
        /** applicabilityLevel 字段。 */
        private String applicabilityLevel;
        /** conditionMetAllowed 字段。 */
        private Boolean conditionMetAllowed;
        /** riskConfirmRequired 字段。 */
        private Boolean riskConfirmRequired;
        /** matrixText 字段。 */
        private String matrixText;

    }

    @Data
    public static class PatternBacktestSupportVO {
        /** patternCode 字段。 */
        private String patternCode;
        /** emotionStage 字段。 */
        private String emotionStage;
        /** leaderType 字段。 */
        private String leaderType;
        /** sampleCount 字段。 */
        private Integer sampleCount;
        /** future3dAvgReturn 字段。 */
        private BigDecimal future3dAvgReturn;
        /** winRate 字段。 */
        private BigDecimal winRate;
        /** maxDrawdown 字段。 */
        private BigDecimal maxDrawdown;
        /** backtestSupportScore 字段。 */
        private BigDecimal backtestSupportScore;
        /** backtestText 字段。 */
        private String backtestText;

    }

}
