package com.astock.module.pattern.infrastructure.query;

import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.pattern.domain.model.PatternEngineSupportContext;
import com.astock.module.pattern.domain.repository.PatternEngineSupportContextRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class PatternEngineSupportContextRepositoryImpl implements PatternEngineSupportContextRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public PatternEngineSupportContextRepositoryImpl(ClickHouseQueryExecutor clickHouseQueryExecutor,
                                                     MysqlQueryExecutor mysqlQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
        this.mysqlQueryExecutor = mysqlQueryExecutor;
    }

    @Override
    public PatternEngineSupportContext load(LocalDate tradeDate, String marketScope) {
        Map<String, Object> params = Map.of(
                "tradeDate", tradeDate,
                "marketScope", marketScope == null ? "" : marketScope
        );
        PatternEngineSupportContext context = new PatternEngineSupportContext();
        context.setPatternBacktestRows(clickHouseQueryExecutor.queryForList(
                PatternEngineSupportContextSql.SELECT_PATTERN_BACKTEST_RESULT, params));
        context.setRiskBindingRows(mysqlQueryExecutor.queryForList(
                PatternEngineSupportContextSql.SELECT_RISK_BINDING, params));
        context.setStageMatrixRows(mysqlQueryExecutor.queryForList(
                PatternEngineSupportContextSql.SELECT_STAGE_MATRIX, params));
        context.setRuleConfigRows(mysqlQueryExecutor.queryForList(
                PatternEngineSupportContextSql.SELECT_RULE_CONFIG, params));
        return context;
    }
}
