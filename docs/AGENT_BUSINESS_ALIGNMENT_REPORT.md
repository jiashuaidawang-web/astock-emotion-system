# BusinessAlignmentAgent 对齐检查报告

状态：PASSED

```json
{
  "mysql_table_count": 42,
  "clickhouse_table_count": 39,
  "wide_java_sql_ref_count": 81,
  "mapper_direct_ref_count": 6,
  "frontend_api_count": 16,
  "backend_method_mapping_count": 28,
  "route_page_count": 15,
  "page_file_count": 15
}
```

- [x] MySQL初始化表数量>=42
```json
{
  "mysql_table_count": 42
}
```

- [x] ClickHouse初始化表数量>=39
```json
{
  "clickhouse_table_count": 39
}
```

- [x] Java宽口径SQL引用表均在DDL
```json
{
  "wide_java_sql_ref_count": 81,
  "mapper_direct_ref_count": 6,
  "missing_tables": []
}
```

- [x] 前端API均有后端方法级映射
```json
{
  "frontend_api_count": 16,
  "backend_method_mapping_count": 28,
  "missing_backend_paths": []
}
```

- [x] 路由页面文件存在
```json
{
  "route_page_count": 15
}
```

- [x] 页面文件数量为15
```json
{
  "page_file_count": 15
}
```

- [x] 页面主展示不依赖JsonViewer
```json
{
  "json_pages": []
}
```
