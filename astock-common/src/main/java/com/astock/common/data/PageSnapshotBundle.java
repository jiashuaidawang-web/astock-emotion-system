package com.astock.common.data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PageSnapshotBundle {
    private final Map<String, List<Map<String, Object>>> tableRows = new LinkedHashMap<>();

    public void putRows(String tableName, List<Map<String, Object>> rows) {
        tableRows.put(tableName, rows == null ? List.of() : rows);
    }

    public List<Map<String, Object>> rows(String tableName) {
        return tableRows.getOrDefault(tableName, List.of());
    }

    public Map<String, Object> firstRow(String tableName) {
        List<Map<String, Object>> rows = rows(tableName);
        return rows.isEmpty() ? Collections.emptyMap() : rows.get(0);
    }

    public boolean hasRows(String tableName) {
        return !rows(tableName).isEmpty();
    }

    public boolean isEmpty() {
        return tableRows.values().stream().allMatch(List::isEmpty);
    }

    public Map<String, List<Map<String, Object>>> asMap() {
        return Collections.unmodifiableMap(tableRows);
    }
}
