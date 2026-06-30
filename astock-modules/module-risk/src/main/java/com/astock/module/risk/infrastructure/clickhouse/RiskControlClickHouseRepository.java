package com.astock.module.risk.infrastructure.clickhouse;

import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.module.risk.domain.repository.RiskControlSnapshotRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class RiskControlClickHouseRepository implements RiskControlSnapshotRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;

    public RiskControlClickHouseRepository(ClickHouseQueryExecutor clickHouseQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
    }

    @Override
    public List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit) {
        return clickHouseQueryExecutor.queryForList(RiskControlClickHouseSql.SELECT_PRIMARY_ROWS,
                Map.of("tradeDate", tradeDate, "marketScope", marketScope == null ? "" : marketScope, "limit", limit));
    }
}
