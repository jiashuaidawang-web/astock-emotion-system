package com.astock.module.leader.domain.service;

import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.module.leader.domain.model.LeaderScore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LeaderOutputRowBuilder {

    public Map<String, List<Map<String, Object>>> buildRows(EngineRunCommand command,
                                                            Long ruleVersionId,
                                                            List<LeaderScore> scores) {
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        result.put("leader_daily_snapshot", buildLeaderDailyRows(command, ruleVersionId, scores));
        result.put("leader_ladder_snapshot", buildLadderRows(command, ruleVersionId, scores));
        result.put("leader_drive_snapshot", buildDriveRows(command, ruleVersionId, scores));
        result.put("leader_negative_feedback", buildNegativeFeedbackRows(command, ruleVersionId, scores));
        return result;
    }

    private List<Map<String, Object>> buildLeaderDailyRows(EngineRunCommand command,
                                                           Long ruleVersionId,
                                                           List<LeaderScore> scores) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (LeaderScore score : scores) {
            Map<String, Object> row = baseRow(command, ruleVersionId);
            row.put("stock_code", score.getFeature().getStockCode());
            row.put("stock_name", score.getFeature().getStockName());
            row.put("sector_code", score.getFeature().getSectorCode());
            row.put("sector_name", score.getFeature().getSectorName());
            row.put("mainline_code", score.getFeature().getMainlineCode());
            row.put("mainline_name", score.getFeature().getMainlineName());
            row.put("leader_type", score.getLeaderType());
            row.put("leader_status", score.getLeaderStatus());
            row.put("leader_score", score.getLeaderScore());
            row.put("rank_no", score.getRankNo());
            row.put("recognition_score", score.getRecognitionScore());
            row.put("mainline_relation_score", score.getMainlineRelationScore());
            row.put("drive_score", score.getDriveScore());
            row.put("strength_score", score.getStrengthScore());
            row.put("support_score", score.getSupportScore());
            row.put("continuity_score", score.getContinuityScore());
            row.put("risk_feedback_score", score.getRiskFeedbackScore());
            row.put("negative_feedback_score", score.getNegativeFeedbackScore());
            row.put("board_height", score.getFeature().getBoardHeight());
            row.put("limit_up", score.getFeature().getLimitUp());
            row.put("broken_board", score.getFeature().getBrokenBoard());
            row.put("pct_change", score.getFeature().getPctChange());
            row.put("turnover_amount", score.getFeature().getTurnoverAmount());
            row.put("evidence_json", score.getEvidenceJson());
            row.put("risk_json", score.getRiskJson());
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> buildLadderRows(EngineRunCommand command,
                                                      Long ruleVersionId,
                                                      List<LeaderScore> scores) {
        Map<Object, List<LeaderScore>> grouped = scores.stream()
                .collect(Collectors.groupingBy(score -> score.getFeature().getBoardHeight()));

        List<Map<String, Object>> rows = new ArrayList<>();
        for (Map.Entry<Object, List<LeaderScore>> entry : grouped.entrySet()) {
            List<LeaderScore> groupScores = entry.getValue().stream()
                    .sorted(Comparator.comparing(LeaderScore::getLeaderScore).reversed())
                    .toList();

            Map<String, Object> row = baseRow(command, ruleVersionId);
            row.put("board_height", entry.getKey());
            row.put("stock_count", groupScores.size());
            row.put("top_stock_code", groupScores.get(0).getFeature().getStockCode());
            row.put("top_stock_name", groupScores.get(0).getFeature().getStockName());
            row.put("top_leader_score", groupScores.get(0).getLeaderScore());
            row.put("leader_type", groupScores.get(0).getLeaderType());
            row.put("evidence_json", "{\"ladderNote\":\"梯队表按板高分组，但组内排序仍使用综合leader_score\"}");
            row.put("risk_json", "{\"riskNote\":\"板高只作为梯队维度，不等于市场总龙头判定\"}");
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> buildDriveRows(EngineRunCommand command,
                                                     Long ruleVersionId,
                                                     List<LeaderScore> scores) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (LeaderScore score : scores) {
            Map<String, Object> row = baseRow(command, ruleVersionId);
            row.put("stock_code", score.getFeature().getStockCode());
            row.put("stock_name", score.getFeature().getStockName());
            row.put("sector_drive_score", score.getFeature().getSectorStrengthScore());
            row.put("mainline_drive_score", score.getFeature().getMainlineStrengthScore());
            row.put("emotion_drive_score", score.getStrengthScore());
            row.put("fund_drive_score", score.getSupportScore());
            row.put("leader_drive_score", score.getDriveScore());
            row.put("evidence_json", "{\"formula\":\"sector 35% + mainline 30% + emotion 20% + fund 15%\"}");
            row.put("risk_json", score.getRiskJson());
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> buildNegativeFeedbackRows(EngineRunCommand command,
                                                                Long ruleVersionId,
                                                                List<LeaderScore> scores) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (LeaderScore score : scores) {
            Map<String, Object> row = baseRow(command, ruleVersionId);
            row.put("stock_code", score.getFeature().getStockCode());
            row.put("stock_name", score.getFeature().getStockName());
            row.put("leader_type", score.getLeaderType());
            row.put("negative_feedback_score", score.getNegativeFeedbackScore());
            row.put("broken_board", score.getFeature().getBrokenBoard());
            row.put("limit_down", false);
            row.put("impact_mainline", score.getNegativeFeedbackScore().compareTo(java.math.BigDecimal.valueOf(60)) >= 0);
            row.put("impact_emotion_cycle", score.getNegativeFeedbackScore().compareTo(java.math.BigDecimal.valueOf(70)) >= 0);
            row.put("evidence_json", "{\"formula\":\"broken board 25% + sector pressure 25% + back row loss 20% + promotion drop 15% + emotion cooling 15%\"}");
            row.put("risk_json", score.getRiskJson());
            rows.add(row);
        }
        return rows;
    }

    private Map<String, Object> baseRow(EngineRunCommand command, Long ruleVersionId) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("trade_date", command.getTradeDate());
        row.put("market_scope", command.getMarketScope());
        row.put("task_id", command.getTaskId());
        row.put("rule_version_id", ruleVersionId);
        row.put("created_at", LocalDateTime.now());
        return row;
    }
}
