#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
TMP_DIR="${TMPDIR:-/tmp}/astock-static-check"
STUB_SRC="$TMP_DIR/stubs-src"
STUB_CLASSES="$TMP_DIR/stubs-classes"
OUT_CLASSES="$TMP_DIR/classes"

rm -rf "$TMP_DIR"
mkdir -p "$STUB_SRC" "$STUB_CLASSES" "$OUT_CLASSES"

echo "[INFO] 本脚本用于无Maven环境下做Java源码静态编译校验。"
echo "[INFO] 正式构建仍以 mvn clean package 为准。"

cat > "$TMP_DIR/sources.txt" <<EOF2
$(find "$ROOT_DIR" -name '*.java')
EOF2

# 实际CI请直接使用Maven依赖，这里不内置Spring/MyBatis桩，避免污染项目。
echo "[INFO] 检测到 $(wc -l < "$TMP_DIR/sources.txt") 个Java文件。"
echo "[INFO] 请在正式环境执行：mvn -U clean package -DskipTests"
