<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNews } from '../../api'
import type { NewsItem } from '../../types'
import { Loading } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const newsList = ref<NewsItem[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const loading = ref(false)

async function fetchNews() {
  loading.value = true
  try {
    const params: { page: number; size: number; categoryId?: number; keyword?: string } = {
      page: currentPage.value - 1,
      size: pageSize,
    }
    if (route.query.categoryId) params.categoryId = Number(route.query.categoryId)
    if (route.query.keyword) params.keyword = route.query.keyword as string
    const res = await getNews(params)
    newsList.value = res.data.data.content
    total.value = res.data.data.totalElements
  } catch (e) {
    console.error('Failed to fetch news list', e)
    newsList.value = []
  } finally {
    loading.value = false
  }
}

function onPageChange(page: number) {
  currentPage.value = page
  fetchNews()
}

function goDetail(id: number) {
  router.push({ name: 'NewsDetail', params: { id } })
}

watch(() => route.query, () => {
  currentPage.value = 1
  fetchNews()
})

onMounted(fetchNews)
</script>

<template>
  <div class="news-list">
    <div v-if="loading" style="text-align: center; padding: 60px 0">
      <el-icon class="is-loading" :size="32"><Loading /></el-icon>
    </div>
    <template v-else>
      <div v-if="newsList.length === 0" style="text-align: center; padding: 60px 0; color: #999">
        暂无新闻
      </div>
      <div v-for="item in newsList" :key="item.id" class="news-card" @click="goDetail(item.id)">
        <div class="news-info">
          <h3 class="news-title">{{ item.title }}</h3>
          <p class="news-summary">{{ item.summary || (item.content || '').slice(0, 120) + '...' }}</p>
          <div class="news-meta">
            <span class="category-tag">{{ item.categoryName }}</span>
            <span>{{ item.createdAt }}</span>
          </div>
        </div>
        <img v-if="item.coverImage" :src="item.coverImage" class="news-cover" alt="cover" />
      </div>
      <div v-if="total > pageSize" style="text-align: center; margin-top: 24px">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="onPageChange"
        />
      </div>
    </template>
  </div>
</template>

<style scoped>
.news-card { display: flex; background: #fff; border-radius: 8px; padding: 20px; margin-bottom: 16px; cursor: pointer; transition: box-shadow 0.2s; }
.news-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,0.1); }
.news-info { flex: 1; }
.news-title { font-size: 18px; font-weight: 600; margin-bottom: 8px; color: #1a1a1a; }
.news-summary { font-size: 14px; color: #666; line-height: 1.6; margin-bottom: 12px; }
.news-meta { font-size: 13px; color: #999; display: flex; gap: 12px; align-items: center; }
.category-tag { background: #ecf5ff; color: #409eff; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
.news-cover { width: 160px; height: 100px; object-fit: cover; border-radius: 4px; margin-left: 20px; }
</style>
