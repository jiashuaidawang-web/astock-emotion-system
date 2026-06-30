package com.astock.module.review.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.review.api.vo.DailyReviewWorkbenchVO;
import com.astock.module.review.application.query.DailyReviewWorkbenchQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DailyReviewConverter implements PageBundleConverter<DailyReviewWorkbenchQuery, DailyReviewWorkbenchVO> {

    @Override
    public DailyReviewWorkbenchVO convert(DailyReviewWorkbenchQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        DailyReviewWorkbenchVO vo = new DailyReviewWorkbenchVO();

        if (bundle == null || bundle.isEmpty()) {
            vo.setDataComplete(false);
            vo.setDataStatusText("多表Repository未返回真实记录，拒绝Mock补齐。");
            vo.setTradeDate(query.getTradeDate());
            vo.setConclusion("无真实多表快照数据。");
            vo.setRiskTips(List.of("请检查页面专属多表Repository、引擎落库结果和规则版本。"));
            return vo;
        }

        Map<String, Object> primary = bundle.firstRow("market_factor_snapshot");
                vo.setTradeDate(MapFieldReader.localDate(primary, "trade_date") == null ? query.getTradeDate() : MapFieldReader.localDate(primary, "trade_date"));
        vo.setDataComplete(quality.getDataComplete());
        vo.setDataStatusText(quality.getDataStatusText());
        vo.setOverview(toDailyReviewOverviewVO(bundle.firstRow("market_factor_snapshot"), bundle));
        vo.setReviewStatus(toDailyReviewStatusVO(bundle.firstRow("market_factor_snapshot"), bundle));
        vo.setMarketSection(toReviewMarketSectionVO(bundle.firstRow("market_factor_snapshot"), bundle));
        vo.setEmotionSection(toReviewEmotionSectionVO(bundle.firstRow("emotion_stage_snapshot"), bundle));
        vo.setSimilaritySection(toReviewSimilaritySectionVO(bundle.firstRow("historical_similarity_match"), bundle));
        vo.setMainlineSection(toReviewMainlineSectionVO(bundle.firstRow("mainline_daily_snapshot"), bundle));
        vo.setLeaderSection(toReviewLeaderSectionVO(bundle.firstRow("leader_daily_snapshot"), bundle));
        vo.setPatternSection(toReviewPatternSectionVO(bundle.firstRow("buy_pattern_signal_snapshot"), bundle));
        vo.setRiskSection(toReviewRiskSectionVO(bundle.firstRow("risk_signal_snapshot"), bundle));
        vo.setBacktestSection(toReviewBacktestSectionVO(bundle.firstRow("backtest_layer_stat"), bundle));
        vo.setManualSection(toManualReviewSectionVO(bundle.firstRow("market_factor_snapshot"), bundle));
        vo.setNextDayPlan(toNextDayObservationPlanVO(bundle.firstRow("market_factor_snapshot"), bundle));
        vo.setChecklists(bundle.rows("market_factor_snapshot").stream().map(r -> toDailyReviewChecklistVO(r, bundle)).toList());
        vo.setAuditLogs(bundle.rows("market_factor_snapshot").stream().map(r -> toDailyReviewAuditLogVO(r, bundle)).toList());
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("market_factor_snapshot"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("risk_signal_snapshot"), "risk_json"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "DailyReviewOverviewVO" -> "market_factor_snapshot";
            case "DailyReviewStatusVO" -> "market_factor_snapshot";
            case "ReviewMarketSectionVO" -> "market_factor_snapshot";
            case "ReviewEmotionSectionVO" -> "emotion_stage_snapshot";
            case "ReviewSimilaritySectionVO" -> "historical_similarity_match";
            case "ReviewMainlineSectionVO" -> "mainline_daily_snapshot";
            case "ReviewLeaderSectionVO" -> "leader_daily_snapshot";
            case "ReviewPatternSectionVO" -> "buy_pattern_signal_snapshot";
            case "ReviewRiskSectionVO" -> "risk_signal_snapshot";
            case "ReviewBacktestSectionVO" -> "backtest_layer_stat";
            case "ManualReviewSectionVO" -> "market_factor_snapshot";
            case "NextDayObservationPlanVO" -> "market_factor_snapshot";
            case "DailyReviewChecklistVO" -> "market_factor_snapshot";
            case "DailyReviewAuditLogVO" -> "market_factor_snapshot";
            case "DailyReviewWorkbenchVO" -> "market_factor_snapshot";
            default -> "market_factor_snapshot";
        };
    }

    private List<String> stringList(Map<String, Object> row, String column) {
        String value = MapFieldReader.string(row, column);
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value);
    }


    private DailyReviewWorkbenchVO.DailyReviewOverviewVO toDailyReviewOverviewVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.DailyReviewOverviewVO item = new DailyReviewWorkbenchVO.DailyReviewOverviewVO();
        item.setReviewStatus(MapFieldReader.string(row, "review_status"));
        item.setSystemConclusionAccepted(MapFieldReader.bool(row, "system_conclusion_accepted"));
        item.setHasManualAdjustment(MapFieldReader.bool(row, "has_manual_adjustment"));
        item.setCycleSampleConfirmed(MapFieldReader.bool(row, "cycle_sample_confirmed"));
        item.setSummaryGenerated(MapFieldReader.bool(row, "summary_generated"));
        item.setOverviewText(MapFieldReader.string(row, "features"));
        return item;
    }

    private DailyReviewWorkbenchVO.DailyReviewStatusVO toDailyReviewStatusVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.DailyReviewStatusVO item = new DailyReviewWorkbenchVO.DailyReviewStatusVO();
        item.setStatus(MapFieldReader.string(row, "status"));
        item.setDraftVersion(MapFieldReader.integer(row, "draft_version"));
        item.setCreatedBy(MapFieldReader.string(row, "created_by"));
        item.setSubmittedAt(MapFieldReader.localDateTime(row, "submitted_at"));
        item.setLockedAt(MapFieldReader.localDateTime(row, "locked_at"));
        return item;
    }

    private DailyReviewWorkbenchVO.ReviewMarketSectionVO toReviewMarketSectionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.ReviewMarketSectionVO item = new DailyReviewWorkbenchVO.ReviewMarketSectionVO();
        item.setSummaryText(MapFieldReader.string(row, "evidence_json"));
        item.setKeyEvidences(stringList(row, "key_evidences"));
        item.setKeyRisks(stringList(row, "key_risks"));
        return item;
    }

    private DailyReviewWorkbenchVO.ReviewEmotionSectionVO toReviewEmotionSectionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.ReviewEmotionSectionVO item = new DailyReviewWorkbenchVO.ReviewEmotionSectionVO();
        item.setEmotionStage(MapFieldReader.string(row, "primary_stage"));
        item.setConfidence(MapFieldReader.decimal(row, "stage_confidence"));
        item.setManualStage(MapFieldReader.string(row, "manual_stage"));
        item.setSectionText(MapFieldReader.string(row, "section_content"));
        return item;
    }

    private DailyReviewWorkbenchVO.ReviewSimilaritySectionVO toReviewSimilaritySectionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.ReviewSimilaritySectionVO item = new DailyReviewWorkbenchVO.ReviewSimilaritySectionVO();
        item.setTopMatchId(MapFieldReader.longValue(row, "top_match_id"));
        item.setSimilarityScore(MapFieldReader.decimal(row, "similarity_score"));
        item.setSectionText(MapFieldReader.string(row, "section_content"));
        return item;
    }

    private DailyReviewWorkbenchVO.ReviewMainlineSectionVO toReviewMainlineSectionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.ReviewMainlineSectionVO item = new DailyReviewWorkbenchVO.ReviewMainlineSectionVO();
        item.setStrongestMainline(MapFieldReader.string(row, "strongest_mainline"));
        item.setLifecycleStage(MapFieldReader.string(row, "lifecycle_stage"));
        item.setSectionText(MapFieldReader.string(row, "section_content"));
        return item;
    }

    private DailyReviewWorkbenchVO.ReviewLeaderSectionVO toReviewLeaderSectionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.ReviewLeaderSectionVO item = new DailyReviewWorkbenchVO.ReviewLeaderSectionVO();
        item.setMarketLeaderName(MapFieldReader.string(row, "market_leader_name"));
        item.setLeaderCount(MapFieldReader.integer(row, "leader_count"));
        item.setSectionText(MapFieldReader.string(row, "section_content"));
        return item;
    }

    private DailyReviewWorkbenchVO.ReviewPatternSectionVO toReviewPatternSectionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.ReviewPatternSectionVO item = new DailyReviewWorkbenchVO.ReviewPatternSectionVO();
        item.setSignalCount(MapFieldReader.integer(row, "signal_count"));
        item.setRiskVetoCount(MapFieldReader.integer(row, "risk_veto_count"));
        item.setSectionText(MapFieldReader.string(row, "section_content"));
        return item;
    }

    private DailyReviewWorkbenchVO.ReviewRiskSectionVO toReviewRiskSectionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.ReviewRiskSectionVO item = new DailyReviewWorkbenchVO.ReviewRiskSectionVO();
        item.setRiskLevel(MapFieldReader.string(row, "risk_level"));
        item.setRiskScore(MapFieldReader.decimal(row, "risk_score"));
        item.setSectionText(MapFieldReader.string(row, "section_content"));
        return item;
    }

    private DailyReviewWorkbenchVO.ReviewBacktestSectionVO toReviewBacktestSectionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.ReviewBacktestSectionVO item = new DailyReviewWorkbenchVO.ReviewBacktestSectionVO();
        item.setLatestReportId(MapFieldReader.longValue(row, "latest_report_id"));
        item.setBacktestSummary(MapFieldReader.string(row, "backtest_summary"));
        item.setSectionText(MapFieldReader.string(row, "section_content"));
        return item;
    }

    private DailyReviewWorkbenchVO.ManualReviewSectionVO toManualReviewSectionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.ManualReviewSectionVO item = new DailyReviewWorkbenchVO.ManualReviewSectionVO();
        item.setManualConclusion(MapFieldReader.string(row, "manual_conclusion"));
        item.setDisagreementReason(MapFieldReader.string(row, "disagreement_reason"));
        item.setNotes(stringList(row, "notes"));
        return item;
    }

    private DailyReviewWorkbenchVO.NextDayObservationPlanVO toNextDayObservationPlanVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.NextDayObservationPlanVO item = new DailyReviewWorkbenchVO.NextDayObservationPlanVO();
        item.setObserveMainlines(stringList(row, "observe_mainlines"));
        item.setObserveLeaders(stringList(row, "observe_leaders"));
        item.setRiskFocus(stringList(row, "risk_focus"));
        item.setPlanText(MapFieldReader.string(row, "plan_text"));
        return item;
    }

    private DailyReviewWorkbenchVO.DailyReviewChecklistVO toDailyReviewChecklistVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.DailyReviewChecklistVO item = new DailyReviewWorkbenchVO.DailyReviewChecklistVO();
        item.setChecklistCode(MapFieldReader.string(row, "checklist_code"));
        item.setChecklistName(MapFieldReader.string(row, "checklist_name"));
        item.setRequired(MapFieldReader.bool(row, "required"));
        item.setCompleted(MapFieldReader.bool(row, "completed"));
        item.setPassed(MapFieldReader.bool(row, "passed"));
        item.setFailedReason(MapFieldReader.string(row, "failed_reason"));
        return item;
    }

    private DailyReviewWorkbenchVO.DailyReviewAuditLogVO toDailyReviewAuditLogVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                DailyReviewWorkbenchVO.DailyReviewAuditLogVO item = new DailyReviewWorkbenchVO.DailyReviewAuditLogVO();
        item.setOperationType(MapFieldReader.string(row, "operation_type"));
        item.setOperator(MapFieldReader.string(row, "operator"));
        item.setOperatedAt(MapFieldReader.localDateTime(row, "operated_at"));
        item.setOperationRemark(MapFieldReader.string(row, "operation_remark"));
        return item;
    }

}
