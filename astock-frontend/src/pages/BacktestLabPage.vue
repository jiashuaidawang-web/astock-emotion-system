<script setup lang="ts">
import PageShell from '@/components/PageShell.vue'
import BacktestLabBusiness from '@/components/business/BacktestLabBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const { loading, error, data, dataComplete, reload } = usePageQuery(pageApi.backtestLab)
</script>

<template>
  <PageShell
    title="回测实验室"
    subtitle="GET /api/backtests/lab"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <BacktestLabBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
