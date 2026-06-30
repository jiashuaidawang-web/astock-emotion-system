<script setup lang="ts">
import { computed } from 'vue'
import { percent } from '@/utils/dataExtract'

const props = defineProps<{
  label: string
  value: number
  max?: number
}>()

const width = computed(() => percent(props.value, props.max || 100))
</script>

<template>
  <div>
    <div class="mb-1 flex items-center justify-between text-xs">
      <span class="text-terminal-sub">{{ label }}</span>
      <span class="font-semibold text-terminal-main">{{ Number(value || 0).toFixed(2) }}</span>
    </div>
    <div class="h-1.5 overflow-hidden rounded bg-terminal-base">
      <div class="h-full rounded bg-terminal-up" :style="{ width: width + '%' }"></div>
    </div>
  </div>
</template>
