package com.astock.module.agentaudit.domain.service;

import com.astock.module.agentaudit.domain.model.AgentAuditIssue;
import com.astock.module.agentaudit.domain.model.AgentAuditLineageIssue;
import com.astock.module.agentaudit.domain.model.AgentReleaseGateCheck;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Service
public class AgentReleaseGateService {

    public List<AgentReleaseGateCheck> check(List<AgentAuditIssue> issues,
                                             List<AgentAuditLineageIssue> lineageIssues) {
        List<AgentReleaseGateCheck> checks = new ArrayList<>();
        checks.add(gate("GATE_NO_MOCK", "禁止Mock残留", issues, i -> "MOCK_DETECTED".equals(i.getIssueCode())));
        checks.add(gate("GATE_NO_TRADING_ADVICE", "禁止交易建议词", issues, i -> "TRADING_ADVICE_WORD_DETECTED".equals(i.getIssueCode())));
        checks.add(gate("GATE_NO_FUTURE_LEAKAGE", "禁止future函数越界", issues, i -> "FUTURE_FUNCTION_RISK".equals(i.getIssueCode())));
        checks.add(gate("GATE_NO_EMPTY_ENGINE", "禁止Engine空实现", issues, i -> "ENGINE_EMPTY_COMPUTE".equals(i.getIssueCode())));
        checks.add(lineageGate(lineageIssues));
        return checks;
    }

    private AgentReleaseGateCheck gate(String code,
                                       String name,
                                       List<AgentAuditIssue> issues,
                                       Predicate<AgentAuditIssue> predicate) {
        int issueCount = (int) issues.stream().filter(predicate).count();
        int blockerCount = (int) issues.stream().filter(predicate).filter(i -> Boolean.TRUE.equals(i.getReleaseBlocker())).count();
        AgentReleaseGateCheck check = new AgentReleaseGateCheck();
        check.setGateCode(code);
        check.setGateName(name);
        check.setIssueCount(issueCount);
        check.setBlockerCount(blockerCount);
        check.setPassed(blockerCount == 0);
        check.setGateStatus(blockerCount == 0 ? "PASS" : "BLOCKED");
        check.setEvidenceJson("{\"issueCount\":" + issueCount + ",\"blockerCount\":" + blockerCount + "}");
        return check;
    }

    private AgentReleaseGateCheck lineageGate(List<AgentAuditLineageIssue> issues) {
        int blockerCount = (int) issues.stream().filter(i -> "BLOCKER".equals(i.getIssueLevel())).count();
        AgentReleaseGateCheck check = new AgentReleaseGateCheck();
        check.setGateCode("GATE_FIELD_LINEAGE");
        check.setGateName("字段血缘完整性");
        check.setIssueCount(issues.size());
        check.setBlockerCount(blockerCount);
        check.setPassed(blockerCount == 0);
        check.setGateStatus(blockerCount == 0 ? "PASS" : "BLOCKED");
        check.setEvidenceJson("{\"issueCount\":" + issues.size() + ",\"blockerCount\":" + blockerCount + "}");
        return check;
    }
}
