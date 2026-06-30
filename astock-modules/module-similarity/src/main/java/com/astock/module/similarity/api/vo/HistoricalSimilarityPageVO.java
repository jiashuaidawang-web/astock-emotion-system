package com.astock.module.similarity.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * HistoricalSimilarityPageVO 数据载体。
 */
@Data
public class HistoricalSimilarityPageVO {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** currentProfile 字段。 */
    private CurrentMarketProfileVO currentProfile;
    /** overview 字段。 */
    private SimilarityOverviewVO overview;
    /** singleDayMatches 字段。 */
    private List<SimilarityMatchVO> singleDayMatches;
    /** threeDayMatches 字段。 */
    private List<SimilarityMatchVO> threeDayMatches;
    /** fiveDayMatches 字段。 */
    private List<SimilarityMatchVO> fiveDayMatches;
    /** tenDayMatches 字段。 */
    private List<SimilarityMatchVO> tenDayMatches;
    /** scoreBreakdown 字段。 */
    private SimilarityScoreBreakdownVO scoreBreakdown;
    /** similarFactors 字段。 */
    private List<SimilarityFactorVO> similarFactors;
    /** differentFactors 字段。 */
    private List<SimilarityFactorVO> differentFactors;
    /** followingPerformance 字段。 */
    private FollowingPerformanceVO followingPerformance;
    /** pathComparison 字段。 */
    private List<SimilarityPathPointVO> pathComparison;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;
    /** backtestEntry 字段。 */
    private BacktestEntryVO backtestEntry;


    @Data
    public static class CurrentMarketProfileVO {
        /** emotionStage 字段。 */
        private String emotionStage;
        /** riskScore 字段。 */
        private BigDecimal riskScore;
        /** limitUpCount 字段。 */
        private Integer limitUpCount;
        /** limitDownCount 字段。 */
        private Integer limitDownCount;
        /** turnoverAmount 字段。 */
        private BigDecimal turnoverAmount;
        /** strongestMainline 字段。 */
        private String strongestMainline;

    }

    @Data
    public static class SimilarityOverviewVO {
        /** matchCount 字段。 */
        private Integer matchCount;
        /** topSimilarityScore 字段。 */
        private BigDecimal topSimilarityScore;
        /** topMatchType 字段。 */
        private String topMatchType;
        /** topHistoricalDate 字段。 */
        private LocalDate topHistoricalDate;
        /** overviewText 字段。 */
        private String overviewText;

    }

    @Data
    public static class SimilarityMatchVO {
        /** matchId 字段。 */
        private Long matchId;
        /** matchType 字段。 */
        private String matchType;
        /** sampleId 字段。 */
        private Long sampleId;
        /** historicalTradeDate 字段。 */
        private LocalDate historicalTradeDate;
        /** historicalStage 字段。 */
        private String historicalStage;
        /** totalSimilarityScore 字段。 */
        private BigDecimal totalSimilarityScore;
        /** future1dReturn 字段。 */
        private BigDecimal future1dReturn;
        /** future3dReturn 字段。 */
        private BigDecimal future3dReturn;
        /** future5dReturn 字段。 */
        private BigDecimal future5dReturn;
        /** maxDrawdown 字段。 */
        private BigDecimal maxDrawdown;
        /** referenceText 字段。 */
        private String referenceText;
        /** riskText 字段。 */
        private String riskText;

    }

    @Data
    public static class SimilarityScoreBreakdownVO {
        /** marketEnvSimilarityScore 字段。 */
        private BigDecimal marketEnvSimilarityScore;
        /** emotionCycleSimilarityScore 字段。 */
        private BigDecimal emotionCycleSimilarityScore;
        /** themeLeaderSimilarityScore 字段。 */
        private BigDecimal themeLeaderSimilarityScore;
        /** dimensions 字段。 */
        private List<SimilarityDimensionScoreVO> dimensions;

    }

    @Data
    public static class SimilarityDimensionScoreVO {
        /** dimensionCode 字段。 */
        private String dimensionCode;
        /** dimensionName 字段。 */
        private String dimensionName;
        /** score 字段。 */
        private BigDecimal score;
        /** weight 字段。 */
        private BigDecimal weight;

    }

    @Data
    public static class SimilarityFactorVO {
        /** factorCode 字段。 */
        private String factorCode;
        /** factorName 字段。 */
        private String factorName;
        /** currentValue 字段。 */
        private String currentValue;
        /** historicalValue 字段。 */
        private String historicalValue;
        /** similarityScore 字段。 */
        private BigDecimal similarityScore;
        /** explanation 字段。 */
        private String explanation;

    }

    @Data
    public static class FollowingPerformanceVO {
        /** future1dReturn 字段。 */
        private BigDecimal future1dReturn;
        /** future3dReturn 字段。 */
        private BigDecimal future3dReturn;
        /** future5dReturn 字段。 */
        private BigDecimal future5dReturn;
        /** future10dReturn 字段。 */
        private BigDecimal future10dReturn;
        /** maxDrawdown 字段。 */
        private BigDecimal maxDrawdown;
        /** followingPathText 字段。 */
        private String followingPathText;

    }

    @Data
    public static class SimilarityPathPointVO {
        /** 交易日。 */
        private LocalDate tradeDate;
        /** currentStage 字段。 */
        private String currentStage;
        /** historicalStage 字段。 */
        private String historicalStage;
        /** similarityScore 字段。 */
        private BigDecimal similarityScore;

    }

    @Data
    public static class BacktestEntryVO {
        /** available 字段。 */
        private Boolean available;
        /** 任务ID。 */
        private Long taskId;
        /** reportId 字段。 */
        private Long reportId;
        /** targetUrl 字段。 */
        private String targetUrl;

    }

}
