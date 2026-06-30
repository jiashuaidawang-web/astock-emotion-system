<script setup lang="ts">
import { getTrendClass } from '@/types/common'

export interface KpiItem {
  label: string
  value: unknown
  suffix?: string
  trend?: boolean
  subText?: string
}

defineProps<{
  items: KpiItem[]
}>()
</script>

<template>
  <div class="grid grid-cols-2 gap-1.5 md:grid-cols-4 xl:grid-cols-6">
    <div v-for="item in items" :key="item.label" class="quant-panel p-3">
      <div class="text-[11px] text-terminal-sub">{{ item.label }}</div>
      <div class="mt-1 truncate text-xl font-semibold" :class="item.trend ? getTrendClass(item.value) : 'text-terminal-main'">
        {{ item.value ?? '-' }}<span v-if="item.suffix" class="ml-1 text-xs text-terminal-sub">{{ item.suffix }}</span>
      </div>
      <div v-if="item.subText" class="mt-1 truncate text-[11px] text-terminal-sub">{{ item.subText }}</div>
    </div>
  </div>
</template>
