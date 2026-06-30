package com.astock.module.rule.infrastructure.converter;

import com.astock.common.lineage.FieldMapping;
import com.astock.common.lineage.PageFieldMappingRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RuleVersionFieldMappingRegistry implements PageFieldMappingRegistry {
    @Override
    public String pageCode() {
        return "PAGE_14_RULE_VERSION";
    }

    @Override
    public String voClassName() {
        return "RuleVersionManagePageVO";
    }

    @Override
    public List<FieldMapping> mappings() {
        return List.of(
                new FieldMapping("RuleVersionManagePageVO.tradeDate", "MYSQL", "rule_version", "trade_date", "", false),
                new FieldMapping("RuleVersionManagePageVO.dataComplete", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("RuleVersionManagePageVO.dataStatusText", "CALCULATION", "", "", "DataQualityQueryService.checkPage", true),
                new FieldMapping("RuleVersionManagePageVO.overview", "MYSQL", "rule_version", "overview", "", false),
                new FieldMapping("RuleVersionManagePageVO.ruleDefinitions", "MYSQL", "rule_version", "rule_definitions", "", false),
                new FieldMapping("RuleVersionManagePageVO.versions", "MYSQL", "rule_version", "versions", "", false),
                new FieldMapping("RuleVersionManagePageVO.activeVersions", "MYSQL", "rule_version", "active_versions", "", false),
                new FieldMapping("RuleVersionManagePageVO.publishChecks", "MYSQL", "rule_version", "publish_checks", "", false),
                new FieldMapping("RuleVersionManagePageVO.backtestChecks", "MYSQL", "rule_version", "backtest_checks", "", false),
                new FieldMapping("RuleVersionManagePageVO.auditLogs", "MYSQL", "rule_version", "audit_logs", "", false),
                new FieldMapping("RuleVersionManagePageVO.compareResults", "MYSQL", "rule_version", "compare_results", "", false),
                new FieldMapping("RuleVersionManagePageVO.conclusion", "MYSQL", "rule_version", "evidence_json", "", false),
                new FieldMapping("RuleVersionManagePageVO.riskTips", "MYSQL", "rule_version", "risk_json", "", false),
                new FieldMapping("RuleVersionOverviewVO.ruleCount", "MYSQL", "rule_version", "rule_count", "", false),
                new FieldMapping("RuleVersionOverviewVO.versionCount", "MYSQL", "rule_version", "version_count", "", false),
                new FieldMapping("RuleVersionOverviewVO.draftCount", "MYSQL", "rule_version", "draft_count", "", false),
                new FieldMapping("RuleVersionOverviewVO.publishedCount", "MYSQL", "rule_version", "published_count", "", false),
                new FieldMapping("RuleVersionOverviewVO.activeCount", "MYSQL", "rule_version", "active_count", "", false),
                new FieldMapping("RuleVersionOverviewVO.publishBlockedCount", "MYSQL", "rule_version", "publish_blocked_count", "", false),
                new FieldMapping("RuleVersionOverviewVO.overviewText", "MYSQL", "rule_version", "features", "", false),
                new FieldMapping("RuleDefinitionVO.ruleId", "MYSQL", "rule_version", "rule_id", "", false),
                new FieldMapping("RuleDefinitionVO.ruleCode", "MYSQL", "rule_version", "rule_code", "", false),
                new FieldMapping("RuleDefinitionVO.ruleName", "MYSQL", "rule_version", "rule_name", "", false),
                new FieldMapping("RuleDefinitionVO.ruleType", "MYSQL", "rule_version", "rule_type", "", false),
                new FieldMapping("RuleDefinitionVO.enabled", "MYSQL", "rule_version", "enabled", "", false),
                new FieldMapping("RuleDefinitionVO.backtestRequired", "MYSQL", "rule_version", "backtest_required", "", false),
                new FieldMapping("RuleDefinitionVO.agentAuditRequired", "MYSQL", "rule_version", "agent_audit_required", "", false),
                new FieldMapping("RuleVersionVO.versionId", "MYSQL", "rule_version", "id", "", false),
                new FieldMapping("RuleVersionVO.ruleCode", "MYSQL", "rule_version", "rule_code", "", false),
                new FieldMapping("RuleVersionVO.versionNo", "MYSQL", "rule_version", "version_no", "", false),
                new FieldMapping("RuleVersionVO.versionName", "MYSQL", "rule_version", "version_name", "", false),
                new FieldMapping("RuleVersionVO.versionStatus", "MYSQL", "rule_version", "version_status", "", false),
                new FieldMapping("RuleVersionVO.active", "MYSQL", "rule_version", "active", "", false),
                new FieldMapping("RuleVersionVO.backtestCheckPassed", "MYSQL", "rule_version", "backtest_check_passed", "", false),
                new FieldMapping("RuleVersionVO.publishCheckPassed", "MYSQL", "rule_version", "publish_check_passed", "", false),
                new FieldMapping("RuleVersionVO.agentAuditPassed", "MYSQL", "rule_version", "agent_audit_passed", "", false),
                new FieldMapping("RuleVersionVO.versionDescription", "MYSQL", "rule_version", "version_description", "", false),
                new FieldMapping("RulePublishCheckVO.versionId", "MYSQL", "rule_version", "id", "", false),
                new FieldMapping("RulePublishCheckVO.checkCode", "MYSQL", "rule_version", "check_code", "", false),
                new FieldMapping("RulePublishCheckVO.checkName", "MYSQL", "rule_version", "check_name", "", false),
                new FieldMapping("RulePublishCheckVO.passed", "MYSQL", "rule_version", "passed", "", false),
                new FieldMapping("RulePublishCheckVO.blockPublish", "MYSQL", "rule_version", "block_publish", "", false),
                new FieldMapping("RulePublishCheckVO.failedReason", "MYSQL", "rule_version", "failed_reason", "", false),
                new FieldMapping("RulePublishCheckVO.fixSuggestion", "MYSQL", "rule_version", "fix_suggestion", "", false),
                new FieldMapping("RuleBacktestCheckVO.versionId", "MYSQL", "rule_version", "id", "", false),
                new FieldMapping("RuleBacktestCheckVO.backtestPassed", "MYSQL", "rule_version", "backtest_passed", "", false),
                new FieldMapping("RuleBacktestCheckVO.latestReportId", "MYSQL", "rule_version", "latest_report_id", "", false),
                new FieldMapping("RuleBacktestCheckVO.checkText", "MYSQL", "rule_version", "check_text", "", false),
                new FieldMapping("RuleVersionAuditLogVO.versionId", "MYSQL", "rule_version", "id", "", false),
                new FieldMapping("RuleVersionAuditLogVO.operationType", "MYSQL", "rule_version", "operation_type", "", false),
                new FieldMapping("RuleVersionAuditLogVO.operator", "MYSQL", "rule_version", "operator", "", false),
                new FieldMapping("RuleVersionAuditLogVO.operatedAt", "MYSQL", "rule_version", "operated_at", "", false),
                new FieldMapping("RuleVersionAuditLogVO.operationRemark", "MYSQL", "rule_version", "operation_remark", "", false),
                new FieldMapping("RuleVersionCompareVO.baseVersionId", "MYSQL", "rule_version", "base_version_id", "", false),
                new FieldMapping("RuleVersionCompareVO.compareVersionId", "MYSQL", "rule_version", "compare_version_id", "", false),
                new FieldMapping("RuleVersionCompareVO.diffText", "MYSQL", "rule_version", "diff_text", "", false),
                new FieldMapping("RuleVersionCompareVO.riskText", "MYSQL", "rule_version", "risk_json", "", false)
        );
    }
}
