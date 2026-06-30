#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
MYSQL_COUNT=$(grep -iE "create[[:space:]]+table[[:space:]]+if[[:space:]]+not[[:space:]]+exists" sql/init_mysql.sql | wc -l | tr -d ' ')
CK_COUNT=$(grep -iE "create[[:space:]]+table[[:space:]]+if[[:space:]]+not[[:space:]]+exists" sql/init_ck.sql | wc -l | tr -d ' ')
echo "[INFO] MySQL表数量：$MYSQL_COUNT"
echo "[INFO] ClickHouse表数量：$CK_COUNT"
[ "$MYSQL_COUNT" -ge 42 ] || { echo "[ERROR] MySQL表数量不足42"; exit 1; }
[ "$CK_COUNT" -ge 39 ] || { echo "[ERROR] ClickHouse表数量不足39"; exit 1; }
python tools/frontend_static_check.py
bash scripts/run-two-agents.sh
