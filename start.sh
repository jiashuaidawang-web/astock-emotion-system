#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-/opt/astock-emotion-system}"
CLICKHOUSE_USER="${CLICKHOUSE_USER:-default}"
CLICKHOUSE_PASSWORD="${CLICKHOUSE_PASSWORD:-pamirs@123}"
CLICKHOUSE_DATABASE="${CLICKHOUSE_DATABASE:-astock_analysis}"

cd "$APP_DIR"

echo "1) 检查 ClickHouse 固化配置文件"
test -f docker/clickhouse/users.d/01-default-user.xml
test -f docker/clickhouse/config.d/01-clickhouse-server.xml

echo "2) 启动 MySQL / ClickHouse"
docker compose -f docker-compose.yml up -d mysql clickhouse

echo "3) 等待 ClickHouse HTTP 8123"
until curl -sf http://127.0.0.1:8123/ping | grep -q Ok; do
  echo "等待 ClickHouse 8123..."
  sleep 2
done

echo "4) 验证 ClickHouse 新密码必须成功"
docker exec astock-clickhouse clickhouse-client \
  --user "$CLICKHOUSE_USER" \
  --password "$CLICKHOUSE_PASSWORD" \
  --query "SELECT currentUser(), version();"

echo "5) 验证 ClickHouse 空密码必须失败"
if docker exec astock-clickhouse clickhouse-client \
  --user "$CLICKHOUSE_USER" \
  --password "" \
  --query "SELECT 1;" >/dev/null 2>&1; then
  echo "错误：ClickHouse 空密码仍然可以登录，终止部署。"
  exit 1
fi

echo "6) 验证 ClickHouse HTTP 带密码查询"
curl -sf -u "$CLICKHOUSE_USER:$CLICKHOUSE_PASSWORD" "http://127.0.0.1:8123/?query=SELECT%201"

echo "7) 创建 ClickHouse 数据库"
docker exec astock-clickhouse clickhouse-client \
  --user "$CLICKHOUSE_USER" \
  --password "$CLICKHOUSE_PASSWORD" \
  --query "CREATE DATABASE IF NOT EXISTS ${CLICKHOUSE_DATABASE};"

echo "8) 构建并启动 backend / frontend"
docker compose -f docker-compose.yml build --no-cache backend frontend
docker compose -f docker-compose.yml up -d --force-recreate backend frontend

echo "9) 验证后端健康"
sleep 15
curl -i http://127.0.0.1:8080/health

echo "10) 验证后端接口"
curl -i "http://127.0.0.1:8080/api/dashboard/market?marketScope=A_SHARE"

echo "11) 验证前端 Nginx 代理"
curl -i "http://127.0.0.1:18080/api/dashboard/market?marketScope=A_SHARE"

echo "部署完成"