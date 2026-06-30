package com.astock.module.mainline.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MainlineRadarSnapshotRepository {
    List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit);
}
