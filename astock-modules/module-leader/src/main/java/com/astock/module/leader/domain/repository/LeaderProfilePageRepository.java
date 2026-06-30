package com.astock.module.leader.domain.repository;

import com.astock.common.data.PageSnapshotBundle;
import java.time.LocalDate;

public interface LeaderProfilePageRepository {
    PageSnapshotBundle queryPage(LocalDate tradeDate, String marketScope, int limit);
}
