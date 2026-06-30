package com.astock.module.sample.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.astock.module.sample.application.query.HistoricalCycleSamplePageQuery;
import lombok.Data;

/**
 * HistoricalCycleSamplePageVO 数据载体。
 */
@Data
public class HistoricalCycleSamplePageVO {
    /** query 字段。 */
    private HistoricalCycleSamplePageQuery query;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** overview 字段。 */
    private CycleSampleOverviewVO overview;
    /** stageDistributions 字段。 */
    private List<CycleSampleStageDistributionVO> stageDistributions;
    /** typeDistributions 字段。 */
    private List<CycleSampleTypeDistributionVO> typeDistributions;
    /** confirmDistributions 字段。 */
    private List<CycleSampleConfirmDistributionVO> confirmDistributions;
    /** samplePage 字段。 */
    private List<CycleSampleListItemVO> samplePage;
    /** highQualitySamples 字段。 */
    private List<CycleSampleListItemVO> highQualitySamples;
    /** pendingConfirmSamples 字段。 */
    private List<CycleSampleListItemVO> pendingConfirmSamples;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;
    /** 交易日。 */
    private LocalDate tradeDate;


    @Data
    public static class CycleSampleOverviewVO {
        /** totalSampleCount 字段。 */
        private Integer totalSampleCount;
        /** confirmedSampleCount 字段。 */
        private Integer confirmedSampleCount;
        /** pendingConfirmCount 字段。 */
        private Integer pendingConfirmCount;
        /** highQualitySampleCount 字段。 */
        private Integer highQualitySampleCount;
        /** overviewText 字段。 */
        private String overviewText;

    }

    @Data
    public static class CycleSampleStageDistributionVO {
        /** stageCode 字段。 */
        private String stageCode;
        /** stageName 字段。 */
        private String stageName;
        /** sampleCount 字段。 */
        private Integer sampleCount;
        /** ratio 字段。 */
        private BigDecimal ratio;

    }

    @Data
    public static class CycleSampleTypeDistributionVO {
        /** sampleType 字段。 */
        private String sampleType;
        /** sampleCount 字段。 */
        private Integer sampleCount;
        /** ratio 字段。 */
        private BigDecimal ratio;

    }

    @Data
    public static class CycleSampleConfirmDistributionVO {
        /** confirmStatus 字段。 */
        private String confirmStatus;
        /** sampleCount 字段。 */
        private Integer sampleCount;
        /** ratio 字段。 */
        private BigDecimal ratio;

    }

    @Data
    public static class CycleSampleListItemVO {
        /** sampleId 字段。 */
        private Long sampleId;
        /** 交易日。 */
        private LocalDate tradeDate;
        /** sampleType 字段。 */
        private String sampleType;
        /** stageType 字段。 */
        private String stageType;
        /** sampleStatus 字段。 */
        private String sampleStatus;
        /** sampleConfidence 字段。 */
        private BigDecimal sampleConfidence;
        /** strongestMainline 字段。 */
        private String strongestMainline;
        /** leaderStockName 字段。 */
        private String leaderStockName;
        /** future3dReturn 字段。 */
        private BigDecimal future3dReturn;
        /** maxDrawdown 字段。 */
        private BigDecimal maxDrawdown;
        /** manuallyConfirmed 字段。 */
        private Boolean manuallyConfirmed;
        /** sampleText 字段。 */
        private String sampleText;

    }

}
