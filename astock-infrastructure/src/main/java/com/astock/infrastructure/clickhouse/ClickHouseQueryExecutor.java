package com.astock.infrastructure.clickhouse;

import java.util.List;
import java.util.Map;

public interface ClickHouseQueryExecutor {
    List<Map<String, Object>> queryForList(String sql, Map<String, Object> params);
}
