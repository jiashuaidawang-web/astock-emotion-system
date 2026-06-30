<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import RankingPanel from '@/components/visual/RankingPanel.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import BusinessBlock from './BusinessBlock.vue'
import StatusBadge from './StatusBadge.vue'
import { findArray, numberValue, tableRows, textValue, topRows, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
const risks = topRows(d, ['riskSignals','riskDetails','records','rows'], ['risk_score'], 10)
const first = findArray(d, ['riskSignals','records','rows'])[0] as AnyRecord | undefined

const kpis = [
  { label: '综合风险分', value: numberValue(first || d, ['risk_score','riskScore']).toFixed(2) },
  { label: '风险等级', value: textValue(first || d, ['risk_level','riskLevel']) },
  { label: '风险动作', value: textValue(first || d, ['risk_action','riskAction']) },
  { label: '风险因子数', value: findArray(d, ['riskSignals','riskDetails','records','rows']).length }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />
    <BusinessBlock title="上级保护层状态" subtitle="风控优先级高于模式条件。">
      <div class="flex items-center gap-3">
        <StatusBadge :value="textValue(first || d, ['risk_action','riskAction'])" />
        <span class="text-xs text-terminal-sub">RISK_VETO / PATTERN_INVALIDATED 会覆盖模式条件。</span>
      </div>
    </BusinessBlock>
    <div class="grid gap-3 xl:grid-cols-2">
      <RankingPanel title="风险因子排行" :rows="risks" :name-keys="['risk_name','risk_code']" :score-keys="['risk_score']" />
      <SmartTable title="风险信号明细" :rows="tableRows(d, ['riskSignals','riskDetails','records','rows'])" :preferred-columns="['risk_code','risk_name','risk_score','risk_level','signal_level','risk_action','one_vote_veto']" />
    </div>
  </div>
</template>
