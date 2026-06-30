<script setup lang="ts">
import PageShell from '@/components/PageShell.vue'
import EmotionCycleBusiness from '@/components/business/EmotionCycleBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const { loading, error, data, dataComplete, reload } = usePageQuery(pageApi.emotionCycle)
</script>

<template>
  <PageShell
    title="情绪周期状态机页"
    subtitle="GET /api/emotion-cycle/state-machine"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <EmotionCycleBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
