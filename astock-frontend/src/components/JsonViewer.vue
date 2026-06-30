<script setup lang="ts">
defineProps<{
  data: unknown
}>()
</script>

<template>
  <pre class="max-h-[720px] overflow-auto rounded-lg bg-terminal-base p-3 text-xs leading-5 text-terminal-main">{{ JSON.stringify(data, null, 2) }}</pre>
</template>
