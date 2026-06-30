package com.astock.module.backtest.domain.service;

import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.module.backtest.domain.model.BacktestFailureCase;
import com.astock.module.backtest.domain.model.BacktestLayerStat;
import com.astock.module.backtest.domain.model.BacktestReplayResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BacktestOutputRowBuilder {

    public Map<String, List<Map<String, Object>>> buildRows(EngineRunCommand command,
                                                            Long ruleVersionId,
                                                            List<BacktestReplayResult> replayResults,
                                                            List<BacktestLayerStat> layerStats,
                                                            List<BacktestFailureCase> failureCases) {
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        result.put("backtest_signal_detail", buildSignalDetailRows(command, ruleVersionId, replayResults));
        result.put("backtest_performance_detail", buildPerformanceRows(command, ruleVersionId, layerStats));
        result.put("backtest_layer_stat", buildLayerStatRows(command, ruleVersionId, layerStats));
        result.put("backtest_failure_case", buildFailureRows(command, ruleVersionId, failureCases));
        return result;
    }

    private List<Map<String, Object>> buildSignalDetailRows(EngineRunCommand command,
                                                            Long ruleVersionId,
                                                            List<BacktestReplayResult> results) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (BacktestReplayResult result : results) {
            Map<String, Object> row = baseRow(command, ruleVersionId);
            row.put("sample_id", result.getSample().getSampleId());
            row.put("sample_date", result.getSample().getSampleDate());
            row.put("stage_code", result.getSample().getStageCode());
            row.put("pattern_code", result.getSample().getPatternCode());
            row.put("stock_code", result.getSample().getStockCode());
            row.put("stock_name", result.getSample().getStockName());
            row.put("mainline_code", result.getSample().getMainlineCode());
            row.put("mainline_name", result.getSample().getMainlineName());
            row.put("signal_score", result.getSample().getSignalScore());
            row.put("risk_score", result.getSample().getRiskScore());
            row.put("risk_action", result.getSample().getRiskAction());
            row.put("signal_effective", result.getSignalEffective());
            row.put("risk_vetoed", result.getRiskVetoed());
            row.put("replay_status", result.getReplayStatus());
            row.put("replay_return", result.getReplayReturn());
            row.put("replay_drawdown", result.getReplayDrawdown());
            row.put("future_1d_return", result.getSample().getFuture1dReturn());
            row.put("future_3d_return", result.getSample().getFuture3dReturn());
            row.put("future_5d_return", result.getSample().getFuture5dReturn());
            row.put("future_10d_return", result.getSample().getFuture10dReturn());
            row.put("max_drawdown", result.getSample().getMaxDrawdown());
            row.put("evidence_json", result.getEvidenceJson());
            row.put("risk_json", result.getRiskJson());
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> buildPerformanceRows(EngineRunCommand command,
                                                           Long ruleVersionId,
                                                           List<BacktestLayerStat> stats) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (BacktestLayerStat stat : stats) {
            rows.add(metricRow(command, ruleVersionId, stat, "win_rate", stat.getWinRate()));
            rows.add(metricRow(command, ruleVersionId, stat, "avg_return", stat.getAvgReturn()));
            rows.add(metricRow(command, ruleVersionId, stat, "avg_drawdown", stat.getAvgDrawdown()));
            rows.add(metricRow(command, ruleVersionId, stat, "profit_loss_ratio", stat.getProfitLossRatio()));
        }
        return rows;
    }

    private List<Map<String, Object>> buildLayerStatRows(EngineRunCommand command,
                                                         Long ruleVersionId,
                                                         List<BacktestLayerStat> stats) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (BacktestLayerStat stat : stats) {
            Map<String, Object> row = baseRow(command, ruleVersionId);
            row.put("layer_code", stat.getLayerCode());
            row.put("layer_name", stat.getLayerName());
            row.put("sample_count", stat.getSampleCount());
            row.put("effective_signal_count", stat.getEffectiveSignalCount());
            row.put("risk_veto_count", stat.getRiskVetoCount());
            row.put("win_rate", stat.getWinRate());
            row.put("avg_return", stat.getAvgReturn());
            row.put("avg_drawdown", stat.getAvgDrawdown());
            row.put("profit_loss_ratio", stat.getProfitLossRatio());
            row.put("evidence_json", stat.getEvidenceJson());
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> buildFailureRows(EngineRunCommand command,
                                                       Long ruleVersionId,
                                                       List<BacktestFailureCase> failureCases) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (BacktestFailureCase failureCase : failureCases) {
            BacktestReplayResult result = failureCase.getReplayResult();
            Map<String, Object> row = baseRow(command, ruleVersionId);
            row.put("sample_id", result.getSample().getSampleId());
            row.put("sample_date", result.getSample().getSampleDate());
            row.put("pattern_code", result.getSample().getPatternCode());
            row.put("stock_code", result.getSample().getStockCode());
            row.put("stock_name", result.getSample().getStockName());
            row.put("failure_type", failureCase.getFailureType());
            row.put("failure_reason", failureCase.getFailureReason());
            row.put("replay_status", result.getReplayStatus());
            row.put("replay_return", result.getReplayReturn());
            row.put("replay_drawdown", result.getReplayDrawdown());
            row.put("future_3d_return", result.getSample().getFuture3dReturn());
            row.put("max_drawdown", result.getSample().getMaxDrawdown());
            row.put("evidence_json", failureCase.getEvidenceJson());
            row.put("risk_json", result.getRiskJson());
            rows.add(row);
        }
        return rows;
    }

    private Map<String, Object> metricRow(EngineRunCommand command,
                                          Long ruleVersionId,
                                          BacktestLayerStat stat,
                                          String metricName,
                                          Object metricValue) {
        Map<String, Object> row = baseRow(command, ruleVersionId);
        row.put("layer_code", stat.getLayerCode());
        row.put("layer_name", stat.getLayerName());
        row.put("metric_name", metricName);
        row.put("metric_value", metricValue);
        row.put("sample_count", stat.getSampleCount());
        row.put("evidence_json", stat.getEvidenceJson());
        return row;
    }

    private Map<String, Object> baseRow(EngineRunCommand command, Long ruleVersionId) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("trade_date", command.getTradeDate());
        row.put("market_scope", command.getMarketScope());
        row.put("task_id", command.getTaskId());
        row.put("rule_version_id", ruleVersionId);
        row.put("created_at", LocalDateTime.now());
        return row;
    }
}
