package com.astock.module.backtest.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.module.backtest.domain.repository.BacktestReplaySampleRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class BacktestReplaySampleRepositoryImpl implements BacktestReplaySampleRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;

    public BacktestReplaySampleRepositoryImpl(ClickHouseQueryExecutor clickHouseQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
    }

    @Override
    public PageSnapshotBundle loadReplayData(LocalDate endDate, String marketScope, int sampleLimit) {
        Map<String, Object> params = Map.of(
                "endDate", endDate,
                "marketScope", marketScope == null ? "" : marketScope,
                "sampleLimit", sampleLimit
        );

        PageSnapshotBundle bundle = new PageSnapshotBundle();
        bundle.putRows("historical_cycle_sample", clickHouseQueryExecutor.queryForList(
                BacktestReplaySampleSql.SELECT_HISTORICAL_CYCLE_SAMPLE, params));
        bundle.putRows("buy_pattern_signal_snapshot", clickHouseQueryExecutor.queryForList(
                BacktestReplaySampleSql.SELECT_BUY_PATTERN_SIGNAL, params));
        bundle.putRows("risk_signal_snapshot", clickHouseQueryExecutor.queryForList(
                BacktestReplaySampleSql.SELECT_RISK_SIGNAL, params));
        bundle.putRows("stock_daily_kline", clickHouseQueryExecutor.queryForList(
                BacktestReplaySampleSql.SELECT_STOCK_DAILY_KLINE, params));
        return bundle;
    }
}
