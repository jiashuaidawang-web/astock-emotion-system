package com.astock.module.backtest.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.backtest.domain.repository.BacktestReportPageRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class BacktestReportMultiTableRepository implements BacktestReportPageRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public BacktestReportMultiTableRepository(ClickHouseQueryExecutor clickHouseQueryExecutor, MysqlQueryExecutor mysqlQueryExecutor) {
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
                bundle.putRows("backtest_signal_detail", clickHouseQueryExecutor.queryForList(BacktestReportPageSql.CK_BACKTEST_SIGNAL_DETAIL, params));
        bundle.putRows("backtest_performance_detail", clickHouseQueryExecutor.queryForList(BacktestReportPageSql.CK_BACKTEST_PERFORMANCE_DETAIL, params));
        bundle.putRows("backtest_layer_stat", clickHouseQueryExecutor.queryForList(BacktestReportPageSql.CK_BACKTEST_LAYER_STAT, params));
        bundle.putRows("backtest_failure_case", clickHouseQueryExecutor.queryForList(BacktestReportPageSql.CK_BACKTEST_FAILURE_CASE, params));
        bundle.putRows("pattern_backtest_result", clickHouseQueryExecutor.queryForList(BacktestReportPageSql.CK_PATTERN_BACKTEST_RESULT, params));
        bundle.putRows("backtest_report", mysqlQueryExecutor.queryForList(BacktestReportPageSql.MYSQL_BACKTEST_REPORT, params));
        bundle.putRows("backtest_task", mysqlQueryExecutor.queryForList(BacktestReportPageSql.MYSQL_BACKTEST_TASK, params));
        bundle.putRows("backtest_task_param", mysqlQueryExecutor.queryForList(BacktestReportPageSql.MYSQL_BACKTEST_TASK_PARAM, params));
        bundle.putRows("backtest_rule_binding", mysqlQueryExecutor.queryForList(BacktestReportPageSql.MYSQL_BACKTEST_RULE_BINDING, params));
        bundle.putRows("rule_version", mysqlQueryExecutor.queryForList(BacktestReportPageSql.MYSQL_RULE_VERSION, params));
        bundle.putRows("data_quality_check_log", mysqlQueryExecutor.queryForList(BacktestReportPageSql.MYSQL_DATA_QUALITY_CHECK_LOG, params));
        return bundle;
    }
}
