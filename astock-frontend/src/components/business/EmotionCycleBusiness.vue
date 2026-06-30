<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import EmotionStateMachine from '@/components/visual/EmotionStateMachine.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import BusinessBlock from './BusinessBlock.vue'
import StatusBadge from './StatusBadge.vue'
import { findArray, numberValue, tableRows, textValue, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
const stageRows = findArray(d, ['stageScores', 'stage_scores', 'records', 'rows'])
const transitions = tableRows(d, ['transitions', 'stageTransitions', 'transitionRows'])
const currentStage = textValue(d, ['primaryStage', 'primary_stage', 'stageCode'])

const kpis = [
  { label: '当前阶段', value: currentStage },
  { label: '阶段置信度', value: numberValue(d, ['stageConfidence', 'stage_confidence', 'stageScore']).toFixed(2) },
  { label: '候选阶段数', value: stageRows.length },
  { label: '转移路径数', value: transitions.length }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />
    <BusinessBlock title="当前情绪阶段" subtitle="情绪阶段由后端状态机与历史增强算法输出。">
      <div class="flex items-center gap-3">
        <StatusBadge :value="currentStage" />
        <span class="text-xs text-terminal-sub">页面仅展示状态，不做前端二次判定。</span>
      </div>
    </BusinessBlock>
    <EmotionStateMachine :current-stage="currentStage" :rows="stageRows" />
    <div class="grid gap-3 xl:grid-cols-2">
      <SmartTable title="阶段评分明细" :rows="tableRows(d, ['stageScores','stage_scores','records','rows'])" :preferred-columns="['stage_code','stage_name','stage_score','rank_no','historical_sample_similarity_score']" />
      <SmartTable title="阶段转移路径" :rows="transitions" :preferred-columns="['from_stage','to_stage','transition_probability','transition_score']" />
    </div>
  </div>
</template>
