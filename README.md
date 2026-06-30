# astock-emotion-system

生成日期：2026-06-29

这是【A股情绪周期复盘与历史相似行情回测系统】第七步工程骨架。

## 已落地内容

- Maven 多模块父工程
- astock-app 启动模块
- astock-common 公共模块
- astock-infrastructure 基础设施模块
- astock-api 协议模块
- astock-modules 聚合模块
- 13 个业务模块
- 15 个页面 Controller 骨架
- 15 个 QueryService 骨架
- 15 个 Aggregator 骨架
- 8 个核心 Engine 接口骨架
- 无 Mock 返回
- 未接入真实数据源时显式抛出 DATA_NOT_READY

## 模块列表

- module-market
- module-emotion
- module-similarity
- module-sample
- module-mainline
- module-sector
- module-leader
- module-pattern
- module-risk
- module-backtest
- module-review
- module-rule
- module-agent-audit

## 启动方式

```bash
cd astock-emotion-system
mvn clean package -DskipTests
java -jar astock-app/target/astock-app-1.0.0-SNAPSHOT.jar
```

## 红线

1. Controller 只调用 Service。
2. Service 只编排，不写评分规则。
3. Aggregator 只聚合真实落库数据。
4. 未接入 Repository 前不得返回假数据。
5. 买点、回测、复盘、规则、审计文案不得出现交易指令。
