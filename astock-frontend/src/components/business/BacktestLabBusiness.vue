<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import BacktestStatsChart from '@/components/visual/BacktestStatsChart.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import { findArray, numberValue, tableRows, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
const stats = tableRows(d, ['layerStats','reports','records','rows'])
const first = findArray(d, ['layerStats','reports','records','rows'])[0] as AnyRecord | undefined

const kpis = [
  { label: '样本数', value: numberValue(first || d, ['sample_count','sampleCount']) },
  { label: '胜率', value: numberValue(first || d, ['win_rate','winRate']).toFixed(2), suffix: '%' },
  { label: '平均收益', value: numberValue(first || d, ['avg_return','avgReturn']).toFixed(2) },
  { label: '风控过滤', value: numberValue(first || d, ['risk_veto_count','riskVetoCount']) }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />
    <BacktestStatsChart :rows="stats" />
    <SmartTable title="回测分层统计" :rows="stats" :preferred-columns="['layer_code','layer_name','sample_count','effective_signal_count','risk_veto_count','win_rate','avg_return','avg_drawdown','profit_loss_ratio']" />
  </div>
</template>
