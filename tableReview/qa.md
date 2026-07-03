理解了，我已经按“表结构评审 + 数据字典 + 业务关系说明”的方式开始梳理，并生成了文档。

这次覆盖：

MySQL：42 张表，1135 个字段
ClickHouse：39 张表，975 个字段
总计：81 张表，2110 个字段

文档里每张表都包含：

1. 表是干什么的
2. 属于哪个业务分组
3. 存什么维度的数据
4. 数据大概怎么写进来
5. 关键关联字段
6. 每个字段的类型
7. 每个字段的DDL注释
8. 每个字段的业务含义
9. 字段和其他表的关联说明
10. 评审关注点

下载完整数据字典：

ASTOCK_DB_TABLE_REVIEW_AND_DATA_DICTIONARY.md

下载表索引总览：

ASTOCK_DB_TABLE_INDEX_OVERVIEW.md

打包下载：

astock-db-table-review-docs.zip

我先给你一个大白话总览，方便你先建立整体感觉。

这套表整体分两大库

MySQL 是业务账本。

它主要存：

规则
版本
任务
配置
人工确认
复盘记录
回测任务
Agent审计任务
发布检查
数据质量检查
字段血缘

也就是说，MySQL 里的表更多是“谁配置了什么、哪个任务跑了、哪个规则版本生效、哪个页面字段来自哪里”。

ClickHouse 是分析事实库。

它主要存：

日K行情
市场因子
涨跌停生态
情绪周期结果
历史相似行情
主线题材
板块强度
龙头梯队
风控信号
回测结果
Agent审计明细

也就是说，ClickHouse 里的表更多是“某天市场发生了什么、某个Engine算出了什么、某个股票/板块/主线处于什么状态”。

最关键的关联字段

你后面看表，先抓这几个字段：

trade_date        交易日，几乎所有 ClickHouse 快照都靠它串起来
market_scope      市场范围，目前默认 A_SHARE
stock_code        股票代码，股票级表的核心关联字段
sector_code       板块编码
theme_code        题材编码
mainline_code     主线编码
rule_code         规则编码
rule_version_id   规则版本ID
task_id           任务ID
audit_task_id     Agent审计任务ID
report_id         回测报告ID
page_code         页面编码
features          扩展JSON字段

你可以这样理解数据流：

stock_daily_kline
market_factor_snapshot
limit_up_down_ecology_snapshot
↓
情绪周期 Engine
主线识别 Engine
龙头识别 Engine
风控 Engine
模式条件 Engine
相似行情 Engine
回测 Engine
↓
ClickHouse 各类分析快照
↓
Java PageDataAggregator
↓
15 个前端页面

MySQL 主要在旁边提供：

规则版本
任务日志
页面字段血缘
数据质量检查
人工确认
Agent发布闸门

下一步建议你不要一次性读 81 张表，太累。你先按这个顺序看：

1. ASTOCK_DB_TABLE_INDEX_OVERVIEW.md
2. stock_daily_kline
3. market_factor_snapshot
4. emotion_stage_snapshot
5. historical_similarity_match
6. mainline_daily_snapshot
7. leader_daily_snapshot
8. risk_signal_snapshot
9. backtest_task / backtest_report
10. agent_audit_task / agent_release_gate_check

这 10 组看懂以后，整个系统的数据思路就基本通了。

存每个股票的日k或者分时数据用那张表
存每个板块的日k
每个板块跟股票的关系,有两种类型,一种同花顺,一种东财,同花顺下面有4个类型[同花顺行业,证券会行业,概念,地域],东财有[行业,概念,地域]
我应该分别存到那个表里,现在的表能支撑吗
还有就是,我希望每天,板块都做个自检
比如说,今天多了一个新的概念,那存每个板块的日k的表,就要多一条数据,并且要有时间
对应的这个新板块对应的成分股,也就是板块与股票的关联关系,也要映射上
现在的表支撑这个吗
已思考 50s

结论先说清楚：现在的表只能“部分支撑”，不能完整、严谨地支撑你说的这套板块体系。

你现在已有表里：

需求	当前能不能支撑	当前表	结论
每个股票日K	能	astock_analysis.stock_daily_kline	这张表就是干这个的
每个股票分时	不能	暂无标准分时表	需要新增 stock_minute_kline
每个板块日K	不完整	sector_daily_snapshot / sector_strength_snapshot	现在更像“板块分析快照/强度快照”，不是标准板块K线
板块和股票关系	部分能	theme_stock_mapping / sector_stock_mapping_snapshot	缺少数据源、板块分类，不能严谨区分同花顺/东财
每天发现新板块并自检	不完整	可借用任务日志/质量日志	缺少专门的板块自检批次表、差异日志表

我给你生成了一份补充建表 SQL，里面包含股票分时、板块日K、板块成分关系V2、板块自检表：

下载 astock_sector_stock_kline_schema_patch.sql

打包版：

下载 astock-sector-stock-kline-schema-patch.zip

1. 存每个股票日K用哪张表？

用现有这张：

astock_analysis.stock_daily_kline

它已经有这些核心字段：

trade_date              交易日
stock_code              股票代码
stock_name              股票名称
open_price              开盘价
high_price              最高价
low_price               最低价
close_price             收盘价
pre_close_price         昨收价
pct_change              涨跌幅
change_amount           涨跌额
volume                  成交量
turnover_amount         成交额
amplitude               振幅
turnover_rate           换手率
total_market_value      总市值
float_market_value      流通市值
is_limit_up             是否涨停
is_limit_down           是否跌停
board_height            连板高度
features                扩展字段JSON

所以股票日K这块当前表是对的。

查询一个股票某天日K：

SELECT *
FROM astock_analysis.stock_daily_kline
WHERE trade_date = '2026-06-30'
AND stock_code = '000001';

查询某天全部股票：

SELECT *
FROM astock_analysis.stock_daily_kline
WHERE trade_date = '2026-06-30'
AND market_scope = 'A_SHARE';
2. 存每个股票分时数据用哪张表？

现在没有。

stock_daily_kline 是日K表，不适合硬塞分时数据。你应该新增：

astock_analysis.stock_minute_kline

核心粒度是：

trade_date + trade_time + minute_level + stock_code

也就是：

某个交易日
某一分钟
某个股票
某个分钟级别
一条K线

例如：

2026-06-30 09:31:00  000001  1m  open/high/low/close/volume/amount

我已经在补充 SQL 里给你设计了这个表。

3. 存每个板块日K用哪张表？

这里要重点说。

你现在有两张相关表：

astock_analysis.sector_daily_snapshot
astock_analysis.sector_strength_snapshot

但是这两张都不是真正标准的“板块日K表”。

sector_strength_snapshot

它更像“板块强度快照”，里面有：

sector_code
sector_name
sector_type
pct_change
limit_up_count
stock_count
turnover_amount
turnover_ratio
continuity_days
max_board_height
leader_count
sector_strength_score
rank_no

这张表适合页面展示：

板块强度
板块排名
涨停数
板块成交额
强度分

但它缺少标准K线字段：

open_price
high_price
low_price
close_price
pre_close_price
volume
amplitude

所以它不适合作为“板块日K源表”。

sector_daily_snapshot

这张表更泛化，字段很多，但偏“通用分析快照”，不是干净的板块K线表。

所以我建议新增一张专表：

astock_analysis.sector_daily_kline

它专门存：

同花顺行业板块日K
证监会行业板块日K
同花顺概念板块日K
同花顺地域板块日K
东方财富行业板块日K
东方财富概念板块日K
东方财富地域板块日K

核心字段应该是：

trade_date              交易日
source_provider         数据源：THS / EASTMONEY
sector_category         板块分类
source_sector_code      数据源原始板块编码
source_sector_name      数据源原始板块名称
sector_code             系统统一板块编码
sector_name             系统统一板块名称
open_price              开盘点位
high_price              最高点位
low_price               最低点位
close_price             收盘点位
pre_close_price         昨收点位
pct_change              涨跌幅
change_amount           涨跌额
volume                  成交量
turnover_amount         成交额
stock_count             成分股数量
rise_count              上涨家数
fall_count              下跌家数
limit_up_count          涨停家数
limit_down_count        跌停家数
is_new_sector           是否今日新发现板块
first_seen_date         首次发现日期
4. 同花顺 / 东财板块分类怎么存？

你这个设计点非常关键，不能只用 sector_name。

因为可能出现：

同花顺的“人工智能”
东方财富的“人工智能”

名字一样，但成分股、口径、分类、指数口径可能不一样。

所以必须拆成三个字段：

source_provider      数据源
sector_category      板块分类
source_sector_code   数据源原始板块编码

我建议枚举如下：

数据源	source_provider	分类	sector_category
同花顺	THS	同花顺行业	THS_INDUSTRY
同花顺	THS	证监会行业	CSRC_INDUSTRY
同花顺	THS	概念	THS_CONCEPT
同花顺	THS	地域	THS_REGION
东方财富	EASTMONEY	行业	EM_INDUSTRY
东方财富	EASTMONEY	概念	EM_CONCEPT
东方财富	EASTMONEY	地域	EM_REGION

系统内部统一板块编码 sector_code 建议这样生成：

source_provider + ':' + sector_category + ':' + source_sector_code

例如：

THS:THS_CONCEPT:308885
THS:CSRC_INDUSTRY:A0201
EASTMONEY:EM_CONCEPT:BK0800
EASTMONEY:EM_INDUSTRY:BK0475

这样可以彻底避免板块重名、跨源混淆。

5. 板块和股票关系应该存哪里？

当前有：

theme_stock_mapping
sector_stock_mapping_snapshot

但问题是：

theme_stock_mapping 偏题材配置，不适合承载所有行业/概念/地域关系
sector_stock_mapping_snapshot 有板块和股票字段，但缺 source_provider / sector_category / source_sector_code

所以现在只能临时用，不建议作为长期正式表。

我建议新增：

astock_analysis.sector_stock_mapping_snapshot_v2

它的粒度是：

trade_date + source_provider + sector_category + sector_code + stock_code

含义是：

某一天
某个数据源
某个板块分类
某个板块
包含某只股票

例如：

2026-06-30
THS
THS_CONCEPT
THS:THS_CONCEPT:308885
000001

它应该有这些关键字段：

trade_date              交易日
source_provider         数据源
sector_category         板块分类
source_sector_code      数据源原始板块编码
sector_code             系统统一板块编码
sector_name             板块名称
stock_code              股票代码
stock_name              股票名称
member_status           成分关系状态
is_new_mapping          是否今日新增成分关系
is_removed_mapping      是否今日移除成分关系
first_seen_date         首次进入板块日期
last_seen_date          最近一次出现日期
weight                  成分权重
rank_no                 板块内排序
source_batch_no         采集批次号
6. 你说的“每天板块自检”现在能不能支撑？

当前不完整。

你的需求其实是一个非常重要的数据治理流程：

每天拉取同花顺/东财板块列表
↓
发现今天有没有新板块
↓
新板块写入板块主数据表
↓
板块日K表增加当天数据
↓
拉取这个板块的成分股
↓
板块-股票关系表写入当天快照
↓
和昨天对比，记录新增/移除/改名/缺失

这个流程建议加两张 MySQL 表：

sector_self_check_batch
sector_self_check_diff_log
sector_self_check_batch

记录一次自检任务：

今天检查的是哪个数据源
检查的是哪个板块分类
总共发现多少个板块
新增多少个板块
消失多少个板块
新增多少条成分股关系
移除多少条成分股关系
自检是否成功
sector_self_check_diff_log

记录自检发现的具体差异：

NEW_SECTOR             新板块
DISAPPEARED_SECTOR     消失板块
RENAMED_SECTOR         改名板块
NEW_MAPPING            新增成分股
REMOVED_MAPPING        移除成分股
KLINE_MISSING          板块日K缺失
MAPPING_EMPTY          板块成分为空

这样你每天就能知道：

今天同花顺多了哪个概念？
这个概念有没有日K？
这个概念包含哪些股票？
这些股票里哪些是新加入的？
哪些是昨天有、今天没了？
7. 新板块出现时，数据应该怎么落？

比如今天同花顺新增一个概念：

概念名：机器人执行器
source_provider = THS
sector_category = THS_CONCEPT
source_sector_code = 308999
第一步：写 MySQL 板块主数据

写入：

sector_definition

核心字段：

source_provider      = THS
sector_category      = THS_CONCEPT
source_sector_code   = 308999
source_sector_name   = 机器人执行器
sector_code          = THS:THS_CONCEPT:308999
sector_name          = 机器人执行器
first_seen_date      = 今天
last_seen_date       = 今天
active_status        = ACTIVE
第二步：写 ClickHouse 板块日K

写入：

astock_analysis.sector_daily_kline

核心字段：

trade_date           = 今天
source_provider      = THS
sector_category      = THS_CONCEPT
sector_code          = THS:THS_CONCEPT:308999
sector_name          = 机器人执行器
open_price
high_price
low_price
close_price
pct_change
turnover_amount
stock_count
is_new_sector        = 1
first_seen_date      = 今天
第三步：写 ClickHouse 板块成分快照

写入：

astock_analysis.sector_stock_mapping_snapshot_v2

假设它包含 20 个股票，就写 20 行：

trade_date           = 今天
source_provider      = THS
sector_category      = THS_CONCEPT
sector_code          = THS:THS_CONCEPT:308999
stock_code           = 每个成分股代码
stock_name           = 每个成分股名称
member_status        = ACTIVE
is_new_mapping       = 1
first_seen_date      = 今天
last_seen_date       = 今天
第四步：写自检差异日志

写入：

sector_self_check_diff_log

至少一条：

diff_type            = NEW_SECTOR
sector_code          = THS:THS_CONCEPT:308999
sector_name          = 机器人执行器

如果有 20 个新成分股，再写 20 条：

diff_type            = NEW_MAPPING
sector_code          = THS:THS_CONCEPT:308999
stock_code           = xxx
stock_name           = xxx
8. 现在已有表怎么改造最合理？

我的建议不是强行把所有东西塞进旧表，而是这样分工：

表	是否保留	用途
stock_daily_kline	保留	股票日K事实表
stock_minute_kline	新增	股票分时K线
sector_daily_kline	新增	板块标准日K
sector_strength_snapshot	保留	板块强度计算结果
sector_stock_mapping_snapshot_v2	新增	每日板块成分快照
sector_definition	新增	板块主数据
sector_self_check_batch	新增	每日自检批次
sector_self_check_diff_log	新增	每日自检差异明细
theme_definition	保留	系统内部题材/主线配置，不建议当作全量板块主数据
theme_stock_mapping	保留	题材和股票的业务配置关系，不建议当作全量板块成分快照
9. 最终回答你的问题
   股票日K用哪张？
   astock_analysis.stock_daily_kline

能支撑。

股票分时用哪张？

现在没有。需要新增：

astock_analysis.stock_minute_kline
板块日K用哪张？

现在没有合格专表。建议新增：

astock_analysis.sector_daily_kline

不要用 sector_strength_snapshot 硬当板块K线表。

板块和股票关系用哪张？

现在有 sector_stock_mapping_snapshot，但字段不够。建议新增：

astock_analysis.sector_stock_mapping_snapshot_v2

核心必须有：

source_provider
sector_category
source_sector_code
sector_code
stock_code
trade_date
member_status
first_seen_date
last_seen_date
每天新板块自检，现在能不能支撑？

只能部分支撑，不完整。

需要新增：

sector_definition
sector_self_check_batch
sector_self_check_diff_log
sector_daily_kline
sector_stock_mapping_snapshot_v2

这几张表加上以后，你说的这个场景才能完整闭环：

今天多了一个新概念
↓
板块主数据多一条
↓
板块日K多一条
↓
板块成分股关系多N条
↓
自检日志记录这个新概念和新增成分关系

你下一步可以直接把我给你的这个 SQL 放到项目的 sql/ 目录里，然后执行建表