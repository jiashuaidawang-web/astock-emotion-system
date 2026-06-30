package com.astock.module.mainline.infrastructure.engine;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.engine.EngineExecutionTemplate;
import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.infrastructure.engine.EngineRunResult;
import com.astock.module.mainline.domain.engine.MainlineEngineContext;
import com.astock.module.mainline.domain.engine.MainlineEngineResult;
import com.astock.module.mainline.domain.engine.MainlineRecognitionEngine;
import com.astock.module.mainline.domain.model.MainlineRecognitionContext;
import com.astock.module.mainline.domain.model.MainlineScore;
import com.astock.module.mainline.domain.model.MainlineThemeFeature;
import com.astock.module.mainline.domain.repository.MainlineRecognitionContextRepository;
import com.astock.module.mainline.domain.service.MainlineFeatureExtractor;
import com.astock.module.mainline.domain.service.MainlineOutputRowBuilder;
import com.astock.module.mainline.domain.service.MainlineStrengthScoringService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class JavaMainlineRecognitionEngine implements MainlineRecognitionEngine {
    private final EngineExecutionTemplate executionTemplate;
    private final MainlineRecognitionContextRepository contextRepository;
    private final MainlineFeatureExtractor featureExtractor;
    private final MainlineStrengthScoringService scoringService;
    private final MainlineOutputRowBuilder outputRowBuilder;

    public JavaMainlineRecognitionEngine(EngineExecutionTemplate executionTemplate,
                                         MainlineRecognitionContextRepository contextRepository,
                                         MainlineFeatureExtractor featureExtractor,
                                         MainlineStrengthScoringService scoringService,
                                         MainlineOutputRowBuilder outputRowBuilder) {
        this.executionTemplate = executionTemplate;
        this.contextRepository = contextRepository;
        this.featureExtractor = featureExtractor;
        this.scoringService = scoringService;
        this.outputRowBuilder = outputRowBuilder;
    }

    @Override
    public MainlineEngineResult execute(MainlineEngineContext context) {
        EngineRunCommand command = new EngineRunCommand();
        command.setTaskId(context.getTaskId());
        command.setTaskName("主线题材识别引擎");
        command.setTaskType("ENGINE_MAINLINE");
        command.setTradeDate(context.getTradeDate() == null ? LocalDate.now() : context.getTradeDate());
        command.setMarketScope(context.getMarketScope() == null ? "A_SHARE" : context.getMarketScope());
        command.setRuleCode("MAINLINE_RECOGNITION_CORE");
        command.setRuleVersionId(context.getRuleVersionId());
        command.setParamJson(context.getParamJson());
        command.setDataCheckEnabled(context.getDataCheckEnabled() == null || Boolean.TRUE.equals(context.getDataCheckEnabled()));

        /*
         * 主线识别的关键输入：
         * 1. theme_daily_snapshot：当日题材基础活跃数据。
         * 2. emotion_stage_snapshot：当日情绪阶段，用于情绪匹配度。
         *
         * sector_strength_snapshot、leader_daily_snapshot、过去主线通过 MainlineRecognitionContextRepository
         * 作为增强上下文加载，缺失不阻断基础主线识别。
         */
        command.setInputTables(List.of(
                "theme_daily_snapshot",
                "emotion_stage_snapshot"
        ));

        command.setOutputTables(List.of(
                "theme_strength_snapshot",
                "mainline_daily_snapshot",
                "mainline_switch_snapshot"
        ));
        command.setFailIfNoOutputRows(true);

        EngineRunResult runResult = executionTemplate.execute(command, this::compute);
        return toResult(runResult);
    }

    private Map<String, List<Map<String, Object>>> compute(EngineRunCommand command,
                                                           Long ruleVersionId,
                                                           PageSnapshotBundle inputBundle) {
        MainlineRecognitionContext recognitionContext = contextRepository.load(
                command.getTradeDate(),
                command.getMarketScope()
        );

        List<MainlineThemeFeature> features = featureExtractor.extract(
                command.getTradeDate(),
                command.getMarketScope(),
                inputBundle,
                recognitionContext
        );

        List<MainlineScore> scores = scoringService.score(features);
        return outputRowBuilder.buildRows(command, ruleVersionId, scores, recognitionContext);
    }

    private MainlineEngineResult toResult(EngineRunResult source) {
        MainlineEngineResult target = new MainlineEngineResult();
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
