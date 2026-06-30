package com.astock.module.rule.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.rule.domain.repository.RuleVersionPageRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class RuleVersionMultiTableRepository implements RuleVersionPageRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public RuleVersionMultiTableRepository(ClickHouseQueryExecutor clickHouseQueryExecutor, MysqlQueryExecutor mysqlQueryExecutor) {
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
                bundle.putRows("backtest_layer_stat", clickHouseQueryExecutor.queryForList(RuleVersionPageSql.CK_BACKTEST_LAYER_STAT, params));
        bundle.putRows("backtest_failure_case", clickHouseQueryExecutor.queryForList(RuleVersionPageSql.CK_BACKTEST_FAILURE_CASE, params));
        bundle.putRows("rule_definition", mysqlQueryExecutor.queryForList(RuleVersionPageSql.MYSQL_RULE_DEFINITION, params));
        bundle.putRows("rule_version", mysqlQueryExecutor.queryForList(RuleVersionPageSql.MYSQL_RULE_VERSION, params));
        bundle.putRows("rule_publish_check_log", mysqlQueryExecutor.queryForList(RuleVersionPageSql.MYSQL_RULE_PUBLISH_CHECK_LOG, params));
        bundle.putRows("rule_version_audit_log", mysqlQueryExecutor.queryForList(RuleVersionPageSql.MYSQL_RULE_VERSION_AUDIT_LOG, params));
        bundle.putRows("backtest_report", mysqlQueryExecutor.queryForList(RuleVersionPageSql.MYSQL_BACKTEST_REPORT, params));
        bundle.putRows("agent_audit_result", mysqlQueryExecutor.queryForList(RuleVersionPageSql.MYSQL_AGENT_AUDIT_RESULT, params));
        return bundle;
    }
}
