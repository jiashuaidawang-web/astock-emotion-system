package com.astock.module.emotion.domain.repository;

import com.astock.common.data.PageSnapshotBundle;
import java.time.LocalDate;

public interface EmotionStateMachinePageRepository {
    PageSnapshotBundle queryPage(LocalDate tradeDate, String marketScope, int limit);
}
