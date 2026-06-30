package com.astock.module.similarity.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.similarity.api.vo.HistoricalSimilarityPageVO;
import com.astock.module.similarity.application.query.HistoricalSimilarityPageQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class HistoricalSimilarityConverter implements PageBundleConverter<HistoricalSimilarityPageQuery, HistoricalSimilarityPageVO> {

    @Override
    public HistoricalSimilarityPageVO convert(HistoricalSimilarityPageQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        HistoricalSimilarityPageVO vo = new HistoricalSimilarityPageVO();

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
        vo.setCurrentProfile(toCurrentMarketProfileVO(bundle.firstRow("market_factor_snapshot"), bundle));
        vo.setOverview(toSimilarityOverviewVO(bundle.firstRow("market_factor_snapshot"), bundle));
        vo.setSingleDayMatches(bundle.rows("historical_similarity_match").stream().map(r -> toSimilarityMatchVO(r, bundle)).toList());
        vo.setThreeDayMatches(bundle.rows("historical_similarity_match").stream().map(r -> toSimilarityMatchVO(r, bundle)).toList());
        vo.setFiveDayMatches(bundle.rows("historical_similarity_match").stream().map(r -> toSimilarityMatchVO(r, bundle)).toList());
        vo.setTenDayMatches(bundle.rows("historical_similarity_match").stream().map(r -> toSimilarityMatchVO(r, bundle)).toList());
        vo.setScoreBreakdown(toSimilarityScoreBreakdownVO(bundle.firstRow("market_factor_snapshot"), bundle));
        vo.setSimilarFactors(bundle.rows("historical_similarity_match").stream().map(r -> toSimilarityFactorVO(r, bundle)).toList());
        vo.setDifferentFactors(bundle.rows("historical_similarity_match").stream().map(r -> toSimilarityFactorVO(r, bundle)).toList());
        vo.setFollowingPerformance(toFollowingPerformanceVO(bundle.firstRow("market_factor_snapshot"), bundle));
        vo.setPathComparison(bundle.rows("historical_similarity_match").stream().map(r -> toSimilarityPathPointVO(r, bundle)).toList());
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("market_factor_snapshot"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("market_factor_snapshot"), "risk_json"));
        vo.setBacktestEntry(toBacktestEntryVO(bundle.firstRow("market_factor_snapshot"), bundle));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "CurrentMarketProfileVO" -> "market_factor_snapshot";
            case "SimilarityOverviewVO" -> "historical_similarity_match";
            case "SimilarityMatchVO" -> "historical_similarity_match";
            case "SimilarityScoreBreakdownVO" -> "historical_similarity_match";
            case "SimilarityDimensionScoreVO" -> "historical_similarity_match";
            case "SimilarityFactorVO" -> "historical_similarity_match";
            case "FollowingPerformanceVO" -> "market_factor_snapshot";
            case "SimilarityPathPointVO" -> "historical_similarity_match";
            case "BacktestEntryVO" -> "market_factor_snapshot";
            case "HistoricalSimilarityPageVO" -> "historical_similarity_match";
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


    private HistoricalSimilarityPageVO.CurrentMarketProfileVO toCurrentMarketProfileVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalSimilarityPageVO.CurrentMarketProfileVO item = new HistoricalSimilarityPageVO.CurrentMarketProfileVO();
        item.setEmotionStage(MapFieldReader.string(row, "primary_stage"));
        item.setRiskScore(MapFieldReader.decimal(row, "risk_score"));
        item.setLimitUpCount(MapFieldReader.integer(row, "limit_up_count"));
        item.setLimitDownCount(MapFieldReader.integer(row, "limit_down_count"));
        item.setTurnoverAmount(MapFieldReader.decimal(row, "turnover_amount"));
        item.setStrongestMainline(MapFieldReader.string(row, "strongest_mainline"));
        return item;
    }

    private HistoricalSimilarityPageVO.SimilarityOverviewVO toSimilarityOverviewVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalSimilarityPageVO.SimilarityOverviewVO item = new HistoricalSimilarityPageVO.SimilarityOverviewVO();
        item.setMatchCount(MapFieldReader.integer(row, "match_count"));
        item.setTopSimilarityScore(MapFieldReader.decimal(row, "top_similarity_score"));
        item.setTopMatchType(MapFieldReader.string(row, "top_match_type"));
        item.setTopHistoricalDate(MapFieldReader.localDate(row, "top_historical_date"));
        item.setOverviewText(MapFieldReader.string(row, "features"));
        return item;
    }

    private HistoricalSimilarityPageVO.SimilarityMatchVO toSimilarityMatchVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalSimilarityPageVO.SimilarityMatchVO item = new HistoricalSimilarityPageVO.SimilarityMatchVO();
        item.setMatchId(MapFieldReader.longValue(row, "match_id"));
        item.setMatchType(MapFieldReader.string(row, "match_type"));
        item.setSampleId(MapFieldReader.longValue(row, "sample_id"));
        item.setHistoricalTradeDate(MapFieldReader.localDate(row, "historical_trade_date"));
        item.setHistoricalStage(MapFieldReader.string(row, "historical_stage"));
        item.setTotalSimilarityScore(MapFieldReader.decimal(row, "total_similarity_score"));
        item.setFuture1dReturn(MapFieldReader.decimal(row, "future_1d_return"));
        item.setFuture3dReturn(MapFieldReader.decimal(row, "future_3d_return"));
        item.setFuture5dReturn(MapFieldReader.decimal(row, "future_5d_return"));
        item.setMaxDrawdown(MapFieldReader.decimal(row, "max_drawdown"));
        item.setReferenceText(MapFieldReader.string(row, "reference_text"));
        item.setRiskText(MapFieldReader.string(row, "risk_json"));
        return item;
    }

    private HistoricalSimilarityPageVO.SimilarityScoreBreakdownVO toSimilarityScoreBreakdownVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalSimilarityPageVO.SimilarityScoreBreakdownVO item = new HistoricalSimilarityPageVO.SimilarityScoreBreakdownVO();
        item.setMarketEnvSimilarityScore(MapFieldReader.decimal(row, "market_env_similarity_score"));
        item.setEmotionCycleSimilarityScore(MapFieldReader.decimal(row, "emotion_cycle_similarity_score"));
        item.setThemeLeaderSimilarityScore(MapFieldReader.decimal(row, "theme_leader_similarity_score"));
        item.setDimensions(bundle.rows(tableFor("SimilarityDimensionScoreVO")).stream().map(r -> toSimilarityDimensionScoreVO(r, bundle)).toList());
        return item;
    }

    private HistoricalSimilarityPageVO.SimilarityDimensionScoreVO toSimilarityDimensionScoreVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalSimilarityPageVO.SimilarityDimensionScoreVO item = new HistoricalSimilarityPageVO.SimilarityDimensionScoreVO();
        item.setDimensionCode(MapFieldReader.string(row, "dimension_code"));
        item.setDimensionName(MapFieldReader.string(row, "dimension_name"));
        item.setScore(MapFieldReader.decimal(row, "score"));
        item.setWeight(MapFieldReader.decimal(row, "weight"));
        return item;
    }

    private HistoricalSimilarityPageVO.SimilarityFactorVO toSimilarityFactorVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalSimilarityPageVO.SimilarityFactorVO item = new HistoricalSimilarityPageVO.SimilarityFactorVO();
        item.setFactorCode(MapFieldReader.string(row, "factor_code"));
        item.setFactorName(MapFieldReader.string(row, "factor_name"));
        item.setCurrentValue(MapFieldReader.string(row, "current_value"));
        item.setHistoricalValue(MapFieldReader.string(row, "historical_value"));
        item.setSimilarityScore(MapFieldReader.decimal(row, "similarity_score"));
        item.setExplanation(MapFieldReader.string(row, "explanation"));
        return item;
    }

    private HistoricalSimilarityPageVO.FollowingPerformanceVO toFollowingPerformanceVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalSimilarityPageVO.FollowingPerformanceVO item = new HistoricalSimilarityPageVO.FollowingPerformanceVO();
        item.setFuture1dReturn(MapFieldReader.decimal(row, "future_1d_return"));
        item.setFuture3dReturn(MapFieldReader.decimal(row, "future_3d_return"));
        item.setFuture5dReturn(MapFieldReader.decimal(row, "future_5d_return"));
        item.setFuture10dReturn(MapFieldReader.decimal(row, "future_10d_return"));
        item.setMaxDrawdown(MapFieldReader.decimal(row, "max_drawdown"));
        item.setFollowingPathText(MapFieldReader.string(row, "following_path_text"));
        return item;
    }

    private HistoricalSimilarityPageVO.SimilarityPathPointVO toSimilarityPathPointVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalSimilarityPageVO.SimilarityPathPointVO item = new HistoricalSimilarityPageVO.SimilarityPathPointVO();
        item.setTradeDate(MapFieldReader.localDate(row, "trade_date"));
        item.setCurrentStage(MapFieldReader.string(row, "current_stage"));
        item.setHistoricalStage(MapFieldReader.string(row, "historical_stage"));
        item.setSimilarityScore(MapFieldReader.decimal(row, "similarity_score"));
        return item;
    }

    private HistoricalSimilarityPageVO.BacktestEntryVO toBacktestEntryVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                HistoricalSimilarityPageVO.BacktestEntryVO item = new HistoricalSimilarityPageVO.BacktestEntryVO();
        item.setAvailable(MapFieldReader.bool(row, "available"));
        item.setTaskId(MapFieldReader.longValue(row, "task_id"));
        item.setReportId(MapFieldReader.longValue(row, "report_id"));
        item.setTargetUrl(MapFieldReader.string(row, "target_url"));
        return item;
    }

}
