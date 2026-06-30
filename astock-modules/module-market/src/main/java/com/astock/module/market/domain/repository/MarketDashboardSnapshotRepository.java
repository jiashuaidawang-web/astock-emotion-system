package com.astock.module.market.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MarketDashboardSnapshotRepository {
    List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit);
}
