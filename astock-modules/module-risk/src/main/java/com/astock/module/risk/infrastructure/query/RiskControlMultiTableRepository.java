package com.astock.module.risk.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.risk.domain.repository.RiskControlPageRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class RiskControlMultiTableRepository implements RiskControlPageRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public RiskControlMultiTableRepository(ClickHouseQueryExecutor clickHouseQueryExecutor, MysqlQueryExecutor mysqlQueryExecutor) {
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
                bundle.putRows("risk_signal_snapshot", clickHouseQueryExecutor.queryForList(RiskControlPageSql.CK_RISK_SIGNAL_SNAPSHOT, params));
        bundle.putRows("risk_signal_detail", clickHouseQueryExecutor.queryForList(RiskControlPageSql.CK_RISK_SIGNAL_DETAIL, params));
        bundle.putRows("pattern_risk_veto_snapshot", clickHouseQueryExecutor.queryForList(RiskControlPageSql.CK_PATTERN_RISK_VETO_SNAPSHOT, params));
        bundle.putRows("leader_negative_feedback", clickHouseQueryExecutor.queryForList(RiskControlPageSql.CK_LEADER_NEGATIVE_FEEDBACK, params));
        bundle.putRows("mainline_daily_snapshot", clickHouseQueryExecutor.queryForList(RiskControlPageSql.CK_MAINLINE_DAILY_SNAPSHOT, params));
        bundle.putRows("historical_cycle_sample", clickHouseQueryExecutor.queryForList(RiskControlPageSql.CK_HISTORICAL_CYCLE_SAMPLE, params));
        bundle.putRows("risk_similarity_match", clickHouseQueryExecutor.queryForList(RiskControlPageSql.CK_RISK_SIMILARITY_MATCH, params));
        bundle.putRows("risk_action_matrix", mysqlQueryExecutor.queryForList(RiskControlPageSql.MYSQL_RISK_ACTION_MATRIX, params));
        bundle.putRows("risk_rule_version", mysqlQueryExecutor.queryForList(RiskControlPageSql.MYSQL_RISK_RULE_VERSION, params));
        bundle.putRows("data_quality_check_log", mysqlQueryExecutor.queryForList(RiskControlPageSql.MYSQL_DATA_QUALITY_CHECK_LOG, params));
        return bundle;
    }
}
