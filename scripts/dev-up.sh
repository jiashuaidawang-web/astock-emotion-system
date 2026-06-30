#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

if [ ! -f .env ]; then
  cp .env.example .env
  echo "[INFO] 已生成 .env，请按需修改数据库密码和端口。"
fi

docker compose up -d mysql clickhouse
echo "[INFO] MySQL / ClickHouse 已启动。"
echo "[INFO] 接下来执行：bash scripts/init-mysql.sh && bash scripts/init-clickhouse.sh"
