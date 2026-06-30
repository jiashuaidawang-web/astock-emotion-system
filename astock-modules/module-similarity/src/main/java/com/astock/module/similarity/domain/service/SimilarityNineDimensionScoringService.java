package com.astock.module.similarity.domain.service;

import com.astock.module.similarity.domain.model.SimilarityDimensionScore;
import com.astock.module.similarity.domain.model.SimilarityDimensionType;
import com.astock.module.similarity.domain.model.SimilarityFeatureVector;
import com.astock.module.similarity.domain.model.SimilarityMatchCandidate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
public class SimilarityNineDimensionScoringService {

    public SimilarityMatchCandidate score(String matchType,
                                          SimilarityFeatureVector current,
                                          SimilarityFeatureVector historical) {
        List<SimilarityDimensionScore> dimensionScores = SimilarityDimensionType.orderedDimensions().stream()
                .map(dimension -> scoreDimension(dimension, current, historical))
                .toList();

        BigDecimal marketEnvScore = weightedGroupScore(dimensionScores, "MARKET_ENV");
        BigDecimal emotionCycleScore = weightedGroupScore(dimensionScores, "EMOTION_CYCLE");
        BigDecimal themeLeaderScore = weightedGroupScore(dimensionScores, "THEME_LEADER");

        BigDecimal total = marketEnvScore.multiply(BigDecimal.valueOf(0.30))
                .add(emotionCycleScore.multiply(BigDecimal.valueOf(0.45)))
                .add(themeLeaderScore.multiply(BigDecimal.valueOf(0.25)))
                .setScale(4, RoundingMode.HALF_UP);

        SimilarityMatchCandidate candidate = new SimilarityMatchCandidate();
        candidate.setSampleId(historical.getSampleId());
        candidate.setMatchType(matchType);
        candidate.setHistoricalTradeDate(historical.getTradeDate());
        candidate.setHistoricalStage(historical.getStageCode());
        candidate.setMarketEnvironmentSimilarityScore(clamp(marketEnvScore));
        candidate.setEmotionCycleSimilarityScore(clamp(emotionCycleScore));
        candidate.setThemeLeaderSimilarityScore(clamp(themeLeaderScore));
        candidate.setTotalSimilarityScore(clamp(total));
        candidate.setDimensionScores(dimensionScores);
        candidate.setEvidenceJson(buildEvidenceJson(candidate));
        candidate.setRiskText(buildRiskText(candidate));
        return candidate;
    }

    public List<SimilarityMatchCandidate> topN(List<SimilarityMatchCandidate> candidates, int topN) {
        return candidates.stream()
                .sorted(Comparator.comparing(SimilarityMatchCandidate::getTotalSimilarityScore).reversed())
                .limit(topN)
                .toList();
    }

    private SimilarityDimensionScore scoreDimension(SimilarityDimensionType dimension,
                                                    SimilarityFeatureVector current,
                                                    SimilarityFeatureVector historical) {
        BigDecimal currentValue = current.get(dimension);
        BigDecimal historicalValue = historical.get(dimension);
        BigDecimal diff = currentValue.subtract(historicalValue).abs();
        BigDecimal score = BigDecimal.valueOf(100).subtract(diff);

        SimilarityDimensionScore dimensionScore = new SimilarityDimensionScore();
        dimensionScore.setDimensionType(dimension);
        dimensionScore.setCurrentValue(currentValue);
        dimensionScore.setHistoricalValue(historicalValue);
        dimensionScore.setSimilarityScore(clamp(score));
        return dimensionScore;
    }

    private BigDecimal weightedGroupScore(List<SimilarityDimensionScore> dimensionScores, String groupCode) {
        BigDecimal weighted = BigDecimal.ZERO;
        BigDecimal weightSum = BigDecimal.ZERO;
        for (SimilarityDimensionScore score : dimensionScores) {
            if (!groupCode.equals(score.getDimensionType().getGroupCode())) {
                continue;
            }
            BigDecimal weight = BigDecimal.valueOf(score.getDimensionType().getWeight());
            weighted = weighted.add(score.getSimilarityScore().multiply(weight));
            weightSum = weightSum.add(weight);
        }
        if (weightSum.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        return weighted.divide(weightSum, 4, RoundingMode.HALF_UP);
    }

    private String buildEvidenceJson(SimilarityMatchCandidate candidate) {
        return "{"
                + "\"sampleId\":" + candidate.getSampleId() + ","
                + "\"matchType\":\"" + candidate.getMatchType() + "\","
                + "\"marketEnvScore\":" + candidate.getMarketEnvironmentSimilarityScore() + ","
                + "\"emotionCycleScore\":" + candidate.getEmotionCycleSimilarityScore() + ","
                + "\"themeLeaderScore\":" + candidate.getThemeLeaderSimilarityScore() + ","
                + "\"totalScore\":" + candidate.getTotalSimilarityScore() + ","
                + "\"futureFieldUsed\":false"
                + "}";
    }

    private String buildRiskText(SimilarityMatchCandidate candidate) {
        return "{"
                + "\"riskNote\":\"相似度匹配只使用T日及历史样本当时可见字段，future_*未参与匹配\","
                + "\"historicalTradeDate\":\"" + candidate.getHistoricalTradeDate() + "\","
                + "\"historicalStage\":\"" + candidate.getHistoricalStage() + "\""
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
