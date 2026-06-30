package com.astock.module.pattern.infrastructure.engine;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.engine.EngineExecutionTemplate;
import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.infrastructure.engine.EngineRunResult;
import com.astock.module.pattern.domain.engine.PatternConditionEngine;
import com.astock.module.pattern.domain.engine.PatternEngineContext;
import com.astock.module.pattern.domain.engine.PatternEngineResult;
import com.astock.module.pattern.domain.model.PatternEngineSupportContext;
import com.astock.module.pattern.domain.model.PatternSignalScore;
import com.astock.module.pattern.domain.model.PatternWatchObject;
import com.astock.module.pattern.domain.repository.PatternEngineSupportContextRepository;
import com.astock.module.pattern.domain.service.PatternConditionScoringService;
import com.astock.module.pattern.domain.service.PatternOutputRowBuilder;
import com.astock.module.pattern.domain.service.PatternWatchPoolBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class JavaPatternConditionEngine implements PatternConditionEngine {
    private final EngineExecutionTemplate executionTemplate;
    private final PatternEngineSupportContextRepository supportContextRepository;
    private final PatternWatchPoolBuilder watchPoolBuilder;
    private final PatternConditionScoringService scoringService;
    private final PatternOutputRowBuilder outputRowBuilder;

    public JavaPatternConditionEngine(EngineExecutionTemplate executionTemplate,
                                      PatternEngineSupportContextRepository supportContextRepository,
                                      PatternWatchPoolBuilder watchPoolBuilder,
                                      PatternConditionScoringService scoringService,
                                      PatternOutputRowBuilder outputRowBuilder) {
        this.executionTemplate = executionTemplate;
        this.supportContextRepository = supportContextRepository;
        this.watchPoolBuilder = watchPoolBuilder;
        this.scoringService = scoringService;
        this.outputRowBuilder = outputRowBuilder;
    }

    @Override
    public PatternEngineResult execute(PatternEngineContext context) {
        EngineRunCommand command = new EngineRunCommand();
        command.setTaskId(context.getTaskId());
        command.setTaskName("买点条件判定引擎");
        command.setTaskType("ENGINE_PATTERN");
        command.setTradeDate(context.getTradeDate() == null ? LocalDate.now() : context.getTradeDate());
        command.setMarketScope(context.getMarketScope() == null ? "A_SHARE" : context.getMarketScope());
        command.setRuleCode("PATTERN_CONDITION_CORE");
        command.setRuleVersionId(context.getRuleVersionId());
        command.setParamJson(context.getParamJson());
        command.setDataCheckEnabled(context.getDataCheckEnabled() == null || Boolean.TRUE.equals(context.getDataCheckEnabled()));

        /*
         * 关键输入：
         * 1. emotion_stage_snapshot：周期准入。
         * 2. mainline_daily_snapshot：主线准入。
         * 3. leader_daily_snapshot：观察对象池和龙头地位。
         * 4. risk_signal_snapshot：风险否决。
         */
        command.setInputTables(List.of(
                "emotion_stage_snapshot",
                "mainline_daily_snapshot",
                "leader_daily_snapshot",
                "risk_signal_snapshot"
        ));

        command.setOutputTables(List.of(
                "buy_pattern_signal_snapshot",
                "pattern_risk_veto_snapshot"
        ));
        command.setFailIfNoOutputRows(true);

        EngineRunResult runResult = executionTemplate.execute(command, this::compute);
        return toResult(runResult);
    }

    private Map<String, List<Map<String, Object>>> compute(EngineRunCommand command,
                                                           Long ruleVersionId,
                                                           PageSnapshotBundle inputBundle) {
        PatternEngineSupportContext supportContext = supportContextRepository.load(
                command.getTradeDate(),
                command.getMarketScope()
        );

        List<PatternWatchObject> watchPool = watchPoolBuilder.build(
                command.getTradeDate(),
                command.getMarketScope(),
                inputBundle
        );

        List<PatternSignalScore> signals = scoringService.score(watchPool, supportContext);
        return outputRowBuilder.buildRows(command, ruleVersionId, signals);
    }

    private PatternEngineResult toResult(EngineRunResult source) {
        PatternEngineResult target = new PatternEngineResult();
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
