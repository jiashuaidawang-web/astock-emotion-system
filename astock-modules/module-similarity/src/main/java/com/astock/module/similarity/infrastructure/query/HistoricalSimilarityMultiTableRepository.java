package com.astock.module.similarity.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.similarity.domain.repository.HistoricalSimilarityPageRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class HistoricalSimilarityMultiTableRepository implements HistoricalSimilarityPageRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public HistoricalSimilarityMultiTableRepository(ClickHouseQueryExecutor clickHouseQueryExecutor, MysqlQueryExecutor mysqlQueryExecutor) {
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
                bundle.putRows("market_factor_snapshot", clickHouseQueryExecutor.queryForList(HistoricalSimilarityPageSql.CK_MARKET_FACTOR_SNAPSHOT, params));
        bundle.putRows("emotion_stage_snapshot", clickHouseQueryExecutor.queryForList(HistoricalSimilarityPageSql.CK_EMOTION_STAGE_SNAPSHOT, params));
        bundle.putRows("historical_similarity_match", clickHouseQueryExecutor.queryForList(HistoricalSimilarityPageSql.CK_HISTORICAL_SIMILARITY_MATCH, params));
        bundle.putRows("historical_similarity_factor_detail", clickHouseQueryExecutor.queryForList(HistoricalSimilarityPageSql.CK_HISTORICAL_SIMILARITY_FACTOR_DETAIL, params));
        bundle.putRows("historical_following_performance", clickHouseQueryExecutor.queryForList(HistoricalSimilarityPageSql.CK_HISTORICAL_FOLLOWING_PERFORMANCE, params));
        bundle.putRows("historical_cycle_sample", clickHouseQueryExecutor.queryForList(HistoricalSimilarityPageSql.CK_HISTORICAL_CYCLE_SAMPLE, params));
        bundle.putRows("similarity_rule_version", mysqlQueryExecutor.queryForList(HistoricalSimilarityPageSql.MYSQL_SIMILARITY_RULE_VERSION, params));
        bundle.putRows("cycle_sample_confirm", mysqlQueryExecutor.queryForList(HistoricalSimilarityPageSql.MYSQL_CYCLE_SAMPLE_CONFIRM, params));
        return bundle;
    }
}
