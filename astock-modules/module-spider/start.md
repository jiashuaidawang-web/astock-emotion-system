代理池预热
curl -s "http://127.0.0.1:8080/api/spider/ths/proxy/candidates"

同花顺
curl -i "http://127.0.0.1:8080/api/spider/ths/daily?tradeDate=2026-07-03"
东方财富
curl -i "http://127.0.0.1:8080/api/spider/dc/daily?tradeDate=2026-07-02"
看日志：
docker logs -f --tail=200 astock-backend
跑完验收：
curl -i "http://127.0.0.1:8080/api/spider/validate?tradeDate=2026-07-02"