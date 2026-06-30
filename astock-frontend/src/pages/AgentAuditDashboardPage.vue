<script setup lang="ts">
import PageShell from '@/components/PageShell.vue'
import AgentAuditBusiness from '@/components/business/AgentAuditBusiness.vue'
import { pageApi } from '@/api/pageApi'
import { usePageQuery } from '@/composables/usePageQuery'

const { loading, error, data, dataComplete, reload } = usePageQuery(pageApi.agentAudit)
</script>

<template>
  <PageShell
    title="Agent研发审计页"
    subtitle="GET /api/agent-audit/dashboard"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <AgentAuditBusiness :key="data ? JSON.stringify(data).length : 0" :data="data" />
  </PageShell>
</template>
