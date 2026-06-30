#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

INIT_SQL="sql/init_ck.sql"

if [ ! -f "$INIT_SQL" ]; then
  echo "[ERROR] 缺少 $INIT_SQL"
  exit 1
fi

TABLE_COUNT=$(grep -iE "create[[:space:]]+table[[:space:]]+if[[:space:]]+not[[:space:]]+exists" "$INIT_SQL" | wc -l | tr -d ' ')
echo "[INFO] $INIT_SQL 表数量：$TABLE_COUNT"

if [ "$TABLE_COUNT" -lt 39 ]; then
  echo "[ERROR] ClickHouse 初始化表数量小于39，疑似旧版 SQL，拒绝执行。"
  exit 1
fi

docker exec -i astock-clickhouse clickhouse-client --multiquery < "$INIT_SQL"
echo "[INFO] ClickHouse 初始化完成。"
