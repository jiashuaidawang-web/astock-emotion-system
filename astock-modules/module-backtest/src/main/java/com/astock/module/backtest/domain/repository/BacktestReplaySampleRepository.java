package com.astock.module.backtest.domain.repository;

import com.astock.common.data.PageSnapshotBundle;
import java.time.LocalDate;

public interface BacktestReplaySampleRepository {
    PageSnapshotBundle loadReplayData(LocalDate endDate, String marketScope, int sampleLimit);
}
