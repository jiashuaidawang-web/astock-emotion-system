package com.astock.module.leader.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.leader.domain.repository.LeaderLadderPageRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class LeaderLadderMultiTableRepository implements LeaderLadderPageRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public LeaderLadderMultiTableRepository(ClickHouseQueryExecutor clickHouseQueryExecutor, MysqlQueryExecutor mysqlQueryExecutor) {
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
                bundle.putRows("leader_daily_snapshot", clickHouseQueryExecutor.queryForList(LeaderLadderPageSql.CK_LEADER_DAILY_SNAPSHOT, params));
        bundle.putRows("leader_ladder_snapshot", clickHouseQueryExecutor.queryForList(LeaderLadderPageSql.CK_LEADER_LADDER_SNAPSHOT, params));
        bundle.putRows("leader_drive_snapshot", clickHouseQueryExecutor.queryForList(LeaderLadderPageSql.CK_LEADER_DRIVE_SNAPSHOT, params));
        bundle.putRows("leader_negative_feedback", clickHouseQueryExecutor.queryForList(LeaderLadderPageSql.CK_LEADER_NEGATIVE_FEEDBACK, params));
        bundle.putRows("trend_leader_snapshot", clickHouseQueryExecutor.queryForList(LeaderLadderPageSql.CK_TREND_LEADER_SNAPSHOT, params));
        bundle.putRows("mainline_daily_snapshot", clickHouseQueryExecutor.queryForList(LeaderLadderPageSql.CK_MAINLINE_DAILY_SNAPSHOT, params));
        bundle.putRows("risk_signal_snapshot", clickHouseQueryExecutor.queryForList(LeaderLadderPageSql.CK_RISK_SIGNAL_SNAPSHOT, params));
        bundle.putRows("buy_pattern_signal_snapshot", clickHouseQueryExecutor.queryForList(LeaderLadderPageSql.CK_BUY_PATTERN_SIGNAL_SNAPSHOT, params));
        bundle.putRows("leader_rule_version", mysqlQueryExecutor.queryForList(LeaderLadderPageSql.MYSQL_LEADER_RULE_VERSION, params));
        bundle.putRows("leader_type_definition", mysqlQueryExecutor.queryForList(LeaderLadderPageSql.MYSQL_LEADER_TYPE_DEFINITION, params));
        bundle.putRows("leader_manual_confirm", mysqlQueryExecutor.queryForList(LeaderLadderPageSql.MYSQL_LEADER_MANUAL_CONFIRM, params));
        return bundle;
    }
}
