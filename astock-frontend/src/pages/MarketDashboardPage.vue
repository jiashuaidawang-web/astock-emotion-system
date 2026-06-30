<script setup lang="ts">
import PageShell from '@/components/PageShell.vue'
import MarketDashboardBusiness from '@/components/business/MarketDashboardBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const { loading, error, data, dataComplete, reload } = usePageQuery(pageApi.marketDashboard)
</script>

<template>
  <PageShell
    title="今日市场总览驾驶舱"
    subtitle="GET /api/dashboard/market"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
<!--    <MarketDashboardBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />-->
    <MarketDashboardBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
