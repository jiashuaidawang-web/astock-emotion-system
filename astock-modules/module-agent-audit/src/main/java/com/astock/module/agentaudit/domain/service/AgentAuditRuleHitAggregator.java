package com.astock.module.agentaudit.domain.service;

import com.astock.module.agentaudit.domain.model.AgentAuditIssue;
import com.astock.module.agentaudit.domain.model.AgentAuditLineageIssue;
import com.astock.module.agentaudit.domain.model.AgentAuditRuleHit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AgentAuditRuleHitAggregator {

    public List<AgentAuditRuleHit> aggregate(List<AgentAuditIssue> issues,
                                             List<AgentAuditLineageIssue> lineageIssues) {
        List<AgentAuditRuleHit> hits = new ArrayList<>();

        Map<String, List<AgentAuditIssue>> grouped = issues.stream()
                .collect(Collectors.groupingBy(AgentAuditIssue::getIssueCode));

        for (Map.Entry<String, List<AgentAuditIssue>> entry : grouped.entrySet()) {
            hits.add(hit(entry.getKey(), ruleName(entry.getKey()), entry.getValue().size(),
                    (int) entry.getValue().stream().filter(i -> Boolean.TRUE.equals(i.getReleaseBlocker())).count()));
        }

        int lineageBlockers = (int) lineageIssues.stream()
                .filter(i -> "BLOCKER".equals(i.getIssueLevel()))
                .count();
        if (!lineageIssues.isEmpty()) {
            hits.add(hit("FIELD_LINEAGE_AUDIT", "字段血缘审计", lineageIssues.size(), lineageBlockers));
        }

        if (hits.isEmpty()) {
            hits.add(hit("ALL_RULES_PASS", "全部红线规则通过", 0, 0));
        }
        return hits;
    }

    private AgentAuditRuleHit hit(String code, String name, int hitCount, int blockerCount) {
        AgentAuditRuleHit hit = new AgentAuditRuleHit();
        hit.setRuleCode(code);
        hit.setRuleName(name);
        hit.setHitCount(hitCount);
        hit.setBlockerCount(blockerCount);
        hit.setHitStatus(blockerCount > 0 ? "BLOCKED" : hitCount > 0 ? "WARNING" : "PASS");
        hit.setEvidenceJson("{\"hitCount\":" + hitCount + ",\"blockerCount\":" + blockerCount + "}");
        return hit;
    }

    private String ruleName(String code) {
        return switch (code) {
            case "MOCK_DETECTED" -> "Mock检测";
            case "TRADING_ADVICE_WORD_DETECTED" -> "交易建议词检测";
            case "FUTURE_FUNCTION_RISK" -> "future函数检测";
            case "CONTROLLER_ALGORITHM_RISK" -> "Controller算法职责检测";
            case "ENGINE_EMPTY_COMPUTE" -> "Engine空实现检测";
            default -> code;
        };
    }
}
