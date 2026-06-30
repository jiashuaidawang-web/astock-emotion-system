package com.astock.module.pattern.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.pattern.domain.repository.PatternConditionPageRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class PatternConditionMultiTableRepository implements PatternConditionPageRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public PatternConditionMultiTableRepository(ClickHouseQueryExecutor clickHouseQueryExecutor, MysqlQueryExecutor mysqlQueryExecutor) {
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
                bundle.putRows("buy_pattern_signal_snapshot", clickHouseQueryExecutor.queryForList(PatternConditionPageSql.CK_BUY_PATTERN_SIGNAL_SNAPSHOT, params));
        bundle.putRows("pattern_risk_veto_snapshot", clickHouseQueryExecutor.queryForList(PatternConditionPageSql.CK_PATTERN_RISK_VETO_SNAPSHOT, params));
        bundle.putRows("risk_signal_snapshot", clickHouseQueryExecutor.queryForList(PatternConditionPageSql.CK_RISK_SIGNAL_SNAPSHOT, params));
        bundle.putRows("risk_signal_detail", clickHouseQueryExecutor.queryForList(PatternConditionPageSql.CK_RISK_SIGNAL_DETAIL, params));
        bundle.putRows("leader_daily_snapshot", clickHouseQueryExecutor.queryForList(PatternConditionPageSql.CK_LEADER_DAILY_SNAPSHOT, params));
        bundle.putRows("mainline_daily_snapshot", clickHouseQueryExecutor.queryForList(PatternConditionPageSql.CK_MAINLINE_DAILY_SNAPSHOT, params));
        bundle.putRows("trend_leader_snapshot", clickHouseQueryExecutor.queryForList(PatternConditionPageSql.CK_TREND_LEADER_SNAPSHOT, params));
        bundle.putRows("pattern_backtest_result", clickHouseQueryExecutor.queryForList(PatternConditionPageSql.CK_PATTERN_BACKTEST_RESULT, params));
        bundle.putRows("buy_pattern_definition", mysqlQueryExecutor.queryForList(PatternConditionPageSql.MYSQL_BUY_PATTERN_DEFINITION, params));
        bundle.putRows("buy_pattern_stage_matrix", mysqlQueryExecutor.queryForList(PatternConditionPageSql.MYSQL_BUY_PATTERN_STAGE_MATRIX, params));
        bundle.putRows("buy_pattern_rule_config", mysqlQueryExecutor.queryForList(PatternConditionPageSql.MYSQL_BUY_PATTERN_RULE_CONFIG, params));
        bundle.putRows("pattern_risk_binding", mysqlQueryExecutor.queryForList(PatternConditionPageSql.MYSQL_PATTERN_RISK_BINDING, params));
        return bundle;
    }
}
