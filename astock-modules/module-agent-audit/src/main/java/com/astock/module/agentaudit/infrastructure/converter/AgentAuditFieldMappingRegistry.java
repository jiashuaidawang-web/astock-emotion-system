package com.astock.module.agentaudit.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgentAuditFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_15_AGENT_AUDIT";
    }

    @Override
    public String voClassName() {
        return "AgentAuditDashboardVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("AgentAuditDashboardVO.auditDate", "MYSQL", "agent_audit_task", "created_at", "", false),
                new FieldMapping("AgentAuditDashboardVO.tradeDate", "MYSQL", "agent_audit_task", "trade_date", "", false),
                new FieldMapping("AgentAuditDashboardVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("AgentAuditDashboardVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("AgentAuditDashboardVO.overview", "MYSQL", "agent_audit_task", "overview", "", false),
                new FieldMapping("AgentAuditDashboardVO.ruleVersionSummary", "MYSQL", "agent_audit_task", "rule_version_summary", "", false),
                new FieldMapping("AgentAuditDashboardVO.recentAuditTasks", "MYSQL", "agent_audit_task", "recent_audit_tasks", "", false),
                new FieldMapping("AgentAuditDashboardVO.pageAuditMatrix", "MYSQL", "agent_audit_task", "page_audit_matrix", "", false),
                new FieldMapping("AgentAuditDashboardVO.moduleAuditStats", "MYSQL", "agent_audit_task", "module_audit_stats", "", false),
                new FieldMapping("AgentAuditDashboardVO.redLineIssues", "MYSQL", "agent_audit_task", "red_line_issues", "", false),
                new FieldMapping("AgentAuditDashboardVO.releaseBlockerIssues", "MYSQL", "agent_audit_task", "release_blocker_issues", "", false),
                new FieldMapping("AgentAuditDashboardVO.resolvedIssues", "MYSQL", "agent_audit_task", "resolved_issues", "", false),
                new FieldMapping("AgentAuditDashboardVO.ruleCoverages", "MYSQL", "agent_audit_task", "rule_coverages", "", false),
                new FieldMapping("AgentAuditDashboardVO.releaseGateChecks", "MYSQL", "agent_audit_task", "release_gate_checks", "", false),
                new FieldMapping("AgentAuditDashboardVO.fixSuggestions", "MYSQL", "agent_audit_task", "fix_suggestions", "", false),
                new FieldMapping("AgentAuditDashboardVO.conclusion", "MYSQL", "agent_audit_task", "evidence_json", "", false),
                new FieldMapping("AgentAuditDashboardVO.riskTips", "MYSQL", "agent_audit_task", "risk_json", "", false),
                new FieldMapping("AgentAuditOverviewVO.totalAuditTaskCount", "MYSQL", "agent_audit_task", "total_audit_task_count", "", false),
                new FieldMapping("AgentAuditOverviewVO.passedTaskCount", "MYSQL", "agent_audit_task", "passed_task_count", "", false),
                new FieldMapping("AgentAuditOverviewVO.failedTaskCount", "MYSQL", "agent_audit_task", "failed_task_count", "", false),
                new FieldMapping("AgentAuditOverviewVO.blockerIssueCount", "MYSQL", "agent_audit_task", "blocker_issue_count", "", false),
                new FieldMapping("AgentAuditOverviewVO.fatalIssueCount", "MYSQL", "agent_audit_task", "fatal_issue_count", "", false),
                new FieldMapping("AgentAuditOverviewVO.releaseAllowed", "MYSQL", "agent_audit_task", "release_allowed", "", false),
                new FieldMapping("AgentAuditOverviewVO.mergeAllowed", "MYSQL", "agent_audit_task", "merge_allowed", "", false),
                new FieldMapping("AgentAuditOverviewVO.auditPassRate", "MYSQL", "agent_audit_task", "audit_pass_rate", "", false),
                new FieldMapping("AgentAuditOverviewVO.overviewText", "MYSQL", "agent_audit_task", "features", "", false),
                new FieldMapping("AgentAuditRuleVersionSummaryVO.auditRuleVersionId", "MYSQL", "agent_audit_task", "audit_rule_version_id", "", false),
                new FieldMapping("AgentAuditRuleVersionSummaryVO.versionNo", "MYSQL", "agent_audit_task", "version_no", "", false),
                new FieldMapping("AgentAuditRuleVersionSummaryVO.versionName", "MYSQL", "agent_audit_task", "version_name", "", false),
                new FieldMapping("AgentAuditRuleVersionSummaryVO.active", "MYSQL", "agent_audit_task", "active", "", false),
                new FieldMapping("AgentAuditRuleVersionSummaryVO.auditRuleCount", "MYSQL", "agent_audit_task", "audit_rule_count", "", false),
                new FieldMapping("AgentAuditRuleVersionSummaryVO.redLineRuleCount", "MYSQL", "agent_audit_task", "red_line_rule_count", "", false),
                new FieldMapping("AgentAuditRuleVersionSummaryVO.requireBacktestCheck", "MYSQL", "agent_audit_task", "require_backtest_check", "", false),
                new FieldMapping("AgentAuditRuleVersionSummaryVO.requireFutureLeakageCheck", "MYSQL", "agent_audit_task", "require_future_leakage_check", "", false),
                new FieldMapping("AgentAuditRuleVersionSummaryVO.requireTradingInstructionCheck", "MYSQL", "agent_audit_task", "require_trading_instruction_check", "", false),
                new FieldMapping("AgentAuditTaskVO.auditTaskId", "MYSQL", "agent_audit_task", "audit_task_id", "", false),
                new FieldMapping("AgentAuditTaskVO.taskName", "MYSQL", "agent_audit_task", "task_name", "", false),
                new FieldMapping("AgentAuditTaskVO.auditObjectType", "MYSQL", "agent_audit_task", "audit_object_type", "", false),
                new FieldMapping("AgentAuditTaskVO.auditObjectName", "MYSQL", "agent_audit_task", "audit_object_name", "", false),
                new FieldMapping("AgentAuditTaskVO.pageCode", "MYSQL", "agent_audit_task", "page_code", "", false),
                new FieldMapping("AgentAuditTaskVO.moduleName", "MYSQL", "agent_audit_task", "module_name", "", false),
                new FieldMapping("AgentAuditTaskVO.auditStatus", "MYSQL", "agent_audit_task", "audit_status", "", false),
                new FieldMapping("AgentAuditTaskVO.issueCount", "MYSQL", "agent_audit_task", "issue_count", "", false),
                new FieldMapping("AgentAuditTaskVO.blockerIssueCount", "MYSQL", "agent_audit_task", "blocker_issue_count", "", false),
                new FieldMapping("AgentAuditTaskVO.mergeAllowed", "MYSQL", "agent_audit_task", "merge_allowed", "", false),
                new FieldMapping("AgentAuditTaskVO.releaseAllowed", "MYSQL", "agent_audit_task", "release_allowed", "", false),
                new FieldMapping("AgentAuditTaskVO.auditSummary", "MYSQL", "agent_audit_task", "audit_summary", "", false),
                new FieldMapping("PageAuditMatrixVO.pageCode", "MYSQL", "agent_audit_task", "page_code", "", false),
                new FieldMapping("PageAuditMatrixVO.pageName", "MYSQL", "agent_audit_task", "page_name", "", false),
                new FieldMapping("PageAuditMatrixVO.pageVoDefined", "MYSQL", "agent_audit_task", "page_vo_defined", "", false),
                new FieldMapping("PageAuditMatrixVO.apiContractDefined", "MYSQL", "agent_audit_task", "api_contract_defined", "", false),
                new FieldMapping("PageAuditMatrixVO.aggregatorDefined", "MYSQL", "agent_audit_task", "aggregator_defined", "", false),
                new FieldMapping("PageAuditMatrixVO.mysqlMappingDefined", "MYSQL", "agent_audit_task", "mysql_mapping_defined", "", false),
                new FieldMapping("PageAuditMatrixVO.clickhouseMappingDefined", "MYSQL", "agent_audit_task", "clickhouse_mapping_defined", "", false),
                new FieldMapping("PageAuditMatrixVO.auditPassed", "MYSQL", "agent_audit_task", "audit_passed", "", false),
                new FieldMapping("PageAuditMatrixVO.blockerIssueCount", "MYSQL", "agent_audit_task", "blocker_issue_count", "", false),
                new FieldMapping("PageAuditMatrixVO.auditText", "MYSQL", "agent_audit_task", "audit_text", "", false),
                new FieldMapping("ModuleAuditStatVO.moduleName", "MYSQL", "agent_audit_task", "module_name", "", false),
                new FieldMapping("ModuleAuditStatVO.auditTaskCount", "MYSQL", "agent_audit_task", "audit_task_count", "", false),
                new FieldMapping("ModuleAuditStatVO.issueCount", "MYSQL", "agent_audit_task", "issue_count", "", false),
                new FieldMapping("ModuleAuditStatVO.blockerIssueCount", "MYSQL", "agent_audit_task", "blocker_issue_count", "", false),
                new FieldMapping("ModuleAuditStatVO.passRate", "MYSQL", "agent_audit_task", "pass_rate", "", false),
                new FieldMapping("ModuleAuditStatVO.releaseAllowed", "MYSQL", "agent_audit_task", "release_allowed", "", false),
                new FieldMapping("ModuleAuditStatVO.moduleRiskText", "MYSQL", "agent_audit_task", "module_risk_text", "", false),
                new FieldMapping("AgentAuditIssueVO.issueId", "MYSQL", "agent_audit_task", "issue_id", "", false),
                new FieldMapping("AgentAuditIssueVO.auditTaskId", "MYSQL", "agent_audit_task", "audit_task_id", "", false),
                new FieldMapping("AgentAuditIssueVO.issueCode", "MYSQL", "agent_audit_task", "issue_code", "", false),
                new FieldMapping("AgentAuditIssueVO.issueName", "MYSQL", "agent_audit_task", "issue_name", "", false),
                new FieldMapping("AgentAuditIssueVO.issueType", "MYSQL", "agent_audit_task", "issue_type", "", false),
                new FieldMapping("AgentAuditIssueVO.issueLevel", "MYSQL", "agent_audit_task", "issue_level", "", false),
                new FieldMapping("AgentAuditIssueVO.blockMerge", "MYSQL", "agent_audit_task", "block_merge", "", false),
                new FieldMapping("AgentAuditIssueVO.blockRelease", "MYSQL", "agent_audit_task", "block_release", "", false),
                new FieldMapping("AgentAuditIssueVO.filePath", "MYSQL", "agent_audit_task", "file_path", "", false),
                new FieldMapping("AgentAuditIssueVO.startLine", "MYSQL", "agent_audit_task", "start_line", "", false),
                new FieldMapping("AgentAuditIssueVO.issueDescription", "MYSQL", "agent_audit_task", "issue_description", "", false),
                new FieldMapping("AgentAuditIssueVO.fixSuggestion", "MYSQL", "agent_audit_task", "fix_suggestion", "", false),
                new FieldMapping("AgentAuditIssueVO.issueStatus", "MYSQL", "agent_audit_task", "issue_status", "", false),
                new FieldMapping("AgentAuditRuleCoverageVO.auditRuleCode", "MYSQL", "agent_audit_task", "audit_rule_code", "", false),
                new FieldMapping("AgentAuditRuleCoverageVO.auditRuleName", "MYSQL", "agent_audit_task", "audit_rule_name", "", false),
                new FieldMapping("AgentAuditRuleCoverageVO.auditRuleType", "MYSQL", "agent_audit_task", "audit_rule_type", "", false),
                new FieldMapping("AgentAuditRuleCoverageVO.enabled", "MYSQL", "agent_audit_task", "enabled", "", false),
                new FieldMapping("AgentAuditRuleCoverageVO.redLineRule", "MYSQL", "agent_audit_task", "red_line_rule", "", false),
                new FieldMapping("AgentAuditRuleCoverageVO.blockRelease", "MYSQL", "agent_audit_task", "block_release", "", false),
                new FieldMapping("AgentAuditRuleCoverageVO.coveredObjectCount", "MYSQL", "agent_audit_task", "covered_object_count", "", false),
                new FieldMapping("AgentAuditRuleCoverageVO.hitIssueCount", "MYSQL", "agent_audit_task", "hit_issue_count", "", false),
                new FieldMapping("AgentAuditRuleCoverageVO.coverageRate", "MYSQL", "agent_audit_task", "coverage_rate", "", false),
                new FieldMapping("AgentReleaseGateCheckVO.checkCode", "MYSQL", "agent_audit_task", "check_code", "", false),
                new FieldMapping("AgentReleaseGateCheckVO.checkName", "MYSQL", "agent_audit_task", "check_name", "", false),
                new FieldMapping("AgentReleaseGateCheckVO.checkType", "MYSQL", "agent_audit_task", "check_type", "", false),
                new FieldMapping("AgentReleaseGateCheckVO.required", "MYSQL", "agent_audit_task", "required", "", false),
                new FieldMapping("AgentReleaseGateCheckVO.passed", "MYSQL", "agent_audit_task", "passed", "", false),
                new FieldMapping("AgentReleaseGateCheckVO.blockRelease", "MYSQL", "agent_audit_task", "block_release", "", false),
                new FieldMapping("AgentReleaseGateCheckVO.failedReason", "MYSQL", "agent_audit_task", "failed_reason", "", false),
                new FieldMapping("AgentReleaseGateCheckVO.fixSuggestion", "MYSQL", "agent_audit_task", "fix_suggestion", "", false),
                new FieldMapping("AgentAuditFixSuggestionVO.suggestionId", "MYSQL", "agent_audit_task", "id", "", false),
                new FieldMapping("AgentAuditFixSuggestionVO.issueId", "MYSQL", "agent_audit_task", "issue_id", "", false),
                new FieldMapping("AgentAuditFixSuggestionVO.fixType", "MYSQL", "agent_audit_task", "fix_type", "", false),
                new FieldMapping("AgentAuditFixSuggestionVO.priority", "MYSQL", "agent_audit_task", "priority", "", false),
                new FieldMapping("AgentAuditFixSuggestionVO.targetFilePath", "MYSQL", "agent_audit_task", "target_file_path", "", false),
                new FieldMapping("AgentAuditFixSuggestionVO.suggestedFixText", "MYSQL", "agent_audit_task", "suggested_fix_text", "", false),
                new FieldMapping("AgentAuditFixSuggestionVO.acceptanceCriteria", "MYSQL", "agent_audit_task", "acceptance_criteria", "", false),
                new FieldMapping("AgentAuditFixSuggestionVO.autoFixAllowed", "MYSQL", "agent_audit_task", "auto_fix_allowed", "", false)
        );
    }
}
