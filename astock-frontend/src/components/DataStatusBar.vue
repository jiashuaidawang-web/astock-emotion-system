<script setup lang="ts">
defineProps<{
  loading: boolean
  error?: string
  dataComplete?: boolean
}>()
</script>

<template>
  <div class="flex items-center gap-2 text-xs">
    <el-tag v-if="loading" type="info" effect="dark">加载中</el-tag>
    <el-tag v-else-if="error" type="danger" effect="dark">接口异常</el-tag>
    <el-tag v-else-if="dataComplete" type="success" effect="dark">数据完整</el-tag>
    <el-tag v-else type="warning" effect="dark">数据不完整</el-tag>
    <span v-if="error" class="text-terminal-down">{{ error }}</span>
  </div>
</template>
