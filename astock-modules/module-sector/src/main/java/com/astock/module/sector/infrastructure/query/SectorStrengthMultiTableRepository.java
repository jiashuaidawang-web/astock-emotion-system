package com.astock.module.sector.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.sector.domain.repository.SectorStrengthPageRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class SectorStrengthMultiTableRepository implements SectorStrengthPageRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public SectorStrengthMultiTableRepository(ClickHouseQueryExecutor clickHouseQueryExecutor, MysqlQueryExecutor mysqlQueryExecutor) {
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
                bundle.putRows("sector_strength_snapshot", clickHouseQueryExecutor.queryForList(SectorStrengthPageSql.CK_SECTOR_STRENGTH_SNAPSHOT, params));
        bundle.putRows("sector_daily_snapshot", clickHouseQueryExecutor.queryForList(SectorStrengthPageSql.CK_SECTOR_DAILY_SNAPSHOT, params));
        bundle.putRows("sector_stock_mapping_snapshot", clickHouseQueryExecutor.queryForList(SectorStrengthPageSql.CK_SECTOR_STOCK_MAPPING_SNAPSHOT, params));
        bundle.putRows("mainline_daily_snapshot", clickHouseQueryExecutor.queryForList(SectorStrengthPageSql.CK_MAINLINE_DAILY_SNAPSHOT, params));
        bundle.putRows("theme_strength_snapshot", clickHouseQueryExecutor.queryForList(SectorStrengthPageSql.CK_THEME_STRENGTH_SNAPSHOT, params));
        bundle.putRows("leader_daily_snapshot", clickHouseQueryExecutor.queryForList(SectorStrengthPageSql.CK_LEADER_DAILY_SNAPSHOT, params));
        bundle.putRows("risk_signal_detail", clickHouseQueryExecutor.queryForList(SectorStrengthPageSql.CK_RISK_SIGNAL_DETAIL, params));
        bundle.putRows("sector_rule_version", mysqlQueryExecutor.queryForList(SectorStrengthPageSql.MYSQL_SECTOR_RULE_VERSION, params));
        bundle.putRows("theme_definition", mysqlQueryExecutor.queryForList(SectorStrengthPageSql.MYSQL_THEME_DEFINITION, params));
        return bundle;
    }
}
