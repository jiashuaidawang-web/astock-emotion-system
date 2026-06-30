# 前端运行手册

生成日期：2026-06-29

## 环境要求

```text
Node.js >= 20
npm >= 10
后端 Spring Boot 已启动
MySQL / ClickHouse 已初始化
```

## 启动

```bash
cd astock-frontend
npm install
npm run dev
```

默认访问：

```text
http://127.0.0.1:5173
```

## 后端地址

复制环境变量文件：

```bash
cp .env.example .env.local
```

配置：

```text
VITE_API_BASE_URL=http://127.0.0.1:8080
```

## 构建

```bash
npm run build
```

## 本次清算说明

当前沙盒环境有 Node/npm，但没有 node_modules，且不能联网拉取依赖，所以没有伪造 npm build 通过。

已执行：

```bash
python tools/frontend_static_check.py
```

正式环境必须执行：

```bash
cd astock-frontend
npm install
npm run build
```
