package com.astock.module.agentaudit.infrastructure.converter;

import com.astock.common.convert.MapFieldReader;
import com.astock.common.convert.PageBundleConverter;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;
import com.astock.module.agentaudit.api.vo.AgentAuditDashboardVO;
import com.astock.module.agentaudit.application.query.AgentAuditDashboardQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AgentAuditConverter implements PageBundleConverter<AgentAuditDashboardQuery, AgentAuditDashboardVO> {

    @Override
    public AgentAuditDashboardVO convert(AgentAuditDashboardQuery query, PageDataQualityVO quality, PageSnapshotBundle bundle) {
        AgentAuditDashboardVO vo = new AgentAuditDashboardVO();

        if (bundle == null || bundle.isEmpty()) {
            vo.setDataComplete(false);
            vo.setDataStatusText("多表Repository未返回真实记录，拒绝Mock补齐。");
            vo.setTradeDate(query.getTradeDate());
            vo.setConclusion("无真实多表快照数据。");
            vo.setRiskTips(List.of("请检查页面专属多表Repository、引擎落库结果和规则版本。"));
            return vo;
        }

        Map<String, Object> primary = bundle.firstRow("agent_audit_code_scan_detail");
                vo.setAuditDate(query.getAuditDate() == null ? java.time.LocalDate.now() : query.getAuditDate());
        vo.setTradeDate(MapFieldReader.localDate(primary, "trade_date") == null ? query.getTradeDate() : MapFieldReader.localDate(primary, "trade_date"));
        vo.setDataComplete(quality.getDataComplete());
        vo.setDataStatusText(quality.getDataStatusText());
        vo.setOverview(toAgentAuditOverviewVO(bundle.firstRow("agent_audit_code_scan_detail"), bundle));
        vo.setRuleVersionSummary(toAgentAuditRuleVersionSummaryVO(bundle.firstRow("agent_audit_rule_version"), bundle));
        vo.setRecentAuditTasks(bundle.rows("agent_audit_code_scan_detail").stream().map(r -> toAgentAuditTaskVO(r, bundle)).toList());
        vo.setPageAuditMatrix(bundle.rows("agent_audit_code_scan_detail").stream().map(r -> toPageAuditMatrixVO(r, bundle)).toList());
        vo.setModuleAuditStats(bundle.rows("agent_audit_code_scan_detail").stream().map(r -> toModuleAuditStatVO(r, bundle)).toList());
        vo.setRedLineIssues(bundle.rows("agent_audit_code_scan_detail").stream().map(r -> toAgentAuditIssueVO(r, bundle)).toList());
        vo.setReleaseBlockerIssues(bundle.rows("agent_audit_code_scan_detail").stream().map(r -> toAgentAuditIssueVO(r, bundle)).toList());
        vo.setResolvedIssues(bundle.rows("agent_audit_code_scan_detail").stream().map(r -> toAgentAuditIssueVO(r, bundle)).toList());
        vo.setRuleCoverages(bundle.rows("agent_audit_rule_version").stream().map(r -> toAgentAuditRuleCoverageVO(r, bundle)).toList());
        vo.setReleaseGateChecks(bundle.rows("agent_audit_code_scan_detail").stream().map(r -> toAgentReleaseGateCheckVO(r, bundle)).toList());
        vo.setFixSuggestions(bundle.rows("agent_audit_code_scan_detail").stream().map(r -> toAgentAuditFixSuggestionVO(r, bundle)).toList());
        vo.setConclusion(MapFieldReader.string(bundle.firstRow("agent_audit_code_scan_detail"), "evidence_json"));
        vo.setRiskTips(stringList(bundle.firstRow("agent_audit_code_scan_detail"), "risk_json"));
        if (vo.getConclusion() == null) { vo.setConclusion("多表Repository已接入，Converter已填充页面核心业务区块；未命中源字段保持为空。"); }
        if (vo.getRiskTips() == null) { vo.setRiskTips(List.of("本页面由多表真实快照聚合，未使用Mock；Converter不做评分、不输出交易建议。")); }
        return vo;
    }

    private String tableFor(String voName) {
        return switch (voName) {
                        case "AgentAuditOverviewVO" -> "agent_audit_code_scan_detail";
            case "AgentAuditRuleVersionSummaryVO" -> "agent_audit_rule_version";
            case "AgentAuditTaskVO" -> "agent_audit_code_scan_detail";
            case "PageAuditMatrixVO" -> "agent_audit_code_scan_detail";
            case "ModuleAuditStatVO" -> "agent_audit_code_scan_detail";
            case "AgentAuditIssueVO" -> "agent_audit_code_scan_detail";
            case "AgentAuditRuleCoverageVO" -> "agent_audit_rule_version";
            case "AgentReleaseGateCheckVO" -> "agent_audit_code_scan_detail";
            case "AgentAuditFixSuggestionVO" -> "agent_audit_code_scan_detail";
            case "AgentAuditDashboardVO" -> "agent_audit_code_scan_detail";
            default -> "agent_audit_code_scan_detail";
        };
    }

    private List<String> stringList(Map<String, Object> row, String column) {
        String value = MapFieldReader.string(row, column);
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value);
    }


    private AgentAuditDashboardVO.AgentAuditOverviewVO toAgentAuditOverviewVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                AgentAuditDashboardVO.AgentAuditOverviewVO item = new AgentAuditDashboardVO.AgentAuditOverviewVO();
        item.setTotalAuditTaskCount(MapFieldReader.integer(row, "total_audit_task_count"));
        item.setPassedTaskCount(MapFieldReader.integer(row, "passed_task_count"));
        item.setFailedTaskCount(MapFieldReader.integer(row, "failed_task_count"));
        item.setBlockerIssueCount(MapFieldReader.integer(row, "blocker_issue_count"));
        item.setFatalIssueCount(MapFieldReader.integer(row, "fatal_issue_count"));
        item.setReleaseAllowed(MapFieldReader.bool(row, "release_allowed"));
        item.setMergeAllowed(MapFieldReader.bool(row, "merge_allowed"));
        item.setAuditPassRate(MapFieldReader.decimal(row, "audit_pass_rate"));
        item.setOverviewText(MapFieldReader.string(row, "features"));
        return item;
    }

    private AgentAuditDashboardVO.AgentAuditRuleVersionSummaryVO toAgentAuditRuleVersionSummaryVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                AgentAuditDashboardVO.AgentAuditRuleVersionSummaryVO item = new AgentAuditDashboardVO.AgentAuditRuleVersionSummaryVO();
        item.setAuditRuleVersionId(MapFieldReader.longValue(row, "audit_rule_version_id"));
        item.setVersionNo(MapFieldReader.string(row, "version_no"));
        item.setVersionName(MapFieldReader.string(row, "version_name"));
        item.setActive(MapFieldReader.bool(row, "active"));
        item.setAuditRuleCount(MapFieldReader.integer(row, "audit_rule_count"));
        item.setRedLineRuleCount(MapFieldReader.integer(row, "red_line_rule_count"));
        item.setRequireBacktestCheck(MapFieldReader.bool(row, "require_backtest_check"));
        item.setRequireFutureLeakageCheck(MapFieldReader.bool(row, "require_future_leakage_check"));
        item.setRequireTradingInstructionCheck(MapFieldReader.bool(row, "require_trading_instruction_check"));
        return item;
    }

    private AgentAuditDashboardVO.AgentAuditTaskVO toAgentAuditTaskVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                AgentAuditDashboardVO.AgentAuditTaskVO item = new AgentAuditDashboardVO.AgentAuditTaskVO();
        item.setAuditTaskId(MapFieldReader.longValue(row, "audit_task_id"));
        item.setTaskName(MapFieldReader.string(row, "task_name"));
        item.setAuditObjectType(MapFieldReader.string(row, "audit_object_type"));
        item.setAuditObjectName(MapFieldReader.string(row, "audit_object_name"));
        item.setPageCode(MapFieldReader.string(row, "page_code"));
        item.setModuleName(MapFieldReader.string(row, "module_name"));
        item.setAuditStatus(MapFieldReader.string(row, "audit_status"));
        item.setIssueCount(MapFieldReader.integer(row, "issue_count"));
        item.setBlockerIssueCount(MapFieldReader.integer(row, "blocker_issue_count"));
        item.setMergeAllowed(MapFieldReader.bool(row, "merge_allowed"));
        item.setReleaseAllowed(MapFieldReader.bool(row, "release_allowed"));
        item.setAuditSummary(MapFieldReader.string(row, "summary_text"));
        return item;
    }

    private AgentAuditDashboardVO.PageAuditMatrixVO toPageAuditMatrixVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                AgentAuditDashboardVO.PageAuditMatrixVO item = new AgentAuditDashboardVO.PageAuditMatrixVO();
        item.setPageCode(MapFieldReader.string(row, "page_code"));
        item.setPageName(MapFieldReader.string(row, "page_name"));
        item.setPageVoDefined(MapFieldReader.bool(row, "page_vo_defined"));
        item.setApiContractDefined(MapFieldReader.bool(row, "api_contract_defined"));
        item.setAggregatorDefined(MapFieldReader.bool(row, "aggregator_defined"));
        item.setMysqlMappingDefined(MapFieldReader.bool(row, "mysql_mapping_defined"));
        item.setClickhouseMappingDefined(MapFieldReader.bool(row, "clickhouse_mapping_defined"));
        item.setAuditPassed(MapFieldReader.bool(row, "audit_passed"));
        item.setBlockerIssueCount(MapFieldReader.integer(row, "blocker_issue_count"));
        item.setAuditText(MapFieldReader.string(row, "audit_text"));
        return item;
    }

    private AgentAuditDashboardVO.ModuleAuditStatVO toModuleAuditStatVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                AgentAuditDashboardVO.ModuleAuditStatVO item = new AgentAuditDashboardVO.ModuleAuditStatVO();
        item.setModuleName(MapFieldReader.string(row, "module_name"));
        item.setAuditTaskCount(MapFieldReader.integer(row, "audit_task_count"));
        item.setIssueCount(MapFieldReader.integer(row, "issue_count"));
        item.setBlockerIssueCount(MapFieldReader.integer(row, "blocker_issue_count"));
        item.setPassRate(MapFieldReader.decimal(row, "pass_rate"));
        item.setReleaseAllowed(MapFieldReader.bool(row, "release_allowed"));
        item.setModuleRiskText(MapFieldReader.string(row, "module_risk_text"));
        return item;
    }

    private AgentAuditDashboardVO.AgentAuditIssueVO toAgentAuditIssueVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                AgentAuditDashboardVO.AgentAuditIssueVO item = new AgentAuditDashboardVO.AgentAuditIssueVO();
        item.setIssueId(MapFieldReader.longValue(row, "issue_id"));
        item.setAuditTaskId(MapFieldReader.longValue(row, "audit_task_id"));
        item.setIssueCode(MapFieldReader.string(row, "issue_code"));
        item.setIssueName(MapFieldReader.string(row, "issue_name"));
        item.setIssueType(MapFieldReader.string(row, "issue_type"));
        item.setIssueLevel(MapFieldReader.string(row, "issue_level"));
        item.setBlockMerge(MapFieldReader.bool(row, "block_merge"));
        item.setBlockRelease(MapFieldReader.bool(row, "block_release"));
        item.setFilePath(MapFieldReader.string(row, "file_path"));
        item.setStartLine(MapFieldReader.integer(row, "start_line"));
        item.setIssueDescription(MapFieldReader.string(row, "issue_description"));
        item.setFixSuggestion(MapFieldReader.string(row, "fix_suggestion"));
        item.setIssueStatus(MapFieldReader.string(row, "issue_status"));
        return item;
    }

    private AgentAuditDashboardVO.AgentAuditRuleCoverageVO toAgentAuditRuleCoverageVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                AgentAuditDashboardVO.AgentAuditRuleCoverageVO item = new AgentAuditDashboardVO.AgentAuditRuleCoverageVO();
        item.setAuditRuleCode(MapFieldReader.string(row, "audit_rule_code"));
        item.setAuditRuleName(MapFieldReader.string(row, "audit_rule_name"));
        item.setAuditRuleType(MapFieldReader.string(row, "audit_rule_type"));
        item.setEnabled(MapFieldReader.bool(row, "enabled"));
        item.setRedLineRule(MapFieldReader.bool(row, "red_line_rule"));
        item.setBlockRelease(MapFieldReader.bool(row, "block_release"));
        item.setCoveredObjectCount(MapFieldReader.integer(row, "covered_object_count"));
        item.setHitIssueCount(MapFieldReader.integer(row, "hit_issue_count"));
        item.setCoverageRate(MapFieldReader.decimal(row, "coverage_rate"));
        return item;
    }

    private AgentAuditDashboardVO.AgentReleaseGateCheckVO toAgentReleaseGateCheckVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                AgentAuditDashboardVO.AgentReleaseGateCheckVO item = new AgentAuditDashboardVO.AgentReleaseGateCheckVO();
        item.setCheckCode(MapFieldReader.string(row, "check_code"));
        item.setCheckName(MapFieldReader.string(row, "check_name"));
        item.setCheckType(MapFieldReader.string(row, "check_type"));
        item.setRequired(MapFieldReader.bool(row, "required"));
        item.setPassed(MapFieldReader.bool(row, "passed"));
        item.setBlockRelease(MapFieldReader.bool(row, "block_release"));
        item.setFailedReason(MapFieldReader.string(row, "failed_reason"));
        item.setFixSuggestion(MapFieldReader.string(row, "fix_suggestion"));
        return item;
    }

    private AgentAuditDashboardVO.AgentAuditFixSuggestionVO toAgentAuditFixSuggestionVO(Map<String, Object> row, PageSnapshotBundle bundle) {
                AgentAuditDashboardVO.AgentAuditFixSuggestionVO item = new AgentAuditDashboardVO.AgentAuditFixSuggestionVO();
        item.setSuggestionId(MapFieldReader.longValue(row, "suggestion_id"));
        item.setIssueId(MapFieldReader.longValue(row, "issue_id"));
        item.setFixType(MapFieldReader.string(row, "fix_type"));
        item.setPriority(MapFieldReader.string(row, "priority"));
        item.setTargetFilePath(MapFieldReader.string(row, "target_file_path"));
        item.setSuggestedFixText(MapFieldReader.string(row, "suggested_fix_text"));
        item.setAcceptanceCriteria(MapFieldReader.string(row, "acceptance_criteria"));
        item.setAutoFixAllowed(MapFieldReader.bool(row, "auto_fix_allowed"));
        return item;
    }

}
