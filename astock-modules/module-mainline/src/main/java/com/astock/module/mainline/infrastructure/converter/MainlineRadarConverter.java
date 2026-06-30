package com.astock.module.mainline.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.mainline.api.vo.MainlineRadarPageVO;
import com.astock.module.mainline.application.query.MainlineRadarPageQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MainlineRadarConverter implements PageBundleConverter<MainlineRadarPageQuery, MainlineRadarPageVO> {

    @Override
    public MainlineRadarPageVO convert(MainlineRadarPageQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        MainlineRadarPageVO vo = new MainlineRadarPageVO();

        if (bundle == null || bundle.isEmpty()) {
            vo.setDataComplete(false);
            vo.setDataStatusText("多表Repository未返回真实记录，拒绝Mock补齐。");
            vo.setTradeDate(query.getTradeDate());
            vo.setConclusion("无真实多表快照数据。");
            vo.setRiskTips(List.of("请检查页面专属多表Repository、引擎落库结果和规则版本。"));
            return vo;
        }

        Map<String, Object> primary = bundle.firstRow("mainline_daily_snapshot");
                vo.setTradeDate(MapFieldReader.localDate(primary, "trade_date") == null ? query.getTradeDate() : MapFieldReader.localDate(primary, "trade_date"));
        vo.setDataComplete(quality.getDataComplete());
        vo.setDataStatusText(quality.getDataStatusText());
        vo.setOverview(toMainlineOverviewVO(bundle.firstRow("mainline_daily_snapshot"), bundle));
        vo.setMainlineRanks(bundle.rows("mainline_daily_snapshot").stream().map(r -> toMainlineRankVO(r, bundle)).toList());
        vo.setLifecycleList(bundle.rows("mainline_daily_snapshot").stream().map(r -> toMainlineLifecycleVO(r, bundle)).toList());
        vo.setCompetition(toMainlineCompetitionVO(bundle.firstRow("mainline_daily_snapshot"), bundle));
        vo.setSwitchSignals(bundle.rows("mainline_daily_snapshot").stream().map(r -> toMainlineSwitchVO(r, bundle)).toList());
        vo.setScoreBreakdowns(bundle.rows("mainline_daily_snapshot").stream().map(r -> toMainlineScoreBreakdownVO(r, bundle)).toList());
        vo.setLadders(bundle.rows("mainline_daily_snapshot").stream().map(r -> toMainlineLadderVO(r, bundle)).toList());
        vo.setLeaders(bundle.rows("leader_daily_snapshot").stream().map(r -> toMainlineLeaderVO(r, bundle)).toList());
        vo.setRisks(bundle.rows("risk_signal_detail").stream().map(r -> toMainlineRiskVO(r, bundle)).toList());
        vo.setHistoricalSamples(bundle.rows("mainline_daily_snapshot").stream().map(r -> toHistoricalMainlineSampleVO(r, bundle)).toList());
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("mainline_daily_snapshot"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("risk_signal_detail"), "risk_json"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "MainlineOverviewVO" -> "mainline_daily_snapshot";
            case "MainlineRankVO" -> "mainline_daily_snapshot";
            case "MainlineLifecycleVO" -> "mainline_daily_snapshot";
            case "MainlineCompetitionVO" -> "mainline_daily_snapshot";
            case "MainlineCompetitionItemVO" -> "mainline_daily_snapshot";
            case "MainlineSwitchVO" -> "mainline_daily_snapshot";
            case "MainlineScoreBreakdownVO" -> "mainline_daily_snapshot";
            case "MainlineLadderVO" -> "mainline_daily_snapshot";
            case "MainlineLeaderVO" -> "leader_daily_snapshot";
            case "MainlineRiskVO" -> "risk_signal_detail";
            case "HistoricalMainlineSampleVO" -> "mainline_daily_snapshot";
            case "MainlineRadarPageVO" -> "mainline_daily_snapshot";
            default -> "mainline_daily_snapshot";
        };
    }

    private List<String> stringList(Map<String, Object> row, String column) {
        String value = MapFieldReader.string(row, column);
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value);
    }


    private MainlineRadarPageVO.MainlineOverviewVO toMainlineOverviewVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MainlineRadarPageVO.MainlineOverviewVO item = new MainlineRadarPageVO.MainlineOverviewVO();
        item.setMainlineCount(MapFieldReader.integer(row, "mainline_count"));
        item.setConfirmedCount(MapFieldReader.integer(row, "confirmed_count"));
        item.setCandidateCount(MapFieldReader.integer(row, "candidate_count"));
        item.setStrongestMainlineName(MapFieldReader.string(row, "strongest_mainline_name"));
        item.setStrongestScore(MapFieldReader.decimal(row, "strongest_score"));
        item.setOverviewText(MapFieldReader.string(row, "features"));
        return item;
    }

    private MainlineRadarPageVO.MainlineRankVO toMainlineRankVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MainlineRadarPageVO.MainlineRankVO item = new MainlineRadarPageVO.MainlineRankVO();
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setMainlineName(MapFieldReader.string(row, "mainline_name"));
        item.setMainlineStatus(MapFieldReader.string(row, "mainline_status"));
        item.setLifecycleStage(MapFieldReader.string(row, "lifecycle_stage"));
        item.setThemeRole(MapFieldReader.string(row, "theme_role"));
        item.setMainlineStrengthScore(MapFieldReader.decimal(row, "mainline_strength_score"));
        item.setRankNo(MapFieldReader.integer(row, "rank_no"));
        item.setLeaderStockName(MapFieldReader.string(row, "leader_stock_name"));
        return item;
    }

    private MainlineRadarPageVO.MainlineLifecycleVO toMainlineLifecycleVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MainlineRadarPageVO.MainlineLifecycleVO item = new MainlineRadarPageVO.MainlineLifecycleVO();
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setMainlineName(MapFieldReader.string(row, "mainline_name"));
        item.setLifecycleStage(MapFieldReader.string(row, "lifecycle_stage"));
        item.setLifecycleText(MapFieldReader.string(row, "lifecycle_text"));
        return item;
    }

    private MainlineRadarPageVO.MainlineCompetitionVO toMainlineCompetitionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MainlineRadarPageVO.MainlineCompetitionVO item = new MainlineRadarPageVO.MainlineCompetitionVO();
        item.setCompetitionStatus(MapFieldReader.string(row, "competition_status"));
        item.setItems(bundle.rows(tableFor("MainlineCompetitionItemVO")).stream().map(r -> toMainlineCompetitionItemVO(r, bundle)).toList());
        item.setCompetitionText(MapFieldReader.string(row, "competition_text"));
        return item;
    }

    private MainlineRadarPageVO.MainlineCompetitionItemVO toMainlineCompetitionItemVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MainlineRadarPageVO.MainlineCompetitionItemVO item = new MainlineRadarPageVO.MainlineCompetitionItemVO();
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setMainlineName(MapFieldReader.string(row, "mainline_name"));
        item.setStrengthScore(MapFieldReader.decimal(row, "mainline_strength_score"));
        item.setRole(MapFieldReader.string(row, "role"));
        return item;
    }

    private MainlineRadarPageVO.MainlineSwitchVO toMainlineSwitchVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MainlineRadarPageVO.MainlineSwitchVO item = new MainlineRadarPageVO.MainlineSwitchVO();
        item.setOldMainlineId(MapFieldReader.longValue(row, "old_mainline_id"));
        item.setOldMainlineName(MapFieldReader.string(row, "old_mainline_name"));
        item.setNewMainlineId(MapFieldReader.longValue(row, "new_mainline_id"));
        item.setNewMainlineName(MapFieldReader.string(row, "new_mainline_name"));
        item.setSwitchStatus(MapFieldReader.string(row, "switch_status"));
        item.setSwitchScore(MapFieldReader.decimal(row, "switch_score"));
        item.setRiskText(MapFieldReader.string(row, "risk_json"));
        return item;
    }

    private MainlineRadarPageVO.MainlineScoreBreakdownVO toMainlineScoreBreakdownVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MainlineRadarPageVO.MainlineScoreBreakdownVO item = new MainlineRadarPageVO.MainlineScoreBreakdownVO();
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setLimitUpClusterScore(MapFieldReader.decimal(row, "limit_up_cluster_score"));
        item.setTurnoverConcentrationScore(MapFieldReader.decimal(row, "turnover_concentration_score"));
        item.setContinuityScore(MapFieldReader.decimal(row, "continuity_score"));
        item.setLadderIntegrityScore(MapFieldReader.decimal(row, "ladder_integrity_score"));
        item.setLeaderDriveScore(MapFieldReader.decimal(row, "leader_drive_score"));
        item.setEmotionMatchScore(MapFieldReader.decimal(row, "emotion_match_score"));
        return item;
    }

    private MainlineRadarPageVO.MainlineLadderVO toMainlineLadderVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MainlineRadarPageVO.MainlineLadderVO item = new MainlineRadarPageVO.MainlineLadderVO();
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setMainlineName(MapFieldReader.string(row, "mainline_name"));
        item.setMaxBoardHeight(MapFieldReader.integer(row, "max_board_height"));
        item.setLimitUpCount(MapFieldReader.integer(row, "limit_up_count"));
        item.setLadderText(MapFieldReader.string(row, "ladder_text"));
        return item;
    }

    private MainlineRadarPageVO.MainlineLeaderVO toMainlineLeaderVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MainlineRadarPageVO.MainlineLeaderVO item = new MainlineRadarPageVO.MainlineLeaderVO();
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setStockCode(MapFieldReader.string(row, "stock_code"));
        item.setStockName(MapFieldReader.string(row, "stock_name"));
        item.setLeaderType(MapFieldReader.string(row, "leader_type"));
        item.setLeaderScore(MapFieldReader.decimal(row, "leader_score"));
        return item;
    }

    private MainlineRadarPageVO.MainlineRiskVO toMainlineRiskVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MainlineRadarPageVO.MainlineRiskVO item = new MainlineRadarPageVO.MainlineRiskVO();
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setRiskType(MapFieldReader.string(row, "risk_type"));
        item.setRiskLevel(MapFieldReader.string(row, "risk_level"));
        item.setRiskScore(MapFieldReader.decimal(row, "risk_score"));
        item.setRiskText(MapFieldReader.string(row, "risk_json"));
        return item;
    }

    private MainlineRadarPageVO.HistoricalMainlineSampleVO toHistoricalMainlineSampleVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                MainlineRadarPageVO.HistoricalMainlineSampleVO item = new MainlineRadarPageVO.HistoricalMainlineSampleVO();
        item.setSampleId(MapFieldReader.longValue(row, "sample_id"));
        item.setTradeDate(MapFieldReader.localDate(row, "trade_date"));
        item.setMainlineName(MapFieldReader.string(row, "mainline_name"));
        item.setSimilarityScore(MapFieldReader.decimal(row, "similarity_score"));
        item.setSampleText(MapFieldReader.string(row, "evidence_json"));
        return item;
    }

}
