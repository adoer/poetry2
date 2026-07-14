<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminAuthorsList, deleteAdminAuthor } from '../../../api/admin'
import type { AuthorItem } from '../../../types'

const router = useRouter()
const list = ref<AuthorItem[]>([])
const total = ref(0)
const page = ref(1)
const keyword = ref('')
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const res = await getAdminAuthorsList({ page: page.value, size: 20, keyword: keyword.value || undefined })
    list.value = res.data.data.content
    total.value = res.data.data.totalElements
  } finally {
    loading.value = false
  }
}

function onSearch() {
  page.value = 1
  fetchData()
}

function goCreate() {
  router.push({ name: 'AdminAuthorsCreate' })
}

function goEdit(id: number) {
  router.push({ name: 'AdminAuthorsEdit', params: { id } })
}

async function onDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该作者吗？此操作不可恢复。', '确认删除', { type: 'warning' })
    await deleteAdminAuthor(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // cancelled
  }
}

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="toolbar">
      <div>
        <el-input v-model="keyword" placeholder="搜索作者姓名" class="search-input" clearable @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch">搜索</el-button>
      </div>
      <el-button type="success" @click="goCreate">新增作者</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="dynasty" label="朝代" width="80" />
      <el-table-column prop="simpleIntro" label="简介" min-width="250" show-overflow-tooltip />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="goEdit(row.id)">编辑</el-button>
          <el-button size="small" type="danger" @click="onDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap">
      <el-pagination v-model:current-page="page" :page-size="20" :total="total" layout="prev, pager, next" @current-change="fetchData" />
    </div>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.search-input {
  width: 300px;
  margin-right: 8px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
