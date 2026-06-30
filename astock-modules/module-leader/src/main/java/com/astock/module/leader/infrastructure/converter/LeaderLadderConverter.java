package com.astock.module.leader.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.leader.api.vo.LeaderLadderPageVO;
import com.astock.module.leader.application.query.LeaderLadderPageQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LeaderLadderConverter implements PageBundleConverter<LeaderLadderPageQuery, LeaderLadderPageVO> {

    @Override
    public LeaderLadderPageVO convert(LeaderLadderPageQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        LeaderLadderPageVO vo = new LeaderLadderPageVO();

        if (bundle == null || bundle.isEmpty()) {
            vo.setDataComplete(false);
            vo.setDataStatusText("多表Repository未返回真实记录，拒绝Mock补齐。");
            vo.setTradeDate(query.getTradeDate());
            vo.setConclusion("无真实多表快照数据。");
            vo.setRiskTips(List.of("请检查页面专属多表Repository、引擎落库结果和规则版本。"));
            return vo;
        }

        Map<String, Object> primary = bundle.firstRow("leader_daily_snapshot");
                vo.setTradeDate(MapFieldReader.localDate(primary, "trade_date") == null ? query.getTradeDate() : MapFieldReader.localDate(primary, "trade_date"));
        vo.setDataComplete(quality.getDataComplete());
        vo.setDataStatusText(quality.getDataStatusText());
        vo.setOverview(toLeaderLadderOverviewVO(bundle.firstRow("leader_daily_snapshot"), bundle));
        vo.setMarketLeaders(bundle.rows("leader_daily_snapshot").stream().map(r -> toLeaderCardVO(r, bundle)).toList());
        vo.setMainlineLeaders(bundle.rows("leader_daily_snapshot").stream().map(r -> toLeaderCardVO(r, bundle)).toList());
        vo.setSectorLeaders(bundle.rows("leader_daily_snapshot").stream().map(r -> toLeaderCardVO(r, bundle)).toList());
        vo.setHighBoardLeaders(bundle.rows("leader_daily_snapshot").stream().map(r -> toLeaderCardVO(r, bundle)).toList());
        vo.setTrendLeaders(bundle.rows("leader_daily_snapshot").stream().map(r -> toLeaderCardVO(r, bundle)).toList());
        vo.setCompensationLeaders(bundle.rows("leader_daily_snapshot").stream().map(r -> toLeaderCardVO(r, bundle)).toList());
        vo.setSwitchLeaders(bundle.rows("leader_daily_snapshot").stream().map(r -> toLeaderCardVO(r, bundle)).toList());
        vo.setFollowers(bundle.rows("leader_daily_snapshot").stream().map(r -> toLeaderCardVO(r, bundle)).toList());
        vo.setConsecutiveBoardLadders(bundle.rows("leader_daily_snapshot").stream().map(r -> toConsecutiveBoardLadderVO(r, bundle)).toList());
        vo.setMainlineLadders(bundle.rows("leader_daily_snapshot").stream().map(r -> toMainlineLeaderLadderVO(r, bundle)).toList());
        vo.setTrendLadders(bundle.rows("leader_daily_snapshot").stream().map(r -> toTrendLeaderLadderVO(r, bundle)).toList());
        vo.setDriveRanks(bundle.rows("leader_daily_snapshot").stream().map(r -> toLeaderDriveRankVO(r, bundle)).toList());
        vo.setNegativeFeedbacks(bundle.rows("leader_daily_snapshot").stream().map(r -> toLeaderNegativeFeedbackVO(r, bundle)).toList());
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("leader_daily_snapshot"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("risk_signal_snapshot"), "risk_json"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "LeaderLadderOverviewVO" -> "leader_daily_snapshot";
            case "LeaderCardVO" -> "leader_daily_snapshot";
            case "ConsecutiveBoardLadderVO" -> "leader_daily_snapshot";
            case "MainlineLeaderLadderVO" -> "leader_daily_snapshot";
            case "TrendLeaderLadderVO" -> "leader_daily_snapshot";
            case "LeaderDriveRankVO" -> "leader_daily_snapshot";
            case "LeaderNegativeFeedbackVO" -> "leader_daily_snapshot";
            case "LeaderLadderPageVO" -> "leader_daily_snapshot";
            default -> "leader_daily_snapshot";
        };
    }

    private List<String> stringList(Map<String, Object> row, String column) {
        String value = MapFieldReader.string(row, column);
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value);
    }


    private LeaderLadderPageVO.LeaderLadderOverviewVO toLeaderLadderOverviewVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderLadderPageVO.LeaderLadderOverviewVO item = new LeaderLadderPageVO.LeaderLadderOverviewVO();
        item.setHasMarketLeader(MapFieldReader.bool(row, "has_market_leader"));
        item.setMarketLeaderStockCode(MapFieldReader.string(row, "market_leader_stock_code"));
        item.setMarketLeaderStockName(MapFieldReader.string(row, "market_leader_stock_name"));
        item.setMarketLeaderScore(MapFieldReader.decimal(row, "market_leader_score"));
        item.setMainlineLeaderCount(MapFieldReader.integer(row, "mainline_leader_count"));
        item.setHighBoardLeaderCount(MapFieldReader.integer(row, "high_board_leader_count"));
        item.setTrendLeaderCount(MapFieldReader.integer(row, "trend_leader_count"));
        item.setFollowerCount(MapFieldReader.integer(row, "follower_count"));
        item.setMaxBoardHeight(MapFieldReader.integer(row, "max_board_height"));
        item.setOverallDriveScore(MapFieldReader.decimal(row, "overall_drive_score"));
        item.setOverallNegativeFeedbackScore(MapFieldReader.decimal(row, "overall_negative_feedback_score"));
        item.setOverviewText(MapFieldReader.string(row, "features"));
        return item;
    }

    private LeaderLadderPageVO.LeaderCardVO toLeaderCardVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderLadderPageVO.LeaderCardVO item = new LeaderLadderPageVO.LeaderCardVO();
        item.setStockCode(MapFieldReader.string(row, "stock_code"));
        item.setStockName(MapFieldReader.string(row, "stock_name"));
        item.setLeaderType(MapFieldReader.string(row, "leader_type"));
        item.setLeaderStatus(MapFieldReader.string(row, "leader_status"));
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setMainlineName(MapFieldReader.string(row, "mainline_name"));
        item.setSectorName(MapFieldReader.string(row, "sector_name"));
        item.setLeaderScore(MapFieldReader.decimal(row, "leader_score"));
        item.setRankNo(MapFieldReader.integer(row, "rank_no"));
        item.setDriveScore(MapFieldReader.decimal(row, "drive_score"));
        item.setNegativeFeedbackScore(MapFieldReader.decimal(row, "negative_feedback_score"));
        item.setConsecutiveBoardHeight(MapFieldReader.integer(row, "consecutive_board_height"));
        item.setLimitUp(MapFieldReader.bool(row, "limit_up"));
        item.setBrokenBoard(MapFieldReader.bool(row, "broken_board"));
        item.setInPatternWatchPool(MapFieldReader.bool(row, "in_pattern_watch_pool"));
        item.setRiskVeto(MapFieldReader.bool(row, "risk_veto"));
        item.setLeaderSummary(MapFieldReader.string(row, "leader_summary"));
        item.setRiskSummary(MapFieldReader.string(row, "risk_summary"));
        return item;
    }

    private LeaderLadderPageVO.ConsecutiveBoardLadderVO toConsecutiveBoardLadderVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderLadderPageVO.ConsecutiveBoardLadderVO item = new LeaderLadderPageVO.ConsecutiveBoardLadderVO();
        item.setBoardHeight(MapFieldReader.integer(row, "board_height"));
        item.setStockCount(MapFieldReader.integer(row, "stock_count"));
        item.setStocks(bundle.rows(tableFor("LeaderCardVO")).stream().map(r -> toLeaderCardVO(r, bundle)).toList());
        item.setLadderText(MapFieldReader.string(row, "ladder_text"));
        return item;
    }

    private LeaderLadderPageVO.MainlineLeaderLadderVO toMainlineLeaderLadderVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderLadderPageVO.MainlineLeaderLadderVO item = new LeaderLadderPageVO.MainlineLeaderLadderVO();
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setMainlineName(MapFieldReader.string(row, "mainline_name"));
        item.setLeaders(bundle.rows(tableFor("LeaderCardVO")).stream().map(r -> toLeaderCardVO(r, bundle)).toList());
        return item;
    }

    private LeaderLadderPageVO.TrendLeaderLadderVO toTrendLeaderLadderVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderLadderPageVO.TrendLeaderLadderVO item = new LeaderLadderPageVO.TrendLeaderLadderVO();
        item.setTrendType(MapFieldReader.string(row, "trend_type"));
        item.setLeaders(bundle.rows(tableFor("LeaderCardVO")).stream().map(r -> toLeaderCardVO(r, bundle)).toList());
        return item;
    }

    private LeaderLadderPageVO.LeaderDriveRankVO toLeaderDriveRankVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderLadderPageVO.LeaderDriveRankVO item = new LeaderLadderPageVO.LeaderDriveRankVO();
        item.setStockCode(MapFieldReader.string(row, "stock_code"));
        item.setStockName(MapFieldReader.string(row, "stock_name"));
        item.setDriveScore(MapFieldReader.decimal(row, "drive_score"));
        item.setDriveText(MapFieldReader.string(row, "drive_text"));
        return item;
    }

    private LeaderLadderPageVO.LeaderNegativeFeedbackVO toLeaderNegativeFeedbackVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderLadderPageVO.LeaderNegativeFeedbackVO item = new LeaderLadderPageVO.LeaderNegativeFeedbackVO();
        item.setStockCode(MapFieldReader.string(row, "stock_code"));
        item.setStockName(MapFieldReader.string(row, "stock_name"));
        item.setNegativeFeedbackScore(MapFieldReader.decimal(row, "negative_feedback_score"));
        item.setTriggerRiskControl(MapFieldReader.bool(row, "trigger_risk_control"));
        item.setFeedbackText(MapFieldReader.string(row, "feedback_text"));
        return item;
    }

}
