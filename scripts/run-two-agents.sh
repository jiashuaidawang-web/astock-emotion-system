#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
PROJECT_ROOT="${PROJECT_ROOT:-$(pwd)}"
MYSQL_SQL="${MYSQL_SQL:-sql/init_mysql.sql}"
CK_SQL="${CK_SQL:-sql/init_ck.sql}"
OUT_DIR="${OUT_DIR:-docs}"

python tools/agent_business_alignment_check.py --project-root "$PROJECT_ROOT" --mysql-sql "$MYSQL_SQL" --ck-sql "$CK_SQL" --out-dir "$OUT_DIR"
python tools/agent_discipline_supervisor.py --project-root "$PROJECT_ROOT" --mysql-sql "$MYSQL_SQL" --ck-sql "$CK_SQL" --out-dir "$OUT_DIR"
echo "[OK] 两个Agent均已通过"
