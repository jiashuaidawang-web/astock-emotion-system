# 最终交付包 V2 对齐修正报告

生成日期：2026-06-30

## 先纠错

你核对得对：上一版最终交付包的主初始化入口仍然是旧 SQL：

```text
sql/20_mysql_full_schema.sql + sql/23_mysql_engine_batch_schema.sql = 14 张 MySQL 表
sql/21_clickhouse_full_schema.sql = 30 张 ClickHouse 表
```

我后面虽然生成了 V2 的 `init_mysql.sql` / `init_ck.sql`，但上一版 final zip 没有把它们纳入工程主初始化路径，所以按最终包核对就是 14 / 30。

这不是你核错了，是上一版交付包确实不完整。

## 本次修正

本次 V2 修正版正式纳入：

```text
sql/init_mysql.sql = 42 张 MySQL 表
sql/init_ck.sql    = 39 张 ClickHouse 表
```

并修正：

```text
scripts/init-mysql.sh
scripts/init-clickhouse.sh
scripts/check-all.sh
scripts/run-two-agents.sh
tools/agent_business_alignment_check.py
tools/agent_discipline_supervisor.py
```

## 统计口径修正

Java SQL 引用以后拆成两个口径：

```text
Mapper直连物理表数量：只统计 *Mapper.java
宽口径 SQL 引用表数量：统计 *Mapper.java / *PageSql.java / *ContextSql.java / *ClickHouseSql.java
```

上一版说的 81 是宽口径，不是 Mapper 直连口径。

Controller 映射以后拆成两个口径：

```text
方法级业务接口映射：用于和前端 API 对齐
注解总数：class-level @RequestMapping + 方法级注解
```

你统计的 47 是注解总数；前端真正需要对齐的是方法级接口。

## 防偷懒机制

```text
1. BusinessAlignmentAgent 不通过，直接失败
2. MySQL < 42，直接失败
3. ClickHouse < 39，直接失败
4. 没有承认并修正 14/30 问题，直接失败
```
