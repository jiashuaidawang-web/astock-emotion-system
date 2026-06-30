-- 第八步：MySQL业务库完整DDL
-- 目标：与当前 Java Engine / Repository / Mapper / AgentAudit / 字段血缘闭环对齐
-- 数据库：MySQL 8.0+
-- 执行建议：
--   create database astock_business default character set utf8mb4 collate utf8mb4_unicode_ci;
--   use astock_business;
--   source sql/20_mysql_full_schema.sql;

set names utf8mb4;
set foreign_key_checks = 0;

create table if not exists rule_definition (
    id bigint primary key auto_increment comment '主键ID',
    rule_code varchar(128) not null comment '规则编码',
    rule_name varchar(255) not null comment '规则名称',
    rule_category varchar(64) not null comment '规则分类',
    rule_desc varchar(1000) null comment '规则说明',
    rule_owner varchar(128) null comment '规则负责人',
    rule_status varchar(32) not null default 'ENABLED' comment '规则状态',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    unique key uk_rule_definition_code (rule_code),
    key idx_rule_definition_category (rule_category, rule_status, is_deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='规则定义表';

create table if not exists rule_version (
    id bigint primary key auto_increment comment '规则版本ID',
    rule_code varchar(128) not null comment '规则编码',
    rule_name varchar(255) null comment '规则名称',
    version_no varchar(64) not null comment '版本号',
    version_name varchar(255) null comment '版本名称',
    version_status varchar(32) not null default 'DRAFT' comment '版本状态：DRAFT/ACTIVE/ARCHIVED',
    active_flag tinyint not null default 0 comment '是否启用版本',
    rule_content_json json null comment '规则内容JSON',
    param_schema_json json null comment '参数结构JSON',
    publish_check_json json null comment '发布检查JSON',
    remark varchar(1000) null comment '备注',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    unique key uk_rule_version (rule_code, version_no),
    key idx_rule_version_rule_status (rule_code, version_status, is_deleted),
    key idx_rule_version_active (rule_code, active_flag, is_deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='规则版本表';

create table if not exists rule_version_audit_log (
    id bigint primary key auto_increment comment '主键ID',
    rule_code varchar(128) not null comment '规则编码',
    rule_version_id bigint not null comment '规则版本ID',
    operation_type varchar(64) not null comment '操作类型',
    before_json json null comment '变更前JSON',
    after_json json null comment '变更后JSON',
    operator_name varchar(128) null comment '操作人',
    audit_result varchar(32) null comment '审计结果',
    audit_message varchar(1000) null comment '审计信息',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    key idx_rule_version_audit_rule (rule_code, rule_version_id, created_at),
    key idx_rule_version_audit_operation (operation_type, created_at)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='规则版本审计日志表';

create table if not exists algorithm_task_log (
    id bigint primary key auto_increment comment '算法任务ID',
    task_name varchar(255) not null comment '任务名称',
    task_type varchar(128) not null comment '任务类型',
    trade_date date null comment '交易日',
    task_status varchar(64) not null comment '任务状态',
    started_at datetime null comment '开始时间',
    finished_at datetime null comment '结束时间',
    cost_millis bigint null comment '耗时毫秒',
    input_json json null comment '输入参数JSON',
    output_json json null comment '输出JSON',
    failure_reason text null comment '失败原因',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    key idx_algorithm_task_log_type_date (task_type, trade_date, task_status),
    key idx_algorithm_task_log_created (created_at),
    key idx_algorithm_task_log_status (task_status, started_at)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='算法任务执行日志表';

create table if not exists data_quality_check_log (
    id bigint primary key auto_increment comment '主键ID',
    page_code varchar(128) null comment '页面编码或任务编码',
    snapshot_code varchar(128) null comment '快照编码',
    snapshot_table varchar(128) not null comment '快照表名',
    trade_date date not null comment '交易日',
    market_scope varchar(64) not null default 'A_SHARE' comment '市场范围',
    data_complete tinyint not null default 0 comment '数据是否完整',
    completeness_ratio decimal(18,6) null comment '完整率',
    critical tinyint not null default 0 comment '是否关键数据',
    missing_reason varchar(1000) null comment '缺失原因',
    check_status varchar(64) not null default 'CHECKED' comment '检查状态',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    key idx_data_quality_check_log_page_date (page_code, trade_date, is_deleted),
    key idx_data_quality_check_log_snapshot (snapshot_table, trade_date, is_deleted),
    key idx_data_quality_check_log_scope (market_scope, trade_date, data_complete)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='数据完整性检查日志表';

create table if not exists page_contract_field_lineage (
    id bigint primary key auto_increment comment '主键ID',
    page_code varchar(128) not null comment '页面编码',
    vo_class_name varchar(255) not null comment 'VO类名',
    field_name varchar(255) not null comment '字段名',
    source_type varchar(64) not null comment '来源类型：MYSQL/CLICKHOUSE/FORMULA/CONSTANT',
    source_table varchar(128) null comment '来源表',
    source_column varchar(128) null comment '来源列',
    calculation_formula varchar(2000) null comment '计算公式',
    required tinyint not null default 0 comment '是否必填',
    audit_passed tinyint not null default 1 comment '审计是否通过',
    remark varchar(1000) null comment '备注',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    unique key uk_page_field_lineage (page_code, vo_class_name, field_name, source_table, source_column),
    key idx_page_contract_field_lineage_page (page_code, vo_class_name, is_deleted),
    key idx_page_contract_field_lineage_source (source_type, source_table, source_column, is_deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='页面字段血缘契约表';

create table if not exists cycle_sample_confirm (
    id bigint primary key auto_increment comment '主键ID',
    sample_id bigint not null comment '历史样本ID',
    cycle_sample_id bigint null comment '周期样本ID',
    confirmed_stage varchar(64) null comment '人工确认阶段',
    stage_code varchar(64) null comment '阶段编码',
    confirm_status varchar(64) not null default 'CONFIRMED' comment '确认状态',
    confirm_user varchar(128) null comment '确认人',
    confirm_reason varchar(1000) null comment '确认原因',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    key idx_cycle_sample_confirm_sample (sample_id, confirm_status, is_deleted),
    key idx_cycle_sample_confirm_stage (confirmed_stage, is_deleted),
    key idx_cycle_sample_confirm_cycle (cycle_sample_id, is_deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='历史周期样本人工确认表';

create table if not exists manual_stage_adjustment (
    id bigint primary key auto_increment comment '主键ID',
    trade_date date not null comment '交易日',
    market_scope varchar(64) not null default 'A_SHARE' comment '市场范围',
    manual_stage varchar(64) null comment '人工阶段',
    adjusted_stage varchar(64) null comment '修正后阶段',
    stage_code varchar(64) null comment '阶段编码',
    adjust_reason varchar(1000) null comment '修正原因',
    operator_name varchar(128) null comment '操作人',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    key idx_manual_stage_adjustment_date (trade_date, adjusted_stage, is_deleted),
    key idx_manual_stage_adjustment_scope (market_scope, trade_date, is_deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='情绪阶段人工修正表';

create table if not exists pattern_risk_binding (
    id bigint primary key auto_increment comment '主键ID',
    pattern_code varchar(128) not null comment '模式编码',
    risk_code varchar(128) null comment '风险编码',
    risk_action varchar(64) not null comment '风险动作',
    binding_status varchar(64) not null default 'ENABLED' comment '绑定状态',
    remark varchar(1000) null comment '备注',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    key idx_pattern_risk_binding_pattern (pattern_code, risk_action, is_deleted),
    key idx_pattern_risk_binding_risk (risk_code, binding_status, is_deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='模式与风险动作绑定表';

create table if not exists buy_pattern_stage_matrix (
    id bigint primary key auto_increment comment '主键ID',
    pattern_code varchar(128) not null comment '模式编码',
    stage_code varchar(64) not null comment '情绪阶段编码',
    matrix_score decimal(18,6) null comment '矩阵分',
    matrix_status varchar(64) not null default 'ENABLED' comment '矩阵状态',
    remark varchar(1000) null comment '备注',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    key idx_buy_pattern_stage_matrix_pattern_stage (pattern_code, stage_code, is_deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='买点模式与情绪阶段矩阵表';

create table if not exists buy_pattern_rule_config (
    id bigint primary key auto_increment comment '主键ID',
    pattern_code varchar(128) not null comment '模式编码',
    rule_code varchar(128) null comment '规则编码',
    rule_status varchar(64) not null default 'ENABLED' comment '规则状态',
    config_json json null comment '配置JSON',
    remark varchar(1000) null comment '备注',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    key idx_buy_pattern_rule_config_pattern (pattern_code, rule_status, is_deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='买点模式规则配置表';

create table if not exists risk_action_matrix (
    id bigint primary key auto_increment comment '主键ID',
    risk_level varchar(64) not null comment '风险等级',
    signal_level varchar(64) null comment '信号等级',
    risk_action varchar(64) not null comment '风险动作',
    action_priority int not null default 0 comment '动作优先级',
    description varchar(1000) null comment '说明',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    key idx_risk_action_matrix_level (risk_level, risk_action, is_deleted),
    key idx_risk_action_matrix_priority (action_priority)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='风控动作矩阵表';

set foreign_key_checks = 1;
