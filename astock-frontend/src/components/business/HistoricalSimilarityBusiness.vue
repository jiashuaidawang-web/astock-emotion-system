<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import RankingPanel from '@/components/visual/RankingPanel.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import BusinessBlock from './BusinessBlock.vue'
import ScoreBar from './ScoreBar.vue'
import { findArray, numberValue, tableRows, topRows, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
const matches = topRows(d, ['matches', 'similarityMatches', 'records', 'rows'], ['total_similarity_score', 'totalSimilarityScore'], 10)
const factors = tableRows(d, ['factorDetails', 'factor_details', 'dimensionDetails', 'details'])
const top = findArray(d, ['matches', 'records', 'rows'])[0] as AnyRecord | undefined

const kpis = [
  { label: '相似样本数', value: findArray(d, ['matches', 'records', 'rows']).length },
  { label: '最高相似度', value: numberValue(top, ['total_similarity_score', 'totalSimilarityScore']).toFixed(2) },
  { label: '市场环境相似', value: numberValue(top, ['market_environment_similarity_score']).toFixed(2) },
  { label: '情绪周期相似', value: numberValue(top, ['emotion_cycle_similarity_score']).toFixed(2) },
  { label: '主线龙头相似', value: numberValue(top, ['theme_leader_similarity_score']).toFixed(2) }
]
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />

    <BusinessBlock title="Top1 相似样本九维拆解" subtitle="只展示历史对照，不读取 future_* 参与T日匹配。">
      <div class="grid gap-3 md:grid-cols-3">
        <ScoreBar label="市场环境相似" :value="numberValue(top, ['market_environment_similarity_score'])" />
        <ScoreBar label="情绪周期相似" :value="numberValue(top, ['emotion_cycle_similarity_score'])" />
        <ScoreBar label="主线龙头相似" :value="numberValue(top, ['theme_leader_similarity_score'])" />
      </div>
    </BusinessBlock>

    <div class="grid gap-3 xl:grid-cols-2">
      <RankingPanel title="历史相似样本排行" :rows="matches" :name-keys="['historical_trade_date','sample_id','historical_stage']" :score-keys="['total_similarity_score','totalSimilarityScore']" />
      <SmartTable title="九维因子明细" :rows="factors" :preferred-columns="['dimension_code','dimension_name','current_value','historical_value','dimension_similarity_score']" />
    </div>
  </div>
</template>
