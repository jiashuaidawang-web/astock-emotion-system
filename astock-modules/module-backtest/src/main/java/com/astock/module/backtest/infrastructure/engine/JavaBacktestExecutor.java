package com.astock.module.backtest.infrastructure.engine;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.engine.EngineExecutionTemplate;
import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.infrastructure.engine.EngineRunResult;
import com.astock.module.backtest.domain.engine.BacktestEngineContext;
import com.astock.module.backtest.domain.engine.BacktestEngineResult;
import com.astock.module.backtest.domain.engine.BacktestExecutor;
import com.astock.module.backtest.domain.model.BacktestFailureCase;
import com.astock.module.backtest.domain.model.BacktestLayerStat;
import com.astock.module.backtest.domain.model.BacktestReplayResult;
import com.astock.module.backtest.domain.model.BacktestReplaySample;
import com.astock.module.backtest.domain.repository.BacktestReplaySampleRepository;
import com.astock.module.backtest.domain.service.BacktestOutputRowBuilder;
import com.astock.module.backtest.domain.service.BacktestPerformanceCalculationService;
import com.astock.module.backtest.domain.service.BacktestReplaySampleBuilder;
import com.astock.module.backtest.domain.service.BacktestSignalReplayService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class JavaBacktestExecutor implements BacktestExecutor {
    private final EngineExecutionTemplate executionTemplate;
    private final BacktestReplaySampleRepository replaySampleRepository;
    private final BacktestReplaySampleBuilder replaySampleBuilder;
    private final BacktestSignalReplayService signalReplayService;
    private final BacktestPerformanceCalculationService performanceCalculationService;
    private final BacktestOutputRowBuilder outputRowBuilder;

    public JavaBacktestExecutor(EngineExecutionTemplate executionTemplate,
                                BacktestReplaySampleRepository replaySampleRepository,
                                BacktestReplaySampleBuilder replaySampleBuilder,
                                BacktestSignalReplayService signalReplayService,
                                BacktestPerformanceCalculationService performanceCalculationService,
                                BacktestOutputRowBuilder outputRowBuilder) {
        this.executionTemplate = executionTemplate;
        this.replaySampleRepository = replaySampleRepository;
        this.replaySampleBuilder = replaySampleBuilder;
        this.signalReplayService = signalReplayService;
        this.performanceCalculationService = performanceCalculationService;
        this.outputRowBuilder = outputRowBuilder;
    }

    @Override
    public BacktestEngineResult execute(BacktestEngineContext context) {
        EngineRunCommand command = new EngineRunCommand();
        command.setTaskId(context.getTaskId());
        command.setTaskName("回测执行引擎");
        command.setTaskType("ENGINE_BACKTEST");
        command.setTradeDate(context.getTradeDate() == null ? LocalDate.now() : context.getTradeDate());
        command.setMarketScope(context.getMarketScope() == null ? "A_SHARE" : context.getMarketScope());
        command.setRuleCode("BACKTEST_EXECUTION_CORE");
        command.setRuleVersionId(context.getRuleVersionId());
        command.setParamJson(context.getParamJson());

        /*
         * 回测不是T日生产信号，不使用EngineSnapshotLoadService的单日输入作为样本主体。
         * 样本主体由BacktestReplaySampleRepository按历史窗口读取。
         * 因此这里保留规则版本、任务日志、输出白名单、失败状态闭环，
         * 但关闭单日快照完整性检查，避免把历史窗口误判成T日输入缺失。
         */
        command.setDataCheckEnabled(false);
        command.setInputTables(List.of("historical_cycle_sample"));
        command.setOutputTables(List.of(
                "backtest_signal_detail",
                "backtest_performance_detail",
                "backtest_layer_stat",
                "backtest_failure_case"
        ));
        command.setFailIfNoOutputRows(true);

        EngineRunResult runResult = executionTemplate.execute(command, this::compute);
        return toResult(runResult);
    }

    private Map<String, List<Map<String, Object>>> compute(EngineRunCommand command,
                                                           Long ruleVersionId,
                                                           PageSnapshotBundle ignoredSingleDayBundle) {
        PageSnapshotBundle replayBundle = replaySampleRepository.loadReplayData(
                command.getTradeDate(),
                command.getMarketScope(),
                resolveSampleLimit(command.getParamJson())
        );

        List<BacktestReplaySample> samples = replaySampleBuilder.build(
                command.getTradeDate(),
                command.getMarketScope(),
                replayBundle
        );

        List<BacktestReplayResult> replayResults = signalReplayService.replay(samples);
        List<BacktestLayerStat> layerStats = performanceCalculationService.layerStats(replayResults);
        List<BacktestFailureCase> failureCases = performanceCalculationService.failureCases(replayResults);

        return outputRowBuilder.buildRows(
                command,
                ruleVersionId,
                replayResults,
                layerStats,
                failureCases
        );
    }

    private int resolveSampleLimit(String paramJson) {
        // 第十五段先给确定性默认值；后续可以从paramJson解析sampleLimit、startDate、endDate、patternCode。
        return 3000;
    }

    private BacktestEngineResult toResult(EngineRunResult source) {
        BacktestEngineResult target = new BacktestEngineResult();
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
