package com.astock.module.review.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailyReviewFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_13_DAILY_REVIEW";
    }

    @Override
    public String voClassName() {
        return "DailyReviewWorkbenchVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("DailyReviewWorkbenchVO.tradeDate", "CLICKHOUSE", "market_factor_snapshot", "trade_date", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("DailyReviewWorkbenchVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("DailyReviewWorkbenchVO.overview", "CLICKHOUSE", "market_factor_snapshot", "overview", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.reviewStatus", "CLICKHOUSE", "market_factor_snapshot", "review_status", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.marketSection", "CLICKHOUSE", "market_factor_snapshot", "market_section", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.emotionSection", "CLICKHOUSE", "market_factor_snapshot", "emotion_section", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.similaritySection", "CLICKHOUSE", "market_factor_snapshot", "similarity_section", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.mainlineSection", "CLICKHOUSE", "market_factor_snapshot", "mainline_section", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.leaderSection", "CLICKHOUSE", "market_factor_snapshot", "leader_section", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.patternSection", "CLICKHOUSE", "market_factor_snapshot", "pattern_section", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.riskSection", "CLICKHOUSE", "market_factor_snapshot", "risk_section", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.backtestSection", "CLICKHOUSE", "market_factor_snapshot", "backtest_section", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.manualSection", "CLICKHOUSE", "market_factor_snapshot", "manual_section", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.nextDayPlan", "CLICKHOUSE", "market_factor_snapshot", "next_day_plan", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.checklists", "CLICKHOUSE", "market_factor_snapshot", "checklists", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.auditLogs", "CLICKHOUSE", "market_factor_snapshot", "audit_logs", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.conclusion", "CLICKHOUSE", "market_factor_snapshot", "evidence_json", "", false),
                new FieldMapping("DailyReviewWorkbenchVO.riskTips", "CLICKHOUSE", "market_factor_snapshot", "risk_json", "", false),
                new FieldMapping("DailyReviewOverviewVO.reviewStatus", "CLICKHOUSE", "market_factor_snapshot", "review_status", "", false),
                new FieldMapping("DailyReviewOverviewVO.systemConclusionAccepted", "CLICKHOUSE", "market_factor_snapshot", "system_conclusion_accepted", "", false),
                new FieldMapping("DailyReviewOverviewVO.hasManualAdjustment", "CLICKHOUSE", "market_factor_snapshot", "has_manual_adjustment", "", false),
                new FieldMapping("DailyReviewOverviewVO.cycleSampleConfirmed", "CLICKHOUSE", "market_factor_snapshot", "cycle_sample_confirmed", "", false),
                new FieldMapping("DailyReviewOverviewVO.summaryGenerated", "CLICKHOUSE", "market_factor_snapshot", "summary_generated", "", false),
                new FieldMapping("DailyReviewOverviewVO.overviewText", "CLICKHOUSE", "market_factor_snapshot", "features", "", false),
                new FieldMapping("DailyReviewStatusVO.status", "CLICKHOUSE", "market_factor_snapshot", "status", "", false),
                new FieldMapping("DailyReviewStatusVO.draftVersion", "CLICKHOUSE", "market_factor_snapshot", "draft_version", "", false),
                new FieldMapping("DailyReviewStatusVO.createdBy", "CLICKHOUSE", "market_factor_snapshot", "created_by", "", false),
                new FieldMapping("DailyReviewStatusVO.submittedAt", "CLICKHOUSE", "market_factor_snapshot", "submitted_at", "", false),
                new FieldMapping("DailyReviewStatusVO.lockedAt", "CLICKHOUSE", "market_factor_snapshot", "locked_at", "", false),
                new FieldMapping("ReviewMarketSectionVO.summaryText", "CLICKHOUSE", "market_factor_snapshot", "evidence_json", "", false),
                new FieldMapping("ReviewMarketSectionVO.keyEvidences", "CLICKHOUSE", "market_factor_snapshot", "key_evidences", "", false),
                new FieldMapping("ReviewMarketSectionVO.keyRisks", "CLICKHOUSE", "market_factor_snapshot", "key_risks", "", false),
                new FieldMapping("ReviewEmotionSectionVO.emotionStage", "CLICKHOUSE", "market_factor_snapshot", "primary_stage", "", false),
                new FieldMapping("ReviewEmotionSectionVO.confidence", "CLICKHOUSE", "market_factor_snapshot", "stage_confidence", "", false),
                new FieldMapping("ReviewEmotionSectionVO.manualStage", "CLICKHOUSE", "market_factor_snapshot", "manual_stage", "", false),
                new FieldMapping("ReviewEmotionSectionVO.sectionText", "CLICKHOUSE", "market_factor_snapshot", "section_content", "", false),
                new FieldMapping("ReviewSimilaritySectionVO.topMatchId", "CLICKHOUSE", "market_factor_snapshot", "top_match_id", "", false),
                new FieldMapping("ReviewSimilaritySectionVO.similarityScore", "CLICKHOUSE", "market_factor_snapshot", "similarity_score", "", false),
                new FieldMapping("ReviewSimilaritySectionVO.sectionText", "CLICKHOUSE", "market_factor_snapshot", "section_content", "", false),
                new FieldMapping("ReviewMainlineSectionVO.strongestMainline", "CLICKHOUSE", "market_factor_snapshot", "strongest_mainline", "", false),
                new FieldMapping("ReviewMainlineSectionVO.lifecycleStage", "CLICKHOUSE", "market_factor_snapshot", "lifecycle_stage", "", false),
                new FieldMapping("ReviewMainlineSectionVO.sectionText", "CLICKHOUSE", "market_factor_snapshot", "section_content", "", false),
                new FieldMapping("ReviewLeaderSectionVO.marketLeaderName", "CLICKHOUSE", "market_factor_snapshot", "market_leader_name", "", false),
                new FieldMapping("ReviewLeaderSectionVO.leaderCount", "CLICKHOUSE", "market_factor_snapshot", "leader_count", "", false),
                new FieldMapping("ReviewLeaderSectionVO.sectionText", "CLICKHOUSE", "market_factor_snapshot", "section_content", "", false),
                new FieldMapping("ReviewPatternSectionVO.signalCount", "CLICKHOUSE", "market_factor_snapshot", "signal_count", "", false),
                new FieldMapping("ReviewPatternSectionVO.riskVetoCount", "CLICKHOUSE", "market_factor_snapshot", "risk_veto_count", "", false),
                new FieldMapping("ReviewPatternSectionVO.sectionText", "CLICKHOUSE", "market_factor_snapshot", "section_content", "", false),
                new FieldMapping("ReviewRiskSectionVO.riskLevel", "CLICKHOUSE", "market_factor_snapshot", "risk_level", "", false),
                new FieldMapping("ReviewRiskSectionVO.riskScore", "CLICKHOUSE", "market_factor_snapshot", "risk_score", "", false),
                new FieldMapping("ReviewRiskSectionVO.sectionText", "CLICKHOUSE", "market_factor_snapshot", "section_content", "", false),
                new FieldMapping("ReviewBacktestSectionVO.latestReportId", "CLICKHOUSE", "market_factor_snapshot", "latest_report_id", "", false),
                new FieldMapping("ReviewBacktestSectionVO.backtestSummary", "CLICKHOUSE", "market_factor_snapshot", "backtest_summary", "", false),
                new FieldMapping("ReviewBacktestSectionVO.sectionText", "CLICKHOUSE", "market_factor_snapshot", "section_content", "", false),
                new FieldMapping("ManualReviewSectionVO.manualConclusion", "CLICKHOUSE", "market_factor_snapshot", "manual_conclusion", "", false),
                new FieldMapping("ManualReviewSectionVO.disagreementReason", "CLICKHOUSE", "market_factor_snapshot", "disagreement_reason", "", false),
                new FieldMapping("ManualReviewSectionVO.notes", "CLICKHOUSE", "market_factor_snapshot", "notes", "", false),
                new FieldMapping("NextDayObservationPlanVO.observeMainlines", "CLICKHOUSE", "market_factor_snapshot", "observe_mainlines", "", false),
                new FieldMapping("NextDayObservationPlanVO.observeLeaders", "CLICKHOUSE", "market_factor_snapshot", "observe_leaders", "", false),
                new FieldMapping("NextDayObservationPlanVO.riskFocus", "CLICKHOUSE", "market_factor_snapshot", "risk_focus", "", false),
                new FieldMapping("NextDayObservationPlanVO.planText", "CLICKHOUSE", "market_factor_snapshot", "plan_text", "", false),
                new FieldMapping("DailyReviewChecklistVO.checklistCode", "CLICKHOUSE", "market_factor_snapshot", "checklist_code", "", false),
                new FieldMapping("DailyReviewChecklistVO.checklistName", "CLICKHOUSE", "market_factor_snapshot", "checklist_name", "", false),
                new FieldMapping("DailyReviewChecklistVO.required", "CLICKHOUSE", "market_factor_snapshot", "required", "", false),
                new FieldMapping("DailyReviewChecklistVO.completed", "CLICKHOUSE", "market_factor_snapshot", "completed", "", false),
                new FieldMapping("DailyReviewChecklistVO.passed", "CLICKHOUSE", "market_factor_snapshot", "passed", "", false),
                new FieldMapping("DailyReviewChecklistVO.failedReason", "CLICKHOUSE", "market_factor_snapshot", "failed_reason", "", false),
                new FieldMapping("DailyReviewAuditLogVO.operationType", "CLICKHOUSE", "market_factor_snapshot", "operation_type", "", false),
                new FieldMapping("DailyReviewAuditLogVO.operator", "CLICKHOUSE", "market_factor_snapshot", "operator", "", false),
                new FieldMapping("DailyReviewAuditLogVO.operatedAt", "CLICKHOUSE", "market_factor_snapshot", "operated_at", "", false),
                new FieldMapping("DailyReviewAuditLogVO.operationRemark", "CLICKHOUSE", "market_factor_snapshot", "operation_remark", "", false)
        );
    }
}
