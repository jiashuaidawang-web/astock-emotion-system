package com.astock.module.sector.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SectorStrengthSnapshotRepository {
    List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit);
}
