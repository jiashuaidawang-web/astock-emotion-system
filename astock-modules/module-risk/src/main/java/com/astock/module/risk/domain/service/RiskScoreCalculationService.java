package com.astock.module.risk.domain.service;

import com.astock.module.risk.domain.model.RiskFactorSnapshot;
import com.astock.module.risk.domain.model.RiskSignalScore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RiskScoreCalculationService {

    public List<RiskSignalScore> score(RiskFactorSnapshot factor) {
        List<RiskSignalScore> signals = new ArrayList<>();
        signals.add(one("EMOTION_CYCLE_RISK", "情绪周期风险", "EMOTION_CYCLE", emotionCycleRiskScore(factor)));
        signals.add(one("LOSS_EFFECT_RISK", "亏钱效应风险", "MARKET_EFFECT", factor.getLossEffectScore()));
        signals.add(one("LIMIT_ECO_RISK", "涨跌停生态风险", "LIMIT_ECO", limitEcoRiskScore(factor)));
        signals.add(one("LEADER_FEEDBACK_RISK", "龙头负反馈风险", "LEADER_FEEDBACK", factor.getLeaderNegativeFeedbackScore()));
        signals.add(one("MAINLINE_DECAY_RISK", "主线衰退风险", "MAINLINE_DECAY", factor.getMainlineDecayScore()));
        signals.add(one("INDEX_FUND_RISK", "指数资金风险", "INDEX_FUND", factor.getIndexFundRiskScore()));
        signals.add(one("DATA_INTEGRITY_RISK", "数据完整性风险", "DATA_QUALITY", factor.getDataIntegrityRiskScore()));

        BigDecimal totalRiskScore = totalRiskScore(signals);
        RiskSignalScore total = one("TOTAL_RISK", "综合风险", "TOTAL", totalRiskScore);
        total.setEvidenceJson(buildTotalEvidenceJson(factor, signals, totalRiskScore));
        signals.add(0, total);

        return signals.stream()
                .sorted(Comparator.comparing(RiskSignalScore::getRiskScore).reversed())
                .toList();
    }

    /**
     * risk_score =
     * 情绪周期风险 * 20%
     * + 亏钱效应风险 * 20%
     * + 涨跌停生态风险 * 20%
     * + 龙头负反馈风险 * 20%
     * + 主线衰退风险 * 10%
     * + 指数资金风险 * 5%
     * + 数据完整性风险 * 5%
     */
    private BigDecimal totalRiskScore(List<RiskSignalScore> signals) {
        BigDecimal emotion = scoreOf(signals, "EMOTION_CYCLE_RISK");
        BigDecimal loss = scoreOf(signals, "LOSS_EFFECT_RISK");
        BigDecimal limitEco = scoreOf(signals, "LIMIT_ECO_RISK");
        BigDecimal leader = scoreOf(signals, "LEADER_FEEDBACK_RISK");
        BigDecimal mainline = scoreOf(signals, "MAINLINE_DECAY_RISK");
        BigDecimal index = scoreOf(signals, "INDEX_FUND_RISK");
        BigDecimal data = scoreOf(signals, "DATA_INTEGRITY_RISK");

        return clamp(emotion.multiply(BigDecimal.valueOf(0.20))
                .add(loss.multiply(BigDecimal.valueOf(0.20)))
                .add(limitEco.multiply(BigDecimal.valueOf(0.20)))
                .add(leader.multiply(BigDecimal.valueOf(0.20)))
                .add(mainline.multiply(BigDecimal.valueOf(0.10)))
                .add(index.multiply(BigDecimal.valueOf(0.05)))
                .add(data.multiply(BigDecimal.valueOf(0.05))));
    }

    private BigDecimal emotionCycleRiskScore(RiskFactorSnapshot factor) {
        String stage = factor.getEmotionStage();
        BigDecimal base = switch (stage == null ? "UNKNOWN" : stage) {
            case "RETREAT" -> BigDecimal.valueOf(90);
            case "ICE_POINT" -> BigDecimal.valueOf(80);
            case "CLIMAX" -> BigDecimal.valueOf(75);
            case "DIVERGENCE" -> BigDecimal.valueOf(70);
            case "CHAOS" -> BigDecimal.valueOf(60);
            case "REPAIR", "TRIAL" -> BigDecimal.valueOf(45);
            case "STARTUP", "FERMENTATION" -> BigDecimal.valueOf(35);
            case "MAIN_RISE" -> BigDecimal.valueOf(30);
            default -> BigDecimal.valueOf(50);
        };

        BigDecimal confidence = factor.getEmotionStageConfidence() == null ? BigDecimal.ZERO : factor.getEmotionStageConfidence();
        BigDecimal confidenceAdjust = BigDecimal.valueOf(100).subtract(confidence).multiply(BigDecimal.valueOf(0.20));
        return clamp(base.add(confidenceAdjust));
    }

    private BigDecimal limitEcoRiskScore(RiskFactorSnapshot factor) {
        return clamp(factor.getLimitDownPressureScore().multiply(BigDecimal.valueOf(0.55))
                .add(factor.getBreakBoardPressureScore().multiply(BigDecimal.valueOf(0.45))));
    }

    private RiskSignalScore one(String code, String name, String source, BigDecimal score) {
        RiskSignalScore signal = new RiskSignalScore();
        signal.setRiskCode(code);
        signal.setRiskName(name);
        signal.setRiskSource(source);
        signal.setRiskScore(clamp(score));
        signal.setRiskLevel(riskLevel(signal.getRiskScore()));
        signal.setSignalLevel(signalLevel(signal.getRiskScore()));
        signal.setRiskAction(riskAction(signal.getRiskScore(), code));
        signal.setOneVoteVeto(oneVoteVeto(signal.getRiskScore(), code));
        signal.setEvidenceJson(buildEvidenceJson(signal));
        signal.setRiskText(buildRiskText(signal));
        return signal;
    }

    private String riskLevel(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(85)) >= 0) return "EXTREME";
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) return "HIGH";
        if (score.compareTo(BigDecimal.valueOf(55)) >= 0) return "MEDIUM";
        if (score.compareTo(BigDecimal.valueOf(35)) >= 0) return "LOW";
        return "NORMAL";
    }

    private String signalLevel(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(85)) >= 0) return "FATAL";
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) return "BLOCKER";
        if (score.compareTo(BigDecimal.valueOf(55)) >= 0) return "WARNING";
        if (score.compareTo(BigDecimal.valueOf(35)) >= 0) return "NOTICE";
        return "NORMAL";
    }

    private String riskAction(BigDecimal score, String code) {
        if ("DATA_INTEGRITY_RISK".equals(code) && score.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "DATA_BLOCK";
        }
        if (score.compareTo(BigDecimal.valueOf(85)) >= 0) return "RISK_VETO";
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) return "PATTERN_INVALIDATED";
        if (score.compareTo(BigDecimal.valueOf(55)) >= 0) return "REQUIRE_CONFIRMATION";
        if (score.compareTo(BigDecimal.valueOf(35)) >= 0) return "CAUTION";
        return "NORMAL";
    }

    private boolean oneVoteVeto(BigDecimal score, String code) {
        return score.compareTo(BigDecimal.valueOf(85)) >= 0
                || ("DATA_INTEGRITY_RISK".equals(code) && score.compareTo(BigDecimal.valueOf(70)) >= 0);
    }

    private BigDecimal scoreOf(List<RiskSignalScore> signals, String code) {
        return signals.stream()
                .filter(signal -> code.equals(signal.getRiskCode()))
                .findFirst()
                .map(RiskSignalScore::getRiskScore)
                .orElse(BigDecimal.ZERO);
    }

    private String buildEvidenceJson(RiskSignalScore signal) {
        return "{"
                + "\"riskCode\":\"" + signal.getRiskCode() + "\","
                + "\"riskScore\":" + signal.getRiskScore() + ","
                + "\"riskLevel\":\"" + signal.getRiskLevel() + "\","
                + "\"riskAction\":\"" + signal.getRiskAction() + "\","
                + "\"riskAsUpperGuard\":true"
                + "}";
    }

    private String buildRiskText(RiskSignalScore signal) {
        return "{"
                + "\"riskNote\":\"风控是模式条件的上级保护层，只输出风险状态和条件否决，不输出交易建议\","
                + "\"oneVoteVeto\":" + signal.getOneVoteVeto()
                + "}";
    }

    private String buildTotalEvidenceJson(RiskFactorSnapshot factor,
                                          List<RiskSignalScore> signals,
                                          BigDecimal totalRiskScore) {
        return "{"
                + "\"emotionStage\":\"" + factor.getEmotionStage() + "\","
                + "\"totalRiskScore\":" + totalRiskScore + ","
                + "\"emotionCycleRisk\":" + scoreOf(signals, "EMOTION_CYCLE_RISK") + ","
                + "\"lossEffectRisk\":" + scoreOf(signals, "LOSS_EFFECT_RISK") + ","
                + "\"limitEcoRisk\":" + scoreOf(signals, "LIMIT_ECO_RISK") + ","
                + "\"leaderFeedbackRisk\":" + scoreOf(signals, "LEADER_FEEDBACK_RISK") + ","
                + "\"mainlineDecayRisk\":" + scoreOf(signals, "MAINLINE_DECAY_RISK") + ","
                + "\"indexFundRisk\":" + scoreOf(signals, "INDEX_FUND_RISK") + ","
                + "\"dataIntegrityRisk\":" + scoreOf(signals, "DATA_INTEGRITY_RISK")
                + "}";
    }

    private BigDecimal clamp(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        if (value.compareTo(BigDecimal.valueOf(100)) > 0) {
            return BigDecimal.valueOf(100).setScale(4, RoundingMode.HALF_UP);
        }
        return value.setScale(4, RoundingMode.HALF_UP);
    }
}
