# 第七步下半段：Repository + Mapper + SQL + 字段血缘 + 数据完整性服务

生成日期：2026-06-29

## 本次补齐内容

1. 新增全局数据完整性检查服务：
   - DataQualityQueryService
   - DataQualityQueryServiceImpl
   - SnapshotExistenceRepository
   - ClickHouseSnapshotExistenceRepository
   - DataQualityCheckLogMapper
   - DataQualityController

2. 新增字段血缘查询能力：
   - PageFieldLineageVO
   - PageFieldLineageMapper
   - PageFieldLineageQueryService
   - PageFieldLineageController

3. 每个业务模块新增：
   - domain/repository/*SnapshotRepository
   - infrastructure/clickhouse/*ClickHouseSql
   - infrastructure/clickhouse/*ClickHouseRepository
   - 规则类模块使用 infrastructure/mysql/*MysqlMapper

4. Aggregator 已升级：
   - 先执行页面级 RequiredSnapshot 检查
   - 关键快照缺失时返回 dataComplete=false
   - 快照存在时查询真实 Repository
   - Repository 无记录时拒绝 Mock
   - 不临时编造业务结论

5. VO 公共字段增加 @PageFieldLineage 注解：
   - tradeDate
   - dataComplete
   - dataStatusText
   - conclusion
   - riskTips

## 仍然保留的红线

1. Controller 不写业务逻辑。
2. Aggregator 不塞 Mock。
3. 前端不计算核心业务规则。
4. 未接入真实字段映射前，只允许返回数据状态，不允许伪造页面结论。
5. future_* 字段只能用于历史统计和回测验证。
6. 买点、复盘、回测、规则、审计文案不得出现交易指令。

## 下一步建议

继续第七步第三段：

```text
补齐每个 PageVO 的字段级 Repository 映射和 Converter，
把 ClickHouse 查询结果真正转换为完整 PageVO，而不是只返回数据状态。
```
