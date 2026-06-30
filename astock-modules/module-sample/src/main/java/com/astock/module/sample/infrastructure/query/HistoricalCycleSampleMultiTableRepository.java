package com.astock.module.sample.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.sample.domain.repository.HistoricalCycleSamplePageRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class HistoricalCycleSampleMultiTableRepository implements HistoricalCycleSamplePageRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public HistoricalCycleSampleMultiTableRepository(ClickHouseQueryExecutor clickHouseQueryExecutor, MysqlQueryExecutor mysqlQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
        this.mysqlQueryExecutor = mysqlQueryExecutor;
    }

    @Override
    public PageSnapshotBundle queryPage(LocalDate tradeDate, String marketScope, int limit) {
        Map<String, Object> params = Map.of(
                "tradeDate", tradeDate,
                "marketScope", marketScope == null ? "" : marketScope,
                "limit", limit
        );
        PageSnapshotBundle bundle = new PageSnapshotBundle();
                bundle.putRows("historical_cycle_sample", clickHouseQueryExecutor.queryForList(HistoricalCycleSamplePageSql.CK_HISTORICAL_CYCLE_SAMPLE, params));
        bundle.putRows("historical_cycle_sample_factor", clickHouseQueryExecutor.queryForList(HistoricalCycleSamplePageSql.CK_HISTORICAL_CYCLE_SAMPLE_FACTOR, params));
        bundle.putRows("historical_following_performance", clickHouseQueryExecutor.queryForList(HistoricalCycleSamplePageSql.CK_HISTORICAL_FOLLOWING_PERFORMANCE, params));
        bundle.putRows("emotion_stage_snapshot", clickHouseQueryExecutor.queryForList(HistoricalCycleSamplePageSql.CK_EMOTION_STAGE_SNAPSHOT, params));
        bundle.putRows("mainline_daily_snapshot", clickHouseQueryExecutor.queryForList(HistoricalCycleSamplePageSql.CK_MAINLINE_DAILY_SNAPSHOT, params));
        bundle.putRows("leader_daily_snapshot", clickHouseQueryExecutor.queryForList(HistoricalCycleSamplePageSql.CK_LEADER_DAILY_SNAPSHOT, params));
        bundle.putRows("risk_signal_snapshot", clickHouseQueryExecutor.queryForList(HistoricalCycleSamplePageSql.CK_RISK_SIGNAL_SNAPSHOT, params));
        bundle.putRows("cycle_mining_task", mysqlQueryExecutor.queryForList(HistoricalCycleSamplePageSql.MYSQL_CYCLE_MINING_TASK, params));
        bundle.putRows("cycle_sample_confirm", mysqlQueryExecutor.queryForList(HistoricalCycleSamplePageSql.MYSQL_CYCLE_SAMPLE_CONFIRM, params));
        return bundle;
    }
}
