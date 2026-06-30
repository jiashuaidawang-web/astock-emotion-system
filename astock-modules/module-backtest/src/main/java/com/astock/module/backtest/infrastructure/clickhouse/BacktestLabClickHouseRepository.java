package com.astock.module.backtest.infrastructure.clickhouse;

import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.module.backtest.domain.repository.BacktestLabSnapshotRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class BacktestLabClickHouseRepository implements BacktestLabSnapshotRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;

    public BacktestLabClickHouseRepository(ClickHouseQueryExecutor clickHouseQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
    }

    @Override
    public List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit) {
        return clickHouseQueryExecutor.queryForList(BacktestLabClickHouseSql.SELECT_PRIMARY_ROWS,
                Map.of("tradeDate", tradeDate, "marketScope", marketScope == null ? "" : marketScope, "limit", limit));
    }
}
