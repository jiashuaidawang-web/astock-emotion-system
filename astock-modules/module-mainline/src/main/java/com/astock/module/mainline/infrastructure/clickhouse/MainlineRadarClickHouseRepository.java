package com.astock.module.mainline.infrastructure.clickhouse;

import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.module.mainline.domain.repository.MainlineRadarSnapshotRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class MainlineRadarClickHouseRepository implements MainlineRadarSnapshotRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;

    public MainlineRadarClickHouseRepository(ClickHouseQueryExecutor clickHouseQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
    }

    @Override
    public List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit) {
        return clickHouseQueryExecutor.queryForList(MainlineRadarClickHouseSql.SELECT_PRIMARY_ROWS,
                Map.of("tradeDate", tradeDate, "marketScope", marketScope == null ? "" : marketScope, "limit", limit));
    }
}
