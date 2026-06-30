<script setup lang="ts">
import PageShell from '@/components/PageShell.vue'
import SectorStrengthBusiness from '@/components/business/SectorStrengthBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const { loading, error, data, dataComplete, reload } = usePageQuery(pageApi.sectorStrength)
</script>

<template>
  <PageShell
    title="板块强度排行页"
    subtitle="GET /api/sectors/strength"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <SectorStrengthBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
