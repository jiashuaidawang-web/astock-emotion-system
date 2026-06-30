import { ref, shallowRef } from 'vue'
import { engineApi } from '@/api/engineApi'
import type { EngineBatchRunRequest, EngineBatchRunResult } from '@/types/engine'
import dayjs from 'dayjs'

export function useEngineBatch() {
  const running = ref(false)
  const error = ref('')
  const result = shallowRef<EngineBatchRunResult | null>(null)

  const form = ref<EngineBatchRunRequest>({
    tradeDate: dayjs().format('YYYY-MM-DD'),
    marketScope: 'A_SHARE',
    ruleVersionId: 1,
    dataCheckEnabled: true,
    continueOnFailure: false,
    runBacktest: false,
    runAgentAudit: true,
    rerunRiskAfterPattern: true,
    paramJson: '{}'
  })

  async function run() {
    running.value = true
    error.value = ''
    try {
      result.value = await engineApi.runDailyBatch(form.value)
    } catch (err) {
      const e = err as Error
      error.value = e.message || '一键跑批失败'
    } finally {
      running.value = false
    }
  }

  return {
    form,
    running,
    error,
    result,
    run
  }
}
