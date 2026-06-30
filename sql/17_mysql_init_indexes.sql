-- 第七步第十七段：MySQL初始化索引脚本
-- 说明：本脚本使用 IF NOT EXISTS 写法，适配 MySQL 8.0+。
-- 若你的MySQL版本不支持 index if not exists，请先查询 information_schema 再创建。

-- 规则版本
create index if not exists idx_rule_version_rule_status
    on rule_version(rule_code, version_status, is_deleted);

create index if not exists idx_rule_version_active
    on rule_version(rule_code, active_flag, is_deleted);

-- 算法任务日志
create index if not exists idx_algorithm_task_log_type_date
    on algorithm_task_log(task_type, trade_date, task_status);

create index if not exists idx_algorithm_task_log_created
    on algorithm_task_log(created_at);

-- 数据质量检查
create index if not exists idx_data_quality_check_log_page_date
    on data_quality_check_log(page_code, trade_date, is_deleted);

create index if not exists idx_data_quality_check_log_snapshot
    on data_quality_check_log(snapshot_table, trade_date, is_deleted);

-- 页面字段血缘
create index if not exists idx_page_contract_field_lineage_page
    on page_contract_field_lineage(page_code, vo_class_name, is_deleted);

create index if not exists idx_page_contract_field_lineage_source
    on page_contract_field_lineage(source_type, source_table, source_column, is_deleted);

-- 历史周期样本确认
create index if not exists idx_cycle_sample_confirm_sample
    on cycle_sample_confirm(sample_id, confirm_status, is_deleted);

create index if not exists idx_cycle_sample_confirm_stage
    on cycle_sample_confirm(confirmed_stage, is_deleted);

-- 人工阶段修正
create index if not exists idx_manual_stage_adjustment_date
    on manual_stage_adjustment(trade_date, adjusted_stage, is_deleted);

-- 模式风险绑定
create index if not exists idx_pattern_risk_binding_pattern
    on pattern_risk_binding(pattern_code, risk_action, is_deleted);

-- 模式阶段矩阵
create index if not exists idx_buy_pattern_stage_matrix_pattern_stage
    on buy_pattern_stage_matrix(pattern_code, stage_code, is_deleted);

-- 模式规则配置
create index if not exists idx_buy_pattern_rule_config_pattern
    on buy_pattern_rule_config(pattern_code, rule_status, is_deleted);

-- 风控动作矩阵
create index if not exists idx_risk_action_matrix_level
    on risk_action_matrix(risk_level, risk_action, is_deleted);
