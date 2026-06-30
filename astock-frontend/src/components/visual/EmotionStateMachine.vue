<script setup lang="ts">
import { computed } from 'vue'
import { textValue, numberValue, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{
  currentStage?: string
  rows: AnyRecord[]
}>()

const stages = ['ICE_POINT', 'REPAIR', 'TRIAL', 'STARTUP', 'FERMENTATION', 'MAIN_RISE', 'CLIMAX', 'DIVERGENCE', 'RETREAT', 'CHAOS']

const scored = computed(() => {
  return stages.map(stage => {
    const hit = props.rows.find(row => textValue(row, ['stage_code', 'primary_stage', 'emotion_stage']) === stage)
    return {
      stage,
      score: hit ? numberValue(hit, ['stage_score', 'score', 'stageScore']) : 0
    }
  })
})
</script>

<template>
  <section class="quant-panel p-3">
    <div class="mb-2">
      <h3 class="text-sm font-semibold text-terminal-main">情绪周期状态机</h3>
      <p class="text-xs text-terminal-sub">按十阶段路径展示当前阶段与候选评分。</p>
    </div>

    <div class="grid grid-cols-2 gap-1.5 md:grid-cols-5 xl:grid-cols-10">
      <div
        v-for="item in scored"
        :key="item.stage"
        class="rounded-lg border p-2 text-center"
        :class="item.stage === currentStage ? 'border-terminal-up bg-terminal-card' : 'border-terminal-line bg-terminal-panel'"
      >
        <div class="text-[11px]" :class="item.stage === currentStage ? 'text-terminal-up' : 'text-terminal-sub'">
          {{ item.stage }}
        </div>
        <div class="mt-1 text-sm font-semibold text-terminal-main">{{ item.score.toFixed(1) }}</div>
      </div>
    </div>
  </section>
</template>
