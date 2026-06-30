package com.astock.module.mainline.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainlineRadarFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_05_MAINLINE_RADAR";
    }

    @Override
    public String voClassName() {
        return "MainlineRadarPageVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("MainlineRadarPageVO.tradeDate", "CLICKHOUSE", "mainline_daily_snapshot", "trade_date", "", false),
                new FieldMapping("MainlineRadarPageVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("MainlineRadarPageVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("MainlineRadarPageVO.overview", "CLICKHOUSE", "mainline_daily_snapshot", "overview", "", false),
                new FieldMapping("MainlineRadarPageVO.mainlineRanks", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_ranks", "", false),
                new FieldMapping("MainlineRadarPageVO.lifecycleList", "CLICKHOUSE", "mainline_daily_snapshot", "lifecycle_list", "", false),
                new FieldMapping("MainlineRadarPageVO.competition", "CLICKHOUSE", "mainline_daily_snapshot", "competition", "", false),
                new FieldMapping("MainlineRadarPageVO.switchSignals", "CLICKHOUSE", "mainline_daily_snapshot", "switch_signals", "", false),
                new FieldMapping("MainlineRadarPageVO.scoreBreakdowns", "CLICKHOUSE", "mainline_daily_snapshot", "score_breakdowns", "", false),
                new FieldMapping("MainlineRadarPageVO.ladders", "CLICKHOUSE", "mainline_daily_snapshot", "ladders", "", false),
                new FieldMapping("MainlineRadarPageVO.leaders", "CLICKHOUSE", "mainline_daily_snapshot", "leaders", "", false),
                new FieldMapping("MainlineRadarPageVO.risks", "CLICKHOUSE", "mainline_daily_snapshot", "risks", "", false),
                new FieldMapping("MainlineRadarPageVO.historicalSamples", "CLICKHOUSE", "mainline_daily_snapshot", "historical_samples", "", false),
                new FieldMapping("MainlineRadarPageVO.conclusion", "CLICKHOUSE", "mainline_daily_snapshot", "evidence_json", "", false),
                new FieldMapping("MainlineRadarPageVO.riskTips", "CLICKHOUSE", "mainline_daily_snapshot", "risk_json", "", false),
                new FieldMapping("MainlineOverviewVO.mainlineCount", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_count", "", false),
                new FieldMapping("MainlineOverviewVO.confirmedCount", "CLICKHOUSE", "mainline_daily_snapshot", "confirmed_count", "", false),
                new FieldMapping("MainlineOverviewVO.candidateCount", "CLICKHOUSE", "mainline_daily_snapshot", "candidate_count", "", false),
                new FieldMapping("MainlineOverviewVO.strongestMainlineName", "CLICKHOUSE", "mainline_daily_snapshot", "strongest_mainline_name", "", false),
                new FieldMapping("MainlineOverviewVO.strongestScore", "CLICKHOUSE", "mainline_daily_snapshot", "strongest_score", "", false),
                new FieldMapping("MainlineOverviewVO.overviewText", "CLICKHOUSE", "mainline_daily_snapshot", "features", "", false),
                new FieldMapping("MainlineRankVO.mainlineId", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_id", "", false),
                new FieldMapping("MainlineRankVO.mainlineName", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_name", "", false),
                new FieldMapping("MainlineRankVO.mainlineStatus", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_status", "", false),
                new FieldMapping("MainlineRankVO.lifecycleStage", "CLICKHOUSE", "mainline_daily_snapshot", "lifecycle_stage", "", false),
                new FieldMapping("MainlineRankVO.themeRole", "CLICKHOUSE", "mainline_daily_snapshot", "theme_role", "", false),
                new FieldMapping("MainlineRankVO.mainlineStrengthScore", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_strength_score", "", false),
                new FieldMapping("MainlineRankVO.rankNo", "CLICKHOUSE", "mainline_daily_snapshot", "rank_no", "", false),
                new FieldMapping("MainlineRankVO.leaderStockName", "CLICKHOUSE", "mainline_daily_snapshot", "leader_stock_name", "", false),
                new FieldMapping("MainlineLifecycleVO.mainlineId", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_id", "", false),
                new FieldMapping("MainlineLifecycleVO.mainlineName", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_name", "", false),
                new FieldMapping("MainlineLifecycleVO.lifecycleStage", "CLICKHOUSE", "mainline_daily_snapshot", "lifecycle_stage", "", false),
                new FieldMapping("MainlineLifecycleVO.lifecycleText", "CLICKHOUSE", "mainline_daily_snapshot", "lifecycle_text", "", false),
                new FieldMapping("MainlineCompetitionVO.competitionStatus", "CLICKHOUSE", "mainline_daily_snapshot", "competition_status", "", false),
                new FieldMapping("MainlineCompetitionVO.items", "CLICKHOUSE", "mainline_daily_snapshot", "items", "", false),
                new FieldMapping("MainlineCompetitionVO.competitionText", "CLICKHOUSE", "mainline_daily_snapshot", "competition_text", "", false),
                new FieldMapping("MainlineCompetitionItemVO.mainlineId", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_id", "", false),
                new FieldMapping("MainlineCompetitionItemVO.mainlineName", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_name", "", false),
                new FieldMapping("MainlineCompetitionItemVO.strengthScore", "CLICKHOUSE", "mainline_daily_snapshot", "strength_score", "", false),
                new FieldMapping("MainlineCompetitionItemVO.role", "CLICKHOUSE", "mainline_daily_snapshot", "role", "", false),
                new FieldMapping("MainlineSwitchVO.oldMainlineId", "CLICKHOUSE", "mainline_daily_snapshot", "old_mainline_id", "", false),
                new FieldMapping("MainlineSwitchVO.oldMainlineName", "CLICKHOUSE", "mainline_daily_snapshot", "old_mainline_name", "", false),
                new FieldMapping("MainlineSwitchVO.newMainlineId", "CLICKHOUSE", "mainline_daily_snapshot", "new_mainline_id", "", false),
                new FieldMapping("MainlineSwitchVO.newMainlineName", "CLICKHOUSE", "mainline_daily_snapshot", "new_mainline_name", "", false),
                new FieldMapping("MainlineSwitchVO.switchStatus", "CLICKHOUSE", "mainline_daily_snapshot", "switch_status", "", false),
                new FieldMapping("MainlineSwitchVO.switchScore", "CLICKHOUSE", "mainline_daily_snapshot", "switch_score", "", false),
                new FieldMapping("MainlineSwitchVO.riskText", "CLICKHOUSE", "mainline_daily_snapshot", "risk_json", "", false),
                new FieldMapping("MainlineScoreBreakdownVO.mainlineId", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_id", "", false),
                new FieldMapping("MainlineScoreBreakdownVO.limitUpClusterScore", "CLICKHOUSE", "mainline_daily_snapshot", "limit_up_cluster_score", "", false),
                new FieldMapping("MainlineScoreBreakdownVO.turnoverConcentrationScore", "CLICKHOUSE", "mainline_daily_snapshot", "turnover_concentration_score", "", false),
                new FieldMapping("MainlineScoreBreakdownVO.continuityScore", "CLICKHOUSE", "mainline_daily_snapshot", "continuity_score", "", false),
                new FieldMapping("MainlineScoreBreakdownVO.ladderIntegrityScore", "CLICKHOUSE", "mainline_daily_snapshot", "ladder_integrity_score", "", false),
                new FieldMapping("MainlineScoreBreakdownVO.leaderDriveScore", "CLICKHOUSE", "mainline_daily_snapshot", "leader_drive_score", "", false),
                new FieldMapping("MainlineScoreBreakdownVO.emotionMatchScore", "CLICKHOUSE", "mainline_daily_snapshot", "emotion_match_score", "", false),
                new FieldMapping("MainlineLadderVO.mainlineId", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_id", "", false),
                new FieldMapping("MainlineLadderVO.mainlineName", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_name", "", false),
                new FieldMapping("MainlineLadderVO.maxBoardHeight", "CLICKHOUSE", "mainline_daily_snapshot", "max_board_height", "", false),
                new FieldMapping("MainlineLadderVO.limitUpCount", "CLICKHOUSE", "mainline_daily_snapshot", "limit_up_count", "", false),
                new FieldMapping("MainlineLadderVO.ladderText", "CLICKHOUSE", "mainline_daily_snapshot", "ladder_text", "", false),
                new FieldMapping("MainlineLeaderVO.mainlineId", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_id", "", false),
                new FieldMapping("MainlineLeaderVO.stockCode", "CLICKHOUSE", "mainline_daily_snapshot", "stock_code", "", false),
                new FieldMapping("MainlineLeaderVO.stockName", "CLICKHOUSE", "mainline_daily_snapshot", "stock_name", "", false),
                new FieldMapping("MainlineLeaderVO.leaderType", "CLICKHOUSE", "mainline_daily_snapshot", "leader_type", "", false),
                new FieldMapping("MainlineLeaderVO.leaderScore", "CLICKHOUSE", "mainline_daily_snapshot", "leader_score", "", false),
                new FieldMapping("MainlineRiskVO.mainlineId", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_id", "", false),
                new FieldMapping("MainlineRiskVO.riskType", "CLICKHOUSE", "mainline_daily_snapshot", "risk_type", "", false),
                new FieldMapping("MainlineRiskVO.riskLevel", "CLICKHOUSE", "mainline_daily_snapshot", "risk_level", "", false),
                new FieldMapping("MainlineRiskVO.riskScore", "CLICKHOUSE", "mainline_daily_snapshot", "risk_score", "", false),
                new FieldMapping("MainlineRiskVO.riskText", "CLICKHOUSE", "mainline_daily_snapshot", "risk_json", "", false),
                new FieldMapping("HistoricalMainlineSampleVO.sampleId", "CLICKHOUSE", "mainline_daily_snapshot", "sample_id", "", false),
                new FieldMapping("HistoricalMainlineSampleVO.tradeDate", "CLICKHOUSE", "mainline_daily_snapshot", "trade_date", "", false),
                new FieldMapping("HistoricalMainlineSampleVO.mainlineName", "CLICKHOUSE", "mainline_daily_snapshot", "mainline_name", "", false),
                new FieldMapping("HistoricalMainlineSampleVO.similarityScore", "CLICKHOUSE", "mainline_daily_snapshot", "similarity_score", "", false),
                new FieldMapping("HistoricalMainlineSampleVO.sampleText", "CLICKHOUSE", "mainline_daily_snapshot", "evidence_json", "", false)
        );
    }
}
