package com.astock.module.risk.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RiskControlFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_10_RISK_CONTROL";
    }

    @Override
    public String voClassName() {
        return "RiskControlPageVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("RiskControlPageVO.tradeDate", "CLICKHOUSE", "risk_signal_snapshot", "trade_date", "", false),
                new FieldMapping("RiskControlPageVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("RiskControlPageVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("RiskControlPageVO.overview", "CLICKHOUSE", "risk_signal_snapshot", "overview", "", false),
                new FieldMapping("RiskControlPageVO.scoreBreakdown", "CLICKHOUSE", "risk_signal_snapshot", "score_breakdown", "", false),
                new FieldMapping("RiskControlPageVO.riskSourceGroups", "CLICKHOUSE", "risk_signal_snapshot", "risk_source_groups", "", false),
                new FieldMapping("RiskControlPageVO.riskSignals", "CLICKHOUSE", "risk_signal_snapshot", "risk_signals", "", false),
                new FieldMapping("RiskControlPageVO.patternVetos", "CLICKHOUSE", "risk_signal_snapshot", "pattern_vetos", "", false),
                new FieldMapping("RiskControlPageVO.invalidations", "CLICKHOUSE", "risk_signal_snapshot", "invalidations", "", false),
                new FieldMapping("RiskControlPageVO.leaderFeedbacks", "CLICKHOUSE", "risk_signal_snapshot", "leader_feedbacks", "", false),
                new FieldMapping("RiskControlPageVO.mainlineRisks", "CLICKHOUSE", "risk_signal_snapshot", "mainline_risks", "", false),
                new FieldMapping("RiskControlPageVO.dataIntegrityRisks", "CLICKHOUSE", "risk_signal_snapshot", "data_integrity_risks", "", false),
                new FieldMapping("RiskControlPageVO.actionMatrix", "CLICKHOUSE", "risk_signal_snapshot", "action_matrix", "", false),
                new FieldMapping("RiskControlPageVO.historicalRiskSamples", "CLICKHOUSE", "risk_signal_snapshot", "historical_risk_samples", "", false),
                new FieldMapping("RiskControlPageVO.conclusion", "CLICKHOUSE", "risk_signal_snapshot", "evidence_json", "", false),
                new FieldMapping("RiskControlPageVO.riskTips", "CLICKHOUSE", "risk_signal_snapshot", "risk_json", "", false),
                new FieldMapping("RiskControlOverviewVO.riskLevel", "CLICKHOUSE", "risk_signal_snapshot", "risk_level", "", false),
                new FieldMapping("RiskControlOverviewVO.riskScore", "CLICKHOUSE", "risk_signal_snapshot", "risk_score", "", false),
                new FieldMapping("RiskControlOverviewVO.riskAction", "CLICKHOUSE", "risk_signal_snapshot", "risk_action", "", false),
                new FieldMapping("RiskControlOverviewVO.emotionStage", "CLICKHOUSE", "risk_signal_snapshot", "primary_stage", "", false),
                new FieldMapping("RiskControlOverviewVO.retreatStopTriggered", "CLICKHOUSE", "risk_signal_snapshot", "retreat_stop_triggered", "", false),
                new FieldMapping("RiskControlOverviewVO.climaxNoChaseTriggered", "CLICKHOUSE", "risk_signal_snapshot", "climax_no_chase_triggered", "", false),
                new FieldMapping("RiskControlOverviewVO.oneVoteVetoTriggered", "CLICKHOUSE", "risk_signal_snapshot", "one_vote_veto_triggered", "", false),
                new FieldMapping("RiskControlOverviewVO.totalRiskSignalCount", "CLICKHOUSE", "risk_signal_snapshot", "total_risk_signal_count", "", false),
                new FieldMapping("RiskControlOverviewVO.vetoedPatternCount", "CLICKHOUSE", "risk_signal_snapshot", "vetoed_pattern_count", "", false),
                new FieldMapping("RiskControlOverviewVO.overviewText", "CLICKHOUSE", "risk_signal_snapshot", "features", "", false),
                new FieldMapping("RiskScoreBreakdownVO.riskScore", "CLICKHOUSE", "risk_signal_snapshot", "risk_score", "", false),
                new FieldMapping("RiskScoreBreakdownVO.emotionCycleRiskScore", "CLICKHOUSE", "risk_signal_snapshot", "emotion_cycle_risk_score", "", false),
                new FieldMapping("RiskScoreBreakdownVO.lossEffectRiskScore", "CLICKHOUSE", "risk_signal_snapshot", "loss_effect_risk_score", "", false),
                new FieldMapping("RiskScoreBreakdownVO.limitEcoRiskScore", "CLICKHOUSE", "risk_signal_snapshot", "limit_eco_risk_score", "", false),
                new FieldMapping("RiskScoreBreakdownVO.leaderFeedbackRiskScore", "CLICKHOUSE", "risk_signal_snapshot", "leader_feedback_risk_score", "", false),
                new FieldMapping("RiskScoreBreakdownVO.mainlineDecayRiskScore", "CLICKHOUSE", "risk_signal_snapshot", "mainline_decay_risk_score", "", false),
                new FieldMapping("RiskScoreBreakdownVO.indexFundRiskScore", "CLICKHOUSE", "risk_signal_snapshot", "index_fund_risk_score", "", false),
                new FieldMapping("RiskScoreBreakdownVO.dataIntegrityRiskScore", "CLICKHOUSE", "risk_signal_snapshot", "data_integrity_risk_score", "", false),
                new FieldMapping("RiskScoreBreakdownVO.formulaText", "CLICKHOUSE", "risk_signal_snapshot", "formula_text", "", false),
                new FieldMapping("RiskSourceGroupVO.riskSource", "CLICKHOUSE", "risk_signal_snapshot", "risk_source", "", false),
                new FieldMapping("RiskSourceGroupVO.sourceRiskScore", "CLICKHOUSE", "risk_signal_snapshot", "source_risk_score", "", false),
                new FieldMapping("RiskSourceGroupVO.riskLevel", "CLICKHOUSE", "risk_signal_snapshot", "risk_level", "", false),
                new FieldMapping("RiskSourceGroupVO.signalCount", "CLICKHOUSE", "risk_signal_snapshot", "signal_count", "", false),
                new FieldMapping("RiskSourceGroupVO.impactPatternSignal", "CLICKHOUSE", "risk_signal_snapshot", "impact_pattern_signal", "", false),
                new FieldMapping("RiskSourceGroupVO.groupText", "CLICKHOUSE", "risk_signal_snapshot", "group_text", "", false),
                new FieldMapping("RiskSignalVO.riskSignalId", "CLICKHOUSE", "risk_signal_snapshot", "risk_signal_id", "", false),
                new FieldMapping("RiskSignalVO.riskCode", "CLICKHOUSE", "risk_signal_snapshot", "risk_code", "", false),
                new FieldMapping("RiskSignalVO.riskName", "CLICKHOUSE", "risk_signal_snapshot", "risk_name", "", false),
                new FieldMapping("RiskSignalVO.riskSource", "CLICKHOUSE", "risk_signal_snapshot", "risk_source", "", false),
                new FieldMapping("RiskSignalVO.signalLevel", "CLICKHOUSE", "risk_signal_snapshot", "signal_level", "", false),
                new FieldMapping("RiskSignalVO.riskLevel", "CLICKHOUSE", "risk_signal_snapshot", "risk_level", "", false),
                new FieldMapping("RiskSignalVO.riskScore", "CLICKHOUSE", "risk_signal_snapshot", "risk_score", "", false),
                new FieldMapping("RiskSignalVO.riskAction", "CLICKHOUSE", "risk_signal_snapshot", "risk_action", "", false),
                new FieldMapping("RiskSignalVO.oneVoteVeto", "CLICKHOUSE", "risk_signal_snapshot", "one_vote_veto", "", false),
                new FieldMapping("RiskSignalVO.riskText", "CLICKHOUSE", "risk_signal_snapshot", "risk_json", "", false),
                new FieldMapping("RiskSignalVO.evidences", "CLICKHOUSE", "risk_signal_snapshot", "evidences", "", false),
                new FieldMapping("RiskSignalVO.impacts", "CLICKHOUSE", "risk_signal_snapshot", "impacts", "", false),
                new FieldMapping("PatternRiskVetoVO.vetoId", "CLICKHOUSE", "risk_signal_snapshot", "veto_id", "", false),
                new FieldMapping("PatternRiskVetoVO.signalId", "CLICKHOUSE", "risk_signal_snapshot", "signal_id", "", false),
                new FieldMapping("PatternRiskVetoVO.patternCode", "CLICKHOUSE", "risk_signal_snapshot", "pattern_code", "", false),
                new FieldMapping("PatternRiskVetoVO.stockCode", "CLICKHOUSE", "risk_signal_snapshot", "stock_code", "", false),
                new FieldMapping("PatternRiskVetoVO.riskCode", "CLICKHOUSE", "risk_signal_snapshot", "risk_code", "", false),
                new FieldMapping("PatternRiskVetoVO.riskLevel", "CLICKHOUSE", "risk_signal_snapshot", "risk_level", "", false),
                new FieldMapping("PatternRiskVetoVO.riskScore", "CLICKHOUSE", "risk_signal_snapshot", "risk_score", "", false),
                new FieldMapping("PatternRiskVetoVO.riskAction", "CLICKHOUSE", "risk_signal_snapshot", "risk_action", "", false),
                new FieldMapping("PatternRiskVetoVO.oneVoteVeto", "CLICKHOUSE", "risk_signal_snapshot", "one_vote_veto", "", false),
                new FieldMapping("PatternRiskVetoVO.vetoReason", "CLICKHOUSE", "risk_signal_snapshot", "veto_reason", "", false),
                new FieldMapping("PatternInvalidationVO.invalidationId", "CLICKHOUSE", "risk_signal_snapshot", "invalidation_id", "", false),
                new FieldMapping("PatternInvalidationVO.signalId", "CLICKHOUSE", "risk_signal_snapshot", "signal_id", "", false),
                new FieldMapping("PatternInvalidationVO.patternCode", "CLICKHOUSE", "risk_signal_snapshot", "pattern_code", "", false),
                new FieldMapping("PatternInvalidationVO.stockCode", "CLICKHOUSE", "risk_signal_snapshot", "stock_code", "", false),
                new FieldMapping("PatternInvalidationVO.invalidationType", "CLICKHOUSE", "risk_signal_snapshot", "invalidation_type", "", false),
                new FieldMapping("PatternInvalidationVO.invalidationReason", "CLICKHOUSE", "risk_signal_snapshot", "invalidation_reason", "", false),
                new FieldMapping("PatternInvalidationVO.savedAsFailureCase", "CLICKHOUSE", "risk_signal_snapshot", "saved_as_failure_case", "", false),
                new FieldMapping("LeaderNegativeFeedbackVO.stockCode", "CLICKHOUSE", "risk_signal_snapshot", "stock_code", "", false),
                new FieldMapping("LeaderNegativeFeedbackVO.stockName", "CLICKHOUSE", "risk_signal_snapshot", "stock_name", "", false),
                new FieldMapping("LeaderNegativeFeedbackVO.leaderType", "CLICKHOUSE", "risk_signal_snapshot", "leader_type", "", false),
                new FieldMapping("LeaderNegativeFeedbackVO.negativeFeedbackScore", "CLICKHOUSE", "risk_signal_snapshot", "negative_feedback_score", "", false),
                new FieldMapping("LeaderNegativeFeedbackVO.triggerRiskControl", "CLICKHOUSE", "risk_signal_snapshot", "trigger_risk_control", "", false),
                new FieldMapping("LeaderNegativeFeedbackVO.feedbackText", "CLICKHOUSE", "risk_signal_snapshot", "feedback_text", "", false),
                new FieldMapping("MainlineRiskVO.mainlineId", "CLICKHOUSE", "risk_signal_snapshot", "mainline_id", "", false),
                new FieldMapping("MainlineRiskVO.mainlineName", "CLICKHOUSE", "risk_signal_snapshot", "mainline_name", "", false),
                new FieldMapping("MainlineRiskVO.riskType", "CLICKHOUSE", "risk_signal_snapshot", "risk_type", "", false),
                new FieldMapping("MainlineRiskVO.riskLevel", "CLICKHOUSE", "risk_signal_snapshot", "risk_level", "", false),
                new FieldMapping("MainlineRiskVO.riskScore", "CLICKHOUSE", "risk_signal_snapshot", "risk_score", "", false),
                new FieldMapping("MainlineRiskVO.riskText", "CLICKHOUSE", "risk_signal_snapshot", "risk_json", "", false),
                new FieldMapping("DataIntegrityRiskVO.dataDomain", "CLICKHOUSE", "risk_signal_snapshot", "data_domain", "", false),
                new FieldMapping("DataIntegrityRiskVO.checkStatus", "CLICKHOUSE", "risk_signal_snapshot", "check_status", "", false),
                new FieldMapping("DataIntegrityRiskVO.critical", "CLICKHOUSE", "risk_signal_snapshot", "critical", "", false),
                new FieldMapping("DataIntegrityRiskVO.completenessRatio", "CLICKHOUSE", "risk_signal_snapshot", "completeness_ratio", "", false),
                new FieldMapping("DataIntegrityRiskVO.dataRiskText", "CLICKHOUSE", "risk_signal_snapshot", "data_risk_text", "", false),
                new FieldMapping("RiskActionMatrixVO.riskAction", "CLICKHOUSE", "risk_signal_snapshot", "risk_action", "", false),
                new FieldMapping("RiskActionMatrixVO.impactObjectType", "CLICKHOUSE", "risk_signal_snapshot", "impact_object_type", "", false),
                new FieldMapping("RiskActionMatrixVO.forbidConditionMetDisplay", "CLICKHOUSE", "risk_signal_snapshot", "forbid_condition_met_display", "", false),
                new FieldMapping("RiskActionMatrixVO.triggerRiskVeto", "CLICKHOUSE", "risk_signal_snapshot", "trigger_risk_veto", "", false),
                new FieldMapping("RiskActionMatrixVO.stopObserving", "CLICKHOUSE", "risk_signal_snapshot", "stop_observing", "", false),
                new FieldMapping("RiskActionMatrixVO.actionText", "CLICKHOUSE", "risk_signal_snapshot", "action_text", "", false),
                new FieldMapping("HistoricalRiskSampleVO.sampleId", "CLICKHOUSE", "risk_signal_snapshot", "sample_id", "", false),
                new FieldMapping("HistoricalRiskSampleVO.tradeDate", "CLICKHOUSE", "risk_signal_snapshot", "trade_date", "", false),
                new FieldMapping("HistoricalRiskSampleVO.emotionStage", "CLICKHOUSE", "risk_signal_snapshot", "primary_stage", "", false),
                new FieldMapping("HistoricalRiskSampleVO.riskLevel", "CLICKHOUSE", "risk_signal_snapshot", "risk_level", "", false),
                new FieldMapping("HistoricalRiskSampleVO.riskSimilarityScore", "CLICKHOUSE", "risk_signal_snapshot", "risk_similarity_score", "", false),
                new FieldMapping("HistoricalRiskSampleVO.future3dReturn", "CLICKHOUSE", "risk_signal_snapshot", "future_3d_return", "", false),
                new FieldMapping("HistoricalRiskSampleVO.maxDrawdown", "CLICKHOUSE", "risk_signal_snapshot", "max_drawdown", "", false),
                new FieldMapping("HistoricalRiskSampleVO.sampleText", "CLICKHOUSE", "risk_signal_snapshot", "evidence_json", "", false)
        );
    }
}
