-- 第八步：ClickHouse分析库完整DDL
-- 目标：让 Engine 写入字段与真实表结构对齐
-- 数据库：ClickHouse 23+
-- 执行建议：
--   clickhouse-client --multiquery < sql/21_clickhouse_full_schema.sql

create database if not exists astock_analysis;

-- =========================================================
-- 基础行情 / 市场快照
-- =========================================================

create table if not exists astock_analysis.stock_daily_kline
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    stock_code String comment '股票代码',
    stock_name String comment '股票名称',
    code String default stock_code comment '兼容代码字段',
    name String default stock_name comment '兼容名称字段',
    exchange LowCardinality(String) default '' comment '交易所',
    sector_code String default '' comment '板块编码',
    sector_name String default '' comment '板块名称',
    industry_code String default '' comment '行业编码',
    industry_name String default '' comment '行业名称',
    theme_code String default '' comment '题材编码',
    theme_name String default '' comment '题材名称',
    mainline_code String default '' comment '主线编码',
    mainline_name String default '' comment '主线名称',
    open_price Decimal(18,4) default 0 comment '开盘价',
    high_price Decimal(18,4) default 0 comment '最高价',
    low_price Decimal(18,4) default 0 comment '最低价',
    close_price Decimal(18,4) default 0 comment '收盘价',
    pre_close_price Decimal(18,4) default 0 comment '昨收价',
    pct_change Decimal(18,6) default 0 comment '涨跌幅',
    change_pct Decimal(18,6) default pct_change comment '兼容涨跌幅',
    change_amount Decimal(18,4) default 0 comment '涨跌额',
    volume UInt64 default 0 comment '成交量',
    turnover_amount Decimal(24,4) default 0 comment '成交额',
    amount Decimal(24,4) default turnover_amount comment '兼容成交额',
    amplitude Decimal(18,6) default 0 comment '振幅',
    volume_ratio Decimal(18,6) default 0 comment '量比',
    turnover_rate Decimal(18,6) default 0 comment '换手率',
    pe_dynamic Decimal(18,6) default 0 comment '动态市盈率',
    pb Decimal(18,6) default 0 comment '市净率',
    roe Decimal(18,6) default 0 comment '资产收益率',
    total_market_value Decimal(24,4) default 0 comment '总市值',
    float_market_value Decimal(24,4) default 0 comment '流通市值',
    is_limit_up UInt8 default 0 comment '是否涨停',
    limit_up UInt8 default is_limit_up comment '是否涨停兼容字段',
    is_limit_down UInt8 default 0 comment '是否跌停',
    is_broken_board UInt8 default 0 comment '是否炸板',
    broken_board UInt8 default is_broken_board comment '是否炸板兼容字段',
    board_height UInt16 default 0 comment '连板高度',
    consecutive_board_height UInt16 default board_height comment '连板高度兼容字段',
    limit_up_days UInt16 default board_height comment '连板天数兼容字段',
    negative_feedback_score Decimal(18,6) default 0 comment '负反馈分',
    drawdown_score Decimal(18,6) default 0 comment '回撤分',
    features String default '{}' comment '扩展字段JSON',
    created_at DateTime default now() comment '创建时间',
    updated_at DateTime default now() comment '更新时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, stock_code)
comment '股票日K线行情快照表';

create table if not exists astock_analysis.market_factor_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    rise_count UInt32 default 0 comment '上涨家数',
    up_count UInt32 default rise_count comment '上涨家数兼容字段',
    rising_count UInt32 default rise_count comment '上涨家数兼容字段',
    fall_count UInt32 default 0 comment '下跌家数',
    down_count UInt32 default fall_count comment '下跌家数兼容字段',
    falling_count UInt32 default fall_count comment '下跌家数兼容字段',
    flat_count UInt32 default 0 comment '平盘家数',
    unchanged_count UInt32 default flat_count comment '平盘家数兼容字段',
    market_breadth_score Decimal(18,6) default 0 comment '市场宽度分',
    breadth_score Decimal(18,6) default market_breadth_score comment '市场宽度分兼容',
    profit_effect_score Decimal(18,6) default 0 comment '赚钱效应分',
    earning_effect_score Decimal(18,6) default profit_effect_score comment '赚钱效应分兼容',
    loss_effect_score Decimal(18,6) default 0 comment '亏钱效应分',
    loss_pressure_score Decimal(18,6) default loss_effect_score comment '亏钱效应分兼容',
    turnover_percentile Decimal(18,6) default 0 comment '成交额分位',
    amount_percentile Decimal(18,6) default turnover_percentile comment '成交额分位兼容',
    turnover_heat_score Decimal(18,6) default turnover_percentile comment '成交热度分',
    index_position_score Decimal(18,6) default 0 comment '指数位置分',
    index_percentile Decimal(18,6) default index_position_score comment '指数分位',
    index_location_score Decimal(18,6) default index_position_score comment '指数位置分兼容',
    index_fund_risk_score Decimal(18,6) default 0 comment '指数资金风险分',
    index_pressure_score Decimal(18,6) default index_fund_risk_score comment '指数压力分',
    features String default '{}' comment '扩展字段JSON',
    created_at DateTime default now() comment '创建时间',
    updated_at DateTime default now() comment '更新时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope)
comment '市场因子快照表';

create table if not exists astock_analysis.limit_up_down_ecology_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    limit_up_count UInt32 default 0 comment '涨停家数',
    zt_count UInt32 default limit_up_count comment '涨停家数兼容',
    limit_down_count UInt32 default 0 comment '跌停家数',
    dt_count UInt32 default limit_down_count comment '跌停家数兼容',
    break_board_count UInt32 default 0 comment '炸板家数',
    broken_board_count UInt32 default break_board_count comment '炸板家数兼容',
    max_board_height UInt16 default 0 comment '最高连板高度',
    highest_board_height UInt16 default max_board_height comment '最高连板高度兼容',
    max_board_height_score Decimal(18,6) default max_board_height * 10 comment '最高板高度分',
    ladder_height_score Decimal(18,6) default max_board_height_score comment '梯队高度分',
    limit_up_ecology_score Decimal(18,6) default 0 comment '涨停生态分',
    limit_eco_score Decimal(18,6) default limit_up_ecology_score comment '涨停生态兼容分',
    limit_ecology_score Decimal(18,6) default limit_up_ecology_score comment '涨停生态兼容分',
    limit_down_pressure_score Decimal(18,6) default 0 comment '跌停压力分',
    break_board_pressure_score Decimal(18,6) default 0 comment '炸板压力分',
    features String default '{}' comment '扩展字段JSON',
    created_at DateTime default now() comment '创建时间',
    updated_at DateTime default now() comment '更新时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope)
comment '涨跌停生态快照表';

-- =========================================================
-- 情绪周期
-- =========================================================

create table if not exists astock_analysis.emotion_stage_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    primary_stage LowCardinality(String) default '' comment '主阶段编码',
    primary_stage_name String default '' comment '主阶段名称',
    stage_code LowCardinality(String) default primary_stage comment '阶段编码兼容',
    emotion_stage LowCardinality(String) default primary_stage comment '情绪阶段兼容',
    stage_confidence Decimal(18,6) default 0 comment '阶段置信度',
    stage_score Decimal(18,6) default stage_confidence comment '阶段分兼容',
    second_candidate_stage LowCardinality(String) default '' comment '第二候选阶段',
    second_candidate_stage_name String default '' comment '第二候选阶段名称',
    third_candidate_stage LowCardinality(String) default '' comment '第三候选阶段',
    third_candidate_stage_name String default '' comment '第三候选阶段名称',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    features String default '{}' comment '扩展字段JSON',
    data_complete UInt8 default 1 comment '数据是否完整',
    created_at DateTime default now() comment '创建时间',
    updated_at DateTime default now() comment '更新时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, rule_version_id)
comment '情绪阶段快照表';

create table if not exists astock_analysis.emotion_stage_score_detail
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    stage_code LowCardinality(String) comment '阶段编码',
    stage_name String default '' comment '阶段名称',
    stage_score Decimal(18,6) default 0 comment '阶段总分',
    rank_no UInt16 default 0 comment '排名',
    factor_percentile_match_score Decimal(18,6) default 0 comment '因子分位匹配分',
    historical_sample_similarity_score Decimal(18,6) default 0 comment '历史样本相似分',
    stage_path_match_score Decimal(18,6) default 0 comment '阶段路径匹配分',
    following_validation_score Decimal(18,6) default 0 comment '后续演化验证分',
    manual_sample_correction_score Decimal(18,6) default 0 comment '人工样本修正分',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    features String default '{}' comment '扩展字段JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, stage_code, rule_version_id)
comment '情绪阶段评分明细表';

create table if not exists astock_analysis.stage_transition_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    from_stage LowCardinality(String) default '' comment '来源阶段',
    to_stage LowCardinality(String) default '' comment '目标阶段',
    primary_stage LowCardinality(String) default to_stage comment '主阶段兼容',
    transition_probability Decimal(18,6) default 0 comment '转移概率',
    transition_score Decimal(18,6) default transition_probability comment '转移分',
    evidence_json String default '{}' comment '证据JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, from_stage, to_stage)
comment '情绪阶段转移快照表';

-- =========================================================
-- 历史样本与相似行情
-- =========================================================

create table if not exists astock_analysis.historical_cycle_sample
(
    trade_date Date comment '样本交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    id UInt64 default 0 comment 'ID兼容',
    sample_id UInt64 default id comment '样本ID',
    stage_code LowCardinality(String) default '' comment '阶段编码',
    stage_type LowCardinality(String) default stage_code comment '阶段类型',
    emotion_stage LowCardinality(String) default stage_code comment '情绪阶段',
    primary_stage LowCardinality(String) default stage_code comment '主阶段',
    sample_type LowCardinality(String) default 'SINGLE_DAY' comment '样本类型',
    sample_confidence Decimal(18,6) default 0 comment '样本置信度',
    confidence Decimal(18,6) default sample_confidence comment '置信度兼容',
    stage_confidence Decimal(18,6) default sample_confidence comment '阶段置信度',
    similarity_score Decimal(18,6) default 0 comment '相似分',
    sample_similarity_score Decimal(18,6) default similarity_score comment '样本相似分',
    stage_score Decimal(18,6) default similarity_score comment '阶段分',
    market_breadth_score Decimal(18,6) default 0 comment '市场宽度分',
    breadth_score Decimal(18,6) default market_breadth_score comment '市场宽度分兼容',
    turnover_percentile Decimal(18,6) default 0 comment '成交分位',
    turnover_score Decimal(18,6) default turnover_percentile comment '成交分',
    turnover_heat_score Decimal(18,6) default turnover_percentile comment '成交热度分',
    index_position_score Decimal(18,6) default 0 comment '指数位置分',
    index_percentile Decimal(18,6) default index_position_score comment '指数分位',
    limit_ecology_score Decimal(18,6) default 0 comment '涨停生态分',
    limit_up_ecology_score Decimal(18,6) default limit_ecology_score comment '涨停生态分兼容',
    leader_ladder_score Decimal(18,6) default 0 comment '龙头梯队分',
    ladder_height_score Decimal(18,6) default leader_ladder_score comment '梯队高度分',
    loss_effect_score Decimal(18,6) default 0 comment '亏钱效应分',
    loss_pressure_score Decimal(18,6) default loss_effect_score comment '亏钱压力分',
    stage_path_score Decimal(18,6) default 0 comment '阶段路径分',
    mainline_structure_score Decimal(18,6) default 0 comment '主线结构分',
    mainline_strength_score Decimal(18,6) default mainline_structure_score comment '主线强度分',
    leader_feedback_score Decimal(18,6) default 0 comment '龙头反馈分',
    negative_feedback_score Decimal(18,6) default leader_feedback_score comment '负反馈分',
    pattern_code String default '' comment '模式编码',
    stock_code String default '' comment '股票代码',
    stock_name String default '' comment '股票名称',
    mainline_code String default '' comment '主线编码',
    theme_code String default mainline_code comment '题材编码',
    mainline_name String default '' comment '主线名称',
    theme_name String default mainline_name comment '题材名称',
    future_1d_return Decimal(18,6) default 0 comment '未来1日收益，仅回测窗口使用',
    future1d_return Decimal(18,6) default future_1d_return comment '未来1日收益兼容',
    future_3d_return Decimal(18,6) default 0 comment '未来3日收益，仅回测窗口使用',
    future3d_return Decimal(18,6) default future_3d_return comment '未来3日收益兼容',
    following_3d_return Decimal(18,6) default future_3d_return comment '后续3日收益兼容',
    future_5d_return Decimal(18,6) default 0 comment '未来5日收益，仅回测窗口使用',
    future5d_return Decimal(18,6) default future_5d_return comment '未来5日收益兼容',
    future_10d_return Decimal(18,6) default 0 comment '未来10日收益，仅回测窗口使用',
    future10d_return Decimal(18,6) default future_10d_return comment '未来10日收益兼容',
    max_drawdown Decimal(18,6) default 0 comment '最大回撤，仅回测窗口使用',
    following_max_drawdown Decimal(18,6) default max_drawdown comment '后续最大回撤兼容',
    evidence_json String default '{}' comment '证据JSON',
    feature_json String default '{}' comment '特征JSON',
    features String default '{}' comment '扩展字段JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, stage_code, sample_id)
comment '历史周期样本库表';

create table if not exists astock_analysis.historical_similarity_match
(
    trade_date Date comment '当前交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    match_type LowCardinality(String) default 'SINGLE_DAY' comment '匹配类型',
    sample_id UInt64 default 0 comment '历史样本ID',
    historical_trade_date Date comment '历史交易日',
    historical_stage LowCardinality(String) default '' comment '历史阶段',
    market_environment_similarity_score Decimal(18,6) default 0 comment '市场环境相似度',
    emotion_cycle_similarity_score Decimal(18,6) default 0 comment '情绪周期相似度',
    theme_leader_similarity_score Decimal(18,6) default 0 comment '主线龙头相似度',
    total_similarity_score Decimal(18,6) default 0 comment '总相似度',
    dimension_score_json String default '[]' comment '维度分JSON',
    reference_text String default '' comment '参考文本',
    risk_text String default '' comment '风险文本',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, match_type, total_similarity_score)
comment '历史相似行情匹配结果表';

create table if not exists astock_analysis.historical_similarity_factor_detail
(
    trade_date Date comment '当前交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    match_type LowCardinality(String) default 'SINGLE_DAY' comment '匹配类型',
    sample_id UInt64 default 0 comment '历史样本ID',
    historical_trade_date Date comment '历史交易日',
    dimension_code LowCardinality(String) comment '维度编码',
    dimension_name String default '' comment '维度名称',
    dimension_group_code LowCardinality(String) default '' comment '维度分组',
    dimension_weight Decimal(18,6) default 0 comment '维度权重',
    current_value Decimal(18,6) default 0 comment '当前值',
    historical_value Decimal(18,6) default 0 comment '历史值',
    dimension_similarity_score Decimal(18,6) default 0 comment '维度相似分',
    evidence_json String default '{}' comment '证据JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, match_type, sample_id, dimension_code)
comment '历史相似行情因子明细表';

-- =========================================================
-- 主线题材与板块
-- =========================================================

create table if not exists astock_analysis.theme_daily_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    theme_code String comment '题材编码',
    theme_name String default '' comment '题材名称',
    theme_type LowCardinality(String) default 'CONCEPT' comment '题材类型',
    sector_code String default theme_code comment '板块编码兼容',
    sector_name String default theme_name comment '板块名称兼容',
    mainline_code String default theme_code comment '主线编码兼容',
    mainline_name String default theme_name comment '主线名称兼容',
    pct_change Decimal(18,6) default 0 comment '涨跌幅',
    change_pct Decimal(18,6) default pct_change comment '涨跌幅兼容',
    limit_up_count Decimal(18,6) default 0 comment '涨停数',
    zt_count Decimal(18,6) default limit_up_count comment '涨停数兼容',
    stock_count Decimal(18,6) default 0 comment '成分股数',
    component_count Decimal(18,6) default stock_count comment '成分股数兼容',
    turnover_amount Decimal(24,4) default 0 comment '成交额',
    amount Decimal(24,4) default turnover_amount comment '成交额兼容',
    turnover_ratio Decimal(18,6) default 0 comment '成交占比',
    turnover_amount_ratio Decimal(18,6) default turnover_ratio comment '成交占比兼容',
    continuity_days Decimal(18,6) default 0 comment '持续天数',
    continuous_days Decimal(18,6) default continuity_days comment '持续天数兼容',
    active_days Decimal(18,6) default continuity_days comment '活跃天数',
    max_board_height Decimal(18,6) default 0 comment '最高板高度',
    highest_board_height Decimal(18,6) default max_board_height comment '最高板高度兼容',
    leader_count Decimal(18,6) default 0 comment '龙头数量',
    core_stock_count Decimal(18,6) default leader_count comment '核心股数量',
    features String default '{}' comment '扩展字段JSON',
    created_at DateTime default now() comment '创建时间',
    updated_at DateTime default now() comment '更新时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, theme_code)
comment '题材日快照表';

create table if not exists astock_analysis.theme_strength_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    theme_code String comment '题材编码',
    theme_name String default '' comment '题材名称',
    theme_type LowCardinality(String) default '' comment '题材类型',
    theme_strength_score Decimal(18,6) default 0 comment '题材强度分',
    strength_score Decimal(18,6) default theme_strength_score comment '强度分兼容',
    rank_no UInt32 default 0 comment '排名',
    limit_up_cluster_score Decimal(18,6) default 0 comment '涨停聚集强度',
    turnover_concentration_score Decimal(18,6) default 0 comment '成交集中强度',
    continuity_score Decimal(18,6) default 0 comment '持续性强度',
    ladder_integrity_score Decimal(18,6) default 0 comment '梯队完整度',
    leader_drive_score Decimal(18,6) default 0 comment '龙头带动性',
    emotion_match_score Decimal(18,6) default 0 comment '情绪匹配度',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, theme_code, rank_no)
comment '题材强度快照表';

create table if not exists astock_analysis.mainline_daily_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    mainline_id UInt64 default 0 comment '主线ID',
    mainline_code String comment '主线编码',
    mainline_name String default '' comment '主线名称',
    theme_code String default mainline_code comment '题材编码',
    theme_name String default mainline_name comment '题材名称',
    mainline_status LowCardinality(String) default '' comment '主线状态',
    lifecycle_stage LowCardinality(String) default '' comment '生命周期阶段',
    theme_role LowCardinality(String) default '' comment '题材角色',
    mainline_strength_score Decimal(18,6) default 0 comment '主线强度分',
    strength_score Decimal(18,6) default mainline_strength_score comment '强度分兼容',
    rank_no UInt32 default 0 comment '排名',
    limit_up_cluster_score Decimal(18,6) default 0 comment '涨停聚集强度',
    turnover_concentration_score Decimal(18,6) default 0 comment '成交集中强度',
    continuity_score Decimal(18,6) default 0 comment '持续性强度',
    ladder_integrity_score Decimal(18,6) default 0 comment '梯队完整度',
    leader_drive_score Decimal(18,6) default 0 comment '龙头带动性',
    emotion_match_score Decimal(18,6) default 0 comment '情绪匹配度',
    mainline_decay_risk_score Decimal(18,6) default 0 comment '主线衰退风险分',
    risk_score Decimal(18,6) default mainline_decay_risk_score comment '风险分兼容',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, mainline_code, rank_no)
comment '主线日快照表';

create table if not exists astock_analysis.mainline_switch_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    old_mainline_code String default '' comment '旧主线编码',
    old_mainline_name String default '' comment '旧主线名称',
    new_mainline_code String default '' comment '新主线编码',
    new_mainline_name String default '' comment '新主线名称',
    switch_status LowCardinality(String) default '' comment '切换状态',
    switch_score Decimal(18,6) default 0 comment '切换分',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, old_mainline_code, new_mainline_code)
comment '主线切换快照表';

create table if not exists astock_analysis.sector_strength_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    sector_code String comment '板块编码',
    sector_name String default '' comment '板块名称',
    theme_code String default sector_code comment '题材编码兼容',
    theme_name String default sector_name comment '题材名称兼容',
    sector_type LowCardinality(String) default '' comment '板块类型',
    pct_change Decimal(18,6) default 0 comment '涨跌幅',
    change_pct Decimal(18,6) default pct_change comment '涨跌幅兼容',
    limit_up_count Decimal(18,6) default 0 comment '涨停数',
    stock_count Decimal(18,6) default 0 comment '股票数',
    turnover_amount Decimal(24,4) default 0 comment '成交额',
    turnover_ratio Decimal(18,6) default 0 comment '成交占比',
    continuity_days Decimal(18,6) default 0 comment '持续天数',
    max_board_height Decimal(18,6) default 0 comment '最高板高度',
    leader_count Decimal(18,6) default 0 comment '龙头数量',
    sector_strength_score Decimal(18,6) default 0 comment '板块强度分',
    strength_score Decimal(18,6) default sector_strength_score comment '强度分兼容',
    rank_no UInt32 default 0 comment '排名',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, sector_code, rank_no)
comment '板块强度快照表';

-- =========================================================
-- 龙头
-- =========================================================

create table if not exists astock_analysis.leader_daily_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    stock_code String comment '股票代码',
    stock_name String default '' comment '股票名称',
    sector_code String default '' comment '板块编码',
    sector_name String default '' comment '板块名称',
    mainline_code String default '' comment '主线编码',
    mainline_name String default '' comment '主线名称',
    theme_code String default mainline_code comment '题材编码兼容',
    theme_name String default mainline_name comment '题材名称兼容',
    leader_type LowCardinality(String) default '' comment '龙头类型',
    leader_status LowCardinality(String) default '' comment '龙头状态',
    leader_score Decimal(18,6) default 0 comment '龙头综合分',
    rank_no UInt32 default 0 comment '排名',
    recognition_score Decimal(18,6) default 0 comment '辨识度分',
    mainline_relation_score Decimal(18,6) default 0 comment '主线关联分',
    drive_score Decimal(18,6) default 0 comment '带动分',
    leader_drive_score Decimal(18,6) default drive_score comment '龙头带动分兼容',
    strength_score Decimal(18,6) default 0 comment '强度分',
    support_score Decimal(18,6) default 0 comment '承接分',
    continuity_score Decimal(18,6) default 0 comment '持续性分',
    risk_feedback_score Decimal(18,6) default 0 comment '风险反馈分',
    negative_feedback_score Decimal(18,6) default 0 comment '负反馈分',
    leader_negative_feedback_score Decimal(18,6) default negative_feedback_score comment '负反馈分兼容',
    board_height Decimal(18,6) default 0 comment '连板高度',
    limit_up UInt8 default 0 comment '是否涨停',
    broken_board UInt8 default 0 comment '是否炸板',
    pct_change Decimal(18,6) default 0 comment '涨跌幅',
    turnover_amount Decimal(24,4) default 0 comment '成交额',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, stock_code, leader_type, rank_no)
comment '龙头日快照表';

create table if not exists astock_analysis.leader_ladder_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    board_height Decimal(18,6) default 0 comment '板高',
    stock_count UInt32 default 0 comment '股票数量',
    top_stock_code String default '' comment '最高分股票代码',
    top_stock_name String default '' comment '最高分股票名称',
    top_leader_score Decimal(18,6) default 0 comment '最高龙头分',
    leader_type LowCardinality(String) default '' comment '龙头类型',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, board_height)
comment '龙头梯队快照表';

create table if not exists astock_analysis.leader_drive_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    stock_code String comment '股票代码',
    stock_name String default '' comment '股票名称',
    sector_drive_score Decimal(18,6) default 0 comment '板块带动分',
    mainline_drive_score Decimal(18,6) default 0 comment '主线带动分',
    emotion_drive_score Decimal(18,6) default 0 comment '情绪带动分',
    fund_drive_score Decimal(18,6) default 0 comment '资金带动分',
    leader_drive_score Decimal(18,6) default 0 comment '龙头带动分',
    drive_score Decimal(18,6) default leader_drive_score comment '带动分兼容',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, stock_code)
comment '龙头带动快照表';

create table if not exists astock_analysis.leader_negative_feedback
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    stock_code String comment '股票代码',
    stock_name String default '' comment '股票名称',
    leader_type LowCardinality(String) default '' comment '龙头类型',
    negative_feedback_score Decimal(18,6) default 0 comment '负反馈分',
    broken_board UInt8 default 0 comment '是否炸板',
    limit_down UInt8 default 0 comment '是否跌停',
    impact_mainline UInt8 default 0 comment '是否影响主线',
    impact_emotion_cycle UInt8 default 0 comment '是否影响情绪周期',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, stock_code)
comment '龙头负反馈快照表';

-- =========================================================
-- 模式条件与风控
-- =========================================================

create table if not exists astock_analysis.buy_pattern_signal_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    pattern_code String comment '模式编码',
    pattern_name String default '' comment '模式名称',
    stock_code String default '' comment '股票代码',
    stock_name String default '' comment '股票名称',
    watch_object_type LowCardinality(String) default '' comment '观察对象类型',
    leader_type LowCardinality(String) default '' comment '龙头类型',
    leader_status LowCardinality(String) default '' comment '龙头状态',
    mainline_code String default '' comment '主线编码',
    mainline_name String default '' comment '主线名称',
    emotion_stage LowCardinality(String) default '' comment '情绪阶段',
    condition_status LowCardinality(String) default '' comment '条件状态',
    condition_score Decimal(18,6) default 0 comment '条件分',
    pattern_condition_score Decimal(18,6) default condition_score comment '模式条件分',
    cycle_admission_score Decimal(18,6) default 0 comment '周期准入分',
    mainline_valid_score Decimal(18,6) default 0 comment '主线有效分',
    leader_position_score Decimal(18,6) default 0 comment '龙头地位分',
    trigger_score Decimal(18,6) default 0 comment '触发分',
    backtest_support_score Decimal(18,6) default 0 comment '回测支持分',
    manual_correction_score Decimal(18,6) default 0 comment '人工修正分',
    risk_veto UInt8 default 0 comment '是否风险否决',
    risk_veto_reason String default '' comment '风险否决原因',
    invalidated UInt8 default 0 comment '是否失效',
    invalidated_reason String default '' comment '失效原因',
    allow_condition_met_display UInt8 default 0 comment '是否允许展示条件满足',
    signal_text String default '' comment '信号文本，非交易建议',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, pattern_code, stock_code, condition_status)
comment '买点条件信号快照表';

create table if not exists astock_analysis.pattern_risk_veto_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    pattern_code String default '' comment '模式编码',
    pattern_name String default '' comment '模式名称',
    stock_code String default '' comment '股票代码',
    stock_name String default '' comment '股票名称',
    leader_type LowCardinality(String) default '' comment '龙头类型',
    condition_status LowCardinality(String) default '' comment '条件状态',
    condition_score Decimal(18,6) default 0 comment '条件分',
    risk_veto UInt8 default 0 comment '是否风险否决',
    risk_veto_reason String default '' comment '风险否决原因',
    invalidated UInt8 default 0 comment '是否失效',
    invalidated_reason String default '' comment '失效原因',
    risk_action LowCardinality(String) default '' comment '风险动作',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, pattern_code, stock_code, risk_action)
comment '模式风险否决快照表';

create table if not exists astock_analysis.risk_signal_snapshot
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    risk_code String comment '风险编码',
    risk_name String default '' comment '风险名称',
    risk_source LowCardinality(String) default '' comment '风险来源',
    risk_score Decimal(18,6) default 0 comment '风险分',
    risk_level LowCardinality(String) default '' comment '风险等级',
    signal_level LowCardinality(String) default '' comment '信号等级',
    risk_action LowCardinality(String) default '' comment '风险动作',
    one_vote_veto UInt8 default 0 comment '是否一票否决',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, risk_code)
comment '风控信号快照表';

create table if not exists astock_analysis.risk_signal_detail
(
    trade_date Date comment '交易日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    risk_code String comment '风险编码',
    risk_name String default '' comment '风险名称',
    risk_source LowCardinality(String) default '' comment '风险来源',
    signal_level LowCardinality(String) default '' comment '信号等级',
    risk_level LowCardinality(String) default '' comment '风险等级',
    risk_score Decimal(18,6) default 0 comment '风险分',
    risk_action LowCardinality(String) default '' comment '风险动作',
    one_vote_veto UInt8 default 0 comment '是否一票否决',
    risk_text String default '' comment '风险文本',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, risk_source, risk_code)
comment '风控信号明细表';

-- =========================================================
-- 回测
-- =========================================================

create table if not exists astock_analysis.backtest_signal_detail
(
    trade_date Date comment '回测任务日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    sample_id UInt64 default 0 comment '样本ID',
    sample_date Date comment '样本日',
    stage_code LowCardinality(String) default '' comment '阶段编码',
    pattern_code String default '' comment '模式编码',
    stock_code String default '' comment '股票代码',
    stock_name String default '' comment '股票名称',
    mainline_code String default '' comment '主线编码',
    mainline_name String default '' comment '主线名称',
    signal_score Decimal(18,6) default 0 comment '信号分',
    risk_score Decimal(18,6) default 0 comment '风险分',
    risk_action LowCardinality(String) default '' comment '风险动作',
    signal_effective UInt8 default 0 comment '信号是否有效',
    risk_vetoed UInt8 default 0 comment '是否被风险过滤',
    replay_status LowCardinality(String) default '' comment '回放状态',
    replay_return Decimal(18,6) default 0 comment '回放收益',
    replay_drawdown Decimal(18,6) default 0 comment '回放回撤',
    future_1d_return Decimal(18,6) default 0 comment '未来1日收益，仅回测',
    future_3d_return Decimal(18,6) default 0 comment '未来3日收益，仅回测',
    future_5d_return Decimal(18,6) default 0 comment '未来5日收益，仅回测',
    future_10d_return Decimal(18,6) default 0 comment '未来10日收益，仅回测',
    max_drawdown Decimal(18,6) default 0 comment '最大回撤，仅回测',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, task_id, sample_date, sample_id)
comment '回测信号明细表';

create table if not exists astock_analysis.backtest_performance_detail
(
    trade_date Date comment '回测任务日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    layer_code String default '' comment '分层编码',
    layer_name String default '' comment '分层名称',
    metric_name String default '' comment '指标名',
    metric_value Decimal(24,6) default 0 comment '指标值',
    sample_count UInt32 default 0 comment '样本数',
    evidence_json String default '{}' comment '证据JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, task_id, layer_code, metric_name)
comment '回测绩效明细表';

create table if not exists astock_analysis.backtest_layer_stat
(
    trade_date Date comment '回测任务日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    layer_code String default '' comment '分层编码',
    layer_name String default '' comment '分层名称',
    sample_count UInt32 default 0 comment '样本数',
    effective_signal_count UInt32 default 0 comment '有效信号数',
    risk_veto_count UInt32 default 0 comment '风控过滤数',
    win_rate Decimal(18,6) default 0 comment '胜率',
    avg_return Decimal(18,6) default 0 comment '平均收益',
    avg_drawdown Decimal(18,6) default 0 comment '平均回撤',
    profit_loss_ratio Decimal(18,6) default 0 comment '盈亏比',
    evidence_json String default '{}' comment '证据JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, task_id, layer_code)
comment '回测分层统计表';

create table if not exists astock_analysis.backtest_failure_case
(
    trade_date Date comment '回测任务日',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    task_id UInt64 default 0 comment '任务ID',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    sample_id UInt64 default 0 comment '样本ID',
    sample_date Date comment '样本日',
    pattern_code String default '' comment '模式编码',
    stock_code String default '' comment '股票代码',
    stock_name String default '' comment '股票名称',
    failure_type LowCardinality(String) default '' comment '失败类型',
    failure_reason String default '' comment '失败原因',
    replay_status LowCardinality(String) default '' comment '回放状态',
    replay_return Decimal(18,6) default 0 comment '回放收益',
    replay_drawdown Decimal(18,6) default 0 comment '回放回撤',
    future_3d_return Decimal(18,6) default 0 comment '未来3日收益，仅回测',
    max_drawdown Decimal(18,6) default 0 comment '最大回撤，仅回测',
    evidence_json String default '{}' comment '证据JSON',
    risk_json String default '{}' comment '风险JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, market_scope, task_id, failure_type, sample_id)
comment '回测失败样本表';

-- =========================================================
-- Agent研发审计
-- =========================================================

create table if not exists astock_analysis.agent_audit_code_scan_detail
(
    trade_date Date comment '审计日期',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    audit_task_id UInt64 default 0 comment '审计任务ID',
    task_id UInt64 default audit_task_id comment '任务ID兼容',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    issue_code String default '' comment '问题编码',
    issue_name String default '' comment '问题名称',
    issue_level LowCardinality(String) default '' comment '问题等级',
    issue_type LowCardinality(String) default '' comment '问题类型',
    module_name String default '' comment '模块名称',
    file_path String default '' comment '文件路径',
    line_no UInt32 default 0 comment '行号',
    release_blocker UInt8 default 0 comment '是否发布阻断',
    evidence_json String default '{}' comment '证据JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, audit_task_id, issue_code, module_name)
comment 'Agent代码扫描明细表';

create table if not exists astock_analysis.agent_audit_data_lineage_detail
(
    trade_date Date comment '审计日期',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    audit_task_id UInt64 default 0 comment '审计任务ID',
    task_id UInt64 default audit_task_id comment '任务ID兼容',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    page_code String default '' comment '页面编码',
    vo_class_name String default '' comment 'VO类名',
    field_name String default '' comment '字段名',
    source_table String default '' comment '来源表',
    source_column String default '' comment '来源列',
    lineage_status LowCardinality(String) default '' comment '血缘状态',
    issue_level LowCardinality(String) default '' comment '问题等级',
    evidence_json String default '{}' comment '证据JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, audit_task_id, page_code, vo_class_name, field_name)
comment 'Agent字段血缘审计明细表';

create table if not exists astock_analysis.agent_audit_rule_hit_detail
(
    trade_date Date comment '审计日期',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    audit_task_id UInt64 default 0 comment '审计任务ID',
    task_id UInt64 default audit_task_id comment '任务ID兼容',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    rule_code String default '' comment '规则编码',
    rule_name String default '' comment '规则名称',
    hit_status LowCardinality(String) default '' comment '命中状态',
    hit_count UInt32 default 0 comment '命中数',
    blocker_count UInt32 default 0 comment '阻断数',
    evidence_json String default '{}' comment '证据JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, audit_task_id, rule_code)
comment 'Agent审计规则命中明细表';

create table if not exists astock_analysis.agent_audit_release_gate_detail
(
    trade_date Date comment '审计日期',
    market_scope LowCardinality(String) default 'A_SHARE' comment '市场范围',
    audit_task_id UInt64 default 0 comment '审计任务ID',
    task_id UInt64 default audit_task_id comment '任务ID兼容',
    rule_version_id UInt64 default 0 comment '规则版本ID',
    gate_code String default '' comment '闸门编码',
    gate_name String default '' comment '闸门名称',
    gate_status LowCardinality(String) default '' comment '闸门状态',
    passed UInt8 default 0 comment '是否通过',
    issue_count UInt32 default 0 comment '问题数',
    blocker_count UInt32 default 0 comment '阻断数',
    evidence_json String default '{}' comment '证据JSON',
    created_at DateTime default now() comment '创建时间'
)
engine = MergeTree
partition by toYYYYMM(trade_date)
order by (trade_date, audit_task_id, gate_code)
comment 'Agent发布闸门检查明细表';
