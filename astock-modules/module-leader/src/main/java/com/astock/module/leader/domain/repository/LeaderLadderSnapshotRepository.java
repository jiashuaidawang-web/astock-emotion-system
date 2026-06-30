package com.astock.module.leader.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LeaderLadderSnapshotRepository {
    List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit);
}
