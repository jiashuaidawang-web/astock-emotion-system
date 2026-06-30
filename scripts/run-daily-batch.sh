#!/usr/bin/env bash
set -euo pipefail

TRADE_DATE="${1:-$(date +%F)}"
BACKEND_URL="${BACKEND_URL:-http://127.0.0.1:8080}"

curl -sS -X POST "${BACKEND_URL}/api/engines/batch/daily/run" \
  -H "Content-Type: application/json" \
  -d "{
    \"tradeDate\": \"${TRADE_DATE}\",
    \"marketScope\": \"A_SHARE\",
    \"ruleVersionId\": 1,
    \"dataCheckEnabled\": true,
    \"continueOnFailure\": false,
    \"runBacktest\": false,
    \"runAgentAudit\": true,
    \"rerunRiskAfterPattern\": true,
    \"paramJson\": \"{}\"
  }"
echo
