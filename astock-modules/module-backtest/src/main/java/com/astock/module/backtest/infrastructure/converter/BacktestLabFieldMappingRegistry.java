package com.astock.module.backtest.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BacktestLabFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_11_BACKTEST_LAB";
    }

    @Override
    public String voClassName() {
        return "BacktestLabPageVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("BacktestLabPageVO.defaultStartDate", "CLICKHOUSE", "backtest_layer_stat", "default_start_date", "", false),
                new FieldMapping("BacktestLabPageVO.defaultEndDate", "CLICKHOUSE", "backtest_layer_stat", "default_end_date", "", false),
                new FieldMapping("BacktestLabPageVO.tradeDate", "CLICKHOUSE", "backtest_layer_stat", "trade_date", "", false),
                new FieldMapping("BacktestLabPageVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("BacktestLabPageVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("BacktestLabPageVO.overview", "CLICKHOUSE", "backtest_layer_stat", "overview", "", false),
                new FieldMapping("BacktestLabPageVO.createConfig", "CLICKHOUSE", "backtest_layer_stat", "create_config", "", false),
                new FieldMapping("BacktestLabPageVO.supportedObjectTypes", "CLICKHOUSE", "backtest_layer_stat", "supported_object_types", "", false),
                new FieldMapping("BacktestLabPageVO.ruleVersions", "CLICKHOUSE", "backtest_layer_stat", "rule_versions", "", false),
                new FieldMapping("BacktestLabPageVO.recentTasks", "CLICKHOUSE", "backtest_layer_stat", "recent_tasks", "", false),
                new FieldMapping("BacktestLabPageVO.runningTasks", "CLICKHOUSE", "backtest_layer_stat", "running_tasks", "", false),
                new FieldMapping("BacktestLabPageVO.dataCoverages", "CLICKHOUSE", "backtest_layer_stat", "data_coverages", "", false),
                new FieldMapping("BacktestLabPageVO.futureLeakageChecks", "CLICKHOUSE", "backtest_layer_stat", "future_leakage_checks", "", false),
                new FieldMapping("BacktestLabPageVO.presetTemplates", "CLICKHOUSE", "backtest_layer_stat", "preset_templates", "", false),
                new FieldMapping("BacktestLabPageVO.conclusion", "CLICKHOUSE", "backtest_layer_stat", "evidence_json", "", false),
                new FieldMapping("BacktestLabPageVO.riskTips", "CLICKHOUSE", "backtest_layer_stat", "risk_json", "", false),
                new FieldMapping("BacktestLabOverviewVO.totalTaskCount", "CLICKHOUSE", "backtest_layer_stat", "total_task_count", "", false),
                new FieldMapping("BacktestLabOverviewVO.runningTaskCount", "CLICKHOUSE", "backtest_layer_stat", "running_task_count", "", false),
                new FieldMapping("BacktestLabOverviewVO.successTaskCount", "CLICKHOUSE", "backtest_layer_stat", "success_task_count", "", false),
                new FieldMapping("BacktestLabOverviewVO.failedTaskCount", "CLICKHOUSE", "backtest_layer_stat", "failed_task_count", "", false),
                new FieldMapping("BacktestLabOverviewVO.latestSuccessTaskId", "CLICKHOUSE", "backtest_layer_stat", "latest_success_task_id", "", false),
                new FieldMapping("BacktestLabOverviewVO.latestSuccessReportId", "CLICKHOUSE", "backtest_layer_stat", "latest_success_report_id", "", false),
                new FieldMapping("BacktestLabOverviewVO.dataCoverageStartDate", "CLICKHOUSE", "backtest_layer_stat", "data_coverage_start_date", "", false),
                new FieldMapping("BacktestLabOverviewVO.dataCoverageEndDate", "CLICKHOUSE", "backtest_layer_stat", "data_coverage_end_date", "", false),
                new FieldMapping("BacktestLabOverviewVO.overviewText", "CLICKHOUSE", "backtest_layer_stat", "features", "", false),
                new FieldMapping("BacktestTaskCreateConfigVO.createAllowed", "CLICKHOUSE", "backtest_layer_stat", "create_allowed", "", false),
                new FieldMapping("BacktestTaskCreateConfigVO.disabledReason", "CLICKHOUSE", "backtest_layer_stat", "disabled_reason", "", false),
                new FieldMapping("BacktestTaskCreateConfigVO.defaultTaskName", "CLICKHOUSE", "backtest_layer_stat", "default_task_name", "", false),
                new FieldMapping("BacktestTaskCreateConfigVO.defaultObjectType", "CLICKHOUSE", "backtest_layer_stat", "default_object_type", "", false),
                new FieldMapping("BacktestTaskCreateConfigVO.defaultRuleVersionId", "CLICKHOUSE", "backtest_layer_stat", "default_rule_version_id", "", false),
                new FieldMapping("BacktestTaskCreateConfigVO.defaultStartDate", "CLICKHOUSE", "backtest_layer_stat", "default_start_date", "", false),
                new FieldMapping("BacktestTaskCreateConfigVO.defaultEndDate", "CLICKHOUSE", "backtest_layer_stat", "default_end_date", "", false),
                new FieldMapping("BacktestTaskCreateConfigVO.defaultFutureLeakageCheckEnabled", "CLICKHOUSE", "backtest_layer_stat", "default_future_leakage_check_enabled", "", false),
                new FieldMapping("BacktestTaskCreateConfigVO.defaultSaveFailureCases", "CLICKHOUSE", "backtest_layer_stat", "default_save_failure_cases", "", false),
                new FieldMapping("BacktestTaskCreateConfigVO.defaultGenerateReport", "CLICKHOUSE", "backtest_layer_stat", "default_generate_report", "", false),
                new FieldMapping("BacktestTaskCreateConfigVO.configText", "CLICKHOUSE", "backtest_layer_stat", "config_text", "", false),
                new FieldMapping("BacktestObjectTypeVO.objectType", "CLICKHOUSE", "backtest_layer_stat", "object_type", "", false),
                new FieldMapping("BacktestObjectTypeVO.objectTypeName", "CLICKHOUSE", "backtest_layer_stat", "object_type_name", "", false),
                new FieldMapping("BacktestObjectTypeVO.enabled", "CLICKHOUSE", "backtest_layer_stat", "enabled", "", false),
                new FieldMapping("BacktestObjectTypeVO.disabledReason", "CLICKHOUSE", "backtest_layer_stat", "disabled_reason", "", false),
                new FieldMapping("BacktestObjectTypeVO.requireCycleSample", "CLICKHOUSE", "backtest_layer_stat", "require_cycle_sample", "", false),
                new FieldMapping("BacktestObjectTypeVO.requirePatternSignalSnapshot", "CLICKHOUSE", "backtest_layer_stat", "require_pattern_signal_snapshot", "", false),
                new FieldMapping("BacktestObjectTypeVO.requireRiskSignalSnapshot", "CLICKHOUSE", "backtest_layer_stat", "require_risk_signal_snapshot", "", false),
                new FieldMapping("BacktestObjectTypeVO.supportedLayerDimensions", "CLICKHOUSE", "backtest_layer_stat", "supported_layer_dimensions", "", false),
                new FieldMapping("BacktestObjectTypeVO.supportedMetrics", "CLICKHOUSE", "backtest_layer_stat", "supported_metrics", "", false),
                new FieldMapping("RuleVersionBriefVO.ruleVersionId", "CLICKHOUSE", "backtest_layer_stat", "rule_version_id", "", false),
                new FieldMapping("RuleVersionBriefVO.ruleCode", "CLICKHOUSE", "backtest_layer_stat", "rule_code", "", false),
                new FieldMapping("RuleVersionBriefVO.ruleName", "CLICKHOUSE", "backtest_layer_stat", "rule_name", "", false),
                new FieldMapping("RuleVersionBriefVO.ruleType", "CLICKHOUSE", "backtest_layer_stat", "rule_type", "", false),
                new FieldMapping("RuleVersionBriefVO.versionNo", "CLICKHOUSE", "backtest_layer_stat", "version_no", "", false),
                new FieldMapping("RuleVersionBriefVO.versionName", "CLICKHOUSE", "backtest_layer_stat", "version_name", "", false),
                new FieldMapping("RuleVersionBriefVO.active", "CLICKHOUSE", "backtest_layer_stat", "active", "", false),
                new FieldMapping("RuleVersionBriefVO.backtestEnabled", "CLICKHOUSE", "backtest_layer_stat", "backtest_enabled", "", false),
                new FieldMapping("RuleVersionBriefVO.latestBacktestStatus", "CLICKHOUSE", "backtest_layer_stat", "latest_backtest_status", "", false),
                new FieldMapping("BacktestTaskVO.taskId", "CLICKHOUSE", "backtest_layer_stat", "task_id", "", false),
                new FieldMapping("BacktestTaskVO.taskName", "CLICKHOUSE", "backtest_layer_stat", "task_name", "", false),
                new FieldMapping("BacktestTaskVO.objectType", "CLICKHOUSE", "backtest_layer_stat", "object_type", "", false),
                new FieldMapping("BacktestTaskVO.ruleVersionId", "CLICKHOUSE", "backtest_layer_stat", "rule_version_id", "", false),
                new FieldMapping("BacktestTaskVO.startDate", "CLICKHOUSE", "backtest_layer_stat", "start_date", "", false),
                new FieldMapping("BacktestTaskVO.endDate", "CLICKHOUSE", "backtest_layer_stat", "end_date", "", false),
                new FieldMapping("BacktestTaskVO.sampleCount", "CLICKHOUSE", "backtest_layer_stat", "sample_count", "", false),
                new FieldMapping("BacktestTaskVO.taskStatus", "CLICKHOUSE", "backtest_layer_stat", "task_status", "", false),
                new FieldMapping("BacktestTaskVO.progressPercent", "CLICKHOUSE", "backtest_layer_stat", "progress_percent", "", false),
                new FieldMapping("BacktestTaskVO.futureLeakageCheckPassed", "CLICKHOUSE", "backtest_layer_stat", "future_leakage_check_passed", "", false),
                new FieldMapping("BacktestTaskVO.dataCheckPassed", "CLICKHOUSE", "backtest_layer_stat", "data_check_passed", "", false),
                new FieldMapping("BacktestTaskVO.reportGenerated", "CLICKHOUSE", "backtest_layer_stat", "report_generated", "", false),
                new FieldMapping("BacktestTaskVO.reportId", "CLICKHOUSE", "backtest_layer_stat", "report_id", "", false),
                new FieldMapping("BacktestTaskVO.taskSummary", "CLICKHOUSE", "backtest_layer_stat", "task_summary", "", false),
                new FieldMapping("BacktestDataCoverageVO.dataDomain", "CLICKHOUSE", "backtest_layer_stat", "data_domain", "", false),
                new FieldMapping("BacktestDataCoverageVO.coverageStartDate", "CLICKHOUSE", "backtest_layer_stat", "coverage_start_date", "", false),
                new FieldMapping("BacktestDataCoverageVO.coverageEndDate", "CLICKHOUSE", "backtest_layer_stat", "coverage_end_date", "", false),
                new FieldMapping("BacktestDataCoverageVO.expectedTradeDayCount", "CLICKHOUSE", "backtest_layer_stat", "expected_trade_day_count", "", false),
                new FieldMapping("BacktestDataCoverageVO.actualTradeDayCount", "CLICKHOUSE", "backtest_layer_stat", "actual_trade_day_count", "", false),
                new FieldMapping("BacktestDataCoverageVO.completenessRatio", "CLICKHOUSE", "backtest_layer_stat", "completeness_ratio", "", false),
                new FieldMapping("BacktestDataCoverageVO.backtestAvailable", "CLICKHOUSE", "backtest_layer_stat", "backtest_available", "", false),
                new FieldMapping("BacktestDataCoverageVO.coverageText", "CLICKHOUSE", "backtest_layer_stat", "coverage_text", "", false),
                new FieldMapping("FutureLeakageCheckVO.checkCode", "CLICKHOUSE", "backtest_layer_stat", "check_code", "", false),
                new FieldMapping("FutureLeakageCheckVO.checkName", "CLICKHOUSE", "backtest_layer_stat", "check_name", "", false),
                new FieldMapping("FutureLeakageCheckVO.passed", "CLICKHOUSE", "backtest_layer_stat", "passed", "", false),
                new FieldMapping("FutureLeakageCheckVO.riskLevel", "CLICKHOUSE", "backtest_layer_stat", "risk_level", "", false),
                new FieldMapping("FutureLeakageCheckVO.relatedField", "CLICKHOUSE", "backtest_layer_stat", "related_field", "", false),
                new FieldMapping("FutureLeakageCheckVO.relatedTable", "CLICKHOUSE", "backtest_layer_stat", "related_table", "", false),
                new FieldMapping("FutureLeakageCheckVO.failureReason", "CLICKHOUSE", "backtest_layer_stat", "failure_reason", "", false),
                new FieldMapping("FutureLeakageCheckVO.fixSuggestion", "CLICKHOUSE", "backtest_layer_stat", "fix_suggestion", "", false),
                new FieldMapping("BacktestPresetTemplateVO.templateId", "CLICKHOUSE", "backtest_layer_stat", "template_id", "", false),
                new FieldMapping("BacktestPresetTemplateVO.templateCode", "CLICKHOUSE", "backtest_layer_stat", "template_code", "", false),
                new FieldMapping("BacktestPresetTemplateVO.templateName", "CLICKHOUSE", "backtest_layer_stat", "template_name", "", false),
                new FieldMapping("BacktestPresetTemplateVO.objectType", "CLICKHOUSE", "backtest_layer_stat", "object_type", "", false),
                new FieldMapping("BacktestPresetTemplateVO.defaultRuleVersionId", "CLICKHOUSE", "backtest_layer_stat", "default_rule_version_id", "", false),
                new FieldMapping("BacktestPresetTemplateVO.systemBuiltIn", "CLICKHOUSE", "backtest_layer_stat", "system_built_in", "", false),
                new FieldMapping("BacktestPresetTemplateVO.templateText", "CLICKHOUSE", "backtest_layer_stat", "template_text", "", false)
        );
    }
}
