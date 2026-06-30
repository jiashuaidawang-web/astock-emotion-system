package com.astock.module.backtest.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.backtest.api.vo.BacktestLabPageVO;
import com.astock.module.backtest.application.query.BacktestLabPageQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BacktestLabConverter implements PageBundleConverter<BacktestLabPageQuery, BacktestLabPageVO> {

    @Override
    public BacktestLabPageVO convert(BacktestLabPageQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        BacktestLabPageVO vo = new BacktestLabPageVO();

        if (bundle == null || bundle.isEmpty()) {
            vo.setDataComplete(false);
            vo.setDataStatusText("多表Repository未返回真实记录，拒绝Mock补齐。");
            vo.setTradeDate(query.getTradeDate());
            vo.setConclusion("无真实多表快照数据。");
            vo.setRiskTips(List.of("请检查页面专属多表Repository、引擎落库结果和规则版本。"));
            return vo;
        }

        Map<String, Object> primary = bundle.firstRow("backtest_signal_detail");
                vo.setDefaultStartDate(MapFieldReader.localDate(bundle.firstRow("backtest_signal_detail"), "default_start_date"));
        vo.setDefaultEndDate(MapFieldReader.localDate(bundle.firstRow("backtest_signal_detail"), "default_end_date"));
        vo.setTradeDate(MapFieldReader.localDate(primary, "trade_date") == null ? query.getTradeDate() : MapFieldReader.localDate(primary, "trade_date"));
        vo.setDataComplete(quality.getDataComplete());
        vo.setDataStatusText(quality.getDataStatusText());
        vo.setOverview(toBacktestLabOverviewVO(bundle.firstRow("backtest_signal_detail"), bundle));
        vo.setCreateConfig(toBacktestTaskCreateConfigVO(bundle.firstRow("backtest_signal_detail"), bundle));
        vo.setSupportedObjectTypes(bundle.rows("backtest_layer_stat").stream().map(r -> toBacktestObjectTypeVO(r, bundle)).toList());
        vo.setRuleVersions(bundle.rows("rule_version").stream().map(r -> toRuleVersionBriefVO(r, bundle)).toList());
        vo.setRecentTasks(bundle.rows("backtest_layer_stat").stream().map(r -> toBacktestTaskVO(r, bundle)).toList());
        vo.setRunningTasks(bundle.rows("backtest_layer_stat").stream().map(r -> toBacktestTaskVO(r, bundle)).toList());
        vo.setDataCoverages(bundle.rows("backtest_layer_stat").stream().map(r -> toBacktestDataCoverageVO(r, bundle)).toList());
        vo.setFutureLeakageChecks(bundle.rows("backtest_signal_detail").stream().map(r -> toFutureLeakageCheckVO(r, bundle)).toList());
        vo.setPresetTemplates(bundle.rows("backtest_layer_stat").stream().map(r -> toBacktestPresetTemplateVO(r, bundle)).toList());
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("backtest_signal_detail"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("backtest_signal_detail"), "risk_json"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "BacktestLabOverviewVO" -> "backtest_layer_stat";
            case "BacktestTaskCreateConfigVO" -> "backtest_layer_stat";
            case "BacktestObjectTypeVO" -> "backtest_layer_stat";
            case "RuleVersionBriefVO" -> "rule_version";
            case "BacktestTaskVO" -> "backtest_layer_stat";
            case "BacktestDataCoverageVO" -> "backtest_layer_stat";
            case "FutureLeakageCheckVO" -> "backtest_signal_detail";
            case "BacktestPresetTemplateVO" -> "backtest_layer_stat";
            case "BacktestLabPageVO" -> "backtest_layer_stat";
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


    private BacktestLabPageVO.BacktestLabOverviewVO toBacktestLabOverviewVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestLabPageVO.BacktestLabOverviewVO item = new BacktestLabPageVO.BacktestLabOverviewVO();
        item.setTotalTaskCount(MapFieldReader.integer(row, "total_task_count"));
        item.setRunningTaskCount(MapFieldReader.integer(row, "running_task_count"));
        item.setSuccessTaskCount(MapFieldReader.integer(row, "success_task_count"));
        item.setFailedTaskCount(MapFieldReader.integer(row, "failed_task_count"));
        item.setLatestSuccessTaskId(MapFieldReader.longValue(row, "latest_success_task_id"));
        item.setLatestSuccessReportId(MapFieldReader.longValue(row, "latest_success_report_id"));
        item.setDataCoverageStartDate(MapFieldReader.localDate(row, "data_coverage_start_date"));
        item.setDataCoverageEndDate(MapFieldReader.localDate(row, "data_coverage_end_date"));
        item.setOverviewText(MapFieldReader.string(row, "features"));
        return item;
    }

    private BacktestLabPageVO.BacktestTaskCreateConfigVO toBacktestTaskCreateConfigVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestLabPageVO.BacktestTaskCreateConfigVO item = new BacktestLabPageVO.BacktestTaskCreateConfigVO();
        item.setCreateAllowed(MapFieldReader.bool(row, "create_allowed"));
        item.setDisabledReason(MapFieldReader.string(row, "disabled_reason"));
        item.setDefaultTaskName(MapFieldReader.string(row, "default_task_name"));
        item.setDefaultObjectType(MapFieldReader.string(row, "default_object_type"));
        item.setDefaultRuleVersionId(MapFieldReader.longValue(row, "default_rule_version_id"));
        item.setDefaultStartDate(MapFieldReader.localDate(row, "default_start_date"));
        item.setDefaultEndDate(MapFieldReader.localDate(row, "default_end_date"));
        item.setDefaultFutureLeakageCheckEnabled(MapFieldReader.bool(row, "default_future_leakage_check_enabled"));
        item.setDefaultSaveFailureCases(MapFieldReader.bool(row, "default_save_failure_cases"));
        item.setDefaultGenerateReport(MapFieldReader.bool(row, "default_generate_report"));
        item.setConfigText(MapFieldReader.string(row, "config_text"));
        return item;
    }

    private BacktestLabPageVO.BacktestObjectTypeVO toBacktestObjectTypeVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestLabPageVO.BacktestObjectTypeVO item = new BacktestLabPageVO.BacktestObjectTypeVO();
        item.setObjectType(MapFieldReader.string(row, "object_type"));
        item.setObjectTypeName(MapFieldReader.string(row, "object_type_name"));
        item.setEnabled(MapFieldReader.bool(row, "enabled"));
        item.setDisabledReason(MapFieldReader.string(row, "disabled_reason"));
        item.setRequireCycleSample(MapFieldReader.bool(row, "require_cycle_sample"));
        item.setRequirePatternSignalSnapshot(MapFieldReader.bool(row, "require_pattern_signal_snapshot"));
        item.setRequireRiskSignalSnapshot(MapFieldReader.bool(row, "require_risk_signal_snapshot"));
        item.setSupportedLayerDimensions(stringList(row, "supported_layer_dimensions"));
        item.setSupportedMetrics(stringList(row, "supported_metrics"));
        return item;
    }

    private BacktestLabPageVO.RuleVersionBriefVO toRuleVersionBriefVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestLabPageVO.RuleVersionBriefVO item = new BacktestLabPageVO.RuleVersionBriefVO();
        item.setRuleVersionId(MapFieldReader.longValue(row, "rule_version_id"));
        item.setRuleCode(MapFieldReader.string(row, "rule_code"));
        item.setRuleName(MapFieldReader.string(row, "rule_name"));
        item.setRuleType(MapFieldReader.string(row, "rule_type"));
        item.setVersionNo(MapFieldReader.string(row, "version_no"));
        item.setVersionName(MapFieldReader.string(row, "version_name"));
        item.setActive(MapFieldReader.bool(row, "active"));
        item.setBacktestEnabled(MapFieldReader.bool(row, "backtest_enabled"));
        item.setLatestBacktestStatus(MapFieldReader.string(row, "latest_backtest_status"));
        return item;
    }

    private BacktestLabPageVO.BacktestTaskVO toBacktestTaskVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestLabPageVO.BacktestTaskVO item = new BacktestLabPageVO.BacktestTaskVO();
        item.setTaskId(MapFieldReader.longValue(row, "task_id"));
        item.setTaskName(MapFieldReader.string(row, "task_name"));
        item.setObjectType(MapFieldReader.string(row, "object_type"));
        item.setRuleVersionId(MapFieldReader.longValue(row, "rule_version_id"));
        item.setStartDate(MapFieldReader.localDate(row, "start_date"));
        item.setEndDate(MapFieldReader.localDate(row, "end_date"));
        item.setSampleCount(MapFieldReader.integer(row, "sample_count"));
        item.setTaskStatus(MapFieldReader.string(row, "task_status"));
        item.setProgressPercent(MapFieldReader.decimal(row, "progress_percent"));
        item.setFutureLeakageCheckPassed(MapFieldReader.bool(row, "future_leakage_check_passed"));
        item.setDataCheckPassed(MapFieldReader.bool(row, "data_check_passed"));
        item.setReportGenerated(MapFieldReader.bool(row, "report_generated"));
        item.setReportId(MapFieldReader.longValue(row, "report_id"));
        item.setTaskSummary(MapFieldReader.string(row, "task_summary"));
        return item;
    }

    private BacktestLabPageVO.BacktestDataCoverageVO toBacktestDataCoverageVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestLabPageVO.BacktestDataCoverageVO item = new BacktestLabPageVO.BacktestDataCoverageVO();
        item.setDataDomain(MapFieldReader.string(row, "data_domain"));
        item.setCoverageStartDate(MapFieldReader.localDate(row, "coverage_start_date"));
        item.setCoverageEndDate(MapFieldReader.localDate(row, "coverage_end_date"));
        item.setExpectedTradeDayCount(MapFieldReader.integer(row, "expected_trade_day_count"));
        item.setActualTradeDayCount(MapFieldReader.integer(row, "actual_trade_day_count"));
        item.setCompletenessRatio(MapFieldReader.decimal(row, "completeness_ratio"));
        item.setBacktestAvailable(MapFieldReader.bool(row, "backtest_available"));
        item.setCoverageText(MapFieldReader.string(row, "coverage_text"));
        return item;
    }

    private BacktestLabPageVO.FutureLeakageCheckVO toFutureLeakageCheckVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestLabPageVO.FutureLeakageCheckVO item = new BacktestLabPageVO.FutureLeakageCheckVO();
        item.setCheckCode(MapFieldReader.string(row, "check_code"));
        item.setCheckName(MapFieldReader.string(row, "check_name"));
        item.setPassed(MapFieldReader.bool(row, "passed"));
        item.setRiskLevel(MapFieldReader.string(row, "risk_level"));
        item.setRelatedField(MapFieldReader.string(row, "related_field"));
        item.setRelatedTable(MapFieldReader.string(row, "related_table"));
        item.setFailureReason(MapFieldReader.string(row, "failure_reason"));
        item.setFixSuggestion(MapFieldReader.string(row, "fix_suggestion"));
        return item;
    }

    private BacktestLabPageVO.BacktestPresetTemplateVO toBacktestPresetTemplateVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                BacktestLabPageVO.BacktestPresetTemplateVO item = new BacktestLabPageVO.BacktestPresetTemplateVO();
        item.setTemplateId(MapFieldReader.longValue(row, "template_id"));
        item.setTemplateCode(MapFieldReader.string(row, "template_code"));
        item.setTemplateName(MapFieldReader.string(row, "template_name"));
        item.setObjectType(MapFieldReader.string(row, "object_type"));
        item.setDefaultRuleVersionId(MapFieldReader.longValue(row, "default_rule_version_id"));
        item.setSystemBuiltIn(MapFieldReader.bool(row, "system_built_in"));
        item.setTemplateText(MapFieldReader.string(row, "template_text"));
        return item;
    }

}
