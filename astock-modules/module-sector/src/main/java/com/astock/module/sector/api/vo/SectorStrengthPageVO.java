package com.astock.module.sector.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * SectorStrengthPageVO 数据载体。
 */
@Data
public class SectorStrengthPageVO {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** overview 字段。 */
    private SectorStrengthOverviewVO overview;
    /** industryRanks 字段。 */
    private List<SectorStrengthRankVO> industryRanks;
    /** conceptRanks 字段。 */
    private List<SectorStrengthRankVO> conceptRanks;
    /** manualThemeRanks 字段。 */
    private List<SectorStrengthRankVO> manualThemeRanks;
    /** limitUpDensityRanks 字段。 */
    private List<SectorLimitUpDensityRankVO> limitUpDensityRanks;
    /** turnoverRanks 字段。 */
    private List<SectorTurnoverRankVO> turnoverRanks;
    /** continuityRanks 字段。 */
    private List<SectorContinuityRankVO> continuityRanks;
    /** sectorLadders 字段。 */
    private List<SectorLadderVO> sectorLadders;
    /** divergenceRisks 字段。 */
    private List<SectorDivergenceRiskVO> divergenceRisks;
    /** mainlineRelations 字段。 */
    private List<SectorMainlineRelationVO> mainlineRelations;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;


    @Data
    public static class SectorStrengthOverviewVO {
        /** sectorCount 字段。 */
        private Integer sectorCount;
        /** strongestSectorName 字段。 */
        private String strongestSectorName;
        /** strongestScore 字段。 */
        private BigDecimal strongestScore;
        /** mainlineRelatedCount 字段。 */
        private Integer mainlineRelatedCount;
        /** overviewText 字段。 */
        private String overviewText;

    }

    @Data
    public static class SectorStrengthRankVO {
        /** sectorCode 字段。 */
        private String sectorCode;
        /** sectorName 字段。 */
        private String sectorName;
        /** sectorType 字段。 */
        private String sectorType;
        /** rankNo 字段。 */
        private Integer rankNo;
        /** sectorStrengthScore 字段。 */
        private BigDecimal sectorStrengthScore;
        /** pctChange 字段。 */
        private BigDecimal pctChange;
        /** limitUpCount 字段。 */
        private Integer limitUpCount;
        /** turnoverAmount 字段。 */
        private BigDecimal turnoverAmount;
        /** mainlineRelated 字段。 */
        private Boolean mainlineRelated;
        /** rankText 字段。 */
        private String rankText;

    }

    @Data
    public static class SectorLimitUpDensityRankVO {
        /** sectorCode 字段。 */
        private String sectorCode;
        /** sectorName 字段。 */
        private String sectorName;
        /** limitUpDensityScore 字段。 */
        private BigDecimal limitUpDensityScore;
        /** limitUpCount 字段。 */
        private Integer limitUpCount;
        /** stockCount 字段。 */
        private Integer stockCount;

    }

    @Data
    public static class SectorTurnoverRankVO {
        /** sectorCode 字段。 */
        private String sectorCode;
        /** sectorName 字段。 */
        private String sectorName;
        /** turnoverAmount 字段。 */
        private BigDecimal turnoverAmount;
        /** turnoverScore 字段。 */
        private BigDecimal turnoverScore;

    }

    @Data
    public static class SectorContinuityRankVO {
        /** sectorCode 字段。 */
        private String sectorCode;
        /** sectorName 字段。 */
        private String sectorName;
        /** continuityScore 字段。 */
        private BigDecimal continuityScore;
        /** continuityText 字段。 */
        private String continuityText;

    }

    @Data
    public static class SectorLadderVO {
        /** sectorCode 字段。 */
        private String sectorCode;
        /** sectorName 字段。 */
        private String sectorName;
        /** maxBoardHeight 字段。 */
        private Integer maxBoardHeight;
        /** limitUpCount 字段。 */
        private Integer limitUpCount;
        /** ladderText 字段。 */
        private String ladderText;

    }

    @Data
    public static class SectorDivergenceRiskVO {
        /** sectorCode 字段。 */
        private String sectorCode;
        /** sectorName 字段。 */
        private String sectorName;
        /** divergenceRiskScore 字段。 */
        private BigDecimal divergenceRiskScore;
        /** riskLevel 字段。 */
        private String riskLevel;
        /** riskText 字段。 */
        private String riskText;

    }

    @Data
    public static class SectorMainlineRelationVO {
        /** sectorCode 字段。 */
        private String sectorCode;
        /** sectorName 字段。 */
        private String sectorName;
        /** mainlineId 字段。 */
        private Long mainlineId;
        /** mainlineName 字段。 */
        private String mainlineName;
        /** relationText 字段。 */
        private String relationText;

    }

}
