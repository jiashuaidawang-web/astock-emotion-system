package com.astock.module.agentaudit.domain.service;

import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.module.agentaudit.domain.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgentAuditOutputRowBuilder {

    public Map<String, List<Map<String, Object>>> buildRows(EngineRunCommand command,
                                                            Long ruleVersionId,
                                                            AgentAuditResultBundle result) {
        Map<String, List<Map<String, Object>>> rows = new LinkedHashMap<>();
        rows.put("agent_audit_code_scan_detail", codeScanRows(command, ruleVersionId, result.getIssues()));
        rows.put("agent_audit_data_lineage_detail", lineageRows(command, ruleVersionId, result.getLineageIssues()));
        rows.put("agent_audit_rule_hit_detail", ruleHitRows(command, ruleVersionId, result.getRuleHits()));
        rows.put("agent_audit_release_gate_detail", gateRows(command, ruleVersionId, result.getGateChecks()));
        return rows;
    }

    private List<Map<String, Object>> codeScanRows(EngineRunCommand command,
                                                   Long ruleVersionId,
                                                   List<AgentAuditIssue> issues) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (issues.isEmpty()) {
            Map<String, Object> row = base(command, ruleVersionId);
            row.put("issue_code", "NO_CODE_RED_LINE_ISSUE");
            row.put("issue_name", "代码红线扫描通过");
            row.put("issue_level", "PASS");
            row.put("issue_type", "CODE_SCAN");
            row.put("module_name", "ALL");
            row.put("file_path", "");
            row.put("line_no", 0);
            row.put("release_blocker", false);
            row.put("evidence_json", "{\"passed\":true}");
            rows.add(row);
            return rows;
        }
        for (AgentAuditIssue issue : issues) {
            Map<String, Object> row = base(command, ruleVersionId);
            row.put("issue_code", issue.getIssueCode());
            row.put("issue_name", issue.getIssueName());
            row.put("issue_level", issue.getIssueLevel());
            row.put("issue_type", issue.getIssueType());
            row.put("module_name", issue.getModuleName());
            row.put("file_path", issue.getFilePath());
            row.put("line_no", issue.getLineNo());
            row.put("release_blocker", issue.getReleaseBlocker());
            row.put("evidence_json", "{\"evidence\":\"" + escape(issue.getEvidenceText()) + "\",\"fixSuggestion\":\"" + escape(issue.getFixSuggestion()) + "\"}");
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> lineageRows(EngineRunCommand command,
                                                  Long ruleVersionId,
                                                  List<AgentAuditLineageIssue> issues) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (issues.isEmpty()) {
            Map<String, Object> row = base(command, ruleVersionId);
            row.put("page_code", "ALL");
            row.put("vo_class_name", "ALL");
            row.put("field_name", "ALL");
            row.put("source_table", "");
            row.put("source_column", "");
            row.put("lineage_status", "PASS");
            row.put("issue_level", "PASS");
            row.put("evidence_json", "{\"passed\":true}");
            rows.add(row);
            return rows;
        }
        for (AgentAuditLineageIssue issue : issues) {
            Map<String, Object> row = base(command, ruleVersionId);
            row.put("page_code", issue.getPageCode());
            row.put("vo_class_name", issue.getVoClassName());
            row.put("field_name", issue.getFieldName());
            row.put("source_table", issue.getSourceTable());
            row.put("source_column", issue.getSourceColumn());
            row.put("lineage_status", issue.getLineageStatus());
            row.put("issue_level", issue.getIssueLevel());
            row.put("evidence_json", "{\"evidence\":\"" + escape(issue.getEvidenceText()) + "\"}");
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> ruleHitRows(EngineRunCommand command,
                                                  Long ruleVersionId,
                                                  List<AgentAuditRuleHit> hits) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (AgentAuditRuleHit hit : hits) {
            Map<String, Object> row = base(command, ruleVersionId);
            row.put("rule_code", hit.getRuleCode());
            row.put("rule_name", hit.getRuleName());
            row.put("hit_status", hit.getHitStatus());
            row.put("hit_count", hit.getHitCount());
            row.put("blocker_count", hit.getBlockerCount());
            row.put("evidence_json", hit.getEvidenceJson());
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> gateRows(EngineRunCommand command,
                                               Long ruleVersionId,
                                               List<AgentReleaseGateCheck> gates) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (AgentReleaseGateCheck gate : gates) {
            Map<String, Object> row = base(command, ruleVersionId);
            row.put("gate_code", gate.getGateCode());
            row.put("gate_name", gate.getGateName());
            row.put("gate_status", gate.getGateStatus());
            row.put("passed", gate.getPassed());
            row.put("issue_count", gate.getIssueCount());
            row.put("blocker_count", gate.getBlockerCount());
            row.put("evidence_json", gate.getEvidenceJson());
            rows.add(row);
        }
        return rows;
    }

    private Map<String, Object> base(EngineRunCommand command, Long ruleVersionId) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("trade_date", command.getTradeDate());
        row.put("market_scope", command.getMarketScope());
        row.put("audit_task_id", command.getTaskId());
        row.put("task_id", command.getTaskId());
        row.put("rule_version_id", ruleVersionId);
        row.put("created_at", LocalDateTime.now());
        return row;
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
