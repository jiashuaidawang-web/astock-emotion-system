# 第十一步：前端15页面真实API联调层

生成日期：2026-06-29

## 本次完成内容

本次新增 `astock-frontend` Vue3 前端工程，补齐15个页面的真实API联调层。

## 技术栈

```text
Vue 3
Vite
TypeScript
Tailwind CSS
Element Plus
Axios
Vue Router
```

## 新增前端目录

```text
astock-frontend
├── package.json
├── vite.config.ts
├── tailwind.config.js
├── postcss.config.js
├── tsconfig.json
├── index.html
└── src
    ├── api
    ├── components
    ├── composables
    ├── pages
    ├── router
    ├── styles
    ├── types
    ├── App.vue
    └── main.ts
```

## 真实API Client

```text
src/api/http.ts
src/api/pageApi.ts
src/api/engineApi.ts
```

15个页面接口已全部接入：

```text
GET /api/dashboard/market
GET /api/similarity/market
GET /api/emotion-cycle/state-machine
GET /api/cycle-samples/page
GET /api/mainlines/radar
GET /api/sectors/strength
GET /api/leaders/ladder
GET /api/leaders/{stockCode}/profile
GET /api/patterns/conditions
GET /api/risks/control
GET /api/backtests/lab
GET /api/backtests/reports/{reportId}
GET /api/reviews/daily/workbench
GET /api/rules/versions/page
GET /api/agent-audit/dashboard
```

## 统一PageVO类型

```text
src/types/common.ts
src/types/page-vo.ts
```

每个页面使用统一 `BasePageVO`：

```text
tradeDate
marketScope
dataComplete
dataQuality
pageCode
ruleVersionId
```

## loading / error / dataComplete 状态

统一封装：

```text
src/composables/usePageQuery.ts
src/components/DataStatusBar.vue
src/components/PageShell.vue
```

所有页面都具备：

```text
loading
error
dataComplete
reload
```

## 一键跑批按钮

新增：

```text
src/components/EngineBatchPanel.vue
src/composables/useEngineBatch.ts
src/api/engineApi.ts
```

接口：

```http
POST /api/engines/batch/daily/run
```

支持：

```text
tradeDate
marketScope
ruleVersionId
dataCheckEnabled
continueOnFailure
runBacktest
runAgentAudit
rerunRiskAfterPattern
paramJson
```

并展示：

```text
batchId
batchStatus
successStepCount
failedStepCount
costMillis
每个Engine step的状态、输出行数、失败原因
```

## 启动命令

```bash
cd astock-frontend
npm install
npm run dev
```

默认代理：

```text
/api -> http://127.0.0.1:8080
```

也可以配置：

```bash
VITE_API_BASE_URL=http://127.0.0.1:8080 npm run dev
```

## 红线保持

```text
1. 前端不Mock数据。
2. 前端不计算核心业务结论。
3. 前端只展示后端PageVO。
4. 前端只展示条件状态和风险状态。
5. 前端不输出交易建议。
6. 一键跑批只触发后端编排，不在前端重排Engine依赖。
```

## 当前边界

当前15个页面先以真实API JSON Viewer方式接入，保证接口、状态、错误、dataComplete完整闭环。

下一步可以进入：

```text
第十二步：15页面工业级可视化组件拆分
把每个PageVO拆成真实卡片、表格、图表和状态机视图。
```
