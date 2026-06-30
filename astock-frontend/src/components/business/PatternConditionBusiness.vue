<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import RankingPanel from '@/components/visual/RankingPanel.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import BusinessBlock from './BusinessBlock.vue'
import { findArray, tableRows, topRows, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
const signals = findArray(d, ['signals','records','rows'])
const vetoes = tableRows(d, ['riskVetoes','vetoes','patternRiskVetoes'])
const ranked = topRows(d, ['signals','records','rows'], ['pattern_condition_score','condition_score'], 10)

const kpis = [
  { label: '条件信号', value: signals.length },
  { label: '风险否决', value: vetoes.length },
  { label: '满足条件', value: signals.filter(row => String(row.condition_status || row.conditionStatus) === 'CONDITION_MET').length },
  { label: '观察中', value: signals.filter(row => String(row.condition_status || row.conditionStatus) === 'OBSERVING').length }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />
    <BusinessBlock title="合规提示" subtitle="本页只展示条件状态，不展示买入、卖出、持有、推荐、目标价。">
      <el-alert type="info" :closable="false" title="条件状态是研究信号，不是交易建议。" />
    </BusinessBlock>
    <div class="grid gap-3 xl:grid-cols-2">
      <RankingPanel title="条件分排行" :rows="ranked" :name-keys="['stock_name','pattern_name','pattern_code']" :score-keys="['pattern_condition_score','condition_score']" />
      <SmartTable title="风险否决覆盖" :rows="vetoes" :preferred-columns="['pattern_code','stock_name','condition_status','risk_veto','risk_action','risk_veto_reason']" />
    </div>
    <SmartTable title="条件信号明细" :rows="tableRows(d, ['signals','records','rows'])" :preferred-columns="['pattern_code','stock_name','condition_status','pattern_condition_score','risk_veto','invalidated','signal_text']" />
  </div>
</template>
