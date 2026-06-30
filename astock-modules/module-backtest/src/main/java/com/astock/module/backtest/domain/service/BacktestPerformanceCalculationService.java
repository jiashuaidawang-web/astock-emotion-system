package com.astock.module.backtest.domain.service;

import com.astock.module.backtest.domain.model.BacktestFailureCase;
import com.astock.module.backtest.domain.model.BacktestLayerStat;
import com.astock.module.backtest.domain.model.BacktestReplayResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BacktestPerformanceCalculationService {

    public List<BacktestLayerStat> layerStats(List<BacktestReplayResult> results) {
        Map<String, List<BacktestReplayResult>> grouped = results.stream()
                .collect(Collectors.groupingBy(this::layerCode));

        List<BacktestLayerStat> stats = new ArrayList<>();
        for (Map.Entry<String, List<BacktestReplayResult>> entry : grouped.entrySet()) {
            stats.add(statOne(entry.getKey(), entry.getValue()));
        }
        stats.add(statOne("ALL", results));
        return stats;
    }

    public List<BacktestFailureCase> failureCases(List<BacktestReplayResult> results) {
        return results.stream()
                .filter(result -> result.getFailureType() != null)
                .map(this::failureCase)
                .toList();
    }

    private BacktestLayerStat statOne(String layerCode, List<BacktestReplayResult> rows) {
        int sampleCount = rows.size();
        int effectiveCount = (int) rows.stream().filter(r -> Boolean.TRUE.equals(r.getSignalEffective())).count();
        int riskVetoCount = (int) rows.stream().filter(r -> Boolean.TRUE.equals(r.getRiskVetoed())).count();
        int winCount = (int) rows.stream()
                .filter(r -> r.getReplayReturn() != null && r.getReplayReturn().compareTo(BigDecimal.ZERO) > 0)
                .count();

        BigDecimal avgReturn = avg(rows.stream().map(BacktestReplayResult::getReplayReturn).toList());
        BigDecimal avgDrawdown = avg(rows.stream().map(BacktestReplayResult::getReplayDrawdown).toList());
        BigDecimal winRate = sampleCount == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(winCount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(sampleCount), 4, RoundingMode.HALF_UP);
        BigDecimal profitLossRatio = profitLossRatio(rows);

        BacktestLayerStat stat = new BacktestLayerStat();
        stat.setLayerCode(layerCode);
        stat.setLayerName(layerName(layerCode));
        stat.setSampleCount(sampleCount);
        stat.setEffectiveSignalCount(effectiveCount);
        stat.setRiskVetoCount(riskVetoCount);
        stat.setWinRate(winRate);
        stat.setAvgReturn(avgReturn);
        stat.setAvgDrawdown(avgDrawdown);
        stat.setProfitLossRatio(profitLossRatio);
        stat.setEvidenceJson("{\"futureFieldReadPhase\":\"BACKTEST_WINDOW_ONLY\",\"layerCode\":\"" + layerCode + "\"}");
        return stat;
    }

    private BacktestFailureCase failureCase(BacktestReplayResult result) {
        BacktestFailureCase failureCase = new BacktestFailureCase();
        failureCase.setReplayResult(result);
        failureCase.setFailureType(result.getFailureType());
        failureCase.setFailureReason("回放状态：" + result.getReplayStatus());
        failureCase.setEvidenceJson(result.getEvidenceJson());
        return failureCase;
    }

    private String layerCode(BacktestReplayResult result) {
        if (Boolean.TRUE.equals(result.getRiskVetoed())) {
            return "RISK_FILTERED";
        }
        String patternCode = result.getSample().getPatternCode();
        if (patternCode != null && !patternCode.isBlank()) {
            return "PATTERN_" + patternCode;
        }
        String stageCode = result.getSample().getStageCode();
        if (stageCode != null && !stageCode.isBlank()) {
            return "STAGE_" + stageCode;
        }
        return "UNKNOWN";
    }

    private String layerName(String layerCode) {
        if ("ALL".equals(layerCode)) {
            return "全部样本";
        }
        if ("RISK_FILTERED".equals(layerCode)) {
            return "风控过滤样本";
        }
        return layerCode;
    }

    private BigDecimal avg(List<BigDecimal> values) {
        List<BigDecimal> valid = values.stream().filter(v -> v != null).toList();
        if (valid.isEmpty()) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        BigDecimal total = valid.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(valid.size()), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal profitLossRatio(List<BacktestReplayResult> rows) {
        BigDecimal profit = rows.stream()
                .map(BacktestReplayResult::getReplayReturn)
                .filter(v -> v != null && v.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal loss = rows.stream()
                .map(BacktestReplayResult::getReplayReturn)
                .filter(v -> v != null && v.compareTo(BigDecimal.ZERO) < 0)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (loss.compareTo(BigDecimal.ZERO) <= 0) {
            return profit.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(999) : BigDecimal.ZERO;
        }
        return profit.divide(loss, 4, RoundingMode.HALF_UP);
    }
}
