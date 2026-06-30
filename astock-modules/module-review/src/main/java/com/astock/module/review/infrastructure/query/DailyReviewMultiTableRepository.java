package com.astock.module.review.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.review.domain.repository.DailyReviewPageRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class DailyReviewMultiTableRepository implements DailyReviewPageRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public DailyReviewMultiTableRepository(ClickHouseQueryExecutor clickHouseQueryExecutor, MysqlQueryExecutor mysqlQueryExecutor) {
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
                bundle.putRows("market_factor_snapshot", clickHouseQueryExecutor.queryForList(DailyReviewPageSql.CK_MARKET_FACTOR_SNAPSHOT, params));
        bundle.putRows("emotion_stage_snapshot", clickHouseQueryExecutor.queryForList(DailyReviewPageSql.CK_EMOTION_STAGE_SNAPSHOT, params));
        bundle.putRows("historical_similarity_match", clickHouseQueryExecutor.queryForList(DailyReviewPageSql.CK_HISTORICAL_SIMILARITY_MATCH, params));
        bundle.putRows("mainline_daily_snapshot", clickHouseQueryExecutor.queryForList(DailyReviewPageSql.CK_MAINLINE_DAILY_SNAPSHOT, params));
        bundle.putRows("sector_strength_snapshot", clickHouseQueryExecutor.queryForList(DailyReviewPageSql.CK_SECTOR_STRENGTH_SNAPSHOT, params));
        bundle.putRows("leader_daily_snapshot", clickHouseQueryExecutor.queryForList(DailyReviewPageSql.CK_LEADER_DAILY_SNAPSHOT, params));
        bundle.putRows("buy_pattern_signal_snapshot", clickHouseQueryExecutor.queryForList(DailyReviewPageSql.CK_BUY_PATTERN_SIGNAL_SNAPSHOT, params));
        bundle.putRows("risk_signal_snapshot", clickHouseQueryExecutor.queryForList(DailyReviewPageSql.CK_RISK_SIGNAL_SNAPSHOT, params));
        bundle.putRows("backtest_layer_stat", clickHouseQueryExecutor.queryForList(DailyReviewPageSql.CK_BACKTEST_LAYER_STAT, params));
        bundle.putRows("daily_review_record", mysqlQueryExecutor.queryForList(DailyReviewPageSql.MYSQL_DAILY_REVIEW_RECORD, params));
        bundle.putRows("daily_review_section", mysqlQueryExecutor.queryForList(DailyReviewPageSql.MYSQL_DAILY_REVIEW_SECTION, params));
        bundle.putRows("daily_review_checklist", mysqlQueryExecutor.queryForList(DailyReviewPageSql.MYSQL_DAILY_REVIEW_CHECKLIST, params));
        bundle.putRows("daily_review_audit_log", mysqlQueryExecutor.queryForList(DailyReviewPageSql.MYSQL_DAILY_REVIEW_AUDIT_LOG, params));
        bundle.putRows("manual_stage_adjustment", mysqlQueryExecutor.queryForList(DailyReviewPageSql.MYSQL_MANUAL_STAGE_ADJUSTMENT, params));
        bundle.putRows("cycle_sample_confirm", mysqlQueryExecutor.queryForList(DailyReviewPageSql.MYSQL_CYCLE_SAMPLE_CONFIRM, params));
        return bundle;
    }
}
