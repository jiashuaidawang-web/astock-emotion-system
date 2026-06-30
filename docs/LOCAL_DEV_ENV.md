# 本地开发环境说明

生成日期：2026-06-29

## 推荐版本

```text
JDK 21
Maven 3.9+
Node.js 20+
npm 10+
Docker 24+
Docker Compose v2
MySQL 8.0
ClickHouse 23.8+
```

## 一键启动基础设施

```bash
cp .env.example .env
make dev-up
make init-mysql
make init-clickhouse
```

## 后端本地运行

```bash
make backend
```

或：

```bash
mvn -pl astock-app -am spring-boot:run -Dspring-boot.run.profiles=local
```

## 前端本地运行

```bash
make frontend
```

或：

```bash
cd astock-frontend
npm install
npm run dev
```

## Docker 方式

```bash
docker compose up -d mysql clickhouse
bash scripts/init-mysql.sh
bash scripts/init-clickhouse.sh
docker compose up -d backend frontend
```

## 常用地址

```text
前端Vite:     http://127.0.0.1:5173
前端Nginx:    http://127.0.0.1:18080
后端API:      http://127.0.0.1:8080
MySQL:        127.0.0.1:3306
ClickHouse:   http://127.0.0.1:8123
```
