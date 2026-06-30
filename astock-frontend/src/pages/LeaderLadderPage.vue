<script setup lang="ts">
import PageShell from '@/components/PageShell.vue'
import LeaderLadderBusiness from '@/components/business/LeaderLadderBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const { loading, error, data, dataComplete, reload } = usePageQuery(pageApi.leaderLadder)
</script>

<template>
  <PageShell
    title="龙头梯队监控页"
    subtitle="GET /api/leaders/ladder"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <LeaderLadderBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
