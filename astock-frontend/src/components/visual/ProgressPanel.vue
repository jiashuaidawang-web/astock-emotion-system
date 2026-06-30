<script setup lang="ts">
import { statusType } from '@/utils/dataExtract'
import type { EngineBatchStepResult } from '@/types/engine'

defineProps<{
  steps: EngineBatchStepResult[]
}>()
</script>

<template>
  <section class="quant-panel p-3">
    <div class="mb-2">
      <h3 class="text-sm font-semibold text-terminal-main">Engine执行进度</h3>
      <p class="text-xs text-terminal-sub">由后端一键跑批结果驱动。</p>
    </div>

    <div class="space-y-2">
      <div v-for="step in steps" :key="step.stepCode" class="flex gap-2 rounded-lg bg-terminal-panel p-2">
        <div class="flex h-7 w-7 shrink-0 items-center justify-center rounded bg-terminal-card text-xs">
          {{ step.stepNo }}
        </div>
        <div class="min-w-0 flex-1">
          <div class="flex items-center justify-between gap-2">
            <div class="truncate text-xs font-semibold text-terminal-main">{{ step.stepName }}</div>
            <el-tag :type="statusType(step.success ? 'SUCCESS' : 'FAILED')" effect="dark" size="small">
              {{ step.success ? 'SUCCESS' : 'FAILED' }}
            </el-tag>
          </div>
          <div class="mt-1 text-[11px] text-terminal-sub">
            {{ step.engineName }} · 输出 {{ step.outputRowCount ?? 0 }} 行 · {{ step.costMillis ?? 0 }} ms
          </div>
          <div v-if="step.failureReason" class="mt-1 text-[11px] text-terminal-down">
            {{ step.failureReason }}
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
