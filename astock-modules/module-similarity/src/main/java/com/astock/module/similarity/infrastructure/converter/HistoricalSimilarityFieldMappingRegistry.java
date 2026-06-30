package com.astock.module.similarity.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HistoricalSimilarityFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_02_HISTORICAL_SIMILARITY";
    }

    @Override
    public String voClassName() {
        return "HistoricalSimilarityPageVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("HistoricalSimilarityPageVO.tradeDate", "CLICKHOUSE", "historical_similarity_match", "trade_date", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("HistoricalSimilarityPageVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("HistoricalSimilarityPageVO.currentProfile", "CLICKHOUSE", "historical_similarity_match", "current_profile", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.overview", "CLICKHOUSE", "historical_similarity_match", "overview", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.singleDayMatches", "CLICKHOUSE", "historical_similarity_match", "single_day_matches", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.threeDayMatches", "CLICKHOUSE", "historical_similarity_match", "three_day_matches", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.fiveDayMatches", "CLICKHOUSE", "historical_similarity_match", "five_day_matches", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.tenDayMatches", "CLICKHOUSE", "historical_similarity_match", "ten_day_matches", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.scoreBreakdown", "CLICKHOUSE", "historical_similarity_match", "score_breakdown", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.similarFactors", "CLICKHOUSE", "historical_similarity_match", "similar_factors", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.differentFactors", "CLICKHOUSE", "historical_similarity_match", "different_factors", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.followingPerformance", "CLICKHOUSE", "historical_similarity_match", "following_performance", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.pathComparison", "CLICKHOUSE", "historical_similarity_match", "path_comparison", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.conclusion", "CLICKHOUSE", "historical_similarity_match", "evidence_json", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.riskTips", "CLICKHOUSE", "historical_similarity_match", "risk_json", "", false),
                new FieldMapping("HistoricalSimilarityPageVO.backtestEntry", "CLICKHOUSE", "historical_similarity_match", "backtest_entry", "", false),
                new FieldMapping("CurrentMarketProfileVO.emotionStage", "CLICKHOUSE", "historical_similarity_match", "primary_stage", "", false),
                new FieldMapping("CurrentMarketProfileVO.riskScore", "CLICKHOUSE", "historical_similarity_match", "risk_score", "", false),
                new FieldMapping("CurrentMarketProfileVO.limitUpCount", "CLICKHOUSE", "historical_similarity_match", "limit_up_count", "", false),
                new FieldMapping("CurrentMarketProfileVO.limitDownCount", "CLICKHOUSE", "historical_similarity_match", "limit_down_count", "", false),
                new FieldMapping("CurrentMarketProfileVO.turnoverAmount", "CLICKHOUSE", "historical_similarity_match", "turnover_amount", "", false),
                new FieldMapping("CurrentMarketProfileVO.strongestMainline", "CLICKHOUSE", "historical_similarity_match", "strongest_mainline", "", false),
                new FieldMapping("SimilarityOverviewVO.matchCount", "CLICKHOUSE", "historical_similarity_match", "match_count", "", false),
                new FieldMapping("SimilarityOverviewVO.topSimilarityScore", "CLICKHOUSE", "historical_similarity_match", "top_similarity_score", "", false),
                new FieldMapping("SimilarityOverviewVO.topMatchType", "CLICKHOUSE", "historical_similarity_match", "top_match_type", "", false),
                new FieldMapping("SimilarityOverviewVO.topHistoricalDate", "CLICKHOUSE", "historical_similarity_match", "top_historical_date", "", false),
                new FieldMapping("SimilarityOverviewVO.overviewText", "CLICKHOUSE", "historical_similarity_match", "features", "", false),
                new FieldMapping("SimilarityMatchVO.matchId", "CLICKHOUSE", "historical_similarity_match", "match_id", "", false),
                new FieldMapping("SimilarityMatchVO.matchType", "CLICKHOUSE", "historical_similarity_match", "match_type", "", false),
                new FieldMapping("SimilarityMatchVO.sampleId", "CLICKHOUSE", "historical_similarity_match", "sample_id", "", false),
                new FieldMapping("SimilarityMatchVO.historicalTradeDate", "CLICKHOUSE", "historical_similarity_match", "historical_trade_date", "", false),
                new FieldMapping("SimilarityMatchVO.historicalStage", "CLICKHOUSE", "historical_similarity_match", "historical_stage", "", false),
                new FieldMapping("SimilarityMatchVO.totalSimilarityScore", "CLICKHOUSE", "historical_similarity_match", "total_similarity_score", "", false),
                new FieldMapping("SimilarityMatchVO.future1dReturn", "CLICKHOUSE", "historical_similarity_match", "future_1d_return", "", false),
                new FieldMapping("SimilarityMatchVO.future3dReturn", "CLICKHOUSE", "historical_similarity_match", "future_3d_return", "", false),
                new FieldMapping("SimilarityMatchVO.future5dReturn", "CLICKHOUSE", "historical_similarity_match", "future_5d_return", "", false),
                new FieldMapping("SimilarityMatchVO.maxDrawdown", "CLICKHOUSE", "historical_similarity_match", "max_drawdown", "", false),
                new FieldMapping("SimilarityMatchVO.referenceText", "CLICKHOUSE", "historical_similarity_match", "reference_text", "", false),
                new FieldMapping("SimilarityMatchVO.riskText", "CLICKHOUSE", "historical_similarity_match", "risk_json", "", false),
                new FieldMapping("SimilarityScoreBreakdownVO.marketEnvSimilarityScore", "CLICKHOUSE", "historical_similarity_match", "market_env_similarity_score", "", false),
                new FieldMapping("SimilarityScoreBreakdownVO.emotionCycleSimilarityScore", "CLICKHOUSE", "historical_similarity_match", "emotion_cycle_similarity_score", "", false),
                new FieldMapping("SimilarityScoreBreakdownVO.themeLeaderSimilarityScore", "CLICKHOUSE", "historical_similarity_match", "theme_leader_similarity_score", "", false),
                new FieldMapping("SimilarityScoreBreakdownVO.dimensions", "CLICKHOUSE", "historical_similarity_match", "dimensions", "", false),
                new FieldMapping("SimilarityDimensionScoreVO.dimensionCode", "CLICKHOUSE", "historical_similarity_match", "dimension_code", "", false),
                new FieldMapping("SimilarityDimensionScoreVO.dimensionName", "CLICKHOUSE", "historical_similarity_match", "dimension_name", "", false),
                new FieldMapping("SimilarityDimensionScoreVO.score", "CLICKHOUSE", "historical_similarity_match", "score", "", false),
                new FieldMapping("SimilarityDimensionScoreVO.weight", "CLICKHOUSE", "historical_similarity_match", "weight", "", false),
                new FieldMapping("SimilarityFactorVO.factorCode", "CLICKHOUSE", "historical_similarity_match", "factor_code", "", false),
                new FieldMapping("SimilarityFactorVO.factorName", "CLICKHOUSE", "historical_similarity_match", "factor_name", "", false),
                new FieldMapping("SimilarityFactorVO.currentValue", "CLICKHOUSE", "historical_similarity_match", "current_value", "", false),
                new FieldMapping("SimilarityFactorVO.historicalValue", "CLICKHOUSE", "historical_similarity_match", "historical_value", "", false),
                new FieldMapping("SimilarityFactorVO.similarityScore", "CLICKHOUSE", "historical_similarity_match", "similarity_score", "", false),
                new FieldMapping("SimilarityFactorVO.explanation", "CLICKHOUSE", "historical_similarity_match", "explanation", "", false),
                new FieldMapping("FollowingPerformanceVO.future1dReturn", "CLICKHOUSE", "historical_similarity_match", "future_1d_return", "", false),
                new FieldMapping("FollowingPerformanceVO.future3dReturn", "CLICKHOUSE", "historical_similarity_match", "future_3d_return", "", false),
                new FieldMapping("FollowingPerformanceVO.future5dReturn", "CLICKHOUSE", "historical_similarity_match", "future_5d_return", "", false),
                new FieldMapping("FollowingPerformanceVO.future10dReturn", "CLICKHOUSE", "historical_similarity_match", "future_10d_return", "", false),
                new FieldMapping("FollowingPerformanceVO.maxDrawdown", "CLICKHOUSE", "historical_similarity_match", "max_drawdown", "", false),
                new FieldMapping("FollowingPerformanceVO.followingPathText", "CLICKHOUSE", "historical_similarity_match", "following_path_text", "", false),
                new FieldMapping("SimilarityPathPointVO.tradeDate", "CLICKHOUSE", "historical_similarity_match", "trade_date", "", false),
                new FieldMapping("SimilarityPathPointVO.currentStage", "CLICKHOUSE", "historical_similarity_match", "current_stage", "", false),
                new FieldMapping("SimilarityPathPointVO.historicalStage", "CLICKHOUSE", "historical_similarity_match", "historical_stage", "", false),
                new FieldMapping("SimilarityPathPointVO.similarityScore", "CLICKHOUSE", "historical_similarity_match", "similarity_score", "", false),
                new FieldMapping("BacktestEntryVO.available", "CLICKHOUSE", "historical_similarity_match", "available", "", false),
                new FieldMapping("BacktestEntryVO.taskId", "CLICKHOUSE", "historical_similarity_match", "task_id", "", false),
                new FieldMapping("BacktestEntryVO.reportId", "CLICKHOUSE", "historical_similarity_match", "report_id", "", false),
                new FieldMapping("BacktestEntryVO.targetUrl", "CLICKHOUSE", "historical_similarity_match", "target_url", "", false)
        );
    }
}
