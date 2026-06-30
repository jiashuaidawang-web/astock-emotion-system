-- 第十步：Engine一键跑批批次日志表
-- 数据库：MySQL 8.0+
-- 执行前：use astock_business;

create table if not exists engine_batch_run_log (
    id bigint primary key auto_increment comment '批次ID',
    trade_date date not null comment '交易日',
    market_scope varchar(64) not null default 'A_SHARE' comment '市场范围',
    batch_status varchar(64) not null comment '批次状态：RUNNING/SUCCESS/FAILED',
    started_at datetime not null comment '开始时间',
    finished_at datetime null comment '结束时间',
    cost_millis bigint null comment '耗时毫秒',
    total_step_count int null comment '总步骤数',
    success_step_count int null comment '成功步骤数',
    failed_step_count int null comment '失败步骤数',
    request_json json null comment '请求JSON',
    failure_reason text null comment '失败原因',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    key idx_engine_batch_run_date (trade_date, market_scope, batch_status),
    key idx_engine_batch_run_started (started_at)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='Engine一键跑批批次日志表';

create table if not exists engine_batch_step_log (
    id bigint primary key auto_increment comment '步骤日志ID',
    batch_id bigint not null comment '批次ID',
    step_no int not null comment '步骤序号',
    step_code varchar(128) not null comment '步骤编码',
    step_name varchar(255) not null comment '步骤名称',
    engine_name varchar(255) not null comment '引擎名称',
    step_status varchar(64) not null comment '步骤状态：SUCCESS/FAILED',
    task_id bigint null comment '底层算法任务ID',
    output_row_count int null comment '输出行数',
    output_tables varchar(1000) null comment '输出表列表',
    failure_reason text null comment '失败原因',
    summary_text text null comment '摘要',
    started_at datetime not null comment '开始时间',
    finished_at datetime null comment '结束时间',
    cost_millis bigint null comment '耗时毫秒',
    features json null comment '扩展字段',
    created_at datetime not null default current_timestamp comment '创建时间',
    updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    is_deleted tinyint not null default 0 comment '逻辑删除：0否1是',
    key idx_engine_batch_step_batch (batch_id, step_no),
    key idx_engine_batch_step_status (step_status, engine_name),
    key idx_engine_batch_step_task (task_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_unicode_ci comment='Engine一键跑批步骤日志表';
