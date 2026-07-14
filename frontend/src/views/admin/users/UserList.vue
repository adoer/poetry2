<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminUsersList, getAdminUserDetail } from '../../../api/admin'
import type { AdminUserItem } from '../../../api/admin'

const list = ref<AdminUserItem[]>([])
const total = ref(0)
const page = ref(1)
const keyword = ref('')
const loading = ref(false)
const dialogVisible = ref(false)
const currentUser = ref<AdminUserItem | null>(null)

async function fetchData() {
  loading.value = true
  try {
    const res = await getAdminUsersList({ page: page.value, size: 20, keyword: keyword.value || undefined })
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

async function showDetail(id: string) {
  try {
    const res = await getAdminUserDetail(id)
    currentUser.value = res.data.data
    dialogVisible.value = true
  } catch {
    ElMessage.error('获取用户详情失败')
  }
}

onMounted(fetchData)
</script>

<template>
  <div>
    <div style="display: flex; margin-bottom: 16px">
      <el-input v-model="keyword" placeholder="搜索用户名/昵称/手机号/邮箱" style="width: 400px" clearable @keyup.enter="onSearch" />
      <el-button type="primary" style="margin-left: 8px" @click="onSearch">搜索</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
      <el-table-column prop="vipLevel" label="角色" width="80">
        <template #default="{ row }">
          <el-tag :type="row.vipLevel === 'admin' ? 'danger' : 'info'" size="small">
            {{ row.vipLevel === 'admin' ? '管理员' : '用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="160" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="display: flex; justify-content: center; margin-top: 16px">
      <el-pagination v-model:current-page="page" :page-size="20" :total="total" layout="prev, pager, next" @current-change="fetchData" />
    </div>

    <el-dialog v-model="dialogVisible" title="用户详情" width="500px">
      <div v-if="currentUser">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentUser.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentUser.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag :type="currentUser.vipLevel === 'admin' ? 'danger' : 'info'" size="small">
              {{ currentUser.vipLevel === 'admin' ? '管理员' : '用户' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ currentUser.createTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="最后登录">{{ currentUser.lastlogintime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="注册IP">{{ currentUser.ip || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>
