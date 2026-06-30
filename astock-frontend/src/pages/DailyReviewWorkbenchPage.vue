<script setup lang="ts">
import PageShell from '@/components/PageShell.vue'
import DailyReviewBusiness from '@/components/business/DailyReviewBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const { loading, error, data, dataComplete, reload } = usePageQuery(pageApi.dailyReview)
</script>

<template>
  <PageShell
    title="每日复盘工作台"
    subtitle="GET /api/reviews/daily/workbench"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <DailyReviewBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
