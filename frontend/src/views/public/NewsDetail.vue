<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNewsDetail } from '../../api'
import type { NewsItem } from '../../types'
import { Loading } from '@element-plus/icons-vue'
import DOMPurify from 'dompurify'

const route = useRoute()
const router = useRouter()
const news = ref<NewsItem | null>(null)
const loading = ref(true)

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getNewsDetail(Number(route.params.id))
    news.value = res.data.data
  } catch (e) {
    console.error('Failed to fetch news detail', e)
    news.value = null
  } finally {
    loading.value = false
  }
}

onMounted(fetchDetail)

watch(() => route.params.id, () => {
  fetchDetail()
})
</script>

<template>
  <div class="news-detail">
    <div v-if="loading" style="text-align: center; padding: 60px 0">
      <el-icon class="is-loading" :size="32"><Loading /></el-icon>
    </div>
    <div v-else-if="!news" style="text-align: center; padding: 60px 0; color: #999">
      新闻不存在或已删除
    </div>
    <div v-else class="detail-content">
      <h1 class="detail-title">{{ news.title }}</h1>
      <div class="detail-meta">
        <span class="category-tag">{{ news.categoryName }}</span>
        <span>{{ news.createdAt }}</span>
      </div>
      <img v-if="news.coverImage" :src="news.coverImage" class="detail-cover" alt="cover" />
      <div class="detail-body" v-html="DOMPurify.sanitize(news.content || '').replace(/\n/g, '<br>')"></div>
      <div style="margin-top: 32px">
        <el-button @click="router.back()">返回</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.detail-content { background: #fff; border-radius: 8px; padding: 32px; }
.detail-title { font-size: 28px; font-weight: 700; margin-bottom: 16px; color: #1a1a1a; }
.detail-meta { font-size: 14px; color: #999; margin-bottom: 24px; display: flex; gap: 12px; align-items: center; }
.category-tag { background: #ecf5ff; color: #409eff; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
.detail-cover { width: 100%; max-height: 400px; object-fit: cover; border-radius: 8px; margin-bottom: 24px; }
.detail-body { font-size: 16px; line-height: 1.8; color: #333; }
</style>
