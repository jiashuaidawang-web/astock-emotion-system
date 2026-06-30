package com.astock.module.market.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * MarketDashboardVO 数据载体。
 */
@Data
public class MarketDashboardVO {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** emotionStage 字段。 */
    private EmotionStageSummaryVO emotionStage;
    /** riskSummary 字段。 */
    private RiskSummaryVO riskSummary;
    /** marketBreadth 字段。 */
    private MarketBreadthVO marketBreadth;
    /** limitUpDownEco 字段。 */
    private LimitUpDownEcoVO limitUpDownEco;
    /** turnoverSummary 字段。 */
    private TurnoverSummaryVO turnoverSummary;
    /** similaritySummary 字段。 */
    private HistoricalSimilaritySummaryVO similaritySummary;
    /** topMainlines 字段。 */
    private List<MainlineBriefVO> topMainlines;
    /** leaderWatchList 字段。 */
    private List<LeaderBriefVO> leaderWatchList;
    /** patternSignalSummary 字段。 */
    private PatternSignalSummaryVO patternSignalSummary;
    /** riskVetoSummary 字段。 */
    private RiskVetoSummaryVO riskVetoSummary;
    /** 摘要文本。 */
    private String summaryText;
    /** keyEvidences 字段。 */
    private List<String> keyEvidences;
    /** keyRisks 字段。 */
    private List<String> keyRisks;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;


    @Data
    public static class EmotionStageSummaryVO {
        /** stageCode 字段。 */
        private String stageCode;
        /** stageName 字段。 */
        private String stageName;
        /** confidence 字段。 */
        private BigDecimal confidence;
        /** secondCandidateStage 字段。 */
        private String secondCandidateStage;
        /** thirdCandidateStage 字段。 */
        private String thirdCandidateStage;
        /** stageText 字段。 */
        private String stageText;

    }

    @Data
    public static class RiskSummaryVO {
        /** riskLevel 字段。 */
        private String riskLevel;
        /** riskScore 字段。 */
        private BigDecimal riskScore;
        /** riskAction 字段。 */
        private String riskAction;
        /** oneVoteVetoTriggered 字段。 */
        private Boolean oneVoteVetoTriggered;
        /** riskText 字段。 */
        private String riskText;

    }

    @Data
    public static class MarketBreadthVO {
        /** riseCount 字段。 */
        private Integer riseCount;
        /** fallCount 字段。 */
        private Integer fallCount;
        /** flatCount 字段。 */
        private Integer flatCount;
        /** marketBreadthScore 字段。 */
        private BigDecimal marketBreadthScore;
        /** profitEffectScore 字段。 */
        private BigDecimal profitEffectScore;
        /** lossEffectScore 字段。 */
        private BigDecimal lossEffectScore;

    }

    @Data
    public static class LimitUpDownEcoVO {
        /** limitUpCount 字段。 */
        private Integer limitUpCount;
        /** limitDownCount 字段。 */
        private Integer limitDownCount;
        /** breakBoardCount 字段。 */
        private Integer breakBoardCount;
        /** breakBoardRate 字段。 */
        private BigDecimal breakBoardRate;
        /** maxBoardHeight 字段。 */
        private Integer maxBoardHeight;
        /** promotionRate 字段。 */
        private BigDecimal promotionRate;

    }

    @Data
    public static class TurnoverSummaryVO {
        /** totalTurnoverAmount 字段。 */
        private BigDecimal totalTurnoverAmount;
        /** turnoverPercentile 字段。 */
        private BigDecimal turnoverPercentile;
        /** turnoverText 字段。 */
        private String turnoverText;

    }

    @Data
    public static class HistoricalSimilaritySummaryVO {
        /** topMatchId 字段。 */
        private Long topMatchId;
        /** historicalTradeDate 字段。 */
        private LocalDate historicalTradeDate;
        /** similarityScore 字段。 */
        private BigDecimal similarityScore;
        /** matchType 字段。 */
        private String matchType;
        /** 摘要文本。 */
        private String summaryText;

    }

    @Data
    public static class MainlineBriefVO {
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** mainlineName 字段。 */
        private String mainlineName;
        /** lifecycleStage 字段。 */
        private String lifecycleStage;
        /** strengthScore 字段。 */
        private BigDecimal strengthScore;
        /** leaderStockName 字段。 */
        private String leaderStockName;

    }

    @Data
    public static class LeaderBriefVO {
        /** stockCode 字段。 */
        private String stockCode;
        /** stockName 字段。 */
        private String stockName;
        /** leaderType 字段。 */
        private String leaderType;
        /** leaderScore 字段。 */
        private BigDecimal leaderScore;
        /** riskVeto 字段。 */
        private Boolean riskVeto;

    }

    @Data
    public static class PatternSignalSummaryVO {
        /** totalSignalCount 字段。 */
        private Integer totalSignalCount;
        /** conditionMetCount 字段。 */
        private Integer conditionMetCount;
        /** riskVetoCount 字段。 */
        private Integer riskVetoCount;
        /** invalidatedCount 字段。 */
        private Integer invalidatedCount;

    }

    @Data
    public static class RiskVetoSummaryVO {
        /** vetoCount 字段。 */
        private Integer vetoCount;
        /** majorVetoReason 字段。 */
        private String majorVetoReason;
        /** vetoText 字段。 */
        private String vetoText;

    }

}
