# A股情绪周期系统数据库表结构评审与字段字典

生成时间：2026-07-01 22:50:49

## 0. 阅读说明

这份文档按“评审表结构”的方式解释当前 V2 初始化脚本里的所有 MySQL 与 ClickHouse 表。重点说明：每张表做什么、存什么维度、数据怎么写入、关键关联字段是什么、每个字段的含义是什么。

> 注意：当前系统处于工程落地和联调阶段，部分表是为了支撑页面契约、Engine输出、Agent审计和未来Python回测预留。文档中的“数据写入来源”是按照当前工程设计和表名语义梳理的落地路径，后续真实采集模块完成后需要继续校准。

## 1. 总体数据分层

| 层级 | 数据库 | 作用 | 典型表 |
|---|---|---|---|
| 业务配置层 | MySQL | 存规则、任务、配置、复盘、发布检查、审计任务 | rule_version、backtest_task、daily_review_record、agent_audit_task |
| 基础事实层 | ClickHouse | 存日K、市场因子、涨跌停生态等输入快照 | stock_daily_kline、market_factor_snapshot、limit_up_down_ecology_snapshot |
| Engine输出层 | ClickHouse | 存情绪、主线、龙头、模式条件、风控、相似匹配、回测结果 | emotion_stage_snapshot、mainline_daily_snapshot、leader_daily_snapshot、risk_signal_snapshot |
| 页面服务层 | Java + MySQL/CK | PageDataAggregator 读取两库并组装 PageVO | 依赖 page_code、trade_date、market_scope 等字段 |
| 审计治理层 | MySQL + ClickHouse | 存字段血缘、代码扫描、发布闸门、质量检查 | page_contract_field_lineage、agent_audit_* |

## 2. 核心关联字段

| 字段 | 关联含义 |
|---|---|
| `trade_date` | 核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | 市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `stock_code` | 股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | 股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `sector_code` | 板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | 板块名称。展示字段，真实关联优先使用 sector_code。 |
| `theme_code` | 题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | 题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | 主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | 主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `rule_code` | 规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_version_id` | 规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | 规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `task_id` | 任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `audit_task_id` | Agent审计任务ID。关联 Agent 审计任务、扫描明细、规则命中、发布闸门结果。 |
| `report_id` | 报告ID。关联回测报告、回测明细、分层统计、失败样本等。 |
| `page_code` | 页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `features` | 扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | 创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | 更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | 逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

## 3. 表级分组总览

### MySQL 表分组
- **规则配置与版本治理**：12 张
  - `rule_definition`：规则定义表
  - `rule_version`：规则版本表
  - `rule_version_audit_log`：规则版本审计日志表
  - `buy_pattern_rule_config`：买点模式规则配置表
  - `agent_audit_rule_version`：Agent审计规则版本业务表
  - `emotion_stage_rule_version`：情绪阶段规则版本展示表
  - `leader_rule_version`：龙头规则版本展示表
  - `mainline_rule_version`：主线规则版本展示表
  - `risk_rule_version`：风控规则版本展示表
  - `rule_publish_check_log`：规则发布检查日志表
  - `sector_rule_version`：板块规则版本展示表
  - `similarity_rule_version`：相似度规则版本展示表
- **任务调度与批次日志**：4 张
  - `algorithm_task_log`：算法任务执行日志表
  - `engine_batch_run_log`：Engine一键跑批批次日志表
  - `engine_batch_step_log`：Engine一键跑批步骤日志表
  - `cycle_mining_task`：周期样本挖掘任务表
- **数据质量与字段血缘**：2 张
  - `data_quality_check_log`：数据完整性检查日志表
  - `page_contract_field_lineage`：页面字段血缘契约表
- **情绪周期与历史样本**：1 张
  - `cycle_sample_confirm`：历史周期样本人工确认表
- **其他业务配置**：1 张
  - `manual_stage_adjustment`：情绪阶段人工修正表
- **模式条件与风控配置**：4 张
  - `pattern_risk_binding`：模式与风险动作绑定表
  - `buy_pattern_stage_matrix`：买点模式与情绪阶段矩阵表
  - `risk_action_matrix`：风控动作矩阵表
  - `buy_pattern_definition`：买点模式定义表
- **Agent审计与发布闸门**：4 张
  - `agent_audit_issue`：Agent审计问题业务表
  - `agent_audit_result`：Agent审计结果业务表
  - `agent_audit_task`：Agent审计任务表
  - `agent_release_gate_check`：Agent发布闸门检查业务表
- **回测任务与报告**：6 张
  - `backtest_preset_template`：回测预设模板表
  - `backtest_report`：回测报告业务表
  - `backtest_rule_binding`：回测规则绑定表
  - `backtest_task`：回测任务表
  - `backtest_task_param`：回测任务参数表
  - `backtest_task_progress`：回测任务进度表
- **每日复盘工作台**：4 张
  - `daily_review_audit_log`：每日复盘审计日志表
  - `daily_review_checklist`：每日复盘检查清单表
  - `daily_review_record`：每日复盘记录表
  - `daily_review_section`：每日复盘章节表
- **龙头规则与人工确认**：2 张
  - `leader_manual_confirm`：龙头人工确认表
  - `leader_type_definition`：龙头类型定义表
- **题材、板块、主线配置**：2 张
  - `theme_definition`：题材定义表
  - `theme_stock_mapping`：题材股票映射表

### ClickHouse 表分组
- **基础行情与市场生态输入**：3 张
  - `astock_analysis.stock_daily_kline`：股票日K线行情快照表
  - `astock_analysis.market_factor_snapshot`：市场因子快照表
  - `astock_analysis.limit_up_down_ecology_snapshot`：涨跌停生态快照表
- **情绪周期与历史样本**：6 张
  - `astock_analysis.emotion_stage_snapshot`：情绪阶段快照表
  - `astock_analysis.emotion_stage_score_detail`：情绪阶段评分明细表
  - `astock_analysis.stage_transition_snapshot`：情绪阶段转移快照表
  - `astock_analysis.historical_cycle_sample`：历史周期样本库表
  - `astock_analysis.historical_cycle_sample_factor`：历史周期样本因子明细表
  - `astock_analysis.historical_following_performance`：历史样本后续表现表
- **历史相似行情匹配**：5 张
  - `astock_analysis.historical_similarity_match`：历史相似行情匹配结果表
  - `astock_analysis.historical_similarity_factor_detail`：历史相似行情因子明细表
  - `astock_analysis.leader_similarity_match`：龙头相似匹配表
  - `astock_analysis.mainline_similarity_match`：主线相似匹配表
  - `astock_analysis.risk_similarity_match`：风险相似匹配表
- **题材、板块、主线分析**：7 张
  - `astock_analysis.theme_daily_snapshot`：题材日快照表
  - `astock_analysis.theme_strength_snapshot`：题材强度快照表
  - `astock_analysis.mainline_daily_snapshot`：主线日快照表
  - `astock_analysis.mainline_switch_snapshot`：主线切换快照表
  - `astock_analysis.sector_strength_snapshot`：板块强度快照表
  - `astock_analysis.sector_daily_snapshot`：板块日快照表
  - `astock_analysis.sector_stock_mapping_snapshot`：板块股票映射快照表
- **龙头梯队与反馈**：5 张
  - `astock_analysis.leader_daily_snapshot`：龙头日快照表
  - `astock_analysis.leader_ladder_snapshot`：龙头梯队快照表
  - `astock_analysis.leader_drive_snapshot`：龙头带动快照表
  - `astock_analysis.leader_negative_feedback`：龙头负反馈快照表
  - `astock_analysis.trend_leader_snapshot`：趋势龙头快照表
- **模式条件与回测结果**：3 张
  - `astock_analysis.buy_pattern_signal_snapshot`：买点条件信号快照表
  - `astock_analysis.pattern_risk_veto_snapshot`：模式风险否决快照表
  - `astock_analysis.pattern_backtest_result`：模式回测结果表
- **风控信号**：2 张
  - `astock_analysis.risk_signal_snapshot`：风控信号快照表
  - `astock_analysis.risk_signal_detail`：风控信号明细表
- **回测明细与统计**：4 张
  - `astock_analysis.backtest_signal_detail`：回测信号明细表
  - `astock_analysis.backtest_performance_detail`：回测绩效明细表
  - `astock_analysis.backtest_layer_stat`：回测分层统计表
  - `astock_analysis.backtest_failure_case`：回测失败样本表
- **Agent审计明细**：4 张
  - `astock_analysis.agent_audit_code_scan_detail`：Agent代码扫描明细表
  - `astock_analysis.agent_audit_data_lineage_detail`：Agent字段血缘审计明细表
  - `astock_analysis.agent_audit_rule_hit_detail`：Agent审计规则命中明细表
  - `astock_analysis.agent_audit_release_gate_detail`：Agent发布闸门检查明细表

# MySQL 详细表结构

## 1. `rule_definition`

- **表注释**：规则定义表
- **所属分组**：规则配置与版本治理
- **表用途**：规则定义表。
- **数据粒度**：业务对象/配置记录级，一行通常表示一个规则、任务、报告或配置对象。
- **数据写入来源**：主要由规则管理页面、配置导入、发布流程或Agent发布检查写入。
- **关键关联字段**：`rule_code`, `features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `rule_code` | `varchar(128) not null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) not null` | 规则名称 | 规则名称 |
| `rule_category` | `varchar(64) not null` | 规则分类 | 规则分类 |
| `rule_desc` | `varchar(1000) null` | 规则说明 | 规则说明 |
| `rule_owner` | `varchar(128) null` | 规则负责人 | 规则负责人 |
| `rule_status` | `varchar(32) not null default 'ENABLED'` | 规则状态 | 规则状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。

## 2. `rule_version`

- **表注释**：规则版本表
- **所属分组**：规则配置与版本治理
- **表用途**：规则版本表。
- **数据粒度**：业务对象/配置记录级，一行通常表示一个规则、任务、报告或配置对象。
- **数据写入来源**：主要由规则管理页面、配置导入、发布流程或Agent发布检查写入。
- **关键关联字段**：`rule_code`, `version_no`, `features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 规则版本ID | 规则版本ID |
| `rule_code` | `varchar(128) not null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `version_no` | `varchar(64) not null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_name` | `varchar(255) null` | 版本名称 | 版本名称 |
| `version_status` | `varchar(32) not null default 'DRAFT'` | 版本状态：DRAFT/ACTIVE/ARCHIVED | 版本状态：DRAFT/ACTIVE/ARCHIVED。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `active_flag` | `tinyint not null default 0` | 是否启用版本 | 是否启用版本 |
| `rule_content_json` | `json null` | 规则内容JSON | 规则内容JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_schema_json` | `json null` | 参数结构JSON | 参数结构JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `publish_check_json` | `json null` | 发布检查JSON | 发布检查JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。

## 3. `rule_version_audit_log`

- **表注释**：规则版本审计日志表
- **所属分组**：规则配置与版本治理
- **表用途**：规则版本审计日志表。
- **数据粒度**：业务对象/配置记录级，一行通常表示一个规则、任务、报告或配置对象。
- **数据写入来源**：主要由规则管理页面、配置导入、发布流程或Agent发布检查写入。
- **关键关联字段**：`rule_code`, `rule_version_id`, `features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `rule_code` | `varchar(128) not null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_version_id` | `bigint not null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `operation_type` | `varchar(64) not null` | 操作类型 | 操作类型 |
| `before_json` | `json null` | 变更前JSON | 变更前JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `after_json` | `json null` | 变更后JSON | 变更后JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `operator_name` | `varchar(128) null` | 操作人 | 操作人 |
| `audit_result` | `varchar(32) null` | 审计结果 | 审计结果 |
| `audit_message` | `varchar(1000) null` | 审计信息 | 审计信息 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。

## 4. `algorithm_task_log`

- **表注释**：算法任务执行日志表
- **所属分组**：任务调度与批次日志
- **表用途**：算法任务执行日志表。
- **数据粒度**：交易日相关业务记录级，一行通常表示某任务/规则/复盘在某交易日的记录。
- **数据写入来源**：由Java一键跑批编排服务写入，用于记录Engine执行批次和步骤。
- **关键关联字段**：`trade_date`, `features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 算法任务ID | 算法任务ID |
| `task_name` | `varchar(255) not null` | 任务名称 | 任务名称 |
| `task_type` | `varchar(128) not null` | 任务类型 | 任务类型 |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `task_status` | `varchar(64) not null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `started_at` | `datetime null` | 开始时间 | 开始时间。时间戳字段，用于审计、排序、排障和增量处理。 |
| `finished_at` | `datetime null` | 结束时间 | 结束时间。时间戳字段，用于审计、排序、排障和增量处理。 |
| `cost_millis` | `bigint null` | 耗时毫秒 | 耗时毫秒 |
| `input_json` | `json null` | 输入参数JSON | 输入参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `output_json` | `json null` | 输出JSON | 输出JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `failure_reason` | `text null` | 失败原因 | 失败原因 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。

## 5. `data_quality_check_log`

- **表注释**：数据完整性检查日志表
- **所属分组**：数据质量与字段血缘
- **表用途**：数据完整性检查日志表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由数据质量检查服务、页面契约检查或Agent审计写入。
- **关键关联字段**：`page_code`, `snapshot_code`, `trade_date`, `market_scope`, `features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `page_code` | `varchar(128) null` | 页面编码或任务编码 | 页面编码或任务编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `snapshot_code` | `varchar(128) null` | 快照编码 | 快照编码 |
| `snapshot_table` | `varchar(128) not null` | 快照表名 | 快照表名 |
| `trade_date` | `date not null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `data_complete` | `tinyint not null default 0` | 数据是否完整 | 数据是否完整 |
| `completeness_ratio` | `decimal(18,6) null` | 完整率 | 完整率 |
| `critical` | `tinyint not null default 0` | 是否关键数据 | 是否关键数据 |
| `missing_reason` | `varchar(1000) null` | 缺失原因 | 缺失原因 |
| `check_status` | `varchar(64) not null default 'CHECKED'` | 检查状态 | 检查状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。

## 6. `page_contract_field_lineage`

- **表注释**：页面字段血缘契约表
- **所属分组**：数据质量与字段血缘
- **表用途**：页面字段血缘契约表。
- **数据粒度**：业务对象/配置记录级，一行通常表示一个规则、任务、报告或配置对象。
- **数据写入来源**：由数据质量检查服务、页面契约检查或Agent审计写入。
- **关键关联字段**：`page_code`, `features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `page_code` | `varchar(128) not null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `vo_class_name` | `varchar(255) not null` | VO类名 | VO类名 |
| `field_name` | `varchar(255) not null` | 字段名 | 字段名 |
| `source_type` | `varchar(64) not null` | 来源类型：MYSQL/CLICKHOUSE/FORMULA/CONSTANT | 来源类型：MYSQL/CLICKHOUSE/FORMULA/CONSTANT |
| `source_table` | `varchar(128) null` | 来源表 | 来源表 |
| `source_column` | `varchar(128) null` | 来源列 | 来源列 |
| `calculation_formula` | `varchar(2000) null` | 计算公式 | 计算公式 |
| `required` | `tinyint not null default 0` | 是否必填 | 是否必填 |
| `audit_passed` | `tinyint not null default 1` | 审计是否通过 | 审计是否通过 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。

## 7. `cycle_sample_confirm`

- **表注释**：历史周期样本人工确认表
- **所属分组**：情绪周期与历史样本
- **表用途**：历史周期样本人工确认表。
- **数据粒度**：业务对象/配置记录级，一行通常表示一个规则、任务、报告或配置对象。
- **数据写入来源**：由后端业务服务、配置页面、批处理任务或人工维护写入。
- **关键关联字段**：`sample_id`, `cycle_sample_id`, `stage_code`, `features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `sample_id` | `bigint not null` | 历史样本ID | 历史样本ID |
| `cycle_sample_id` | `bigint null` | 周期样本ID | 周期样本ID |
| `confirmed_stage` | `varchar(64) null` | 人工确认阶段 | 人工确认阶段 |
| `stage_code` | `varchar(64) null` | 阶段编码 | 阶段编码 |
| `confirm_status` | `varchar(64) not null default 'CONFIRMED'` | 确认状态 | 确认状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `confirm_user` | `varchar(128) null` | 确认人 | 确认人 |
| `confirm_reason` | `varchar(1000) null` | 确认原因 | 确认原因 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。

## 8. `manual_stage_adjustment`

- **表注释**：情绪阶段人工修正表
- **所属分组**：其他业务配置
- **表用途**：情绪阶段人工修正表。
- **数据粒度**：交易日相关业务记录级，一行通常表示某任务/规则/复盘在某交易日的记录。
- **数据写入来源**：由后端业务服务、配置页面、批处理任务或人工维护写入。
- **关键关联字段**：`trade_date`, `market_scope`, `stage_code`, `features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date not null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `manual_stage` | `varchar(64) null` | 人工阶段 | 人工阶段 |
| `adjusted_stage` | `varchar(64) null` | 修正后阶段 | 修正后阶段 |
| `stage_code` | `varchar(64) null` | 阶段编码 | 阶段编码 |
| `adjust_reason` | `varchar(1000) null` | 修正原因 | 修正原因 |
| `operator_name` | `varchar(128) null` | 操作人 | 操作人 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。

## 9. `pattern_risk_binding`

- **表注释**：模式与风险动作绑定表
- **所属分组**：模式条件与风控配置
- **表用途**：模式与风险动作绑定表。
- **数据粒度**：业务对象/配置记录级，一行通常表示一个规则、任务、报告或配置对象。
- **数据写入来源**：由后端业务服务、配置页面、批处理任务或人工维护写入。
- **关键关联字段**：`pattern_code`, `risk_code`, `features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `pattern_code` | `varchar(128) not null` | 模式编码 | 模式编码 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_action` | `varchar(64) not null` | 风险动作 | 风险动作 |
| `binding_status` | `varchar(64) not null default 'ENABLED'` | 绑定状态 | 绑定状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。

## 10. `buy_pattern_stage_matrix`

- **表注释**：买点模式与情绪阶段矩阵表
- **所属分组**：模式条件与风控配置
- **表用途**：买点模式与情绪阶段矩阵表。
- **数据粒度**：业务对象/配置记录级，一行通常表示一个规则、任务、报告或配置对象。
- **数据写入来源**：由后端业务服务、配置页面、批处理任务或人工维护写入。
- **关键关联字段**：`pattern_code`, `stage_code`, `features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `pattern_code` | `varchar(128) not null` | 模式编码 | 模式编码 |
| `stage_code` | `varchar(64) not null` | 情绪阶段编码 | 情绪阶段编码 |
| `matrix_score` | `decimal(18,6) null` | 矩阵分 | 矩阵分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `matrix_status` | `varchar(64) not null default 'ENABLED'` | 矩阵状态 | 矩阵状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。

## 11. `buy_pattern_rule_config`

- **表注释**：买点模式规则配置表
- **所属分组**：规则配置与版本治理
- **表用途**：买点模式规则配置表。
- **数据粒度**：业务对象/配置记录级，一行通常表示一个规则、任务、报告或配置对象。
- **数据写入来源**：主要由规则管理页面、配置导入、发布流程或Agent发布检查写入。
- **关键关联字段**：`pattern_code`, `rule_code`, `features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `pattern_code` | `varchar(128) not null` | 模式编码 | 模式编码 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_status` | `varchar(64) not null default 'ENABLED'` | 规则状态 | 规则状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。

## 12. `risk_action_matrix`

- **表注释**：风控动作矩阵表
- **所属分组**：模式条件与风控配置
- **表用途**：风控动作矩阵表。
- **数据粒度**：业务对象/配置记录级，一行通常表示一个规则、任务、报告或配置对象。
- **数据写入来源**：由后端业务服务、配置页面、批处理任务或人工维护写入。
- **关键关联字段**：`features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `risk_level` | `varchar(64) not null` | 风险等级 | 风险等级 |
| `signal_level` | `varchar(64) null` | 信号等级 | 信号等级 |
| `risk_action` | `varchar(64) not null` | 风险动作 | 风险动作 |
| `action_priority` | `int not null default 0` | 动作优先级 | 动作优先级 |
| `description` | `varchar(1000) null` | 说明 | 说明 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。

## 13. `engine_batch_run_log`

- **表注释**：Engine一键跑批批次日志表
- **所属分组**：任务调度与批次日志
- **表用途**：Engine一键跑批批次日志表。
- **数据粒度**：交易日相关业务记录级，一行通常表示某任务/规则/复盘在某交易日的记录。
- **数据写入来源**：由Java一键跑批编排服务写入，用于记录Engine执行批次和步骤。
- **关键关联字段**：`trade_date`, `market_scope`, `features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 批次ID | 批次ID |
| `trade_date` | `date not null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `batch_status` | `varchar(64) not null` | 批次状态：RUNNING/SUCCESS/FAILED | 批次状态：RUNNING/SUCCESS/FAILED。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `started_at` | `datetime not null` | 开始时间 | 开始时间。时间戳字段，用于审计、排序、排障和增量处理。 |
| `finished_at` | `datetime null` | 结束时间 | 结束时间。时间戳字段，用于审计、排序、排障和增量处理。 |
| `cost_millis` | `bigint null` | 耗时毫秒 | 耗时毫秒 |
| `total_step_count` | `int null` | 总步骤数 | 总步骤数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `success_step_count` | `int null` | 成功步骤数 | 成功步骤数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `failed_step_count` | `int null` | 失败步骤数 | 失败步骤数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `request_json` | `json null` | 请求JSON | 请求JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `failure_reason` | `text null` | 失败原因 | 失败原因 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。

## 14. `engine_batch_step_log`

- **表注释**：Engine一键跑批步骤日志表
- **所属分组**：任务调度与批次日志
- **表用途**：Engine一键跑批步骤日志表。
- **数据粒度**：业务对象/配置记录级，一行通常表示一个规则、任务、报告或配置对象。
- **数据写入来源**：由Java一键跑批编排服务写入，用于记录Engine执行批次和步骤。
- **关键关联字段**：`batch_id`, `step_code`, `task_id`, `features`, `created_at`, `updated_at`, `is_deleted`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 步骤日志ID | 步骤日志ID |
| `batch_id` | `bigint not null` | 批次ID | 批次ID |
| `step_no` | `int not null` | 步骤序号 | 步骤序号 |
| `step_code` | `varchar(128) not null` | 步骤编码 | 步骤编码 |
| `step_name` | `varchar(255) not null` | 步骤名称 | 步骤名称 |
| `engine_name` | `varchar(255) not null` | 引擎名称 | 引擎名称 |
| `step_status` | `varchar(64) not null` | 步骤状态：SUCCESS/FAILED | 步骤状态：SUCCESS/FAILED。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `task_id` | `bigint null` | 底层算法任务ID | 底层算法任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `output_row_count` | `int null` | 输出行数 | 输出行数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `output_tables` | `varchar(1000) null` | 输出表列表 | 输出表列表 |
| `failure_reason` | `text null` | 失败原因 | 失败原因 |
| `summary_text` | `text null` | 摘要 | 摘要 |
| `started_at` | `datetime not null` | 开始时间 | 开始时间。时间戳字段，用于审计、排序、排障和增量处理。 |
| `finished_at` | `datetime null` | 结束时间 | 结束时间。时间戳字段，用于审计、排序、排障和增量处理。 |
| `cost_millis` | `bigint null` | 耗时毫秒 | 耗时毫秒 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 15. `agent_audit_issue`

- **表注释**：Agent审计问题业务表
- **所属分组**：Agent审计与发布闸门
- **表用途**：Agent审计问题业务表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由AgentAuditExecutor或发布闸门检查流程写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 16. `agent_audit_result`

- **表注释**：Agent审计结果业务表
- **所属分组**：Agent审计与发布闸门
- **表用途**：Agent审计结果业务表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由AgentAuditExecutor或发布闸门检查流程写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 17. `agent_audit_rule_version`

- **表注释**：Agent审计规则版本业务表
- **所属分组**：规则配置与版本治理
- **表用途**：Agent审计规则版本业务表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：主要由规则管理页面、配置导入、发布流程或Agent发布检查写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 18. `agent_audit_task`

- **表注释**：Agent审计任务表
- **所属分组**：Agent审计与发布闸门
- **表用途**：Agent审计任务表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由AgentAuditExecutor或发布闸门检查流程写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 19. `agent_release_gate_check`

- **表注释**：Agent发布闸门检查业务表
- **所属分组**：Agent审计与发布闸门
- **表用途**：Agent发布闸门检查业务表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由AgentAuditExecutor或发布闸门检查流程写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 20. `backtest_preset_template`

- **表注释**：回测预设模板表
- **所属分组**：回测任务与报告
- **表用途**：回测预设模板表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由回测任务创建、回测执行器、Python Runner或Java BacktestExecutor写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 21. `backtest_report`

- **表注释**：回测报告业务表
- **所属分组**：回测任务与报告
- **表用途**：回测报告业务表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由回测任务创建、回测执行器、Python Runner或Java BacktestExecutor写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 22. `backtest_rule_binding`

- **表注释**：回测规则绑定表
- **所属分组**：回测任务与报告
- **表用途**：回测规则绑定表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：主要由规则管理页面、配置导入、发布流程或Agent发布检查写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 23. `backtest_task`

- **表注释**：回测任务表
- **所属分组**：回测任务与报告
- **表用途**：回测任务表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由回测任务创建、回测执行器、Python Runner或Java BacktestExecutor写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 24. `backtest_task_param`

- **表注释**：回测任务参数表
- **所属分组**：回测任务与报告
- **表用途**：回测任务参数表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由回测任务创建、回测执行器、Python Runner或Java BacktestExecutor写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 25. `backtest_task_progress`

- **表注释**：回测任务进度表
- **所属分组**：回测任务与报告
- **表用途**：回测任务进度表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由回测任务创建、回测执行器、Python Runner或Java BacktestExecutor写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 26. `buy_pattern_definition`

- **表注释**：买点模式定义表
- **所属分组**：模式条件与风控配置
- **表用途**：买点模式定义表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由后端业务服务、配置页面、批处理任务或人工维护写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 27. `cycle_mining_task`

- **表注释**：周期样本挖掘任务表
- **所属分组**：任务调度与批次日志
- **表用途**：周期样本挖掘任务表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由后端业务服务、配置页面、批处理任务或人工维护写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 28. `daily_review_audit_log`

- **表注释**：每日复盘审计日志表
- **所属分组**：每日复盘工作台
- **表用途**：每日复盘审计日志表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由每日复盘工作台、人工复盘、系统生成检查项写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 29. `daily_review_checklist`

- **表注释**：每日复盘检查清单表
- **所属分组**：每日复盘工作台
- **表用途**：每日复盘检查清单表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由每日复盘工作台、人工复盘、系统生成检查项写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 30. `daily_review_record`

- **表注释**：每日复盘记录表
- **所属分组**：每日复盘工作台
- **表用途**：每日复盘记录表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由每日复盘工作台、人工复盘、系统生成检查项写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 31. `daily_review_section`

- **表注释**：每日复盘章节表
- **所属分组**：每日复盘工作台
- **表用途**：每日复盘章节表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由每日复盘工作台、人工复盘、系统生成检查项写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 32. `emotion_stage_rule_version`

- **表注释**：情绪阶段规则版本展示表
- **所属分组**：规则配置与版本治理
- **表用途**：情绪阶段规则版本展示表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：主要由规则管理页面、配置导入、发布流程或Agent发布检查写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 33. `leader_manual_confirm`

- **表注释**：龙头人工确认表
- **所属分组**：龙头规则与人工确认
- **表用途**：龙头人工确认表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由龙头规则配置、人工确认流程、Leader Engine写入或维护。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 34. `leader_rule_version`

- **表注释**：龙头规则版本展示表
- **所属分组**：规则配置与版本治理
- **表用途**：龙头规则版本展示表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：主要由规则管理页面、配置导入、发布流程或Agent发布检查写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 35. `leader_type_definition`

- **表注释**：龙头类型定义表
- **所属分组**：龙头规则与人工确认
- **表用途**：龙头类型定义表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由龙头规则配置、人工确认流程、Leader Engine写入或维护。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 36. `mainline_rule_version`

- **表注释**：主线规则版本展示表
- **所属分组**：规则配置与版本治理
- **表用途**：主线规则版本展示表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：主要由规则管理页面、配置导入、发布流程或Agent发布检查写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 37. `risk_rule_version`

- **表注释**：风控规则版本展示表
- **所属分组**：规则配置与版本治理
- **表用途**：风控规则版本展示表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：主要由规则管理页面、配置导入、发布流程或Agent发布检查写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 38. `rule_publish_check_log`

- **表注释**：规则发布检查日志表
- **所属分组**：规则配置与版本治理
- **表用途**：规则发布检查日志表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：主要由规则管理页面、配置导入、发布流程或Agent发布检查写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 39. `sector_rule_version`

- **表注释**：板块规则版本展示表
- **所属分组**：规则配置与版本治理
- **表用途**：板块规则版本展示表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：主要由规则管理页面、配置导入、发布流程或Agent发布检查写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 40. `similarity_rule_version`

- **表注释**：相似度规则版本展示表
- **所属分组**：规则配置与版本治理
- **表用途**：相似度规则版本展示表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：主要由规则管理页面、配置导入、发布流程或Agent发布检查写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 41. `theme_definition`

- **表注释**：题材定义表
- **所属分组**：题材、板块、主线配置
- **表用途**：题材定义表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由题材/板块配置管理、数据导入或人工维护写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 42. `theme_stock_mapping`

- **表注释**：题材股票映射表
- **所属分组**：题材、板块、主线配置
- **表用途**：题材股票映射表。
- **数据粒度**：页面-交易日级，一行通常表示某页面某交易日的一条质量/审计/复盘记录。
- **数据写入来源**：由题材/板块配置管理、数据导入或人工维护写入。
- **关键关联字段**：`trade_date`, `market_scope`, `rule_code`, `rule_version_id`, `version_no`, `business_code`, `task_id`, `page_code`, `stock_code`, `stock_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `id` | `bigint primary key auto_increment` | 主键ID | 主键ID |
| `trade_date` | `date null` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `varchar(64) not null default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rule_code` | `varchar(128) null` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `varchar(255) null` | 规则名称 | 规则名称 |
| `rule_version_id` | `bigint null` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `version_no` | `varchar(64) null` | 版本号 | 版本号。关联说明：规则版本号。与 rule_code 共同唯一定位一个规则版本。 |
| `version_status` | `varchar(64) null` | 版本状态 | 版本状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `business_code` | `varchar(128) null` | 业务编码 | 业务编码 |
| `business_name` | `varchar(255) null` | 业务名称 | 业务名称 |
| `task_id` | `bigint null` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `task_status` | `varchar(64) null` | 任务状态 | 任务状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `page_code` | `varchar(128) null` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `stock_code` | `varchar(32) null` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `varchar(128) null` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `theme_code` | `varchar(128) null` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `varchar(255) null` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `varchar(128) null` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `varchar(255) null` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `varchar(128) null` | 模式编码 | 模式编码 |
| `pattern_name` | `varchar(255) null` | 模式名称 | 模式名称 |
| `risk_code` | `varchar(128) null` | 风险编码 | 风险编码 |
| `risk_name` | `varchar(255) null` | 风险名称 | 风险名称 |
| `status` | `varchar(64) null` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `score` | `decimal(18,6) null` | 评分 | 评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `config_json` | `json null` | 配置JSON | 配置JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `param_json` | `json null` | 参数JSON | 参数JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `result_json` | `json null` | 结果JSON | 结果JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `evidence_json` | `json null` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `remark` | `varchar(1000) null` | 备注 | 备注 |
| `features` | `json null` | 扩展字段 | 扩展字段。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `datetime not null default current_timestamp` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `datetime not null default current_timestamp on update current_timestamp` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |
| `is_deleted` | `tinyint not null default 0` | 逻辑删除：0否1是 | 逻辑删除：0否1是。关联说明：逻辑删除标识。MySQL业务表常用，0表示有效，1表示逻辑删除。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

# ClickHouse 详细表结构

## 1. `astock_analysis.stock_daily_kline`

- **表注释**：股票日K线行情快照表
- **所属分组**：基础行情与市场生态输入
- **表用途**：股票日K线行情快照表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由数据采集/导入任务写入，是所有股票级分析的基础输入表。
- **关键关联字段**：`trade_date`, `market_scope`, `stock_code`, `stock_name`, `sector_code`, `sector_name`, `industry_code`, `theme_code`, `theme_name`, `mainline_code`, `mainline_name`, `features`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `stock_code` | `String` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `code` | `String default stock_code` | 兼容代码字段 | 兼容代码字段 |
| `name` | `String default stock_name` | 兼容名称字段 | 兼容名称字段 |
| `exchange` | `LowCardinality(String) default ''` | 交易所 | 交易所 |
| `sector_code` | `String default ''` | 板块编码 | 板块编码。关联说明：板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | `String default ''` | 板块名称 | 板块名称。关联说明：板块名称。展示字段，真实关联优先使用 sector_code。 |
| `industry_code` | `String default ''` | 行业编码 | 行业编码 |
| `industry_name` | `String default ''` | 行业名称 | 行业名称 |
| `theme_code` | `String default ''` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default ''` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `open_price` | `Decimal(18,4) default 0` | 开盘价 | 开盘价 |
| `high_price` | `Decimal(18,4) default 0` | 最高价 | 最高价 |
| `low_price` | `Decimal(18,4) default 0` | 最低价 | 最低价 |
| `close_price` | `Decimal(18,4) default 0` | 收盘价 | 收盘价 |
| `pre_close_price` | `Decimal(18,4) default 0` | 昨收价 | 昨收价 |
| `pct_change` | `Decimal(18,6) default 0` | 涨跌幅 | 涨跌幅 |
| `change_pct` | `Decimal(18,6) default pct_change` | 兼容涨跌幅 | 兼容涨跌幅 |
| `change_amount` | `Decimal(18,4) default 0` | 涨跌额 | 涨跌额 |
| `volume` | `UInt64 default 0` | 成交量 | 成交量 |
| `turnover_amount` | `Decimal(24,4) default 0` | 成交额 | 成交额 |
| `amount` | `Decimal(24,4) default turnover_amount` | 兼容成交额 | 兼容成交额 |
| `amplitude` | `Decimal(18,6) default 0` | 振幅 | 振幅 |
| `volume_ratio` | `Decimal(18,6) default 0` | 量比 | 量比 |
| `turnover_rate` | `Decimal(18,6) default 0` | 换手率 | 换手率 |
| `pe_dynamic` | `Decimal(18,6) default 0` | 动态市盈率 | 动态市盈率 |
| `pb` | `Decimal(18,6) default 0` | 市净率 | 市净率 |
| `roe` | `Decimal(18,6) default 0` | 资产收益率 | 资产收益率 |
| `total_market_value` | `Decimal(24,4) default 0` | 总市值 | 总市值 |
| `float_market_value` | `Decimal(24,4) default 0` | 流通市值 | 流通市值 |
| `is_limit_up` | `UInt8 default 0` | 是否涨停 | 是否涨停 |
| `limit_up` | `UInt8 default is_limit_up` | 是否涨停兼容字段 | 是否涨停兼容字段 |
| `is_limit_down` | `UInt8 default 0` | 是否跌停 | 是否跌停 |
| `is_broken_board` | `UInt8 default 0` | 是否炸板 | 是否炸板 |
| `broken_board` | `UInt8 default is_broken_board` | 是否炸板兼容字段 | 是否炸板兼容字段 |
| `board_height` | `UInt16 default 0` | 连板高度 | 连板高度 |
| `consecutive_board_height` | `UInt16 default board_height` | 连板高度兼容字段 | 连板高度兼容字段 |
| `limit_up_days` | `UInt16 default board_height` | 连板天数兼容字段 | 连板天数兼容字段 |
| `negative_feedback_score` | `Decimal(18,6) default 0` | 负反馈分 | 负反馈分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `drawdown_score` | `Decimal(18,6) default 0` | 回撤分 | 回撤分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `DateTime default now()` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 2. `astock_analysis.market_factor_snapshot`

- **表注释**：市场因子快照表
- **所属分组**：基础行情与市场生态输入
- **表用途**：市场因子快照表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由行情聚合任务从日K和涨跌停明细聚合写入，是市场情绪和风控Engine的输入。
- **关键关联字段**：`trade_date`, `market_scope`, `features`, `created_at`, `updated_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `rise_count` | `UInt32 default 0` | 上涨家数 | 上涨家数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `up_count` | `UInt32 default rise_count` | 上涨家数兼容字段 | 上涨家数兼容字段。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `rising_count` | `UInt32 default rise_count` | 上涨家数兼容字段 | 上涨家数兼容字段。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `fall_count` | `UInt32 default 0` | 下跌家数 | 下跌家数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `down_count` | `UInt32 default fall_count` | 下跌家数兼容字段 | 下跌家数兼容字段。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `falling_count` | `UInt32 default fall_count` | 下跌家数兼容字段 | 下跌家数兼容字段。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `flat_count` | `UInt32 default 0` | 平盘家数 | 平盘家数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `unchanged_count` | `UInt32 default flat_count` | 平盘家数兼容字段 | 平盘家数兼容字段。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `market_breadth_score` | `Decimal(18,6) default 0` | 市场宽度分 | 市场宽度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `breadth_score` | `Decimal(18,6) default market_breadth_score` | 市场宽度分兼容 | 市场宽度分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `profit_effect_score` | `Decimal(18,6) default 0` | 赚钱效应分 | 赚钱效应分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `earning_effect_score` | `Decimal(18,6) default profit_effect_score` | 赚钱效应分兼容 | 赚钱效应分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `loss_effect_score` | `Decimal(18,6) default 0` | 亏钱效应分 | 亏钱效应分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `loss_pressure_score` | `Decimal(18,6) default loss_effect_score` | 亏钱效应分兼容 | 亏钱效应分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `turnover_percentile` | `Decimal(18,6) default 0` | 成交额分位 | 成交额分位 |
| `amount_percentile` | `Decimal(18,6) default turnover_percentile` | 成交额分位兼容 | 成交额分位兼容 |
| `turnover_heat_score` | `Decimal(18,6) default turnover_percentile` | 成交热度分 | 成交热度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `index_position_score` | `Decimal(18,6) default 0` | 指数位置分 | 指数位置分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `index_percentile` | `Decimal(18,6) default index_position_score` | 指数分位 | 指数分位 |
| `index_location_score` | `Decimal(18,6) default index_position_score` | 指数位置分兼容 | 指数位置分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `index_fund_risk_score` | `Decimal(18,6) default 0` | 指数资金风险分 | 指数资金风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `index_pressure_score` | `Decimal(18,6) default index_fund_risk_score` | 指数压力分 | 指数压力分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `DateTime default now()` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。

## 3. `astock_analysis.limit_up_down_ecology_snapshot`

- **表注释**：涨跌停生态快照表
- **所属分组**：基础行情与市场生态输入
- **表用途**：涨跌停生态快照表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由行情聚合任务从日K和涨跌停明细聚合写入，是市场情绪和风控Engine的输入。
- **关键关联字段**：`trade_date`, `market_scope`, `features`, `created_at`, `updated_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `limit_up_count` | `UInt32 default 0` | 涨停家数 | 涨停家数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `zt_count` | `UInt32 default limit_up_count` | 涨停家数兼容 | 涨停家数兼容。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `limit_down_count` | `UInt32 default 0` | 跌停家数 | 跌停家数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `dt_count` | `UInt32 default limit_down_count` | 跌停家数兼容 | 跌停家数兼容。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `break_board_count` | `UInt32 default 0` | 炸板家数 | 炸板家数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `broken_board_count` | `UInt32 default break_board_count` | 炸板家数兼容 | 炸板家数兼容。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `max_board_height` | `UInt16 default 0` | 最高连板高度 | 最高连板高度 |
| `highest_board_height` | `UInt16 default max_board_height` | 最高连板高度兼容 | 最高连板高度兼容 |
| `max_board_height_score` | `Decimal(18,6) default max_board_height * 10` | 最高板高度分 | 最高板高度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `ladder_height_score` | `Decimal(18,6) default max_board_height_score` | 梯队高度分 | 梯队高度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `limit_up_ecology_score` | `Decimal(18,6) default 0` | 涨停生态分 | 涨停生态分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `limit_eco_score` | `Decimal(18,6) default limit_up_ecology_score` | 涨停生态兼容分 | 涨停生态兼容分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `limit_ecology_score` | `Decimal(18,6) default limit_up_ecology_score` | 涨停生态兼容分 | 涨停生态兼容分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `limit_down_pressure_score` | `Decimal(18,6) default 0` | 跌停压力分 | 跌停压力分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `break_board_pressure_score` | `Decimal(18,6) default 0` | 炸板压力分 | 炸板压力分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `DateTime default now()` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。

## 4. `astock_analysis.emotion_stage_snapshot`

- **表注释**：情绪阶段快照表
- **所属分组**：情绪周期与历史样本
- **表用途**：情绪阶段快照表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 EmotionStageRecognitionEngine 根据市场因子、涨跌停生态和历史样本计算写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `stage_code`, `features`, `created_at`, `updated_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `primary_stage` | `LowCardinality(String) default ''` | 主阶段编码 | 主阶段编码 |
| `primary_stage_name` | `String default ''` | 主阶段名称 | 主阶段名称 |
| `stage_code` | `LowCardinality(String) default primary_stage` | 阶段编码兼容 | 阶段编码兼容 |
| `emotion_stage` | `LowCardinality(String) default primary_stage` | 情绪阶段兼容 | 情绪阶段兼容 |
| `stage_confidence` | `Decimal(18,6) default 0` | 阶段置信度 | 阶段置信度 |
| `stage_score` | `Decimal(18,6) default stage_confidence` | 阶段分兼容 | 阶段分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `second_candidate_stage` | `LowCardinality(String) default ''` | 第二候选阶段 | 第二候选阶段。日期维度字段，通常参与过滤、分区、排序或页面查询。 |
| `second_candidate_stage_name` | `String default ''` | 第二候选阶段名称 | 第二候选阶段名称。日期维度字段，通常参与过滤、分区、排序或页面查询。 |
| `third_candidate_stage` | `LowCardinality(String) default ''` | 第三候选阶段 | 第三候选阶段。日期维度字段，通常参与过滤、分区、排序或页面查询。 |
| `third_candidate_stage_name` | `String default ''` | 第三候选阶段名称 | 第三候选阶段名称。日期维度字段，通常参与过滤、分区、排序或页面查询。 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `data_complete` | `UInt8 default 1` | 数据是否完整 | 数据是否完整 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `DateTime default now()` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 5. `astock_analysis.emotion_stage_score_detail`

- **表注释**：情绪阶段评分明细表
- **所属分组**：情绪周期与历史样本
- **表用途**：情绪阶段评分明细表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 EmotionStageRecognitionEngine 根据市场因子、涨跌停生态和历史样本计算写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `stage_code`, `features`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `stage_code` | `LowCardinality(String)` | 阶段编码 | 阶段编码 |
| `stage_name` | `String default ''` | 阶段名称 | 阶段名称 |
| `stage_score` | `Decimal(18,6) default 0` | 阶段总分 | 阶段总分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `rank_no` | `UInt16 default 0` | 排名 | 排名 |
| `factor_percentile_match_score` | `Decimal(18,6) default 0` | 因子分位匹配分 | 因子分位匹配分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `historical_sample_similarity_score` | `Decimal(18,6) default 0` | 历史样本相似分 | 历史样本相似分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `stage_path_match_score` | `Decimal(18,6) default 0` | 阶段路径匹配分 | 阶段路径匹配分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `following_validation_score` | `Decimal(18,6) default 0` | 后续演化验证分 | 后续演化验证分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `manual_sample_correction_score` | `Decimal(18,6) default 0` | 人工样本修正分 | 人工样本修正分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 6. `astock_analysis.stage_transition_snapshot`

- **表注释**：情绪阶段转移快照表
- **所属分组**：情绪周期与历史样本
- **表用途**：情绪阶段转移快照表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 EmotionStageRecognitionEngine 根据市场因子、涨跌停生态和历史样本计算写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `from_stage` | `LowCardinality(String) default ''` | 来源阶段 | 来源阶段 |
| `to_stage` | `LowCardinality(String) default ''` | 目标阶段 | 目标阶段 |
| `primary_stage` | `LowCardinality(String) default to_stage` | 主阶段兼容 | 主阶段兼容 |
| `transition_probability` | `Decimal(18,6) default 0` | 转移概率 | 转移概率 |
| `transition_score` | `Decimal(18,6) default transition_probability` | 转移分 | 转移分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 7. `astock_analysis.historical_cycle_sample`

- **表注释**：历史周期样本库表
- **所属分组**：情绪周期与历史样本
- **表用途**：历史周期样本库表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由ClickHouse分析任务、Engine输出或数据导入任务写入。
- **关键关联字段**：`trade_date`, `market_scope`, `sample_id`, `stage_code`, `pattern_code`, `stock_code`, `stock_name`, `mainline_code`, `theme_code`, `mainline_name`, `theme_name`, `features`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 样本交易日 | 样本交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `id` | `UInt64 default 0` | ID兼容 | ID兼容 |
| `sample_id` | `UInt64 default id` | 样本ID | 样本ID |
| `stage_code` | `LowCardinality(String) default ''` | 阶段编码 | 阶段编码 |
| `stage_type` | `LowCardinality(String) default stage_code` | 阶段类型 | 阶段类型 |
| `emotion_stage` | `LowCardinality(String) default stage_code` | 情绪阶段 | 情绪阶段 |
| `primary_stage` | `LowCardinality(String) default stage_code` | 主阶段 | 主阶段 |
| `sample_type` | `LowCardinality(String) default 'SINGLE_DAY'` | 样本类型 | 样本类型 |
| `sample_confidence` | `Decimal(18,6) default 0` | 样本置信度 | 样本置信度 |
| `confidence` | `Decimal(18,6) default sample_confidence` | 置信度兼容 | 置信度兼容 |
| `stage_confidence` | `Decimal(18,6) default sample_confidence` | 阶段置信度 | 阶段置信度 |
| `similarity_score` | `Decimal(18,6) default 0` | 相似分 | 相似分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `sample_similarity_score` | `Decimal(18,6) default similarity_score` | 样本相似分 | 样本相似分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `stage_score` | `Decimal(18,6) default similarity_score` | 阶段分 | 阶段分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `market_breadth_score` | `Decimal(18,6) default 0` | 市场宽度分 | 市场宽度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `breadth_score` | `Decimal(18,6) default market_breadth_score` | 市场宽度分兼容 | 市场宽度分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `turnover_percentile` | `Decimal(18,6) default 0` | 成交分位 | 成交分位 |
| `turnover_score` | `Decimal(18,6) default turnover_percentile` | 成交分 | 成交分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `turnover_heat_score` | `Decimal(18,6) default turnover_percentile` | 成交热度分 | 成交热度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `index_position_score` | `Decimal(18,6) default 0` | 指数位置分 | 指数位置分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `index_percentile` | `Decimal(18,6) default index_position_score` | 指数分位 | 指数分位 |
| `limit_ecology_score` | `Decimal(18,6) default 0` | 涨停生态分 | 涨停生态分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `limit_up_ecology_score` | `Decimal(18,6) default limit_ecology_score` | 涨停生态分兼容 | 涨停生态分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `leader_ladder_score` | `Decimal(18,6) default 0` | 龙头梯队分 | 龙头梯队分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `ladder_height_score` | `Decimal(18,6) default leader_ladder_score` | 梯队高度分 | 梯队高度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `loss_effect_score` | `Decimal(18,6) default 0` | 亏钱效应分 | 亏钱效应分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `loss_pressure_score` | `Decimal(18,6) default loss_effect_score` | 亏钱压力分 | 亏钱压力分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `stage_path_score` | `Decimal(18,6) default 0` | 阶段路径分 | 阶段路径分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `mainline_structure_score` | `Decimal(18,6) default 0` | 主线结构分 | 主线结构分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `mainline_strength_score` | `Decimal(18,6) default mainline_structure_score` | 主线强度分 | 主线强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `leader_feedback_score` | `Decimal(18,6) default 0` | 龙头反馈分 | 龙头反馈分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `negative_feedback_score` | `Decimal(18,6) default leader_feedback_score` | 负反馈分 | 负反馈分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `pattern_code` | `String default ''` | 模式编码 | 模式编码 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `theme_code` | `String default mainline_code` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `theme_name` | `String default mainline_name` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `future_1d_return` | `Decimal(18,6) default 0` | 未来1日收益，仅回测窗口使用 | 未来1日收益，仅回测窗口使用 |
| `future1d_return` | `Decimal(18,6) default future_1d_return` | 未来1日收益兼容 | 未来1日收益兼容 |
| `future_3d_return` | `Decimal(18,6) default 0` | 未来3日收益，仅回测窗口使用 | 未来3日收益，仅回测窗口使用 |
| `future3d_return` | `Decimal(18,6) default future_3d_return` | 未来3日收益兼容 | 未来3日收益兼容 |
| `following_3d_return` | `Decimal(18,6) default future_3d_return` | 后续3日收益兼容 | 后续3日收益兼容 |
| `future_5d_return` | `Decimal(18,6) default 0` | 未来5日收益，仅回测窗口使用 | 未来5日收益，仅回测窗口使用 |
| `future5d_return` | `Decimal(18,6) default future_5d_return` | 未来5日收益兼容 | 未来5日收益兼容 |
| `future_10d_return` | `Decimal(18,6) default 0` | 未来10日收益，仅回测窗口使用 | 未来10日收益，仅回测窗口使用 |
| `future10d_return` | `Decimal(18,6) default future_10d_return` | 未来10日收益兼容 | 未来10日收益兼容 |
| `max_drawdown` | `Decimal(18,6) default 0` | 最大回撤，仅回测窗口使用 | 最大回撤，仅回测窗口使用 |
| `following_max_drawdown` | `Decimal(18,6) default max_drawdown` | 后续最大回撤兼容 | 后续最大回撤兼容 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `feature_json` | `String default '{}'` | 特征JSON | 特征JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 8. `astock_analysis.historical_similarity_match`

- **表注释**：历史相似行情匹配结果表
- **所属分组**：历史相似行情匹配
- **表用途**：历史相似行情匹配结果表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 SimilarityMatchEngine 进行T日相似度匹配后写入；T日匹配不得读取 future_* 字段。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `sample_id`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 当前交易日 | 当前交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `match_type` | `LowCardinality(String) default 'SINGLE_DAY'` | 匹配类型 | 匹配类型 |
| `sample_id` | `UInt64 default 0` | 历史样本ID | 历史样本ID |
| `historical_trade_date` | `Date` | 历史交易日 | 历史交易日。日期维度字段，通常参与过滤、分区、排序或页面查询。 |
| `historical_stage` | `LowCardinality(String) default ''` | 历史阶段 | 历史阶段 |
| `market_environment_similarity_score` | `Decimal(18,6) default 0` | 市场环境相似度 | 市场环境相似度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `emotion_cycle_similarity_score` | `Decimal(18,6) default 0` | 情绪周期相似度 | 情绪周期相似度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `theme_leader_similarity_score` | `Decimal(18,6) default 0` | 主线龙头相似度 | 主线龙头相似度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `total_similarity_score` | `Decimal(18,6) default 0` | 总相似度 | 总相似度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `dimension_score_json` | `String default '[]'` | 维度分JSON | 维度分JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `reference_text` | `String default ''` | 参考文本 | 参考文本 |
| `risk_text` | `String default ''` | 风险文本 | 风险文本 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 9. `astock_analysis.historical_similarity_factor_detail`

- **表注释**：历史相似行情因子明细表
- **所属分组**：历史相似行情匹配
- **表用途**：历史相似行情因子明细表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 SimilarityMatchEngine 进行T日相似度匹配后写入；T日匹配不得读取 future_* 字段。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `sample_id`, `dimension_code`, `dimension_group_code`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 当前交易日 | 当前交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `match_type` | `LowCardinality(String) default 'SINGLE_DAY'` | 匹配类型 | 匹配类型 |
| `sample_id` | `UInt64 default 0` | 历史样本ID | 历史样本ID |
| `historical_trade_date` | `Date` | 历史交易日 | 历史交易日。日期维度字段，通常参与过滤、分区、排序或页面查询。 |
| `dimension_code` | `LowCardinality(String)` | 维度编码 | 维度编码 |
| `dimension_name` | `String default ''` | 维度名称 | 维度名称 |
| `dimension_group_code` | `LowCardinality(String) default ''` | 维度分组 | 维度分组 |
| `dimension_weight` | `Decimal(18,6) default 0` | 维度权重 | 维度权重 |
| `current_value` | `Decimal(18,6) default 0` | 当前值 | 当前值 |
| `historical_value` | `Decimal(18,6) default 0` | 历史值 | 历史值 |
| `dimension_similarity_score` | `Decimal(18,6) default 0` | 维度相似分 | 维度相似分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 10. `astock_analysis.theme_daily_snapshot`

- **表注释**：题材日快照表
- **所属分组**：题材、板块、主线分析
- **表用途**：题材日快照表。
- **数据粒度**：交易日-板块级，一行表示某板块在某交易日的快照。
- **数据写入来源**：由主线/题材/板块聚合任务或 MainlineRecognitionEngine 写入。
- **关键关联字段**：`trade_date`, `market_scope`, `theme_code`, `theme_name`, `sector_code`, `sector_name`, `mainline_code`, `mainline_name`, `features`, `created_at`, `updated_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `theme_code` | `String` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default ''` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `theme_type` | `LowCardinality(String) default 'CONCEPT'` | 题材类型 | 题材类型 |
| `sector_code` | `String default theme_code` | 板块编码兼容 | 板块编码兼容。关联说明：板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | `String default theme_name` | 板块名称兼容 | 板块名称兼容。关联说明：板块名称。展示字段，真实关联优先使用 sector_code。 |
| `mainline_code` | `String default theme_code` | 主线编码兼容 | 主线编码兼容。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default theme_name` | 主线名称兼容 | 主线名称兼容。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pct_change` | `Decimal(18,6) default 0` | 涨跌幅 | 涨跌幅 |
| `change_pct` | `Decimal(18,6) default pct_change` | 涨跌幅兼容 | 涨跌幅兼容 |
| `limit_up_count` | `Decimal(18,6) default 0` | 涨停数 | 涨停数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `zt_count` | `Decimal(18,6) default limit_up_count` | 涨停数兼容 | 涨停数兼容。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `stock_count` | `Decimal(18,6) default 0` | 成分股数 | 成分股数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `component_count` | `Decimal(18,6) default stock_count` | 成分股数兼容 | 成分股数兼容。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `turnover_amount` | `Decimal(24,4) default 0` | 成交额 | 成交额 |
| `amount` | `Decimal(24,4) default turnover_amount` | 成交额兼容 | 成交额兼容 |
| `turnover_ratio` | `Decimal(18,6) default 0` | 成交占比 | 成交占比 |
| `turnover_amount_ratio` | `Decimal(18,6) default turnover_ratio` | 成交占比兼容 | 成交占比兼容 |
| `continuity_days` | `Decimal(18,6) default 0` | 持续天数 | 持续天数 |
| `continuous_days` | `Decimal(18,6) default continuity_days` | 持续天数兼容 | 持续天数兼容 |
| `active_days` | `Decimal(18,6) default continuity_days` | 活跃天数 | 活跃天数 |
| `max_board_height` | `Decimal(18,6) default 0` | 最高板高度 | 最高板高度 |
| `highest_board_height` | `Decimal(18,6) default max_board_height` | 最高板高度兼容 | 最高板高度兼容 |
| `leader_count` | `Decimal(18,6) default 0` | 龙头数量 | 龙头数量。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `core_stock_count` | `Decimal(18,6) default leader_count` | 核心股数量 | 核心股数量。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |
| `updated_at` | `DateTime default now()` | 更新时间 | 更新时间。关联说明：更新时间。记录数据最后更新时间，用于增量同步和问题追踪。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。

## 11. `astock_analysis.theme_strength_snapshot`

- **表注释**：题材强度快照表
- **所属分组**：题材、板块、主线分析
- **表用途**：题材强度快照表。
- **数据粒度**：交易日-题材级，一行表示某题材在某交易日的快照。
- **数据写入来源**：由主线/题材/板块聚合任务或 MainlineRecognitionEngine 写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `theme_code`, `theme_name`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `theme_code` | `String` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default ''` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `theme_type` | `LowCardinality(String) default ''` | 题材类型 | 题材类型 |
| `theme_strength_score` | `Decimal(18,6) default 0` | 题材强度分 | 题材强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `strength_score` | `Decimal(18,6) default theme_strength_score` | 强度分兼容 | 强度分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `rank_no` | `UInt32 default 0` | 排名 | 排名 |
| `limit_up_cluster_score` | `Decimal(18,6) default 0` | 涨停聚集强度 | 涨停聚集强度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `turnover_concentration_score` | `Decimal(18,6) default 0` | 成交集中强度 | 成交集中强度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `continuity_score` | `Decimal(18,6) default 0` | 持续性强度 | 持续性强度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `ladder_integrity_score` | `Decimal(18,6) default 0` | 梯队完整度 | 梯队完整度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `leader_drive_score` | `Decimal(18,6) default 0` | 龙头带动性 | 龙头带动性。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `emotion_match_score` | `Decimal(18,6) default 0` | 情绪匹配度 | 情绪匹配度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 12. `astock_analysis.mainline_daily_snapshot`

- **表注释**：主线日快照表
- **所属分组**：题材、板块、主线分析
- **表用途**：主线日快照表。
- **数据粒度**：交易日-题材级，一行表示某题材在某交易日的快照。
- **数据写入来源**：由主线/题材/板块聚合任务或 MainlineRecognitionEngine 写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `mainline_id`, `mainline_code`, `mainline_name`, `theme_code`, `theme_name`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `mainline_id` | `UInt64 default 0` | 主线ID | 主线ID |
| `mainline_code` | `String` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `theme_code` | `String default mainline_code` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default mainline_name` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_status` | `LowCardinality(String) default ''` | 主线状态 | 主线状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `lifecycle_stage` | `LowCardinality(String) default ''` | 生命周期阶段 | 生命周期阶段 |
| `theme_role` | `LowCardinality(String) default ''` | 题材角色 | 题材角色 |
| `mainline_strength_score` | `Decimal(18,6) default 0` | 主线强度分 | 主线强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `strength_score` | `Decimal(18,6) default mainline_strength_score` | 强度分兼容 | 强度分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `rank_no` | `UInt32 default 0` | 排名 | 排名 |
| `limit_up_cluster_score` | `Decimal(18,6) default 0` | 涨停聚集强度 | 涨停聚集强度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `turnover_concentration_score` | `Decimal(18,6) default 0` | 成交集中强度 | 成交集中强度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `continuity_score` | `Decimal(18,6) default 0` | 持续性强度 | 持续性强度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `ladder_integrity_score` | `Decimal(18,6) default 0` | 梯队完整度 | 梯队完整度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `leader_drive_score` | `Decimal(18,6) default 0` | 龙头带动性 | 龙头带动性。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `emotion_match_score` | `Decimal(18,6) default 0` | 情绪匹配度 | 情绪匹配度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `mainline_decay_risk_score` | `Decimal(18,6) default 0` | 主线衰退风险分 | 主线衰退风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_score` | `Decimal(18,6) default mainline_decay_risk_score` | 风险分兼容 | 风险分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 13. `astock_analysis.mainline_switch_snapshot`

- **表注释**：主线切换快照表
- **所属分组**：题材、板块、主线分析
- **表用途**：主线切换快照表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由主线/题材/板块聚合任务或 MainlineRecognitionEngine 写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `old_mainline_code`, `new_mainline_code`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `old_mainline_code` | `String default ''` | 旧主线编码 | 旧主线编码 |
| `old_mainline_name` | `String default ''` | 旧主线名称 | 旧主线名称 |
| `new_mainline_code` | `String default ''` | 新主线编码 | 新主线编码 |
| `new_mainline_name` | `String default ''` | 新主线名称 | 新主线名称 |
| `switch_status` | `LowCardinality(String) default ''` | 切换状态 | 切换状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `switch_score` | `Decimal(18,6) default 0` | 切换分 | 切换分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 14. `astock_analysis.sector_strength_snapshot`

- **表注释**：板块强度快照表
- **所属分组**：题材、板块、主线分析
- **表用途**：板块强度快照表。
- **数据粒度**：交易日-板块级，一行表示某板块在某交易日的快照。
- **数据写入来源**：由主线/题材/板块聚合任务或 MainlineRecognitionEngine 写入。
- **关键关联字段**：`trade_date`, `market_scope`, `sector_code`, `sector_name`, `theme_code`, `theme_name`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `sector_code` | `String` | 板块编码 | 板块编码。关联说明：板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | `String default ''` | 板块名称 | 板块名称。关联说明：板块名称。展示字段，真实关联优先使用 sector_code。 |
| `theme_code` | `String default sector_code` | 题材编码兼容 | 题材编码兼容。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default sector_name` | 题材名称兼容 | 题材名称兼容。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `sector_type` | `LowCardinality(String) default ''` | 板块类型 | 板块类型 |
| `pct_change` | `Decimal(18,6) default 0` | 涨跌幅 | 涨跌幅 |
| `change_pct` | `Decimal(18,6) default pct_change` | 涨跌幅兼容 | 涨跌幅兼容 |
| `limit_up_count` | `Decimal(18,6) default 0` | 涨停数 | 涨停数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `stock_count` | `Decimal(18,6) default 0` | 股票数 | 股票数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `turnover_amount` | `Decimal(24,4) default 0` | 成交额 | 成交额 |
| `turnover_ratio` | `Decimal(18,6) default 0` | 成交占比 | 成交占比 |
| `continuity_days` | `Decimal(18,6) default 0` | 持续天数 | 持续天数 |
| `max_board_height` | `Decimal(18,6) default 0` | 最高板高度 | 最高板高度 |
| `leader_count` | `Decimal(18,6) default 0` | 龙头数量 | 龙头数量。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `sector_strength_score` | `Decimal(18,6) default 0` | 板块强度分 | 板块强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `strength_score` | `Decimal(18,6) default sector_strength_score` | 强度分兼容 | 强度分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `rank_no` | `UInt32 default 0` | 排名 | 排名 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。

## 15. `astock_analysis.leader_daily_snapshot`

- **表注释**：龙头日快照表
- **所属分组**：龙头梯队与反馈
- **表用途**：龙头日快照表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由 LeaderRecognitionEngine 根据个股强度、主线关系、带动性和反馈计算写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `stock_code`, `stock_name`, `sector_code`, `sector_name`, `mainline_code`, `mainline_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `stock_code` | `String` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `sector_code` | `String default ''` | 板块编码 | 板块编码。关联说明：板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | `String default ''` | 板块名称 | 板块名称。关联说明：板块名称。展示字段，真实关联优先使用 sector_code。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `theme_code` | `String default mainline_code` | 题材编码兼容 | 题材编码兼容。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default mainline_name` | 题材名称兼容 | 题材名称兼容。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `leader_status` | `LowCardinality(String) default ''` | 龙头状态 | 龙头状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `leader_score` | `Decimal(18,6) default 0` | 龙头综合分 | 龙头综合分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `rank_no` | `UInt32 default 0` | 排名 | 排名 |
| `recognition_score` | `Decimal(18,6) default 0` | 辨识度分 | 辨识度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `mainline_relation_score` | `Decimal(18,6) default 0` | 主线关联分 | 主线关联分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `drive_score` | `Decimal(18,6) default 0` | 带动分 | 带动分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `leader_drive_score` | `Decimal(18,6) default drive_score` | 龙头带动分兼容 | 龙头带动分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `strength_score` | `Decimal(18,6) default 0` | 强度分 | 强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `support_score` | `Decimal(18,6) default 0` | 承接分 | 承接分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `continuity_score` | `Decimal(18,6) default 0` | 持续性分 | 持续性分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_feedback_score` | `Decimal(18,6) default 0` | 风险反馈分 | 风险反馈分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `negative_feedback_score` | `Decimal(18,6) default 0` | 负反馈分 | 负反馈分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `leader_negative_feedback_score` | `Decimal(18,6) default negative_feedback_score` | 负反馈分兼容 | 负反馈分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `board_height` | `Decimal(18,6) default 0` | 连板高度 | 连板高度 |
| `limit_up` | `UInt8 default 0` | 是否涨停 | 是否涨停 |
| `broken_board` | `UInt8 default 0` | 是否炸板 | 是否炸板 |
| `pct_change` | `Decimal(18,6) default 0` | 涨跌幅 | 涨跌幅 |
| `turnover_amount` | `Decimal(24,4) default 0` | 成交额 | 成交额 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 16. `astock_analysis.leader_ladder_snapshot`

- **表注释**：龙头梯队快照表
- **所属分组**：龙头梯队与反馈
- **表用途**：龙头梯队快照表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 LeaderRecognitionEngine 根据个股强度、主线关系、带动性和反馈计算写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `top_stock_code`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `board_height` | `Decimal(18,6) default 0` | 板高 | 板高 |
| `stock_count` | `UInt32 default 0` | 股票数量 | 股票数量。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `top_stock_code` | `String default ''` | 最高分股票代码 | 最高分股票代码 |
| `top_stock_name` | `String default ''` | 最高分股票名称 | 最高分股票名称 |
| `top_leader_score` | `Decimal(18,6) default 0` | 最高龙头分 | 最高龙头分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 17. `astock_analysis.leader_drive_snapshot`

- **表注释**：龙头带动快照表
- **所属分组**：龙头梯队与反馈
- **表用途**：龙头带动快照表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由 LeaderRecognitionEngine 根据个股强度、主线关系、带动性和反馈计算写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `stock_code`, `stock_name`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `stock_code` | `String` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `sector_drive_score` | `Decimal(18,6) default 0` | 板块带动分 | 板块带动分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `mainline_drive_score` | `Decimal(18,6) default 0` | 主线带动分 | 主线带动分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `emotion_drive_score` | `Decimal(18,6) default 0` | 情绪带动分 | 情绪带动分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `fund_drive_score` | `Decimal(18,6) default 0` | 资金带动分 | 资金带动分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `leader_drive_score` | `Decimal(18,6) default 0` | 龙头带动分 | 龙头带动分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `drive_score` | `Decimal(18,6) default leader_drive_score` | 带动分兼容 | 带动分兼容。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 18. `astock_analysis.leader_negative_feedback`

- **表注释**：龙头负反馈快照表
- **所属分组**：龙头梯队与反馈
- **表用途**：龙头负反馈快照表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由 LeaderRecognitionEngine 根据个股强度、主线关系、带动性和反馈计算写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `stock_code`, `stock_name`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `stock_code` | `String` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `negative_feedback_score` | `Decimal(18,6) default 0` | 负反馈分 | 负反馈分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `broken_board` | `UInt8 default 0` | 是否炸板 | 是否炸板 |
| `limit_down` | `UInt8 default 0` | 是否跌停 | 是否跌停 |
| `impact_mainline` | `UInt8 default 0` | 是否影响主线 | 是否影响主线 |
| `impact_emotion_cycle` | `UInt8 default 0` | 是否影响情绪周期 | 是否影响情绪周期 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 19. `astock_analysis.buy_pattern_signal_snapshot`

- **表注释**：买点条件信号快照表
- **所属分组**：模式条件与回测结果
- **表用途**：买点条件信号快照表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由 PatternConditionEngine 或回测任务写入，只表达研究条件状态，不输出行动建议。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `pattern_code`, `stock_code`, `stock_name`, `mainline_code`, `mainline_name`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `pattern_code` | `String` | 模式编码 | 模式编码 |
| `pattern_name` | `String default ''` | 模式名称 | 模式名称 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `watch_object_type` | `LowCardinality(String) default ''` | 观察对象类型 | 观察对象类型 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `leader_status` | `LowCardinality(String) default ''` | 龙头状态 | 龙头状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `emotion_stage` | `LowCardinality(String) default ''` | 情绪阶段 | 情绪阶段 |
| `condition_status` | `LowCardinality(String) default ''` | 条件状态 | 条件状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `condition_score` | `Decimal(18,6) default 0` | 条件分 | 条件分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `pattern_condition_score` | `Decimal(18,6) default condition_score` | 模式条件分 | 模式条件分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `cycle_admission_score` | `Decimal(18,6) default 0` | 周期准入分 | 周期准入分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `mainline_valid_score` | `Decimal(18,6) default 0` | 主线有效分 | 主线有效分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `leader_position_score` | `Decimal(18,6) default 0` | 龙头地位分 | 龙头地位分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `trigger_score` | `Decimal(18,6) default 0` | 触发分 | 触发分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `backtest_support_score` | `Decimal(18,6) default 0` | 回测支持分 | 回测支持分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `manual_correction_score` | `Decimal(18,6) default 0` | 人工修正分 | 人工修正分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_veto` | `UInt8 default 0` | 是否风险否决 | 是否风险否决 |
| `risk_veto_reason` | `String default ''` | 风险否决原因 | 风险否决原因 |
| `invalidated` | `UInt8 default 0` | 是否失效 | 是否失效。日期维度字段，通常参与过滤、分区、排序或页面查询。 |
| `invalidated_reason` | `String default ''` | 失效原因 | 失效原因。日期维度字段，通常参与过滤、分区、排序或页面查询。 |
| `allow_condition_met_display` | `UInt8 default 0` | 是否允许展示条件满足 | 是否允许展示条件满足 |
| `signal_text` | `String default ''` | 信号文本，非交易建议 | 信号文本，非交易建议 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 20. `astock_analysis.pattern_risk_veto_snapshot`

- **表注释**：模式风险否决快照表
- **所属分组**：模式条件与回测结果
- **表用途**：模式风险否决快照表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由 RiskControlEngine 根据市场生态、个股反馈、主线衰退等风险因子写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `pattern_code`, `stock_code`, `stock_name`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `pattern_code` | `String default ''` | 模式编码 | 模式编码 |
| `pattern_name` | `String default ''` | 模式名称 | 模式名称 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `condition_status` | `LowCardinality(String) default ''` | 条件状态 | 条件状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `condition_score` | `Decimal(18,6) default 0` | 条件分 | 条件分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_veto` | `UInt8 default 0` | 是否风险否决 | 是否风险否决 |
| `risk_veto_reason` | `String default ''` | 风险否决原因 | 风险否决原因 |
| `invalidated` | `UInt8 default 0` | 是否失效 | 是否失效。日期维度字段，通常参与过滤、分区、排序或页面查询。 |
| `invalidated_reason` | `String default ''` | 失效原因 | 失效原因。日期维度字段，通常参与过滤、分区、排序或页面查询。 |
| `risk_action` | `LowCardinality(String) default ''` | 风险动作 | 风险动作 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 21. `astock_analysis.risk_signal_snapshot`

- **表注释**：风控信号快照表
- **所属分组**：风控信号
- **表用途**：风控信号快照表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 RiskControlEngine 根据市场生态、个股反馈、主线衰退等风险因子写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `risk_code`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `risk_code` | `String` | 风险编码 | 风险编码 |
| `risk_name` | `String default ''` | 风险名称 | 风险名称 |
| `risk_source` | `LowCardinality(String) default ''` | 风险来源 | 风险来源 |
| `risk_score` | `Decimal(18,6) default 0` | 风险分 | 风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_level` | `LowCardinality(String) default ''` | 风险等级 | 风险等级 |
| `signal_level` | `LowCardinality(String) default ''` | 信号等级 | 信号等级 |
| `risk_action` | `LowCardinality(String) default ''` | 风险动作 | 风险动作 |
| `one_vote_veto` | `UInt8 default 0` | 是否一票否决 | 是否一票否决 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 22. `astock_analysis.risk_signal_detail`

- **表注释**：风控信号明细表
- **所属分组**：风控信号
- **表用途**：风控信号明细表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 RiskControlEngine 根据市场生态、个股反馈、主线衰退等风险因子写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `risk_code`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `risk_code` | `String` | 风险编码 | 风险编码 |
| `risk_name` | `String default ''` | 风险名称 | 风险名称 |
| `risk_source` | `LowCardinality(String) default ''` | 风险来源 | 风险来源 |
| `signal_level` | `LowCardinality(String) default ''` | 信号等级 | 信号等级 |
| `risk_level` | `LowCardinality(String) default ''` | 风险等级 | 风险等级 |
| `risk_score` | `Decimal(18,6) default 0` | 风险分 | 风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_action` | `LowCardinality(String) default ''` | 风险动作 | 风险动作 |
| `one_vote_veto` | `UInt8 default 0` | 是否一票否决 | 是否一票否决 |
| `risk_text` | `String default ''` | 风险文本 | 风险文本 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 23. `astock_analysis.backtest_signal_detail`

- **表注释**：回测信号明细表
- **所属分组**：回测明细与统计
- **表用途**：回测信号明细表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由 BacktestExecutor / Python Runner 写入，用于回测明细、绩效、分层统计和失败归因。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `sample_id`, `stage_code`, `pattern_code`, `stock_code`, `stock_name`, `mainline_code`, `mainline_name`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 回测任务日 | 回测任务日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `sample_id` | `UInt64 default 0` | 样本ID | 样本ID |
| `sample_date` | `Date` | 样本日 | 样本日。日期维度字段，通常参与过滤、分区、排序或页面查询。 |
| `stage_code` | `LowCardinality(String) default ''` | 阶段编码 | 阶段编码 |
| `pattern_code` | `String default ''` | 模式编码 | 模式编码 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `signal_score` | `Decimal(18,6) default 0` | 信号分 | 信号分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_score` | `Decimal(18,6) default 0` | 风险分 | 风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_action` | `LowCardinality(String) default ''` | 风险动作 | 风险动作 |
| `signal_effective` | `UInt8 default 0` | 信号是否有效 | 信号是否有效 |
| `risk_vetoed` | `UInt8 default 0` | 是否被风险过滤 | 是否被风险过滤 |
| `replay_status` | `LowCardinality(String) default ''` | 回放状态 | 回放状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `replay_return` | `Decimal(18,6) default 0` | 回放收益 | 回放收益 |
| `replay_drawdown` | `Decimal(18,6) default 0` | 回放回撤 | 回放回撤 |
| `future_1d_return` | `Decimal(18,6) default 0` | 未来1日收益，仅回测 | 未来1日收益，仅回测 |
| `future_3d_return` | `Decimal(18,6) default 0` | 未来3日收益，仅回测 | 未来3日收益，仅回测 |
| `future_5d_return` | `Decimal(18,6) default 0` | 未来5日收益，仅回测 | 未来5日收益，仅回测 |
| `future_10d_return` | `Decimal(18,6) default 0` | 未来10日收益，仅回测 | 未来10日收益，仅回测 |
| `max_drawdown` | `Decimal(18,6) default 0` | 最大回撤，仅回测 | 最大回撤，仅回测 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 24. `astock_analysis.backtest_performance_detail`

- **表注释**：回测绩效明细表
- **所属分组**：回测明细与统计
- **表用途**：回测绩效明细表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 BacktestExecutor / Python Runner 写入，用于回测明细、绩效、分层统计和失败归因。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `layer_code`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 回测任务日 | 回测任务日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `layer_code` | `String default ''` | 分层编码 | 分层编码 |
| `layer_name` | `String default ''` | 分层名称 | 分层名称 |
| `metric_name` | `String default ''` | 指标名 | 指标名 |
| `metric_value` | `Decimal(24,6) default 0` | 指标值 | 指标值 |
| `sample_count` | `UInt32 default 0` | 样本数 | 样本数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 25. `astock_analysis.backtest_layer_stat`

- **表注释**：回测分层统计表
- **所属分组**：回测明细与统计
- **表用途**：回测分层统计表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 BacktestExecutor / Python Runner 写入，用于回测明细、绩效、分层统计和失败归因。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `layer_code`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 回测任务日 | 回测任务日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `layer_code` | `String default ''` | 分层编码 | 分层编码 |
| `layer_name` | `String default ''` | 分层名称 | 分层名称 |
| `sample_count` | `UInt32 default 0` | 样本数 | 样本数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `effective_signal_count` | `UInt32 default 0` | 有效信号数 | 有效信号数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `risk_veto_count` | `UInt32 default 0` | 风控过滤数 | 风控过滤数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `win_rate` | `Decimal(18,6) default 0` | 胜率 | 胜率 |
| `avg_return` | `Decimal(18,6) default 0` | 平均收益 | 平均收益 |
| `avg_drawdown` | `Decimal(18,6) default 0` | 平均回撤 | 平均回撤 |
| `profit_loss_ratio` | `Decimal(18,6) default 0` | 盈亏比 | 盈亏比 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 26. `astock_analysis.backtest_failure_case`

- **表注释**：回测失败样本表
- **所属分组**：回测明细与统计
- **表用途**：回测失败样本表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由 BacktestExecutor / Python Runner 写入，用于回测明细、绩效、分层统计和失败归因。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `sample_id`, `pattern_code`, `stock_code`, `stock_name`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 回测任务日 | 回测任务日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `sample_id` | `UInt64 default 0` | 样本ID | 样本ID |
| `sample_date` | `Date` | 样本日 | 样本日。日期维度字段，通常参与过滤、分区、排序或页面查询。 |
| `pattern_code` | `String default ''` | 模式编码 | 模式编码 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `failure_type` | `LowCardinality(String) default ''` | 失败类型 | 失败类型 |
| `failure_reason` | `String default ''` | 失败原因 | 失败原因 |
| `replay_status` | `LowCardinality(String) default ''` | 回放状态 | 回放状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `replay_return` | `Decimal(18,6) default 0` | 回放收益 | 回放收益 |
| `replay_drawdown` | `Decimal(18,6) default 0` | 回放回撤 | 回放回撤 |
| `future_3d_return` | `Decimal(18,6) default 0` | 未来3日收益，仅回测 | 未来3日收益，仅回测 |
| `max_drawdown` | `Decimal(18,6) default 0` | 最大回撤，仅回测 | 最大回撤，仅回测 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 27. `astock_analysis.agent_audit_code_scan_detail`

- **表注释**：Agent代码扫描明细表
- **所属分组**：Agent审计明细
- **表用途**：Agent代码扫描明细表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 AgentAuditExecutor 写入，用于代码扫描、字段血缘、规则命中、发布闸门。
- **关键关联字段**：`trade_date`, `market_scope`, `audit_task_id`, `task_id`, `rule_version_id`, `issue_code`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 审计日期 | 审计日期。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `audit_task_id` | `UInt64 default 0` | 审计任务ID | 审计任务ID。关联说明：Agent审计任务ID。关联 Agent 审计任务、扫描明细、规则命中、发布闸门结果。 |
| `task_id` | `UInt64 default audit_task_id` | 任务ID兼容 | 任务ID兼容。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `issue_code` | `String default ''` | 问题编码 | 问题编码 |
| `issue_name` | `String default ''` | 问题名称 | 问题名称 |
| `issue_level` | `LowCardinality(String) default ''` | 问题等级 | 问题等级 |
| `issue_type` | `LowCardinality(String) default ''` | 问题类型 | 问题类型 |
| `module_name` | `String default ''` | 模块名称 | 模块名称 |
| `file_path` | `String default ''` | 文件路径 | 文件路径 |
| `line_no` | `UInt32 default 0` | 行号 | 行号 |
| `release_blocker` | `UInt8 default 0` | 是否发布阻断 | 是否发布阻断 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 28. `astock_analysis.agent_audit_data_lineage_detail`

- **表注释**：Agent字段血缘审计明细表
- **所属分组**：Agent审计明细
- **表用途**：Agent字段血缘审计明细表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 AgentAuditExecutor 写入，用于代码扫描、字段血缘、规则命中、发布闸门。
- **关键关联字段**：`trade_date`, `market_scope`, `audit_task_id`, `task_id`, `rule_version_id`, `page_code`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 审计日期 | 审计日期。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `audit_task_id` | `UInt64 default 0` | 审计任务ID | 审计任务ID。关联说明：Agent审计任务ID。关联 Agent 审计任务、扫描明细、规则命中、发布闸门结果。 |
| `task_id` | `UInt64 default audit_task_id` | 任务ID兼容 | 任务ID兼容。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `page_code` | `String default ''` | 页面编码 | 页面编码。关联说明：页面编码。用于页面字段血缘、数据质量、页面契约和前端页面关联。 |
| `vo_class_name` | `String default ''` | VO类名 | VO类名 |
| `field_name` | `String default ''` | 字段名 | 字段名 |
| `source_table` | `String default ''` | 来源表 | 来源表 |
| `source_column` | `String default ''` | 来源列 | 来源列 |
| `lineage_status` | `LowCardinality(String) default ''` | 血缘状态 | 血缘状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `issue_level` | `LowCardinality(String) default ''` | 问题等级 | 问题等级 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 29. `astock_analysis.agent_audit_rule_hit_detail`

- **表注释**：Agent审计规则命中明细表
- **所属分组**：Agent审计明细
- **表用途**：Agent审计规则命中明细表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 AgentAuditExecutor 写入，用于代码扫描、字段血缘、规则命中、发布闸门。
- **关键关联字段**：`trade_date`, `market_scope`, `audit_task_id`, `task_id`, `rule_version_id`, `rule_code`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 审计日期 | 审计日期。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `audit_task_id` | `UInt64 default 0` | 审计任务ID | 审计任务ID。关联说明：Agent审计任务ID。关联 Agent 审计任务、扫描明细、规则命中、发布闸门结果。 |
| `task_id` | `UInt64 default audit_task_id` | 任务ID兼容 | 任务ID兼容。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `rule_code` | `String default ''` | 规则编码 | 规则编码。关联说明：规则编码。MySQL规则定义、规则版本、规则发布检查、各类Engine规则版本通过该字段关联。 |
| `rule_name` | `String default ''` | 规则名称 | 规则名称 |
| `hit_status` | `LowCardinality(String) default ''` | 命中状态 | 命中状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `hit_count` | `UInt32 default 0` | 命中数 | 命中数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `blocker_count` | `UInt32 default 0` | 阻断数 | 阻断数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 30. `astock_analysis.agent_audit_release_gate_detail`

- **表注释**：Agent发布闸门检查明细表
- **所属分组**：Agent审计明细
- **表用途**：Agent发布闸门检查明细表。
- **数据粒度**：交易日-市场范围级，一行表示某市场范围在某交易日的聚合快照。
- **数据写入来源**：由 AgentAuditExecutor 写入，用于代码扫描、字段血缘、规则命中、发布闸门。
- **关键关联字段**：`trade_date`, `market_scope`, `audit_task_id`, `task_id`, `rule_version_id`, `gate_code`, `created_at`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 审计日期 | 审计日期。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `audit_task_id` | `UInt64 default 0` | 审计任务ID | 审计任务ID。关联说明：Agent审计任务ID。关联 Agent 审计任务、扫描明细、规则命中、发布闸门结果。 |
| `task_id` | `UInt64 default audit_task_id` | 任务ID兼容 | 任务ID兼容。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `gate_code` | `String default ''` | 闸门编码 | 闸门编码 |
| `gate_name` | `String default ''` | 闸门名称 | 闸门名称 |
| `gate_status` | `LowCardinality(String) default ''` | 闸门状态 | 闸门状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `passed` | `UInt8 default 0` | 是否通过 | 是否通过 |
| `issue_count` | `UInt32 default 0` | 问题数 | 问题数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `blocker_count` | `UInt32 default 0` | 阻断数 | 阻断数。统计数量字段，通常由日快照聚合或Engine计算产生。 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。

## 31. `astock_analysis.historical_cycle_sample_factor`

- **表注释**：历史周期样本因子明细表
- **所属分组**：情绪周期与历史样本
- **表用途**：历史周期样本因子明细表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由ClickHouse分析任务、Engine输出或数据导入任务写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `sample_id`, `report_id`, `stock_code`, `stock_name`, `sector_code`, `sector_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `sample_id` | `UInt64 default 0` | 样本ID | 样本ID |
| `report_id` | `UInt64 default 0` | 报告ID | 报告ID。关联说明：报告ID。关联回测报告、回测明细、分层统计、失败样本等。 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `sector_code` | `String default ''` | 板块编码 | 板块编码。关联说明：板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | `String default ''` | 板块名称 | 板块名称。关联说明：板块名称。展示字段，真实关联优先使用 sector_code。 |
| `theme_code` | `String default ''` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default ''` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `String default ''` | 模式编码 | 模式编码 |
| `pattern_name` | `String default ''` | 模式名称 | 模式名称 |
| `risk_code` | `String default ''` | 风险编码 | 风险编码 |
| `risk_name` | `String default ''` | 风险名称 | 风险名称 |
| `stage_code` | `LowCardinality(String) default ''` | 阶段编码 | 阶段编码 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `match_type` | `LowCardinality(String) default ''` | 匹配类型 | 匹配类型 |
| `status` | `LowCardinality(String) default ''` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `rank_no` | `UInt32 default 0` | 排名 | 排名 |
| `score` | `Decimal(18,6) default 0` | 通用评分 | 通用评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `strength_score` | `Decimal(18,6) default 0` | 强度分 | 强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `similarity_score` | `Decimal(18,6) default 0` | 相似度 | 相似度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_score` | `Decimal(18,6) default 0` | 风险分 | 风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `win_rate` | `Decimal(18,6) default 0` | 胜率 | 胜率 |
| `avg_return` | `Decimal(18,6) default 0` | 平均收益 | 平均收益 |
| `avg_drawdown` | `Decimal(18,6) default 0` | 平均回撤 | 平均回撤 |
| `future_1d_return` | `Decimal(18,6) default 0` | 未来1日收益，仅历史/回测使用 | 未来1日收益，仅历史/回测使用 |
| `future_3d_return` | `Decimal(18,6) default 0` | 未来3日收益，仅历史/回测使用 | 未来3日收益，仅历史/回测使用 |
| `future_5d_return` | `Decimal(18,6) default 0` | 未来5日收益，仅历史/回测使用 | 未来5日收益，仅历史/回测使用 |
| `future_10d_return` | `Decimal(18,6) default 0` | 未来10日收益，仅历史/回测使用 | 未来10日收益，仅历史/回测使用 |
| `max_drawdown` | `Decimal(18,6) default 0` | 最大回撤，仅历史/回测使用 | 最大回撤，仅历史/回测使用 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 32. `astock_analysis.historical_following_performance`

- **表注释**：历史样本后续表现表
- **所属分组**：情绪周期与历史样本
- **表用途**：历史样本后续表现表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由ClickHouse分析任务、Engine输出或数据导入任务写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `sample_id`, `report_id`, `stock_code`, `stock_name`, `sector_code`, `sector_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `sample_id` | `UInt64 default 0` | 样本ID | 样本ID |
| `report_id` | `UInt64 default 0` | 报告ID | 报告ID。关联说明：报告ID。关联回测报告、回测明细、分层统计、失败样本等。 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `sector_code` | `String default ''` | 板块编码 | 板块编码。关联说明：板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | `String default ''` | 板块名称 | 板块名称。关联说明：板块名称。展示字段，真实关联优先使用 sector_code。 |
| `theme_code` | `String default ''` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default ''` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `String default ''` | 模式编码 | 模式编码 |
| `pattern_name` | `String default ''` | 模式名称 | 模式名称 |
| `risk_code` | `String default ''` | 风险编码 | 风险编码 |
| `risk_name` | `String default ''` | 风险名称 | 风险名称 |
| `stage_code` | `LowCardinality(String) default ''` | 阶段编码 | 阶段编码 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `match_type` | `LowCardinality(String) default ''` | 匹配类型 | 匹配类型 |
| `status` | `LowCardinality(String) default ''` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `rank_no` | `UInt32 default 0` | 排名 | 排名 |
| `score` | `Decimal(18,6) default 0` | 通用评分 | 通用评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `strength_score` | `Decimal(18,6) default 0` | 强度分 | 强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `similarity_score` | `Decimal(18,6) default 0` | 相似度 | 相似度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_score` | `Decimal(18,6) default 0` | 风险分 | 风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `win_rate` | `Decimal(18,6) default 0` | 胜率 | 胜率 |
| `avg_return` | `Decimal(18,6) default 0` | 平均收益 | 平均收益 |
| `avg_drawdown` | `Decimal(18,6) default 0` | 平均回撤 | 平均回撤 |
| `future_1d_return` | `Decimal(18,6) default 0` | 未来1日收益，仅历史/回测使用 | 未来1日收益，仅历史/回测使用 |
| `future_3d_return` | `Decimal(18,6) default 0` | 未来3日收益，仅历史/回测使用 | 未来3日收益，仅历史/回测使用 |
| `future_5d_return` | `Decimal(18,6) default 0` | 未来5日收益，仅历史/回测使用 | 未来5日收益，仅历史/回测使用 |
| `future_10d_return` | `Decimal(18,6) default 0` | 未来10日收益，仅历史/回测使用 | 未来10日收益，仅历史/回测使用 |
| `max_drawdown` | `Decimal(18,6) default 0` | 最大回撤，仅历史/回测使用 | 最大回撤，仅历史/回测使用 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 33. `astock_analysis.leader_similarity_match`

- **表注释**：龙头相似匹配表
- **所属分组**：历史相似行情匹配
- **表用途**：龙头相似匹配表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由 SimilarityMatchEngine 进行T日相似度匹配后写入；T日匹配不得读取 future_* 字段。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `sample_id`, `report_id`, `stock_code`, `stock_name`, `sector_code`, `sector_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `sample_id` | `UInt64 default 0` | 样本ID | 样本ID |
| `report_id` | `UInt64 default 0` | 报告ID | 报告ID。关联说明：报告ID。关联回测报告、回测明细、分层统计、失败样本等。 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `sector_code` | `String default ''` | 板块编码 | 板块编码。关联说明：板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | `String default ''` | 板块名称 | 板块名称。关联说明：板块名称。展示字段，真实关联优先使用 sector_code。 |
| `theme_code` | `String default ''` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default ''` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `String default ''` | 模式编码 | 模式编码 |
| `pattern_name` | `String default ''` | 模式名称 | 模式名称 |
| `risk_code` | `String default ''` | 风险编码 | 风险编码 |
| `risk_name` | `String default ''` | 风险名称 | 风险名称 |
| `stage_code` | `LowCardinality(String) default ''` | 阶段编码 | 阶段编码 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `match_type` | `LowCardinality(String) default ''` | 匹配类型 | 匹配类型 |
| `status` | `LowCardinality(String) default ''` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `rank_no` | `UInt32 default 0` | 排名 | 排名 |
| `score` | `Decimal(18,6) default 0` | 通用评分 | 通用评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `strength_score` | `Decimal(18,6) default 0` | 强度分 | 强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `similarity_score` | `Decimal(18,6) default 0` | 相似度 | 相似度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_score` | `Decimal(18,6) default 0` | 风险分 | 风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `win_rate` | `Decimal(18,6) default 0` | 胜率 | 胜率 |
| `avg_return` | `Decimal(18,6) default 0` | 平均收益 | 平均收益 |
| `avg_drawdown` | `Decimal(18,6) default 0` | 平均回撤 | 平均回撤 |
| `future_1d_return` | `Decimal(18,6) default 0` | 未来1日收益，仅历史/回测使用 | 未来1日收益，仅历史/回测使用 |
| `future_3d_return` | `Decimal(18,6) default 0` | 未来3日收益，仅历史/回测使用 | 未来3日收益，仅历史/回测使用 |
| `future_5d_return` | `Decimal(18,6) default 0` | 未来5日收益，仅历史/回测使用 | 未来5日收益，仅历史/回测使用 |
| `future_10d_return` | `Decimal(18,6) default 0` | 未来10日收益，仅历史/回测使用 | 未来10日收益，仅历史/回测使用 |
| `max_drawdown` | `Decimal(18,6) default 0` | 最大回撤，仅历史/回测使用 | 最大回撤，仅历史/回测使用 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 34. `astock_analysis.mainline_similarity_match`

- **表注释**：主线相似匹配表
- **所属分组**：历史相似行情匹配
- **表用途**：主线相似匹配表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由 SimilarityMatchEngine 进行T日相似度匹配后写入；T日匹配不得读取 future_* 字段。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `sample_id`, `report_id`, `stock_code`, `stock_name`, `sector_code`, `sector_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `sample_id` | `UInt64 default 0` | 样本ID | 样本ID |
| `report_id` | `UInt64 default 0` | 报告ID | 报告ID。关联说明：报告ID。关联回测报告、回测明细、分层统计、失败样本等。 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `sector_code` | `String default ''` | 板块编码 | 板块编码。关联说明：板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | `String default ''` | 板块名称 | 板块名称。关联说明：板块名称。展示字段，真实关联优先使用 sector_code。 |
| `theme_code` | `String default ''` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default ''` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `String default ''` | 模式编码 | 模式编码 |
| `pattern_name` | `String default ''` | 模式名称 | 模式名称 |
| `risk_code` | `String default ''` | 风险编码 | 风险编码 |
| `risk_name` | `String default ''` | 风险名称 | 风险名称 |
| `stage_code` | `LowCardinality(String) default ''` | 阶段编码 | 阶段编码 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `match_type` | `LowCardinality(String) default ''` | 匹配类型 | 匹配类型 |
| `status` | `LowCardinality(String) default ''` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `rank_no` | `UInt32 default 0` | 排名 | 排名 |
| `score` | `Decimal(18,6) default 0` | 通用评分 | 通用评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `strength_score` | `Decimal(18,6) default 0` | 强度分 | 强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `similarity_score` | `Decimal(18,6) default 0` | 相似度 | 相似度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_score` | `Decimal(18,6) default 0` | 风险分 | 风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `win_rate` | `Decimal(18,6) default 0` | 胜率 | 胜率 |
| `avg_return` | `Decimal(18,6) default 0` | 平均收益 | 平均收益 |
| `avg_drawdown` | `Decimal(18,6) default 0` | 平均回撤 | 平均回撤 |
| `future_1d_return` | `Decimal(18,6) default 0` | 未来1日收益，仅历史/回测使用 | 未来1日收益，仅历史/回测使用 |
| `future_3d_return` | `Decimal(18,6) default 0` | 未来3日收益，仅历史/回测使用 | 未来3日收益，仅历史/回测使用 |
| `future_5d_return` | `Decimal(18,6) default 0` | 未来5日收益，仅历史/回测使用 | 未来5日收益，仅历史/回测使用 |
| `future_10d_return` | `Decimal(18,6) default 0` | 未来10日收益，仅历史/回测使用 | 未来10日收益，仅历史/回测使用 |
| `max_drawdown` | `Decimal(18,6) default 0` | 最大回撤，仅历史/回测使用 | 最大回撤，仅历史/回测使用 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 35. `astock_analysis.pattern_backtest_result`

- **表注释**：模式回测结果表
- **所属分组**：模式条件与回测结果
- **表用途**：模式回测结果表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由 PatternConditionEngine 或回测任务写入，只表达研究条件状态，不输出行动建议。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `sample_id`, `report_id`, `stock_code`, `stock_name`, `sector_code`, `sector_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `sample_id` | `UInt64 default 0` | 样本ID | 样本ID |
| `report_id` | `UInt64 default 0` | 报告ID | 报告ID。关联说明：报告ID。关联回测报告、回测明细、分层统计、失败样本等。 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `sector_code` | `String default ''` | 板块编码 | 板块编码。关联说明：板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | `String default ''` | 板块名称 | 板块名称。关联说明：板块名称。展示字段，真实关联优先使用 sector_code。 |
| `theme_code` | `String default ''` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default ''` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `String default ''` | 模式编码 | 模式编码 |
| `pattern_name` | `String default ''` | 模式名称 | 模式名称 |
| `risk_code` | `String default ''` | 风险编码 | 风险编码 |
| `risk_name` | `String default ''` | 风险名称 | 风险名称 |
| `stage_code` | `LowCardinality(String) default ''` | 阶段编码 | 阶段编码 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `match_type` | `LowCardinality(String) default ''` | 匹配类型 | 匹配类型 |
| `status` | `LowCardinality(String) default ''` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `rank_no` | `UInt32 default 0` | 排名 | 排名 |
| `score` | `Decimal(18,6) default 0` | 通用评分 | 通用评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `strength_score` | `Decimal(18,6) default 0` | 强度分 | 强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `similarity_score` | `Decimal(18,6) default 0` | 相似度 | 相似度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_score` | `Decimal(18,6) default 0` | 风险分 | 风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `win_rate` | `Decimal(18,6) default 0` | 胜率 | 胜率 |
| `avg_return` | `Decimal(18,6) default 0` | 平均收益 | 平均收益 |
| `avg_drawdown` | `Decimal(18,6) default 0` | 平均回撤 | 平均回撤 |
| `future_1d_return` | `Decimal(18,6) default 0` | 未来1日收益，仅历史/回测使用 | 未来1日收益，仅历史/回测使用 |
| `future_3d_return` | `Decimal(18,6) default 0` | 未来3日收益，仅历史/回测使用 | 未来3日收益，仅历史/回测使用 |
| `future_5d_return` | `Decimal(18,6) default 0` | 未来5日收益，仅历史/回测使用 | 未来5日收益，仅历史/回测使用 |
| `future_10d_return` | `Decimal(18,6) default 0` | 未来10日收益，仅历史/回测使用 | 未来10日收益，仅历史/回测使用 |
| `max_drawdown` | `Decimal(18,6) default 0` | 最大回撤，仅历史/回测使用 | 最大回撤，仅历史/回测使用 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 36. `astock_analysis.risk_similarity_match`

- **表注释**：风险相似匹配表
- **所属分组**：历史相似行情匹配
- **表用途**：风险相似匹配表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由 SimilarityMatchEngine 进行T日相似度匹配后写入；T日匹配不得读取 future_* 字段。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `sample_id`, `report_id`, `stock_code`, `stock_name`, `sector_code`, `sector_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `sample_id` | `UInt64 default 0` | 样本ID | 样本ID |
| `report_id` | `UInt64 default 0` | 报告ID | 报告ID。关联说明：报告ID。关联回测报告、回测明细、分层统计、失败样本等。 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `sector_code` | `String default ''` | 板块编码 | 板块编码。关联说明：板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | `String default ''` | 板块名称 | 板块名称。关联说明：板块名称。展示字段，真实关联优先使用 sector_code。 |
| `theme_code` | `String default ''` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default ''` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `String default ''` | 模式编码 | 模式编码 |
| `pattern_name` | `String default ''` | 模式名称 | 模式名称 |
| `risk_code` | `String default ''` | 风险编码 | 风险编码 |
| `risk_name` | `String default ''` | 风险名称 | 风险名称 |
| `stage_code` | `LowCardinality(String) default ''` | 阶段编码 | 阶段编码 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `match_type` | `LowCardinality(String) default ''` | 匹配类型 | 匹配类型 |
| `status` | `LowCardinality(String) default ''` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `rank_no` | `UInt32 default 0` | 排名 | 排名 |
| `score` | `Decimal(18,6) default 0` | 通用评分 | 通用评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `strength_score` | `Decimal(18,6) default 0` | 强度分 | 强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `similarity_score` | `Decimal(18,6) default 0` | 相似度 | 相似度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_score` | `Decimal(18,6) default 0` | 风险分 | 风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `win_rate` | `Decimal(18,6) default 0` | 胜率 | 胜率 |
| `avg_return` | `Decimal(18,6) default 0` | 平均收益 | 平均收益 |
| `avg_drawdown` | `Decimal(18,6) default 0` | 平均回撤 | 平均回撤 |
| `future_1d_return` | `Decimal(18,6) default 0` | 未来1日收益，仅历史/回测使用 | 未来1日收益，仅历史/回测使用 |
| `future_3d_return` | `Decimal(18,6) default 0` | 未来3日收益，仅历史/回测使用 | 未来3日收益，仅历史/回测使用 |
| `future_5d_return` | `Decimal(18,6) default 0` | 未来5日收益，仅历史/回测使用 | 未来5日收益，仅历史/回测使用 |
| `future_10d_return` | `Decimal(18,6) default 0` | 未来10日收益，仅历史/回测使用 | 未来10日收益，仅历史/回测使用 |
| `max_drawdown` | `Decimal(18,6) default 0` | 最大回撤，仅历史/回测使用 | 最大回撤，仅历史/回测使用 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 37. `astock_analysis.sector_daily_snapshot`

- **表注释**：板块日快照表
- **所属分组**：题材、板块、主线分析
- **表用途**：板块日快照表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由主线/题材/板块聚合任务或 MainlineRecognitionEngine 写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `sample_id`, `report_id`, `stock_code`, `stock_name`, `sector_code`, `sector_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `sample_id` | `UInt64 default 0` | 样本ID | 样本ID |
| `report_id` | `UInt64 default 0` | 报告ID | 报告ID。关联说明：报告ID。关联回测报告、回测明细、分层统计、失败样本等。 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `sector_code` | `String default ''` | 板块编码 | 板块编码。关联说明：板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | `String default ''` | 板块名称 | 板块名称。关联说明：板块名称。展示字段，真实关联优先使用 sector_code。 |
| `theme_code` | `String default ''` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default ''` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `String default ''` | 模式编码 | 模式编码 |
| `pattern_name` | `String default ''` | 模式名称 | 模式名称 |
| `risk_code` | `String default ''` | 风险编码 | 风险编码 |
| `risk_name` | `String default ''` | 风险名称 | 风险名称 |
| `stage_code` | `LowCardinality(String) default ''` | 阶段编码 | 阶段编码 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `match_type` | `LowCardinality(String) default ''` | 匹配类型 | 匹配类型 |
| `status` | `LowCardinality(String) default ''` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `rank_no` | `UInt32 default 0` | 排名 | 排名 |
| `score` | `Decimal(18,6) default 0` | 通用评分 | 通用评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `strength_score` | `Decimal(18,6) default 0` | 强度分 | 强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `similarity_score` | `Decimal(18,6) default 0` | 相似度 | 相似度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_score` | `Decimal(18,6) default 0` | 风险分 | 风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `win_rate` | `Decimal(18,6) default 0` | 胜率 | 胜率 |
| `avg_return` | `Decimal(18,6) default 0` | 平均收益 | 平均收益 |
| `avg_drawdown` | `Decimal(18,6) default 0` | 平均回撤 | 平均回撤 |
| `future_1d_return` | `Decimal(18,6) default 0` | 未来1日收益，仅历史/回测使用 | 未来1日收益，仅历史/回测使用 |
| `future_3d_return` | `Decimal(18,6) default 0` | 未来3日收益，仅历史/回测使用 | 未来3日收益，仅历史/回测使用 |
| `future_5d_return` | `Decimal(18,6) default 0` | 未来5日收益，仅历史/回测使用 | 未来5日收益，仅历史/回测使用 |
| `future_10d_return` | `Decimal(18,6) default 0` | 未来10日收益，仅历史/回测使用 | 未来10日收益，仅历史/回测使用 |
| `max_drawdown` | `Decimal(18,6) default 0` | 最大回撤，仅历史/回测使用 | 最大回撤，仅历史/回测使用 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 38. `astock_analysis.sector_stock_mapping_snapshot`

- **表注释**：板块股票映射快照表
- **所属分组**：题材、板块、主线分析
- **表用途**：板块股票映射快照表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由主线/题材/板块聚合任务或 MainlineRecognitionEngine 写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `sample_id`, `report_id`, `stock_code`, `stock_name`, `sector_code`, `sector_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `sample_id` | `UInt64 default 0` | 样本ID | 样本ID |
| `report_id` | `UInt64 default 0` | 报告ID | 报告ID。关联说明：报告ID。关联回测报告、回测明细、分层统计、失败样本等。 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `sector_code` | `String default ''` | 板块编码 | 板块编码。关联说明：板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | `String default ''` | 板块名称 | 板块名称。关联说明：板块名称。展示字段，真实关联优先使用 sector_code。 |
| `theme_code` | `String default ''` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default ''` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `String default ''` | 模式编码 | 模式编码 |
| `pattern_name` | `String default ''` | 模式名称 | 模式名称 |
| `risk_code` | `String default ''` | 风险编码 | 风险编码 |
| `risk_name` | `String default ''` | 风险名称 | 风险名称 |
| `stage_code` | `LowCardinality(String) default ''` | 阶段编码 | 阶段编码 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `match_type` | `LowCardinality(String) default ''` | 匹配类型 | 匹配类型 |
| `status` | `LowCardinality(String) default ''` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `rank_no` | `UInt32 default 0` | 排名 | 排名 |
| `score` | `Decimal(18,6) default 0` | 通用评分 | 通用评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `strength_score` | `Decimal(18,6) default 0` | 强度分 | 强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `similarity_score` | `Decimal(18,6) default 0` | 相似度 | 相似度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_score` | `Decimal(18,6) default 0` | 风险分 | 风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `win_rate` | `Decimal(18,6) default 0` | 胜率 | 胜率 |
| `avg_return` | `Decimal(18,6) default 0` | 平均收益 | 平均收益 |
| `avg_drawdown` | `Decimal(18,6) default 0` | 平均回撤 | 平均回撤 |
| `future_1d_return` | `Decimal(18,6) default 0` | 未来1日收益，仅历史/回测使用 | 未来1日收益，仅历史/回测使用 |
| `future_3d_return` | `Decimal(18,6) default 0` | 未来3日收益，仅历史/回测使用 | 未来3日收益，仅历史/回测使用 |
| `future_5d_return` | `Decimal(18,6) default 0` | 未来5日收益，仅历史/回测使用 | 未来5日收益，仅历史/回测使用 |
| `future_10d_return` | `Decimal(18,6) default 0` | 未来10日收益，仅历史/回测使用 | 未来10日收益，仅历史/回测使用 |
| `max_drawdown` | `Decimal(18,6) default 0` | 最大回撤，仅历史/回测使用 | 最大回撤，仅历史/回测使用 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

## 39. `astock_analysis.trend_leader_snapshot`

- **表注释**：趋势龙头快照表
- **所属分组**：龙头梯队与反馈
- **表用途**：趋势龙头快照表。
- **数据粒度**：交易日-股票级，一行表示某股票在某交易日的快照或分析结果。
- **数据写入来源**：由 LeaderRecognitionEngine 根据个股强度、主线关系、带动性和反馈计算写入。
- **关键关联字段**：`trade_date`, `market_scope`, `task_id`, `rule_version_id`, `sample_id`, `report_id`, `stock_code`, `stock_name`, `sector_code`, `sector_name`, `theme_code`, `theme_name`

### 字段说明

| 字段 | 类型/定义 | DDL注释 | 业务含义与关联说明 |
|---|---|---|---|
| `trade_date` | `Date` | 交易日 | 交易日。关联说明：核心时间维度。ClickHouse 大多数快照按交易日关联；页面查询、Engine跑批、数据质量检查都以该字段过滤。 |
| `market_scope` | `LowCardinality(String) default 'A_SHARE'` | 市场范围 | 市场范围。关联说明：市场范围维度。当前默认 A_SHARE，用于区分A股全市场、指数范围、可扩展市场范围。 |
| `task_id` | `UInt64 default 0` | 任务ID | 任务ID。关联说明：任务ID。Engine运行、回测任务、审计任务与输出快照的追踪字段。 |
| `rule_version_id` | `UInt64 default 0` | 规则版本ID | 规则版本ID。关联说明：规则版本ID。Engine输出快照常带该字段，用于追溯本次计算使用的规则版本。 |
| `sample_id` | `UInt64 default 0` | 样本ID | 样本ID |
| `report_id` | `UInt64 default 0` | 报告ID | 报告ID。关联说明：报告ID。关联回测报告、回测明细、分层统计、失败样本等。 |
| `stock_code` | `String default ''` | 股票代码 | 股票代码。关联说明：股票代码。关联 stock_daily_kline、leader_*、risk_*、pattern_* 等股票级快照。 |
| `stock_name` | `String default ''` | 股票名称 | 股票名称。关联说明：股票名称。展示字段，通常不作为主关联键，真实关联优先使用 stock_code。 |
| `sector_code` | `String default ''` | 板块编码 | 板块编码。关联说明：板块编码。关联 sector_*、stock_daily_kline、theme/mainline 等板块维度数据。 |
| `sector_name` | `String default ''` | 板块名称 | 板块名称。关联说明：板块名称。展示字段，真实关联优先使用 sector_code。 |
| `theme_code` | `String default ''` | 题材编码 | 题材编码。关联说明：题材编码。关联 theme_definition、theme_stock_mapping、theme_* 快照。 |
| `theme_name` | `String default ''` | 题材名称 | 题材名称。关联说明：题材名称。展示字段，真实关联优先使用 theme_code。 |
| `mainline_code` | `String default ''` | 主线编码 | 主线编码。关联说明：主线编码。关联 mainline_*、leader_*、theme_* 数据，是主线雷达/主线切换的重要键。 |
| `mainline_name` | `String default ''` | 主线名称 | 主线名称。关联说明：主线名称。展示字段，真实关联优先使用 mainline_code。 |
| `pattern_code` | `String default ''` | 模式编码 | 模式编码 |
| `pattern_name` | `String default ''` | 模式名称 | 模式名称 |
| `risk_code` | `String default ''` | 风险编码 | 风险编码 |
| `risk_name` | `String default ''` | 风险名称 | 风险名称 |
| `stage_code` | `LowCardinality(String) default ''` | 阶段编码 | 阶段编码 |
| `leader_type` | `LowCardinality(String) default ''` | 龙头类型 | 龙头类型 |
| `match_type` | `LowCardinality(String) default ''` | 匹配类型 | 匹配类型 |
| `status` | `LowCardinality(String) default ''` | 状态 | 状态。状态字段，应有枚举约束文档，例如 DRAFT/ACTIVE/SUCCESS/FAILED/PASSED/MISSING 等。 |
| `rank_no` | `UInt32 default 0` | 排名 | 排名 |
| `score` | `Decimal(18,6) default 0` | 通用评分 | 通用评分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `strength_score` | `Decimal(18,6) default 0` | 强度分 | 强度分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `similarity_score` | `Decimal(18,6) default 0` | 相似度 | 相似度。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `risk_score` | `Decimal(18,6) default 0` | 风险分 | 风险分。评分/量化字段，通常由Engine根据规则版本计算产生，用于页面排序、状态判断或风险提示。 |
| `win_rate` | `Decimal(18,6) default 0` | 胜率 | 胜率 |
| `avg_return` | `Decimal(18,6) default 0` | 平均收益 | 平均收益 |
| `avg_drawdown` | `Decimal(18,6) default 0` | 平均回撤 | 平均回撤 |
| `future_1d_return` | `Decimal(18,6) default 0` | 未来1日收益，仅历史/回测使用 | 未来1日收益，仅历史/回测使用 |
| `future_3d_return` | `Decimal(18,6) default 0` | 未来3日收益，仅历史/回测使用 | 未来3日收益，仅历史/回测使用 |
| `future_5d_return` | `Decimal(18,6) default 0` | 未来5日收益，仅历史/回测使用 | 未来5日收益，仅历史/回测使用 |
| `future_10d_return` | `Decimal(18,6) default 0` | 未来10日收益，仅历史/回测使用 | 未来10日收益，仅历史/回测使用 |
| `max_drawdown` | `Decimal(18,6) default 0` | 最大回撤，仅历史/回测使用 | 最大回撤，仅历史/回测使用 |
| `evidence_json` | `String default '{}'` | 证据JSON | 证据JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `risk_json` | `String default '{}'` | 风险JSON | 风险JSON。一般存JSON结构，用于保存可扩展参数、证据、结果或特征。上线前应避免把核心高频查询字段长期只放在JSON里。 |
| `features` | `String default '{}'` | 扩展字段JSON | 扩展字段JSON。关联说明：扩展字段。用于存放短期扩展属性或低频业务特征，正式高频字段应沉淀为物理列。 |
| `created_at` | `DateTime default now()` | 创建时间 | 创建时间。关联说明：创建时间。记录数据首次写入时间，用于审计和排障。 |

### 评审关注点

- 包含 `features` 扩展字段，适合短期扩展；若字段成为高频查询条件，应沉淀为物理列。
- 包含 `trade_date`，应作为查询过滤条件；ClickHouse中通常也参与分区/排序。
- 包含 `market_scope`，当前默认 A_SHARE；未来扩展指数范围、全市场范围时要保持枚举一致。
- 包含 `rule_version_id`，可以追溯Engine结果使用的规则版本，是可解释性关键字段。
- 包含 `task_id`，可以追踪批处理/回测/审计任务来源。
- 股票级表应优先通过 `trade_date + stock_code` 查询，`stock_name` 只作为展示冗余。

# 4. 典型业务链路怎么串表

## 4.1 页面渲染链路

```text
前端页面 -> 后端 Controller -> PageDataAggregator -> Repository/SQL -> MySQL规则/任务 + ClickHouse快照 -> PageVO -> 前端组件渲染
```

关键字段：`page_code`、`trade_date`、`market_scope`。页面是否可展示完整可信结论，要看 `data_quality_check_log` 与相关 ClickHouse 快照是否完整。

## 4.2 Engine 跑批链路

```text
engine_batch_run_log
  -> engine_batch_step_log
  -> algorithm_task_log
  -> ClickHouse各类Engine输出快照
```

关键字段：`task_id`、`trade_date`、`market_scope`、`rule_version_id`。

## 4.3 规则版本链路

```text
rule_definition
  -> rule_version
  -> rule_version_audit_log / rule_publish_check_log
  -> 各Engine规则版本表
  -> ClickHouse输出快照.rule_version_id
```

关键字段：`rule_code`、`version_no`、`rule_version_id`。

## 4.4 情绪周期与相似匹配链路

```text
stock_daily_kline + market_factor_snapshot + limit_up_down_ecology_snapshot
  -> emotion_stage_snapshot + emotion_stage_score_detail
  -> historical_cycle_sample / historical_cycle_sample_factor
  -> historical_similarity_match / historical_similarity_factor_detail
```

关键红线：T日相似匹配不能读取 `future_*` 字段；`future_*` 只能在历史样本复盘、回测评价阶段使用。

## 4.5 主线、板块、龙头链路

```text
stock_daily_kline
  -> theme_daily_snapshot / sector_strength_snapshot / mainline_daily_snapshot
  -> leader_daily_snapshot / leader_ladder_snapshot / leader_drive_snapshot
  -> leader_negative_feedback / risk_signal_snapshot
```

关键字段：`theme_code`、`sector_code`、`mainline_code`、`stock_code`。

## 4.6 回测链路

```text
backtest_task + backtest_task_param + backtest_rule_binding
  -> backtest_signal_detail
  -> backtest_performance_detail
  -> backtest_layer_stat
  -> backtest_failure_case
  -> backtest_report
```

关键字段：`task_id`、`report_id`、`rule_version_id`、`trade_date`。

## 4.7 Agent审计链路

```text
agent_audit_task
  -> agent_audit_code_scan_detail
  -> agent_audit_data_lineage_detail
  -> agent_audit_rule_hit_detail
  -> agent_audit_release_gate_detail
  -> agent_audit_result / agent_release_gate_check / agent_audit_issue
```

关键字段：`audit_task_id`、`gate_code`、`rule_code`、`page_code`。

# 5. 当前评审结论

1. **两库职责基本清晰**：MySQL管规则、配置、任务、审计；ClickHouse管行情事实、Engine输出、分析明细。
2. **核心关联字段明确**：`trade_date`、`market_scope`、`stock_code`、`sector_code`、`theme_code`、`mainline_code`、`rule_version_id`、`task_id`、`audit_task_id` 是全系统最关键的字段。
3. **需要后续继续补强**：真实数据采集、字段枚举字典、更多唯一约束/索引评审、Python回测写入协议、样本数据真实性校验。
4. **不要把种子数据误认为真实行情**：当前 `seed_minimal_*` 只是页面联调样本，后续必须接入真实采集或导入任务。
