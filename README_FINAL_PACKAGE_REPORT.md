# 最终交付包清算报告

生成日期：2026-06-29

## 文件完整性检查

```text
状态：PASSED
缺失：[]
```

## 关键交付件

```text
README_FINAL_DELIVERY.md
docker-compose.yml
.env.example
Makefile
docker/*
scripts/*
docs/*
sql/*
astock-app
astock-modules
astock-frontend
```

## 本次沙盒限制

```text
未执行 Maven 编译
未执行 npm build
未执行 docker compose up
未连接真实 MySQL / ClickHouse
```

## 正式验收入口

```text
docs/FINAL_RELEASE_CHECKLIST.md
```


## V2 对齐修正

本包已修正旧版最终包 SQL 初始化入口问题。

必须使用：

```text
sql/init_mysql.sql  -> 42 张 MySQL 表
sql/init_ck.sql     -> 39 张 ClickHouse 表
```

初始化脚本已经改为：

```bash
bash scripts/init-mysql.sh
bash scripts/init-clickhouse.sh
```

两个脚本会做表数量硬校验，低于 42 / 39 会直接拒绝执行。

详见：

```text
docs/FINAL_V2_ALIGNMENT_CORRECTION_REPORT.md
```
