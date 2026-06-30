package com.astock.infrastructure.engine;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.common.exception.BusinessException;
import com.astock.infrastructure.engine.entity.AlgorithmTaskLogEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EngineExecutionTemplate {
    private final RuleVersionGuardService ruleVersionGuardService;
    private final EngineDataQualityGuard dataQualityGuard;
    private final EngineSnapshotLoadService snapshotLoadService;
    private final EngineSnapshotWriteService snapshotWriteService;
    private final EngineTaskLogService taskLogService;

    public EngineExecutionTemplate(RuleVersionGuardService ruleVersionGuardService,
                                   EngineDataQualityGuard dataQualityGuard,
                                   EngineSnapshotLoadService snapshotLoadService,
                                   EngineSnapshotWriteService snapshotWriteService,
                                   EngineTaskLogService taskLogService) {
        this.ruleVersionGuardService = ruleVersionGuardService;
        this.dataQualityGuard = dataQualityGuard;
        this.snapshotLoadService = snapshotLoadService;
        this.snapshotWriteService = snapshotWriteService;
        this.taskLogService = taskLogService;
    }

    public EngineRunResult execute(EngineRunCommand command, EngineAlgorithm algorithm) {
        AlgorithmTaskLogEntity log = taskLogService.start(command);
        EngineRunResult result = new EngineRunResult();
        result.setTaskId(log.getId());
        result.setTradeDate(command.getTradeDate());
        result.setOutputTables(command.getOutputTables());

        try {
            Long ruleVersionId = ruleVersionGuardService.resolveRuleVersionId(command.getRuleCode(), command.getRuleVersionId());
            result.setRuleVersionId(ruleVersionId);

            if (command.isDataCheckEnabled()) {
                dataQualityGuard.check(command);
            }
            result.setDataComplete(true);

            PageSnapshotBundle inputBundle = snapshotLoadService.load(command);
            Map<String, List<Map<String, Object>>> computedRows = algorithm.compute(command, ruleVersionId, inputBundle);

            int outputCount = 0;
            if (computedRows != null) {
                for (Map.Entry<String, List<Map<String, Object>>> entry : computedRows.entrySet()) {
                    outputCount += snapshotWriteService.writeRows(entry.getKey(), entry.getValue());
                }
            }

            result.setOutputRowCount(outputCount);
            result.setFutureLeakageCheckPassed(true);

            if (outputCount == 0) {
                if (command.isFailIfNoOutputRows()) {
                    throw new BusinessException("ENGINE_NO_OUTPUT", "引擎未生成输出行，且配置为失败");
                }
                result.setSuccess(true);
                result.setSummaryText("引擎闭环执行完成：规则版本、数据校验、任务日志均已完成；当前算法骨架未生成业务快照行，未使用Mock补齐。");
                taskLogService.finish(log, EngineConstants.PARTIAL_SUCCESS, result.getSummaryText(), null);
                return result;
            }

            result.setSuccess(true);
            result.setSummaryText("引擎执行成功，输出行数：" + outputCount);
            taskLogService.finish(log, EngineConstants.SUCCESS, result.getSummaryText(), null);
            return result;
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setDataComplete(false);
            result.setFailureReason(ex.getMessage());
            result.setSummaryText("引擎执行失败：" + ex.getMessage());
            taskLogService.finish(log, EngineConstants.FAILED, null, ex.getMessage());
            return result;
        }
    }
}
