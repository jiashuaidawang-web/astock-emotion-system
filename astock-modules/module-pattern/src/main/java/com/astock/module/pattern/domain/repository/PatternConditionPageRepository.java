package com.astock.module.pattern.domain.repository;

import com.astock.common.data.PageSnapshotBundle;
import java.time.LocalDate;

public interface PatternConditionPageRepository {
    PageSnapshotBundle queryPage(LocalDate tradeDate, String marketScope, int limit);
}
