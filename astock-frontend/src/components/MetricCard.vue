<script setup lang="ts">
import { getTrendClass } from '@/types/common'

defineProps<{
  title: string
  value: unknown
  suffix?: string
  trend?: boolean
}>()
</script>

<template>
  <div class="quant-panel p-3">
    <div class="text-xs text-terminal-sub">{{ title }}</div>
    <div class="mt-1 text-xl font-semibold" :class="trend ? getTrendClass(value) : 'text-terminal-main'">
      {{ value ?? '-' }}<span v-if="suffix" class="ml-1 text-xs text-terminal-sub">{{ suffix }}</span>
    </div>
  </div>
</template>
