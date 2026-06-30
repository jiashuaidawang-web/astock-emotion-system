package com.astock.module.emotion.domain.service;

import com.astock.module.emotion.domain.model.EmotionHistoricalContext;
import com.astock.module.emotion.domain.model.EmotionMarketFeature;
import com.astock.module.emotion.domain.model.EmotionStageScore;
import com.astock.module.emotion.domain.model.EmotionStageType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EmotionStageScoringService {
    private final EmotionHistoricalScoreEnhancer historicalScoreEnhancer;

    public EmotionStageScoringService(EmotionHistoricalScoreEnhancer historicalScoreEnhancer) {
        this.historicalScoreEnhancer = historicalScoreEnhancer;
    }

    public List<EmotionStageScore> score(EmotionMarketFeature feature, EmotionHistoricalContext historicalContext) {
        List<EmotionStageScore> scores = EmotionStageType.orderedStages().stream()
                .map(stage -> scoreOne(stage, feature, historicalContext))
                .sorted(Comparator.comparing(EmotionStageScore::getStageScore).reversed())
                .toList();

        AtomicInteger rank = new AtomicInteger(1);
        scores.forEach(score -> score.setRankNo(rank.getAndIncrement()));
        return scores;
    }

    private EmotionStageScore scoreOne(EmotionStageType stage,
                                       EmotionMarketFeature feature,
                                       EmotionHistoricalContext historicalContext) {
        BigDecimal factorScore = factorMatchScore(stage, feature);
        BigDecimal historicalScore = historicalScoreEnhancer.historicalSampleSimilarityScore(stage, factorScore, feature, historicalContext);
        BigDecimal pathScore = historicalScoreEnhancer.stagePathMatchScore(stage, feature, historicalContext);
        BigDecimal followingScore = historicalScoreEnhancer.followingValidationScore(stage, historicalScore, factorScore, historicalContext);
        BigDecimal manualCorrection = historicalScoreEnhancer.manualSampleCorrectionScore(stage, historicalContext);

        BigDecimal finalScore = factorScore.multiply(BigDecimal.valueOf(0.30))
                .add(historicalScore.multiply(BigDecimal.valueOf(0.35)))
                .add(pathScore.multiply(BigDecimal.valueOf(0.20)))
                .add(followingScore.multiply(BigDecimal.valueOf(0.10)))
                .add(manualCorrection.multiply(BigDecimal.valueOf(0.05)))
                .setScale(4, RoundingMode.HALF_UP);

        EmotionStageScore score = new EmotionStageScore();
        score.setStageType(stage);
        score.setFactorPercentileMatchScore(factorScore);
        score.setHistoricalSampleSimilarityScore(historicalScore);
        score.setStagePathMatchScore(pathScore);
        score.setFollowingValidationScore(followingScore);
        score.setManualSampleCorrectionScore(manualCorrection);
        score.setStageScore(clamp(finalScore));
        score.setEvidenceText(buildEvidenceText(stage, feature, factorScore, historicalScore, pathScore, followingScore, manualCorrection, historicalContext));
        score.setRiskText(buildRiskText(stage, feature, historicalContext));
        return score;
    }

    private BigDecimal factorMatchScore(EmotionStageType stage, EmotionMarketFeature feature) {
        double[] actual = new double[] {
                value(feature.getMarketBreadthScore()),
                value(feature.getProfitEffectScore()),
                value(feature.getLossEffectScore()),
                value(feature.getLimitUpEcoScore()),
                value(feature.getLimitDownPressureScore()),
                value(feature.getLadderHeightScore()),
                value(feature.getBreakBoardPressureScore()),
                value(feature.getTurnoverHeatScore())
        };
        double[] center = new double[] {
                stage.getMarketBreadthCenter(),
                stage.getProfitEffectCenter(),
                stage.getLossEffectCenter(),
                stage.getLimitUpEcoCenter(),
                stage.getLimitDownPressureCenter(),
                stage.getLadderHeightCenter(),
                stage.getBreakBoardPressureCenter(),
                stage.getTurnoverHeatCenter()
        };

        double squared = 0D;
        for (int i = 0; i < actual.length; i++) {
            double diff = actual[i] - center[i];
            squared += diff * diff;
        }
        double distance = Math.sqrt(squared / actual.length);
        double score = 100D - distance;
        return clamp(BigDecimal.valueOf(score));
    }

    private String buildEvidenceText(EmotionStageType stage,
                                     EmotionMarketFeature feature,
                                     BigDecimal factorScore,
                                     BigDecimal historicalScore,
                                     BigDecimal pathScore,
                                     BigDecimal followingScore,
                                     BigDecimal manualCorrection,
                                     EmotionHistoricalContext historicalContext) {
        int sampleCount = historicalContext == null || historicalContext.getHistoricalCycleSamples() == null
                ? 0 : historicalContext.getHistoricalCycleSamples().size();
        int confirmCount = historicalContext == null || historicalContext.getCycleSampleConfirms() == null
                ? 0 : historicalContext.getCycleSampleConfirms().size();
        int manualCount = historicalContext == null || historicalContext.getManualStageAdjustments() == null
                ? 0 : historicalContext.getManualStageAdjustments().size();
        int transitionCount = historicalContext == null || historicalContext.getRecentStageTransitions() == null
                ? 0 : historicalContext.getRecentStageTransitions().size();

        return "{"
                + "\"stage\":\"" + stage.getCode() + "\","
                + "\"factorScore\":" + factorScore + ","
                + "\"historicalSampleScore\":" + historicalScore + ","
                + "\"pathScore\":" + pathScore + ","
                + "\"followingScore\":" + followingScore + ","
                + "\"manualCorrectionScore\":" + manualCorrection + ","
                + "\"historicalSampleCount\":" + sampleCount + ","
                + "\"confirmedSampleCount\":" + confirmCount + ","
                + "\"manualAdjustmentCount\":" + manualCount + ","
                + "\"recentTransitionCount\":" + transitionCount + ","
                + "\"marketBreadthScore\":" + feature.getMarketBreadthScore() + ","
                + "\"profitEffectScore\":" + feature.getProfitEffectScore() + ","
                + "\"lossEffectScore\":" + feature.getLossEffectScore() + ","
                + "\"limitUpEcoScore\":" + feature.getLimitUpEcoScore() + ","
                + "\"limitDownPressureScore\":" + feature.getLimitDownPressureScore() + ","
                + "\"breakBoardPressureScore\":" + feature.getBreakBoardPressureScore()
                + "}";
    }

    private String buildRiskText(EmotionStageType stage, EmotionMarketFeature feature, EmotionHistoricalContext historicalContext) {
        boolean historyMissing = historicalContext == null || !historicalContext.hasHistoricalSamples();
        boolean transitionMissing = historicalContext == null || !historicalContext.hasRecentTransitions();

        return "{"
                + "\"stage\":\"" + stage.getCode() + "\","
                + "\"riskNote\":\"该阶段由连续得分排序产生，历史样本和路径数据已作为增强项接入\","
                + "\"historyMissing\":" + historyMissing + ","
                + "\"transitionMissing\":" + transitionMissing + ","
                + "\"lossEffectScore\":" + feature.getLossEffectScore() + ","
                + "\"limitDownPressureScore\":" + feature.getLimitDownPressureScore() + ","
                + "\"breakBoardPressureScore\":" + feature.getBreakBoardPressureScore()
                + "}";
    }

    private double value(BigDecimal value) {
        return value == null ? 0D : value.doubleValue();
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
