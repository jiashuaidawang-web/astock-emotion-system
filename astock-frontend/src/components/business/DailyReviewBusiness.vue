<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import BusinessBlock from './BusinessBlock.vue'
import { tableRows, textValue, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
const rows = tableRows(d, ['reviewItems','items','records','rows'])

const kpis = [
  { label: '复盘日期', value: textValue(d, ['tradeDate','trade_date']) },
  { label: '复盘项', value: rows.length },
  { label: '主线状态', value: textValue(d, ['summary.mainlineStatus','mainlineStatus']) },
  { label: '风险状态', value: textValue(d, ['summary.riskStatus','riskStatus']) }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />
    <BusinessBlock title="复盘摘要" subtitle="复盘内容来自后端工作台数据。">
      <pre class="max-h-64 overflow-auto rounded bg-terminal-base p-3 text-xs">{{ JSON.stringify(d.summary || {}, null, 2) }}</pre>
    </BusinessBlock>
    <SmartTable title="复盘任务清单" :rows="rows" :preferred-columns="['item_name','itemName','item_status','status','owner','summary','created_at']" />
  </div>
</template>
