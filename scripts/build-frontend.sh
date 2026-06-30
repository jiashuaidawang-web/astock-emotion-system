#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../astock-frontend"
npm install
npm run build
