package com.astock.module.emotion.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EmotionStateMachineSnapshotRepository {
    List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit);
}
