<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import RankingPanel from '@/components/visual/RankingPanel.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import { findArray, numberValue, tableRows, topRows, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
const sectors = topRows(d, ['sectors','records','rows'], ['sector_strength_score','strength_score'], 15)
const rows = tableRows(d, ['sectors','records','rows'])
const top = findArray(d, ['sectors','records','rows'])[0] as AnyRecord | undefined

const kpis = [
  { label: '板块数量', value: rows.length },
  { label: '最强板块强度', value: numberValue(top, ['sector_strength_score','strength_score']).toFixed(2) },
  { label: '涨停贡献', value: numberValue(top, ['limit_up_count']).toFixed(0) },
  { label: '持续天数', value: numberValue(top, ['continuity_days']).toFixed(0) }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />
    <div class="grid gap-3 xl:grid-cols-2">
      <RankingPanel title="板块强度排行" :rows="sectors" :name-keys="['sector_name','theme_name','sector_code']" :score-keys="['sector_strength_score','strength_score']" />
      <SmartTable title="板块强度明细" :rows="rows" :preferred-columns="['rank_no','sector_name','sector_strength_score','pct_change','limit_up_count','turnover_amount','continuity_days']" />
    </div>
  </div>
</template>
