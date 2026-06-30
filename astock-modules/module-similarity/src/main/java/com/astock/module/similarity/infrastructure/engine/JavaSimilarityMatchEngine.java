package com.astock.module.similarity.infrastructure.engine;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.engine.EngineExecutionTemplate;
import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.infrastructure.engine.EngineRunResult;
import com.astock.module.similarity.domain.engine.SimilarityMatchEngine;
import com.astock.module.similarity.domain.engine.SimilarityMatchEngineContext;
import com.astock.module.similarity.domain.engine.SimilarityMatchEngineResult;
import com.astock.module.similarity.domain.model.SimilarityFeatureVector;
import com.astock.module.similarity.domain.model.SimilarityMatchCandidate;
import com.astock.module.similarity.domain.service.SimilarityFeatureVectorExtractor;
import com.astock.module.similarity.domain.service.SimilarityNineDimensionScoringService;
import com.astock.module.similarity.domain.service.SimilarityOutputRowBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class JavaSimilarityMatchEngine implements SimilarityMatchEngine {
    private final EngineExecutionTemplate executionTemplate;
    private final SimilarityFeatureVectorExtractor featureVectorExtractor;
    private final SimilarityNineDimensionScoringService scoringService;
    private final SimilarityOutputRowBuilder outputRowBuilder;

    public JavaSimilarityMatchEngine(EngineExecutionTemplate executionTemplate,
                                     SimilarityFeatureVectorExtractor featureVectorExtractor,
                                     SimilarityNineDimensionScoringService scoringService,
                                     SimilarityOutputRowBuilder outputRowBuilder) {
        this.executionTemplate = executionTemplate;
        this.featureVectorExtractor = featureVectorExtractor;
        this.scoringService = scoringService;
        this.outputRowBuilder = outputRowBuilder;
    }

    @Override
    public SimilarityMatchEngineResult execute(SimilarityMatchEngineContext context) {
        EngineRunCommand command = new EngineRunCommand();
        command.setTaskId(context.getTaskId());
        command.setTaskName("历史相似行情匹配引擎");
        command.setTaskType("ENGINE_SIMILARITY_MATCH");
        command.setTradeDate(context.getTradeDate() == null ? LocalDate.now() : context.getTradeDate());
        command.setMarketScope(context.getMarketScope() == null ? "A_SHARE" : context.getMarketScope());
        command.setRuleCode("SIMILARITY_MATCH_CORE");
        command.setRuleVersionId(context.getRuleVersionId());
        command.setParamJson(context.getParamJson());
        command.setDataCheckEnabled(context.getDataCheckEnabled() == null || Boolean.TRUE.equals(context.getDataCheckEnabled()));

        command.setInputTables(List.of(
                "market_factor_snapshot",
                "limit_up_down_ecology_snapshot",
                "emotion_stage_snapshot",
                "mainline_daily_snapshot",
                "leader_daily_snapshot",
                "historical_cycle_sample"
        ));

        command.setOutputTables(List.of(
                "historical_similarity_match",
                "historical_similarity_factor_detail"
        ));
        command.setFailIfNoOutputRows(true);

        EngineRunResult runResult = executionTemplate.execute(command, this::compute);
        return toResult(runResult);
    }

    private Map<String, List<Map<String, Object>>> compute(EngineRunCommand command,
                                                           Long ruleVersionId,
                                                           PageSnapshotBundle inputBundle) {
        SimilarityFeatureVector current = featureVectorExtractor.current(command.getTradeDate(), inputBundle);

        List<SimilarityMatchCandidate> candidates = inputBundle.rows("historical_cycle_sample").stream()
                .filter(row -> featureVectorExtractor.historical(row).getTradeDate() != null)
                .filter(row -> featureVectorExtractor.historical(row).getTradeDate().isBefore(command.getTradeDate()))
                .map(row -> {
                    SimilarityFeatureVector historical = featureVectorExtractor.historical(row);
                    String matchType = String.valueOf(row.getOrDefault("sample_type", "SINGLE_DAY"));
                    return scoringService.score(matchType, current, historical);
                })
                .toList();

        List<SimilarityMatchCandidate> topCandidates = scoringService.topN(candidates, 30);
        return outputRowBuilder.buildRows(command, ruleVersionId, topCandidates);
    }

    private SimilarityMatchEngineResult toResult(EngineRunResult source) {
        SimilarityMatchEngineResult target = new SimilarityMatchEngineResult();
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
