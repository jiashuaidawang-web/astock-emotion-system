package com.astock.module.agentaudit.domain.service;

import com.astock.module.agentaudit.domain.model.AgentAuditFileSnapshot;
import com.astock.module.agentaudit.domain.model.AgentAuditIssue;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AgentAuditRedLineScanner {
    private static final List<String> TRADING_ADVICE_WORDS = List.of(
            "买入", "卖出", "持有", "推荐", "目标价", "加仓", "清仓",
            "BUY", "SELL", "HOLD", "TARGET_PRICE", "RECOMMEND"
    );

    private static final List<String> FUTURE_LEAKAGE_WORDS = List.of(
            "future_1d_return", "future_3d_return", "future_5d_return", "future_10d_return",
            "future1d_return", "future3d_return", "future5d_return", "future10d_return",
            "following_return", "max_drawdown"
    );

    public List<AgentAuditIssue> scan(List<AgentAuditFileSnapshot> files) {
        List<AgentAuditIssue> issues = new ArrayList<>();
        for (AgentAuditFileSnapshot file : files) {
            scanMock(file, issues);
            scanTradingAdvice(file, issues);
            scanFutureLeakage(file, issues);
            scanControllerAlgorithm(file, issues);
            scanEmptyCompute(file, issues);
        }
        return issues;
    }

    private void scanMock(AgentAuditFileSnapshot file, List<AgentAuditIssue> issues) {
        scanKeyword(file, issues, "MOCK_DETECTED", "Mock数据残留", "BLOCKER", "MOCK",
                List.of("mock", "Mock", "MOCK", "fakeData", "demoData", "示例数据"),
                "删除Mock数据，改为Repository真实查询或DATA_NOT_READY。");
    }

    private void scanTradingAdvice(AgentAuditFileSnapshot file, List<AgentAuditIssue> issues) {
        scanKeyword(file, issues, "TRADING_ADVICE_WORD_DETECTED", "交易建议词命中", "BLOCKER", "TRADING_ADVICE",
                TRADING_ADVICE_WORDS,
                "替换为条件状态、风险状态或研究性描述，禁止输出交易指令。");
    }

    private void scanFutureLeakage(AgentAuditFileSnapshot file, List<AgentAuditIssue> issues) {
        if (isAllowedFutureFile(file)) {
            return;
        }
        scanKeyword(file, issues, "FUTURE_FUNCTION_RISK", "future字段越界使用风险", "BLOCKER", "FUTURE_LEAKAGE",
                FUTURE_LEAKAGE_WORDS,
                "future_*只能在历史样本展示和回测窗口读取，禁止参与T日判断。");
    }

    private boolean isAllowedFutureFile(AgentAuditFileSnapshot file) {
        String path = file.getRelativePath();
        return path.contains("Backtest")
                || path.contains("backtest")
                || path.contains("Historical")
                || path.contains("historical")
                || path.contains("SimilarityOutput")
                || path.contains("output_contract")
                || path.endsWith(".md")
                || path.endsWith(".sql");
    }

    private void scanControllerAlgorithm(AgentAuditFileSnapshot file, List<AgentAuditIssue> issues) {
        String path = file.getRelativePath();
        if (!path.endsWith("Controller.java")) {
            return;
        }
        scanKeyword(file, issues, "CONTROLLER_ALGORITHM_RISK", "Controller疑似承载算法逻辑", "MAJOR", "ARCHITECTURE",
                List.of("for (", "stream()", "calculate", "score", "Repository", "JdbcTemplate"),
                "Controller只做参数接收与Service调用，算法应下沉到Engine/Service。");
    }

    private void scanEmptyCompute(AgentAuditFileSnapshot file, List<AgentAuditIssue> issues) {
        String path = file.getRelativePath();
        if (!path.contains("/infrastructure/engine/") || !path.endsWith(".java")) {
            return;
        }
        String content = file.getContent();
        if (content != null && content.contains("return Map.of();")) {
            issues.add(issue("ENGINE_EMPTY_COMPUTE", "Engine compute仍为空实现", "BLOCKER", "ENGINE_IMPL",
                    file, lineOf(content, "return Map.of();"),
                    "compute方法直接 return Map.of()，未产生真实输出。",
                    "补齐真实算法并返回目标快照表写入行。"));
        }
    }

    private void scanKeyword(AgentAuditFileSnapshot file,
                             List<AgentAuditIssue> issues,
                             String code,
                             String name,
                             String level,
                             String type,
                             List<String> keywords,
                             String suggestion) {
        String content = file.getContent();
        if (content == null || content.isBlank()) {
            return;
        }
        String lower = content.toLowerCase(Locale.ROOT);
        for (String keyword : keywords) {
            String test = keyword.toLowerCase(Locale.ROOT);
            if (lower.contains(test)) {
                issues.add(issue(code, name, level, type, file, lineOf(lower, test),
                        "命中关键字：" + keyword,
                        suggestion));
                return;
            }
        }
    }

    private AgentAuditIssue issue(String code,
                                  String name,
                                  String level,
                                  String type,
                                  AgentAuditFileSnapshot file,
                                  int lineNo,
                                  String evidence,
                                  String suggestion) {
        AgentAuditIssue issue = new AgentAuditIssue();
        issue.setIssueCode(code);
        issue.setIssueName(name);
        issue.setIssueLevel(level);
        issue.setIssueType(type);
        issue.setModuleName(file.getModuleName());
        issue.setFilePath(file.getRelativePath());
        issue.setLineNo(lineNo);
        issue.setEvidenceText(evidence);
        issue.setFixSuggestion(suggestion);
        issue.setReleaseBlocker("BLOCKER".equals(level));
        return issue;
    }

    private int lineOf(String content, String keyword) {
        int idx = content.indexOf(keyword);
        if (idx < 0) {
            return 1;
        }
        return (int) content.substring(0, idx).chars().filter(ch -> ch == '\n').count() + 1;
    }
}
