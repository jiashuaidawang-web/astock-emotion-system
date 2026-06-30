<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import RankingPanel from '@/components/visual/RankingPanel.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import BusinessBlock from './BusinessBlock.vue'
import ScoreBar from './ScoreBar.vue'
import { findArray, numberValue, tableRows, topRows, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
const leaders = topRows(d, ['leaders','records','rows'], ['leader_score'], 10)
const ladders = tableRows(d, ['ladders','leaderLadders','ladderRows'])
const top = findArray(d, ['leaders','records','rows'])[0] as AnyRecord | undefined

const kpis = [
  { label: '龙头候选', value: findArray(d, ['leaders','records','rows']).length },
  { label: '梯队层数', value: ladders.length },
  { label: '最高综合分', value: numberValue(top, ['leader_score']).toFixed(2) },
  { label: '板高', value: numberValue(top, ['board_height']).toFixed(0) }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />
    <BusinessBlock title="市场龙头七维结构" subtitle="禁止最高板等同市场总龙头。">
      <div class="grid gap-3 md:grid-cols-4">
        <ScoreBar label="辨识度" :value="numberValue(top, ['recognition_score'])" />
        <ScoreBar label="主线关联" :value="numberValue(top, ['mainline_relation_score'])" />
        <ScoreBar label="带动性" :value="numberValue(top, ['drive_score','leader_drive_score'])" />
        <ScoreBar label="强度" :value="numberValue(top, ['strength_score'])" />
        <ScoreBar label="承接" :value="numberValue(top, ['support_score'])" />
        <ScoreBar label="持续性" :value="numberValue(top, ['continuity_score'])" />
        <ScoreBar label="风险反馈" :value="numberValue(top, ['risk_feedback_score'])" />
      </div>
    </BusinessBlock>
    <div class="grid gap-3 xl:grid-cols-2">
      <RankingPanel title="龙头综合分排行" :rows="leaders" :name-keys="['stock_name','stock_code']" :score-keys="['leader_score']" />
      <SmartTable title="梯队结构" :rows="ladders" :preferred-columns="['board_height','stock_count','top_stock_name','top_leader_score','leader_type']" />
    </div>
  </div>
</template>
