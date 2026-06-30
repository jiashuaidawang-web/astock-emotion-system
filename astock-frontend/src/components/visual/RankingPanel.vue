<script setup lang="ts">
import { numberValue, textValue, percent, type AnyRecord } from '@/utils/dataExtract'

defineProps<{
  title: string
  rows: AnyRecord[]
  nameKeys: string[]
  scoreKeys: string[]
}>()
</script>

<template>
  <section class="quant-panel p-3">
    <div class="mb-2 flex items-center justify-between">
      <h3 class="text-sm font-semibold text-terminal-main">{{ title }}</h3>
      <span class="text-xs text-terminal-sub">Top {{ rows.length }}</span>
    </div>

    <el-empty v-if="rows.length === 0" description="暂无排行数据" />

    <div v-else class="space-y-2">
      <div v-for="(row, index) in rows" :key="index" class="rounded-lg bg-terminal-panel p-2">
        <div class="mb-1 flex items-center justify-between gap-2 text-xs">
          <div class="flex min-w-0 items-center gap-2">
            <span class="inline-flex h-5 w-5 shrink-0 items-center justify-center rounded bg-terminal-card text-[11px] text-terminal-sub">
              {{ index + 1 }}
            </span>
            <span class="truncate text-terminal-main">{{ textValue(row, nameKeys) }}</span>
          </div>
          <span class="font-semibold text-terminal-main">{{ numberValue(row, scoreKeys).toFixed(2) }}</span>
        </div>
        <div class="h-1.5 overflow-hidden rounded-full bg-terminal-base">
          <div class="h-full rounded-full bg-terminal-up" :style="{ width: percent(numberValue(row, scoreKeys)) + '%' }"></div>
        </div>
      </div>
    </div>
  </section>
</template>
