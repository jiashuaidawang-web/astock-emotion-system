export type AnyRecord = Record<string, unknown>

export function isRecord(value: unknown): value is AnyRecord {
  return Boolean(value) && typeof value === 'object' && !Array.isArray(value)
}

export function asRecord(value: unknown): AnyRecord {
  return isRecord(value) ? value : {}
}

export function asArray(value: unknown): AnyRecord[] {
  if (!Array.isArray(value)) return []
  return value.filter(isRecord)
}

export function getByPath(source: unknown, path: string): unknown {
  const parts = path.split('.').filter(Boolean)
  let current: unknown = source
  for (const part of parts) {
    if (!isRecord(current)) return undefined
    current = current[part]
  }
  return current
}

export function firstValue(source: unknown, keys: string[]): unknown {
  for (const key of keys) {
    const value = key.includes('.') ? getByPath(source, key) : asRecord(source)[key]
    if (value !== undefined && value !== null && value !== '') return value
  }
  return undefined
}

export function numberValue(source: unknown, keys: string[], fallback = 0): number {
  const value = firstValue(source, keys)
  const num = Number(value)
  return Number.isFinite(num) ? num : fallback
}

export function textValue(source: unknown, keys: string[], fallback = '-'): string {
  const value = firstValue(source, keys)
  if (value === undefined || value === null || value === '') return fallback
  return String(value)
}

export function boolValue(source: unknown, keys: string[], fallback = false): boolean {
  const value = firstValue(source, keys)
  if (typeof value === 'boolean') return value
  if (typeof value === 'number') return value !== 0
  if (typeof value === 'string') return ['true', '1', 'yes', 'Y', 'y'].includes(value)
  return fallback
}

export function findArray(source: unknown, keys: string[]): AnyRecord[] {
  for (const key of keys) {
    const value = key.includes('.') ? getByPath(source, key) : asRecord(source)[key]
    const rows = asArray(value)
    if (rows.length > 0) return rows
  }

  const record = asRecord(source)
  for (const value of Object.values(record)) {
    if (Array.isArray(value) && value.some(isRecord)) return asArray(value)
  }
  return []
}

export function flattenRecord(value: unknown): AnyRecord {
  const record = asRecord(value)
  const flat: AnyRecord = {}

  Object.entries(record).forEach(([key, item]) => {
    if (item === null || item === undefined) return
    if (typeof item === 'object') {
      flat[key] = Array.isArray(item) ? `${item.length} 条` : JSON.stringify(item)
    } else {
      flat[key] = item
    }
  })

  return flat
}

export function tableRows(source: unknown, keys: string[], limit = 80): AnyRecord[] {
  return findArray(source, keys).slice(0, limit).map(flattenRecord)
}

export function topRows(source: unknown, keys: string[], scoreKeys: string[], limit = 10): AnyRecord[] {
  return findArray(source, keys)
    .slice()
    .sort((a, b) => numberValue(b, scoreKeys) - numberValue(a, scoreKeys))
    .slice(0, limit)
    .map(flattenRecord)
}

export function displayColumns(rows: AnyRecord[], preferred: string[] = [], max = 8): string[] {
  const keys = new Set<string>()
  preferred.forEach(key => rows.some(row => row[key] !== undefined) && keys.add(key))
  rows.slice(0, 10).forEach(row => Object.keys(row).forEach(key => keys.add(key)))
  return Array.from(keys).slice(0, max)
}

export function formatCell(value: unknown): string {
  if (value === undefined || value === null || value === '') return '-'
  if (typeof value === 'number') {
    if (Math.abs(value) >= 100000000) return `${(value / 100000000).toFixed(2)}亿`
    if (Math.abs(value) >= 10000) return `${(value / 10000).toFixed(2)}万`
    return Number.isInteger(value) ? String(value) : value.toFixed(2)
  }
  if (typeof value === 'boolean') return value ? '是' : '否'
  return String(value)
}

export function statusType(status: unknown): 'success' | 'warning' | 'danger' | 'info' {
  const s = String(status || '').toUpperCase()
  if (['SUCCESS', 'PASS', 'NORMAL', 'DATA_COMPLETE', 'CONDITION_MET', 'EFFECTIVE', 'ACTIVE'].includes(s)) return 'success'
  if (['WARNING', 'CAUTION', 'OBSERVING', 'WEAK_MATCH', 'MEDIUM', 'DRAFT', 'NOTICE'].includes(s)) return 'warning'
  if (['FAILED', 'BLOCKED', 'RISK_VETO', 'PATTERN_INVALIDATED', 'DATA_BLOCK', 'HIGH', 'EXTREME', 'FATAL', 'BLOCKER'].includes(s)) return 'danger'
  return 'info'
}

export function percent(value: number, max = 100): number {
  if (!Number.isFinite(value)) return 0
  if (max <= 0) return 0
  return Math.max(0, Math.min(100, (value / max) * 100))
}

export function countByStatus(rows: AnyRecord[], keys: string[], status: string): number {
  return rows.filter(row => textValue(row, keys, '').toUpperCase() === status.toUpperCase()).length
}

export function sumNumber(rows: AnyRecord[], keys: string[]): number {
  return rows.reduce((total, row) => total + numberValue(row, keys), 0)
}
