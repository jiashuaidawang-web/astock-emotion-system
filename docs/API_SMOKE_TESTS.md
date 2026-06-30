# API 冒烟测试

生成日期：2026-06-29

## 一键跑批

```bash
bash scripts/run-daily-batch.sh 2026-06-30
```

## 15 页面接口

```bash
BASE=http://127.0.0.1:8080

curl -s "$BASE/api/dashboard/market?tradeDate=2026-06-30&marketScope=A_SHARE"
curl -s "$BASE/api/similarity/market?tradeDate=2026-06-30&marketScope=A_SHARE"
curl -s "$BASE/api/emotion-cycle/state-machine?tradeDate=2026-06-30&marketScope=A_SHARE"
curl -s "$BASE/api/cycle-samples/page?tradeDate=2026-06-30&marketScope=A_SHARE"
curl -s "$BASE/api/mainlines/radar?tradeDate=2026-06-30&marketScope=A_SHARE"
curl -s "$BASE/api/sectors/strength?tradeDate=2026-06-30&marketScope=A_SHARE"
curl -s "$BASE/api/leaders/ladder?tradeDate=2026-06-30&marketScope=A_SHARE"
curl -s "$BASE/api/leaders/000001/profile?tradeDate=2026-06-30&marketScope=A_SHARE"
curl -s "$BASE/api/patterns/conditions?tradeDate=2026-06-30&marketScope=A_SHARE"
curl -s "$BASE/api/risks/control?tradeDate=2026-06-30&marketScope=A_SHARE"
curl -s "$BASE/api/backtests/lab?tradeDate=2026-06-30&marketScope=A_SHARE"
curl -s "$BASE/api/backtests/reports/1?tradeDate=2026-06-30&marketScope=A_SHARE"
curl -s "$BASE/api/reviews/daily/workbench?tradeDate=2026-06-30&marketScope=A_SHARE"
curl -s "$BASE/api/rules/versions/page?pageNo=1&pageSize=20"
curl -s "$BASE/api/agent-audit/dashboard?tradeDate=2026-06-30&marketScope=A_SHARE"
```
