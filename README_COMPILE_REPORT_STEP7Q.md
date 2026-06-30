# 第七步第十七段：全工程编译修复与启动闭环清算报告

生成日期：2026-06-29

## 1. 本次处理范围

本次对第七步前16段累计生成的后端工程进行全量清算：

```text
astock-common
astock-infrastructure
astock-api
astock-modules/*
astock-app
```

## 2. 静态编译验证

沙盒环境缺少 Maven：

```text
mvn: command not found
```

因此本次无法在沙盒内执行真实 Maven 生命周期：

```bash
mvn clean package
```

已执行替代校验：

```text
使用 JDK 21 javac
使用 Spring / MyBatis / JDBC 外部API最小桩
全量编译 375 个 Java 源文件
```

校验目标：

```text
1. Java语法错误
2. 类名与文件名一致性
3. 包路径一致性
4. 方法签名不一致
5. Getter / Setter 缺失
6. 构造器注入参数不一致
7. 接口实现方法缺失
8. Engine返回类型不一致
9. VO / Query / Result 字段访问错误
10. Java 21语法兼容性
```

校验结果：

```text
javac --release 21 静态编译通过。
```

## 3. 本次新增启动闭环文件

```text
README_RUN.md
README_COMPILE_REPORT_STEP7Q.md
sql/17_mysql_init_indexes.sql
sql/18_clickhouse_init_order_and_indexes.sql
```

## 4. 正式环境仍需执行

由于沙盒没有 Maven，正式仓库落地后必须执行：

```bash
mvn -U clean package -DskipTests
```

然后执行：

```bash
mvn -pl astock-app -am spring-boot:run
```

或：

```bash
java -jar astock-app/target/astock-app-1.0.0-SNAPSHOT.jar --spring.profiles.active=local
```

## 5. 仍需真实数据库验证的内容

以下内容需要连接真实 MySQL / ClickHouse 后验证：

```text
1. 目标表字段是否与输出契约完全一致
2. ClickHouse INSERT 字段类型是否匹配
3. MySQL Mapper SQL 是否与真实DDL一致
4. 数据完整性检查是否能命中快照表
5. Engine执行顺序是否满足数据依赖
6. AgentAuditExecutor扫描路径是否为真实工程根目录
```

## 6. 当前工程红线状态

```text
Controller 不承载算法
Aggregator 不返回 Mock
Engine 不输出交易建议
future_* 只允许历史展示与回测窗口
RiskControlEngine 作为 PatternConditionEngine 上级保护层
AgentAuditExecutor 作为发布闸门
```
