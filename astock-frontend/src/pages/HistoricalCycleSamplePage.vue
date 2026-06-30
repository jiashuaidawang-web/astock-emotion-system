<script setup lang="ts">
import PageShell from '@/components/PageShell.vue'
import HistoricalCycleSampleBusiness from '@/components/business/HistoricalCycleSampleBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const { loading, error, data, dataComplete, reload } = usePageQuery(pageApi.cycleSamples)
</script>

<template>
  <PageShell
    title="历史周期样本库页"
    subtitle="GET /api/cycle-samples/page"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <HistoricalCycleSampleBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
