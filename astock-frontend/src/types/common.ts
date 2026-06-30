export type Nullable<T> = T | null | undefined

export interface PageQuery {
  tradeDate?: string
  marketScope?: string
  ruleVersionId?: number
  pageNo?: number
  pageSize?: number
  stockCode?: string
  reportId?: string | number
}

export interface PageDataQuality {
  dataComplete?: boolean
  data_complete?: boolean
  missingReason?: string
  missing_reason?: string
  requiredSnapshots?: unknown[]
}

export interface BasePageVO {
  tradeDate?: string
  marketScope?: string
  dataComplete?: boolean
  data_complete?: boolean
  dataQuality?: PageDataQuality
  pageCode?: string
  ruleVersionId?: number
  [key: string]: unknown
}

export function resolveDataComplete(data: unknown): boolean {
  const vo = data as BasePageVO | undefined
  if (!vo) return false
  if (typeof vo.dataComplete === 'boolean') return vo.dataComplete
  if (typeof vo.data_complete === 'boolean') return vo.data_complete
  if (vo.dataQuality && typeof vo.dataQuality.dataComplete === 'boolean') return vo.dataQuality.dataComplete
  if (vo.dataQuality && typeof vo.dataQuality.data_complete === 'boolean') return vo.dataQuality.data_complete
  return true
}

export function getTrendClass(value: unknown): string {
  const num = Number(value)
  if (Number.isNaN(num)) return 'text-neutral'
  if (num > 0) return 'text-up'
  if (num < 0) return 'text-down'
  return 'text-neutral'
}
