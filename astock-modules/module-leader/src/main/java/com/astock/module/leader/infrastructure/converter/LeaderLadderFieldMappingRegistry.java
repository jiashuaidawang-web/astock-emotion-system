package com.astock.module.leader.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeaderLadderFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_07_LEADER_LADDER";
    }

    @Override
    public String voClassName() {
        return "LeaderLadderPageVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("LeaderLadderPageVO.tradeDate", "CLICKHOUSE", "leader_daily_snapshot", "trade_date", "", false),
                new FieldMapping("LeaderLadderPageVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("LeaderLadderPageVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("LeaderLadderPageVO.overview", "CLICKHOUSE", "leader_daily_snapshot", "overview", "", false),
                new FieldMapping("LeaderLadderPageVO.marketLeaders", "CLICKHOUSE", "leader_daily_snapshot", "market_leaders", "", false),
                new FieldMapping("LeaderLadderPageVO.mainlineLeaders", "CLICKHOUSE", "leader_daily_snapshot", "mainline_leaders", "", false),
                new FieldMapping("LeaderLadderPageVO.sectorLeaders", "CLICKHOUSE", "leader_daily_snapshot", "sector_leaders", "", false),
                new FieldMapping("LeaderLadderPageVO.highBoardLeaders", "CLICKHOUSE", "leader_daily_snapshot", "high_board_leaders", "", false),
                new FieldMapping("LeaderLadderPageVO.trendLeaders", "CLICKHOUSE", "leader_daily_snapshot", "trend_leaders", "", false),
                new FieldMapping("LeaderLadderPageVO.compensationLeaders", "CLICKHOUSE", "leader_daily_snapshot", "compensation_leaders", "", false),
                new FieldMapping("LeaderLadderPageVO.switchLeaders", "CLICKHOUSE", "leader_daily_snapshot", "switch_leaders", "", false),
                new FieldMapping("LeaderLadderPageVO.followers", "CLICKHOUSE", "leader_daily_snapshot", "followers", "", false),
                new FieldMapping("LeaderLadderPageVO.consecutiveBoardLadders", "CLICKHOUSE", "leader_daily_snapshot", "consecutive_board_ladders", "", false),
                new FieldMapping("LeaderLadderPageVO.mainlineLadders", "CLICKHOUSE", "leader_daily_snapshot", "mainline_ladders", "", false),
                new FieldMapping("LeaderLadderPageVO.trendLadders", "CLICKHOUSE", "leader_daily_snapshot", "trend_ladders", "", false),
                new FieldMapping("LeaderLadderPageVO.driveRanks", "CLICKHOUSE", "leader_daily_snapshot", "drive_ranks", "", false),
                new FieldMapping("LeaderLadderPageVO.negativeFeedbacks", "CLICKHOUSE", "leader_daily_snapshot", "negative_feedbacks", "", false),
                new FieldMapping("LeaderLadderPageVO.conclusion", "CLICKHOUSE", "leader_daily_snapshot", "evidence_json", "", false),
                new FieldMapping("LeaderLadderPageVO.riskTips", "CLICKHOUSE", "leader_daily_snapshot", "risk_json", "", false),
                new FieldMapping("LeaderLadderOverviewVO.hasMarketLeader", "CLICKHOUSE", "leader_daily_snapshot", "has_market_leader", "", false),
                new FieldMapping("LeaderLadderOverviewVO.marketLeaderStockCode", "CLICKHOUSE", "leader_daily_snapshot", "market_leader_stock_code", "", false),
                new FieldMapping("LeaderLadderOverviewVO.marketLeaderStockName", "CLICKHOUSE", "leader_daily_snapshot", "market_leader_stock_name", "", false),
                new FieldMapping("LeaderLadderOverviewVO.marketLeaderScore", "CLICKHOUSE", "leader_daily_snapshot", "market_leader_score", "", false),
                new FieldMapping("LeaderLadderOverviewVO.mainlineLeaderCount", "CLICKHOUSE", "leader_daily_snapshot", "mainline_leader_count", "", false),
                new FieldMapping("LeaderLadderOverviewVO.highBoardLeaderCount", "CLICKHOUSE", "leader_daily_snapshot", "high_board_leader_count", "", false),
                new FieldMapping("LeaderLadderOverviewVO.trendLeaderCount", "CLICKHOUSE", "leader_daily_snapshot", "trend_leader_count", "", false),
                new FieldMapping("LeaderLadderOverviewVO.followerCount", "CLICKHOUSE", "leader_daily_snapshot", "follower_count", "", false),
                new FieldMapping("LeaderLadderOverviewVO.maxBoardHeight", "CLICKHOUSE", "leader_daily_snapshot", "max_board_height", "", false),
                new FieldMapping("LeaderLadderOverviewVO.overallDriveScore", "CLICKHOUSE", "leader_daily_snapshot", "overall_drive_score", "", false),
                new FieldMapping("LeaderLadderOverviewVO.overallNegativeFeedbackScore", "CLICKHOUSE", "leader_daily_snapshot", "overall_negative_feedback_score", "", false),
                new FieldMapping("LeaderLadderOverviewVO.overviewText", "CLICKHOUSE", "leader_daily_snapshot", "features", "", false),
                new FieldMapping("LeaderCardVO.stockCode", "CLICKHOUSE", "leader_daily_snapshot", "stock_code", "", false),
                new FieldMapping("LeaderCardVO.stockName", "CLICKHOUSE", "leader_daily_snapshot", "stock_name", "", false),
                new FieldMapping("LeaderCardVO.leaderType", "CLICKHOUSE", "leader_daily_snapshot", "leader_type", "", false),
                new FieldMapping("LeaderCardVO.leaderStatus", "CLICKHOUSE", "leader_daily_snapshot", "leader_status", "", false),
                new FieldMapping("LeaderCardVO.mainlineId", "CLICKHOUSE", "leader_daily_snapshot", "mainline_id", "", false),
                new FieldMapping("LeaderCardVO.mainlineName", "CLICKHOUSE", "leader_daily_snapshot", "mainline_name", "", false),
                new FieldMapping("LeaderCardVO.sectorName", "CLICKHOUSE", "leader_daily_snapshot", "sector_name", "", false),
                new FieldMapping("LeaderCardVO.leaderScore", "CLICKHOUSE", "leader_daily_snapshot", "leader_score", "", false),
                new FieldMapping("LeaderCardVO.rankNo", "CLICKHOUSE", "leader_daily_snapshot", "rank_no", "", false),
                new FieldMapping("LeaderCardVO.driveScore", "CLICKHOUSE", "leader_daily_snapshot", "drive_score", "", false),
                new FieldMapping("LeaderCardVO.negativeFeedbackScore", "CLICKHOUSE", "leader_daily_snapshot", "negative_feedback_score", "", false),
                new FieldMapping("LeaderCardVO.consecutiveBoardHeight", "CLICKHOUSE", "leader_daily_snapshot", "consecutive_board_height", "", false),
                new FieldMapping("LeaderCardVO.limitUp", "CLICKHOUSE", "leader_daily_snapshot", "limit_up", "", false),
                new FieldMapping("LeaderCardVO.brokenBoard", "CLICKHOUSE", "leader_daily_snapshot", "broken_board", "", false),
                new FieldMapping("LeaderCardVO.inPatternWatchPool", "CLICKHOUSE", "leader_daily_snapshot", "in_pattern_watch_pool", "", false),
                new FieldMapping("LeaderCardVO.riskVeto", "CLICKHOUSE", "leader_daily_snapshot", "risk_veto", "", false),
                new FieldMapping("LeaderCardVO.leaderSummary", "CLICKHOUSE", "leader_daily_snapshot", "leader_summary", "", false),
                new FieldMapping("LeaderCardVO.riskSummary", "CLICKHOUSE", "leader_daily_snapshot", "risk_summary", "", false),
                new FieldMapping("ConsecutiveBoardLadderVO.boardHeight", "CLICKHOUSE", "leader_daily_snapshot", "board_height", "", false),
                new FieldMapping("ConsecutiveBoardLadderVO.stockCount", "CLICKHOUSE", "leader_daily_snapshot", "stock_count", "", false),
                new FieldMapping("ConsecutiveBoardLadderVO.stocks", "CLICKHOUSE", "leader_daily_snapshot", "stocks", "", false),
                new FieldMapping("ConsecutiveBoardLadderVO.ladderText", "CLICKHOUSE", "leader_daily_snapshot", "ladder_text", "", false),
                new FieldMapping("MainlineLeaderLadderVO.mainlineId", "CLICKHOUSE", "leader_daily_snapshot", "mainline_id", "", false),
                new FieldMapping("MainlineLeaderLadderVO.mainlineName", "CLICKHOUSE", "leader_daily_snapshot", "mainline_name", "", false),
                new FieldMapping("MainlineLeaderLadderVO.leaders", "CLICKHOUSE", "leader_daily_snapshot", "leaders", "", false),
                new FieldMapping("TrendLeaderLadderVO.trendType", "CLICKHOUSE", "leader_daily_snapshot", "trend_type", "", false),
                new FieldMapping("TrendLeaderLadderVO.leaders", "CLICKHOUSE", "leader_daily_snapshot", "leaders", "", false),
                new FieldMapping("LeaderDriveRankVO.stockCode", "CLICKHOUSE", "leader_daily_snapshot", "stock_code", "", false),
                new FieldMapping("LeaderDriveRankVO.stockName", "CLICKHOUSE", "leader_daily_snapshot", "stock_name", "", false),
                new FieldMapping("LeaderDriveRankVO.driveScore", "CLICKHOUSE", "leader_daily_snapshot", "drive_score", "", false),
                new FieldMapping("LeaderDriveRankVO.driveText", "CLICKHOUSE", "leader_daily_snapshot", "drive_text", "", false),
                new FieldMapping("LeaderNegativeFeedbackVO.stockCode", "CLICKHOUSE", "leader_daily_snapshot", "stock_code", "", false),
                new FieldMapping("LeaderNegativeFeedbackVO.stockName", "CLICKHOUSE", "leader_daily_snapshot", "stock_name", "", false),
                new FieldMapping("LeaderNegativeFeedbackVO.negativeFeedbackScore", "CLICKHOUSE", "leader_daily_snapshot", "negative_feedback_score", "", false),
                new FieldMapping("LeaderNegativeFeedbackVO.triggerRiskControl", "CLICKHOUSE", "leader_daily_snapshot", "trigger_risk_control", "", false),
                new FieldMapping("LeaderNegativeFeedbackVO.feedbackText", "CLICKHOUSE", "leader_daily_snapshot", "feedback_text", "", false)
        );
    }
}
