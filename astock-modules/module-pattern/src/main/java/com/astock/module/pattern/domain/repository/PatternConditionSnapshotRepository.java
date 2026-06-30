package com.astock.module.pattern.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PatternConditionSnapshotRepository {
    List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit);
}
