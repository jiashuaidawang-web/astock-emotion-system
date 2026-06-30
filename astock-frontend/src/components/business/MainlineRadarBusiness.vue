<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import RankingPanel from '@/components/visual/RankingPanel.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import BusinessBlock from './BusinessBlock.vue'
import ScoreBar from './ScoreBar.vue'
import { findArray, numberValue, tableRows, textValue, topRows, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
const mainlines = topRows(d, ['mainlines','records','rows'], ['mainline_strength_score','theme_strength_score','strength_score'], 10)
const switches = tableRows(d, ['switches','mainlineSwitches','switchRows'])
const top = findArray(d, ['mainlines','records','rows'])[0] as AnyRecord | undefined

const kpis = [
  { label: '主线数量', value: findArray(d, ['mainlines','records','rows']).length },
  { label: '最强主线', value: textValue(top, ['mainline_name','theme_name','mainline_code']) },
  { label: '主线强度', value: numberValue(top, ['mainline_strength_score','theme_strength_score','strength_score']).toFixed(2) },
  { label: '切换信号', value: switches.length }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />
    <BusinessBlock title="最强主线六维结构" subtitle="禁止涨幅第一或涨停最多等同主线。">
      <div class="grid gap-3 md:grid-cols-3">
        <ScoreBar label="涨停聚集" :value="numberValue(top, ['limit_up_cluster_score'])" />
        <ScoreBar label="成交集中" :value="numberValue(top, ['turnover_concentration_score'])" />
        <ScoreBar label="持续性" :value="numberValue(top, ['continuity_score'])" />
        <ScoreBar label="梯队完整" :value="numberValue(top, ['ladder_integrity_score'])" />
        <ScoreBar label="龙头带动" :value="numberValue(top, ['leader_drive_score'])" />
        <ScoreBar label="情绪匹配" :value="numberValue(top, ['emotion_match_score'])" />
      </div>
    </BusinessBlock>
    <div class="grid gap-3 xl:grid-cols-2">
      <RankingPanel title="主线强度排行" :rows="mainlines" :name-keys="['mainline_name','theme_name','mainline_code']" :score-keys="['mainline_strength_score','theme_strength_score','strength_score']" />
      <SmartTable title="主线切换记录" :rows="switches" :preferred-columns="['old_mainline_name','new_mainline_name','switch_status','switch_score']" />
    </div>
  </div>
</template>
