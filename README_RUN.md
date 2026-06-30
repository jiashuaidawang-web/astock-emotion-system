# A股情绪周期复盘与历史相似行情回测系统：运行闭环说明

生成日期：2026-06-29

## 1. 工程定位

本工程是基于 Java 21 + Spring Boot 3.x + MySQL + ClickHouse 的宏单体工程。

系统目标：

```text
A股情绪周期复盘
历史相似行情匹配
主线题材识别
龙头梯队识别
模式条件判定
风控上级保护
历史回测验证
Agent研发审计
```

系统不做荐股，不输出交易建议。

## 2. 环境要求

```text
JDK: 21
Maven: 3.9+
MySQL: 8.0+
ClickHouse: 23+
Spring Boot: 3.3.5
```

## 3. 启动前初始化顺序

### 第一步：初始化 MySQL 业务库

建议数据库名：

```sql
create database astock_business default character set utf8mb4 collate utf8mb4_unicode_ci;
```

执行顺序：

```text
1. 业务DDL脚本
2. sql/17_mysql_init_indexes.sql
3. 规则版本初始化数据
4. page_contract_field_lineage字段血缘初始化数据
```

### 第二步：初始化 ClickHouse 分析库

建议数据库名：

```sql
create database if not exists astock_analysis;
```

执行顺序：

```text
1. ClickHouse核心快照表DDL
2. sql/18_clickhouse_init_order_and_indexes.sql
3. 行情快照 / 情绪快照 / 回测快照等基础数据导入
```

### 第三步：修改 application.yml

配置 MySQL：

```yaml
spring:
  datasource:
    mysql:
      url: jdbc:mysql://127.0.0.1:3306/astock_business?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
      username: root
      password: your_password
```

配置 ClickHouse：

```yaml
clickhouse:
  datasource:
    url: jdbc:clickhouse://127.0.0.1:8123/astock_analysis
    username: default
    password:
```

## 4. 编译命令

正式环境执行：

```bash
mvn -U clean package -DskipTests
```

单独编译启动模块：

```bash
mvn -pl astock-app -am clean package -DskipTests
```

## 5. 启动命令

```bash
java --enable-preview \
  -jar astock-app/target/astock-app-1.0.0-SNAPSHOT.jar \
  --spring.profiles.active=local
```

如果不使用 preview，可直接：

```bash
java -jar astock-app/target/astock-app-1.0.0-SNAPSHOT.jar --spring.profiles.active=local
```

## 6. 推荐启动顺序

```text
1. 启动 MySQL
2. 启动 ClickHouse
3. 执行 MySQL 初始化脚本
4. 执行 ClickHouse 初始化脚本
5. 启动 astock-app
6. 调用数据完整性接口检查快照
7. 依次运行 Engine
8. 打开15个页面接口验证PageVO
```

## 7. Engine 执行顺序

建议按依赖顺序执行：

```text
1. EmotionStageRecognitionEngine
   POST /api/engines/emotion-stage/run

2. MainlineRecognitionEngine
   POST /api/engines/mainline/run

3. LeaderRecognitionEngine
   POST /api/engines/leader/run

4. RiskControlEngine
   POST /api/engines/risk/run

5. PatternConditionEngine
   POST /api/engines/pattern/run

6. RiskControlEngine 二次执行
   POST /api/engines/risk/run
   用于对 buy_pattern_signal_snapshot 做上级风控覆盖

7. SimilarityMatchEngine
   POST /api/engines/similarity/run

8. BacktestExecutor
   POST /api/engines/backtest/run

9. AgentAuditExecutor
   POST /api/engines/agent-audit/run
```

## 8. Engine 请求示例

```json
{
  "tradeDate": "2026-06-30",
  "marketScope": "A_SHARE",
  "ruleVersionId": 1,
  "dataCheckEnabled": true,
  "paramJson": "{}"
}
```

## 9. 15个页面接口

```text
GET /api/dashboard/market
GET /api/similarity/market
GET /api/emotion-cycle/state-machine
GET /api/cycle-samples/page
GET /api/mainlines/radar
GET /api/sectors/strength
GET /api/leaders/ladder
GET /api/leaders/{stockCode}/profile
GET /api/patterns/conditions
GET /api/risks/control
GET /api/backtests/lab
GET /api/backtests/reports/{reportId}
GET /api/reviews/daily/workbench
GET /api/rules/versions/page
GET /api/agent-audit/dashboard
```

## 10. 红线

```text
1. Controller 不返回 Mock。
2. Aggregator 不注水。
3. Converter 只做字段映射。
4. Engine 只输出研究信号、条件状态、风险状态、回测结果、审计结果。
5. 不输出买入、卖出、持有、推荐、目标价、加仓、清仓。
6. future_* 只能在历史展示和回测窗口读取。
7. RiskControlEngine 是 PatternConditionEngine 的上级保护层。
8. AgentAuditExecutor 是发布闸门。
```

## 11. 本次静态编译清算结果

本次沙盒环境没有 Maven 命令，因此没有执行真实 `mvn clean package`。

已执行：

```text
JDK 21 javac 静态编译校验
使用 Spring / MyBatis / JDBC 外部API最小桩，仅校验项目Java源码类型、方法签名、构造器注入、字段访问和语法闭环。
```

结果：

```text
375 个 Java 源文件通过 javac 静态编译校验。
```

正式仓库落地后仍需执行：

```bash
mvn -U clean package -DskipTests
```


## 第八步DDL最终对齐补充

新增完整DDL文件：

```text
sql/20_mysql_full_schema.sql
sql/21_clickhouse_full_schema.sql
sql/22_rule_version_seed.sql
```

推荐执行：

```bash
mysql -uroot -p -e "create database if not exists astock_business default character set utf8mb4 collate utf8mb4_unicode_ci;"
mysql -uroot -p astock_business < sql/20_mysql_full_schema.sql
mysql -uroot -p astock_business < sql/22_rule_version_seed.sql
clickhouse-client --multiquery < sql/21_clickhouse_full_schema.sql
```



## 第十步：一键跑批入口

新增接口：

```http
POST /api/engines/batch/daily/run
```

执行前补充批次日志表：

```bash
mysql -uroot -p astock_business < sql/23_mysql_engine_batch_schema.sql
```

推荐请求：

```json
{
  "tradeDate": "2026-06-30",
  "marketScope": "A_SHARE",
  "ruleVersionId": 1,
  "dataCheckEnabled": true,
  "continueOnFailure": false,
  "runBacktest": false,
  "runAgentAudit": true,
  "rerunRiskAfterPattern": true,
  "paramJson": "{}"
}
```



## 第十一步：前端真实API联调层

新增前端目录：

```text
astock-frontend
```

启动：

```bash
cd astock-frontend
npm install
npm run dev
```

默认代理：

```text
/api -> http://127.0.0.1:8080
```

前端一键跑批入口：

```text
页面顶部 EngineBatchPanel
POST /api/engines/batch/daily/run
```



## 第十二步：前端工业级可视化组件

新增：

```text
src/components/visual/KpiGrid.vue
src/components/visual/SmartTable.vue
src/components/visual/RankingPanel.vue
src/components/visual/EmotionStateMachine.vue
src/components/visual/ProgressPanel.vue
src/components/visual/BacktestStatsChart.vue
src/components/visual/GenericVisualPage.vue
src/utils/dataExtract.ts
```

15个页面已从 JSON Viewer 升级为 KPI、排行、表格、状态机、进度面板和回测统计图。



## 第十三步：前端15页面深度业务化

新增：

```text
src/components/business/*
```

15个页面已从 GenericVisualPage 升级为一页一业务组件，包括专属KPI、专属表格列、专属状态区块、专属排行榜和合规提示。



## 第十四步：前端联调可编译清算

新增：

```text
README_FRONTEND_RUN.md
docs/FRONTEND_BACKEND_INTEGRATION_CHECKLIST.md
tools/frontend_static_check.py
astock-frontend/src/env.d.ts
astock-frontend/.env.example
```

静态检查：

```bash
python tools/frontend_static_check.py
```

正式构建：

```bash
cd astock-frontend
npm install
npm run build
```



## 第十五步：全栈最终交付包清算

新增：

```text
README_FINAL_DELIVERY.md
docker-compose.yml
.env.example
Makefile
docker/backend/Dockerfile
docker/frontend/Dockerfile
docker/frontend/nginx.conf
scripts/*
docs/LOCAL_DEV_ENV.md
docs/FINAL_DELIVERY_STRUCTURE.md
docs/FINAL_RELEASE_CHECKLIST.md
docs/API_SMOKE_TESTS.md
docs/DELIVERY_NOTES.md
```

快速启动：

```bash
cp .env.example .env
make dev-up
make init-mysql
make init-clickhouse
make backend
make frontend
```

Docker全栈：

```bash
docker compose up -d mysql clickhouse
bash scripts/init-mysql.sh
bash scripts/init-clickhouse.sh
docker compose up -d backend frontend
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
