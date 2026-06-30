# 第十二步：15页面工业级可视化组件拆分

生成日期：2026-06-29

## 本次完成内容

这次把第十一步的 JSON Viewer 页面升级为真实工业级可视化组件。

## 新增组件

```text
src/components/visual/KpiGrid.vue
src/components/visual/SmartTable.vue
src/components/visual/RankingPanel.vue
src/components/visual/EmotionStateMachine.vue
src/components/visual/ProgressPanel.vue
src/components/visual/BacktestStatsChart.vue
src/components/visual/GenericVisualPage.vue
```

## 新增工具

```text
src/utils/dataExtract.ts
```

用于统一处理：

```text
1. 后端字段 camelCase / snake_case 兼容
2. 任意PageVO数组提取
3. 表格列自动推断
4. 排行分数字段自动识别
5. 状态标签映射
6. 百分比进度条换算
```

## 页面升级

15个页面已经从：

```text
JsonViewer
```

升级为：

```text
KPI卡片
核心排行
工业表格
状态标签
数据完整性状态
```

特殊页面增强：

```text
情绪周期状态机页：
- EmotionStateMachine 十阶段状态机

回测实验室 / 回测报告详情：
- BacktestStatsChart 回测统计图

一键跑批区域：
- ProgressPanel Engine执行进度面板
```

## 15页面覆盖

```text
今日市场总览驾驶舱
历史相似行情匹配页
情绪周期状态机页
历史周期样本库页
主线题材雷达页
板块强度排行页
龙头梯队监控页
龙头个股画像页
买点条件判定页
风控与失效信号页
回测实验室
回测报告详情页
每日复盘工作台
规则版本管理页
Agent研发审计页
```

## 保持的红线

```text
1. 前端不Mock数据。
2. 前端不计算核心业务结论。
3. 前端只做展示型聚合、排序和字段兼容。
4. 买点条件页面只展示条件状态，不展示交易建议。
5. 回测图只展示历史统计，不参与T日判断。
6. 进度面板只展示后端一键跑批返回结果，不重排Engine依赖。
```

## 启动

```bash
cd astock-frontend
npm install
npm run dev
```

## 下一步建议

进入第十三步：

```text
前端15页面深度业务化：
按每个PageVO字段设计专属卡片、专属表格列、专属状态颜色、专属业务区块，
从“通用工业组件”升级到“一页一产品形态”。
```
