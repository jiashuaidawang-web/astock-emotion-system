# 第七步第十六段：AgentAuditExecutor 研发审计真实算法

生成日期：2026-06-29

## 本次完成内容

这次把 `AgentAuditExecutor` 从执行骨架推进到真实算法落地。

## 新增类

```text
AgentAuditFileSnapshot
AgentAuditIssue
AgentAuditRuleHit
AgentAuditLineageIssue
AgentReleaseGateCheck
AgentAuditResultBundle
AgentAuditLineageRepository
AgentAuditLineageRepositoryImpl
AgentAuditLineageSql
AgentAuditProjectScanner
AgentAuditRedLineScanner
AgentAuditLineageChecker
AgentAuditRuleHitAggregator
AgentReleaseGateService
AgentAuditExecutionService
AgentAuditOutputRowBuilder
```

## 已落地真实链路

```text
JavaAgentAuditExecutor
-> EngineExecutionTemplate
-> RuleVersionGuardService
-> AgentAuditExecutionService
   -> AgentAuditProjectScanner
   -> AgentAuditRedLineScanner
      -> Mock检测
      -> 交易建议词检测
      -> future函数检测
      -> Controller算法职责检测
      -> Engine空实现检测
   -> AgentAuditLineageChecker
      -> page_contract_field_lineage
   -> AgentAuditRuleHitAggregator
   -> AgentReleaseGateService
-> AgentAuditOutputRowBuilder
-> EngineSnapshotWriteService
-> ClickHouse:
   - agent_audit_code_scan_detail
   - agent_audit_data_lineage_detail
   - agent_audit_rule_hit_detail
   - agent_audit_release_gate_detail
-> algorithm_task_log
```

## 已落地审计规则

```text
MOCK_DETECTED
TRADING_ADVICE_WORD_DETECTED
FUTURE_FUNCTION_RISK
CONTROLLER_ALGORITHM_RISK
ENGINE_EMPTY_COMPUTE
FIELD_LINEAGE_AUDIT
```

## 交易建议词检测

检测词包括：

```text
买入
卖出
持有
推荐
目标价
加仓
清仓
BUY
SELL
HOLD
TARGET_PRICE
RECOMMEND
```

## future函数检测

检测词包括：

```text
future_1d_return
future_3d_return
future_5d_return
future_10d_return
future1d_return
future3d_return
future5d_return
future10d_return
following_return
max_drawdown
```

允许出现位置：

```text
Backtest相关代码
Historical相关代码
SQL契约说明
README说明
```

其他T日判断、相似度匹配、模式条件、风控代码中命中则记为 BLOCKER。

## 字段血缘审计

读取：

```text
page_contract_field_lineage
```

检查：

```text
1. audit_passed=false
2. source_type 为 MYSQL / CLICKHOUSE 但 source_table 缺失
3. source_type 为 MYSQL / CLICKHOUSE 但 source_column 缺失
4. 血缘表为空
```

## 发布闸门已落地

```text
GATE_NO_MOCK
GATE_NO_TRADING_ADVICE
GATE_NO_FUTURE_LEAKAGE
GATE_NO_EMPTY_ENGINE
GATE_FIELD_LINEAGE
```

任何 BLOCKER 都会导致对应闸门：

```text
gate_status = BLOCKED
passed = false
```

## 输出表

```text
agent_audit_code_scan_detail
agent_audit_data_lineage_detail
agent_audit_rule_hit_detail
agent_audit_release_gate_detail
```

## 重要边界

1. AgentAuditExecutor 不依赖T日ClickHouse业务快照。
2. 它审计工程源码与字段血缘，因此关闭单日快照完整性检查。
3. 仍然保留规则版本校验、任务日志、输出表白名单、失败状态闭环。
4. 默认扫描 `System.getProperty("user.dir")`，后续可以从 `paramJson` 扩展 `projectRootPath`。
5. 审计结果只写入审计快照表，不改动源码。
6. BLOCKER 不等于自动修复，只阻断发布闸门。

## 新增 SQL 契约说明

```text
sql/16_agent_audit_executor_output_contract.sql
```

## 下一步建议

继续第七步第十七段：

```text
做一次全工程编译修复与启动闭环清算：
修复当前所有可能的编译错误、接口签名不一致、字段方法缺失、构造器注入冲突；
然后补齐最终 README_RUN.md、初始化SQL索引脚本和启动顺序。
```
