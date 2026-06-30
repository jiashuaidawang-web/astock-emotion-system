package com.astock.app.orchestration;

import com.astock.app.orchestration.entity.EngineBatchRunLogEntity;
import com.astock.app.orchestration.entity.EngineBatchStepLogEntity;
import com.astock.module.agentaudit.domain.engine.AgentAuditEngineContext;
import com.astock.module.agentaudit.domain.engine.AgentAuditExecutor;
import com.astock.module.backtest.domain.engine.BacktestEngineContext;
import com.astock.module.backtest.domain.engine.BacktestExecutor;
import com.astock.module.emotion.domain.engine.EmotionStageEngineContext;
import com.astock.module.emotion.domain.engine.EmotionStageRecognitionEngine;
import com.astock.module.leader.domain.engine.LeaderEngineContext;
import com.astock.module.leader.domain.engine.LeaderRecognitionEngine;
import com.astock.module.mainline.domain.engine.MainlineEngineContext;
import com.astock.module.mainline.domain.engine.MainlineRecognitionEngine;
import com.astock.module.pattern.domain.engine.PatternConditionEngine;
import com.astock.module.pattern.domain.engine.PatternEngineContext;
import com.astock.module.risk.domain.engine.RiskControlEngine;
import com.astock.module.risk.domain.engine.RiskEngineContext;
import com.astock.module.similarity.domain.engine.SimilarityMatchEngine;
import com.astock.module.similarity.domain.engine.SimilarityMatchEngineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EngineBatchOrchestrationService {
    private final EmotionStageRecognitionEngine emotionEngine;
    private final MainlineRecognitionEngine mainlineEngine;
    private final LeaderRecognitionEngine leaderEngine;
    private final RiskControlEngine riskEngine;
    private final PatternConditionEngine patternEngine;
    private final SimilarityMatchEngine similarityEngine;
    private final BacktestExecutor backtestExecutor;
    private final AgentAuditExecutor agentAuditExecutor;
    private final EngineBatchRunLogMapper batchLogMapper;
    private final EngineBatchStepLogMapper stepLogMapper;

    public EngineBatchOrchestrationService(EmotionStageRecognitionEngine emotionEngine,
                                           MainlineRecognitionEngine mainlineEngine,
                                           LeaderRecognitionEngine leaderEngine,
                                           RiskControlEngine riskEngine,
                                           PatternConditionEngine patternEngine,
                                           SimilarityMatchEngine similarityEngine,
                                           BacktestExecutor backtestExecutor,
                                           AgentAuditExecutor agentAuditExecutor,
                                           EngineBatchRunLogMapper batchLogMapper,
                                           EngineBatchStepLogMapper stepLogMapper) {
        this.emotionEngine = emotionEngine;
        this.mainlineEngine = mainlineEngine;
        this.leaderEngine = leaderEngine;
        this.riskEngine = riskEngine;
        this.patternEngine = patternEngine;
        this.similarityEngine = similarityEngine;
        this.backtestExecutor = backtestExecutor;
        this.agentAuditExecutor = agentAuditExecutor;
        this.batchLogMapper = batchLogMapper;
        this.stepLogMapper = stepLogMapper;
    }

    @Transactional
    public EngineBatchRunResult runDailyBatch(EngineBatchRunRequest request) {
        LocalDateTime batchStart = LocalDateTime.now();
        LocalDate tradeDate = request.getTradeDate() == null ? LocalDate.now() : request.getTradeDate();
        String marketScope = request.getMarketScope() == null || request.getMarketScope().isBlank()
                ? "A_SHARE"
                : request.getMarketScope();

        EngineBatchRunLogEntity batchRecord = new EngineBatchRunLogEntity();
        batchRecord.setTradeDate(tradeDate);
        batchRecord.setMarketScope(marketScope);
        batchRecord.setBatchStatus("RUNNING");
        batchRecord.setStartedAt(batchStart);
        batchRecord.setRequestJson(requestJson(request));
        batchLogMapper.insert(batchRecord);

        EngineBatchRunResult batchResult = new EngineBatchRunResult();
        batchResult.setBatchId(batchRecord.getId());
        batchResult.setTradeDate(tradeDate);
        batchResult.setMarketScope(marketScope);
        batchResult.setStartedAt(batchStart);
        batchResult.setBatchStatus("RUNNING");

        List<EngineStep> steps = buildSteps(request);
        List<EngineBatchStepResult> stepResults = new ArrayList<>();

        for (EngineStep step : steps) {
            EngineBatchStepResult stepResult = executeStep(batchRecord.getId(), step, tradeDate, marketScope, request);
            stepResults.add(stepResult);
            if (!Boolean.TRUE.equals(stepResult.getSuccess()) && !Boolean.TRUE.equals(request.getContinueOnFailure())) {
                break;
            }
        }

        LocalDateTime batchEnd = LocalDateTime.now();
        int successCount = (int) stepResults.stream().filter(s -> Boolean.TRUE.equals(s.getSuccess())).count();
        int failedCount = (int) stepResults.stream().filter(s -> !Boolean.TRUE.equals(s.getSuccess())).count();
        String failureReason = stepResults.stream()
                .filter(s -> !Boolean.TRUE.equals(s.getSuccess()))
                .map(EngineBatchStepResult::getFailureReason)
                .findFirst()
                .orElse(null);

        batchRecord.setBatchStatus(failedCount == 0 && stepResults.size() == steps.size() ? "SUCCESS" : "FAILED");
        batchRecord.setFinishedAt(batchEnd);
        batchRecord.setCostMillis(Duration.between(batchStart, batchEnd).toMillis());
        batchRecord.setTotalStepCount(steps.size());
        batchRecord.setSuccessStepCount(successCount);
        batchRecord.setFailedStepCount(failedCount);
        batchRecord.setFailureReason(failureReason);
        batchLogMapper.updateById(batchRecord);

        batchResult.setSteps(stepResults);
        batchResult.setFinishedAt(batchEnd);
        batchResult.setCostMillis(Duration.between(batchStart, batchEnd).toMillis());
        batchResult.setTotalStepCount(steps.size());
        batchResult.setSuccessStepCount(successCount);
        batchResult.setFailedStepCount(failedCount);
        batchResult.setSuccess(failedCount == 0 && stepResults.size() == steps.size());
        batchResult.setBatchStatus(batchRecord.getBatchStatus());
        batchResult.setFailureReason(failureReason);
        return batchResult;
    }

    private List<EngineStep> buildSteps(EngineBatchRunRequest request) {
        List<EngineStep> steps = new ArrayList<>();
        steps.add(new EngineStep(1, "EMOTION_STAGE", "情绪周期识别", "EmotionStageRecognitionEngine"));
        steps.add(new EngineStep(2, "MAINLINE", "主线题材识别", "MainlineRecognitionEngine"));
        steps.add(new EngineStep(3, "LEADER", "龙头梯队识别", "LeaderRecognitionEngine"));
        steps.add(new EngineStep(4, "RISK_PRE_PATTERN", "风控预检查", "RiskControlEngine"));
        steps.add(new EngineStep(5, "PATTERN", "模式条件判定", "PatternConditionEngine"));
        if (!Boolean.FALSE.equals(request.getRerunRiskAfterPattern())) {
            steps.add(new EngineStep(6, "RISK_AFTER_PATTERN", "风控二次覆盖", "RiskControlEngine"));
        }
        steps.add(new EngineStep(7, "SIMILARITY", "历史相似行情匹配", "SimilarityMatchEngine"));
        if (Boolean.TRUE.equals(request.getRunBacktest())) {
            steps.add(new EngineStep(8, "BACKTEST", "历史回测执行", "BacktestExecutor"));
        }
        if (!Boolean.FALSE.equals(request.getRunAgentAudit())) {
            steps.add(new EngineStep(9, "AGENT_AUDIT", "Agent研发审计", "AgentAuditExecutor"));
        }
        return steps;
    }

    private EngineBatchStepResult executeStep(Long batchId,
                                              EngineStep step,
                                              LocalDate tradeDate,
                                              String marketScope,
                                              EngineBatchRunRequest request) {
        LocalDateTime startedAt = LocalDateTime.now();
        EngineBatchStepResult stepResult = new EngineBatchStepResult();
        stepResult.setStepNo(step.stepNo());
        stepResult.setStepCode(step.stepCode());
        stepResult.setStepName(step.stepName());
        stepResult.setEngineName(step.engineName());
        stepResult.setStartedAt(startedAt);

        try {
            EngineCommonResult result = switch (step.stepCode()) {
                case "EMOTION_STAGE" -> runEmotion(tradeDate, marketScope, request);
                case "MAINLINE" -> runMainline(tradeDate, marketScope, request);
                case "LEADER" -> runLeader(tradeDate, marketScope, request);
                case "RISK_PRE_PATTERN", "RISK_AFTER_PATTERN" -> runRisk(tradeDate, marketScope, request);
                case "PATTERN" -> runPattern(tradeDate, marketScope, request);
                case "SIMILARITY" -> runSimilarity(tradeDate, marketScope, request);
                case "BACKTEST" -> runBacktest(tradeDate, marketScope, request);
                case "AGENT_AUDIT" -> runAgentAudit(tradeDate, marketScope, request);
                default -> throw new IllegalArgumentException("未知步骤：" + step.stepCode());
            };

            stepResult.setSuccess(result.success());
            stepResult.setTaskId(result.taskId());
            stepResult.setOutputRowCount(result.outputRowCount());
            stepResult.setOutputTables(result.outputTables());
            stepResult.setFailureReason(result.failureReason());
            stepResult.setSummaryText(result.summaryText());
        } catch (Exception ex) {
            stepResult.setSuccess(false);
            stepResult.setFailureReason(ex.getMessage());
            stepResult.setSummaryText("步骤执行失败：" + ex.getMessage());
        }

        LocalDateTime finishedAt = LocalDateTime.now();
        stepResult.setFinishedAt(finishedAt);
        stepResult.setCostMillis(Duration.between(startedAt, finishedAt).toMillis());
        insertStepLog(batchId, stepResult);
        return stepResult;
    }

    private EngineCommonResult runEmotion(LocalDate tradeDate, String marketScope, EngineBatchRunRequest request) {
        EmotionStageEngineContext c = new EmotionStageEngineContext();
        fill(c, tradeDate, marketScope, request);
        var r = emotionEngine.execute(c);
        return new EngineCommonResult(r.getSuccess(), r.getTaskId(), r.getOutputRowCount(), r.getOutputTables(), r.getFailureReason(), r.getSummaryText());
    }

    private EngineCommonResult runMainline(LocalDate tradeDate, String marketScope, EngineBatchRunRequest request) {
        MainlineEngineContext c = new MainlineEngineContext();
        fill(c, tradeDate, marketScope, request);
        var r = mainlineEngine.execute(c);
        return new EngineCommonResult(r.getSuccess(), r.getTaskId(), r.getOutputRowCount(), r.getOutputTables(), r.getFailureReason(), r.getSummaryText());
    }

    private EngineCommonResult runLeader(LocalDate tradeDate, String marketScope, EngineBatchRunRequest request) {
        LeaderEngineContext c = new LeaderEngineContext();
        fill(c, tradeDate, marketScope, request);
        var r = leaderEngine.execute(c);
        return new EngineCommonResult(r.getSuccess(), r.getTaskId(), r.getOutputRowCount(), r.getOutputTables(), r.getFailureReason(), r.getSummaryText());
    }

    private EngineCommonResult runRisk(LocalDate tradeDate, String marketScope, EngineBatchRunRequest request) {
        RiskEngineContext c = new RiskEngineContext();
        fill(c, tradeDate, marketScope, request);
        var r = riskEngine.execute(c);
        return new EngineCommonResult(r.getSuccess(), r.getTaskId(), r.getOutputRowCount(), r.getOutputTables(), r.getFailureReason(), r.getSummaryText());
    }

    private EngineCommonResult runPattern(LocalDate tradeDate, String marketScope, EngineBatchRunRequest request) {
        PatternEngineContext c = new PatternEngineContext();
        fill(c, tradeDate, marketScope, request);
        var r = patternEngine.execute(c);
        return new EngineCommonResult(r.getSuccess(), r.getTaskId(), r.getOutputRowCount(), r.getOutputTables(), r.getFailureReason(), r.getSummaryText());
    }

    private EngineCommonResult runSimilarity(LocalDate tradeDate, String marketScope, EngineBatchRunRequest request) {
        SimilarityMatchEngineContext c = new SimilarityMatchEngineContext();
        fill(c, tradeDate, marketScope, request);
        var r = similarityEngine.execute(c);
        return new EngineCommonResult(r.getSuccess(), r.getTaskId(), r.getOutputRowCount(), r.getOutputTables(), r.getFailureReason(), r.getSummaryText());
    }

    private EngineCommonResult runBacktest(LocalDate tradeDate, String marketScope, EngineBatchRunRequest request) {
        BacktestEngineContext c = new BacktestEngineContext();
        fill(c, tradeDate, marketScope, request);
        var r = backtestExecutor.execute(c);
        return new EngineCommonResult(r.getSuccess(), r.getTaskId(), r.getOutputRowCount(), r.getOutputTables(), r.getFailureReason(), r.getSummaryText());
    }

    private EngineCommonResult runAgentAudit(LocalDate tradeDate, String marketScope, EngineBatchRunRequest request) {
        AgentAuditEngineContext c = new AgentAuditEngineContext();
        fill(c, tradeDate, marketScope, request);
        var r = agentAuditExecutor.execute(c);
        return new EngineCommonResult(r.getSuccess(), r.getTaskId(), r.getOutputRowCount(), r.getOutputTables(), r.getFailureReason(), r.getSummaryText());
    }

    private void fill(EmotionStageEngineContext c, LocalDate tradeDate, String marketScope, EngineBatchRunRequest r) {
        c.setTradeDate(tradeDate); c.setMarketScope(marketScope); c.setRuleVersionId(r.getRuleVersionId()); c.setDataCheckEnabled(r.getDataCheckEnabled()); c.setParamJson(r.getParamJson());
    }
    private void fill(MainlineEngineContext c, LocalDate tradeDate, String marketScope, EngineBatchRunRequest r) {
        c.setTradeDate(tradeDate); c.setMarketScope(marketScope); c.setRuleVersionId(r.getRuleVersionId()); c.setDataCheckEnabled(r.getDataCheckEnabled()); c.setParamJson(r.getParamJson());
    }
    private void fill(LeaderEngineContext c, LocalDate tradeDate, String marketScope, EngineBatchRunRequest r) {
        c.setTradeDate(tradeDate); c.setMarketScope(marketScope); c.setRuleVersionId(r.getRuleVersionId()); c.setDataCheckEnabled(r.getDataCheckEnabled()); c.setParamJson(r.getParamJson());
    }
    private void fill(RiskEngineContext c, LocalDate tradeDate, String marketScope, EngineBatchRunRequest r) {
        c.setTradeDate(tradeDate); c.setMarketScope(marketScope); c.setRuleVersionId(r.getRuleVersionId()); c.setDataCheckEnabled(r.getDataCheckEnabled()); c.setParamJson(r.getParamJson());
    }
    private void fill(PatternEngineContext c, LocalDate tradeDate, String marketScope, EngineBatchRunRequest r) {
        c.setTradeDate(tradeDate); c.setMarketScope(marketScope); c.setRuleVersionId(r.getRuleVersionId()); c.setDataCheckEnabled(r.getDataCheckEnabled()); c.setParamJson(r.getParamJson());
    }
    private void fill(SimilarityMatchEngineContext c, LocalDate tradeDate, String marketScope, EngineBatchRunRequest r) {
        c.setTradeDate(tradeDate); c.setMarketScope(marketScope); c.setRuleVersionId(r.getRuleVersionId()); c.setDataCheckEnabled(r.getDataCheckEnabled()); c.setParamJson(r.getParamJson());
    }
    private void fill(BacktestEngineContext c, LocalDate tradeDate, String marketScope, EngineBatchRunRequest r) {
        c.setTradeDate(tradeDate); c.setMarketScope(marketScope); c.setRuleVersionId(r.getRuleVersionId()); c.setDataCheckEnabled(r.getDataCheckEnabled()); c.setParamJson(r.getParamJson());
    }
    private void fill(AgentAuditEngineContext c, LocalDate tradeDate, String marketScope, EngineBatchRunRequest r) {
        c.setTradeDate(tradeDate); c.setMarketScope(marketScope); c.setRuleVersionId(r.getRuleVersionId()); c.setDataCheckEnabled(r.getDataCheckEnabled()); c.setParamJson(r.getParamJson());
    }

    private void insertStepLog(Long batchId, EngineBatchStepResult result) {
        EngineBatchStepLogEntity record = new EngineBatchStepLogEntity();
        record.setBatchId(batchId);
        record.setStepNo(result.getStepNo());
        record.setStepCode(result.getStepCode());
        record.setStepName(result.getStepName());
        record.setEngineName(result.getEngineName());
        record.setStepStatus(Boolean.TRUE.equals(result.getSuccess()) ? "SUCCESS" : "FAILED");
        record.setTaskId(result.getTaskId());
        record.setOutputRowCount(result.getOutputRowCount());
        record.setOutputTables(result.getOutputTables() == null ? "[]" : result.getOutputTables().toString());
        record.setFailureReason(result.getFailureReason());
        record.setSummaryText(result.getSummaryText());
        record.setStartedAt(result.getStartedAt());
        record.setFinishedAt(result.getFinishedAt());
        record.setCostMillis(result.getCostMillis());
        stepLogMapper.insert(record);
    }

    private String requestJson(EngineBatchRunRequest request) {
        return "{"
                + "\"tradeDate\":\"" + request.getTradeDate() + "\","
                + "\"marketScope\":\"" + request.getMarketScope() + "\","
                + "\"ruleVersionId\":" + request.getRuleVersionId() + ","
                + "\"dataCheckEnabled\":" + request.getDataCheckEnabled() + ","
                + "\"continueOnFailure\":" + request.getContinueOnFailure() + ","
                + "\"runBacktest\":" + request.getRunBacktest() + ","
                + "\"runAgentAudit\":" + request.getRunAgentAudit()
                + "}";
    }

    private record EngineStep(Integer stepNo, String stepCode, String stepName, String engineName) {}

    private record EngineCommonResult(Boolean success,
                                      Long taskId,
                                      Integer outputRowCount,
                                      List<String> outputTables,
                                      String failureReason,
                                      String summaryText) {}
}
