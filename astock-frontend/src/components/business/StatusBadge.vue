<script setup lang="ts">
import { computed } from 'vue'
import { statusType } from '@/utils/dataExtract'

const props = defineProps<{
  value: unknown
}>()

const label = computed(() => props.value === undefined || props.value === null || props.value === '' ? '-' : String(props.value))
</script>

<template>
  <el-tag :type="statusType(value)" effect="dark" size="small">
    {{ label }}
  </el-tag>
</template>
