package com.astock.module.backtest.domain.service;

import com.astock.module.backtest.domain.model.BacktestReplayResult;
import com.astock.module.backtest.domain.model.BacktestReplaySample;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BacktestSignalReplayService {

    public List<BacktestReplayResult> replay(List<BacktestReplaySample> samples) {
        return samples.stream().map(this::replayOne).toList();
    }

    private BacktestReplayResult replayOne(BacktestReplaySample sample) {
        boolean riskVetoed = "RISK_VETO".equals(sample.getRiskAction())
                || "PATTERN_INVALIDATED".equals(sample.getRiskAction())
                || "DATA_BLOCK".equals(sample.getRiskAction());

        boolean signalEffective = !riskVetoed
                && sample.getSignalScore() != null
                && sample.getSignalScore().compareTo(BigDecimal.valueOf(55)) >= 0;

        BacktestReplayResult result = new BacktestReplayResult();
        result.setSample(sample);
        result.setRiskVetoed(riskVetoed);
        result.setSignalEffective(signalEffective);
        result.setReplayReturn(resolveReplayReturn(sample));
        result.setReplayDrawdown(sample.getMaxDrawdown() == null ? BigDecimal.ZERO : sample.getMaxDrawdown());
        result.setReplayStatus(resolveReplayStatus(signalEffective, riskVetoed, result.getReplayReturn(), result.getReplayDrawdown()));
        result.setFailureType(resolveFailureType(result));
        result.setEvidenceJson(buildEvidenceJson(result));
        result.setRiskJson(buildRiskJson(result));
        return result;
    }

    private BigDecimal resolveReplayReturn(BacktestReplaySample sample) {
        if (sample.getFuture5dReturn() != null && sample.getFuture5dReturn().compareTo(BigDecimal.ZERO) != 0) {
            return sample.getFuture5dReturn();
        }
        if (sample.getFuture3dReturn() != null && sample.getFuture3dReturn().compareTo(BigDecimal.ZERO) != 0) {
            return sample.getFuture3dReturn();
        }
        if (sample.getFuture1dReturn() != null) {
            return sample.getFuture1dReturn();
        }
        return BigDecimal.ZERO;
    }

    private String resolveReplayStatus(boolean signalEffective,
                                       boolean riskVetoed,
                                       BigDecimal replayReturn,
                                       BigDecimal drawdown) {
        if (riskVetoed) {
            return "RISK_FILTERED";
        }
        if (!signalEffective) {
            return "SIGNAL_NOT_EFFECTIVE";
        }
        if (replayReturn.compareTo(BigDecimal.ZERO) > 0 && drawdown.abs().compareTo(BigDecimal.valueOf(8)) <= 0) {
            return "EFFECTIVE";
        }
        if (replayReturn.compareTo(BigDecimal.ZERO) > 0) {
            return "EFFECTIVE_WITH_DRAWDOWN";
        }
        return "FAILED";
    }

    private String resolveFailureType(BacktestReplayResult result) {
        if ("RISK_FILTERED".equals(result.getReplayStatus())) {
            return "RISK_VETO_FILTERED";
        }
        if ("SIGNAL_NOT_EFFECTIVE".equals(result.getReplayStatus())) {
            return "WEAK_SIGNAL";
        }
        if ("FAILED".equals(result.getReplayStatus())) {
            return result.getReplayDrawdown().abs().compareTo(BigDecimal.valueOf(8)) > 0
                    ? "HIGH_DRAWDOWN"
                    : "NEGATIVE_RETURN";
        }
        return null;
    }

    private String buildEvidenceJson(BacktestReplayResult result) {
        return "{"
                + "\"sampleId\":" + result.getSample().getSampleId() + ","
                + "\"sampleDate\":\"" + result.getSample().getSampleDate() + "\","
                + "\"patternCode\":\"" + result.getSample().getPatternCode() + "\","
                + "\"signalScore\":" + result.getSample().getSignalScore() + ","
                + "\"riskAction\":\"" + result.getSample().getRiskAction() + "\","
                + "\"futureFieldReadPhase\":\"BACKTEST_WINDOW_ONLY\","
                + "\"replayStatus\":\"" + result.getReplayStatus() + "\""
                + "}";
    }

    private String buildRiskJson(BacktestReplayResult result) {
        return "{"
                + "\"riskNote\":\"future_*字段只在回测窗口读取，不参与T日信号生成与匹配\","
                + "\"riskVetoed\":" + result.getRiskVetoed()
                + "}";
    }
}
