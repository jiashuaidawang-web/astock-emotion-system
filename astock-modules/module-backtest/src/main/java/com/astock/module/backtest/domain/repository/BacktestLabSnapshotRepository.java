package com.astock.module.backtest.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BacktestLabSnapshotRepository {
    List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit);
}
