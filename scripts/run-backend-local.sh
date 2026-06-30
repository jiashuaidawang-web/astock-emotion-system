#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

if ! command -v mvn >/dev/null 2>&1; then
  echo "[ERROR] 未找到 mvn，请先安装 Maven 3.9+"
  exit 1
fi

mvn -pl astock-app -am spring-boot:run -Dspring-boot.run.profiles=local
