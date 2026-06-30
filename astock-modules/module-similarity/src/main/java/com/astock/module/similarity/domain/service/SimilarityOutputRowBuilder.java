package com.astock.module.similarity.domain.service;

import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.module.similarity.domain.model.SimilarityDimensionScore;
import com.astock.module.similarity.domain.model.SimilarityMatchCandidate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimilarityOutputRowBuilder {

    public Map<String, List<Map<String, Object>>> buildRows(EngineRunCommand command,
                                                            Long ruleVersionId,
                                                            List<SimilarityMatchCandidate> candidates) {
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        result.put("historical_similarity_match", buildMatchRows(command, ruleVersionId, candidates));
        result.put("historical_similarity_factor_detail", buildFactorDetailRows(command, ruleVersionId, candidates));
        return result;
    }

    private List<Map<String, Object>> buildMatchRows(EngineRunCommand command,
                                                     Long ruleVersionId,
                                                     List<SimilarityMatchCandidate> candidates) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SimilarityMatchCandidate candidate : candidates) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("trade_date", command.getTradeDate());
            row.put("market_scope", command.getMarketScope());
            row.put("task_id", command.getTaskId());
            row.put("rule_version_id", ruleVersionId);
            row.put("match_type", candidate.getMatchType());
            row.put("sample_id", candidate.getSampleId());
            row.put("historical_trade_date", candidate.getHistoricalTradeDate());
            row.put("historical_stage", candidate.getHistoricalStage());
            row.put("market_environment_similarity_score", candidate.getMarketEnvironmentSimilarityScore());
            row.put("emotion_cycle_similarity_score", candidate.getEmotionCycleSimilarityScore());
            row.put("theme_leader_similarity_score", candidate.getThemeLeaderSimilarityScore());
            row.put("total_similarity_score", candidate.getTotalSimilarityScore());
            row.put("dimension_score_json", dimensionScoreJson(candidate.getDimensionScores()));
            row.put("reference_text", candidate.getEvidenceJson());
            row.put("risk_text", candidate.getRiskText());
            row.put("created_at", LocalDateTime.now());
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> buildFactorDetailRows(EngineRunCommand command,
                                                            Long ruleVersionId,
                                                            List<SimilarityMatchCandidate> candidates) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SimilarityMatchCandidate candidate : candidates) {
            for (SimilarityDimensionScore score : candidate.getDimensionScores()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("trade_date", command.getTradeDate());
                row.put("market_scope", command.getMarketScope());
                row.put("task_id", command.getTaskId());
                row.put("rule_version_id", ruleVersionId);
                row.put("match_type", candidate.getMatchType());
                row.put("sample_id", candidate.getSampleId());
                row.put("historical_trade_date", candidate.getHistoricalTradeDate());
                row.put("dimension_code", score.getDimensionType().getCode());
                row.put("dimension_name", score.getDimensionType().getName());
                row.put("dimension_group_code", score.getDimensionType().getGroupCode());
                row.put("dimension_weight", score.getDimensionType().getWeight());
                row.put("current_value", score.getCurrentValue());
                row.put("historical_value", score.getHistoricalValue());
                row.put("dimension_similarity_score", score.getSimilarityScore());
                row.put("evidence_json", "{\"futureFieldUsed\":false,\"dimension\":\"" + score.getDimensionType().getCode() + "\"}");
                row.put("created_at", LocalDateTime.now());
                rows.add(row);
            }
        }
        return rows;
    }

    private String dimensionScoreJson(List<SimilarityDimensionScore> scores) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < scores.size(); i++) {
            SimilarityDimensionScore score = scores.get(i);
            if (i > 0) {
                builder.append(",");
            }
            builder.append("{")
                    .append("\"dimensionCode\":\"").append(score.getDimensionType().getCode()).append("\",")
                    .append("\"score\":").append(score.getSimilarityScore()).append(",")
                    .append("\"weight\":").append(score.getDimensionType().getWeight())
                    .append("}");
        }
        builder.append("]");
        return builder.toString();
    }
}
