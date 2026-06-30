package com.astock.module.leader.infrastructure.query;

import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.module.leader.domain.model.LeaderRecognitionContext;
import com.astock.module.leader.domain.repository.LeaderRecognitionContextRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class LeaderRecognitionContextRepositoryImpl implements LeaderRecognitionContextRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;

    public LeaderRecognitionContextRepositoryImpl(ClickHouseQueryExecutor clickHouseQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
    }

    @Override
    public LeaderRecognitionContext load(LocalDate tradeDate, String marketScope) {
        Map<String, Object> params = Map.of(
                "tradeDate", tradeDate,
                "marketScope", marketScope == null ? "" : marketScope
        );

        LeaderRecognitionContext context = new LeaderRecognitionContext();
        context.setPreviousLeaderRows(clickHouseQueryExecutor.queryForList(LeaderRecognitionContextSql.SELECT_PREVIOUS_LEADER_ROWS, params));
        context.setRiskRows(clickHouseQueryExecutor.queryForList(LeaderRecognitionContextSql.SELECT_RISK_ROWS, params));
        context.setPatternRows(clickHouseQueryExecutor.queryForList(LeaderRecognitionContextSql.SELECT_PATTERN_ROWS, params));
        return context;
    }
}
