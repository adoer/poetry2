<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFavorites, deleteFavorite } from '../../api'
import type { FavoriteItem } from '../../types'
const favorites = ref<FavoriteItem[]>([])
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const res = await getFavorites()
    favorites.value = res.data.data || []
  } catch (e: any) { console.error('Failed to load favorites', e); favorites.value = [] }
  finally { loading.value = false }
}

async function removeFav(item: FavoriteItem) {
  try {
    await deleteFavorite({ id: item.id })
    favorites.value = favorites.value.filter(f => f.id !== item.id)
  } catch (e: any) { ElMessage.error(e.response?.data?.message || '操作失败') }
}

onMounted(fetchData)
</script>

<template>
  <div class="max-w-3xl mx-auto py-8">
    <div class="flex items-center gap-4 mb-6">
      <div class="w-1 h-8 bg-vermillion rounded"></div>
      <h2 class="text-2xl font-semibold text-ink font-serif">我的收藏</h2>
    </div>

    <div v-if="loading" class="text-center py-16 text-sepia">加载中...</div>
    <div v-else-if="favorites.length === 0" class="text-center py-16 text-sepia">暂无收藏</div>
    <div v-else class="space-y-3">
      <div v-for="item in favorites" :key="item.id"
        class="fav-card rounded-xl p-5">
        <div class="flex justify-between items-center">
          <div class="flex-1 min-w-0">
            <h3 class="text-lg font-semibold text-ink">{{ item.title || item.content }}</h3>
            <p class="text-sm text-sepia mt-1">{{ item.writer }} &middot; {{ item.type === 'shiju' ? '诗词' : item.type === 'mingju' ? '名句' : '作者' }}</p>
          </div>
          <el-button size="small" text @click="removeFav(item)" class="text-sepia hover:text-vermillion">取消收藏</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.fav-card {
  background: linear-gradient(135deg, rgba(245, 240, 230, 0.95) 0%, rgba(255, 255, 255, 0.8) 100%);
  border: 1px solid rgba(139, 115, 85, 0.12);
  transition: all 0.3s ease;
}
.fav-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(26, 26, 26, 0.08);
}

.text-ink { color: #1a1a1a; }
.text-sepia { color: #8b7355; }
.text-vermillion { color: #c23a2b; }
.hover\:text-vermillion:hover { color: #c23a2b; }
.bg-vermillion { background: #c23a2b; }
.font-serif { font-family: 'Noto Serif SC', serif; }
</style>
