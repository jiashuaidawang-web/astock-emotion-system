<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import BacktestStatsChart from '@/components/visual/BacktestStatsChart.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import { findArray, numberValue, tableRows, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
const stats = tableRows(d, ['layerStats','performanceDetails','records','rows'])
const failures = tableRows(d, ['failureCases','failures','failureRows'])
const signals = tableRows(d, ['signalDetails','signals','records','rows'])
const first = findArray(d, ['layerStats','performanceDetails','records','rows'])[0] as AnyRecord | undefined

const kpis = [
  { label: '信号样本', value: signals.length },
  { label: '失败样本', value: failures.length },
  { label: '胜率', value: numberValue(first || d, ['win_rate','winRate']).toFixed(2), suffix: '%' },
  { label: '平均回撤', value: numberValue(first || d, ['avg_drawdown','avgDrawdown']).toFixed(2) }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />
    <BacktestStatsChart :rows="stats.length ? stats : signals" />
    <div class="grid gap-3 xl:grid-cols-2">
      <SmartTable title="回测信号明细" :rows="signals" :preferred-columns="['sample_id','sample_date','pattern_code','stock_name','signal_score','risk_action','replay_status','replay_return','max_drawdown']" />
      <SmartTable title="失败样本归因" :rows="failures" :preferred-columns="['sample_id','pattern_code','stock_name','failure_type','failure_reason','replay_return','replay_drawdown']" />
    </div>
  </div>
</template>
