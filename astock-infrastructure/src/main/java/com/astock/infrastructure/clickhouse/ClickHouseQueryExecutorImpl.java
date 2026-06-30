package com.astock.infrastructure.clickhouse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class ClickHouseQueryExecutorImpl implements ClickHouseQueryExecutor {
    private final NamedParameterJdbcTemplate clickHouseJdbcTemplate;

    public ClickHouseQueryExecutorImpl(@Qualifier("clickHouseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate clickHouseJdbcTemplate) {
        this.clickHouseJdbcTemplate = clickHouseJdbcTemplate;
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Map<String, Object> params) {
        return clickHouseJdbcTemplate.queryForList(sql, params);
    }
}
