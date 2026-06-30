#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../astock-frontend"

if ! command -v npm >/dev/null 2>&1; then
  echo "[ERROR] 未找到 npm，请先安装 Node.js 20+"
  exit 1
fi

if [ ! -d node_modules ]; then
  npm install
fi

npm run dev
