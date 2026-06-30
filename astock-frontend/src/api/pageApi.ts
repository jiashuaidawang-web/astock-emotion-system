import { request } from './http'
import type {
  AgentAuditDashboardVO,
  BacktestLabPageVO,
  BacktestReportDetailVO,
  CycleSamplePageVO,
  DailyReviewWorkbenchVO,
  EmotionCycleStateMachineVO,
  HistoricalSimilarityPageVO,
  LeaderLadderPageVO,
  LeaderProfilePageVO,
  MainlineRadarPageVO,
  MarketDashboardVO,
  PatternConditionPageVO,
  RiskControlPageVO,
  RuleVersionManagePageVO,
  SectorStrengthPageVO
} from '@/types/page-vo'
import type { PageQuery } from '@/types/common'

function params(query: PageQuery) {
  const p: Record<string, unknown> = {}
  Object.entries(query).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') p[key] = value
  })
  return p
}

export const pageApi = {
  marketDashboard: (query: PageQuery) =>
    request<MarketDashboardVO>({ url: '/api/dashboard/market', method: 'GET', params: params(query) }),

  similarity: (query: PageQuery) =>
    request<HistoricalSimilarityPageVO>({ url: '/api/similarity/market', method: 'GET', params: params(query) }),

  emotionCycle: (query: PageQuery) =>
    request<EmotionCycleStateMachineVO>({ url: '/api/emotion-cycle/state-machine', method: 'GET', params: params(query) }),

  cycleSamples: (query: PageQuery) =>
    request<CycleSamplePageVO>({ url: '/api/cycle-samples/page', method: 'GET', params: params(query) }),

  mainlineRadar: (query: PageQuery) =>
    request<MainlineRadarPageVO>({ url: '/api/mainlines/radar', method: 'GET', params: params(query) }),

  sectorStrength: (query: PageQuery) =>
    request<SectorStrengthPageVO>({ url: '/api/sectors/strength', method: 'GET', params: params(query) }),

  leaderLadder: (query: PageQuery) =>
    request<LeaderLadderPageVO>({ url: '/api/leaders/ladder', method: 'GET', params: params(query) }),

  leaderProfile: (stockCode: string, query: PageQuery) =>
    request<LeaderProfilePageVO>({ url: `/api/leaders/${stockCode}/profile`, method: 'GET', params: params(query) }),

  patternConditions: (query: PageQuery) =>
    request<PatternConditionPageVO>({ url: '/api/patterns/conditions', method: 'GET', params: params(query) }),

  riskControl: (query: PageQuery) =>
    request<RiskControlPageVO>({ url: '/api/risks/control', method: 'GET', params: params(query) }),

  backtestLab: (query: PageQuery) =>
    request<BacktestLabPageVO>({ url: '/api/backtests/lab', method: 'GET', params: params(query) }),

  backtestReport: (reportId: string | number, query: PageQuery) =>
    request<BacktestReportDetailVO>({ url: `/api/backtests/reports/${reportId}`, method: 'GET', params: params(query) }),

  dailyReview: (query: PageQuery) =>
    request<DailyReviewWorkbenchVO>({ url: '/api/reviews/daily/workbench', method: 'GET', params: params(query) }),

  ruleVersions: (query: PageQuery) =>
    request<RuleVersionManagePageVO>({ url: '/api/rules/versions/page', method: 'GET', params: params(query) }),

  agentAudit: (query: PageQuery) =>
    request<AgentAuditDashboardVO>({ url: '/api/agent-audit/dashboard', method: 'GET', params: params(query) })
}
