<script setup lang="ts">
import PageShell from '@/components/PageShell.vue'
import MainlineRadarBusiness from '@/components/business/MainlineRadarBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const { loading, error, data, dataComplete, reload } = usePageQuery(pageApi.mainlineRadar)
</script>

<template>
  <PageShell
    title="主线题材雷达页"
    subtitle="GET /api/mainlines/radar"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <MainlineRadarBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
