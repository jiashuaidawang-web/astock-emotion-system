package com.astock.infrastructure.engine;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EngineSnapshotWriteService {
    private static final Set<String> ALLOWED_OUTPUT_TABLES = Set.of(
            "emotion_stage_snapshot", "emotion_stage_score_detail", "stage_transition_snapshot",
            "historical_similarity_match", "historical_similarity_factor_detail",
            "mainline_daily_snapshot", "theme_strength_snapshot", "mainline_switch_snapshot",
            "leader_daily_snapshot", "leader_ladder_snapshot", "leader_drive_snapshot", "leader_negative_feedback", "trend_leader_snapshot",
            "buy_pattern_signal_snapshot", "pattern_risk_veto_snapshot",
            "risk_signal_snapshot", "risk_signal_detail",
            "backtest_signal_detail", "backtest_performance_detail", "backtest_layer_stat", "backtest_failure_case",
            "agent_audit_code_scan_detail", "agent_audit_data_lineage_detail", "agent_audit_rule_hit_detail", "agent_audit_release_gate_detail"
    );

    private final NamedParameterJdbcTemplate clickHouse;

    public EngineSnapshotWriteService(@Qualifier("clickHouseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate clickHouse) {
        this.clickHouse = clickHouse;
    }

    public int writeRows(String tableName, List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return 0;
        }
        if (!ALLOWED_OUTPUT_TABLES.contains(tableName)) {
            throw new IllegalArgumentException("未允许的引擎输出表：" + tableName);
        }
        int total = 0;
        for (Map<String, Object> row : rows) {
            String columns = String.join(", ", row.keySet());
            String values = row.keySet().stream().map(k -> ":" + k).collect(Collectors.joining(", "));
            String sql = "insert into " + tableName + " (" + columns + ") values (" + values + ")";
            total += clickHouse.update(sql, row);
        }
        return total;
    }
}
