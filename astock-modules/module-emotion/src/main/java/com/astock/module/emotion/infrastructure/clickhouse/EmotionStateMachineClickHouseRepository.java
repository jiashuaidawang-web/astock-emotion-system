package com.astock.module.emotion.infrastructure.clickhouse;

import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.module.emotion.domain.repository.EmotionStateMachineSnapshotRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class EmotionStateMachineClickHouseRepository implements EmotionStateMachineSnapshotRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;

    public EmotionStateMachineClickHouseRepository(ClickHouseQueryExecutor clickHouseQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
    }

    @Override
    public List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit) {
        return clickHouseQueryExecutor.queryForList(EmotionStateMachineClickHouseSql.SELECT_PRIMARY_ROWS,
                Map.of("tradeDate", tradeDate, "marketScope", marketScope == null ? "" : marketScope, "limit", limit));
    }
}
