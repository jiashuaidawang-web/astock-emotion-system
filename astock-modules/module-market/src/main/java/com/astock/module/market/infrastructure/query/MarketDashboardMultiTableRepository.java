package com.astock.module.market.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.market.domain.repository.MarketDashboardPageRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class MarketDashboardMultiTableRepository implements MarketDashboardPageRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public MarketDashboardMultiTableRepository(ClickHouseQueryExecutor clickHouseQueryExecutor, MysqlQueryExecutor mysqlQueryExecutor) {
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
                bundle.putRows("market_factor_snapshot", clickHouseQueryExecutor.queryForList(MarketDashboardPageSql.CK_MARKET_FACTOR_SNAPSHOT, params));
        bundle.putRows("limit_up_down_ecology_snapshot", clickHouseQueryExecutor.queryForList(MarketDashboardPageSql.CK_LIMIT_UP_DOWN_ECOLOGY_SNAPSHOT, params));
        bundle.putRows("emotion_stage_snapshot", clickHouseQueryExecutor.queryForList(MarketDashboardPageSql.CK_EMOTION_STAGE_SNAPSHOT, params));
        bundle.putRows("risk_signal_snapshot", clickHouseQueryExecutor.queryForList(MarketDashboardPageSql.CK_RISK_SIGNAL_SNAPSHOT, params));
        bundle.putRows("historical_similarity_match", clickHouseQueryExecutor.queryForList(MarketDashboardPageSql.CK_HISTORICAL_SIMILARITY_MATCH, params));
        bundle.putRows("mainline_daily_snapshot", clickHouseQueryExecutor.queryForList(MarketDashboardPageSql.CK_MAINLINE_DAILY_SNAPSHOT, params));
        bundle.putRows("leader_daily_snapshot", clickHouseQueryExecutor.queryForList(MarketDashboardPageSql.CK_LEADER_DAILY_SNAPSHOT, params));
        bundle.putRows("buy_pattern_signal_snapshot", clickHouseQueryExecutor.queryForList(MarketDashboardPageSql.CK_BUY_PATTERN_SIGNAL_SNAPSHOT, params));
        bundle.putRows("pattern_risk_veto_snapshot", clickHouseQueryExecutor.queryForList(MarketDashboardPageSql.CK_PATTERN_RISK_VETO_SNAPSHOT, params));
        return bundle;
    }
}
