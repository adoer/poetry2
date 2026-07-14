<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getAdminStatsOverview, getAdminStatsRegistration, getAdminStatsFavorites } from '../../../api/admin'

const overview = ref({ poesyCount: 0, quotesCount: 0, authorsCount: 0, userCount: 0 })
const registration = ref<Record<string, number>>({})
const favorites = ref({ poesy: 0, quotes: 0 })
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const [overviewRes, regRes, favRes] = await Promise.all([
      getAdminStatsOverview(),
      getAdminStatsRegistration(),
      getAdminStatsFavorites(),
    ])
    overview.value = overviewRes.data.data
    registration.value = regRes.data.data.daily
    favorites.value = favRes.data.data
  } catch {
    // stats unavailable
  } finally {
    loading.value = false
  }
})

const regDays = computed(() => {
  const days = Object.entries(registration.value)
  days.sort((a, b) => a[0].localeCompare(b[0]))
  const last30 = days.slice(-30)
  const counts = last30.map(([, c]) => c)
  const maxCount = Math.max(...counts, 1)
  return {
    labels: last30.map(([d]) => d.slice(5)),
    counts,
    maxCount,
  }
})

const favTotal = computed(() => favorites.value.poesy + favorites.value.quotes)

const favPercent = (val: number) => {
  const total = favTotal.value
  return total > 0 ? ((val / total) * 100).toFixed(1) : '0'
}
</script>

<template>
  <div v-loading="loading">
    <div class="stat-grid">
      <el-card shadow="hover">
        <div class="stat-card">
          <div class="stat-value stat-value-primary">{{ overview.poesyCount }}</div>
          <div class="stat-label">诗词总数</div>
        </div>
      </el-card>
      <el-card shadow="hover">
        <div class="stat-card">
          <div class="stat-value stat-value-success">{{ overview.quotesCount }}</div>
          <div class="stat-label">名句总数</div>
        </div>
      </el-card>
      <el-card shadow="hover">
        <div class="stat-card">
          <div class="stat-value stat-value-warning">{{ overview.authorsCount }}</div>
          <div class="stat-label">作者总数</div>
        </div>
      </el-card>
      <el-card shadow="hover">
        <div class="stat-card">
          <div class="stat-value stat-value-danger">{{ overview.userCount }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </el-card>
    </div>

    <el-row :gutter="16">
      <el-col :span="14">
        <el-card>
          <template #header>用户注册趋势（最近30天）</template>
          <div v-if="regDays.labels.length > 0" class="chart-bars">
            <div v-for="(c, i) in regDays.counts" :key="i" class="chart-bar-col">
              <div class="chart-bar-label">{{ c }}</div>
              <div
                class="chart-bar"
                :style="{
                  height: Math.max(4, (c / regDays.maxCount) * 200) + 'px',
                }"
              />
              <div class="chart-bar-date">
                {{ regDays.labels[i] }}
              </div>
            </div>
          </div>
          <div v-else class="chart-empty">
            暂无注册数据
          </div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <template #header>收藏分布</template>
          <div style="padding: 20px">
            <div v-if="favTotal > 0">
              <div style="margin-bottom: 16px">
                <div style="display: flex; justify-content: space-between; margin-bottom: 4px">
                  <span>诗词收藏</span>
                  <span>{{ favorites.poesy }} ({{ favPercent(favorites.poesy) }}%)</span>
                </div>
                <el-progress :percentage="Number(favPercent(favorites.poesy))" />
              </div>
              <div>
                <div style="display: flex; justify-content: space-between; margin-bottom: 4px">
                  <span>名句收藏</span>
                  <span>{{ favorites.quotes }} ({{ favPercent(favorites.quotes) }}%)</span>
                </div>
                <el-progress :percentage="Number(favPercent(favorites.quotes))" />
              </div>
            </div>
            <div v-else class="fav-empty">
              暂无收藏数据
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}
.stat-card {
  text-align: center;
  padding: 12px;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
}
.stat-value-primary {
  color: var(--el-color-primary);
}
.stat-value-success {
  color: var(--el-color-success);
}
.stat-value-warning {
  color: var(--el-color-warning);
}
.stat-value-danger {
  color: var(--el-color-danger);
}
.stat-label {
  color: var(--el-text-color-secondary);
  margin-top: 8px;
}
.chart-bars {
  height: 300px;
  display: flex;
  align-items: flex-end;
  gap: 2px;
}
.chart-bar-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.chart-bar-label {
  font-size: 10px;
  color: var(--el-text-color-secondary);
}
.chart-bar {
  width: 100%;
  background: var(--el-color-primary);
  border-radius: 2px 2px 0 0;
  min-width: 8px;
}
.chart-bar-date {
  font-size: 9px;
  color: var(--el-text-color-placeholder);
  margin-top: 4px;
  transform: rotate(-45deg);
  white-space: nowrap;
}
.chart-empty {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-placeholder);
}
.fav-empty {
  text-align: center;
  color: var(--el-text-color-placeholder);
  padding: 40px 0;
}
</style>
