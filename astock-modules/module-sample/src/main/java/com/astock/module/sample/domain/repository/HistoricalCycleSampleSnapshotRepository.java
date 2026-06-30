package com.astock.module.sample.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HistoricalCycleSampleSnapshotRepository {
    List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit);
}
