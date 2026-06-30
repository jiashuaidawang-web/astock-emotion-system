package com.astock.module.risk.infrastructure.query;

import com.astock.infrastructure.clickhouse.ClickHouseQueryExecutor;
import com.astock.infrastructure.mysql.MysqlQueryExecutor;
import com.astock.module.risk.domain.model.RiskControlContext;
import com.astock.module.risk.domain.repository.RiskControlContextRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;

@Repository
public class RiskControlContextRepositoryImpl implements RiskControlContextRepository {
    private final ClickHouseQueryExecutor clickHouseQueryExecutor;
    private final MysqlQueryExecutor mysqlQueryExecutor;

    public RiskControlContextRepositoryImpl(ClickHouseQueryExecutor clickHouseQueryExecutor,
                                            MysqlQueryExecutor mysqlQueryExecutor) {
        this.clickHouseQueryExecutor = clickHouseQueryExecutor;
        this.mysqlQueryExecutor = mysqlQueryExecutor;
    }

    @Override
    public RiskControlContext load(LocalDate tradeDate, String marketScope) {
        Map<String, Object> params = Map.of(
                "tradeDate", tradeDate,
                "marketScope", marketScope == null ? "" : marketScope
        );

        RiskControlContext context = new RiskControlContext();
        context.setPatternSignalRows(clickHouseQueryExecutor.queryForList(RiskControlContextSql.SELECT_PATTERN_SIGNAL_ROWS, params));
        context.setDataQualityRows(mysqlQueryExecutor.queryForList(RiskControlContextSql.SELECT_DATA_QUALITY_ROWS, params));
        context.setRiskActionMatrixRows(mysqlQueryExecutor.queryForList(RiskControlContextSql.SELECT_RISK_ACTION_MATRIX, params));
        return context;
    }
}
