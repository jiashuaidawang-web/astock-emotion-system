package com.astock.module.emotion.infrastructure.query;

import com.astock.common.data.PageSnapshotBundle;
import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.emotion.domain.repository.EmotionStateMachinePageRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class EmotionStateMachineMultiTableRepository implements EmotionStateMachinePageRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public EmotionStateMachineMultiTableRepository(ClickHouseQueryExecutor clickHouseQueryExecutor, MysqlQueryExecutor mysqlQueryExecutor) {
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
                bundle.putRows("emotion_stage_snapshot", clickHouseQueryExecutor.queryForList(EmotionStateMachinePageSql.CK_EMOTION_STAGE_SNAPSHOT, params));
        bundle.putRows("emotion_stage_score_detail", clickHouseQueryExecutor.queryForList(EmotionStateMachinePageSql.CK_EMOTION_STAGE_SCORE_DETAIL, params));
        bundle.putRows("stage_transition_snapshot", clickHouseQueryExecutor.queryForList(EmotionStateMachinePageSql.CK_STAGE_TRANSITION_SNAPSHOT, params));
        bundle.putRows("historical_similarity_match", clickHouseQueryExecutor.queryForList(EmotionStateMachinePageSql.CK_HISTORICAL_SIMILARITY_MATCH, params));
        bundle.putRows("historical_cycle_sample", clickHouseQueryExecutor.queryForList(EmotionStateMachinePageSql.CK_HISTORICAL_CYCLE_SAMPLE, params));
        bundle.putRows("historical_following_performance", clickHouseQueryExecutor.queryForList(EmotionStateMachinePageSql.CK_HISTORICAL_FOLLOWING_PERFORMANCE, params));
        bundle.putRows("manual_stage_adjustment", mysqlQueryExecutor.queryForList(EmotionStateMachinePageSql.MYSQL_MANUAL_STAGE_ADJUSTMENT, params));
        bundle.putRows("emotion_stage_rule_version", mysqlQueryExecutor.queryForList(EmotionStateMachinePageSql.MYSQL_EMOTION_STAGE_RULE_VERSION, params));
        return bundle;
    }
}
