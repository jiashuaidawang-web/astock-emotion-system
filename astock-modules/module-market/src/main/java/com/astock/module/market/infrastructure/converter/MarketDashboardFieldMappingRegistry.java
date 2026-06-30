package com.astock.module.market.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarketDashboardFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_01_MARKET_DASHBOARD";
    }

    @Override
    public String voClassName() {
        return "MarketDashboardVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("MarketDashboardVO.tradeDate", "CLICKHOUSE", "market_factor_snapshot", "trade_date", "", false),
                new FieldMapping("MarketDashboardVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("MarketDashboardVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("MarketDashboardVO.emotionStage", "CLICKHOUSE", "market_factor_snapshot", "primary_stage", "", false),
                new FieldMapping("MarketDashboardVO.riskSummary", "CLICKHOUSE", "market_factor_snapshot", "risk_summary", "", false),
                new FieldMapping("MarketDashboardVO.marketBreadth", "CLICKHOUSE", "market_factor_snapshot", "market_breadth", "", false),
                new FieldMapping("MarketDashboardVO.limitUpDownEco", "CLICKHOUSE", "market_factor_snapshot", "limit_up_down_eco", "", false),
                new FieldMapping("MarketDashboardVO.turnoverSummary", "CLICKHOUSE", "market_factor_snapshot", "turnover_summary", "", false),
                new FieldMapping("MarketDashboardVO.similaritySummary", "CLICKHOUSE", "market_factor_snapshot", "similarity_summary", "", false),
                new FieldMapping("MarketDashboardVO.topMainlines", "CLICKHOUSE", "market_factor_snapshot", "top_mainlines", "", false),
                new FieldMapping("MarketDashboardVO.leaderWatchList", "CLICKHOUSE", "market_factor_snapshot", "leader_watch_list", "", false),
                new FieldMapping("MarketDashboardVO.patternSignalSummary", "CLICKHOUSE", "market_factor_snapshot", "pattern_signal_summary", "", false),
                new FieldMapping("MarketDashboardVO.riskVetoSummary", "CLICKHOUSE", "market_factor_snapshot", "risk_veto_summary", "", false),
                new FieldMapping("MarketDashboardVO.summaryText", "CLICKHOUSE", "market_factor_snapshot", "evidence_json", "", false),
                new FieldMapping("MarketDashboardVO.keyEvidences", "CLICKHOUSE", "market_factor_snapshot", "key_evidences", "", false),
                new FieldMapping("MarketDashboardVO.keyRisks", "CLICKHOUSE", "market_factor_snapshot", "key_risks", "", false),
                new FieldMapping("MarketDashboardVO.conclusion", "CLICKHOUSE", "market_factor_snapshot", "evidence_json", "", false),
                new FieldMapping("MarketDashboardVO.riskTips", "CLICKHOUSE", "market_factor_snapshot", "risk_json", "", false),
                new FieldMapping("EmotionStageSummaryVO.stageCode", "CLICKHOUSE", "market_factor_snapshot", "primary_stage", "", false),
                new FieldMapping("EmotionStageSummaryVO.stageName", "CLICKHOUSE", "market_factor_snapshot", "primary_stage_name", "", false),
                new FieldMapping("EmotionStageSummaryVO.confidence", "CLICKHOUSE", "market_factor_snapshot", "stage_confidence", "", false),
                new FieldMapping("EmotionStageSummaryVO.secondCandidateStage", "CLICKHOUSE", "market_factor_snapshot", "second_candidate_stage", "", false),
                new FieldMapping("EmotionStageSummaryVO.thirdCandidateStage", "CLICKHOUSE", "market_factor_snapshot", "third_candidate_stage", "", false),
                new FieldMapping("EmotionStageSummaryVO.stageText", "CLICKHOUSE", "market_factor_snapshot", "stage_text", "", false),
                new FieldMapping("RiskSummaryVO.riskLevel", "CLICKHOUSE", "market_factor_snapshot", "risk_level", "", false),
                new FieldMapping("RiskSummaryVO.riskScore", "CLICKHOUSE", "market_factor_snapshot", "risk_score", "", false),
                new FieldMapping("RiskSummaryVO.riskAction", "CLICKHOUSE", "market_factor_snapshot", "risk_action", "", false),
                new FieldMapping("RiskSummaryVO.oneVoteVetoTriggered", "CLICKHOUSE", "market_factor_snapshot", "one_vote_veto_triggered", "", false),
                new FieldMapping("RiskSummaryVO.riskText", "CLICKHOUSE", "market_factor_snapshot", "risk_json", "", false),
                new FieldMapping("MarketBreadthVO.riseCount", "CLICKHOUSE", "market_factor_snapshot", "rise_count", "", false),
                new FieldMapping("MarketBreadthVO.fallCount", "CLICKHOUSE", "market_factor_snapshot", "fall_count", "", false),
                new FieldMapping("MarketBreadthVO.flatCount", "CLICKHOUSE", "market_factor_snapshot", "flat_count", "", false),
                new FieldMapping("MarketBreadthVO.marketBreadthScore", "CLICKHOUSE", "market_factor_snapshot", "market_breadth_score", "", false),
                new FieldMapping("MarketBreadthVO.profitEffectScore", "CLICKHOUSE", "market_factor_snapshot", "profit_effect_score", "", false),
                new FieldMapping("MarketBreadthVO.lossEffectScore", "CLICKHOUSE", "market_factor_snapshot", "loss_effect_score", "", false),
                new FieldMapping("LimitUpDownEcoVO.limitUpCount", "CLICKHOUSE", "market_factor_snapshot", "limit_up_count", "", false),
                new FieldMapping("LimitUpDownEcoVO.limitDownCount", "CLICKHOUSE", "market_factor_snapshot", "limit_down_count", "", false),
                new FieldMapping("LimitUpDownEcoVO.breakBoardCount", "CLICKHOUSE", "market_factor_snapshot", "break_board_count", "", false),
                new FieldMapping("LimitUpDownEcoVO.breakBoardRate", "CLICKHOUSE", "market_factor_snapshot", "break_board_rate", "", false),
                new FieldMapping("LimitUpDownEcoVO.maxBoardHeight", "CLICKHOUSE", "market_factor_snapshot", "max_board_height", "", false),
                new FieldMapping("LimitUpDownEcoVO.promotionRate", "CLICKHOUSE", "market_factor_snapshot", "promotion_rate", "", false),
                new FieldMapping("TurnoverSummaryVO.totalTurnoverAmount", "CLICKHOUSE", "market_factor_snapshot", "total_turnover_amount", "", false),
                new FieldMapping("TurnoverSummaryVO.turnoverPercentile", "CLICKHOUSE", "market_factor_snapshot", "turnover_percentile", "", false),
                new FieldMapping("TurnoverSummaryVO.turnoverText", "CLICKHOUSE", "market_factor_snapshot", "turnover_text", "", false),
                new FieldMapping("HistoricalSimilaritySummaryVO.topMatchId", "CLICKHOUSE", "market_factor_snapshot", "top_match_id", "", false),
                new FieldMapping("HistoricalSimilaritySummaryVO.historicalTradeDate", "CLICKHOUSE", "market_factor_snapshot", "historical_trade_date", "", false),
                new FieldMapping("HistoricalSimilaritySummaryVO.similarityScore", "CLICKHOUSE", "market_factor_snapshot", "similarity_score", "", false),
                new FieldMapping("HistoricalSimilaritySummaryVO.matchType", "CLICKHOUSE", "market_factor_snapshot", "match_type", "", false),
                new FieldMapping("HistoricalSimilaritySummaryVO.summaryText", "CLICKHOUSE", "market_factor_snapshot", "evidence_json", "", false),
                new FieldMapping("MainlineBriefVO.mainlineId", "CLICKHOUSE", "market_factor_snapshot", "mainline_id", "", false),
                new FieldMapping("MainlineBriefVO.mainlineName", "CLICKHOUSE", "market_factor_snapshot", "mainline_name", "", false),
                new FieldMapping("MainlineBriefVO.lifecycleStage", "CLICKHOUSE", "market_factor_snapshot", "lifecycle_stage", "", false),
                new FieldMapping("MainlineBriefVO.strengthScore", "CLICKHOUSE", "market_factor_snapshot", "strength_score", "", false),
                new FieldMapping("MainlineBriefVO.leaderStockName", "CLICKHOUSE", "market_factor_snapshot", "leader_stock_name", "", false),
                new FieldMapping("LeaderBriefVO.stockCode", "CLICKHOUSE", "market_factor_snapshot", "stock_code", "", false),
                new FieldMapping("LeaderBriefVO.stockName", "CLICKHOUSE", "market_factor_snapshot", "stock_name", "", false),
                new FieldMapping("LeaderBriefVO.leaderType", "CLICKHOUSE", "market_factor_snapshot", "leader_type", "", false),
                new FieldMapping("LeaderBriefVO.leaderScore", "CLICKHOUSE", "market_factor_snapshot", "leader_score", "", false),
                new FieldMapping("LeaderBriefVO.riskVeto", "CLICKHOUSE", "market_factor_snapshot", "risk_veto", "", false),
                new FieldMapping("PatternSignalSummaryVO.totalSignalCount", "CLICKHOUSE", "market_factor_snapshot", "total_signal_count", "", false),
                new FieldMapping("PatternSignalSummaryVO.conditionMetCount", "CLICKHOUSE", "market_factor_snapshot", "condition_met_count", "", false),
                new FieldMapping("PatternSignalSummaryVO.riskVetoCount", "CLICKHOUSE", "market_factor_snapshot", "risk_veto_count", "", false),
                new FieldMapping("PatternSignalSummaryVO.invalidatedCount", "CLICKHOUSE", "market_factor_snapshot", "invalidated_count", "", false),
                new FieldMapping("RiskVetoSummaryVO.vetoCount", "CLICKHOUSE", "market_factor_snapshot", "veto_count", "", false),
                new FieldMapping("RiskVetoSummaryVO.majorVetoReason", "CLICKHOUSE", "market_factor_snapshot", "major_veto_reason", "", false),
                new FieldMapping("RiskVetoSummaryVO.vetoText", "CLICKHOUSE", "market_factor_snapshot", "veto_text", "", false)
        );
    }
}
