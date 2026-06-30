package com.astock.module.risk.infrastructure.engine;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.engine.EngineExecutionTemplate;
import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.infrastructure.engine.EngineRunResult;
import com.astock.module.risk.domain.engine.RiskControlEngine;
import com.astock.module.risk.domain.engine.RiskEngineContext;
import com.astock.module.risk.domain.engine.RiskEngineResult;
import com.astock.module.risk.domain.model.RiskControlContext;
import com.astock.module.risk.domain.model.RiskFactorSnapshot;
import com.astock.module.risk.domain.model.RiskSignalScore;
import com.astock.module.risk.domain.repository.RiskControlContextRepository;
import com.astock.module.risk.domain.service.RiskFactorExtractor;
import com.astock.module.risk.domain.service.RiskOutputRowBuilder;
import com.astock.module.risk.domain.service.RiskScoreCalculationService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class JavaRiskControlEngine implements RiskControlEngine {
    private final EngineExecutionTemplate executionTemplate;
    private final RiskControlContextRepository contextRepository;
    private final RiskFactorExtractor factorExtractor;
    private final RiskScoreCalculationService scoreCalculationService;
    private final RiskOutputRowBuilder outputRowBuilder;

    public JavaRiskControlEngine(EngineExecutionTemplate executionTemplate,
                                 RiskControlContextRepository contextRepository,
                                 RiskFactorExtractor factorExtractor,
                                 RiskScoreCalculationService scoreCalculationService,
                                 RiskOutputRowBuilder outputRowBuilder) {
        this.executionTemplate = executionTemplate;
        this.contextRepository = contextRepository;
        this.factorExtractor = factorExtractor;
        this.scoreCalculationService = scoreCalculationService;
        this.outputRowBuilder = outputRowBuilder;
    }

    @Override
    public RiskEngineResult execute(RiskEngineContext context) {
        EngineRunCommand command = new EngineRunCommand();
        command.setTaskId(context.getTaskId());
        command.setTaskName("风控与失效引擎");
        command.setTaskType("ENGINE_RISK");
        command.setTradeDate(context.getTradeDate() == null ? LocalDate.now() : context.getTradeDate());
        command.setMarketScope(context.getMarketScope() == null ? "A_SHARE" : context.getMarketScope());
        command.setRuleCode("RISK_CONTROL_CORE");
        command.setRuleVersionId(context.getRuleVersionId());
        command.setParamJson(context.getParamJson());
        command.setDataCheckEnabled(context.getDataCheckEnabled() == null || Boolean.TRUE.equals(context.getDataCheckEnabled()));

        /*
         * 风控关键输入：
         * 1. emotion_stage_snapshot：周期风险。
         * 2. market_factor_snapshot：亏钱效应、指数资金风险。
         * 3. limit_up_down_ecology_snapshot：涨跌停生态风险。
         * 4. leader_negative_feedback：龙头负反馈。
         * 5. mainline_daily_snapshot：主线衰退风险。
         *
         * buy_pattern_signal_snapshot / data_quality_check_log / risk_action_matrix
         * 作为上级保护层上下文由 RiskControlContextRepository 加载。
         */
        command.setInputTables(List.of(
                "emotion_stage_snapshot",
                "market_factor_snapshot",
                "limit_up_down_ecology_snapshot",
                "leader_negative_feedback",
                "mainline_daily_snapshot"
        ));

        command.setOutputTables(List.of(
                "risk_signal_snapshot",
                "risk_signal_detail",
                "pattern_risk_veto_snapshot"
        ));
        command.setFailIfNoOutputRows(true);

        EngineRunResult runResult = executionTemplate.execute(command, this::compute);
        return toResult(runResult);
    }

    private Map<String, List<Map<String, Object>>> compute(EngineRunCommand command,
                                                           Long ruleVersionId,
                                                           PageSnapshotBundle inputBundle) {
        RiskControlContext riskContext = contextRepository.load(
                command.getTradeDate(),
                command.getMarketScope()
        );

        RiskFactorSnapshot factor = factorExtractor.extract(
                command.getTradeDate(),
                command.getMarketScope(),
                inputBundle,
                riskContext
        );

        List<RiskSignalScore> signals = scoreCalculationService.score(factor);
        return outputRowBuilder.buildRows(command, ruleVersionId, factor, signals, riskContext);
    }

    private RiskEngineResult toResult(EngineRunResult source) {
        RiskEngineResult target = new RiskEngineResult();
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
