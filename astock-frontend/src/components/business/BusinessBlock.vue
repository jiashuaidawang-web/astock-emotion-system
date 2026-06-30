<script setup lang="ts">
defineProps<{
  title: string
  subtitle?: string
}>()
</script>

<template>
  <section class="quant-panel p-3">
    <div class="mb-2">
      <h3 class="text-sm font-semibold text-terminal-main">{{ title }}</h3>
      <p v-if="subtitle" class="mt-1 text-xs text-terminal-sub">{{ subtitle }}</p>
    </div>
    <slot />
  </section>
</template>
