package com.astock.module.review.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DailyReviewSnapshotRepository {
    List<Map<String, Object>> selectPrimaryRows(LocalDate tradeDate, String marketScope, int limit);
}
