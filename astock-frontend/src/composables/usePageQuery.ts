import { onMounted, ref, shallowRef } from 'vue'
import type { PageQuery } from '@/types/common'
import { resolveDataComplete } from '@/types/common'

export function usePageQuery<T>(loader: (query: PageQuery) => Promise<T>, initialQuery: PageQuery = {}) {
  const loading = ref(false)
  const error = ref<string>('')
  const data = shallowRef<T | null>(null)
  const dataComplete = ref(false)
  const query = ref<PageQuery>({
    marketScope: 'A_SHARE',
    ...initialQuery
  })

  async function reload(patch: PageQuery = {}) {
    query.value = { ...query.value, ...patch }
    loading.value = true
    error.value = ''
    try {
      const result = await loader(query.value)
      data.value = result
      dataComplete.value = resolveDataComplete(result)
    } catch (err) {
      const e = err as Error
      error.value = e.message || '页面接口请求失败'
      data.value = null
      dataComplete.value = false
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    reload()
  })

  return {
    query,
    loading,
    error,
    data,
    dataComplete,
    reload
  }
}
