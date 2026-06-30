<script setup lang="ts">
import PageShell from '@/components/PageShell.vue'
import RiskControlBusiness from '@/components/business/RiskControlBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const { loading, error, data, dataComplete, reload } = usePageQuery(pageApi.riskControl)
</script>

<template>
  <PageShell
    title="风控与失效信号页"
    subtitle="GET /api/risks/control"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <RiskControlBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
