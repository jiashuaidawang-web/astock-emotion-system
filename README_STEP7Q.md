# 第七步第十七段：全工程编译修复与启动闭环清算

生成日期：2026-06-29

## 本次完成内容

这次完成第七步最终清算：

```text
1. 全量Java源码静态编译校验
2. 接口签名清算
3. Getter / Setter 字段访问清算
4. 构造器注入一致性清算
5. Engine返回类型一致性清算
6. 启动说明补齐
7. MySQL索引初始化脚本补齐
8. ClickHouse建表排序键建议补齐
9. 编译清算报告补齐
```

## 静态编译结果

```text
Java文件数量：375
总文件数量：426
README数量：18
SQL脚本数量：12
```

本次沙盒环境没有 Maven，无法执行真实 `mvn clean package`。

已使用：

```text
JDK 21 javac + 外部依赖最小桩
```

完成源码层静态编译清算，结果为：

```text
javac --release 21 静态编译通过
```

## 新增文件

```text
README_RUN.md
README_COMPILE_REPORT_STEP7Q.md
README_STEP7Q.md
sql/17_mysql_init_indexes.sql
sql/18_clickhouse_init_order_and_indexes.sql
tools/static_javac_check.sh
```

## 正式启动顺序

```text
1. 启动 MySQL
2. 启动 ClickHouse
3. 执行 MySQL DDL
4. 执行 sql/17_mysql_init_indexes.sql
5. 执行 ClickHouse DDL
6. 执行 sql/18_clickhouse_init_order_and_indexes.sql 中的排序键建议核对
7. 启动 astock-app
8. 按 README_RUN.md 的 Engine 顺序跑数
9. 打开15个页面接口验证
10. 执行 AgentAuditExecutor 作为发布闸门
```

## 正式编译命令

```bash
mvn -U clean package -DskipTests
```

## 正式运行命令

```bash
java -jar astock-app/target/astock-app-1.0.0-SNAPSHOT.jar --spring.profiles.active=local
```

## 下一步建议

第八步建议进入：

```text
数据库DDL最终对齐：
把第七步所有输出契约 SQL 合并成完整 MySQL + ClickHouse 可执行建表DDL，
让 Engine 写入字段与真实表结构100%一致。
```
