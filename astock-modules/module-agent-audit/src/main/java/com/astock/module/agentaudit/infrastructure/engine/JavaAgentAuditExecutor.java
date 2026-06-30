package com.astock.module.agentaudit.infrastructure.engine;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.engine.EngineExecutionTemplate;
import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.infrastructure.engine.EngineRunResult;
import com.astock.module.agentaudit.domain.engine.AgentAuditEngineContext;
import com.astock.module.agentaudit.domain.engine.AgentAuditEngineResult;
import com.astock.module.agentaudit.domain.engine.AgentAuditExecutor;
import com.astock.module.agentaudit.domain.model.AgentAuditResultBundle;
import com.astock.module.agentaudit.domain.service.AgentAuditExecutionService;
import com.astock.module.agentaudit.domain.service.AgentAuditOutputRowBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class JavaAgentAuditExecutor implements AgentAuditExecutor {
    private final EngineExecutionTemplate executionTemplate;
    private final AgentAuditExecutionService auditExecutionService;
    private final AgentAuditOutputRowBuilder outputRowBuilder;

    public JavaAgentAuditExecutor(EngineExecutionTemplate executionTemplate,
                                  AgentAuditExecutionService auditExecutionService,
                                  AgentAuditOutputRowBuilder outputRowBuilder) {
        this.executionTemplate = executionTemplate;
        this.auditExecutionService = auditExecutionService;
        this.outputRowBuilder = outputRowBuilder;
    }

    @Override
    public AgentAuditEngineResult execute(AgentAuditEngineContext context) {
        EngineRunCommand command = new EngineRunCommand();
        command.setTaskId(context.getTaskId());
        command.setTaskName("Agent研发审计引擎");
        command.setTaskType("ENGINE_AGENT_AUDIT");
        command.setTradeDate(context.getTradeDate() == null ? LocalDate.now() : context.getTradeDate());
        command.setMarketScope(context.getMarketScope() == null ? "A_SHARE" : context.getMarketScope());
        command.setRuleCode("AGENT_AUDIT_CORE");
        command.setRuleVersionId(context.getRuleVersionId());
        command.setParamJson(context.getParamJson());

        /*
         * AgentAuditExecutor审计的是工程源码、字段血缘和发布闸门，不依赖T日ClickHouse输入快照。
         * 因此关闭输入快照完整性检查；仍保留规则版本、任务日志、输出表白名单、失败状态闭环。
         */
        command.setDataCheckEnabled(false);
        command.setInputTables(List.of());
        command.setOutputTables(List.of(
                "agent_audit_code_scan_detail",
                "agent_audit_data_lineage_detail",
                "agent_audit_rule_hit_detail",
                "agent_audit_release_gate_detail"
        ));
        command.setFailIfNoOutputRows(true);

        EngineRunResult runResult = executionTemplate.execute(command, this::compute);
        return toResult(runResult);
    }

    private Map<String, List<Map<String, Object>>> compute(EngineRunCommand command,
                                                           Long ruleVersionId,
                                                           PageSnapshotBundle ignored) {
        String projectRootPath = resolveProjectRootPath(command.getParamJson());
        AgentAuditResultBundle auditResult = auditExecutionService.execute(projectRootPath);
        return outputRowBuilder.buildRows(command, ruleVersionId, auditResult);
    }

    private String resolveProjectRootPath(String paramJson) {
        /*
         * 第十六段先使用JVM工作目录作为默认项目根目录。
         * 后续可从paramJson解析 projectRootPath，支持CI/CD显式传入工程路径。
         */
        return System.getProperty("user.dir");
    }

    private AgentAuditEngineResult toResult(EngineRunResult source) {
        AgentAuditEngineResult target = new AgentAuditEngineResult();
        target.setSuccess(source.getSuccess());
        target.setTaskId(source.getTaskId());
        target.setTradeDate(source.getTradeDate());
        target.setOutputTables(source.getOutputTables());
        target.setOutputRowCount(source.getOutputRowCount());
        target.setDataComplete(source.getDataComplete());
        target.setFutureLeakageCheckPassed(source.getFutureLeakageCheckPassed());
        target.setFailureReason(source.getFailureReason());
        target.setSummaryText(source.getSummaryText());
        return target;
    }
}
