package com.astock.module.mainline.infrastructure.query;

import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.module.mainline.domain.model.MainlineRecognitionContext;
import com.astock.module.mainline.domain.repository.MainlineRecognitionContextRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class MainlineRecognitionContextRepositoryImpl implements MainlineRecognitionContextRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;

    public MainlineRecognitionContextRepositoryImpl(ClickHouseQueryExecutor clickHouseQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
    }

    @Override
    public MainlineRecognitionContext load(LocalDate tradeDate, String marketScope) {
        Map<String, Object> params = Map.of(
                "tradeDate", tradeDate,
                "marketScope", marketScope == null ? "" : marketScope
        );
        MainlineRecognitionContext context = new MainlineRecognitionContext();
        context.setSectorRows(clickHouseQueryExecutor.queryForList(MainlineRecognitionContextSql.SELECT_SECTOR_ROWS, params));
        context.setLeaderRows(clickHouseQueryExecutor.queryForList(MainlineRecognitionContextSql.SELECT_LEADER_ROWS, params));
        context.setPreviousMainlineRows(clickHouseQueryExecutor.queryForList(MainlineRecognitionContextSql.SELECT_PREVIOUS_MAINLINE_ROWS, params));
        return context;
    }
}
