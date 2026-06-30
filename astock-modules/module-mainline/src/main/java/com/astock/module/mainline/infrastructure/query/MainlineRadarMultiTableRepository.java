package com.astock.module.mainline.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.mainline.domain.repository.MainlineRadarPageRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class MainlineRadarMultiTableRepository implements MainlineRadarPageRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public MainlineRadarMultiTableRepository(ClickHouseQueryExecutor clickHouseQueryExecutor, MysqlQueryExecutor mysqlQueryExecutor) {
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
                bundle.putRows("mainline_daily_snapshot", clickHouseQueryExecutor.queryForList(MainlineRadarPageSql.CK_MAINLINE_DAILY_SNAPSHOT, params));
        bundle.putRows("theme_strength_snapshot", clickHouseQueryExecutor.queryForList(MainlineRadarPageSql.CK_THEME_STRENGTH_SNAPSHOT, params));
        bundle.putRows("theme_daily_snapshot", clickHouseQueryExecutor.queryForList(MainlineRadarPageSql.CK_THEME_DAILY_SNAPSHOT, params));
        bundle.putRows("mainline_switch_snapshot", clickHouseQueryExecutor.queryForList(MainlineRadarPageSql.CK_MAINLINE_SWITCH_SNAPSHOT, params));
        bundle.putRows("leader_daily_snapshot", clickHouseQueryExecutor.queryForList(MainlineRadarPageSql.CK_LEADER_DAILY_SNAPSHOT, params));
        bundle.putRows("leader_drive_snapshot", clickHouseQueryExecutor.queryForList(MainlineRadarPageSql.CK_LEADER_DRIVE_SNAPSHOT, params));
        bundle.putRows("risk_signal_detail", clickHouseQueryExecutor.queryForList(MainlineRadarPageSql.CK_RISK_SIGNAL_DETAIL, params));
        bundle.putRows("mainline_similarity_match", clickHouseQueryExecutor.queryForList(MainlineRadarPageSql.CK_MAINLINE_SIMILARITY_MATCH, params));
        bundle.putRows("theme_definition", mysqlQueryExecutor.queryForList(MainlineRadarPageSql.MYSQL_THEME_DEFINITION, params));
        bundle.putRows("theme_stock_mapping", mysqlQueryExecutor.queryForList(MainlineRadarPageSql.MYSQL_THEME_STOCK_MAPPING, params));
        bundle.putRows("mainline_rule_version", mysqlQueryExecutor.queryForList(MainlineRadarPageSql.MYSQL_MAINLINE_RULE_VERSION, params));
        return bundle;
    }
}
