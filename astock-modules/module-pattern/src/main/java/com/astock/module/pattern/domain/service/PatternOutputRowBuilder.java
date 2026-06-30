package com.astock.module.pattern.domain.service;

import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.module.pattern.domain.model.PatternSignalScore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatternOutputRowBuilder {

    public Map<String, List<Map<String, Object>>> buildRows(EngineRunCommand command,
                                                            Long ruleVersionId,
                                                            List<PatternSignalScore> signals) {
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        result.put("buy_pattern_signal_snapshot", buildSignalRows(command, ruleVersionId, signals));
        result.put("pattern_risk_veto_snapshot", buildRiskVetoRows(command, ruleVersionId, signals));
        return result;
    }

    private List<Map<String, Object>> buildSignalRows(EngineRunCommand command,
                                                      Long ruleVersionId,
                                                      List<PatternSignalScore> signals) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (PatternSignalScore signal : signals) {
            Map<String, Object> row = baseRow(command, ruleVersionId);
            row.put("pattern_code", signal.getPatternType().getCode());
            row.put("pattern_name", signal.getPatternType().getName());
            row.put("stock_code", signal.getWatchObject().getStockCode());
            row.put("stock_name", signal.getWatchObject().getStockName());
            row.put("watch_object_type", signal.getWatchObject().getWatchObjectType());
            row.put("leader_type", signal.getWatchObject().getLeaderType());
            row.put("leader_status", signal.getWatchObject().getLeaderStatus());
            row.put("mainline_code", signal.getWatchObject().getMainlineCode());
            row.put("mainline_name", signal.getWatchObject().getMainlineName());
            row.put("emotion_stage", signal.getWatchObject().getEmotionStage());
            row.put("condition_status", signal.getConditionStatus());
            row.put("condition_score", signal.getPatternConditionScore());
            row.put("pattern_condition_score", signal.getPatternConditionScore());
            row.put("cycle_admission_score", signal.getCycleAdmissionScore());
            row.put("mainline_valid_score", signal.getMainlineValidScore());
            row.put("leader_position_score", signal.getLeaderPositionScore());
            row.put("trigger_score", signal.getTriggerScore());
            row.put("backtest_support_score", signal.getBacktestSupportScore());
            row.put("manual_correction_score", signal.getManualCorrectionScore());
            row.put("risk_veto", signal.getRiskVeto());
            row.put("risk_veto_reason", signal.getRiskVetoReason());
            row.put("invalidated", signal.getInvalidated());
            row.put("invalidated_reason", signal.getInvalidatedReason());
            row.put("allow_condition_met_display", signal.getAllowConditionMetDisplay());
            row.put("signal_text", "条件状态：" + signal.getConditionStatus() + "。本字段不是交易建议。");
            row.put("evidence_json", signal.getEvidenceJson());
            row.put("risk_json", signal.getRiskJson());
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> buildRiskVetoRows(EngineRunCommand command,
                                                        Long ruleVersionId,
                                                        List<PatternSignalScore> signals) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (PatternSignalScore signal : signals) {
            if (!Boolean.TRUE.equals(signal.getRiskVeto()) && !Boolean.TRUE.equals(signal.getInvalidated())) {
                continue;
            }
            Map<String, Object> row = baseRow(command, ruleVersionId);
            row.put("pattern_code", signal.getPatternType().getCode());
            row.put("pattern_name", signal.getPatternType().getName());
            row.put("stock_code", signal.getWatchObject().getStockCode());
            row.put("stock_name", signal.getWatchObject().getStockName());
            row.put("leader_type", signal.getWatchObject().getLeaderType());
            row.put("condition_status", signal.getConditionStatus());
            row.put("condition_score", signal.getPatternConditionScore());
            row.put("risk_veto", signal.getRiskVeto());
            row.put("risk_veto_reason", signal.getRiskVetoReason());
            row.put("invalidated", signal.getInvalidated());
            row.put("invalidated_reason", signal.getInvalidatedReason());
            row.put("risk_action", Boolean.TRUE.equals(signal.getRiskVeto()) ? "RISK_VETO" : "PATTERN_INVALIDATED");
            row.put("evidence_json", signal.getEvidenceJson());
            row.put("risk_json", signal.getRiskJson());
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
