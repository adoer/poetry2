<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../store/user'
import { getAdminStatsOverview } from '../../api/admin'

const userStore = useUserStore()
const stats = ref({ poesyCount: 0, quotesCount: 0, authorsCount: 0, userCount: 0 })

onMounted(async () => {
  try {
    const res = await getAdminStatsOverview()
    stats.value = res.data.data
  } catch {
    // stats unavailable
  }
})
</script>

<template>
  <div>
    <el-card>
      <h2>欢迎回来，{{ userStore.username }}</h2>
      <p class="welcome-sub">在这里管理古诗文平台的数据。</p>
    </el-card>

    <div class="stat-grid">
      <el-card shadow="hover">
        <div class="stat-card">
          <div class="stat-num vermillion">{{ stats.poesyCount }}</div>
          <div class="stat-label">诗词总数</div>
        </div>
      </el-card>
      <el-card shadow="hover">
        <div class="stat-card">
          <div class="stat-num jade">{{ stats.quotesCount }}</div>
          <div class="stat-label">名句总数</div>
        </div>
      </el-card>
      <el-card shadow="hover">
        <div class="stat-card">
          <div class="stat-num gold">{{ stats.authorsCount }}</div>
          <div class="stat-label">作者总数</div>
        </div>
      </el-card>
      <el-card shadow="hover">
        <div class="stat-card">
          <div class="stat-num vermillion">{{ stats.userCount }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.welcome-sub {
  color: #8b7355;
  margin-top: 12px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-top: 16px;
}

.stat-card {
  text-align: center;
  padding: 12px;
}

.stat-num {
  font-size: 32px;
  font-weight: bold;
}

.stat-label {
  color: #8b7355;
  margin-top: 8px;
}

.vermillion { color: #c23a2b; }
.jade { color: #5a7d6c; }
.gold { color: #b8860b; }
</style>
