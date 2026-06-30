package com.astock.module.review.api.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * DailyReviewWorkbenchVO 数据载体。
 */
@Data
public class DailyReviewWorkbenchVO {
    /** 交易日。 */
    private LocalDate tradeDate;
    /** 数据是否完整。 */
    private Boolean dataComplete;
    /** dataStatusText 字段。 */
    private String dataStatusText;
    /** overview 字段。 */
    private DailyReviewOverviewVO overview;
    /** reviewStatus 字段。 */
    private DailyReviewStatusVO reviewStatus;
    /** marketSection 字段。 */
    private ReviewMarketSectionVO marketSection;
    /** emotionSection 字段。 */
    private ReviewEmotionSectionVO emotionSection;
    /** similaritySection 字段。 */
    private ReviewSimilaritySectionVO similaritySection;
    /** mainlineSection 字段。 */
    private ReviewMainlineSectionVO mainlineSection;
    /** leaderSection 字段。 */
    private ReviewLeaderSectionVO leaderSection;
    /** patternSection 字段。 */
    private ReviewPatternSectionVO patternSection;
    /** riskSection 字段。 */
    private ReviewRiskSectionVO riskSection;
    /** backtestSection 字段。 */
    private ReviewBacktestSectionVO backtestSection;
    /** manualSection 字段。 */
    private ManualReviewSectionVO manualSection;
    /** nextDayPlan 字段。 */
    private NextDayObservationPlanVO nextDayPlan;
    /** checklists 字段。 */
    private List<DailyReviewChecklistVO> checklists;
    /** auditLogs 字段。 */
    private List<DailyReviewAuditLogVO> auditLogs;
    /** conclusion 字段。 */
    private String conclusion;
    /** riskTips 字段。 */
    private List<String> riskTips;


    @Data
    public static class DailyReviewOverviewVO {
        /** reviewStatus 字段。 */
        private String reviewStatus;
        /** systemConclusionAccepted 字段。 */
        private Boolean systemConclusionAccepted;
        /** hasManualAdjustment 字段。 */
        private Boolean hasManualAdjustment;
        /** cycleSampleConfirmed 字段。 */
        private Boolean cycleSampleConfirmed;
        /** summaryGenerated 字段。 */
        private Boolean summaryGenerated;
        /** overviewText 字段。 */
        private String overviewText;

    }

    @Data
    public static class DailyReviewStatusVO {
        /** status 字段。 */
        private String status;
        /** draftVersion 字段。 */
        private Integer draftVersion;
        /** createdBy 字段。 */
        private String createdBy;
        /** submittedAt 字段。 */
        private LocalDateTime submittedAt;
        /** lockedAt 字段。 */
        private LocalDateTime lockedAt;

    }

    @Data
    public static class ReviewMarketSectionVO {
        /** 摘要文本。 */
        private String summaryText;
        /** keyEvidences 字段。 */
        private List<String> keyEvidences;
        /** keyRisks 字段。 */
        private List<String> keyRisks;

    }

    @Data
    public static class ReviewEmotionSectionVO {
        /** emotionStage 字段。 */
        private String emotionStage;
        /** confidence 字段。 */
        private BigDecimal confidence;
        /** manualStage 字段。 */
        private String manualStage;
        /** sectionText 字段。 */
        private String sectionText;

    }

    @Data
    public static class ReviewSimilaritySectionVO {
        /** topMatchId 字段。 */
        private Long topMatchId;
        /** similarityScore 字段。 */
        private BigDecimal similarityScore;
        /** sectionText 字段。 */
        private String sectionText;

    }

    @Data
    public static class ReviewMainlineSectionVO {
        /** strongestMainline 字段。 */
        private String strongestMainline;
        /** lifecycleStage 字段。 */
        private String lifecycleStage;
        /** sectionText 字段。 */
        private String sectionText;

    }

    @Data
    public static class ReviewLeaderSectionVO {
        /** marketLeaderName 字段。 */
        private String marketLeaderName;
        /** leaderCount 字段。 */
        private Integer leaderCount;
        /** sectionText 字段。 */
        private String sectionText;

    }

    @Data
    public static class ReviewPatternSectionVO {
        /** signalCount 字段。 */
        private Integer signalCount;
        /** riskVetoCount 字段。 */
        private Integer riskVetoCount;
        /** sectionText 字段。 */
        private String sectionText;

    }

    @Data
    public static class ReviewRiskSectionVO {
        /** riskLevel 字段。 */
        private String riskLevel;
        /** riskScore 字段。 */
        private BigDecimal riskScore;
        /** sectionText 字段。 */
        private String sectionText;

    }

    @Data
    public static class ReviewBacktestSectionVO {
        /** latestReportId 字段。 */
        private Long latestReportId;
        /** backtestSummary 字段。 */
        private String backtestSummary;
        /** sectionText 字段。 */
        private String sectionText;

    }

    @Data
    public static class ManualReviewSectionVO {
        /** manualConclusion 字段。 */
        private String manualConclusion;
        /** disagreementReason 字段。 */
        private String disagreementReason;
        /** notes 字段。 */
        private List<String> notes;

    }

    @Data
    public static class NextDayObservationPlanVO {
        /** observeMainlines 字段。 */
        private List<String> observeMainlines;
        /** observeLeaders 字段。 */
        private List<String> observeLeaders;
        /** riskFocus 字段。 */
        private List<String> riskFocus;
        /** planText 字段。 */
        private String planText;

    }

    @Data
    public static class DailyReviewChecklistVO {
        /** checklistCode 字段。 */
        private String checklistCode;
        /** checklistName 字段。 */
        private String checklistName;
        /** 是否必填。 */
        private Boolean required;
        /** completed 字段。 */
        private Boolean completed;
        /** passed 字段。 */
        private Boolean passed;
        /** failedReason 字段。 */
        private String failedReason;

    }

    @Data
    public static class DailyReviewAuditLogVO {
        /** operationType 字段。 */
        private String operationType;
        /** operator 字段。 */
        private String operator;
        /** operatedAt 字段。 */
        private LocalDateTime operatedAt;
        /** operationRemark 字段。 */
        private String operationRemark;

    }

}
