<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAdminNewsList, deleteNews } from '../../api'
import type { NewsItem } from '../../types'

const router = useRouter()
const newsList = ref<NewsItem[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const keyword = ref('')
const loading = ref(false)
let fetchId = 0

async function fetchNews() {
  const id = ++fetchId
  loading.value = true
  try {
    const res = await getAdminNewsList({
      keyword: keyword.value,
      page: currentPage.value - 1,
      size: pageSize,
    })
    if (id !== fetchId) return
    newsList.value = res.data.data.content
    total.value = res.data.data.totalElements
  } catch (e) {
    if (id !== fetchId) return
    console.error('Failed to fetch admin news', e)
    newsList.value = []
  } finally {
    if (id === fetchId) loading.value = false
  }
}

async function search() {
  currentPage.value = 1
  await fetchNews()
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该新闻吗？', '提示')
    await deleteNews(id)
    ElMessage.success('删除成功')
    await fetchNews()
  } catch (e) {
    if (e !== 'cancel') console.error('Failed to delete news', e)
  }
}

async function onPageChange(page: number) {
  currentPage.value = page
  await fetchNews()
}

onMounted(fetchNews)
</script>

<template>
  <el-card>
    <div style="display: flex; justify-content: space-between; margin-bottom: 16px">
      <div>
        <el-input v-model="keyword" placeholder="搜索新闻标题..." style="width: 240px" @keyup.enter="search" />
        <el-button type="primary" style="margin-left: 8px" @click="search">搜索</el-button>
      </div>
      <el-button type="primary" @click="router.push({ name: 'AdminNewsCreate' })">发布新闻</el-button>
    </div>
    <el-table :data="newsList" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="categoryId" label="分类 ID" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'">
            {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="发布时间" width="180" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="router.push({ name: 'AdminNewsEdit', params: { id: row.id } })">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="text-align: center; margin-top: 16px">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="onPageChange"
      />
    </div>
  </el-card>
</template>
