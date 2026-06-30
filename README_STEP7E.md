# 第七步第五段：逐页面补齐嵌套VO Converter映射

生成日期：2026-06-29

## 本次完成内容

1. 15个页面的Converter全部升级为“嵌套VO填充版”。
2. 每个Converter都包含：
   - 顶层PageVO字段映射
   - 嵌套VO对象映射方法
   - 嵌套VO列表映射方法
   - 空数据保护
   - 无Mock保护
3. 每个页面的FieldMappingRegistry已扩展到嵌套VO字段。
4. `sql/07_page_field_lineage_seed.sql` 已扩展到：
   - 顶层PageVO字段
   - 嵌套VO字段
   - List<NestedVO>字段
5. LeaderProfile 和 BacktestReport 两个详情页也接入真实Repository + Converter链路，不再直接DATA_NOT_READY。

## 当前真实链路

```text
Controller
-> QueryService
-> Aggregator / DetailService
-> DataQualityQueryService
-> Repository
-> Converter
-> 完整PageVO
-> 嵌套VO区块
```

## 当前边界

当前Converter按字段名和ClickHouse/MySQL列名做确定性映射：

```text
stockCode -> stock_code
riskScore -> risk_score
leaderScore -> leader_score
future3dReturn -> future_3d_return
```

如果某个字段当前源表没有同名列，则保持null或空集合，不允许用Mock补齐。

## 下一步建议

第七步第六段：

```text
补齐页面专属多表Repository查询：
例如 MarketDashboardConverter 不只读 market_factor_snapshot，
还要读 emotion_stage_snapshot、risk_signal_snapshot、mainline_daily_snapshot、leader_daily_snapshot、buy_pattern_signal_snapshot。
让每个页面从多张表聚合完整业务区块。
```

## 红线

1. Converter只做字段转换，不做评分。
2. Aggregator只做编排，不做核心规则。
3. Repository只查真实表。
4. 没有源字段时留空，不Mock。
5. 历史future_*字段只用于历史统计和回测展示。
6. 不输出买入、卖出、持有、推荐、目标价。
