package com.astock.module.similarity.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HistoricalSimilaritySnapshotRepository {
    List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit);
}
