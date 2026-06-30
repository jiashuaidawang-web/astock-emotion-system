package com.astock.module.pattern.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.pattern.api.vo.PatternConditionPageVO;
import com.astock.module.pattern.application.query.PatternConditionPageQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PatternConditionConverter implements PageBundleConverter<PatternConditionPageQuery, PatternConditionPageVO> {

    @Override
    public PatternConditionPageVO convert(PatternConditionPageQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        PatternConditionPageVO vo = new PatternConditionPageVO();

        if (bundle == null || bundle.isEmpty()) {
            vo.setDataComplete(false);
            vo.setDataStatusText("多表Repository未返回真实记录，拒绝Mock补齐。");
            vo.setTradeDate(query.getTradeDate());
            vo.setConclusion("无真实多表快照数据。");
            vo.setRiskTips(List.of("请检查页面专属多表Repository、引擎落库结果和规则版本。"));
            return vo;
        }

        Map<String, Object> primary = bundle.firstRow("buy_pattern_signal_snapshot");
                vo.setTradeDate(MapFieldReader.localDate(primary, "trade_date") == null ? query.getTradeDate() : MapFieldReader.localDate(primary, "trade_date"));
        vo.setDataComplete(quality.getDataComplete());
        vo.setDataStatusText(quality.getDataStatusText());
        vo.setOverview(toPatternConditionOverviewVO(bundle.firstRow("buy_pattern_signal_snapshot"), bundle));
        vo.setWatchPool(bundle.rows("buy_pattern_signal_snapshot").stream().map(r -> toPatternWatchObjectVO(r, bundle)).toList());
        vo.setPatternTypeStatistics(bundle.rows("buy_pattern_signal_snapshot").stream().map(r -> toPatternTypeStatisticsVO(r, bundle)).toList());
        vo.setSignals(bundle.rows("buy_pattern_signal_snapshot").stream().map(r -> toPatternSignalVO(r, bundle)).toList());
        vo.setConditionMetSignals(bundle.rows("buy_pattern_signal_snapshot").stream().map(r -> toPatternSignalVO(r, bundle)).toList());
        vo.setObservingSignals(bundle.rows("buy_pattern_signal_snapshot").stream().map(r -> toPatternSignalVO(r, bundle)).toList());
        vo.setRiskVetoSignals(bundle.rows("buy_pattern_signal_snapshot").stream().map(r -> toPatternSignalVO(r, bundle)).toList());
        vo.setInvalidatedSignals(bundle.rows("buy_pattern_signal_snapshot").stream().map(r -> toPatternSignalVO(r, bundle)).toList());
        vo.setStageMatrix(bundle.rows("buy_pattern_signal_snapshot").stream().map(r -> toPatternStageMatrixVO(r, bundle)).toList());
        vo.setBacktestSupports(bundle.rows("buy_pattern_signal_snapshot").stream().map(r -> toPatternBacktestSupportVO(r, bundle)).toList());
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("buy_pattern_signal_snapshot"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("risk_signal_detail"), "risk_json"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "PatternConditionOverviewVO" -> "buy_pattern_signal_snapshot";
            case "PatternWatchObjectVO" -> "buy_pattern_signal_snapshot";
            case "PatternTypeStatisticsVO" -> "buy_pattern_signal_snapshot";
            case "PatternSignalVO" -> "buy_pattern_signal_snapshot";
            case "PatternStageMatrixVO" -> "buy_pattern_signal_snapshot";
            case "PatternBacktestSupportVO" -> "buy_pattern_signal_snapshot";
            case "PatternConditionPageVO" -> "buy_pattern_signal_snapshot";
            default -> "buy_pattern_signal_snapshot";
        };
    }

    private List<String> stringList(Map<String, Object> row, String column) {
        String value = MapFieldReader.string(row, column);
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value);
    }


    private PatternConditionPageVO.PatternConditionOverviewVO toPatternConditionOverviewVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                PatternConditionPageVO.PatternConditionOverviewVO item = new PatternConditionPageVO.PatternConditionOverviewVO();
        item.setWatchObjectCount(MapFieldReader.integer(row, "watch_object_count"));
        item.setCalculatedObjectCount(MapFieldReader.integer(row, "calculated_object_count"));
        item.setTotalSignalCount(MapFieldReader.integer(row, "total_signal_count"));
        item.setConditionMetCount(MapFieldReader.integer(row, "condition_met_count"));
        item.setRiskVetoCount(MapFieldReader.integer(row, "risk_veto_count"));
        item.setInvalidatedCount(MapFieldReader.integer(row, "invalidated_count"));
        item.setEmotionStage(MapFieldReader.string(row, "primary_stage"));
        item.setRiskLevel(MapFieldReader.string(row, "risk_level"));
        item.setRetreatStopTriggered(MapFieldReader.bool(row, "retreat_stop_triggered"));
        item.setClimaxNoChaseTriggered(MapFieldReader.bool(row, "climax_no_chase_triggered"));
        item.setOverviewText(MapFieldReader.string(row, "features"));
        return item;
    }

    private PatternConditionPageVO.PatternWatchObjectVO toPatternWatchObjectVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                PatternConditionPageVO.PatternWatchObjectVO item = new PatternConditionPageVO.PatternWatchObjectVO();
        item.setStockCode(MapFieldReader.string(row, "stock_code"));
        item.setStockName(MapFieldReader.string(row, "stock_name"));
        item.setWatchObjectType(MapFieldReader.string(row, "watch_object_type"));
        item.setLeaderType(MapFieldReader.string(row, "leader_type"));
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setMainlineName(MapFieldReader.string(row, "mainline_name"));
        item.setLeaderScore(MapFieldReader.decimal(row, "leader_score"));
        item.setPatternCalculationAllowed(MapFieldReader.bool(row, "pattern_calculation_allowed"));
        item.setHasRiskVeto(MapFieldReader.bool(row, "has_risk_veto"));
        item.setWatchObjectText(MapFieldReader.string(row, "watch_object_text"));
        return item;
    }

    private PatternConditionPageVO.PatternTypeStatisticsVO toPatternTypeStatisticsVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                PatternConditionPageVO.PatternTypeStatisticsVO item = new PatternConditionPageVO.PatternTypeStatisticsVO();
        item.setPatternCode(MapFieldReader.string(row, "pattern_code"));
        item.setPatternName(MapFieldReader.string(row, "pattern_name"));
        item.setApplicableObjectCount(MapFieldReader.integer(row, "applicable_object_count"));
        item.setConditionMetCount(MapFieldReader.integer(row, "condition_met_count"));
        item.setRiskVetoCount(MapFieldReader.integer(row, "risk_veto_count"));
        item.setInvalidatedCount(MapFieldReader.integer(row, "invalidated_count"));
        item.setAvgConditionScore(MapFieldReader.decimal(row, "avg_condition_score"));
        item.setGloballyRestricted(MapFieldReader.bool(row, "globally_restricted"));
        item.setStatisticsText(MapFieldReader.string(row, "statistics_text"));
        return item;
    }

    private PatternConditionPageVO.PatternSignalVO toPatternSignalVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                PatternConditionPageVO.PatternSignalVO item = new PatternConditionPageVO.PatternSignalVO();
        item.setSignalId(MapFieldReader.longValue(row, "signal_id"));
        item.setPatternCode(MapFieldReader.string(row, "pattern_code"));
        item.setPatternName(MapFieldReader.string(row, "pattern_name"));
        item.setStockCode(MapFieldReader.string(row, "stock_code"));
        item.setStockName(MapFieldReader.string(row, "stock_name"));
        item.setMainlineId(MapFieldReader.longValue(row, "mainline_id"));
        item.setMainlineName(MapFieldReader.string(row, "mainline_name"));
        item.setLeaderType(MapFieldReader.string(row, "leader_type"));
        item.setEmotionStage(MapFieldReader.string(row, "primary_stage"));
        item.setConditionStatus(MapFieldReader.string(row, "condition_status"));
        item.setConditionScore(MapFieldReader.decimal(row, "condition_score"));
        item.setCycleAdmissionScore(MapFieldReader.decimal(row, "cycle_admission_score"));
        item.setMainlineValidScore(MapFieldReader.decimal(row, "mainline_valid_score"));
        item.setLeaderPositionScore(MapFieldReader.decimal(row, "leader_position_score"));
        item.setTriggerScore(MapFieldReader.decimal(row, "trigger_score"));
        item.setBacktestSupportScore(MapFieldReader.decimal(row, "backtest_support_score"));
        item.setRiskVeto(MapFieldReader.bool(row, "risk_veto"));
        item.setRiskVetoReason(MapFieldReader.string(row, "risk_veto_reason"));
        item.setInvalidated(MapFieldReader.bool(row, "invalidated"));
        item.setInvalidatedReason(MapFieldReader.string(row, "invalidated_reason"));
        item.setAllowConditionMetDisplay(MapFieldReader.bool(row, "allow_condition_met_display"));
        item.setSignalText(MapFieldReader.string(row, "signal_text"));
        return item;
    }

    private PatternConditionPageVO.PatternStageMatrixVO toPatternStageMatrixVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                PatternConditionPageVO.PatternStageMatrixVO item = new PatternConditionPageVO.PatternStageMatrixVO();
        item.setPatternCode(MapFieldReader.string(row, "pattern_code"));
        item.setStageCode(MapFieldReader.string(row, "primary_stage"));
        item.setApplicabilityLevel(MapFieldReader.string(row, "applicability_level"));
        item.setConditionMetAllowed(MapFieldReader.bool(row, "condition_met_allowed"));
        item.setRiskConfirmRequired(MapFieldReader.bool(row, "risk_confirm_required"));
        item.setMatrixText(MapFieldReader.string(row, "matrix_text"));
        return item;
    }

    private PatternConditionPageVO.PatternBacktestSupportVO toPatternBacktestSupportVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                PatternConditionPageVO.PatternBacktestSupportVO item = new PatternConditionPageVO.PatternBacktestSupportVO();
        item.setPatternCode(MapFieldReader.string(row, "pattern_code"));
        item.setEmotionStage(MapFieldReader.string(row, "primary_stage"));
        item.setLeaderType(MapFieldReader.string(row, "leader_type"));
        item.setSampleCount(MapFieldReader.integer(row, "sample_count"));
        item.setFuture3dAvgReturn(MapFieldReader.decimal(row, "future3d_avg_return"));
        item.setWinRate(MapFieldReader.decimal(row, "win_rate"));
        item.setMaxDrawdown(MapFieldReader.decimal(row, "max_drawdown"));
        item.setBacktestSupportScore(MapFieldReader.decimal(row, "backtest_support_score"));
        item.setBacktestText(MapFieldReader.string(row, "backtest_text"));
        return item;
    }

}
