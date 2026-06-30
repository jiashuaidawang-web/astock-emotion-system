import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/dashboard/market' },
  { path: '/dashboard/market', component: () => import('@/pages/MarketDashboardPage.vue'), meta: { title: '今日市场总览驾驶舱' } },
  { path: '/similarity/market', component: () => import('@/pages/HistoricalSimilarityPage.vue'), meta: { title: '历史相似行情匹配页' } },
  { path: '/emotion-cycle/state-machine', component: () => import('@/pages/EmotionCycleStateMachinePage.vue'), meta: { title: '情绪周期状态机页' } },
  { path: '/cycle-samples/page', component: () => import('@/pages/HistoricalCycleSamplePage.vue'), meta: { title: '历史周期样本库页' } },
  { path: '/mainlines/radar', component: () => import('@/pages/MainlineRadarPage.vue'), meta: { title: '主线题材雷达页' } },
  { path: '/sectors/strength', component: () => import('@/pages/SectorStrengthPage.vue'), meta: { title: '板块强度排行页' } },
  { path: '/leaders/ladder', component: () => import('@/pages/LeaderLadderPage.vue'), meta: { title: '龙头梯队监控页' } },
  { path: '/leaders/:stockCode/profile', component: () => import('@/pages/LeaderProfilePage.vue'), meta: { title: '龙头个股画像页' } },
  { path: '/patterns/conditions', component: () => import('@/pages/PatternConditionPage.vue'), meta: { title: '买点条件判定页' } },
  { path: '/risks/control', component: () => import('@/pages/RiskControlPage.vue'), meta: { title: '风控与失效信号页' } },
  { path: '/backtests/lab', component: () => import('@/pages/BacktestLabPage.vue'), meta: { title: '回测实验室' } },
  { path: '/backtests/reports/:reportId', component: () => import('@/pages/BacktestReportDetailPage.vue'), meta: { title: '回测报告详情页' } },
  { path: '/reviews/daily/workbench', component: () => import('@/pages/DailyReviewWorkbenchPage.vue'), meta: { title: '每日复盘工作台' } },
  { path: '/rules/versions/page', component: () => import('@/pages/RuleVersionManagePage.vue'), meta: { title: '规则版本管理页' } },
  { path: '/agent-audit/dashboard', component: () => import('@/pages/AgentAuditDashboardPage.vue'), meta: { title: 'Agent研发审计页' } }
]

export const router = createRouter({
  history: createWebHistory(),
  routes
})
