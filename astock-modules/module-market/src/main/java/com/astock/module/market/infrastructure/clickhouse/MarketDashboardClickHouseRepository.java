package com.astock.module.market.infrastructure.clickhouse;

import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.module.market.domain.repository.MarketDashboardSnapshotRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class MarketDashboardClickHouseRepository implements MarketDashboardSnapshotRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;

    public MarketDashboardClickHouseRepository(ClickHouseQueryExecutor clickHouseQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
    }

    @Override
    public List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit) {
        return clickHouseQueryExecutor.queryForList(MarketDashboardClickHouseSql.SELECT_PRIMARY_ROWS,
                Map.of("tradeDate", tradeDate, "marketScope", marketScope == null ? "" : marketScope, "limit", limit));
    }
}
