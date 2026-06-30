# 第十三步：前端15页面深度业务化

生成日期：2026-06-29

## 本次完成内容

本次把第十二步的“通用工业组件页面”升级为“一页一产品形态”。

每个页面都拥有专属业务组件，不再只依赖 GenericVisualPage。

## 新增公共业务组件

```text
src/components/business/BusinessBlock.vue
src/components/business/StatusBadge.vue
src/components/business/ScoreBar.vue
```

## 新增15个页面专属业务组件

```text
src/components/business/MarketDashboardBusiness.vue
src/components/business/HistoricalSimilarityBusiness.vue
src/components/business/EmotionCycleBusiness.vue
src/components/business/HistoricalCycleSampleBusiness.vue
src/components/business/MainlineRadarBusiness.vue
src/components/business/SectorStrengthBusiness.vue
src/components/business/LeaderLadderBusiness.vue
src/components/business/LeaderProfileBusiness.vue
src/components/business/PatternConditionBusiness.vue
src/components/business/RiskControlBusiness.vue
src/components/business/BacktestLabBusiness.vue
src/components/business/BacktestReportBusiness.vue
src/components/business/DailyReviewBusiness.vue
src/components/business/RuleVersionBusiness.vue
src/components/business/AgentAuditBusiness.vue
```

## 页面业务化重点

### 1. 今日市场总览驾驶舱

```text
市场宽度
赚钱效应
亏钱效应
上涨/下跌/涨停家数
市场温度结构
市场核心因子明细
```

### 2. 历史相似行情匹配页

```text
Top1相似样本九维拆解
历史相似样本排行
九维因子明细
future_* 不参与T日匹配提示
```

### 3. 情绪周期状态机页

```text
十阶段状态机
当前阶段Badge
阶段评分明细
阶段转移路径
```

### 4. 历史周期样本库页

```text
样本总数
高质量样本排行
历史周期样本列表
阶段覆盖统计
```

### 5. 主线题材雷达页

```text
最强主线六维结构
主线强度排行
主线切换记录
禁止涨幅第一/涨停最多等同主线提示
```

### 6. 板块强度排行页

```text
板块强度排行
涨停贡献
成交额
持续天数
板块明细表
```

### 7. 龙头梯队监控页

```text
市场龙头七维结构
龙头综合分排行
梯队结构表
禁止最高板等同市场总龙头提示
```

### 8. 龙头个股画像页

```text
个股画像评分结构
辨识度
带动性
承接
负反馈
画像事件明细
```

### 9. 买点条件判定页

```text
条件信号
风险否决
条件分排行
风险否决覆盖
合规提示：不是交易建议
```

### 10. 风控与失效信号页

```text
综合风险分
风险等级
风险动作
风险因子排行
风控上级保护层状态
```

### 11-12. 回测实验室 / 回测报告详情页

```text
回测统计图
信号样本
失败样本归因
胜率
平均收益
平均回撤
风控过滤数
```

### 13. 每日复盘工作台

```text
复盘摘要
复盘任务清单
主线状态
风险状态
```

### 14. 规则版本管理页

```text
规则版本数
启用版本
草稿版本
归档版本
规则版本表
```

### 15. Agent研发审计页

```text
发布闸门总览
阻断项
代码问题
审计规则命中排行
发布闸门明细
代码审计问题
```

## 保持红线

```text
1. 前端仍然不Mock数据。
2. 前端仍然不计算核心业务结论。
3. 前端只做展示、排序、字段兼容、状态渲染。
4. 买点条件页面明确提示不是交易建议。
5. 主线页面明确禁止涨幅第一/涨停最多等同主线。
6. 龙头页面明确禁止最高板等同市场总龙头。
7. 回测页面只展示历史统计，不参与T日判断。
8. Agent审计页只展示发布闸门，不自动修改代码。
```

## 启动

```bash
cd astock-frontend
npm install
npm run dev
```

## 下一步建议

进入第十四步：

```text
前端联调可编译清算：
修复Vue/TS潜在编译错误、补齐类型缺口、统一路由懒加载、校验npm build，
并生成前端运行手册和后端联调清单。
```
