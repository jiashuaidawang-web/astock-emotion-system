package com.astock.module.backtest.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.backtest.api.vo.BacktestReportDetailVO;
import com.astock.module.backtest.application.query.BacktestReportDetailQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BacktestReportConverter implements PageBundleConverter<BacktestReportDetailQuery, BacktestReportDetailVO> {

    @Override
    public BacktestReportDetailVO convert(BacktestReportDetailQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        BacktestReportDetailVO vo = new BacktestReportDetailVO();

        if (bundle == null || bundle.isEmpty()) {
            vo.setDataComplete(false);
            vo.setDataStatusText("多表Repository未返回真实记录，拒绝Mock补齐。");
            vo.setTradeDate(query.getTradeDate());
            vo.setConclusion("无真实多表快照数据。");
            vo.setRiskTips(List.of("请检查页面专属多表Repository、引擎落库结果和规则版本。"));
            return vo;
        }

        Map<String, Object> primary = bundle.firstRow("backtest_signal_detail");
                vo.setReportId(MapFieldReader.longValue(bundle.firstRow("backtest_signal_detail"), "report_id"));
        vo.setTaskId(MapFieldReader.longValue(bundle.firstRow("backtest_signal_detail"), "task_id"));
        vo.setTradeDate(MapFieldReader.localDate(primary, "trade_date") == null ? query.getTradeDate() : MapFieldReader.localDate(primary, "trade_date"));
        vo.setDataComplete(quality.getDataComplete());
        vo.setDataStatusText(quality.getDataStatusText());
        vo.setHeader(toBacktestReportHeaderVO(bundle.firstRow("backtest_signal_detail"), bundle));
        vo.setTaskParam(toBacktestTaskParamVO(bundle.firstRow("backtest_signal_detail"), bundle));
        vo.setMetricSummary(toBacktestMetricSummaryVO(bundle.firstRow("backtest_layer_stat"), bundle));
        vo.setMetricCards(bundle.rows("backtest_layer_stat").stream().map(r -> toBacktestMetricCardVO(r, bundle)).toList());
        vo.setEquityCurve(bundle.rows("backtest_layer_stat").stream().map(r -> toBacktestEquityCurvePointVO(r, bundle)).toList());
        vo.setLayerStats(bundle.rows("backtest_layer_stat").stream().map(r -> toBacktestLayerStatVO(r, bundle)).toList());
        vo.setRiskEffect(toBacktestRiskEffectVO(bundle.firstRow("backtest_signal_detail"), bundle));
        vo.setFailureCases(bundle.rows("backtest_layer_stat").stream().map(r -> toBacktestFailureCaseVO(r, bundle)).toList());
        vo.setRuleVersionSnapshot(toBacktestRuleVersionSnapshotVO(bundle.firstRow("rule_version"), bundle));
        vo.setRuleVersionDiff(toBacktestRuleVersionDiffVO(bundle.firstRow("rule_version"), bundle));
        vo.setDataQuality(toBacktestDataQualitySummaryVO(bundle.firstRow("backtest_signal_detail"), bundle));
        vo.setFutureLeakageSummary(toBacktestFutureLeakageSummaryVO(bundle.firstRow("backtest_signal_detail"), bundle));
        vo.setOptimizationAdvices(bundle.rows("backtest_layer_stat").stream().map(r -> toBacktestOptimizationAdviceVO(r, bundle)).toList());
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("backtest_signal_detail"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("backtest_signal_detail"), "risk_json"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "BacktestReportHeaderVO" -> "backtest_layer_stat";
            case "BacktestTaskParamVO" -> "backtest_layer_stat";
            case "BacktestMetricSummaryVO" -> "backtest_layer_stat";
            case "BacktestMetricCardVO" -> "backtest_layer_stat";
            case "BacktestEquityCurvePointVO" -> "backtest_layer_stat";
            case "BacktestLayerStatVO" -> "backtest_layer_stat";
            case "BacktestRiskEffectVO" -> "backtest_signal_detail";
            case "BacktestFailureCaseVO" -> "backtest_layer_stat";
            case "BacktestRuleVersionSnapshotVO" -> "backtest_layer_stat";
            case "BacktestRuleVersionDiffVO" -> "backtest_layer_stat";
            case "BacktestDataQualitySummaryVO" -> "backtest_layer_stat";
            case "BacktestFutureLeakageSummaryVO" -> "backtest_layer_stat";
            case "BacktestOptimizationAdviceVO" -> "backtest_layer_stat";
            case "BacktestReportDetailVO" -> "backtest_layer_stat";
            default -> "backtest_signal_detail";
        };
    }

    private List<String> stringList(Map<String, Object> row, String column) {
        String value = MapFieldReader.string(row, column);
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value);
    }


    private BacktestReportDetailVO.BacktestReportHeaderVO toBacktestReportHeaderVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestReportDetailVO.BacktestReportHeaderVO item = new BacktestReportDetailVO.BacktestReportHeaderVO();
        item.setReportId(MapFieldReader.longValue(row, "report_id"));
        item.setReportName(MapFieldReader.string(row, "report_name"));
        item.setReportStatus(MapFieldReader.string(row, "report_status"));
        item.setObjectType(MapFieldReader.string(row, "object_type"));
        item.setCredibilityLevel(MapFieldReader.string(row, "credibility_level"));
        item.setDataCheckPassed(MapFieldReader.bool(row, "data_check_passed"));
        item.setFutureLeakageCheckPassed(MapFieldReader.bool(row, "future_leakage_check_passed"));
        item.setGeneratedAt(MapFieldReader.localDateTime(row, "generated_at"));
        item.setReportSummary(MapFieldReader.string(row, "report_summary"));
        return item;
    }

    private BacktestReportDetailVO.BacktestTaskParamVO toBacktestTaskParamVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestReportDetailVO.BacktestTaskParamVO item = new BacktestReportDetailVO.BacktestTaskParamVO();
        item.setTaskId(MapFieldReader.longValue(row, "task_id"));
        item.setObjectType(MapFieldReader.string(row, "object_type"));
        item.setRuleVersionId(MapFieldReader.longValue(row, "rule_version_id"));
        item.setCompareRuleVersionId(MapFieldReader.longValue(row, "compare_rule_version_id"));
        item.setStartDate(MapFieldReader.localDate(row, "start_date"));
        item.setEndDate(MapFieldReader.localDate(row, "end_date"));
        item.setLayerDimensions(stringList(row, "layer_dimensions"));
        item.setMetrics(stringList(row, "metrics"));
        item.setParamText(MapFieldReader.string(row, "param_text"));
        return item;
    }

    private BacktestReportDetailVO.BacktestMetricSummaryVO toBacktestMetricSummaryVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestReportDetailVO.BacktestMetricSummaryVO item = new BacktestReportDetailVO.BacktestMetricSummaryVO();
        item.setSignalCount(MapFieldReader.integer(row, "signal_count"));
        item.setValidSignalCount(MapFieldReader.integer(row, "valid_signal_count"));
        item.setFailureCaseCount(MapFieldReader.integer(row, "failure_case_count"));
        item.setFuture1dAvgReturn(MapFieldReader.decimal(row, "future1d_avg_return"));
        item.setFuture3dAvgReturn(MapFieldReader.decimal(row, "future3d_avg_return"));
        item.setFuture5dAvgReturn(MapFieldReader.decimal(row, "future5d_avg_return"));
        item.setMaxDrawdown(MapFieldReader.decimal(row, "max_drawdown"));
        item.setWinRate(MapFieldReader.decimal(row, "win_rate"));
        item.setRiskVetoEffectScore(MapFieldReader.decimal(row, "risk_veto_effect_score"));
        return item;
    }

    private BacktestReportDetailVO.BacktestMetricCardVO toBacktestMetricCardVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestReportDetailVO.BacktestMetricCardVO item = new BacktestReportDetailVO.BacktestMetricCardVO();
        item.setMetricCode(MapFieldReader.string(row, "metric_code"));
        item.setMetricName(MapFieldReader.string(row, "metric_name"));
        item.setMetricValue(MapFieldReader.string(row, "metric_value"));
        item.setMetricLevel(MapFieldReader.string(row, "metric_level"));
        item.setMetricText(MapFieldReader.string(row, "metric_text"));
        return item;
    }

    private BacktestReportDetailVO.BacktestEquityCurvePointVO toBacktestEquityCurvePointVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestReportDetailVO.BacktestEquityCurvePointVO item = new BacktestReportDetailVO.BacktestEquityCurvePointVO();
        item.setTradeDate(MapFieldReader.localDate(row, "trade_date"));
        item.setEquityValue(MapFieldReader.decimal(row, "equity_value"));
        item.setDrawdown(MapFieldReader.decimal(row, "drawdown"));
        item.setPointText(MapFieldReader.string(row, "point_text"));
        return item;
    }

    private BacktestReportDetailVO.BacktestLayerStatVO toBacktestLayerStatVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestReportDetailVO.BacktestLayerStatVO item = new BacktestReportDetailVO.BacktestLayerStatVO();
        item.setLayerDimension(MapFieldReader.string(row, "layer_dimension"));
        item.setLayerValue(MapFieldReader.string(row, "layer_value"));
        item.setSampleCount(MapFieldReader.integer(row, "sample_count"));
        item.setFuture3dAvgReturn(MapFieldReader.decimal(row, "future3d_avg_return"));
        item.setMaxDrawdown(MapFieldReader.decimal(row, "max_drawdown"));
        item.setWinRate(MapFieldReader.decimal(row, "win_rate"));
        item.setLayerText(MapFieldReader.string(row, "layer_text"));
        return item;
    }

    private BacktestReportDetailVO.BacktestRiskEffectVO toBacktestRiskEffectVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestReportDetailVO.BacktestRiskEffectVO item = new BacktestReportDetailVO.BacktestRiskEffectVO();
        item.setRiskVetoCount(MapFieldReader.integer(row, "risk_veto_count"));
        item.setVetoEffectScore(MapFieldReader.decimal(row, "veto_effect_score"));
        item.setRiskEffectText(MapFieldReader.string(row, "risk_effect_text"));
        return item;
    }

    private BacktestReportDetailVO.BacktestFailureCaseVO toBacktestFailureCaseVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestReportDetailVO.BacktestFailureCaseVO item = new BacktestReportDetailVO.BacktestFailureCaseVO();
        item.setCaseId(MapFieldReader.longValue(row, "case_id"));
        item.setTradeDate(MapFieldReader.localDate(row, "trade_date"));
        item.setStockCode(MapFieldReader.string(row, "stock_code"));
        item.setFailureReason(MapFieldReader.string(row, "failure_reason"));
        item.setMaxDrawdown(MapFieldReader.decimal(row, "max_drawdown"));
        item.setCaseText(MapFieldReader.string(row, "case_text"));
        return item;
    }

    private BacktestReportDetailVO.BacktestRuleVersionSnapshotVO toBacktestRuleVersionSnapshotVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestReportDetailVO.BacktestRuleVersionSnapshotVO item = new BacktestReportDetailVO.BacktestRuleVersionSnapshotVO();
        item.setRuleVersionId(MapFieldReader.longValue(row, "rule_version_id"));
        item.setRuleCode(MapFieldReader.string(row, "rule_code"));
        item.setVersionNo(MapFieldReader.string(row, "version_no"));
        item.setConfigSnapshotText(MapFieldReader.string(row, "config_snapshot_text"));
        return item;
    }

    private BacktestReportDetailVO.BacktestRuleVersionDiffVO toBacktestRuleVersionDiffVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestReportDetailVO.BacktestRuleVersionDiffVO item = new BacktestReportDetailVO.BacktestRuleVersionDiffVO();
        item.setBaseVersionId(MapFieldReader.longValue(row, "base_version_id"));
        item.setCompareVersionId(MapFieldReader.longValue(row, "compare_version_id"));
        item.setDiffText(MapFieldReader.string(row, "diff_text"));
        return item;
    }

    private BacktestReportDetailVO.BacktestDataQualitySummaryVO toBacktestDataQualitySummaryVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestReportDetailVO.BacktestDataQualitySummaryVO item = new BacktestReportDetailVO.BacktestDataQualitySummaryVO();
        item.setDataCheckPassed(MapFieldReader.bool(row, "data_check_passed"));
        item.setCompletenessRatio(MapFieldReader.decimal(row, "completeness_ratio"));
        item.setQualityText(MapFieldReader.string(row, "quality_text"));
        return item;
    }

    private BacktestReportDetailVO.BacktestFutureLeakageSummaryVO toBacktestFutureLeakageSummaryVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestReportDetailVO.BacktestFutureLeakageSummaryVO item = new BacktestReportDetailVO.BacktestFutureLeakageSummaryVO();
        item.setFutureLeakageCheckPassed(MapFieldReader.bool(row, "future_leakage_check_passed"));
        item.setFailedCheckCount(MapFieldReader.integer(row, "failed_check_count"));
        item.setSummaryText(MapFieldReader.string(row, "evidence_json"));
        return item;
    }

    private BacktestReportDetailVO.BacktestOptimizationAdviceVO toBacktestOptimizationAdviceVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestReportDetailVO.BacktestOptimizationAdviceVO item = new BacktestReportDetailVO.BacktestOptimizationAdviceVO();
        item.setAdviceCode(MapFieldReader.string(row, "advice_code"));
        item.setAdviceType(MapFieldReader.string(row, "advice_type"));
        item.setPriority(MapFieldReader.string(row, "priority"));
        item.setAdviceText(MapFieldReader.string(row, "advice_text"));
        item.setCreateRuleDraftAllowed(MapFieldReader.bool(row, "create_rule_draft_allowed"));
        return item;
    }

}
