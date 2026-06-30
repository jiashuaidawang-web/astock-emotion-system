package com.astock.module.risk.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RiskControlSnapshotRepository {
    List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit);
}
