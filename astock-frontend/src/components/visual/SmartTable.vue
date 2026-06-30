<script setup lang="ts">
import { computed } from 'vue'
import { displayColumns, formatCell, statusType, type AnyRecord } from '@/utils/dataExtract'

const props = withDefaults(defineProps<{
  title: string
  rows: AnyRecord[]
  preferredColumns?: string[]
  maxColumns?: number
}>(), {
  maxColumns: 8
})

const columns = computed(() => displayColumns(props.rows, props.preferredColumns || [], props.maxColumns))
</script>

<template>
  <section class="quant-panel p-3">
    <div class="mb-2 flex items-center justify-between">
      <h3 class="text-sm font-semibold text-terminal-main">{{ title }}</h3>
      <span class="text-xs text-terminal-sub">{{ rows.length }} 条</span>
    </div>

    <el-empty v-if="rows.length === 0" description="暂无数据" />

    <el-table v-else :data="rows" size="small" border height="360">
      <el-table-column
        v-for="column in columns"
        :key="column"
        :prop="column"
        :label="column"
        min-width="130"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <el-tag
            v-if="String(column).toLowerCase().includes('status') || String(column).toLowerCase().includes('level') || String(column).toLowerCase().includes('action')"
            :type="statusType(row[column])"
            effect="dark"
            size="small"
          >
            {{ formatCell(row[column]) }}
          </el-tag>
          <span v-else>{{ formatCell(row[column]) }}</span>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>
