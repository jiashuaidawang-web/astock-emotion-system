<script setup lang="ts">
import PageShell from '@/components/PageShell.vue'
import PatternConditionBusiness from '@/components/business/PatternConditionBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const { loading, error, data, dataComplete, reload } = usePageQuery(pageApi.patternConditions)
</script>

<template>
  <PageShell
    title="买点条件判定页"
    subtitle="GET /api/patterns/conditions；只展示条件状态，不输出交易建议"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <PatternConditionBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
