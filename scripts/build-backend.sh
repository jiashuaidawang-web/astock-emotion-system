#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mvn -U clean package -DskipTests
