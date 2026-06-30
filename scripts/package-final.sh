#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

PACKAGE_NAME="astock-emotion-system-final-delivery-$(date +%Y%m%d%H%M%S).zip"
cd ..
zip -r "$PACKAGE_NAME" astock-emotion-system \
  -x "*/target/*" \
  -x "*/node_modules/*" \
  -x "*/dist/*" \
  -x "*/.git/*"

echo "[INFO] 已生成 $PACKAGE_NAME"
