<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import PageShell from '@/components/PageShell.vue'
import BacktestReportBusiness from '@/components/business/BacktestReportBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const route = useRoute()
const reportId = computed(() => String(route.params.reportId || '1'))

const { loading, error, data, dataComplete, reload } = usePageQuery(
  (query) => pageApi.backtestReport(reportId.value, query)
)
</script>

<template>
  <PageShell
    title="回测报告详情页"
    :subtitle="`GET /api/backtests/reports/${reportId}`"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <BacktestReportBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
