<script setup lang="ts">
import { computed } from 'vue'
import { numberValue, textValue, percent, type AnyRecord } from '@/utils/dataExtract'

const props = defineProps<{
  rows: AnyRecord[]
}>()

const chartRows = computed(() => props.rows.slice(0, 8))
</script>

<template>
  <section class="quant-panel p-3">
    <div class="mb-2">
      <h3 class="text-sm font-semibold text-terminal-main">回测统计图</h3>
      <p class="text-xs text-terminal-sub">展示分层胜率、平均收益、最大回撤等后验统计。</p>
    </div>

    <el-empty v-if="chartRows.length === 0" description="暂无回测统计" />

    <div v-else class="space-y-3">
      <div v-for="row in chartRows" :key="textValue(row, ['layer_code', 'metric_name'])">
        <div class="mb-1 flex items-center justify-between text-xs">
          <span class="truncate text-terminal-main">{{ textValue(row, ['layer_name', 'layer_code', 'metric_name']) }}</span>
          <span class="text-terminal-sub">
            胜率 {{ numberValue(row, ['win_rate']).toFixed(2) }}% · 收益 {{ numberValue(row, ['avg_return', 'metric_value']).toFixed(2) }}
          </span>
        </div>
        <div class="grid grid-cols-3 gap-1">
          <div class="h-2 overflow-hidden rounded bg-terminal-base">
            <div class="h-full bg-terminal-up" :style="{ width: percent(numberValue(row, ['win_rate'])) + '%' }"></div>
          </div>
          <div class="h-2 overflow-hidden rounded bg-terminal-base">
            <div class="h-full bg-terminal-neutral" :style="{ width: percent(Math.abs(numberValue(row, ['avg_return', 'metric_value'])), 20) + '%' }"></div>
          </div>
          <div class="h-2 overflow-hidden rounded bg-terminal-base">
            <div class="h-full bg-terminal-down" :style="{ width: percent(Math.abs(numberValue(row, ['avg_drawdown'])), 20) + '%' }"></div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
