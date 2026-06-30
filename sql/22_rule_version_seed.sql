-- 第八步：规则定义与默认启用版本初始化
-- 数据库：MySQL 8.0+
-- 执行前：use astock_business;

insert into rule_definition(rule_code, rule_name, rule_category, rule_desc, rule_status)
values
('EMOTION_STAGE_CORE', '情绪周期识别核心规则', 'ENGINE', '识别10阶段情绪周期，并输出评分明细与阶段快照', 'ENABLED'),
('SIMILARITY_MATCH_CORE', '历史相似行情匹配核心规则', 'ENGINE', '九维相似度匹配，不允许future字段参与T日匹配', 'ENABLED'),
('MAINLINE_RECOGNITION_CORE', '主线题材识别核心规则', 'ENGINE', '六维连续分识别主线，禁止涨幅第一或涨停最多硬判定', 'ENABLED'),
('LEADER_RECOGNITION_CORE', '龙头识别核心规则', 'ENGINE', '七维综合分识别龙头，禁止最高板等同市场总龙头', 'ENABLED'),
('PATTERN_CONDITION_CORE', '买点条件判定核心规则', 'ENGINE', '只输出条件状态，不输出交易建议', 'ENABLED'),
('RISK_CONTROL_CORE', '风控上级保护核心规则', 'ENGINE', '综合风险评分并覆盖模式风险否决', 'ENABLED'),
('BACKTEST_EXECUTION_CORE', '回测执行核心规则', 'ENGINE', 'future字段仅在历史回测窗口读取', 'ENABLED'),
('AGENT_AUDIT_CORE', 'Agent研发审计核心规则', 'ENGINE', '扫描Mock、交易建议词、future越界、字段血缘和发布闸门', 'ENABLED')
on duplicate key update
    rule_name = values(rule_name),
    rule_category = values(rule_category),
    rule_desc = values(rule_desc),
    rule_status = values(rule_status),
    updated_at = now();

insert into rule_version(rule_code, rule_name, version_no, version_name, version_status, active_flag, rule_content_json, param_schema_json)
values
('EMOTION_STAGE_CORE', '情绪周期识别核心规则', 'v1.0.0', '默认启用版本', 'ACTIVE', 1, json_object('formula','stage_score'), json_object()),
('SIMILARITY_MATCH_CORE', '历史相似行情匹配核心规则', 'v1.0.0', '默认启用版本', 'ACTIVE', 1, json_object('formula','nine_dimension_similarity'), json_object()),
('MAINLINE_RECOGNITION_CORE', '主线题材识别核心规则', 'v1.0.0', '默认启用版本', 'ACTIVE', 1, json_object('formula','mainline_strength_score'), json_object()),
('LEADER_RECOGNITION_CORE', '龙头识别核心规则', 'v1.0.0', '默认启用版本', 'ACTIVE', 1, json_object('formula','leader_score'), json_object()),
('PATTERN_CONDITION_CORE', '买点条件判定核心规则', 'v1.0.0', '默认启用版本', 'ACTIVE', 1, json_object('formula','pattern_condition_score'), json_object()),
('RISK_CONTROL_CORE', '风控上级保护核心规则', 'v1.0.0', '默认启用版本', 'ACTIVE', 1, json_object('formula','risk_score'), json_object()),
('BACKTEST_EXECUTION_CORE', '回测执行核心规则', 'v1.0.0', '默认启用版本', 'ACTIVE', 1, json_object('futureReadPhase','BACKTEST_WINDOW_ONLY'), json_object()),
('AGENT_AUDIT_CORE', 'Agent研发审计核心规则', 'v1.0.0', '默认启用版本', 'ACTIVE', 1, json_object('gates','release_gate'), json_object())
on duplicate key update
    version_status = values(version_status),
    active_flag = values(active_flag),
    rule_content_json = values(rule_content_json),
    param_schema_json = values(param_schema_json),
    updated_at = now();
