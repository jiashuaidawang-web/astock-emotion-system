package com.astock.module.leader.infrastructure.engine;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.engine.EngineExecutionTemplate;
import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.infrastructure.engine.EngineRunResult;
import com.astock.module.leader.domain.engine.LeaderEngineContext;
import com.astock.module.leader.domain.engine.LeaderEngineResult;
import com.astock.module.leader.domain.engine.LeaderRecognitionEngine;
import com.astock.module.leader.domain.model.LeaderCandidateFeature;
import com.astock.module.leader.domain.model.LeaderRecognitionContext;
import com.astock.module.leader.domain.model.LeaderScore;
import com.astock.module.leader.domain.repository.LeaderRecognitionContextRepository;
import com.astock.module.leader.domain.service.LeaderFeatureExtractor;
import com.astock.module.leader.domain.service.LeaderOutputRowBuilder;
import com.astock.module.leader.domain.service.LeaderScoreCalculationService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class JavaLeaderRecognitionEngine implements LeaderRecognitionEngine {
    private final EngineExecutionTemplate executionTemplate;
    private final LeaderRecognitionContextRepository contextRepository;
    private final LeaderFeatureExtractor featureExtractor;
    private final LeaderScoreCalculationService scoreCalculationService;
    private final LeaderOutputRowBuilder outputRowBuilder;

    public JavaLeaderRecognitionEngine(EngineExecutionTemplate executionTemplate,
                                       LeaderRecognitionContextRepository contextRepository,
                                       LeaderFeatureExtractor featureExtractor,
                                       LeaderScoreCalculationService scoreCalculationService,
                                       LeaderOutputRowBuilder outputRowBuilder) {
        this.executionTemplate = executionTemplate;
        this.contextRepository = contextRepository;
        this.featureExtractor = featureExtractor;
        this.scoreCalculationService = scoreCalculationService;
        this.outputRowBuilder = outputRowBuilder;
    }

    @Override
    public LeaderEngineResult execute(LeaderEngineContext context) {
        EngineRunCommand command = new EngineRunCommand();
        command.setTaskId(context.getTaskId());
        command.setTaskName("龙头识别引擎");
        command.setTaskType("ENGINE_LEADER");
        command.setTradeDate(context.getTradeDate() == null ? LocalDate.now() : context.getTradeDate());
        command.setMarketScope(context.getMarketScope() == null ? "A_SHARE" : context.getMarketScope());
        command.setRuleCode("LEADER_RECOGNITION_CORE");
        command.setRuleVersionId(context.getRuleVersionId());
        command.setParamJson(context.getParamJson());
        command.setDataCheckEnabled(context.getDataCheckEnabled() == null || Boolean.TRUE.equals(context.getDataCheckEnabled()));

        /*
         * 关键输入：
         * 1. stock_daily_kline：个股行情与基础强度。
         * 2. mainline_daily_snapshot：主线关联评分。
         * 3. sector_strength_snapshot：板块带动评分。
         * 4. limit_up_down_ecology_snapshot：市场梯队高度上下文。
         *
         * previous leader / risk / pattern 通过 LeaderRecognitionContextRepository 加载为增强上下文。
         */
        command.setInputTables(List.of(
                "stock_daily_kline",
                "mainline_daily_snapshot",
                "sector_strength_snapshot",
                "limit_up_down_ecology_snapshot"
        ));

        command.setOutputTables(List.of(
                "leader_daily_snapshot",
                "leader_ladder_snapshot",
                "leader_drive_snapshot",
                "leader_negative_feedback"
        ));
        command.setFailIfNoOutputRows(true);

        EngineRunResult runResult = executionTemplate.execute(command, this::compute);
        return toResult(runResult);
    }

    private Map<String, List<Map<String, Object>>> compute(EngineRunCommand command,
                                                           Long ruleVersionId,
                                                           PageSnapshotBundle inputBundle) {
        LeaderRecognitionContext recognitionContext = contextRepository.load(
                command.getTradeDate(),
                command.getMarketScope()
        );

        List<LeaderCandidateFeature> features = featureExtractor.extract(
                command.getTradeDate(),
                command.getMarketScope(),
                inputBundle
        );

        List<LeaderScore> scores = scoreCalculationService.score(features, recognitionContext);
        return outputRowBuilder.buildRows(command, ruleVersionId, scores);
    }

    private LeaderEngineResult toResult(EngineRunResult source) {
        LeaderEngineResult target = new LeaderEngineResult();
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
