package com.astock.module.emotion.domain.service;

import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.module.emotion.domain.model.EmotionHistoricalContext;
import com.astock.module.emotion.domain.model.EmotionStageScore;
import com.astock.module.emotion.domain.model.EmotionStageType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmotionStageOutputRowBuilder {

    public Map<String, List<Map<String, Object>>> buildRows(EngineRunCommand command,
                                                            Long ruleVersionId,
                                                            List<EmotionStageScore> scores,
                                                            EmotionHistoricalContext historicalContext) {
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        result.put("emotion_stage_score_detail", buildScoreDetailRows(command, ruleVersionId, scores, historicalContext));
        result.put("emotion_stage_snapshot", List.of(buildSnapshotRow(command, ruleVersionId, scores, historicalContext)));
        result.put("stage_transition_snapshot", buildTransitionRows(command, ruleVersionId, scores));
        return result;
    }

    private List<Map<String, Object>> buildScoreDetailRows(EngineRunCommand command,
                                                           Long ruleVersionId,
                                                           List<EmotionStageScore> scores,
                                                           EmotionHistoricalContext historicalContext) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (EmotionStageScore score : scores) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("trade_date", command.getTradeDate());
            row.put("market_scope", command.getMarketScope());
            row.put("task_id", command.getTaskId());
            row.put("rule_version_id", ruleVersionId);
            row.put("stage_code", score.getStageType().getCode());
            row.put("stage_name", score.getStageType().getName());
            row.put("stage_score", score.getStageScore());
            row.put("rank_no", score.getRankNo());
            row.put("factor_percentile_match_score", score.getFactorPercentileMatchScore());
            row.put("historical_sample_similarity_score", score.getHistoricalSampleSimilarityScore());
            row.put("stage_path_match_score", score.getStagePathMatchScore());
            row.put("following_validation_score", score.getFollowingValidationScore());
            row.put("manual_sample_correction_score", score.getManualSampleCorrectionScore());
            row.put("evidence_json", score.getEvidenceText());
            row.put("risk_json", score.getRiskText());
            row.put("features", buildFeatureSourceJson(historicalContext));
            row.put("created_at", LocalDateTime.now());
            rows.add(row);
        }
        return rows;
    }

    private Map<String, Object> buildSnapshotRow(EngineRunCommand command,
                                                 Long ruleVersionId,
                                                 List<EmotionStageScore> scores,
                                                 EmotionHistoricalContext historicalContext) {
        EmotionStageScore first = scores.get(0);
        EmotionStageScore second = scores.size() > 1 ? scores.get(1) : first;
        EmotionStageScore third = scores.size() > 2 ? scores.get(2) : second;

        Map<String, Object> row = new LinkedHashMap<>();
        row.put("trade_date", command.getTradeDate());
        row.put("market_scope", command.getMarketScope());
        row.put("task_id", command.getTaskId());
        row.put("rule_version_id", ruleVersionId);
        row.put("primary_stage", first.getStageType().getCode());
        row.put("primary_stage_name", first.getStageType().getName());
        row.put("stage_confidence", first.getStageScore());
        row.put("second_candidate_stage", second.getStageType().getCode());
        row.put("second_candidate_stage_name", second.getStageType().getName());
        row.put("third_candidate_stage", third.getStageType().getCode());
        row.put("third_candidate_stage_name", third.getStageType().getName());
        row.put("evidence_json", first.getEvidenceText());
        row.put("risk_json", first.getRiskText());
        row.put("features", buildFeatureSourceJson(historicalContext));
        row.put("data_complete", true);
        row.put("created_at", LocalDateTime.now());
        return row;
    }

    private List<Map<String, Object>> buildTransitionRows(EngineRunCommand command,
                                                          Long ruleVersionId,
                                                          List<EmotionStageScore> scores) {
        EmotionStageScore current = scores.get(0);
        List<EmotionStageType> candidates = possibleNextStages(current.getStageType());

        List<Map<String, Object>> rows = new ArrayList<>();
        for (EmotionStageType toStage : candidates) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("trade_date", command.getTradeDate());
            row.put("market_scope", command.getMarketScope());
            row.put("task_id", command.getTaskId());
            row.put("rule_version_id", ruleVersionId);
            row.put("from_stage", current.getStageType().getCode());
            row.put("to_stage", toStage.getCode());
            row.put("transition_probability", transitionProbability(toStage, scores));
            row.put("transition_score", transitionProbability(toStage, scores));
            row.put("evidence_json", "{\"fromStage\":\"" + current.getStageType().getCode() + "\",\"toStage\":\"" + toStage.getCode() + "\"}");
            row.put("created_at", LocalDateTime.now());
            rows.add(row);
        }
        return rows;
    }

    private String buildFeatureSourceJson(EmotionHistoricalContext historicalContext) {
        int historicalSampleCount = historicalContext == null || historicalContext.getHistoricalCycleSamples() == null ? 0 : historicalContext.getHistoricalCycleSamples().size();
        int confirmCount = historicalContext == null || historicalContext.getCycleSampleConfirms() == null ? 0 : historicalContext.getCycleSampleConfirms().size();
        int manualAdjustmentCount = historicalContext == null || historicalContext.getManualStageAdjustments() == null ? 0 : historicalContext.getManualStageAdjustments().size();
        int transitionCount = historicalContext == null || historicalContext.getRecentStageTransitions() == null ? 0 : historicalContext.getRecentStageTransitions().size();
        return "{"
                + "\"historicalCycleSampleCount\":" + historicalSampleCount + ","
                + "\"cycleSampleConfirmCount\":" + confirmCount + ","
                + "\"manualStageAdjustmentCount\":" + manualAdjustmentCount + ","
                + "\"recentStageTransitionCount\":" + transitionCount
                + "}";
    }

    private List<EmotionStageType> possibleNextStages(EmotionStageType current) {
        return switch (current) {
            case ICE_POINT -> List.of(EmotionStageType.REPAIR, EmotionStageType.CHAOS);
            case REPAIR -> List.of(EmotionStageType.TRIAL, EmotionStageType.CHAOS);
            case TRIAL -> List.of(EmotionStageType.STARTUP, EmotionStageType.CHAOS, EmotionStageType.RETREAT);
            case STARTUP -> List.of(EmotionStageType.FERMENTATION, EmotionStageType.CHAOS);
            case FERMENTATION -> List.of(EmotionStageType.MAIN_RISE, EmotionStageType.DIVERGENCE);
            case MAIN_RISE -> List.of(EmotionStageType.CLIMAX, EmotionStageType.DIVERGENCE);
            case CLIMAX -> List.of(EmotionStageType.DIVERGENCE, EmotionStageType.RETREAT);
            case DIVERGENCE -> List.of(EmotionStageType.MAIN_RISE, EmotionStageType.RETREAT);
            case RETREAT -> List.of(EmotionStageType.ICE_POINT, EmotionStageType.REPAIR);
            case CHAOS -> List.of(EmotionStageType.STARTUP, EmotionStageType.RETREAT);
        };
    }

    private Object transitionProbability(EmotionStageType toStage, List<EmotionStageScore> scores) {
        return scores.stream()
                .filter(score -> score.getStageType() == toStage)
                .findFirst()
                .map(EmotionStageScore::getStageScore)
                .orElse(scores.get(0).getStageScore());
    }
}
