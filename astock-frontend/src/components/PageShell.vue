<script setup lang="ts">
import DataStatusBar from './DataStatusBar.vue'

defineProps<{
  title: string
  subtitle?: string
  loading: boolean
  error?: string
  dataComplete?: boolean
}>()

const emit = defineEmits<{
  refresh: []
}>()
</script>

<template>
  <section class="space-y-3">
    <header class="flex items-start justify-between gap-3">
      <div>
        <h1 class="text-lg font-semibold text-terminal-main">{{ title }}</h1>
        <p v-if="subtitle" class="mt-1 text-xs text-terminal-sub">{{ subtitle }}</p>
      </div>
      <div class="flex items-center gap-2">
        <DataStatusBar :loading="loading" :error="error" :data-complete="dataComplete" />
        <el-button size="small" :loading="loading" @click="emit('refresh')">刷新</el-button>
      </div>
    </header>

    <div class="quant-panel p-3">
      <slot />
    </div>
  </section>
</template>
