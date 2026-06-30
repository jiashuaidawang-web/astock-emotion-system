<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import RankingPanel from '@/components/visual/RankingPanel.vue'
import { findArray, numberValue, tableRows, topRows, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
const samples = tableRows(d, ['samples','records','rows','list'])
const ranked = topRows(d, ['samples','records','rows','list'], ['sample_confidence','confidence','similarity_score'], 10)

const kpis = [
  { label: '样本总数', value: numberValue(d, ['total', 'totalCount'], samples.length) },
  { label: '当前页样本', value: samples.length },
  { label: '高置信样本', value: ranked.filter(row => numberValue(row, ['sample_confidence','confidence']) >= 80).length },
  { label: '阶段覆盖', value: findArray(d, ['stageGroups','stageStats']).length || '-' }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />
    <div class="grid gap-3 xl:grid-cols-2">
      <RankingPanel title="高质量历史样本" :rows="ranked" :name-keys="['trade_date','sample_id','stage_code']" :score-keys="['sample_confidence','confidence','similarity_score']" />
      <SmartTable title="历史周期样本列表" :rows="samples" :preferred-columns="['sample_id','trade_date','stage_code','sample_type','sample_confidence','pattern_code','stock_code']" />
    </div>
  </div>
</template>
