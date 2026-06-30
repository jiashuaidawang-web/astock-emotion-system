package com.astock.module.leader.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * LeaderLadderPageVO 数据载体。
 */
@Data
public class LeaderLadderPageVO {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** overview 字段。 */
    private LeaderLadderOverviewVO overview;
    /** marketLeaders 字段。 */
    private List<LeaderCardVO> marketLeaders;
    /** mainlineLeaders 字段。 */
    private List<LeaderCardVO> mainlineLeaders;
    /** sectorLeaders 字段。 */
    private List<LeaderCardVO> sectorLeaders;
    /** highBoardLeaders 字段。 */
    private List<LeaderCardVO> highBoardLeaders;
    /** trendLeaders 字段。 */
    private List<LeaderCardVO> trendLeaders;
    /** compensationLeaders 字段。 */
    private List<LeaderCardVO> compensationLeaders;
    /** switchLeaders 字段。 */
    private List<LeaderCardVO> switchLeaders;
    /** followers 字段。 */
    private List<LeaderCardVO> followers;
    /** consecutiveBoardLadders 字段。 */
    private List<ConsecutiveBoardLadderVO> consecutiveBoardLadders;
    /** mainlineLadders 字段。 */
    private List<MainlineLeaderLadderVO> mainlineLadders;
    /** trendLadders 字段。 */
    private List<TrendLeaderLadderVO> trendLadders;
    /** driveRanks 字段。 */
    private List<LeaderDriveRankVO> driveRanks;
    /** negativeFeedbacks 字段。 */
    private List<LeaderNegativeFeedbackVO> negativeFeedbacks;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;


    @Data
    public static class LeaderLadderOverviewVO {
        /** hasMarketLeader 字段。 */
        private Boolean hasMarketLeader;
        /** marketLeaderStockCode 字段。 */
        private String marketLeaderStockCode;
        /** marketLeaderStockName 字段。 */
        private String marketLeaderStockName;
        /** marketLeaderScore 字段。 */
        private BigDecimal marketLeaderScore;
        /** mainlineLeaderCount 字段。 */
        private Integer mainlineLeaderCount;
        /** highBoardLeaderCount 字段。 */
        private Integer highBoardLeaderCount;
        /** trendLeaderCount 字段。 */
        private Integer trendLeaderCount;
        /** followerCount 字段。 */
        private Integer followerCount;
        /** maxBoardHeight 字段。 */
        private Integer maxBoardHeight;
        /** overallDriveScore 字段。 */
        private BigDecimal overallDriveScore;
        /** overallNegativeFeedbackScore 字段。 */
        private BigDecimal overallNegativeFeedbackScore;
        /** overviewText 字段。 */
        private String overviewText;

    }

    @Data
    public static class LeaderCardVO {
        /** stockCode 字段。 */
        private String stockCode;
        /** stockName 字段。 */
        private String stockName;
        /** leaderType 字段。 */
        private String leaderType;
        /** leaderStatus 字段。 */
        private String leaderStatus;
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** mainlineName 字段。 */
        private String mainlineName;
        /** sectorName 字段。 */
        private String sectorName;
        /** leaderScore 字段。 */
        private BigDecimal leaderScore;
        /** rankNo 字段。 */
        private Integer rankNo;
        /** driveScore 字段。 */
        private BigDecimal driveScore;
        /** negativeFeedbackScore 字段。 */
        private BigDecimal negativeFeedbackScore;
        /** consecutiveBoardHeight 字段。 */
        private Integer consecutiveBoardHeight;
        /** limitUp 字段。 */
        private Boolean limitUp;
        /** brokenBoard 字段。 */
        private Boolean brokenBoard;
        /** inPatternWatchPool 字段。 */
        private Boolean inPatternWatchPool;
        /** riskVeto 字段。 */
        private Boolean riskVeto;
        /** leaderSummary 字段。 */
        private String leaderSummary;
        /** riskSummary 字段。 */
        private String riskSummary;

    }

    @Data
    public static class ConsecutiveBoardLadderVO {
        /** boardHeight 字段。 */
        private Integer boardHeight;
        /** stockCount 字段。 */
        private Integer stockCount;
        /** stocks 字段。 */
        private List<LeaderCardVO> stocks;
        /** ladderText 字段。 */
        private String ladderText;

    }

    @Data
    public static class MainlineLeaderLadderVO {
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** mainlineName 字段。 */
        private String mainlineName;
        /** leaders 字段。 */
        private List<LeaderCardVO> leaders;

    }

    @Data
    public static class TrendLeaderLadderVO {
        /** trendType 字段。 */
        private String trendType;
        /** leaders 字段。 */
        private List<LeaderCardVO> leaders;

    }

    @Data
    public static class LeaderDriveRankVO {
        /** stockCode 字段。 */
        private String stockCode;
        /** stockName 字段。 */
        private String stockName;
        /** driveScore 字段。 */
        private BigDecimal driveScore;
        /** driveText 字段。 */
        private String driveText;

    }

    @Data
    public static class LeaderNegativeFeedbackVO {
        /** stockCode 字段。 */
        private String stockCode;
        /** stockName 字段。 */
        private String stockName;
        /** negativeFeedbackScore 字段。 */
        private BigDecimal negativeFeedbackScore;
        /** triggerRiskControl 字段。 */
        private Boolean triggerRiskControl;
        /** feedbackText 字段。 */
        private String feedbackText;

    }

}
