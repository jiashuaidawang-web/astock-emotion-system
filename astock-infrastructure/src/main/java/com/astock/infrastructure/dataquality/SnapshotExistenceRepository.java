package com.astock.infrastructure.dataquality;

import java.time.LocalDate;

public interface SnapshotExistenceRepository {
    long countByTradeDate(String tableName, LocalDate tradeDate, String marketScope);
}
