export interface EngineBatchRunRequest {
  tradeDate: string
  marketScope: string
  ruleVersionId?: number
  dataCheckEnabled: boolean
  continueOnFailure: boolean
  runBacktest: boolean
  runAgentAudit: boolean
  rerunRiskAfterPattern: boolean
  paramJson?: string
}

export interface EngineBatchStepResult {
  stepNo: number
  stepCode: string
  stepName: string
  engineName: string
  success: boolean
  taskId?: number
  outputRowCount?: number
  outputTables?: string[]
  failureReason?: string
  summaryText?: string
  startedAt?: string
  finishedAt?: string
  costMillis?: number
}

export interface EngineBatchRunResult {
  batchId: number
  tradeDate: string
  marketScope: string
  success: boolean
  batchStatus: string
  failureReason?: string
  totalStepCount: number
  successStepCount: number
  failedStepCount: number
  startedAt?: string
  finishedAt?: string
  costMillis?: number
  steps: EngineBatchStepResult[]
}
