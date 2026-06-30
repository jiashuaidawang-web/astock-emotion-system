package com.astock.module.market.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.market.api.vo.MarketDashboardVO;
import com.astock.module.market.application.query.MarketDashboardQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MarketDashboardConverter implements PageBundleConverter<MarketDashboardQuery, MarketDashboardVO> {

    @Override
    public MarketDashboardVO convert(MarketDashboardQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        MarketDashboardVO vo = new MarketDashboardVO();

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
        vo.setEmotionStage(toEmotionStageSummaryVO(bundle.firstRow("emotion_stage_snapshot"), bundle));
        vo.setRiskSummary(toRiskSummaryVO(bundle.firstRow("pattern_risk_veto_snapshot"), bundle));
        vo.setMarketBreadth(toMarketBreadthVO(bundle.firstRow("market_factor_snapshot"), bundle));
        vo.setLimitUpDownEco(toLimitUpDownEcoVO(bundle.firstRow("limit_up_down_ecology_snapshot"), bundle));
        vo.setTurnoverSummary(toTurnoverSummaryVO(bundle.firstRow("market_factor_snapshot"), bundle));
        vo.setSimilaritySummary(toHistoricalSimilaritySummaryVO(bundle.firstRow("historical_similarity_match"), bundle));
        vo.setTopMainlines(bundle.rows("mainline_daily_snapshot").stream().map(r -> toMainlineBriefVO(r, bundle)).toList());
        vo.setLeaderWatchList(bundle.rows("leader_daily_snapshot").stream().map(r -> toLeaderBriefVO(r, bundle)).toList());
        vo.setPatternSignalSummary(toPatternSignalSummaryVO(bundle.firstRow("buy_pattern_signal_snapshot"), bundle));
        vo.setRiskVetoSummary(toRiskVetoSummaryVO(bundle.firstRow("pattern_risk_veto_snapshot"), bundle));
        vo.setSummaryText(MapFieldReader.string(bundle.firstRow("market_factor_snapshot"), "evidence_json"));
        vo.setKeyEvidences(stringList(bundle.firstRow("market_factor_snapshot"), "key_evidences"));
        vo.setKeyRisks(stringList(bundle.firstRow("pattern_risk_veto_snapshot"), "key_risks"));
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("market_factor_snapshot"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("pattern_risk_veto_snapshot"), "risk_json"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "EmotionStageSummaryVO" -> "emotion_stage_snapshot";
            case "RiskSummaryVO" -> "pattern_risk_veto_snapshot";
            case "MarketBreadthVO" -> "market_factor_snapshot";
            case "LimitUpDownEcoVO" -> "limit_up_down_ecology_snapshot";
            case "TurnoverSummaryVO" -> "market_factor_snapshot";
            case "HistoricalSimilaritySummaryVO" -> "historical_similarity_match";
            case "MainlineBriefVO" -> "mainline_daily_snapshot";
            case "LeaderBriefVO" -> "leader_daily_snapshot";
            case "PatternSignalSummaryVO" -> "buy_pattern_signal_snapshot";
            case "RiskVetoSummaryVO" -> "pattern_risk_veto_snapshot";
            case "MarketDashboardVO" -> "market_factor_snapshot";
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


    private MarketDashboardVO.EmotionStageSummaryVO toEmotionStageSummaryVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MarketDashboardVO.EmotionStageSummaryVO item = new MarketDashboardVO.EmotionStageSummaryVO();
        item.setStageCode(MapFieldReader.string(row, "primary_stage"));
        item.setStageName(MapFieldReader.string(row, "primary_stage_name"));
        item.setConfidence(MapFieldReader.decimal(row, "stage_confidence"));
        item.setSecondCandidateStage(MapFieldReader.string(row, "second_candidate_stage"));
        item.setThirdCandidateStage(MapFieldReader.string(row, "third_candidate_stage"));
        item.setStageText(MapFieldReader.string(row, "stage_text"));
        return item;
    }

    private MarketDashboardVO.RiskSummaryVO toRiskSummaryVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MarketDashboardVO.RiskSummaryVO item = new MarketDashboardVO.RiskSummaryVO();
        item.setRiskLevel(MapFieldReader.string(row, "risk_level"));
        item.setRiskScore(MapFieldReader.decimal(row, "risk_score"));
        item.setRiskAction(MapFieldReader.string(row, "risk_action"));
        item.setOneVoteVetoTriggered(MapFieldReader.bool(row, "one_vote_veto_triggered"));
        item.setRiskText(MapFieldReader.string(row, "risk_json"));
        return item;
    }

    private MarketDashboardVO.MarketBreadthVO toMarketBreadthVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MarketDashboardVO.MarketBreadthVO item = new MarketDashboardVO.MarketBreadthVO();
        item.setRiseCount(MapFieldReader.integer(row, "rise_count"));
        item.setFallCount(MapFieldReader.integer(row, "fall_count"));
        item.setFlatCount(MapFieldReader.integer(row, "flat_count"));
        item.setMarketBreadthScore(MapFieldReader.decimal(row, "market_breadth_score"));
        item.setProfitEffectScore(MapFieldReader.decimal(row, "profit_effect_score"));
        item.setLossEffectScore(MapFieldReader.decimal(row, "loss_effect_score"));
        return item;
    }

    private MarketDashboardVO.LimitUpDownEcoVO toLimitUpDownEcoVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MarketDashboardVO.LimitUpDownEcoVO item = new MarketDashboardVO.LimitUpDownEcoVO();
        item.setLimitUpCount(MapFieldReader.integer(row, "limit_up_count"));
        item.setLimitDownCount(MapFieldReader.integer(row, "limit_down_count"));
        item.setBreakBoardCount(MapFieldReader.integer(row, "break_board_count"));
        item.setBreakBoardRate(MapFieldReader.decimal(row, "break_board_rate"));
        item.setMaxBoardHeight(MapFieldReader.integer(row, "max_board_height"));
        item.setPromotionRate(MapFieldReader.decimal(row, "promotion_rate"));
        return item;
    }

    private MarketDashboardVO.TurnoverSummaryVO toTurnoverSummaryVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MarketDashboardVO.TurnoverSummaryVO item = new MarketDashboardVO.TurnoverSummaryVO();
        item.setTotalTurnoverAmount(MapFieldReader.decimal(row, "total_turnover_amount"));
        item.setTurnoverPercentile(MapFieldReader.decimal(row, "turnover_percentile"));
        item.setTurnoverText(MapFieldReader.string(row, "turnover_text"));
        return item;
    }

    private MarketDashboardVO.HistoricalSimilaritySummaryVO toHistoricalSimilaritySummaryVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MarketDashboardVO.HistoricalSimilaritySummaryVO item = new MarketDashboardVO.HistoricalSimilaritySummaryVO();
        item.setTopMatchId(MapFieldReader.longValue(row, "top_match_id"));
        item.setHistoricalTradeDate(MapFieldReader.localDate(row, "historical_trade_date"));
        item.setSimilarityScore(MapFieldReader.decimal(row, "similarity_score"));
        item.setMatchType(MapFieldReader.string(row, "match_type"));
        item.setSummaryText(MapFieldReader.string(row, "evidence_json"));
        return item;
    }

    private MarketDashboardVO.MainlineBriefVO toMainlineBriefVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MarketDashboardVO.MainlineBriefVO item = new MarketDashboardVO.MainlineBriefVO();
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setMainlineName(MapFieldReader.string(row, "mainline_name"));
        item.setLifecycleStage(MapFieldReader.string(row, "lifecycle_stage"));
        item.setStrengthScore(MapFieldReader.decimal(row, "mainline_strength_score"));
        item.setLeaderStockName(MapFieldReader.string(row, "leader_stock_name"));
        return item;
    }

    private MarketDashboardVO.LeaderBriefVO toLeaderBriefVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MarketDashboardVO.LeaderBriefVO item = new MarketDashboardVO.LeaderBriefVO();
        item.setStockCode(MapFieldReader.string(row, "stock_code"));
        item.setStockName(MapFieldReader.string(row, "stock_name"));
        item.setLeaderType(MapFieldReader.string(row, "leader_type"));
        item.setLeaderScore(MapFieldReader.decimal(row, "leader_score"));
        item.setRiskVeto(MapFieldReader.bool(row, "risk_veto"));
        return item;
    }

    private MarketDashboardVO.PatternSignalSummaryVO toPatternSignalSummaryVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MarketDashboardVO.PatternSignalSummaryVO item = new MarketDashboardVO.PatternSignalSummaryVO();
        item.setTotalSignalCount(MapFieldReader.integer(row, "total_signal_count"));
        item.setConditionMetCount(MapFieldReader.integer(row, "condition_met_count"));
        item.setRiskVetoCount(MapFieldReader.integer(row, "risk_veto_count"));
        item.setInvalidatedCount(MapFieldReader.integer(row, "invalidated_count"));
        return item;
    }

    private MarketDashboardVO.RiskVetoSummaryVO toRiskVetoSummaryVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MarketDashboardVO.RiskVetoSummaryVO item = new MarketDashboardVO.RiskVetoSummaryVO();
        item.setVetoCount(MapFieldReader.integer(row, "veto_count"));
        item.setMajorVetoReason(MapFieldReader.string(row, "major_veto_reason"));
        item.setVetoText(MapFieldReader.string(row, "veto_text"));
        return item;
    }

}
