package com.astock.infrastructure.dataquality;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Repository
public class ClickHouseSnapshotExistenceRepository implements SnapshotExistenceRepository {
    private static final Set<String> ALLOWED_TABLES = Set.of(
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
            "backtest_signal_detail", "backtest_performance_detail", "backtest_layer_stat", "backtest_failure_case"
    );

    private final NamedParameterJdbcTemplate clickHouse;

    public ClickHouseSnapshotExistenceRepository(@Qualifier("clickHouseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate clickHouse) {
        this.clickHouse = clickHouse;
    }

    @Override
    public long countByTradeDate(String tableName, LocalDate tradeDate, String marketScope) {
        if (!ALLOWED_TABLES.contains(tableName)) {
            throw new IllegalArgumentException("未在白名单中的ClickHouse表：" + tableName);
        }
        String sql = "select count() from " + tableName + " where trade_date = :tradeDate";
        if (marketScope != null && !marketScope.isBlank() && hasMarketScope(tableName)) {
            sql += " and market_scope = :marketScope";
        }
        Long count = clickHouse.queryForObject(sql, Map.of(
                "tradeDate", tradeDate,
                "marketScope", marketScope == null ? "" : marketScope
        ), Long.class);
        return count == null ? 0L : count;
    }

    private boolean hasMarketScope(String tableName) {
        return Set.of("market_factor_snapshot", "limit_up_down_ecology_snapshot",
                "emotion_stage_snapshot", "emotion_stage_score_detail", "risk_signal_snapshot").contains(tableName);
    }
}
