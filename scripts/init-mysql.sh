#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

set -a
[ -f .env ] && source .env
set +a

MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-astock_root}"
INIT_SQL="sql/init_mysql.sql"

if [ ! -f "$INIT_SQL" ]; then
  echo "[ERROR] 缺少 $INIT_SQL"
  exit 1
fi

TABLE_COUNT=$(grep -iE "create[[:space:]]+table[[:space:]]+if[[:space:]]+not[[:space:]]+exists" "$INIT_SQL" | wc -l | tr -d ' ')
echo "[INFO] $INIT_SQL 表数量：$TABLE_COUNT"

if [ "$TABLE_COUNT" -lt 42 ]; then
  echo "[ERROR] MySQL 初始化表数量小于42，疑似旧版 SQL，拒绝执行。"
  exit 1
fi

docker exec -i astock-mysql mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" < "$INIT_SQL"
echo "[INFO] MySQL 初始化完成。"
