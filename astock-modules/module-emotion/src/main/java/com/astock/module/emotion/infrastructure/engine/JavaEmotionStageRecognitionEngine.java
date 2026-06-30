package com.astock.module.emotion.infrastructure.engine;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.engine.EngineExecutionTemplate;
import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.infrastructure.engine.EngineRunResult;
import com.astock.module.emotion.domain.engine.EmotionStageEngineContext;
import com.astock.module.emotion.domain.engine.EmotionStageEngineResult;
import com.astock.module.emotion.domain.engine.EmotionStageRecognitionEngine;
import com.astock.module.emotion.domain.model.EmotionHistoricalContext;
import com.astock.module.emotion.domain.model.EmotionMarketFeature;
import com.astock.module.emotion.domain.model.EmotionStageScore;
import com.astock.module.emotion.domain.repository.EmotionHistoricalContextRepository;
import com.astock.module.emotion.domain.service.EmotionMarketFeatureExtractor;
import com.astock.module.emotion.domain.service.EmotionStageOutputRowBuilder;
import com.astock.module.emotion.domain.service.EmotionStageScoringService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class JavaEmotionStageRecognitionEngine implements EmotionStageRecognitionEngine {
    private final EngineExecutionTemplate executionTemplate;
    private final EmotionMarketFeatureExtractor featureExtractor;
    private final EmotionStageScoringService scoringService;
    private final EmotionStageOutputRowBuilder outputRowBuilder;
    private final EmotionHistoricalContextRepository historicalContextRepository;

    public JavaEmotionStageRecognitionEngine(EngineExecutionTemplate executionTemplate,
                                             EmotionMarketFeatureExtractor featureExtractor,
                                             EmotionStageScoringService scoringService,
                                             EmotionStageOutputRowBuilder outputRowBuilder,
                                             EmotionHistoricalContextRepository historicalContextRepository) {
        this.executionTemplate = executionTemplate;
        this.featureExtractor = featureExtractor;
        this.scoringService = scoringService;
        this.outputRowBuilder = outputRowBuilder;
        this.historicalContextRepository = historicalContextRepository;
    }

    @Override
    public EmotionStageEngineResult execute(EmotionStageEngineContext context) {
        EngineRunCommand command = new EngineRunCommand();
        command.setTaskId(context.getTaskId());
        command.setTaskName("情绪周期识别引擎");
        command.setTaskType("ENGINE_EMOTION_STAGE");
        command.setTradeDate(context.getTradeDate() == null ? LocalDate.now() : context.getTradeDate());
        command.setMarketScope(context.getMarketScope() == null ? "A_SHARE" : context.getMarketScope());
        command.setRuleCode("EMOTION_STAGE_CORE");
        command.setRuleVersionId(context.getRuleVersionId());
        command.setParamJson(context.getParamJson());
        command.setDataCheckEnabled(context.getDataCheckEnabled() == null || Boolean.TRUE.equals(context.getDataCheckEnabled()));

        /*
         * 当日基础判断强制依赖市场画像与涨跌停生态。
         * 历史样本、人工确认、过去N日路径通过 EmotionHistoricalContextRepository 作为增强项加载。
         * 增强项缺失不阻断基础阶段识别，但会在 evidence_json / risk_json 中体现。
         */
        command.setInputTables(List.of(
                "market_factor_snapshot",
                "limit_up_down_ecology_snapshot"
        ));

        command.setOutputTables(List.of(
                "emotion_stage_snapshot",
                "emotion_stage_score_detail",
                "stage_transition_snapshot"
        ));
        command.setFailIfNoOutputRows(true);

        EngineRunResult runResult = executionTemplate.execute(command, this::compute);
        return toResult(runResult);
    }

    private Map<String, List<Map<String, Object>>> compute(EngineRunCommand command,
                                                           Long ruleVersionId,
                                                           PageSnapshotBundle inputBundle) {
        EmotionMarketFeature feature = featureExtractor.extract(
                command.getTradeDate(),
                command.getMarketScope(),
                inputBundle
        );

        EmotionHistoricalContext historicalContext = historicalContextRepository.load(
                command.getTradeDate(),
                command.getMarketScope(),
                resolvePathWindowDays(command.getParamJson())
        );

        List<EmotionStageScore> scores = scoringService.score(feature, historicalContext);
        return outputRowBuilder.buildRows(command, ruleVersionId, scores, historicalContext);
    }

    private int resolvePathWindowDays(String paramJson) {
        // 先给确定性默认值，后续可以从paramJson解析 pathWindowDays。
        return 10;
    }

    private EmotionStageEngineResult toResult(EngineRunResult source) {
        EmotionStageEngineResult target = new EmotionStageEngineResult();
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
