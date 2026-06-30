# 第七步第三段：PageVO字段级Repository映射 + Converter

生成日期：2026-06-29

## 本次补齐内容

1. 新增通用转换层：
   - PageConverter
   - MapFieldReader
   - FieldMapping
   - PageFieldMappingRegistry

2. 每个业务模块新增：
   - infrastructure/converter/*FieldMappingRegistry
   - infrastructure/converter/*Converter

3. Aggregator再次升级：
   - 解析 tradeDate / marketScope
   - 执行 RequiredSnapshot 数据完整性检查
   - 调用真实 Repository 查询 ClickHouse/MySQL
   - 调用 Converter 将 Map 行转换为 PageVO
   - Repository无数据时，不使用Mock，返回 dataComplete=false

4. 新增字段血缘初始化脚本：
   - sql/07_page_field_lineage_seed.sql

## 当前转换范围

当前工程骨架中的 PageVO 还是第七步骨架版，只包含公共字段：

- tradeDate
- dataComplete
- dataStatusText
- conclusion
- riskTips

所以本轮已经对这些字段完成了真实 Repository -> Converter -> PageVO 转换链路。

## 下一步

进入第七步第四段时，应补齐15个页面完整业务字段VO类，并继续扩展 Converter 的字段级映射：

```text
ClickHouse Row / MySQL Row
-> Converter
-> PageVO完整字段
-> 前端只展示，不计算业务规则
```

## 红线

1. Converter只能做类型转换和字段映射，不能做业务评分。
2. Aggregator只能编排质量检查、Repository、Converter，不能写核心规则。
3. Repository只能查真实表，不能返回Mock。
4. PageVO字段必须登记 page_contract_field_lineage。
