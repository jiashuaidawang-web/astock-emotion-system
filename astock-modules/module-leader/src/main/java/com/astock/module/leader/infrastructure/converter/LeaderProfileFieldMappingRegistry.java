package com.astock.module.leader.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeaderProfileFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_08_LEADER_PROFILE";
    }

    @Override
    public String voClassName() {
        return "LeaderProfilePageVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("LeaderProfilePageVO.tradeDate", "CLICKHOUSE", "leader_daily_snapshot", "trade_date", "", false),
                new FieldMapping("LeaderProfilePageVO.stockCode", "CLICKHOUSE", "leader_daily_snapshot", "stock_code", "", false),
                new FieldMapping("LeaderProfilePageVO.stockName", "CLICKHOUSE", "leader_daily_snapshot", "stock_name", "", false),
                new FieldMapping("LeaderProfilePageVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("LeaderProfilePageVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("LeaderProfilePageVO.identity", "CLICKHOUSE", "leader_daily_snapshot", "identity", "", false),
                new FieldMapping("LeaderProfilePageVO.scoreBreakdown", "CLICKHOUSE", "leader_daily_snapshot", "score_breakdown", "", false),
                new FieldMapping("LeaderProfilePageVO.marketData", "CLICKHOUSE", "leader_daily_snapshot", "market_data", "", false),
                new FieldMapping("LeaderProfilePageVO.mainlineRelation", "CLICKHOUSE", "leader_daily_snapshot", "mainline_relation", "", false),
                new FieldMapping("LeaderProfilePageVO.boardStructure", "CLICKHOUSE", "leader_daily_snapshot", "board_structure", "", false),
                new FieldMapping("LeaderProfilePageVO.trendStructure", "CLICKHOUSE", "leader_daily_snapshot", "trend_structure", "", false),
                new FieldMapping("LeaderProfilePageVO.driveAnalysis", "CLICKHOUSE", "leader_daily_snapshot", "drive_analysis", "", false),
                new FieldMapping("LeaderProfilePageVO.negativeFeedback", "CLICKHOUSE", "leader_daily_snapshot", "negative_feedback", "", false),
                new FieldMapping("LeaderProfilePageVO.lifecycle", "CLICKHOUSE", "leader_daily_snapshot", "lifecycle", "", false),
                new FieldMapping("LeaderProfilePageVO.relatedPatternSignals", "CLICKHOUSE", "leader_daily_snapshot", "related_pattern_signals", "", false),
                new FieldMapping("LeaderProfilePageVO.relatedRiskSignals", "CLICKHOUSE", "leader_daily_snapshot", "related_risk_signals", "", false),
                new FieldMapping("LeaderProfilePageVO.similarLeaderSamples", "CLICKHOUSE", "leader_daily_snapshot", "similar_leader_samples", "", false),
                new FieldMapping("LeaderProfilePageVO.evidenceChain", "CLICKHOUSE", "leader_daily_snapshot", "evidence_chain", "", false),
                new FieldMapping("LeaderProfilePageVO.conclusion", "CLICKHOUSE", "leader_daily_snapshot", "evidence_json", "", false),
                new FieldMapping("LeaderProfilePageVO.riskTips", "CLICKHOUSE", "leader_daily_snapshot", "risk_json", "", false),
                new FieldMapping("LeaderIdentityVO.leaderType", "CLICKHOUSE", "leader_daily_snapshot", "leader_type", "", false),
                new FieldMapping("LeaderIdentityVO.leaderStatus", "CLICKHOUSE", "leader_daily_snapshot", "leader_status", "", false),
                new FieldMapping("LeaderIdentityVO.roleInMainline", "CLICKHOUSE", "leader_daily_snapshot", "role_in_mainline", "", false),
                new FieldMapping("LeaderIdentityVO.inPatternWatchPool", "CLICKHOUSE", "leader_daily_snapshot", "in_pattern_watch_pool", "", false),
                new FieldMapping("LeaderIdentityVO.riskVeto", "CLICKHOUSE", "leader_daily_snapshot", "risk_veto", "", false),
                new FieldMapping("LeaderIdentityVO.identityText", "CLICKHOUSE", "leader_daily_snapshot", "identity_text", "", false),
                new FieldMapping("LeaderScoreBreakdownVO.leaderScore", "CLICKHOUSE", "leader_daily_snapshot", "leader_score", "", false),
                new FieldMapping("LeaderScoreBreakdownVO.recognitionScore", "CLICKHOUSE", "leader_daily_snapshot", "recognition_score", "", false),
                new FieldMapping("LeaderScoreBreakdownVO.mainlineRelationScore", "CLICKHOUSE", "leader_daily_snapshot", "mainline_relation_score", "", false),
                new FieldMapping("LeaderScoreBreakdownVO.driveScore", "CLICKHOUSE", "leader_daily_snapshot", "drive_score", "", false),
                new FieldMapping("LeaderScoreBreakdownVO.strengthScore", "CLICKHOUSE", "leader_daily_snapshot", "strength_score", "", false),
                new FieldMapping("LeaderScoreBreakdownVO.supportScore", "CLICKHOUSE", "leader_daily_snapshot", "support_score", "", false),
                new FieldMapping("LeaderScoreBreakdownVO.continuityScore", "CLICKHOUSE", "leader_daily_snapshot", "continuity_score", "", false),
                new FieldMapping("LeaderScoreBreakdownVO.negativeFeedbackScore", "CLICKHOUSE", "leader_daily_snapshot", "negative_feedback_score", "", false),
                new FieldMapping("LeaderMarketDataVO.closePrice", "CLICKHOUSE", "leader_daily_snapshot", "close_price", "", false),
                new FieldMapping("LeaderMarketDataVO.pctChange", "CLICKHOUSE", "leader_daily_snapshot", "pct_change", "", false),
                new FieldMapping("LeaderMarketDataVO.turnoverAmount", "CLICKHOUSE", "leader_daily_snapshot", "turnover_amount", "", false),
                new FieldMapping("LeaderMarketDataVO.turnoverRate", "CLICKHOUSE", "leader_daily_snapshot", "turnover_rate", "", false),
                new FieldMapping("LeaderMarketDataVO.volumeRatio", "CLICKHOUSE", "leader_daily_snapshot", "volume_ratio", "", false),
                new FieldMapping("LeaderMainlineRelationVO.mainlineId", "CLICKHOUSE", "leader_daily_snapshot", "mainline_id", "", false),
                new FieldMapping("LeaderMainlineRelationVO.mainlineName", "CLICKHOUSE", "leader_daily_snapshot", "mainline_name", "", false),
                new FieldMapping("LeaderMainlineRelationVO.lifecycleStage", "CLICKHOUSE", "leader_daily_snapshot", "lifecycle_stage", "", false),
                new FieldMapping("LeaderMainlineRelationVO.relationScore", "CLICKHOUSE", "leader_daily_snapshot", "relation_score", "", false),
                new FieldMapping("LeaderMainlineRelationVO.relationText", "CLICKHOUSE", "leader_daily_snapshot", "relation_text", "", false),
                new FieldMapping("LeaderBoardStructureVO.boardHeight", "CLICKHOUSE", "leader_daily_snapshot", "board_height", "", false),
                new FieldMapping("LeaderBoardStructureVO.limitUp", "CLICKHOUSE", "leader_daily_snapshot", "limit_up", "", false),
                new FieldMapping("LeaderBoardStructureVO.brokenBoard", "CLICKHOUSE", "leader_daily_snapshot", "broken_board", "", false),
                new FieldMapping("LeaderBoardStructureVO.reversalBoard", "CLICKHOUSE", "leader_daily_snapshot", "reversal_board", "", false),
                new FieldMapping("LeaderBoardStructureVO.boardText", "CLICKHOUSE", "leader_daily_snapshot", "board_text", "", false),
                new FieldMapping("LeaderTrendStructureVO.trendLeaderType", "CLICKHOUSE", "leader_daily_snapshot", "trend_leader_type", "", false),
                new FieldMapping("LeaderTrendStructureVO.trendPosition", "CLICKHOUSE", "leader_daily_snapshot", "trend_position", "", false),
                new FieldMapping("LeaderTrendStructureVO.trendStrengthScore", "CLICKHOUSE", "leader_daily_snapshot", "trend_strength_score", "", false),
                new FieldMapping("LeaderTrendStructureVO.trendBroken", "CLICKHOUSE", "leader_daily_snapshot", "trend_broken", "", false),
                new FieldMapping("LeaderTrendStructureVO.trendText", "CLICKHOUSE", "leader_daily_snapshot", "trend_text", "", false),
                new FieldMapping("LeaderDriveAnalysisVO.leaderDriveScore", "CLICKHOUSE", "leader_daily_snapshot", "leader_drive_score", "", false),
                new FieldMapping("LeaderDriveAnalysisVO.sectorDriveScore", "CLICKHOUSE", "leader_daily_snapshot", "sector_drive_score", "", false),
                new FieldMapping("LeaderDriveAnalysisVO.mainlineDriveScore", "CLICKHOUSE", "leader_daily_snapshot", "mainline_drive_score", "", false),
                new FieldMapping("LeaderDriveAnalysisVO.emotionDriveScore", "CLICKHOUSE", "leader_daily_snapshot", "emotion_drive_score", "", false),
                new FieldMapping("LeaderDriveAnalysisVO.fundDriveScore", "CLICKHOUSE", "leader_daily_snapshot", "fund_drive_score", "", false),
                new FieldMapping("LeaderDriveAnalysisVO.driveText", "CLICKHOUSE", "leader_daily_snapshot", "drive_text", "", false),
                new FieldMapping("LeaderNegativeFeedbackAnalysisVO.negativeFeedbackScore", "CLICKHOUSE", "leader_daily_snapshot", "negative_feedback_score", "", false),
                new FieldMapping("LeaderNegativeFeedbackAnalysisVO.brokenBoard", "CLICKHOUSE", "leader_daily_snapshot", "broken_board", "", false),
                new FieldMapping("LeaderNegativeFeedbackAnalysisVO.limitDown", "CLICKHOUSE", "leader_daily_snapshot", "limit_down", "", false),
                new FieldMapping("LeaderNegativeFeedbackAnalysisVO.impactMainline", "CLICKHOUSE", "leader_daily_snapshot", "impact_mainline", "", false),
                new FieldMapping("LeaderNegativeFeedbackAnalysisVO.impactEmotionCycle", "CLICKHOUSE", "leader_daily_snapshot", "impact_emotion_cycle", "", false),
                new FieldMapping("LeaderNegativeFeedbackAnalysisVO.feedbackText", "CLICKHOUSE", "leader_daily_snapshot", "feedback_text", "", false),
                new FieldMapping("LeaderLifecyclePointVO.tradeDate", "CLICKHOUSE", "leader_daily_snapshot", "trade_date", "", false),
                new FieldMapping("LeaderLifecyclePointVO.leaderType", "CLICKHOUSE", "leader_daily_snapshot", "leader_type", "", false),
                new FieldMapping("LeaderLifecyclePointVO.leaderStatus", "CLICKHOUSE", "leader_daily_snapshot", "leader_status", "", false),
                new FieldMapping("LeaderLifecyclePointVO.leaderScore", "CLICKHOUSE", "leader_daily_snapshot", "leader_score", "", false),
                new FieldMapping("LeaderLifecyclePointVO.pointText", "CLICKHOUSE", "leader_daily_snapshot", "point_text", "", false),
                new FieldMapping("LeaderRelatedPatternSignalVO.signalId", "CLICKHOUSE", "leader_daily_snapshot", "signal_id", "", false),
                new FieldMapping("LeaderRelatedPatternSignalVO.patternCode", "CLICKHOUSE", "leader_daily_snapshot", "pattern_code", "", false),
                new FieldMapping("LeaderRelatedPatternSignalVO.conditionStatus", "CLICKHOUSE", "leader_daily_snapshot", "condition_status", "", false),
                new FieldMapping("LeaderRelatedPatternSignalVO.conditionScore", "CLICKHOUSE", "leader_daily_snapshot", "condition_score", "", false),
                new FieldMapping("LeaderRelatedPatternSignalVO.riskVeto", "CLICKHOUSE", "leader_daily_snapshot", "risk_veto", "", false),
                new FieldMapping("LeaderRelatedRiskSignalVO.riskSignalId", "CLICKHOUSE", "leader_daily_snapshot", "risk_signal_id", "", false),
                new FieldMapping("LeaderRelatedRiskSignalVO.riskCode", "CLICKHOUSE", "leader_daily_snapshot", "risk_code", "", false),
                new FieldMapping("LeaderRelatedRiskSignalVO.riskLevel", "CLICKHOUSE", "leader_daily_snapshot", "risk_level", "", false),
                new FieldMapping("LeaderRelatedRiskSignalVO.riskScore", "CLICKHOUSE", "leader_daily_snapshot", "risk_score", "", false),
                new FieldMapping("LeaderRelatedRiskSignalVO.riskText", "CLICKHOUSE", "leader_daily_snapshot", "risk_json", "", false),
                new FieldMapping("HistoricalLeaderSampleVO.sampleId", "CLICKHOUSE", "leader_daily_snapshot", "sample_id", "", false),
                new FieldMapping("HistoricalLeaderSampleVO.tradeDate", "CLICKHOUSE", "leader_daily_snapshot", "trade_date", "", false),
                new FieldMapping("HistoricalLeaderSampleVO.historicalStockName", "CLICKHOUSE", "leader_daily_snapshot", "historical_stock_name", "", false),
                new FieldMapping("HistoricalLeaderSampleVO.leaderType", "CLICKHOUSE", "leader_daily_snapshot", "leader_type", "", false),
                new FieldMapping("HistoricalLeaderSampleVO.similarityScore", "CLICKHOUSE", "leader_daily_snapshot", "similarity_score", "", false),
                new FieldMapping("HistoricalLeaderSampleVO.future3dReturn", "CLICKHOUSE", "leader_daily_snapshot", "future_3d_return", "", false),
                new FieldMapping("HistoricalLeaderSampleVO.maxDrawdown", "CLICKHOUSE", "leader_daily_snapshot", "max_drawdown", "", false),
                new FieldMapping("LeaderEvidenceChainVO.identityEvidences", "CLICKHOUSE", "leader_daily_snapshot", "identity_evidences", "", false),
                new FieldMapping("LeaderEvidenceChainVO.driveEvidences", "CLICKHOUSE", "leader_daily_snapshot", "drive_evidences", "", false),
                new FieldMapping("LeaderEvidenceChainVO.riskEvidences", "CLICKHOUSE", "leader_daily_snapshot", "risk_evidences", "", false),
                new FieldMapping("LeaderEvidenceChainVO.evidenceSummary", "CLICKHOUSE", "leader_daily_snapshot", "evidence_json", "", false)
        );
    }
}
