package com.astock.module.emotion.domain.service;

import com.astock.common.convert.MapFieldReader;
import com.astock.module.emotion.domain.model.EmotionHistoricalContext;
import com.astock.module.emotion.domain.model.EmotionMarketFeature;
import com.astock.module.emotion.domain.model.EmotionStageType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class EmotionHistoricalScoreEnhancer {

    public BigDecimal historicalSampleSimilarityScore(EmotionStageType stage,
                                                      BigDecimal factorScore,
                                                      EmotionMarketFeature feature,
                                                      EmotionHistoricalContext context) {
        if (context == null || !context.hasHistoricalSamples()) {
            return fallback(factorScore, BigDecimal.valueOf(0.80));
        }

        List<Map<String, Object>> samples = context.getHistoricalCycleSamples().stream()
                .filter(row -> stage.getCode().equalsIgnoreCase(readStageCode(row)))
                .toList();

        if (samples.isEmpty()) {
            return fallback(factorScore, BigDecimal.valueOf(0.70));
        }

        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (Map<String, Object> sample : samples) {
            BigDecimal sampleConfidence = readAny(sample, "sample_confidence", "confidence", "stage_confidence");
            BigDecimal sampleSimilarity = readAny(sample, "similarity_score", "sample_similarity_score", "stage_score");
            BigDecimal confirmedBoost = isConfirmedSample(sample, context) ? BigDecimal.TEN : BigDecimal.ZERO;
            BigDecimal oneScore = sampleConfidence.multiply(BigDecimal.valueOf(0.45))
                    .add(sampleSimilarity.multiply(BigDecimal.valueOf(0.45)))
                    .add(confirmedBoost);
            total = total.add(clamp(oneScore));
            count++;
        }

        BigDecimal average = total.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP);
        return clamp(average.multiply(BigDecimal.valueOf(0.65)).add(factorScore.multiply(BigDecimal.valueOf(0.35))));
    }

    public BigDecimal stagePathMatchScore(EmotionStageType stage,
                                          EmotionMarketFeature feature,
                                          EmotionHistoricalContext context) {
        if (context == null || !context.hasRecentTransitions()) {
            return fallbackFromFeaturePath(stage, feature);
        }

        List<Map<String, Object>> transitions = context.getRecentStageTransitions();
        long hitToStage = transitions.stream()
                .filter(row -> stage.getCode().equalsIgnoreCase(MapFieldReader.string(row, "to_stage"))
                        || stage.getCode().equalsIgnoreCase(MapFieldReader.string(row, "primary_stage")))
                .count();

        BigDecimal avgTransitionScore = transitions.stream()
                .map(row -> readAny(row, "transition_score", "transition_probability", "stage_score"))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(Math.max(1, transitions.size())), 4, RoundingMode.HALF_UP);

        BigDecimal hitScore = BigDecimal.valueOf(hitToStage)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(Math.max(1, transitions.size())), 4, RoundingMode.HALF_UP);

        return clamp(hitScore.multiply(BigDecimal.valueOf(0.55)).add(avgTransitionScore.multiply(BigDecimal.valueOf(0.45))));
    }

    public BigDecimal followingValidationScore(EmotionStageType stage,
                                               BigDecimal historicalSampleScore,
                                               BigDecimal factorScore,
                                               EmotionHistoricalContext context) {
        if (context == null || !context.hasHistoricalSamples()) {
            return clamp(historicalSampleScore.multiply(BigDecimal.valueOf(0.60)).add(factorScore.multiply(BigDecimal.valueOf(0.40))));
        }

        List<Map<String, Object>> samples = context.getHistoricalCycleSamples().stream()
                .filter(row -> stage.getCode().equalsIgnoreCase(readStageCode(row)))
                .toList();

        if (samples.isEmpty()) {
            return clamp(historicalSampleScore.multiply(BigDecimal.valueOf(0.60)).add(factorScore.multiply(BigDecimal.valueOf(0.40))));
        }

        BigDecimal followingScoreSum = BigDecimal.ZERO;
        int count = 0;
        for (Map<String, Object> row : samples) {
            BigDecimal future3d = readAny(row, "future_3d_return", "following_3d_return", "future3d_return");
            BigDecimal maxDrawdown = readAny(row, "max_drawdown", "following_max_drawdown");
            BigDecimal one = BigDecimal.valueOf(50)
                    .add(future3d.multiply(BigDecimal.valueOf(2)))
                    .subtract(maxDrawdown.abs());
            followingScoreSum = followingScoreSum.add(clamp(one));
            count++;
        }

        BigDecimal average = followingScoreSum.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP);
        return clamp(average.multiply(BigDecimal.valueOf(0.70)).add(historicalSampleScore.multiply(BigDecimal.valueOf(0.30))));
    }

    public BigDecimal manualSampleCorrectionScore(EmotionStageType stage, EmotionHistoricalContext context) {
        if (context == null) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }

        BigDecimal score = BigDecimal.ZERO;
        if (context.hasManualAdjustments()) {
            long manualHit = context.getManualStageAdjustments().stream()
                    .filter(row -> stage.getCode().equalsIgnoreCase(MapFieldReader.string(row, "manual_stage"))
                            || stage.getCode().equalsIgnoreCase(MapFieldReader.string(row, "adjusted_stage"))
                            || stage.getCode().equalsIgnoreCase(MapFieldReader.string(row, "stage_code")))
                    .count();
            if (manualHit > 0) {
                score = score.add(BigDecimal.valueOf(80));
            }
        }

        if (context.hasConfirmedSamples()) {
            long confirmHit = context.getCycleSampleConfirms().stream()
                    .filter(row -> stage.getCode().equalsIgnoreCase(MapFieldReader.string(row, "confirmed_stage"))
                            || stage.getCode().equalsIgnoreCase(MapFieldReader.string(row, "stage_code")))
                    .count();
            if (confirmHit > 0) {
                score = score.add(BigDecimal.valueOf(20));
            }
        }
        return clamp(score);
    }

    private boolean isConfirmedSample(Map<String, Object> sample, EmotionHistoricalContext context) {
        if (context == null || !context.hasConfirmedSamples()) {
            return false;
        }
        Long sampleId = MapFieldReader.longValue(sample, "sample_id");
        if (sampleId == null) {
            sampleId = MapFieldReader.longValue(sample, "id");
        }
        if (sampleId == null) {
            return false;
        }
        Long finalSampleId = sampleId;
        return context.getCycleSampleConfirms().stream()
                .anyMatch(row -> finalSampleId.equals(MapFieldReader.longValue(row, "sample_id"))
                        || finalSampleId.equals(MapFieldReader.longValue(row, "cycle_sample_id")));
    }

    private String readStageCode(Map<String, Object> row) {
        String value = MapFieldReader.string(row, "stage_code");
        if (value == null) value = MapFieldReader.string(row, "stage_type");
        if (value == null) value = MapFieldReader.string(row, "emotion_stage");
        if (value == null) value = MapFieldReader.string(row, "primary_stage");
        return value == null ? "" : value;
    }

    private BigDecimal fallback(BigDecimal base, BigDecimal ratio) {
        return clamp(base.multiply(ratio));
    }

    private BigDecimal fallbackFromFeaturePath(EmotionStageType stage, EmotionMarketFeature feature) {
        BigDecimal continuitySignal = feature.getLadderHeightScore()
                .add(feature.getLimitUpEcoScore())
                .add(BigDecimal.valueOf(100).subtract(feature.getBreakBoardPressureScore()))
                .divide(BigDecimal.valueOf(3), 4, RoundingMode.HALF_UP);

        if (stage == EmotionStageType.STARTUP
                || stage == EmotionStageType.FERMENTATION
                || stage == EmotionStageType.MAIN_RISE
                || stage == EmotionStageType.CLIMAX) {
            return clamp(continuitySignal);
        }
        if (stage == EmotionStageType.RETREAT || stage == EmotionStageType.ICE_POINT) {
            return clamp(feature.getLossEffectScore()
                    .add(feature.getLimitDownPressureScore())
                    .add(feature.getBreakBoardPressureScore())
                    .divide(BigDecimal.valueOf(3), 4, RoundingMode.HALF_UP));
        }
        return clamp(BigDecimal.valueOf(100).subtract(
                feature.getBreakBoardPressureScore().subtract(feature.getLimitUpEcoScore()).abs()
        ));
    }

    private BigDecimal readAny(Map<String, Object> row, String... columns) {
        for (String column : columns) {
            BigDecimal value = MapFieldReader.decimal(row, column);
            if (value != null) {
                return value;
            }
        }
        return BigDecimal.ZERO;
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
