import type { BasePageVO } from './common'

export interface MarketDashboardVO extends BasePageVO {
  marketBreadthScore?: number
  profitEffectScore?: number
  lossEffectScore?: number
  primaryStage?: string
  primaryStageName?: string
}

export interface HistoricalSimilarityPageVO extends BasePageVO {
  matches?: unknown[]
  factorDetails?: unknown[]
}

export interface EmotionCycleStateMachineVO extends BasePageVO {
  primaryStage?: string
  primaryStageName?: string
  stageScores?: unknown[]
  transitions?: unknown[]
}

export interface HistoricalCycleSamplePageVO extends BasePageVO {
  records?: unknown[]
  samples?: unknown[]
  total?: number
}

export interface MainlineRadarPageVO extends BasePageVO {
  mainlines?: unknown[]
  switches?: unknown[]
}

export interface SectorStrengthPageVO extends BasePageVO {
  sectors?: unknown[]
}

export interface LeaderLadderPageVO extends BasePageVO {
  ladders?: unknown[]
  leaders?: unknown[]
}

export interface LeaderProfilePageVO extends BasePageVO {
  stockCode?: string
  stockName?: string
  profile?: Record<string, unknown>
}

export interface PatternConditionPageVO extends BasePageVO {
  signals?: unknown[]
  riskVetoes?: unknown[]
}

export interface RiskControlPageVO extends BasePageVO {
  riskSignals?: unknown[]
  riskDetails?: unknown[]
}

export interface BacktestLabPageVO extends BasePageVO {
  reports?: unknown[]
  layerStats?: unknown[]
}

export interface BacktestReportDetailVO extends BasePageVO {
  reportId?: string | number
  signalDetails?: unknown[]
  performanceDetails?: unknown[]
  layerStats?: unknown[]
  failureCases?: unknown[]
}

export interface DailyReviewWorkbenchVO extends BasePageVO {
  reviewItems?: unknown[]
  summary?: Record<string, unknown>
}

export interface RuleVersionManagePageVO extends BasePageVO {
  versions?: unknown[]
}

export interface AgentAuditDashboardVO extends BasePageVO {
  gates?: unknown[]
  issues?: unknown[]
  ruleHits?: unknown[]
}

export type CycleSamplePageVO = HistoricalCycleSamplePageVO

export type AnyPageVO =
  | MarketDashboardVO
  | HistoricalSimilarityPageVO
  | EmotionCycleStateMachineVO
  | HistoricalCycleSamplePageVO
  | MainlineRadarPageVO
  | SectorStrengthPageVO
  | LeaderLadderPageVO
  | LeaderProfilePageVO
  | PatternConditionPageVO
  | RiskControlPageVO
  | BacktestLabPageVO
  | BacktestReportDetailVO
  | DailyReviewWorkbenchVO
  | RuleVersionManagePageVO
  | AgentAuditDashboardVO
