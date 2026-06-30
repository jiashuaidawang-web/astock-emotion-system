package com.astock.module.pattern.domain.service;

import com.astock.common.convert.MapFieldReader;
import com.astock.module.pattern.domain.model.PatternEngineSupportContext;
import com.astock.module.pattern.domain.model.PatternSignalScore;
import com.astock.module.pattern.domain.model.PatternType;
import com.astock.module.pattern.domain.model.PatternWatchObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PatternConditionScoringService {

    public List<PatternSignalScore> score(List<PatternWatchObject> watchPool, PatternEngineSupportContext supportContext) {
        List<PatternSignalScore> signals = new ArrayList<>();
        for (PatternWatchObject watchObject : watchPool) {
            for (PatternType patternType : PatternType.orderedPatterns()) {
                signals.add(scoreOne(patternType, watchObject, supportContext));
            }
        }
        return signals;
    }

    private PatternSignalScore scoreOne(PatternType patternType,
                                        PatternWatchObject watchObject,
                                        PatternEngineSupportContext supportContext) {
        BigDecimal cycleAdmissionScore = cycleAdmissionScore(patternType, watchObject);
        BigDecimal mainlineValidScore = mainlineValidScore(patternType, watchObject);
        BigDecimal leaderPositionScore = leaderPositionScore(patternType, watchObject);
        BigDecimal triggerScore = triggerScore(patternType, watchObject);
        BigDecimal backtestSupportScore = backtestSupportScore(patternType, watchObject, supportContext);
        BigDecimal manualCorrectionScore = manualCorrectionScore(patternType, watchObject, supportContext);

        BigDecimal conditionScore = cycleAdmissionScore.multiply(BigDecimal.valueOf(0.25))
                .add(mainlineValidScore.multiply(BigDecimal.valueOf(0.20)))
                .add(leaderPositionScore.multiply(BigDecimal.valueOf(0.20)))
                .add(triggerScore.multiply(BigDecimal.valueOf(0.20)))
                .add(backtestSupportScore.multiply(BigDecimal.valueOf(0.10)))
                .add(manualCorrectionScore.multiply(BigDecimal.valueOf(0.05)))
                .setScale(4, RoundingMode.HALF_UP);

        boolean riskVeto = Boolean.TRUE.equals(watchObject.getRiskVeto()) || riskBindingHit(patternType, supportContext);
        boolean invalidated = invalidated(patternType, watchObject);

        PatternSignalScore score = new PatternSignalScore();
        score.setPatternType(patternType);
        score.setWatchObject(watchObject);
        score.setCycleAdmissionScore(clamp(cycleAdmissionScore));
        score.setMainlineValidScore(clamp(mainlineValidScore));
        score.setLeaderPositionScore(clamp(leaderPositionScore));
        score.setTriggerScore(clamp(triggerScore));
        score.setBacktestSupportScore(clamp(backtestSupportScore));
        score.setManualCorrectionScore(clamp(manualCorrectionScore));
        score.setPatternConditionScore(clamp(conditionScore));
        score.setRiskVeto(riskVeto);
        score.setRiskVetoReason(riskVeto ? buildRiskVetoReason(patternType, watchObject) : null);
        score.setInvalidated(invalidated);
        score.setInvalidatedReason(invalidated ? "模式触发条件与当前对象状态冲突，标记失效" : null);
        score.setAllowConditionMetDisplay(allowConditionMetDisplay(watchObject, riskVeto, invalidated));
        score.setConditionStatus(resolveConditionStatus(score));
        score.setEvidenceJson(buildEvidenceJson(score));
        score.setRiskJson(buildRiskJson(score));
        return score;
    }

    /**
     * pattern_condition_score =
     * 周期准入分 * 25%
     * + 主线有效分 * 20%
     * + 龙头地位分 * 20%
     * + 模式触发分 * 20%
     * + 历史回测支持分 * 10%
     * + 人工确认修正 * 5%
     *
     * 注意：输出是“条件状态”，不是任何交易动作建议。
     */
    private BigDecimal cycleAdmissionScore(PatternType patternType, PatternWatchObject object) {
        String stage = object.getEmotionStage();
        if (stage == null) {
            return BigDecimal.valueOf(40);
        }
        return switch (patternType) {
            case ICE_REPAIR -> matchStage(stage, "ICE_POINT", "REPAIR");
            case MAINLINE_STARTUP -> matchStage(stage, "TRIAL", "STARTUP", "FERMENTATION");
            case LEADER_DIVERGENCE_TO_CONSISTENCY -> matchStage(stage, "DIVERGENCE", "MAIN_RISE", "FERMENTATION");
            case TREND_LEADER_PULLBACK -> matchStage(stage, "FERMENTATION", "MAIN_RISE", "DIVERGENCE");
            case CLIMAX_NO_CHASE -> matchStage(stage, "CLIMAX");
            case RETREAT_STOP -> matchStage(stage, "RETREAT", "ICE_POINT");
        };
    }

    private BigDecimal mainlineValidScore(PatternType patternType, PatternWatchObject object) {
        BigDecimal strength = object.getMainlineStrengthScore() == null ? BigDecimal.ZERO : object.getMainlineStrengthScore();
        if (patternType == PatternType.ICE_REPAIR || patternType == PatternType.RETREAT_STOP) {
            return clamp(BigDecimal.valueOf(100).subtract(strength.multiply(BigDecimal.valueOf(0.30))));
        }
        return clamp(strength);
    }

    private BigDecimal leaderPositionScore(PatternType patternType, PatternWatchObject object) {
        BigDecimal leaderScore = object.getLeaderScore() == null ? BigDecimal.ZERO : object.getLeaderScore();
        if ("FOLLOWER".equals(object.getLeaderType())) {
            return BigDecimal.ZERO;
        }
        if (patternType == PatternType.CLIMAX_NO_CHASE || patternType == PatternType.RETREAT_STOP) {
            return clamp(BigDecimal.valueOf(100).subtract(leaderScore.multiply(BigDecimal.valueOf(0.25))));
        }
        return clamp(leaderScore);
    }

    private BigDecimal triggerScore(PatternType patternType, PatternWatchObject object) {
        BigDecimal leaderScore = object.getLeaderScore() == null ? BigDecimal.ZERO : object.getLeaderScore();
        BigDecimal mainlineScore = object.getMainlineStrengthScore() == null ? BigDecimal.ZERO : object.getMainlineStrengthScore();
        BigDecimal riskScore = object.getRiskScore() == null ? BigDecimal.ZERO : object.getRiskScore();

        return switch (patternType) {
            case ICE_REPAIR -> clamp(BigDecimal.valueOf(100).subtract(riskScore));
            case MAINLINE_STARTUP -> clamp(mainlineScore.multiply(BigDecimal.valueOf(0.65)).add(leaderScore.multiply(BigDecimal.valueOf(0.35))));
            case LEADER_DIVERGENCE_TO_CONSISTENCY -> clamp(leaderScore.multiply(BigDecimal.valueOf(0.70)).add(BigDecimal.valueOf(100).subtract(riskScore).multiply(BigDecimal.valueOf(0.30))));
            case TREND_LEADER_PULLBACK -> clamp(leaderScore.multiply(BigDecimal.valueOf(0.55)).add(mainlineScore.multiply(BigDecimal.valueOf(0.45))));
            case CLIMAX_NO_CHASE -> clamp(riskScore);
            case RETREAT_STOP -> clamp(riskScore.multiply(BigDecimal.valueOf(0.80)).add(BigDecimal.valueOf(20)));
        };
    }

    private BigDecimal backtestSupportScore(PatternType patternType,
                                            PatternWatchObject object,
                                            PatternEngineSupportContext supportContext) {
        if (supportContext == null || !supportContext.hasPatternBacktestRows()) {
            return BigDecimal.valueOf(50);
        }

        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (Map<String, Object> row : supportContext.getPatternBacktestRows()) {
            String patternCode = MapFieldReader.string(row, "pattern_code");
            String leaderType = MapFieldReader.string(row, "leader_type");
            boolean patternMatched = patternType.getCode().equals(patternCode);
            boolean leaderMatched = leaderType == null || leaderType.equals(object.getLeaderType());
            if (!patternMatched || !leaderMatched) {
                continue;
            }
            BigDecimal support = readAny(row, "backtest_support_score", "win_rate", "effect_score");
            total = total.add(support);
            count++;
        }
        return count == 0
                ? BigDecimal.valueOf(50)
                : clamp(total.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP));
    }

    private BigDecimal manualCorrectionScore(PatternType patternType,
                                             PatternWatchObject object,
                                             PatternEngineSupportContext supportContext) {
        if (supportContext == null || !supportContext.hasStageMatrixRows()) {
            return BigDecimal.ZERO;
        }
        for (Map<String, Object> row : supportContext.getStageMatrixRows()) {
            String patternCode = MapFieldReader.string(row, "pattern_code");
            String stageCode = MapFieldReader.string(row, "stage_code");
            if (patternType.getCode().equals(patternCode)
                    && object.getEmotionStage() != null
                    && object.getEmotionStage().equals(stageCode)) {
                return BigDecimal.valueOf(60);
            }
        }
        return BigDecimal.ZERO;
    }

    private boolean riskBindingHit(PatternType patternType, PatternEngineSupportContext supportContext) {
        if (supportContext == null || !supportContext.hasRiskBindingRows()) {
            return false;
        }
        return supportContext.getRiskBindingRows().stream()
                .anyMatch(row -> patternType.getCode().equals(MapFieldReader.string(row, "pattern_code"))
                        && "RISK_VETO".equals(MapFieldReader.string(row, "risk_action")));
    }

    private boolean invalidated(PatternType patternType, PatternWatchObject object) {
        if (!Boolean.TRUE.equals(object.getPatternCalculationAllowed())) {
            return true;
        }
        if (patternType == PatternType.RETREAT_STOP && !"RETREAT".equals(object.getEmotionStage())) {
            return false;
        }
        return "FOLLOWER".equals(object.getLeaderType());
    }

    private boolean allowConditionMetDisplay(PatternWatchObject object, boolean riskVeto, boolean invalidated) {
        return Boolean.TRUE.equals(object.getPatternCalculationAllowed()) && !riskVeto && !invalidated;
    }

    private String resolveConditionStatus(PatternSignalScore score) {
        if (Boolean.TRUE.equals(score.getRiskVeto())) {
            return "RISK_VETO";
        }
        if (Boolean.TRUE.equals(score.getInvalidated())) {
            return "INVALIDATED";
        }
        if (!Boolean.TRUE.equals(score.getAllowConditionMetDisplay())) {
            return "NOT_APPLICABLE";
        }
        if (score.getPatternConditionScore().compareTo(BigDecimal.valueOf(75)) >= 0) {
            return "CONDITION_MET";
        }
        if (score.getPatternConditionScore().compareTo(BigDecimal.valueOf(55)) >= 0) {
            return "OBSERVING";
        }
        if (score.getPatternConditionScore().compareTo(BigDecimal.valueOf(40)) >= 0) {
            return "WEAK_MATCH";
        }
        return "WAITING";
    }

    private String buildRiskVetoReason(PatternType patternType, PatternWatchObject object) {
        if (object.getRiskVetoReason() != null) {
            return object.getRiskVetoReason();
        }
        return "模式 " + patternType.getCode() + " 绑定风控否决，条件状态必须降级为RISK_VETO";
    }

    private BigDecimal matchStage(String actual, String... expected) {
        for (String item : expected) {
            if (item.equals(actual)) {
                return BigDecimal.valueOf(100);
            }
        }
        return BigDecimal.valueOf(35);
    }

    private BigDecimal readAny(Map<String, Object> row, String... columns) {
        for (String column : columns) {
            BigDecimal value = MapFieldReader.decimal(row, column);
            if (value != null) {
                if ("win_rate".equals(column)) {
                    return clamp(value.multiply(BigDecimal.valueOf(100)));
                }
                return clamp(value);
            }
        }
        return BigDecimal.ZERO;
    }

    private String buildEvidenceJson(PatternSignalScore score) {
        return "{"
                + "\"patternCode\":\"" + score.getPatternType().getCode() + "\","
                + "\"stockCode\":\"" + score.getWatchObject().getStockCode() + "\","
                + "\"cycleAdmissionScore\":" + score.getCycleAdmissionScore() + ","
                + "\"mainlineValidScore\":" + score.getMainlineValidScore() + ","
                + "\"leaderPositionScore\":" + score.getLeaderPositionScore() + ","
                + "\"triggerScore\":" + score.getTriggerScore() + ","
                + "\"backtestSupportScore\":" + score.getBacktestSupportScore() + ","
                + "\"manualCorrectionScore\":" + score.getManualCorrectionScore() + ","
                + "\"patternConditionScore\":" + score.getPatternConditionScore() + ","
                + "\"notTradingAdvice\":true"
                + "}";
    }

    private String buildRiskJson(PatternSignalScore score) {
        return "{"
                + "\"riskNote\":\"模式条件判定只输出条件状态，不输出任何交易动作或价格判断\","
                + "\"riskVeto\":" + score.getRiskVeto() + ","
                + "\"conditionStatus\":\"" + score.getConditionStatus() + "\""
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
