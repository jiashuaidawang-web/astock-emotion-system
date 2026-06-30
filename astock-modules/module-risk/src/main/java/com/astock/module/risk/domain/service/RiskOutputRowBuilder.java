package com.astock.module.risk.domain.service;

import com.astock.common.convert.MapFieldReader;
import com.astock.infrastructure.engine.EngineRunCommand;
import com.astock.module.risk.domain.model.RiskControlContext;
import com.astock.module.risk.domain.model.RiskFactorSnapshot;
import com.astock.module.risk.domain.model.RiskSignalScore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RiskOutputRowBuilder {

    public Map<String, List<Map<String, Object>>> buildRows(EngineRunCommand command,
                                                            Long ruleVersionId,
                                                            RiskFactorSnapshot factor,
                                                            List<RiskSignalScore> signals,
                                                            RiskControlContext context) {
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        result.put("risk_signal_snapshot", List.of(buildSnapshotRow(command, ruleVersionId, signals.get(0))));
        result.put("risk_signal_detail", buildDetailRows(command, ruleVersionId, signals));
        result.put("pattern_risk_veto_snapshot", buildPatternVetoRows(command, ruleVersionId, signals.get(0), context));
        return result;
    }

    private Map<String, Object> buildSnapshotRow(EngineRunCommand command,
                                                 Long ruleVersionId,
                                                 RiskSignalScore totalRisk) {
        Map<String, Object> row = baseRow(command, ruleVersionId);
        row.put("risk_code", totalRisk.getRiskCode());
        row.put("risk_name", totalRisk.getRiskName());
        row.put("risk_source", totalRisk.getRiskSource());
        row.put("risk_score", totalRisk.getRiskScore());
        row.put("risk_level", totalRisk.getRiskLevel());
        row.put("signal_level", totalRisk.getSignalLevel());
        row.put("risk_action", totalRisk.getRiskAction());
        row.put("one_vote_veto", totalRisk.getOneVoteVeto());
        row.put("evidence_json", totalRisk.getEvidenceJson());
        row.put("risk_json", totalRisk.getRiskText());
        return row;
    }

    private List<Map<String, Object>> buildDetailRows(EngineRunCommand command,
                                                      Long ruleVersionId,
                                                      List<RiskSignalScore> signals) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (RiskSignalScore signal : signals) {
            Map<String, Object> row = baseRow(command, ruleVersionId);
            row.put("risk_code", signal.getRiskCode());
            row.put("risk_name", signal.getRiskName());
            row.put("risk_source", signal.getRiskSource());
            row.put("signal_level", signal.getSignalLevel());
            row.put("risk_level", signal.getRiskLevel());
            row.put("risk_score", signal.getRiskScore());
            row.put("risk_action", signal.getRiskAction());
            row.put("one_vote_veto", signal.getOneVoteVeto());
            row.put("risk_text", signal.getRiskText());
            row.put("evidence_json", signal.getEvidenceJson());
            row.put("risk_json", signal.getRiskText());
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> buildPatternVetoRows(EngineRunCommand command,
                                                           Long ruleVersionId,
                                                           RiskSignalScore totalRisk,
                                                           RiskControlContext context) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (context == null || !context.hasPatternSignals()) {
            return rows;
        }
        if (!shouldOverridePattern(totalRisk)) {
            return rows;
        }

        for (Map<String, Object> signal : context.getPatternSignalRows()) {
            Map<String, Object> row = baseRow(command, ruleVersionId);
            row.put("pattern_code", MapFieldReader.string(signal, "pattern_code"));
            row.put("pattern_name", MapFieldReader.string(signal, "pattern_name"));
            row.put("stock_code", MapFieldReader.string(signal, "stock_code"));
            row.put("stock_name", MapFieldReader.string(signal, "stock_name"));
            row.put("leader_type", MapFieldReader.string(signal, "leader_type"));
            row.put("condition_status", totalRisk.getRiskAction());
            row.put("condition_score", MapFieldReader.decimal(signal, "condition_score"));
            row.put("risk_veto", true);
            row.put("risk_veto_reason", "风控上级保护层二次覆盖：" + totalRisk.getRiskLevel() + "/" + totalRisk.getRiskAction());
            row.put("invalidated", "PATTERN_INVALIDATED".equals(totalRisk.getRiskAction()));
            row.put("invalidated_reason", "风险评分达到条件失效级别，由RiskControlEngine覆盖");
            row.put("risk_action", totalRisk.getRiskAction());
            row.put("evidence_json", totalRisk.getEvidenceJson());
            row.put("risk_json", totalRisk.getRiskText());
            rows.add(row);
        }
        return rows;
    }

    private boolean shouldOverridePattern(RiskSignalScore totalRisk) {
        return "RISK_VETO".equals(totalRisk.getRiskAction())
                || "PATTERN_INVALIDATED".equals(totalRisk.getRiskAction())
                || Boolean.TRUE.equals(totalRisk.getOneVoteVeto());
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
