package com.astock.module.pattern.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatternConditionFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_09_PATTERN_CONDITION";
    }

    @Override
    public String voClassName() {
        return "PatternConditionPageVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("PatternConditionPageVO.tradeDate", "CLICKHOUSE", "buy_pattern_signal_snapshot", "trade_date", "", false),
                new FieldMapping("PatternConditionPageVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("PatternConditionPageVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("PatternConditionPageVO.overview", "CLICKHOUSE", "buy_pattern_signal_snapshot", "overview", "", false),
                new FieldMapping("PatternConditionPageVO.watchPool", "CLICKHOUSE", "buy_pattern_signal_snapshot", "watch_pool", "", false),
                new FieldMapping("PatternConditionPageVO.patternTypeStatistics", "CLICKHOUSE", "buy_pattern_signal_snapshot", "pattern_type_statistics", "", false),
                new FieldMapping("PatternConditionPageVO.signals", "CLICKHOUSE", "buy_pattern_signal_snapshot", "signals", "", false),
                new FieldMapping("PatternConditionPageVO.conditionMetSignals", "CLICKHOUSE", "buy_pattern_signal_snapshot", "condition_met_signals", "", false),
                new FieldMapping("PatternConditionPageVO.observingSignals", "CLICKHOUSE", "buy_pattern_signal_snapshot", "observing_signals", "", false),
                new FieldMapping("PatternConditionPageVO.riskVetoSignals", "CLICKHOUSE", "buy_pattern_signal_snapshot", "risk_veto_signals", "", false),
                new FieldMapping("PatternConditionPageVO.invalidatedSignals", "CLICKHOUSE", "buy_pattern_signal_snapshot", "invalidated_signals", "", false),
                new FieldMapping("PatternConditionPageVO.stageMatrix", "CLICKHOUSE", "buy_pattern_signal_snapshot", "stage_matrix", "", false),
                new FieldMapping("PatternConditionPageVO.backtestSupports", "CLICKHOUSE", "buy_pattern_signal_snapshot", "backtest_supports", "", false),
                new FieldMapping("PatternConditionPageVO.conclusion", "CLICKHOUSE", "buy_pattern_signal_snapshot", "evidence_json", "", false),
                new FieldMapping("PatternConditionPageVO.riskTips", "CLICKHOUSE", "buy_pattern_signal_snapshot", "risk_json", "", false),
                new FieldMapping("PatternConditionOverviewVO.watchObjectCount", "CLICKHOUSE", "buy_pattern_signal_snapshot", "watch_object_count", "", false),
                new FieldMapping("PatternConditionOverviewVO.calculatedObjectCount", "CLICKHOUSE", "buy_pattern_signal_snapshot", "calculated_object_count", "", false),
                new FieldMapping("PatternConditionOverviewVO.totalSignalCount", "CLICKHOUSE", "buy_pattern_signal_snapshot", "total_signal_count", "", false),
                new FieldMapping("PatternConditionOverviewVO.conditionMetCount", "CLICKHOUSE", "buy_pattern_signal_snapshot", "condition_met_count", "", false),
                new FieldMapping("PatternConditionOverviewVO.riskVetoCount", "CLICKHOUSE", "buy_pattern_signal_snapshot", "risk_veto_count", "", false),
                new FieldMapping("PatternConditionOverviewVO.invalidatedCount", "CLICKHOUSE", "buy_pattern_signal_snapshot", "invalidated_count", "", false),
                new FieldMapping("PatternConditionOverviewVO.emotionStage", "CLICKHOUSE", "buy_pattern_signal_snapshot", "primary_stage", "", false),
                new FieldMapping("PatternConditionOverviewVO.riskLevel", "CLICKHOUSE", "buy_pattern_signal_snapshot", "risk_level", "", false),
                new FieldMapping("PatternConditionOverviewVO.retreatStopTriggered", "CLICKHOUSE", "buy_pattern_signal_snapshot", "retreat_stop_triggered", "", false),
                new FieldMapping("PatternConditionOverviewVO.climaxNoChaseTriggered", "CLICKHOUSE", "buy_pattern_signal_snapshot", "climax_no_chase_triggered", "", false),
                new FieldMapping("PatternConditionOverviewVO.overviewText", "CLICKHOUSE", "buy_pattern_signal_snapshot", "features", "", false),
                new FieldMapping("PatternWatchObjectVO.stockCode", "CLICKHOUSE", "buy_pattern_signal_snapshot", "stock_code", "", false),
                new FieldMapping("PatternWatchObjectVO.stockName", "CLICKHOUSE", "buy_pattern_signal_snapshot", "stock_name", "", false),
                new FieldMapping("PatternWatchObjectVO.watchObjectType", "CLICKHOUSE", "buy_pattern_signal_snapshot", "watch_object_type", "", false),
                new FieldMapping("PatternWatchObjectVO.leaderType", "CLICKHOUSE", "buy_pattern_signal_snapshot", "leader_type", "", false),
                new FieldMapping("PatternWatchObjectVO.mainlineId", "CLICKHOUSE", "buy_pattern_signal_snapshot", "mainline_id", "", false),
                new FieldMapping("PatternWatchObjectVO.mainlineName", "CLICKHOUSE", "buy_pattern_signal_snapshot", "mainline_name", "", false),
                new FieldMapping("PatternWatchObjectVO.leaderScore", "CLICKHOUSE", "buy_pattern_signal_snapshot", "leader_score", "", false),
                new FieldMapping("PatternWatchObjectVO.patternCalculationAllowed", "CLICKHOUSE", "buy_pattern_signal_snapshot", "pattern_calculation_allowed", "", false),
                new FieldMapping("PatternWatchObjectVO.hasRiskVeto", "CLICKHOUSE", "buy_pattern_signal_snapshot", "has_risk_veto", "", false),
                new FieldMapping("PatternWatchObjectVO.watchObjectText", "CLICKHOUSE", "buy_pattern_signal_snapshot", "watch_object_text", "", false),
                new FieldMapping("PatternTypeStatisticsVO.patternCode", "CLICKHOUSE", "buy_pattern_signal_snapshot", "pattern_code", "", false),
                new FieldMapping("PatternTypeStatisticsVO.patternName", "CLICKHOUSE", "buy_pattern_signal_snapshot", "pattern_name", "", false),
                new FieldMapping("PatternTypeStatisticsVO.applicableObjectCount", "CLICKHOUSE", "buy_pattern_signal_snapshot", "applicable_object_count", "", false),
                new FieldMapping("PatternTypeStatisticsVO.conditionMetCount", "CLICKHOUSE", "buy_pattern_signal_snapshot", "condition_met_count", "", false),
                new FieldMapping("PatternTypeStatisticsVO.riskVetoCount", "CLICKHOUSE", "buy_pattern_signal_snapshot", "risk_veto_count", "", false),
                new FieldMapping("PatternTypeStatisticsVO.invalidatedCount", "CLICKHOUSE", "buy_pattern_signal_snapshot", "invalidated_count", "", false),
                new FieldMapping("PatternTypeStatisticsVO.avgConditionScore", "CLICKHOUSE", "buy_pattern_signal_snapshot", "avg_condition_score", "", false),
                new FieldMapping("PatternTypeStatisticsVO.globallyRestricted", "CLICKHOUSE", "buy_pattern_signal_snapshot", "globally_restricted", "", false),
                new FieldMapping("PatternTypeStatisticsVO.statisticsText", "CLICKHOUSE", "buy_pattern_signal_snapshot", "statistics_text", "", false),
                new FieldMapping("PatternSignalVO.signalId", "CLICKHOUSE", "buy_pattern_signal_snapshot", "signal_id", "", false),
                new FieldMapping("PatternSignalVO.patternCode", "CLICKHOUSE", "buy_pattern_signal_snapshot", "pattern_code", "", false),
                new FieldMapping("PatternSignalVO.patternName", "CLICKHOUSE", "buy_pattern_signal_snapshot", "pattern_name", "", false),
                new FieldMapping("PatternSignalVO.stockCode", "CLICKHOUSE", "buy_pattern_signal_snapshot", "stock_code", "", false),
                new FieldMapping("PatternSignalVO.stockName", "CLICKHOUSE", "buy_pattern_signal_snapshot", "stock_name", "", false),
                new FieldMapping("PatternSignalVO.mainlineId", "CLICKHOUSE", "buy_pattern_signal_snapshot", "mainline_id", "", false),
                new FieldMapping("PatternSignalVO.mainlineName", "CLICKHOUSE", "buy_pattern_signal_snapshot", "mainline_name", "", false),
                new FieldMapping("PatternSignalVO.leaderType", "CLICKHOUSE", "buy_pattern_signal_snapshot", "leader_type", "", false),
                new FieldMapping("PatternSignalVO.emotionStage", "CLICKHOUSE", "buy_pattern_signal_snapshot", "primary_stage", "", false),
                new FieldMapping("PatternSignalVO.conditionStatus", "CLICKHOUSE", "buy_pattern_signal_snapshot", "condition_status", "", false),
                new FieldMapping("PatternSignalVO.conditionScore", "CLICKHOUSE", "buy_pattern_signal_snapshot", "condition_score", "", false),
                new FieldMapping("PatternSignalVO.cycleAdmissionScore", "CLICKHOUSE", "buy_pattern_signal_snapshot", "cycle_admission_score", "", false),
                new FieldMapping("PatternSignalVO.mainlineValidScore", "CLICKHOUSE", "buy_pattern_signal_snapshot", "mainline_valid_score", "", false),
                new FieldMapping("PatternSignalVO.leaderPositionScore", "CLICKHOUSE", "buy_pattern_signal_snapshot", "leader_position_score", "", false),
                new FieldMapping("PatternSignalVO.triggerScore", "CLICKHOUSE", "buy_pattern_signal_snapshot", "trigger_score", "", false),
                new FieldMapping("PatternSignalVO.backtestSupportScore", "CLICKHOUSE", "buy_pattern_signal_snapshot", "backtest_support_score", "", false),
                new FieldMapping("PatternSignalVO.riskVeto", "CLICKHOUSE", "buy_pattern_signal_snapshot", "risk_veto", "", false),
                new FieldMapping("PatternSignalVO.riskVetoReason", "CLICKHOUSE", "buy_pattern_signal_snapshot", "risk_veto_reason", "", false),
                new FieldMapping("PatternSignalVO.invalidated", "CLICKHOUSE", "buy_pattern_signal_snapshot", "invalidated", "", false),
                new FieldMapping("PatternSignalVO.invalidatedReason", "CLICKHOUSE", "buy_pattern_signal_snapshot", "invalidated_reason", "", false),
                new FieldMapping("PatternSignalVO.allowConditionMetDisplay", "CLICKHOUSE", "buy_pattern_signal_snapshot", "allow_condition_met_display", "", false),
                new FieldMapping("PatternSignalVO.signalText", "CLICKHOUSE", "buy_pattern_signal_snapshot", "signal_text", "", false),
                new FieldMapping("PatternStageMatrixVO.patternCode", "CLICKHOUSE", "buy_pattern_signal_snapshot", "pattern_code", "", false),
                new FieldMapping("PatternStageMatrixVO.stageCode", "CLICKHOUSE", "buy_pattern_signal_snapshot", "primary_stage", "", false),
                new FieldMapping("PatternStageMatrixVO.applicabilityLevel", "CLICKHOUSE", "buy_pattern_signal_snapshot", "applicability_level", "", false),
                new FieldMapping("PatternStageMatrixVO.conditionMetAllowed", "CLICKHOUSE", "buy_pattern_signal_snapshot", "condition_met_allowed", "", false),
                new FieldMapping("PatternStageMatrixVO.riskConfirmRequired", "CLICKHOUSE", "buy_pattern_signal_snapshot", "risk_confirm_required", "", false),
                new FieldMapping("PatternStageMatrixVO.matrixText", "CLICKHOUSE", "buy_pattern_signal_snapshot", "matrix_text", "", false),
                new FieldMapping("PatternBacktestSupportVO.patternCode", "CLICKHOUSE", "buy_pattern_signal_snapshot", "pattern_code", "", false),
                new FieldMapping("PatternBacktestSupportVO.emotionStage", "CLICKHOUSE", "buy_pattern_signal_snapshot", "primary_stage", "", false),
                new FieldMapping("PatternBacktestSupportVO.leaderType", "CLICKHOUSE", "buy_pattern_signal_snapshot", "leader_type", "", false),
                new FieldMapping("PatternBacktestSupportVO.sampleCount", "CLICKHOUSE", "buy_pattern_signal_snapshot", "sample_count", "", false),
                new FieldMapping("PatternBacktestSupportVO.future3dAvgReturn", "CLICKHOUSE", "buy_pattern_signal_snapshot", "future3d_avg_return", "", false),
                new FieldMapping("PatternBacktestSupportVO.winRate", "CLICKHOUSE", "buy_pattern_signal_snapshot", "win_rate", "", false),
                new FieldMapping("PatternBacktestSupportVO.maxDrawdown", "CLICKHOUSE", "buy_pattern_signal_snapshot", "max_drawdown", "", false),
                new FieldMapping("PatternBacktestSupportVO.backtestSupportScore", "CLICKHOUSE", "buy_pattern_signal_snapshot", "backtest_support_score", "", false),
                new FieldMapping("PatternBacktestSupportVO.backtestText", "CLICKHOUSE", "buy_pattern_signal_snapshot", "backtest_text", "", false)
        );
    }
}
