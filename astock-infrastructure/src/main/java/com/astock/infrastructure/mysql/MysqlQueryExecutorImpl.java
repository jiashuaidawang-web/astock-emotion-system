package com.astock.infrastructure.mysql;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class MysqlQueryExecutorImpl implements MysqlQueryExecutor {
    private final NamedParameterJdbcTemplate mysqlJdbcTemplate;

    public MysqlQueryExecutorImpl(@Qualifier("mysqlNamedParameterJdbcTemplate") NamedParameterJdbcTemplate mysqlJdbcTemplate) {
        this.mysqlJdbcTemplate = mysqlJdbcTemplate;
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Map<String, Object> params) {
        return mysqlJdbcTemplate.queryForList(sql, params);
    }
}
