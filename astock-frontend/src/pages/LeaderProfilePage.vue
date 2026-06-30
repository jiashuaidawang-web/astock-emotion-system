<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import PageShell from '@/components/PageShell.vue'
import LeaderProfileBusiness from '@/components/business/LeaderProfileBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const route = useRoute()
const stockCode = computed(() => String(route.params.stockCode || '000001'))

const { loading, error, data, dataComplete, reload } = usePageQuery(
  (query) => pageApi.leaderProfile(stockCode.value, query)
)
</script>

<template>
  <PageShell
    title="龙头个股画像页"
    :subtitle="`GET /api/leaders/${stockCode}/profile`"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <LeaderProfileBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
