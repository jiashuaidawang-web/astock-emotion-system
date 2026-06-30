<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import RankingPanel from '@/components/visual/RankingPanel.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import BusinessBlock from './BusinessBlock.vue'
import StatusBadge from './StatusBadge.vue'
import { findArray, numberValue, tableRows, topRows, textValue, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
const gates = tableRows(d, ['gates','releaseGates','records','rows'])
const issues = tableRows(d, ['issues','codeScanIssues','details'])
const hits = topRows(d, ['ruleHits','hits','records','rows'], ['blocker_count','hit_count'], 10)
const firstGate = findArray(d, ['gates','releaseGates','records','rows'])[0] as AnyRecord | undefined

const kpis = [
  { label: '发布闸门', value: gates.length },
  { label: '阻断项', value: gates.reduce((sum, row) => sum + numberValue(row, ['blocker_count','blockerCount']), 0) },
  { label: '代码问题', value: issues.length },
  { label: '总状态', value: textValue(firstGate, ['gate_status','gateStatus'], '-') }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />
    <BusinessBlock title="发布闸门总览" subtitle="Agent审计用于阻断Mock、交易建议词、future越界和字段血缘问题。">
      <div class="flex items-center gap-3">
        <StatusBadge :value="textValue(firstGate, ['gate_status','gateStatus'])" />
        <span class="text-xs text-terminal-sub">BLOCKED 表示存在必须修复的发布阻断项。</span>
      </div>
    </BusinessBlock>
    <div class="grid gap-3 xl:grid-cols-2">
      <RankingPanel title="审计规则命中排行" :rows="hits" :name-keys="['rule_name','rule_code']" :score-keys="['blocker_count','hit_count']" />
      <SmartTable title="发布闸门明细" :rows="gates" :preferred-columns="['gate_code','gate_name','gate_status','passed','issue_count','blocker_count']" />
    </div>
    <SmartTable title="代码审计问题" :rows="issues" :preferred-columns="['issue_code','issue_name','issue_level','module_name','file_path','line_no','release_blocker']" />
  </div>
</template>
