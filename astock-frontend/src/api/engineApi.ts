import { request } from './http'
import type { EngineBatchRunRequest, EngineBatchRunResult } from '@/types/engine'

export const engineApi = {
  runDailyBatch: (body: EngineBatchRunRequest) =>
    request<EngineBatchRunResult>({
      url: '/api/engines/batch/daily/run',
      method: 'POST',
      data: body
    })
}
