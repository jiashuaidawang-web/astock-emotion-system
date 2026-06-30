<script setup lang="ts">
import { computed } from 'vue'
import PageShell from '@/components/PageShell.vue'
import KpiGrid from './KpiGrid.vue'
import RankingPanel from './RankingPanel.vue'
import SmartTable from './SmartTable.vue'
import EmotionStateMachine from './EmotionStateMachine.vue'
import BacktestStatsChart from './BacktestStatsChart.vue'
import {
  findArray,
  numberValue,
  tableRows,
  textValue,
  topRows,
  type AnyRecord
} from '@/utils/dataExtract'
import type { PageQuery } from '@/types/common'

const props = defineProps<{
  title: string
  subtitle: string
  pageKind: string
  loading: boolean
  error?: string
  dataComplete?: boolean
  data: unknown
  reload: (patch?: PageQuery) => void
}>()

const record = computed(() => (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {}))

const kpis = computed(() => {
  const d = record.value
  const common = [
    { label: '数据完整', value: props.dataComplete ? '完整' : '不完整' },
    { label: '交易日', value: textValue(d, ['tradeDate', 'trade_date']) },
    { label: '市场', value: textValue(d, ['marketScope', 'market_scope']) }
  ]

  if (props.pageKind === 'market') {
    return [
      ...common,
      { label: '市场宽度', value: numberValue(d, ['marketBreadthScore', 'market_breadth_score', 'breadthScore']).toFixed(2) },
      { label: '赚钱效应', value: numberValue(d, ['profitEffectScore', 'profit_effect_score']).toFixed(2) },
      { label: '亏钱效应', value: numberValue(d, ['lossEffectScore', 'loss_effect_score']).toFixed(2) }
    ]
  }

  if (props.pageKind === 'emotion') {
    return [
      ...common,
      { label: '当前阶段', value: textValue(d, ['primaryStage', 'primary_stage', 'stageCode']) },
      { label: '阶段置信度', value: numberValue(d, ['stageConfidence', 'stage_confidence', 'stageScore']).toFixed(2) }
    ]
  }

  if (props.pageKind === 'risk') {
    return [
      ...common,
      { label: '综合风险', value: numberValue(d, ['riskScore', 'risk_score']).toFixed(2) },
      { label: '风险等级', value: textValue(d, ['riskLevel', 'risk_level']) },
      { label: '风险动作', value: textValue(d, ['riskAction', 'risk_action']) }
    ]
  }

  return [
    ...common,
    { label: '主列表数量', value: mainRows.value.length },
    { label: '排行数量', value: rankRows.value.length }
  ]
})

const mainRows = computed(() => {
  const keysByPage: Record<string, string[]> = {
    market: ['marketItems', 'items', 'records', 'rows'],
    similarity: ['matches', 'similarList', 'records', 'rows'],
    emotion: ['stageScores', 'stage_scores', 'records', 'rows'],
    samples: ['samples', 'records', 'list', 'rows'],
    mainline: ['mainlines', 'mainlineList', 'records', 'rows'],
    sector: ['sectors', 'sectorList', 'records', 'rows'],
    leader: ['leaders', 'leaderList', 'records', 'rows'],
    profile: ['events', 'records', 'rows'],
    pattern: ['signals', 'signalList', 'records', 'rows'],
    risk: ['riskSignals', 'riskDetails', 'details', 'records', 'rows'],
    backtest: ['layerStats', 'reports', 'records', 'rows'],
    report: ['signalDetails', 'performanceDetails', 'failureCases', 'records', 'rows'],
    review: ['reviewItems', 'items', 'records', 'rows'],
    rules: ['versions', 'records', 'rows'],
    audit: ['gates', 'issues', 'ruleHits', 'records', 'rows']
  }
  return tableRows(record.value, keysByPage[props.pageKind] || ['records', 'rows'])
})

const rankRows = computed(() => {
  const keysByPage: Record<string, string[]> = {
    similarity: ['matches', 'records', 'rows'],
    mainline: ['mainlines', 'records', 'rows'],
    sector: ['sectors', 'records', 'rows'],
    leader: ['leaders', 'records', 'rows'],
    pattern: ['signals', 'records', 'rows'],
    risk: ['riskSignals', 'riskDetails', 'records', 'rows'],
    backtest: ['layerStats', 'records', 'rows'],
    audit: ['gates', 'issues', 'ruleHits', 'records', 'rows']
  }
  const scoreKeys = ['total_similarity_score', 'mainline_strength_score', 'sector_strength_score', 'leader_score', 'pattern_condition_score', 'risk_score', 'win_rate', 'blocker_count', 'score']
  return topRows(record.value, keysByPage[props.pageKind] || ['records', 'rows'], scoreKeys, 10)
})

const rankNameKeys = computed(() => {
  if (props.pageKind === 'leader') return ['stock_name', 'stockName', 'stock_code']
  if (props.pageKind === 'sector') return ['sector_name', 'sectorName', 'sector_code']
  if (props.pageKind === 'mainline') return ['mainline_name', 'mainlineName', 'theme_name']
  if (props.pageKind === 'pattern') return ['stock_name', 'pattern_name', 'pattern_code']
  if (props.pageKind === 'risk') return ['risk_name', 'risk_code']
  if (props.pageKind === 'audit') return ['gate_name', 'issue_name', 'rule_name']
  return ['name', 'title', 'stage_name', 'sample_id', 'stock_name']
})

const rankScoreKeys = computed(() => {
  if (props.pageKind === 'similarity') return ['total_similarity_score', 'similarityScore']
  if (props.pageKind === 'mainline') return ['mainline_strength_score', 'theme_strength_score', 'strength_score']
  if (props.pageKind === 'sector') return ['sector_strength_score', 'strength_score']
  if (props.pageKind === 'leader') return ['leader_score', 'score']
  if (props.pageKind === 'pattern') return ['pattern_condition_score', 'condition_score']
  if (props.pageKind === 'risk') return ['risk_score']
  if (props.pageKind === 'backtest') return ['win_rate', 'avg_return', 'metric_value']
  if (props.pageKind === 'audit') return ['blocker_count', 'hit_count', 'issue_count']
  return ['score', 'rank_no']
})

const stageRows = computed(() => findArray(record.value, ['stageScores', 'stage_scores', 'records', 'rows']))
const currentStage = computed(() => textValue(record.value, ['primaryStage', 'primary_stage', 'stageCode', 'stage_code']))
const backtestRows = computed(() => findArray(record.value, ['layerStats', 'performanceDetails', 'records', 'rows']))
</script>

<template>
  <PageShell
    :title="title"
    :subtitle="subtitle"
    :loading="loading"
    :error="error"
    :data-complete="dataComplete"
    @refresh="reload()"
  >
    <div class="space-y-3">
      <KpiGrid :items="kpis" />

      <EmotionStateMachine
        v-if="pageKind === 'emotion'"
        :current-stage="currentStage"
        :rows="stageRows"
      />

      <BacktestStatsChart
        v-if="pageKind === 'backtest' || pageKind === 'report'"
        :rows="backtestRows.length ? backtestRows : mainRows"
      />

      <div class="grid gap-3 xl:grid-cols-2">
        <RankingPanel
          v-if="rankRows.length > 0"
          :title="pageKind === 'risk' ? '风险排行' : pageKind === 'audit' ? '审计闸门排行' : '核心排行'"
          :rows="rankRows"
          :name-keys="rankNameKeys"
          :score-keys="rankScoreKeys"
        />

        <SmartTable
          title="明细表格"
          :rows="mainRows"
          :preferred-columns="[
            'tradeDate','trade_date','rank_no','stock_name','stockName','sector_name','mainline_name',
            'stage_code','pattern_code','risk_code','condition_status','leader_score','risk_score',
            'mainline_strength_score','total_similarity_score','win_rate','gate_status'
          ]"
        />
      </div>
    </div>
  </PageShell>
</template>
