package com.astock.module.rule.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RuleVersionSnapshotRepository {
    List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit);
}
