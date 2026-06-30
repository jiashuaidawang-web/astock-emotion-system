<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import { tableRows, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
const rows = tableRows(d, ['versions','records','rows'])

const kpis = [
  { label: '规则版本数', value: rows.length },
  { label: '启用版本', value: rows.filter(row => row.active_flag === true || row.activeFlag === true || row.version_status === 'ACTIVE').length },
  { label: '草稿版本', value: rows.filter(row => row.version_status === 'DRAFT').length },
  { label: '归档版本', value: rows.filter(row => row.version_status === 'ARCHIVED').length }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />
    <SmartTable title="规则版本列表" :rows="rows" :preferred-columns="['rule_code','rule_name','version_no','version_status','active_flag','updated_at','remark']" />
  </div>
</template>
