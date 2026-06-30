package com.astock.module.mainline.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * MainlineRadarPageVO 数据载体。
 */
@Data
public class MainlineRadarPageVO {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** overview 字段。 */
    private MainlineOverviewVO overview;
    /** mainlineRanks 字段。 */
    private List<MainlineRankVO> mainlineRanks;
    /** lifecycleList 字段。 */
    private List<MainlineLifecycleVO> lifecycleList;
    /** competition 字段。 */
    private MainlineCompetitionVO competition;
    /** switchSignals 字段。 */
    private List<MainlineSwitchVO> switchSignals;
    /** scoreBreakdowns 字段。 */
    private List<MainlineScoreBreakdownVO> scoreBreakdowns;
    /** ladders 字段。 */
    private List<MainlineLadderVO> ladders;
    /** leaders 字段。 */
    private List<MainlineLeaderVO> leaders;
    /** risks 字段。 */
    private List<MainlineRiskVO> risks;
    /** historicalSamples 字段。 */
    private List<HistoricalMainlineSampleVO> historicalSamples;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;


    @Data
    public static class MainlineOverviewVO {
        /** mainlineCount 字段。 */
        private Integer mainlineCount;
        /** confirmedCount 字段。 */
        private Integer confirmedCount;
        /** candidateCount 字段。 */
        private Integer candidateCount;
        /** strongestMainlineName 字段。 */
        private String strongestMainlineName;
        /** strongestScore 字段。 */
        private BigDecimal strongestScore;
        /** overviewText 字段。 */
        private String overviewText;

    }

    @Data
    public static class MainlineRankVO {
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** mainlineName 字段。 */
        private String mainlineName;
        /** mainlineStatus 字段。 */
        private String mainlineStatus;
        /** lifecycleStage 字段。 */
        private String lifecycleStage;
        /** themeRole 字段。 */
        private String themeRole;
        /** mainlineStrengthScore 字段。 */
        private BigDecimal mainlineStrengthScore;
        /** rankNo 字段。 */
        private Integer rankNo;
        /** leaderStockName 字段。 */
        private String leaderStockName;

    }

    @Data
    public static class MainlineLifecycleVO {
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** mainlineName 字段。 */
        private String mainlineName;
        /** lifecycleStage 字段。 */
        private String lifecycleStage;
        /** lifecycleText 字段。 */
        private String lifecycleText;

    }

    @Data
    public static class MainlineCompetitionVO {
        /** competitionStatus 字段。 */
        private String competitionStatus;
        /** items 字段。 */
        private List<MainlineCompetitionItemVO> items;
        /** competitionText 字段。 */
        private String competitionText;

    }

    @Data
    public static class MainlineCompetitionItemVO {
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** mainlineName 字段。 */
        private String mainlineName;
        /** strengthScore 字段。 */
        private BigDecimal strengthScore;
        /** role 字段。 */
        private String role;

    }

    @Data
    public static class MainlineSwitchVO {
        /** oldMainlineId 字段。 */
        private Long oldMainlineId;
        /** oldMainlineName 字段。 */
        private String oldMainlineName;
        /** newMainlineId 字段。 */
        private Long newMainlineId;
        /** newMainlineName 字段。 */
        private String newMainlineName;
        /** switchStatus 字段。 */
        private String switchStatus;
        /** switchScore 字段。 */
        private BigDecimal switchScore;
        /** riskText 字段。 */
        private String riskText;

    }

    @Data
    public static class MainlineScoreBreakdownVO {
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** limitUpClusterScore 字段。 */
        private BigDecimal limitUpClusterScore;
        /** turnoverConcentrationScore 字段。 */
        private BigDecimal turnoverConcentrationScore;
        /** continuityScore 字段。 */
        private BigDecimal continuityScore;
        /** ladderIntegrityScore 字段。 */
        private BigDecimal ladderIntegrityScore;
        /** leaderDriveScore 字段。 */
        private BigDecimal leaderDriveScore;
        /** emotionMatchScore 字段。 */
        private BigDecimal emotionMatchScore;

    }

    @Data
    public static class MainlineLadderVO {
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** mainlineName 字段。 */
        private String mainlineName;
        /** maxBoardHeight 字段。 */
        private Integer maxBoardHeight;
        /** limitUpCount 字段。 */
        private Integer limitUpCount;
        /** ladderText 字段。 */
        private String ladderText;

    }

    @Data
    public static class MainlineLeaderVO {
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** stockCode 字段。 */
        private String stockCode;
        /** stockName 字段。 */
        private String stockName;
        /** leaderType 字段。 */
        private String leaderType;
        /** leaderScore 字段。 */
        private BigDecimal leaderScore;

    }

    @Data
    public static class MainlineRiskVO {
        /** mainlineId 字段。 */
        private Long mainlineId;
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
    public static class HistoricalMainlineSampleVO {
        /** sampleId 字段。 */
        private Long sampleId;
        /** 交易日。 */
        private LocalDate tradeDate;
        /** mainlineName 字段。 */
        private String mainlineName;
        /** similarityScore 字段。 */
        private BigDecimal similarityScore;
        /** sampleText 字段。 */
        private String sampleText;

    }

}
