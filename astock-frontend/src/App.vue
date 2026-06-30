<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import EngineBatchPanel from '@/components/EngineBatchPanel.vue'

const route = useRoute()

const navs = [
  ['今日市场总览', '/dashboard/market'],
  ['历史相似行情', '/similarity/market'],
  ['情绪周期状态机', '/emotion-cycle/state-machine'],
  ['历史周期样本库', '/cycle-samples/page'],
  ['主线题材雷达', '/mainlines/radar'],
  ['板块强度排行', '/sectors/strength'],
  ['龙头梯队监控', '/leaders/ladder'],
  ['龙头个股画像', '/leaders/000001/profile'],
  ['条件判定', '/patterns/conditions'],
  ['风控失效信号', '/risks/control'],
  ['回测实验室', '/backtests/lab'],
  ['回测报告详情', '/backtests/reports/1'],
  ['每日复盘工作台', '/reviews/daily/workbench'],
  ['规则版本管理', '/rules/versions/page'],
  ['Agent研发审计', '/agent-audit/dashboard']
]

const pageTitle = computed(() => String(route.meta.title || 'A股情绪周期复盘系统'))
</script>

<template>
  <div class="min-h-screen bg-terminal-base text-terminal-main">
    <aside class="fixed left-0 top-0 h-screen w-64 overflow-y-auto border-r border-terminal-line bg-terminal-panel p-3">
      <div class="mb-4">
        <div class="text-base font-semibold">A股情绪周期系统</div>
        <div class="text-xs text-terminal-sub">真实API联调层</div>
      </div>
      <nav class="space-y-1">
        <RouterLink
          v-for="item in navs"
          :key="item[1]"
          :to="item[1]"
          class="block rounded-lg px-3 py-2 text-xs text-terminal-sub hover:bg-terminal-card hover:text-terminal-main"
          :class="{ 'bg-terminal-card text-terminal-main': route.path === item[1] }"
        >
          {{ item[0] }}
        </RouterLink>
      </nav>
    </aside>

    <main class="ml-64 space-y-4 p-4">
      <header class="flex items-center justify-between">
        <div>
          <div class="text-xs text-terminal-sub">当前页面</div>
          <div class="text-xl font-semibold">{{ pageTitle }}</div>
        </div>
      </header>

      <EngineBatchPanel />

      <RouterView />
    </main>
  </div>
</template>
