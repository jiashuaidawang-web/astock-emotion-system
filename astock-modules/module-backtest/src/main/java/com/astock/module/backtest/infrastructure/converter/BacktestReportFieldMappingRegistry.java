package com.astock.module.backtest.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BacktestReportFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_12_BACKTEST_REPORT";
    }

    @Override
    public String voClassName() {
        return "BacktestReportDetailVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("BacktestReportDetailVO.reportId", "CLICKHOUSE", "backtest_layer_stat", "report_id", "", false),
                new FieldMapping("BacktestReportDetailVO.taskId", "CLICKHOUSE", "backtest_layer_stat", "task_id", "", false),
                new FieldMapping("BacktestReportDetailVO.tradeDate", "CLICKHOUSE", "backtest_layer_stat", "trade_date", "", false),
                new FieldMapping("BacktestReportDetailVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("BacktestReportDetailVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("BacktestReportDetailVO.header", "CLICKHOUSE", "backtest_layer_stat", "header", "", false),
                new FieldMapping("BacktestReportDetailVO.taskParam", "CLICKHOUSE", "backtest_layer_stat", "task_param", "", false),
                new FieldMapping("BacktestReportDetailVO.metricSummary", "CLICKHOUSE", "backtest_layer_stat", "metric_summary", "", false),
                new FieldMapping("BacktestReportDetailVO.metricCards", "CLICKHOUSE", "backtest_layer_stat", "metric_cards", "", false),
                new FieldMapping("BacktestReportDetailVO.equityCurve", "CLICKHOUSE", "backtest_layer_stat", "equity_curve", "", false),
                new FieldMapping("BacktestReportDetailVO.layerStats", "CLICKHOUSE", "backtest_layer_stat", "layer_stats", "", false),
                new FieldMapping("BacktestReportDetailVO.riskEffect", "CLICKHOUSE", "backtest_layer_stat", "risk_effect", "", false),
                new FieldMapping("BacktestReportDetailVO.failureCases", "CLICKHOUSE", "backtest_layer_stat", "failure_cases", "", false),
                new FieldMapping("BacktestReportDetailVO.ruleVersionSnapshot", "CLICKHOUSE", "backtest_layer_stat", "rule_version_snapshot", "", false),
                new FieldMapping("BacktestReportDetailVO.ruleVersionDiff", "CLICKHOUSE", "backtest_layer_stat", "rule_version_diff", "", false),
                new FieldMapping("BacktestReportDetailVO.dataQuality", "CLICKHOUSE", "backtest_layer_stat", "data_quality", "", false),
                new FieldMapping("BacktestReportDetailVO.futureLeakageSummary", "CLICKHOUSE", "backtest_layer_stat", "future_leakage_summary", "", false),
                new FieldMapping("BacktestReportDetailVO.optimizationAdvices", "CLICKHOUSE", "backtest_layer_stat", "optimization_advices", "", false),
                new FieldMapping("BacktestReportDetailVO.conclusion", "CLICKHOUSE", "backtest_layer_stat", "evidence_json", "", false),
                new FieldMapping("BacktestReportDetailVO.riskTips", "CLICKHOUSE", "backtest_layer_stat", "risk_json", "", false),
                new FieldMapping("BacktestReportHeaderVO.reportId", "CLICKHOUSE", "backtest_layer_stat", "report_id", "", false),
                new FieldMapping("BacktestReportHeaderVO.reportName", "CLICKHOUSE", "backtest_layer_stat", "report_name", "", false),
                new FieldMapping("BacktestReportHeaderVO.reportStatus", "CLICKHOUSE", "backtest_layer_stat", "report_status", "", false),
                new FieldMapping("BacktestReportHeaderVO.objectType", "CLICKHOUSE", "backtest_layer_stat", "object_type", "", false),
                new FieldMapping("BacktestReportHeaderVO.credibilityLevel", "CLICKHOUSE", "backtest_layer_stat", "credibility_level", "", false),
                new FieldMapping("BacktestReportHeaderVO.dataCheckPassed", "CLICKHOUSE", "backtest_layer_stat", "data_check_passed", "", false),
                new FieldMapping("BacktestReportHeaderVO.futureLeakageCheckPassed", "CLICKHOUSE", "backtest_layer_stat", "future_leakage_check_passed", "", false),
                new FieldMapping("BacktestReportHeaderVO.generatedAt", "CLICKHOUSE", "backtest_layer_stat", "generated_at", "", false),
                new FieldMapping("BacktestReportHeaderVO.reportSummary", "CLICKHOUSE", "backtest_layer_stat", "report_summary", "", false),
                new FieldMapping("BacktestTaskParamVO.taskId", "CLICKHOUSE", "backtest_layer_stat", "task_id", "", false),
                new FieldMapping("BacktestTaskParamVO.objectType", "CLICKHOUSE", "backtest_layer_stat", "object_type", "", false),
                new FieldMapping("BacktestTaskParamVO.ruleVersionId", "CLICKHOUSE", "backtest_layer_stat", "rule_version_id", "", false),
                new FieldMapping("BacktestTaskParamVO.compareRuleVersionId", "CLICKHOUSE", "backtest_layer_stat", "compare_rule_version_id", "", false),
                new FieldMapping("BacktestTaskParamVO.startDate", "CLICKHOUSE", "backtest_layer_stat", "start_date", "", false),
                new FieldMapping("BacktestTaskParamVO.endDate", "CLICKHOUSE", "backtest_layer_stat", "end_date", "", false),
                new FieldMapping("BacktestTaskParamVO.layerDimensions", "CLICKHOUSE", "backtest_layer_stat", "layer_dimensions", "", false),
                new FieldMapping("BacktestTaskParamVO.metrics", "CLICKHOUSE", "backtest_layer_stat", "metrics", "", false),
                new FieldMapping("BacktestTaskParamVO.paramText", "CLICKHOUSE", "backtest_layer_stat", "param_text", "", false),
                new FieldMapping("BacktestMetricSummaryVO.signalCount", "CLICKHOUSE", "backtest_layer_stat", "signal_count", "", false),
                new FieldMapping("BacktestMetricSummaryVO.validSignalCount", "CLICKHOUSE", "backtest_layer_stat", "valid_signal_count", "", false),
                new FieldMapping("BacktestMetricSummaryVO.failureCaseCount", "CLICKHOUSE", "backtest_layer_stat", "failure_case_count", "", false),
                new FieldMapping("BacktestMetricSummaryVO.future1dAvgReturn", "CLICKHOUSE", "backtest_layer_stat", "future1d_avg_return", "", false),
                new FieldMapping("BacktestMetricSummaryVO.future3dAvgReturn", "CLICKHOUSE", "backtest_layer_stat", "future3d_avg_return", "", false),
                new FieldMapping("BacktestMetricSummaryVO.future5dAvgReturn", "CLICKHOUSE", "backtest_layer_stat", "future5d_avg_return", "", false),
                new FieldMapping("BacktestMetricSummaryVO.maxDrawdown", "CLICKHOUSE", "backtest_layer_stat", "max_drawdown", "", false),
                new FieldMapping("BacktestMetricSummaryVO.winRate", "CLICKHOUSE", "backtest_layer_stat", "win_rate", "", false),
                new FieldMapping("BacktestMetricSummaryVO.riskVetoEffectScore", "CLICKHOUSE", "backtest_layer_stat", "risk_veto_effect_score", "", false),
                new FieldMapping("BacktestMetricCardVO.metricCode", "CLICKHOUSE", "backtest_layer_stat", "metric_code", "", false),
                new FieldMapping("BacktestMetricCardVO.metricName", "CLICKHOUSE", "backtest_layer_stat", "metric_name", "", false),
                new FieldMapping("BacktestMetricCardVO.metricValue", "CLICKHOUSE", "backtest_layer_stat", "metric_value", "", false),
                new FieldMapping("BacktestMetricCardVO.metricLevel", "CLICKHOUSE", "backtest_layer_stat", "metric_level", "", false),
                new FieldMapping("BacktestMetricCardVO.metricText", "CLICKHOUSE", "backtest_layer_stat", "metric_text", "", false),
                new FieldMapping("BacktestEquityCurvePointVO.tradeDate", "CLICKHOUSE", "backtest_layer_stat", "trade_date", "", false),
                new FieldMapping("BacktestEquityCurvePointVO.equityValue", "CLICKHOUSE", "backtest_layer_stat", "equity_value", "", false),
                new FieldMapping("BacktestEquityCurvePointVO.drawdown", "CLICKHOUSE", "backtest_layer_stat", "drawdown", "", false),
                new FieldMapping("BacktestEquityCurvePointVO.pointText", "CLICKHOUSE", "backtest_layer_stat", "point_text", "", false),
                new FieldMapping("BacktestLayerStatVO.layerDimension", "CLICKHOUSE", "backtest_layer_stat", "layer_dimension", "", false),
                new FieldMapping("BacktestLayerStatVO.layerValue", "CLICKHOUSE", "backtest_layer_stat", "layer_value", "", false),
                new FieldMapping("BacktestLayerStatVO.sampleCount", "CLICKHOUSE", "backtest_layer_stat", "sample_count", "", false),
                new FieldMapping("BacktestLayerStatVO.future3dAvgReturn", "CLICKHOUSE", "backtest_layer_stat", "future3d_avg_return", "", false),
                new FieldMapping("BacktestLayerStatVO.maxDrawdown", "CLICKHOUSE", "backtest_layer_stat", "max_drawdown", "", false),
                new FieldMapping("BacktestLayerStatVO.winRate", "CLICKHOUSE", "backtest_layer_stat", "win_rate", "", false),
                new FieldMapping("BacktestLayerStatVO.layerText", "CLICKHOUSE", "backtest_layer_stat", "layer_text", "", false),
                new FieldMapping("BacktestRiskEffectVO.riskVetoCount", "CLICKHOUSE", "backtest_layer_stat", "risk_veto_count", "", false),
                new FieldMapping("BacktestRiskEffectVO.vetoEffectScore", "CLICKHOUSE", "backtest_layer_stat", "veto_effect_score", "", false),
                new FieldMapping("BacktestRiskEffectVO.riskEffectText", "CLICKHOUSE", "backtest_layer_stat", "risk_effect_text", "", false),
                new FieldMapping("BacktestFailureCaseVO.caseId", "CLICKHOUSE", "backtest_layer_stat", "case_id", "", false),
                new FieldMapping("BacktestFailureCaseVO.tradeDate", "CLICKHOUSE", "backtest_layer_stat", "trade_date", "", false),
                new FieldMapping("BacktestFailureCaseVO.stockCode", "CLICKHOUSE", "backtest_layer_stat", "stock_code", "", false),
                new FieldMapping("BacktestFailureCaseVO.failureReason", "CLICKHOUSE", "backtest_layer_stat", "failure_reason", "", false),
                new FieldMapping("BacktestFailureCaseVO.maxDrawdown", "CLICKHOUSE", "backtest_layer_stat", "max_drawdown", "", false),
                new FieldMapping("BacktestFailureCaseVO.caseText", "CLICKHOUSE", "backtest_layer_stat", "case_text", "", false),
                new FieldMapping("BacktestRuleVersionSnapshotVO.ruleVersionId", "CLICKHOUSE", "backtest_layer_stat", "rule_version_id", "", false),
                new FieldMapping("BacktestRuleVersionSnapshotVO.ruleCode", "CLICKHOUSE", "backtest_layer_stat", "rule_code", "", false),
                new FieldMapping("BacktestRuleVersionSnapshotVO.versionNo", "CLICKHOUSE", "backtest_layer_stat", "version_no", "", false),
                new FieldMapping("BacktestRuleVersionSnapshotVO.configSnapshotText", "CLICKHOUSE", "backtest_layer_stat", "config_snapshot_text", "", false),
                new FieldMapping("BacktestRuleVersionDiffVO.baseVersionId", "CLICKHOUSE", "backtest_layer_stat", "base_version_id", "", false),
                new FieldMapping("BacktestRuleVersionDiffVO.compareVersionId", "CLICKHOUSE", "backtest_layer_stat", "compare_version_id", "", false),
                new FieldMapping("BacktestRuleVersionDiffVO.diffText", "CLICKHOUSE", "backtest_layer_stat", "diff_text", "", false),
                new FieldMapping("BacktestDataQualitySummaryVO.dataCheckPassed", "CLICKHOUSE", "backtest_layer_stat", "data_check_passed", "", false),
                new FieldMapping("BacktestDataQualitySummaryVO.completenessRatio", "CLICKHOUSE", "backtest_layer_stat", "completeness_ratio", "", false),
                new FieldMapping("BacktestDataQualitySummaryVO.qualityText", "CLICKHOUSE", "backtest_layer_stat", "quality_text", "", false),
                new FieldMapping("BacktestFutureLeakageSummaryVO.futureLeakageCheckPassed", "CLICKHOUSE", "backtest_layer_stat", "future_leakage_check_passed", "", false),
                new FieldMapping("BacktestFutureLeakageSummaryVO.failedCheckCount", "CLICKHOUSE", "backtest_layer_stat", "failed_check_count", "", false),
                new FieldMapping("BacktestFutureLeakageSummaryVO.summaryText", "CLICKHOUSE", "backtest_layer_stat", "evidence_json", "", false),
                new FieldMapping("BacktestOptimizationAdviceVO.adviceCode", "CLICKHOUSE", "backtest_layer_stat", "advice_code", "", false),
                new FieldMapping("BacktestOptimizationAdviceVO.adviceType", "CLICKHOUSE", "backtest_layer_stat", "advice_type", "", false),
                new FieldMapping("BacktestOptimizationAdviceVO.priority", "CLICKHOUSE", "backtest_layer_stat", "priority", "", false),
                new FieldMapping("BacktestOptimizationAdviceVO.adviceText", "CLICKHOUSE", "backtest_layer_stat", "advice_text", "", false),
                new FieldMapping("BacktestOptimizationAdviceVO.createRuleDraftAllowed", "CLICKHOUSE", "backtest_layer_stat", "create_rule_draft_allowed", "", false)
        );
    }
}
