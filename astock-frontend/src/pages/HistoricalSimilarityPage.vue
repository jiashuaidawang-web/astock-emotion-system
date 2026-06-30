<script setup lang="ts">
import PageShell from '@/components/PageShell.vue'
import HistoricalSimilarityBusiness from '@/components/business/HistoricalSimilarityBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const { loading, error, data, dataComplete, reload } = usePageQuery(pageApi.similarity)
</script>

<template>
  <PageShell
    title="历史相似行情匹配页"
    subtitle="GET /api/similarity/market"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <HistoricalSimilarityBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
