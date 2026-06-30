package com.astock.module.agentaudit.domain.service;

import com.astock.common.convert.MapFieldReader;
import com.astock.module.agentaudit.domain.model.AgentAuditLineageIssue;
import com.astock.module.agentaudit.domain.repository.AgentAuditLineageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AgentAuditLineageChecker {
    private final AgentAuditLineageRepository lineageRepository;

    public AgentAuditLineageChecker(AgentAuditLineageRepository lineageRepository) {
        this.lineageRepository = lineageRepository;
    }

    public List<AgentAuditLineageIssue> check() {
        List<Map<String, Object>> rows = lineageRepository.selectLineageRows();
        List<AgentAuditLineageIssue> issues = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            String pageCode = MapFieldReader.string(row, "page_code");
            String voClassName = MapFieldReader.string(row, "vo_class_name");
            String fieldName = MapFieldReader.string(row, "field_name");
            String sourceType = MapFieldReader.string(row, "source_type");
            String sourceTable = MapFieldReader.string(row, "source_table");
            String sourceColumn = MapFieldReader.string(row, "source_column");
            Boolean required = MapFieldReader.bool(row, "required");
            Boolean auditPassed = MapFieldReader.bool(row, "audit_passed");

            if (Boolean.FALSE.equals(auditPassed)) {
                issues.add(issue(pageCode, voClassName, fieldName, sourceTable, sourceColumn,
                        "AUDIT_NOT_PASSED", "MAJOR", "字段血缘审计未通过。"));
            }

            boolean sourceMissing = ("CLICKHOUSE".equals(sourceType) || "MYSQL".equals(sourceType))
                    && (sourceTable == null || sourceTable.isBlank() || sourceColumn == null || sourceColumn.isBlank());
            if (sourceMissing) {
                issues.add(issue(pageCode, voClassName, fieldName, sourceTable, sourceColumn,
                        "SOURCE_MISSING", Boolean.TRUE.equals(required) ? "BLOCKER" : "MAJOR",
                        "字段声明为真实数据源，但source_table/source_column缺失。"));
            }
        }
        if (rows.isEmpty()) {
            issues.add(issue("GLOBAL", "ALL", "ALL", "", "",
                    "LINEAGE_EMPTY", "BLOCKER", "page_contract_field_lineage无可审计记录。"));
        }
        return issues;
    }

    private AgentAuditLineageIssue issue(String pageCode,
                                         String voClassName,
                                         String fieldName,
                                         String sourceTable,
                                         String sourceColumn,
                                         String status,
                                         String level,
                                         String evidence) {
        AgentAuditLineageIssue issue = new AgentAuditLineageIssue();
        issue.setPageCode(pageCode);
        issue.setVoClassName(voClassName);
        issue.setFieldName(fieldName);
        issue.setSourceTable(sourceTable);
        issue.setSourceColumn(sourceColumn);
        issue.setLineageStatus(status);
        issue.setIssueLevel(level);
        issue.setEvidenceText(evidence);
        return issue;
    }
}
