package com.astock.module.emotion.infrastructure.query;

import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.emotion.domain.model.EmotionHistoricalContext;
import com.astock.module.emotion.domain.repository.EmotionHistoricalContextRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class EmotionHistoricalContextRepositoryImpl implements EmotionHistoricalContextRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public EmotionHistoricalContextRepositoryImpl(ClickHouseQueryExecutor clickHouseQueryExecutor,
                                                  MysqlQueryExecutor mysqlQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
        this.mysqlQueryExecutor = mysqlQueryExecutor;
    }

    @Override
    public EmotionHistoricalContext load(LocalDate tradeDate, String marketScope, int pathWindowDays) {
        Map<String, Object> params = Map.of(
                "tradeDate", tradeDate,
                "marketScope", marketScope == null ? "" : marketScope,
                "pathWindowDays", pathWindowDays
        );

        EmotionHistoricalContext context = new EmotionHistoricalContext();
        context.setHistoricalCycleSamples(clickHouseQueryExecutor.queryForList(
                EmotionHistoricalContextSql.SELECT_HISTORICAL_CYCLE_SAMPLE, params));
        context.setRecentStageTransitions(clickHouseQueryExecutor.queryForList(
                EmotionHistoricalContextSql.SELECT_RECENT_STAGE_TRANSITION, params));
        context.setCycleSampleConfirms(mysqlQueryExecutor.queryForList(
                EmotionHistoricalContextSql.SELECT_CYCLE_SAMPLE_CONFIRM, params));
        context.setManualStageAdjustments(mysqlQueryExecutor.queryForList(
                EmotionHistoricalContextSql.SELECT_MANUAL_STAGE_ADJUSTMENT, params));
        return context;
    }
}
