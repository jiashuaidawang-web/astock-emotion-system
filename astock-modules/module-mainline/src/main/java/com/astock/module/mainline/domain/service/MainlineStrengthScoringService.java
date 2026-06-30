package com.astock.module.mainline.domain.service;

import com.astock.module.mainline.domain.model.MainlineScore;
import com.astock.module.mainline.domain.model.MainlineThemeFeature;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MainlineStrengthScoringService {

    public List<MainlineScore> score(List<MainlineThemeFeature> features) {
        BigDecimal maxLimitUp = max(features.stream().map(MainlineThemeFeature::getLimitUpCount).toList());
        BigDecimal maxTurnover = max(features.stream().map(MainlineThemeFeature::getTurnoverAmount).toList());
        BigDecimal maxContinuity = max(features.stream().map(MainlineThemeFeature::getContinuityDays).toList());
        BigDecimal maxBoardHeight = max(features.stream().map(MainlineThemeFeature::getMaxBoardHeight).toList());
        BigDecimal maxLeaderDrive = max(features.stream().map(MainlineThemeFeature::getLeaderDriveRawScore).toList());

        List<MainlineScore> scores = features.stream()
                .map(feature -> scoreOne(feature, maxLimitUp, maxTurnover, maxContinuity, maxBoardHeight, maxLeaderDrive))
                .sorted(Comparator.comparing(MainlineScore::getMainlineStrengthScore).reversed())
                .toList();

        AtomicInteger rank = new AtomicInteger(1);
        scores.forEach(score -> fillRankAndRole(score, rank.getAndIncrement(), scores.size()));
        return scores;
    }

    private MainlineScore scoreOne(MainlineThemeFeature feature,
                                   BigDecimal maxLimitUp,
                                   BigDecimal maxTurnover,
                                   BigDecimal maxContinuity,
                                   BigDecimal maxBoardHeight,
                                   BigDecimal maxLeaderDrive) {
        BigDecimal limitUpClusterScore = limitUpClusterScore(feature, maxLimitUp);
        BigDecimal turnoverConcentrationScore = ratioScore(feature.getTurnoverAmount(), maxTurnover);
        BigDecimal continuityScore = ratioScore(feature.getContinuityDays(), maxContinuity);
        BigDecimal ladderIntegrityScore = ladderIntegrityScore(feature, maxBoardHeight);
        BigDecimal leaderDriveScore = ratioScore(feature.getLeaderDriveRawScore(), maxLeaderDrive);
        BigDecimal emotionMatchScore = emotionMatchScore(feature.getEmotionStage(),
                limitUpClusterScore, turnoverConcentrationScore, leaderDriveScore);

        BigDecimal mainlineStrengthScore = limitUpClusterScore.multiply(BigDecimal.valueOf(0.20))
                .add(turnoverConcentrationScore.multiply(BigDecimal.valueOf(0.20)))
                .add(continuityScore.multiply(BigDecimal.valueOf(0.15)))
                .add(ladderIntegrityScore.multiply(BigDecimal.valueOf(0.15)))
                .add(leaderDriveScore.multiply(BigDecimal.valueOf(0.20)))
                .add(emotionMatchScore.multiply(BigDecimal.valueOf(0.10)))
                .setScale(4, RoundingMode.HALF_UP);

        MainlineScore score = new MainlineScore();
        score.setFeature(feature);
        score.setLimitUpClusterScore(clamp(limitUpClusterScore));
        score.setTurnoverConcentrationScore(clamp(turnoverConcentrationScore));
        score.setContinuityScore(clamp(continuityScore));
        score.setLadderIntegrityScore(clamp(ladderIntegrityScore));
        score.setLeaderDriveScore(clamp(leaderDriveScore));
        score.setEmotionMatchScore(clamp(emotionMatchScore));
        score.setMainlineStrengthScore(clamp(mainlineStrengthScore));
        score.setEvidenceJson(buildEvidenceJson(score));
        score.setRiskJson(buildRiskJson(score));
        return score;
    }

    /**
     * mainline_strength_score =
     * 涨停聚集强度 * 20%
     * + 成交额集中强度 * 20%
     * + 持续性强度 * 15%
     * + 梯队完整度 * 15%
     * + 龙头带动性 * 20%
     * + 情绪周期匹配度 * 10%
     *
     * 注意：这里不是“涨幅第一=主线”，也不是“涨停最多=主线”。
     * 每个主题都计算六维连续分，再按综合分排序。
     */
    private BigDecimal limitUpClusterScore(MainlineThemeFeature feature, BigDecimal maxLimitUp) {
        BigDecimal limitUpNormalized = ratioScore(feature.getLimitUpCount(), maxLimitUp);
        BigDecimal densityScore;
        if (feature.getStockCount() == null || feature.getStockCount().compareTo(BigDecimal.ZERO) <= 0) {
            densityScore = BigDecimal.ZERO;
        } else {
            densityScore = feature.getLimitUpCount()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(feature.getStockCount(), 4, RoundingMode.HALF_UP);
        }
        return clamp(limitUpNormalized.multiply(BigDecimal.valueOf(0.70))
                .add(densityScore.multiply(BigDecimal.valueOf(0.30))));
    }

    private BigDecimal ladderIntegrityScore(MainlineThemeFeature feature, BigDecimal maxBoardHeight) {
        BigDecimal boardHeightScore = ratioScore(feature.getMaxBoardHeight(), maxBoardHeight);
        BigDecimal leaderPresenceScore = feature.getLeaderCount() == null
                ? BigDecimal.ZERO
                : clamp(feature.getLeaderCount().multiply(BigDecimal.valueOf(25)));
        return clamp(boardHeightScore.multiply(BigDecimal.valueOf(0.65))
                .add(leaderPresenceScore.multiply(BigDecimal.valueOf(0.35))));
    }

    private BigDecimal emotionMatchScore(String emotionStage,
                                         BigDecimal limitUpClusterScore,
                                         BigDecimal turnoverConcentrationScore,
                                         BigDecimal leaderDriveScore) {
        BigDecimal activity = limitUpClusterScore.add(turnoverConcentrationScore).add(leaderDriveScore)
                .divide(BigDecimal.valueOf(3), 4, RoundingMode.HALF_UP);

        BigDecimal stageWeight = switch (emotionStage == null ? "UNKNOWN" : emotionStage) {
            case "STARTUP" -> BigDecimal.valueOf(85);
            case "FERMENTATION" -> BigDecimal.valueOf(95);
            case "MAIN_RISE" -> BigDecimal.valueOf(100);
            case "CLIMAX" -> BigDecimal.valueOf(70);
            case "DIVERGENCE" -> BigDecimal.valueOf(60);
            case "REPAIR", "TRIAL" -> BigDecimal.valueOf(75);
            case "RETREAT", "ICE_POINT" -> BigDecimal.valueOf(35);
            default -> BigDecimal.valueOf(50);
        };

        return clamp(activity.multiply(BigDecimal.valueOf(0.70)).add(stageWeight.multiply(BigDecimal.valueOf(0.30))));
    }

    private void fillRankAndRole(MainlineScore score, int rankNo, int totalCount) {
        score.setRankNo(rankNo);

        if (rankNo == 1) {
            score.setThemeRole("ABSOLUTE_MAINLINE_CANDIDATE");
            score.setMainlineStatus("CONFIRMED_BY_RANK");
            score.setLifecycleStage(resolveLifecycle(score));
            return;
        }
        if (rankNo <= Math.max(2, Math.min(3, totalCount))) {
            score.setThemeRole("PARALLEL_MAINLINE_CANDIDATE");
            score.setMainlineStatus("CANDIDATE_BY_RANK");
            score.setLifecycleStage(resolveLifecycle(score));
            return;
        }
        score.setThemeRole("ROTATION_HOTSPOT");
        score.setMainlineStatus("OBSERVING");
        score.setLifecycleStage(resolveLifecycle(score));
    }

    private String resolveLifecycle(MainlineScore score) {
        String emotionStage = score.getFeature().getEmotionStage();
        BigDecimal continuity = score.getContinuityScore();
        BigDecimal leaderDrive = score.getLeaderDriveScore();

        if ("RETREAT".equals(emotionStage) || "ICE_POINT".equals(emotionStage)) {
            return "WATCHING";
        }
        if ("STARTUP".equals(emotionStage) || "TRIAL".equals(emotionStage)) {
            return "STARTUP";
        }
        if ("FERMENTATION".equals(emotionStage)) {
            return "FERMENTATION";
        }
        if ("MAIN_RISE".equals(emotionStage) && leaderDrive.compareTo(continuity) >= 0) {
            return "MAIN_RISE";
        }
        if ("CLIMAX".equals(emotionStage)) {
            return "CLIMAX";
        }
        if ("DIVERGENCE".equals(emotionStage)) {
            return "DIVERGENCE";
        }
        return "TRIAL";
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

    private String buildEvidenceJson(MainlineScore score) {
        return "{"
                + "\"themeCode\":\"" + score.getFeature().getThemeCode() + "\","
                + "\"themeName\":\"" + score.getFeature().getThemeName() + "\","
                + "\"limitUpClusterScore\":" + score.getLimitUpClusterScore() + ","
                + "\"turnoverConcentrationScore\":" + score.getTurnoverConcentrationScore() + ","
                + "\"continuityScore\":" + score.getContinuityScore() + ","
                + "\"ladderIntegrityScore\":" + score.getLadderIntegrityScore() + ","
                + "\"leaderDriveScore\":" + score.getLeaderDriveScore() + ","
                + "\"emotionMatchScore\":" + score.getEmotionMatchScore() + ","
                + "\"mainlineStrengthScore\":" + score.getMainlineStrengthScore() + ","
                + "\"notSingleFactor\":true"
                + "}";
    }

    private String buildRiskJson(MainlineScore score) {
        return "{"
                + "\"riskNote\":\"主线识别由六维连续分综合计算，不使用涨幅第一或涨停最多单因子硬判定\","
                + "\"emotionStage\":\"" + score.getFeature().getEmotionStage() + "\","
                + "\"themeRole\":\"" + score.getThemeRole() + "\""
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
