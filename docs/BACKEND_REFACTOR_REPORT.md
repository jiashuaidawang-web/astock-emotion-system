# astock-emotion-system 后端启动与 Mapper 层重构报告

## 1. 本次重构目标

本次重构围绕三条主线完成：

1. 彻底治理 Spring Bean / MyBatis Mapper 注册边界，解决普通业务接口被注册成 Bean 后导致的重复注入问题。
2. 将现有手写 MyBatis 注解 SQL Mapper 重构为 MyBatis-Plus `BaseMapper` + `Wrapper` 查询/更新语法。
3. 对 DTO / VO / Entity / Engine Context / Result 等数据载体引入 Lombok，并补充类、字段、核心方法注释。

## 2. 根因定位

连续出现的 Bean 冲突并不是单个构造函数缺少 `@Qualifier`，而是启动边界设计问题：

- MySQL 和 ClickHouse 都有数据源，但 MyBatis-Plus 没有显式绑定到 MySQL `SqlSessionFactory`。
- Mapper 分散在多个包里，依赖 MyBatis Boot 自动扫描，边界不够清晰。
- 部分 Mapper 使用手写 `@Select/@Insert/@Update`，SQL 字段与当前初始化脚本中的表结构已经出现漂移，例如 `rule_version.active` 与实际 `active_flag` 不一致。
- 当 MyBatis 扫描边界不清晰时，普通领域接口、Repository 抽象、Executor 抽象容易被误注册，进而和实现类形成两个候选 Bean。

## 3. 核心改造

### 3.1 MyBatis-Plus 显式绑定 MySQL

改造文件：

- `astock-infrastructure/src/main/java/com/astock/infrastructure/config/MysqlDataSourceConfig.java`
- `astock-infrastructure/src/main/java/com/astock/infrastructure/config/ClickHouseDataSourceConfig.java`

关键策略：

- MySQL 作为 `@Primary` 业务库。
- 新增 `mysqlSqlSessionFactory`。
- 新增 `mysqlSqlSessionTemplate`。
- `@MapperScan` 只扫描 `@Mapper` 注解接口，并显式绑定 MySQL 会话工厂。
- ClickHouse 保持 JDBC 查询边界，不参与 MyBatis-Plus Mapper 扫描。

### 3.2 Mapper 全部切换 MyBatis-Plus

以下 Mapper 已重构为 `BaseMapper<T>`：

- `EngineBatchRunLogMapper`
- `EngineBatchStepLogMapper`
- `AlgorithmTaskLogMapper`
- `DataQualityCheckLogMapper`
- `PageFieldLineageMapper`
- `RuleVersionMapper`
- `AgentAuditMysqlMapper`
- `RuleVersionMysqlMapper`

所有原有 `@Select/@Insert/@Update/@Delete` 注解 SQL 已清空。

### 3.3 新增实体

新增 MyBatis-Plus 实体：

- `EngineBatchRunLogEntity`
- `EngineBatchStepLogEntity`
- `AlgorithmTaskLogEntity`
- `DataQualityCheckLogEntity`
- `PageContractFieldLineageEntity`
- `RuleVersionEntity`

每个实体均补充：

- `@TableName`
- `@TableId(type = IdType.AUTO)`
- `@Data`
- 类注释
- 字段注释

### 3.4 查询/新增/修改语法重构

已改为 MyBatis-Plus 语法：

- 新增：`mapper.insert(entity)`
- 修改：`mapper.updateById(entity)`
- 条件查询：`Wrappers.lambdaQuery()` / `QueryWrapper`
- Map 查询：`mapper.selectMaps(queryWrapper)`
- 数量查询：`mapper.selectCount(lambdaQuery)`

### 3.5 Docker 后端 Jar 可执行问题修复

改造文件：

- `astock-app/pom.xml`

新增：

- `mainClass = com.astock.app.AstockApplication`
- `spring-boot-maven-plugin` 的 `repackage` goal

这样 Docker 中 `java -jar /app/astock-app.jar` 才能拿到 Spring Boot 可执行 Jar manifest。

### 3.6 Lombok 与参数名保留

改造文件：

- 根 `pom.xml`

新增：

- `lombok.version`
- Lombok provided 依赖
- compiler `parameters=true`
- Lombok annotation processor

大量 DTO / VO / Query / Engine Context / Result 已切换为 Lombok `@Data`，枚举元数据切换为 `@Getter`。

## 4. 设计不合理点同步修正

1. `RuleVersionMapper` 原先查询字段 `active`，但初始化脚本真实字段是 `active_flag`，已改成 MyBatis-Plus `RuleVersionEntity::getActiveFlag`。
2. `DataQualityCheckLogMapper` 原先查询 `data_domain/expected_count/actual_count/missing_count/check_text` 等字段，但表结构中不存在这些列，已改为基于 `data_quality_check_log` 实体转换 VO。
3. `AgentAuditClickHouseRepository` / `RuleVersionClickHouseRepository` 位于 mysql 包却叫 ClickHouse，已重命名为 `AgentAuditMysqlRepository` / `RuleVersionMysqlRepository`。
4. 批次日志 Mapper 原先一个 Mapper 管两张表，已拆成 `EngineBatchRunLogMapper` 与 `EngineBatchStepLogMapper`。
5. 不引入 LiteFlow：当前问题是 Bean/Mapper/DataSource 边界污染，不是编排框架缺失。应等启动边界稳定后，再考虑把 `EngineBatchOrchestrationService` 升级成 LiteFlow Chain。

## 5. 质量守卫

新增脚本：

```bash
bash scripts/quality-guard-backend-refactor.sh
```

检查项：

1. 禁止继续出现 MyBatis 手写 SQL 注解。
2. Mapper 必须继承 MyBatis-Plus `BaseMapper`。
3. 普通业务接口禁止误加 `@Mapper/@Component/@Service/@Repository`。
4. MySQL 数据源配置只能有一个权威来源。
5. MyBatis-Plus 必须显式绑定 MySQL `SqlSessionFactory`。
6. Spring Boot Jar 必须配置 `repackage`。
7. 业务输出禁用交易动作建议词；扫描器和数据库历史表名白名单排除。

## 6. 本地验证命令

Mac 本机 IDEA 启动前，建议在项目根目录执行：

```bash
find . -name target -type d -prune -exec rm -rf {} +
mvn clean package -DskipTests
bash scripts/quality-guard-backend-refactor.sh
```

IDEA 环境变量继续使用本机连接 Docker 数据库：

```text
SPRING_PROFILES_ACTIVE=local;MYSQL_HOST=127.0.0.1;MYSQL_PORT=3306;MYSQL_DATABASE=astock_business;MYSQL_USER=astock;MYSQL_PASSWORD=pamirs@123;CLICKHOUSE_HOST=127.0.0.1;CLICKHOUSE_HTTP_PORT=8123;CLICKHOUSE_DATABASE=astock_analysis;CLICKHOUSE_USER=default;CLICKHOUSE_PASSWORD=pamirs@123
```

## 7. 验证说明

当前执行环境没有 Maven 命令，因此本报告中的编译验证需要你在 Mac 本机执行 `mvn clean package -DskipTests`。本环境已完成静态质量守卫检查、Java 大括号平衡检查、Mapper SQL 注解清零检查和字段注释覆盖检查。
