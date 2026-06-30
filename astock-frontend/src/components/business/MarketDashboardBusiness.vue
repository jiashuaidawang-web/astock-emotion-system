<script setup lang="ts">
import KpiGrid from '@/components/visual/KpiGrid.vue'
import SmartTable from '@/components/visual/SmartTable.vue'
import BusinessBlock from './BusinessBlock.vue'
import ScoreBar from './ScoreBar.vue'
import { numberValue, tableRows, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{ data: unknown }>()

const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})

const kpis = [
  { label: '市场宽度', value: numberValue(d, ['marketBreadthScore', 'market_breadth_score', 'breadthScore']).toFixed(2) },
  { label: '赚钱效应', value: numberValue(d, ['profitEffectScore', 'profit_effect_score', 'earningEffectScore']).toFixed(2) },
  { label: '亏钱效应', value: numberValue(d, ['lossEffectScore', 'loss_effect_score', 'lossPressureScore']).toFixed(2) },
  { label: '上涨家数', value: numberValue(d, ['riseCount', 'rise_count', 'up_count']) },
  { label: '下跌家数', value: numberValue(d, ['fallCount', 'fall_count', 'down_count']) },
  { label: '涨停家数', value: numberValue(d, ['limitUpCount', 'limit_up_count', 'zt_count']) }
]

const factorRows = tableRows(d, ['factorRows', 'marketFactors', 'records', 'rows'])
</script>

<template>
  <div class="space-y-3">
    <KpiGrid :items="kpis" />

    <BusinessBlock title="市场温度结构" subtitle="用市场宽度、赚钱效应、亏钱效应拆解今日市场环境。">
      <div class="grid gap-3 md:grid-cols-3">
        <ScoreBar label="市场宽度" :value="numberValue(d, ['marketBreadthScore', 'market_breadth_score'])" />
        <ScoreBar label="赚钱效应" :value="numberValue(d, ['profitEffectScore', 'profit_effect_score'])" />
        <ScoreBar label="亏钱效应" :value="numberValue(d, ['lossEffectScore', 'loss_effect_score'])" />
      </div>
    </BusinessBlock>

    <SmartTable
      title="市场核心因子明细"
      :rows="factorRows"
      :preferred-columns="['factor_name','factorName','factor_value','factorValue','score','risk_level','riskLevel']"
    />
  </div>
</template>
