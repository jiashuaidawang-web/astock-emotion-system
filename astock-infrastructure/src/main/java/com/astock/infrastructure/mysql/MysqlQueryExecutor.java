package com.astock.infrastructure.mysql;

import java.util.List;
import java.util.Map;

public interface MysqlQueryExecutor {
    List<Map<String, Object>> queryForList(String sql, Map<String, Object> params);
}
