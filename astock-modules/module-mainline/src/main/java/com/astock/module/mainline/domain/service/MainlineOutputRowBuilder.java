package com.astock.module.mainline.domain.service;

import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.module.mainline.domain.model.MainlineRecognitionContext;
import com.astock.module.mainline.domain.model.MainlineScore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MainlineOutputRowBuilder {

    public Map<String, List<Map<String, Object>>> buildRows(EngineRunCommand command,
                                                            Long ruleVersionId,
                                                            List<MainlineScore> scores,
                                                            MainlineRecognitionContext context) {
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        result.put("theme_strength_snapshot", buildThemeStrengthRows(command, ruleVersionId, scores));
        result.put("mainline_daily_snapshot", buildMainlineRows(command, ruleVersionId, scores));
        result.put("mainline_switch_snapshot", buildSwitchRows(command, ruleVersionId, scores, context));
        return result;
    }

    private List<Map<String, Object>> buildThemeStrengthRows(EngineRunCommand command,
                                                             Long ruleVersionId,
                                                             List<MainlineScore> scores) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (MainlineScore score : scores) {
            Map<String, Object> row = baseRow(command, ruleVersionId, score);
            row.put("theme_code", score.getFeature().getThemeCode());
            row.put("theme_name", score.getFeature().getThemeName());
            row.put("theme_type", score.getFeature().getThemeType());
            row.put("theme_strength_score", score.getMainlineStrengthScore());
            row.put("strength_score", score.getMainlineStrengthScore());
            row.put("rank_no", score.getRankNo());
            row.put("limit_up_cluster_score", score.getLimitUpClusterScore());
            row.put("turnover_concentration_score", score.getTurnoverConcentrationScore());
            row.put("continuity_score", score.getContinuityScore());
            row.put("ladder_integrity_score", score.getLadderIntegrityScore());
            row.put("leader_drive_score", score.getLeaderDriveScore());
            row.put("emotion_match_score", score.getEmotionMatchScore());
            row.put("evidence_json", score.getEvidenceJson());
            row.put("risk_json", score.getRiskJson());
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> buildMainlineRows(EngineRunCommand command,
                                                        Long ruleVersionId,
                                                        List<MainlineScore> scores) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (MainlineScore score : scores) {
            Map<String, Object> row = baseRow(command, ruleVersionId, score);
            row.put("mainline_id", stableMainlineId(score));
            row.put("mainline_code", score.getFeature().getThemeCode());
            row.put("mainline_name", score.getFeature().getThemeName());
            row.put("theme_code", score.getFeature().getThemeCode());
            row.put("theme_name", score.getFeature().getThemeName());
            row.put("mainline_status", score.getMainlineStatus());
            row.put("lifecycle_stage", score.getLifecycleStage());
            row.put("theme_role", score.getThemeRole());
            row.put("mainline_strength_score", score.getMainlineStrengthScore());
            row.put("rank_no", score.getRankNo());
            row.put("limit_up_cluster_score", score.getLimitUpClusterScore());
            row.put("turnover_concentration_score", score.getTurnoverConcentrationScore());
            row.put("continuity_score", score.getContinuityScore());
            row.put("ladder_integrity_score", score.getLadderIntegrityScore());
            row.put("leader_drive_score", score.getLeaderDriveScore());
            row.put("emotion_match_score", score.getEmotionMatchScore());
            row.put("evidence_json", score.getEvidenceJson());
            row.put("risk_json", score.getRiskJson());
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> buildSwitchRows(EngineRunCommand command,
                                                      Long ruleVersionId,
                                                      List<MainlineScore> scores,
                                                      MainlineRecognitionContext context) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (scores.isEmpty()) {
            return rows;
        }

        MainlineScore currentTop = scores.get(0);
        Map<String, Object> previousTop = context == null || !context.hasPreviousMainlineRows()
                ? Map.of()
                : context.getPreviousMainlineRows().get(0);

        String oldCode = string(previousTop, "mainline_code", "theme_code");
        String oldName = string(previousTop, "mainline_name", "theme_name");
        String newCode = currentTop.getFeature().getThemeCode();
        String newName = currentTop.getFeature().getThemeName();

        String switchStatus;
        if (oldCode == null || oldCode.isBlank()) {
            switchStatus = "NO_PREVIOUS_MAINLINE";
        } else if (oldCode.equals(newCode)) {
            switchStatus = "NO_SWITCH";
        } else {
            switchStatus = "SUSPECTED_SWITCH";
        }

        Map<String, Object> row = new LinkedHashMap<>();
        row.put("trade_date", command.getTradeDate());
        row.put("market_scope", command.getMarketScope());
        row.put("task_id", command.getTaskId());
        row.put("rule_version_id", ruleVersionId);
        row.put("old_mainline_code", oldCode);
        row.put("old_mainline_name", oldName);
        row.put("new_mainline_code", newCode);
        row.put("new_mainline_name", newName);
        row.put("switch_status", switchStatus);
        row.put("switch_score", currentTop.getMainlineStrengthScore());
        row.put("evidence_json", "{\"oldMainline\":\"" + oldCode + "\",\"newMainline\":\"" + newCode + "\",\"switchStatus\":\"" + switchStatus + "\"}");
        row.put("risk_json", "{\"riskNote\":\"主线切换基于综合强度排序与历史主线对比，不由单日涨幅或涨停数量决定\"}");
        row.put("created_at", LocalDateTime.now());
        rows.add(row);
        return rows;
    }

    private Map<String, Object> baseRow(EngineRunCommand command, Long ruleVersionId, MainlineScore score) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("trade_date", command.getTradeDate());
        row.put("market_scope", command.getMarketScope());
        row.put("task_id", command.getTaskId());
        row.put("rule_version_id", ruleVersionId);
        row.put("created_at", LocalDateTime.now());
        return row;
    }

    private long stableMainlineId(MainlineScore score) {
        String key = score.getFeature().getThemeCode();
        if (key == null || key.isBlank()) {
            key = score.getFeature().getThemeName();
        }
        return Math.abs((long) key.hashCode());
    }

    private String string(Map<String, Object> row, String... columns) {
        if (row == null || row.isEmpty()) {
            return null;
        }
        for (String column : columns) {
            Object value = row.get(column);
            if (value != null) {
                return String.valueOf(value);
            }
        }
        return null;
    }
}
