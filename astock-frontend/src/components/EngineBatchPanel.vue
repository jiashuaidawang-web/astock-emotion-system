<script setup lang="ts">
import { useEngineBatch } from '@/composables/useEngineBatch'
import ProgressPanel from '@/components/visual/ProgressPanel.vue'

const { form, running, error, result, run } = useEngineBatch()
</script>

<template>
  <section class="quant-panel p-3">
    <div class="mb-3 flex items-center justify-between">
      <div>
        <h2 class="text-sm font-semibold text-terminal-main">Engine 一键跑批</h2>
        <p class="text-xs text-terminal-sub">按后端依赖顺序执行：情绪 → 主线 → 龙头 → 风控 → 条件 → 二次风控 → 相似 → 审计。</p>
      </div>
      <el-button type="primary" :loading="running" @click="run">执行跑批</el-button>
    </div>

    <el-form label-position="top" class="grid grid-cols-2 gap-2 md:grid-cols-4">
      <el-form-item label="交易日">
        <el-date-picker
          v-model="form.tradeDate"
          type="date"
          value-format="YYYY-MM-DD"
          class="w-full"
        />
      </el-form-item>
      <el-form-item label="市场范围">
        <el-input v-model="form.marketScope" />
      </el-form-item>
      <el-form-item label="规则版本ID">
        <el-input-number v-model="form.ruleVersionId" :min="1" class="w-full" />
      </el-form-item>
      <el-form-item label="参数JSON">
        <el-input v-model="form.paramJson" />
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="form.dataCheckEnabled">数据完整性检查</el-checkbox>
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="form.rerunRiskAfterPattern">Pattern后二次风控</el-checkbox>
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="form.runBacktest">执行回测</el-checkbox>
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="form.runAgentAudit">执行Agent审计</el-checkbox>
      </el-form-item>
    </el-form>

    <el-alert v-if="error" type="error" :closable="false" class="mt-2" :title="error" />

    <div v-if="result" class="mt-3 grid gap-3 xl:grid-cols-[360px_1fr]">
      <div class="quant-panel p-3">
        <div class="text-xs text-terminal-sub">批次ID</div>
        <div class="mt-1 text-xl font-semibold text-terminal-main">{{ result.batchId }}</div>

        <div class="mt-3 grid grid-cols-2 gap-2 text-xs">
          <div class="rounded-lg bg-terminal-panel p-2">
            <div class="text-terminal-sub">状态</div>
            <div :class="result.success ? 'text-up' : 'text-down'">{{ result.batchStatus }}</div>
          </div>
          <div class="rounded-lg bg-terminal-panel p-2">
            <div class="text-terminal-sub">耗时</div>
            <div class="text-terminal-main">{{ result.costMillis }} ms</div>
          </div>
          <div class="rounded-lg bg-terminal-panel p-2">
            <div class="text-terminal-sub">成功</div>
            <div class="text-terminal-main">{{ result.successStepCount }}/{{ result.totalStepCount }}</div>
          </div>
          <div class="rounded-lg bg-terminal-panel p-2">
            <div class="text-terminal-sub">失败</div>
            <div class="text-terminal-main">{{ result.failedStepCount }}</div>
          </div>
        </div>

        <div v-if="result.failureReason" class="mt-2 text-xs text-terminal-down">
          {{ result.failureReason }}
        </div>
      </div>

      <ProgressPanel :steps="result.steps" />
    </div>
  </section>
</template>
