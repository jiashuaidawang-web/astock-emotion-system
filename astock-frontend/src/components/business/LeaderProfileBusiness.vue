<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import BusinessBlock from './BusinessBlock.vue'
import ScoreBar from './ScoreBar.vue'
import { numberValue, tableRows, textValue, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})

const kpis = [
  { label: '股票', value: textValue(d, ['stockName','stock_name','profile.stockName']) },
  { label: '代码', value: textValue(d, ['stockCode','stock_code','profile.stockCode']) },
  { label: '龙头分', value: numberValue(d, ['leaderScore','leader_score','profile.leader_score']).toFixed(2) },
  { label: '主线关联', value: numberValue(d, ['mainlineRelationScore','mainline_relation_score','profile.mainline_relation_score']).toFixed(2) }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />
    <BusinessBlock title="个股画像评分结构" subtitle="画像由后端龙头识别结果驱动。">
      <div class="grid gap-3 md:grid-cols-4">
        <ScoreBar label="辨识度" :value="numberValue(d, ['recognitionScore','recognition_score'])" />
        <ScoreBar label="带动性" :value="numberValue(d, ['driveScore','drive_score'])" />
        <ScoreBar label="承接" :value="numberValue(d, ['supportScore','support_score'])" />
        <ScoreBar label="负反馈" :value="numberValue(d, ['negativeFeedbackScore','negative_feedback_score'])" />
      </div>
    </BusinessBlock>
    <SmartTable title="画像事件与评分明细" :rows="tableRows(d, ['events','records','rows','profile.details'])" :preferred-columns="['trade_date','event_type','score','status','evidence_json']" />
  </div>
</template>
