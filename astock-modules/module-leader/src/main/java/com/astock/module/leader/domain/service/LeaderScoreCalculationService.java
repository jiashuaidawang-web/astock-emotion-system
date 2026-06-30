package com.astock.module.leader.domain.service;

import com.astock.module.leader.domain.model.LeaderCandidateFeature;
import com.astock.module.leader.domain.model.LeaderRecognitionContext;
import com.astock.module.leader.domain.model.LeaderScore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LeaderScoreCalculationService {

    public List<LeaderScore> score(List<LeaderCandidateFeature> features, LeaderRecognitionContext context) {
        BigDecimal maxTurnover = max(features.stream().map(LeaderCandidateFeature::getTurnoverAmount).toList());
        BigDecimal maxBoardHeight = max(features.stream().map(LeaderCandidateFeature::getBoardHeight).toList());
        BigDecimal maxMainlineStrength = max(features.stream().map(LeaderCandidateFeature::getMainlineStrengthScore).toList());
        BigDecimal maxSectorStrength = max(features.stream().map(LeaderCandidateFeature::getSectorStrengthScore).toList());

        List<LeaderScore> scores = features.stream()
                .map(feature -> scoreOne(feature, context, maxTurnover, maxBoardHeight, maxMainlineStrength, maxSectorStrength))
                .sorted(Comparator.comparing(LeaderScore::getLeaderScore).reversed())
                .toList();

        AtomicInteger rank = new AtomicInteger(1);
        scores.forEach(score -> fillRankTypeStatus(score, rank.getAndIncrement()));
        return scores;
    }

    private LeaderScore scoreOne(LeaderCandidateFeature feature,
                                 LeaderRecognitionContext context,
                                 BigDecimal maxTurnover,
                                 BigDecimal maxBoardHeight,
                                 BigDecimal maxMainlineStrength,
                                 BigDecimal maxSectorStrength) {
        BigDecimal recognitionScore = recognitionScore(feature, maxBoardHeight, maxTurnover);
        BigDecimal mainlineRelationScore = ratioScore(feature.getMainlineStrengthScore(), maxMainlineStrength);
        BigDecimal driveScore = driveScore(feature, maxMainlineStrength, maxSectorStrength, maxTurnover);
        BigDecimal strengthScore = strengthScore(feature, maxBoardHeight);
        BigDecimal supportScore = supportScore(feature, maxTurnover);
        BigDecimal continuityScore = continuityScore(feature, context, maxBoardHeight);
        BigDecimal negativeFeedbackScore = negativeFeedbackScore(feature, context);
        BigDecimal riskFeedbackScore = BigDecimal.valueOf(100).subtract(negativeFeedbackScore);

        BigDecimal leaderScore = recognitionScore.multiply(BigDecimal.valueOf(0.20))
                .add(mainlineRelationScore.multiply(BigDecimal.valueOf(0.20)))
                .add(driveScore.multiply(BigDecimal.valueOf(0.20)))
                .add(strengthScore.multiply(BigDecimal.valueOf(0.15)))
                .add(supportScore.multiply(BigDecimal.valueOf(0.10)))
                .add(continuityScore.multiply(BigDecimal.valueOf(0.10)))
                .add(riskFeedbackScore.multiply(BigDecimal.valueOf(0.05)))
                .setScale(4, RoundingMode.HALF_UP);

        LeaderScore score = new LeaderScore();
        score.setFeature(feature);
        score.setRecognitionScore(clamp(recognitionScore));
        score.setMainlineRelationScore(clamp(mainlineRelationScore));
        score.setDriveScore(clamp(driveScore));
        score.setStrengthScore(clamp(strengthScore));
        score.setSupportScore(clamp(supportScore));
        score.setContinuityScore(clamp(continuityScore));
        score.setNegativeFeedbackScore(clamp(negativeFeedbackScore));
        score.setRiskFeedbackScore(clamp(riskFeedbackScore));
        score.setLeaderScore(clamp(leaderScore));
        score.setEvidenceJson(buildEvidenceJson(score));
        score.setRiskJson(buildRiskJson(score));
        return score;
    }

    /**
     * leader_score =
     * 辨识度评分 * 20%
     * + 主线关联评分 * 20%
     * + 带动性评分 * 20%
     * + 强度评分 * 15%
     * + 承接评分 * 10%
     * + 持续性评分 * 10%
     * + 风险反馈评分 * 5%
     *
     * 注意：市场总龙头候选来自综合 leader_score 排序，不是“最高板=市场总龙头”。
     */
    private BigDecimal recognitionScore(LeaderCandidateFeature feature, BigDecimal maxBoardHeight, BigDecimal maxTurnover) {
        BigDecimal boardRecognition = ratioScore(feature.getBoardHeight(), maxBoardHeight);
        BigDecimal turnoverRecognition = ratioScore(feature.getTurnoverAmount(), maxTurnover);
        BigDecimal nameRecognition = feature.getMainlineStrengthScore() == null ? BigDecimal.ZERO : feature.getMainlineStrengthScore();
        return clamp(boardRecognition.multiply(BigDecimal.valueOf(0.40))
                .add(turnoverRecognition.multiply(BigDecimal.valueOf(0.35)))
                .add(nameRecognition.multiply(BigDecimal.valueOf(0.25))));
    }

    private BigDecimal driveScore(LeaderCandidateFeature feature,
                                  BigDecimal maxMainlineStrength,
                                  BigDecimal maxSectorStrength,
                                  BigDecimal maxTurnover) {
        BigDecimal sectorDrive = ratioScore(feature.getSectorStrengthScore(), maxSectorStrength);
        BigDecimal mainlineDrive = ratioScore(feature.getMainlineStrengthScore(), maxMainlineStrength);
        BigDecimal emotionDrive = feature.getLimitUp() ? BigDecimal.valueOf(80) : clamp(feature.getPctChange().multiply(BigDecimal.valueOf(5)));
        BigDecimal fundDrive = ratioScore(feature.getTurnoverAmount(), maxTurnover);

        return clamp(sectorDrive.multiply(BigDecimal.valueOf(0.35))
                .add(mainlineDrive.multiply(BigDecimal.valueOf(0.30)))
                .add(emotionDrive.multiply(BigDecimal.valueOf(0.20)))
                .add(fundDrive.multiply(BigDecimal.valueOf(0.15))));
    }

    private BigDecimal strengthScore(LeaderCandidateFeature feature, BigDecimal maxBoardHeight) {
        BigDecimal boardStrength = ratioScore(feature.getBoardHeight(), maxBoardHeight);
        BigDecimal priceStrength = clamp(feature.getPctChange().multiply(BigDecimal.valueOf(5)));
        BigDecimal limitUpBonus = feature.getLimitUp() ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        return clamp(boardStrength.multiply(BigDecimal.valueOf(0.35))
                .add(priceStrength.multiply(BigDecimal.valueOf(0.35)))
                .add(limitUpBonus.multiply(BigDecimal.valueOf(0.30))));
    }

    private BigDecimal supportScore(LeaderCandidateFeature feature, BigDecimal maxTurnover) {
        BigDecimal amountSupport = ratioScore(feature.getTurnoverAmount(), maxTurnover);
        BigDecimal turnoverSupport = clamp(feature.getTurnoverRate().multiply(BigDecimal.valueOf(5)));
        BigDecimal volumeSupport = clamp(feature.getVolumeRatio().multiply(BigDecimal.valueOf(20)));
        return clamp(amountSupport.multiply(BigDecimal.valueOf(0.50))
                .add(turnoverSupport.multiply(BigDecimal.valueOf(0.30)))
                .add(volumeSupport.multiply(BigDecimal.valueOf(0.20))));
    }

    private BigDecimal continuityScore(LeaderCandidateFeature feature, LeaderRecognitionContext context, BigDecimal maxBoardHeight) {
        BigDecimal boardContinuity = ratioScore(feature.getBoardHeight(), maxBoardHeight);
        BigDecimal previousBoost = BigDecimal.ZERO;
        if (context != null && context.hasPreviousLeaderRows()) {
            for (Map<String, Object> row : context.getPreviousLeaderRows()) {
                Object stockCode = row.get("stock_code");
                if (stockCode != null && stockCode.toString().equals(feature.getStockCode())) {
                    previousBoost = BigDecimal.valueOf(25);
                    break;
                }
            }
        }
        return clamp(boardContinuity.multiply(BigDecimal.valueOf(0.75)).add(previousBoost));
    }

    private BigDecimal negativeFeedbackScore(LeaderCandidateFeature feature, LeaderRecognitionContext context) {
        BigDecimal brokenBoardDropScore = Boolean.TRUE.equals(feature.getBrokenBoard())
                ? clamp(feature.getPctChange().abs().multiply(BigDecimal.valueOf(5)))
                : BigDecimal.ZERO;

        BigDecimal backRowLossEffect = BigDecimal.ZERO;
        if (context != null && context.hasRiskRows()) {
            backRowLossEffect = BigDecimal.valueOf(Math.min(100, context.getRiskRows().size() * 5L));
        }

        BigDecimal promotionDrop = BigDecimal.ZERO;
        if (feature.getMaxBoardHeight() != null && feature.getMaxBoardHeight().compareTo(BigDecimal.ZERO) > 0) {
            promotionDrop = BigDecimal.valueOf(100).subtract(ratioScore(feature.getBoardHeight(), feature.getMaxBoardHeight()));
        }

        BigDecimal emotionCooling = feature.getPctChange().compareTo(BigDecimal.ZERO) < 0
                ? clamp(feature.getPctChange().abs().multiply(BigDecimal.valueOf(5)))
                : BigDecimal.ZERO;

        return clamp(brokenBoardDropScore.multiply(BigDecimal.valueOf(0.25))
                .add(feature.getSectorStrengthScore() == null ? BigDecimal.ZERO : BigDecimal.valueOf(100).subtract(feature.getSectorStrengthScore()).multiply(BigDecimal.valueOf(0.25)))
                .add(backRowLossEffect.multiply(BigDecimal.valueOf(0.20)))
                .add(promotionDrop.multiply(BigDecimal.valueOf(0.15)))
                .add(emotionCooling.multiply(BigDecimal.valueOf(0.15))));
    }

    private void fillRankTypeStatus(LeaderScore score, int rankNo) {
        score.setRankNo(rankNo);
        if (rankNo == 1) {
            score.setLeaderType("MARKET_LEADER_CANDIDATE");
            score.setLeaderStatus("CONFIRMED_BY_COMPOSITE_SCORE");
            return;
        }
        if (score.getMainlineRelationScore().compareTo(BigDecimal.valueOf(60)) >= 0) {
            score.setLeaderType("MAINLINE_LEADER");
            score.setLeaderStatus("CANDIDATE");
            return;
        }
        if (score.getFeature().getBoardHeight().compareTo(BigDecimal.valueOf(2)) >= 0) {
            score.setLeaderType("HIGH_BOARD_LEADER");
            score.setLeaderStatus("CANDIDATE");
            return;
        }
        if (score.getStrengthScore().compareTo(BigDecimal.valueOf(60)) >= 0
                && score.getContinuityScore().compareTo(BigDecimal.valueOf(50)) >= 0) {
            score.setLeaderType("TREND_LEADER");
            score.setLeaderStatus("CANDIDATE");
            return;
        }
        score.setLeaderType("FOLLOWER");
        score.setLeaderStatus("OBSERVING");
    }

    private BigDecimal ratioScore(BigDecimal value, BigDecimal max) {
        if (value == null || max == null || max.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        return clamp(value.multiply(BigDecimal.valueOf(100)).divide(max, 4, RoundingMode.HALF_UP));
    }

    private BigDecimal max(List<BigDecimal> values) {
        return values.stream()
                .filter(v -> v != null && v.compareTo(BigDecimal.ZERO) > 0)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private String buildEvidenceJson(LeaderScore score) {
        return "{"
                + "\"stockCode\":\"" + score.getFeature().getStockCode() + "\","
                + "\"stockName\":\"" + score.getFeature().getStockName() + "\","
                + "\"recognitionScore\":" + score.getRecognitionScore() + ","
                + "\"mainlineRelationScore\":" + score.getMainlineRelationScore() + ","
                + "\"driveScore\":" + score.getDriveScore() + ","
                + "\"strengthScore\":" + score.getStrengthScore() + ","
                + "\"supportScore\":" + score.getSupportScore() + ","
                + "\"continuityScore\":" + score.getContinuityScore() + ","
                + "\"riskFeedbackScore\":" + score.getRiskFeedbackScore() + ","
                + "\"leaderScore\":" + score.getLeaderScore() + ","
                + "\"notHighestBoardOnly\":true"
                + "}";
    }

    private String buildRiskJson(LeaderScore score) {
        return "{"
                + "\"riskNote\":\"龙头识别由七维连续分综合计算，不使用最高板等同市场总龙头\","
                + "\"negativeFeedbackScore\":" + score.getNegativeFeedbackScore() + ","
                + "\"leaderType\":\"" + score.getLeaderType() + "\""
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
