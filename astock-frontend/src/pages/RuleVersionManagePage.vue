<script setup lang="ts">
import PageShell from '@/components/PageShell.vue'
import RuleVersionBusiness from '@/components/business/RuleVersionBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const { loading, error, data, dataComplete, reload } = usePageQuery(pageApi.ruleVersions)
</script>

<template>
  <PageShell
    title="规则版本管理页"
    subtitle="GET /api/rules/versions/page"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <RuleVersionBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
