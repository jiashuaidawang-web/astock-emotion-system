package com.astock.infrastructure.engine;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class EngineSnapshotLoadService {
    private static final Set<String> ALLOWED_INPUT_TABLES = Set.of(
            "market_factor_snapshot", "limit_up_down_ecology_snapshot", "stock_daily_kline",
            "emotion_stage_snapshot", "emotion_stage_score_detail", "stage_transition_snapshot",
            "historical_similarity_match", "historical_similarity_factor_detail",
            "historical_cycle_sample", "historical_cycle_sample_factor", "historical_following_performance",
            "theme_daily_snapshot", "theme_strength_snapshot", "mainline_daily_snapshot", "mainline_switch_snapshot",
            "sector_daily_snapshot", "sector_strength_snapshot", "sector_stock_mapping_snapshot",
            "leader_daily_snapshot", "leader_ladder_snapshot", "leader_drive_snapshot", "leader_negative_feedback",
            "trend_leader_snapshot", "leader_similarity_match",
            "buy_pattern_signal_snapshot", "pattern_risk_veto_snapshot",
            "risk_signal_snapshot", "risk_signal_detail", "risk_similarity_match",
            "backtest_signal_detail", "backtest_performance_detail", "backtest_layer_stat", "backtest_failure_case",
            "agent_audit_code_scan_detail", "agent_audit_data_lineage_detail", "agent_audit_rule_hit_detail", "agent_audit_release_gate_detail"
    );

    private final ClickHouseQueryExecutor clickHouseQueryExecutor;

    public EngineSnapshotLoadService(ClickHouseQueryExecutor clickHouseQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
    }

    public PageSnapshotBundle load(EngineRunCommand command) {
        PageSnapshotBundle bundle = new PageSnapshotBundle();
        for (String table : command.getInputTables()) {
            if (!ALLOWED_INPUT_TABLES.contains(table)) {
                throw new IllegalArgumentException("未允许的引擎输入表：" + table);
            }
            String sql = "select * from " + table + " where trade_date = :tradeDate limit 2000";
            List<Map<String, Object>> rows = clickHouseQueryExecutor.queryForList(sql, Map.of(
                    "tradeDate", command.getTradeDate(),
                    "marketScope", command.getMarketScope() == null ? "" : command.getMarketScope()
            ));
            bundle.putRows(table, rows);
        }
        return bundle;
    }
}
