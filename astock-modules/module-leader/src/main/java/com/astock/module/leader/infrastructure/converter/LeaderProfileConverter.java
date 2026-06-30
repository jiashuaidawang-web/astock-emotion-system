package com.astock.module.leader.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.leader.api.vo.LeaderProfilePageVO;
import com.astock.module.leader.application.query.LeaderProfilePageQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LeaderProfileConverter implements PageBundleConverter<LeaderProfilePageQuery, LeaderProfilePageVO> {

    @Override
    public LeaderProfilePageVO convert(LeaderProfilePageQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        LeaderProfilePageVO vo = new LeaderProfilePageVO();

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
        vo.setStockCode(MapFieldReader.string(bundle.firstRow("leader_daily_snapshot"), "stock_code"));
        vo.setStockName(MapFieldReader.string(bundle.firstRow("leader_daily_snapshot"), "stock_name"));
        vo.setDataComplete(quality.getDataComplete());
        vo.setDataStatusText(quality.getDataStatusText());
        vo.setIdentity(toLeaderIdentityVO(bundle.firstRow("leader_daily_snapshot"), bundle));
        vo.setScoreBreakdown(toLeaderScoreBreakdownVO(bundle.firstRow("leader_daily_snapshot"), bundle));
        vo.setMarketData(toLeaderMarketDataVO(bundle.firstRow("leader_daily_snapshot"), bundle));
        vo.setMainlineRelation(toLeaderMainlineRelationVO(bundle.firstRow("mainline_daily_snapshot"), bundle));
        vo.setBoardStructure(toLeaderBoardStructureVO(bundle.firstRow("leader_daily_snapshot"), bundle));
        vo.setTrendStructure(toLeaderTrendStructureVO(bundle.firstRow("leader_daily_snapshot"), bundle));
        vo.setDriveAnalysis(toLeaderDriveAnalysisVO(bundle.firstRow("leader_daily_snapshot"), bundle));
        vo.setNegativeFeedback(toLeaderNegativeFeedbackAnalysisVO(bundle.firstRow("leader_daily_snapshot"), bundle));
        vo.setLifecycle(bundle.rows("leader_daily_snapshot").stream().map(r -> toLeaderLifecyclePointVO(r, bundle)).toList());
        vo.setRelatedPatternSignals(bundle.rows("leader_daily_snapshot").stream().map(r -> toLeaderRelatedPatternSignalVO(r, bundle)).toList());
        vo.setRelatedRiskSignals(bundle.rows("risk_signal_detail").stream().map(r -> toLeaderRelatedRiskSignalVO(r, bundle)).toList());
        vo.setSimilarLeaderSamples(bundle.rows("leader_daily_snapshot").stream().map(r -> toHistoricalLeaderSampleVO(r, bundle)).toList());
        vo.setEvidenceChain(toLeaderEvidenceChainVO(bundle.firstRow("leader_daily_snapshot"), bundle));
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("leader_daily_snapshot"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("risk_signal_detail"), "risk_json"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "LeaderIdentityVO" -> "leader_daily_snapshot";
            case "LeaderScoreBreakdownVO" -> "leader_daily_snapshot";
            case "LeaderMarketDataVO" -> "leader_daily_snapshot";
            case "LeaderMainlineRelationVO" -> "leader_daily_snapshot";
            case "LeaderBoardStructureVO" -> "leader_daily_snapshot";
            case "LeaderTrendStructureVO" -> "leader_daily_snapshot";
            case "LeaderDriveAnalysisVO" -> "leader_daily_snapshot";
            case "LeaderNegativeFeedbackAnalysisVO" -> "leader_daily_snapshot";
            case "LeaderLifecyclePointVO" -> "leader_daily_snapshot";
            case "LeaderRelatedPatternSignalVO" -> "leader_daily_snapshot";
            case "LeaderRelatedRiskSignalVO" -> "risk_signal_detail";
            case "HistoricalLeaderSampleVO" -> "leader_daily_snapshot";
            case "LeaderEvidenceChainVO" -> "leader_daily_snapshot";
            case "LeaderProfilePageVO" -> "leader_daily_snapshot";
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


    private LeaderProfilePageVO.LeaderIdentityVO toLeaderIdentityVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderProfilePageVO.LeaderIdentityVO item = new LeaderProfilePageVO.LeaderIdentityVO();
        item.setLeaderType(MapFieldReader.string(row, "leader_type"));
        item.setLeaderStatus(MapFieldReader.string(row, "leader_status"));
        item.setRoleInMainline(MapFieldReader.string(row, "role_in_mainline"));
        item.setInPatternWatchPool(MapFieldReader.bool(row, "in_pattern_watch_pool"));
        item.setRiskVeto(MapFieldReader.bool(row, "risk_veto"));
        item.setIdentityText(MapFieldReader.string(row, "identity_text"));
        return item;
    }

    private LeaderProfilePageVO.LeaderScoreBreakdownVO toLeaderScoreBreakdownVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderProfilePageVO.LeaderScoreBreakdownVO item = new LeaderProfilePageVO.LeaderScoreBreakdownVO();
        item.setLeaderScore(MapFieldReader.decimal(row, "leader_score"));
        item.setRecognitionScore(MapFieldReader.decimal(row, "recognition_score"));
        item.setMainlineRelationScore(MapFieldReader.decimal(row, "mainline_relation_score"));
        item.setDriveScore(MapFieldReader.decimal(row, "drive_score"));
        item.setStrengthScore(MapFieldReader.decimal(row, "mainline_strength_score"));
        item.setSupportScore(MapFieldReader.decimal(row, "support_score"));
        item.setContinuityScore(MapFieldReader.decimal(row, "continuity_score"));
        item.setNegativeFeedbackScore(MapFieldReader.decimal(row, "negative_feedback_score"));
        return item;
    }

    private LeaderProfilePageVO.LeaderMarketDataVO toLeaderMarketDataVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderProfilePageVO.LeaderMarketDataVO item = new LeaderProfilePageVO.LeaderMarketDataVO();
        item.setClosePrice(MapFieldReader.decimal(row, "close_price"));
        item.setPctChange(MapFieldReader.decimal(row, "pct_change"));
        item.setTurnoverAmount(MapFieldReader.decimal(row, "turnover_amount"));
        item.setTurnoverRate(MapFieldReader.decimal(row, "turnover_rate"));
        item.setVolumeRatio(MapFieldReader.decimal(row, "volume_ratio"));
        return item;
    }

    private LeaderProfilePageVO.LeaderMainlineRelationVO toLeaderMainlineRelationVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderProfilePageVO.LeaderMainlineRelationVO item = new LeaderProfilePageVO.LeaderMainlineRelationVO();
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setMainlineName(MapFieldReader.string(row, "mainline_name"));
        item.setLifecycleStage(MapFieldReader.string(row, "lifecycle_stage"));
        item.setRelationScore(MapFieldReader.decimal(row, "relation_score"));
        item.setRelationText(MapFieldReader.string(row, "relation_text"));
        return item;
    }

    private LeaderProfilePageVO.LeaderBoardStructureVO toLeaderBoardStructureVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderProfilePageVO.LeaderBoardStructureVO item = new LeaderProfilePageVO.LeaderBoardStructureVO();
        item.setBoardHeight(MapFieldReader.integer(row, "board_height"));
        item.setLimitUp(MapFieldReader.bool(row, "limit_up"));
        item.setBrokenBoard(MapFieldReader.bool(row, "broken_board"));
        item.setReversalBoard(MapFieldReader.bool(row, "reversal_board"));
        item.setBoardText(MapFieldReader.string(row, "board_text"));
        return item;
    }

    private LeaderProfilePageVO.LeaderTrendStructureVO toLeaderTrendStructureVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderProfilePageVO.LeaderTrendStructureVO item = new LeaderProfilePageVO.LeaderTrendStructureVO();
        item.setTrendLeaderType(MapFieldReader.string(row, "trend_leader_type"));
        item.setTrendPosition(MapFieldReader.string(row, "trend_position"));
        item.setTrendStrengthScore(MapFieldReader.decimal(row, "trend_strength_score"));
        item.setTrendBroken(MapFieldReader.bool(row, "trend_broken"));
        item.setTrendText(MapFieldReader.string(row, "trend_text"));
        return item;
    }

    private LeaderProfilePageVO.LeaderDriveAnalysisVO toLeaderDriveAnalysisVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderProfilePageVO.LeaderDriveAnalysisVO item = new LeaderProfilePageVO.LeaderDriveAnalysisVO();
        item.setLeaderDriveScore(MapFieldReader.decimal(row, "leader_drive_score"));
        item.setSectorDriveScore(MapFieldReader.decimal(row, "sector_drive_score"));
        item.setMainlineDriveScore(MapFieldReader.decimal(row, "mainline_drive_score"));
        item.setEmotionDriveScore(MapFieldReader.decimal(row, "emotion_drive_score"));
        item.setFundDriveScore(MapFieldReader.decimal(row, "fund_drive_score"));
        item.setDriveText(MapFieldReader.string(row, "drive_text"));
        return item;
    }

    private LeaderProfilePageVO.LeaderNegativeFeedbackAnalysisVO toLeaderNegativeFeedbackAnalysisVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderProfilePageVO.LeaderNegativeFeedbackAnalysisVO item = new LeaderProfilePageVO.LeaderNegativeFeedbackAnalysisVO();
        item.setNegativeFeedbackScore(MapFieldReader.decimal(row, "negative_feedback_score"));
        item.setBrokenBoard(MapFieldReader.bool(row, "broken_board"));
        item.setLimitDown(MapFieldReader.bool(row, "limit_down"));
        item.setImpactMainline(MapFieldReader.bool(row, "impact_mainline"));
        item.setImpactEmotionCycle(MapFieldReader.bool(row, "impact_emotion_cycle"));
        item.setFeedbackText(MapFieldReader.string(row, "feedback_text"));
        return item;
    }

    private LeaderProfilePageVO.LeaderLifecyclePointVO toLeaderLifecyclePointVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderProfilePageVO.LeaderLifecyclePointVO item = new LeaderProfilePageVO.LeaderLifecyclePointVO();
        item.setTradeDate(MapFieldReader.localDate(row, "trade_date"));
        item.setLeaderType(MapFieldReader.string(row, "leader_type"));
        item.setLeaderStatus(MapFieldReader.string(row, "leader_status"));
        item.setLeaderScore(MapFieldReader.decimal(row, "leader_score"));
        item.setPointText(MapFieldReader.string(row, "point_text"));
        return item;
    }

    private LeaderProfilePageVO.LeaderRelatedPatternSignalVO toLeaderRelatedPatternSignalVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderProfilePageVO.LeaderRelatedPatternSignalVO item = new LeaderProfilePageVO.LeaderRelatedPatternSignalVO();
        item.setSignalId(MapFieldReader.longValue(row, "signal_id"));
        item.setPatternCode(MapFieldReader.string(row, "pattern_code"));
        item.setConditionStatus(MapFieldReader.string(row, "condition_status"));
        item.setConditionScore(MapFieldReader.decimal(row, "condition_score"));
        item.setRiskVeto(MapFieldReader.bool(row, "risk_veto"));
        return item;
    }

    private LeaderProfilePageVO.LeaderRelatedRiskSignalVO toLeaderRelatedRiskSignalVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderProfilePageVO.LeaderRelatedRiskSignalVO item = new LeaderProfilePageVO.LeaderRelatedRiskSignalVO();
        item.setRiskSignalId(MapFieldReader.longValue(row, "risk_signal_id"));
        item.setRiskCode(MapFieldReader.string(row, "risk_code"));
        item.setRiskLevel(MapFieldReader.string(row, "risk_level"));
        item.setRiskScore(MapFieldReader.decimal(row, "risk_score"));
        item.setRiskText(MapFieldReader.string(row, "risk_json"));
        return item;
    }

    private LeaderProfilePageVO.HistoricalLeaderSampleVO toHistoricalLeaderSampleVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderProfilePageVO.HistoricalLeaderSampleVO item = new LeaderProfilePageVO.HistoricalLeaderSampleVO();
        item.setSampleId(MapFieldReader.longValue(row, "sample_id"));
        item.setTradeDate(MapFieldReader.localDate(row, "trade_date"));
        item.setHistoricalStockName(MapFieldReader.string(row, "historical_stock_name"));
        item.setLeaderType(MapFieldReader.string(row, "leader_type"));
        item.setSimilarityScore(MapFieldReader.decimal(row, "similarity_score"));
        item.setFuture3dReturn(MapFieldReader.decimal(row, "future_3d_return"));
        item.setMaxDrawdown(MapFieldReader.decimal(row, "max_drawdown"));
        return item;
    }

    private LeaderProfilePageVO.LeaderEvidenceChainVO toLeaderEvidenceChainVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                LeaderProfilePageVO.LeaderEvidenceChainVO item = new LeaderProfilePageVO.LeaderEvidenceChainVO();
        item.setIdentityEvidences(stringList(row, "identity_evidences"));
        item.setDriveEvidences(stringList(row, "drive_evidences"));
        item.setRiskEvidences(stringList(row, "risk_evidences"));
        item.setEvidenceSummary(MapFieldReader.string(row, "evidence_json"));
        return item;
    }

}
