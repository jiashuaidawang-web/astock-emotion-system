package com.astock.module.risk.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.risk.api.vo.RiskControlPageVO;
import com.astock.module.risk.application.query.RiskControlPageQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RiskControlConverter implements PageBundleConverter<RiskControlPageQuery, RiskControlPageVO> {

    @Override
    public RiskControlPageVO convert(RiskControlPageQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        RiskControlPageVO vo = new RiskControlPageVO();

        if (bundle == null || bundle.isEmpty()) {
            vo.setDataComplete(false);
            vo.setDataStatusText("多表Repository未返回真实记录，拒绝Mock补齐。");
            vo.setTradeDate(query.getTradeDate());
            vo.setConclusion("无真实多表快照数据。");
            vo.setRiskTips(List.of("请检查页面专属多表Repository、引擎落库结果和规则版本。"));
            return vo;
        }

        Map<String, Object> primary = bundle.firstRow("risk_signal_snapshot");
                vo.setTradeDate(MapFieldReader.localDate(primary, "trade_date") == null ? query.getTradeDate() : MapFieldReader.localDate(primary, "trade_date"));
        vo.setDataComplete(quality.getDataComplete());
        vo.setDataStatusText(quality.getDataStatusText());
        vo.setOverview(toRiskControlOverviewVO(bundle.firstRow("risk_signal_snapshot"), bundle));
        vo.setScoreBreakdown(toRiskScoreBreakdownVO(bundle.firstRow("risk_signal_snapshot"), bundle));
        vo.setRiskSourceGroups(bundle.rows("risk_signal_detail").stream().map(r -> toRiskSourceGroupVO(r, bundle)).toList());
        vo.setRiskSignals(bundle.rows("risk_signal_detail").stream().map(r -> toRiskSignalVO(r, bundle)).toList());
        vo.setPatternVetos(bundle.rows("risk_signal_detail").stream().map(r -> toPatternRiskVetoVO(r, bundle)).toList());
        vo.setInvalidations(bundle.rows("risk_signal_detail").stream().map(r -> toPatternInvalidationVO(r, bundle)).toList());
        vo.setLeaderFeedbacks(bundle.rows("risk_signal_snapshot").stream().map(r -> toLeaderNegativeFeedbackVO(r, bundle)).toList());
        vo.setMainlineRisks(bundle.rows("risk_signal_detail").stream().map(r -> toMainlineRiskVO(r, bundle)).toList());
        vo.setDataIntegrityRisks(bundle.rows("risk_signal_detail").stream().map(r -> toDataIntegrityRiskVO(r, bundle)).toList());
        vo.setActionMatrix(bundle.rows("risk_signal_detail").stream().map(r -> toRiskActionMatrixVO(r, bundle)).toList());
        vo.setHistoricalRiskSamples(bundle.rows("risk_signal_detail").stream().map(r -> toHistoricalRiskSampleVO(r, bundle)).toList());
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("risk_signal_snapshot"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("risk_signal_detail"), "risk_json"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "RiskControlOverviewVO" -> "risk_signal_detail";
            case "RiskScoreBreakdownVO" -> "risk_signal_detail";
            case "RiskSourceGroupVO" -> "risk_signal_detail";
            case "RiskSignalVO" -> "risk_signal_detail";
            case "PatternRiskVetoVO" -> "risk_signal_detail";
            case "PatternInvalidationVO" -> "risk_signal_detail";
            case "LeaderNegativeFeedbackVO" -> "risk_signal_snapshot";
            case "MainlineRiskVO" -> "risk_signal_detail";
            case "DataIntegrityRiskVO" -> "risk_signal_detail";
            case "RiskActionMatrixVO" -> "risk_signal_detail";
            case "HistoricalRiskSampleVO" -> "risk_signal_detail";
            case "RiskControlPageVO" -> "risk_signal_detail";
            default -> "risk_signal_snapshot";
        };
    }

    private List<String> stringList(Map<String, Object> row, String column) {
        String value = MapFieldReader.string(row, column);
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value);
    }


    private RiskControlPageVO.RiskControlOverviewVO toRiskControlOverviewVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RiskControlPageVO.RiskControlOverviewVO item = new RiskControlPageVO.RiskControlOverviewVO();
        item.setRiskLevel(MapFieldReader.string(row, "risk_level"));
        item.setRiskScore(MapFieldReader.decimal(row, "risk_score"));
        item.setRiskAction(MapFieldReader.string(row, "risk_action"));
        item.setEmotionStage(MapFieldReader.string(row, "primary_stage"));
        item.setRetreatStopTriggered(MapFieldReader.bool(row, "retreat_stop_triggered"));
        item.setClimaxNoChaseTriggered(MapFieldReader.bool(row, "climax_no_chase_triggered"));
        item.setOneVoteVetoTriggered(MapFieldReader.bool(row, "one_vote_veto_triggered"));
        item.setTotalRiskSignalCount(MapFieldReader.integer(row, "total_risk_signal_count"));
        item.setVetoedPatternCount(MapFieldReader.integer(row, "vetoed_pattern_count"));
        item.setOverviewText(MapFieldReader.string(row, "features"));
        return item;
    }

    private RiskControlPageVO.RiskScoreBreakdownVO toRiskScoreBreakdownVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RiskControlPageVO.RiskScoreBreakdownVO item = new RiskControlPageVO.RiskScoreBreakdownVO();
        item.setRiskScore(MapFieldReader.decimal(row, "risk_score"));
        item.setEmotionCycleRiskScore(MapFieldReader.decimal(row, "emotion_cycle_risk_score"));
        item.setLossEffectRiskScore(MapFieldReader.decimal(row, "loss_effect_risk_score"));
        item.setLimitEcoRiskScore(MapFieldReader.decimal(row, "limit_eco_risk_score"));
        item.setLeaderFeedbackRiskScore(MapFieldReader.decimal(row, "leader_feedback_risk_score"));
        item.setMainlineDecayRiskScore(MapFieldReader.decimal(row, "mainline_decay_risk_score"));
        item.setIndexFundRiskScore(MapFieldReader.decimal(row, "index_fund_risk_score"));
        item.setDataIntegrityRiskScore(MapFieldReader.decimal(row, "data_integrity_risk_score"));
        item.setFormulaText(MapFieldReader.string(row, "formula_text"));
        return item;
    }

    private RiskControlPageVO.RiskSourceGroupVO toRiskSourceGroupVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RiskControlPageVO.RiskSourceGroupVO item = new RiskControlPageVO.RiskSourceGroupVO();
        item.setRiskSource(MapFieldReader.string(row, "risk_source"));
        item.setSourceRiskScore(MapFieldReader.decimal(row, "source_risk_score"));
        item.setRiskLevel(MapFieldReader.string(row, "risk_level"));
        item.setSignalCount(MapFieldReader.integer(row, "signal_count"));
        item.setImpactPatternSignal(MapFieldReader.bool(row, "impact_pattern_signal"));
        item.setGroupText(MapFieldReader.string(row, "group_text"));
        return item;
    }

    private RiskControlPageVO.RiskSignalVO toRiskSignalVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RiskControlPageVO.RiskSignalVO item = new RiskControlPageVO.RiskSignalVO();
        item.setRiskSignalId(MapFieldReader.longValue(row, "risk_signal_id"));
        item.setRiskCode(MapFieldReader.string(row, "risk_code"));
        item.setRiskName(MapFieldReader.string(row, "risk_name"));
        item.setRiskSource(MapFieldReader.string(row, "risk_source"));
        item.setSignalLevel(MapFieldReader.string(row, "signal_level"));
        item.setRiskLevel(MapFieldReader.string(row, "risk_level"));
        item.setRiskScore(MapFieldReader.decimal(row, "risk_score"));
        item.setRiskAction(MapFieldReader.string(row, "risk_action"));
        item.setOneVoteVeto(MapFieldReader.bool(row, "one_vote_veto"));
        item.setRiskText(MapFieldReader.string(row, "risk_json"));
        item.setEvidences(stringList(row, "evidences"));
        item.setImpacts(stringList(row, "impacts"));
        return item;
    }

    private RiskControlPageVO.PatternRiskVetoVO toPatternRiskVetoVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RiskControlPageVO.PatternRiskVetoVO item = new RiskControlPageVO.PatternRiskVetoVO();
        item.setVetoId(MapFieldReader.longValue(row, "veto_id"));
        item.setSignalId(MapFieldReader.longValue(row, "signal_id"));
        item.setPatternCode(MapFieldReader.string(row, "pattern_code"));
        item.setStockCode(MapFieldReader.string(row, "stock_code"));
        item.setRiskCode(MapFieldReader.string(row, "risk_code"));
        item.setRiskLevel(MapFieldReader.string(row, "risk_level"));
        item.setRiskScore(MapFieldReader.decimal(row, "risk_score"));
        item.setRiskAction(MapFieldReader.string(row, "risk_action"));
        item.setOneVoteVeto(MapFieldReader.bool(row, "one_vote_veto"));
        item.setVetoReason(MapFieldReader.string(row, "veto_reason"));
        return item;
    }

    private RiskControlPageVO.PatternInvalidationVO toPatternInvalidationVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RiskControlPageVO.PatternInvalidationVO item = new RiskControlPageVO.PatternInvalidationVO();
        item.setInvalidationId(MapFieldReader.longValue(row, "invalidation_id"));
        item.setSignalId(MapFieldReader.longValue(row, "signal_id"));
        item.setPatternCode(MapFieldReader.string(row, "pattern_code"));
        item.setStockCode(MapFieldReader.string(row, "stock_code"));
        item.setInvalidationType(MapFieldReader.string(row, "invalidation_type"));
        item.setInvalidationReason(MapFieldReader.string(row, "invalidation_reason"));
        item.setSavedAsFailureCase(MapFieldReader.bool(row, "saved_as_failure_case"));
        return item;
    }

    private RiskControlPageVO.LeaderNegativeFeedbackVO toLeaderNegativeFeedbackVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RiskControlPageVO.LeaderNegativeFeedbackVO item = new RiskControlPageVO.LeaderNegativeFeedbackVO();
        item.setStockCode(MapFieldReader.string(row, "stock_code"));
        item.setStockName(MapFieldReader.string(row, "stock_name"));
        item.setLeaderType(MapFieldReader.string(row, "leader_type"));
        item.setNegativeFeedbackScore(MapFieldReader.decimal(row, "negative_feedback_score"));
        item.setTriggerRiskControl(MapFieldReader.bool(row, "trigger_risk_control"));
        item.setFeedbackText(MapFieldReader.string(row, "feedback_text"));
        return item;
    }

    private RiskControlPageVO.MainlineRiskVO toMainlineRiskVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RiskControlPageVO.MainlineRiskVO item = new RiskControlPageVO.MainlineRiskVO();
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setMainlineName(MapFieldReader.string(row, "mainline_name"));
        item.setRiskType(MapFieldReader.string(row, "risk_type"));
        item.setRiskLevel(MapFieldReader.string(row, "risk_level"));
        item.setRiskScore(MapFieldReader.decimal(row, "risk_score"));
        item.setRiskText(MapFieldReader.string(row, "risk_json"));
        return item;
    }

    private RiskControlPageVO.DataIntegrityRiskVO toDataIntegrityRiskVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RiskControlPageVO.DataIntegrityRiskVO item = new RiskControlPageVO.DataIntegrityRiskVO();
        item.setDataDomain(MapFieldReader.string(row, "data_domain"));
        item.setCheckStatus(MapFieldReader.string(row, "check_status"));
        item.setCritical(MapFieldReader.bool(row, "critical"));
        item.setCompletenessRatio(MapFieldReader.decimal(row, "completeness_ratio"));
        item.setDataRiskText(MapFieldReader.string(row, "data_risk_text"));
        return item;
    }

    private RiskControlPageVO.RiskActionMatrixVO toRiskActionMatrixVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RiskControlPageVO.RiskActionMatrixVO item = new RiskControlPageVO.RiskActionMatrixVO();
        item.setRiskAction(MapFieldReader.string(row, "risk_action"));
        item.setImpactObjectType(MapFieldReader.string(row, "impact_object_type"));
        item.setForbidConditionMetDisplay(MapFieldReader.bool(row, "forbid_condition_met_display"));
        item.setTriggerRiskVeto(MapFieldReader.bool(row, "trigger_risk_veto"));
        item.setStopObserving(MapFieldReader.bool(row, "stop_observing"));
        item.setActionText(MapFieldReader.string(row, "action_text"));
        return item;
    }

    private RiskControlPageVO.HistoricalRiskSampleVO toHistoricalRiskSampleVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                RiskControlPageVO.HistoricalRiskSampleVO item = new RiskControlPageVO.HistoricalRiskSampleVO();
        item.setSampleId(MapFieldReader.longValue(row, "sample_id"));
        item.setTradeDate(MapFieldReader.localDate(row, "trade_date"));
        item.setEmotionStage(MapFieldReader.string(row, "primary_stage"));
        item.setRiskLevel(MapFieldReader.string(row, "risk_level"));
        item.setRiskSimilarityScore(MapFieldReader.decimal(row, "risk_similarity_score"));
        item.setFuture3dReturn(MapFieldReader.decimal(row, "future_3d_return"));
        item.setMaxDrawdown(MapFieldReader.decimal(row, "max_drawdown"));
        item.setSampleText(MapFieldReader.string(row, "evidence_json"));
        return item;
    }

}
