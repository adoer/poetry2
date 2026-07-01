<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { logout } from '../api'

const router = useRouter()
const userStore = useUserStore()

async function handleLogout() {
  try {
    await logout()
  } catch (e) {
    console.error('Logout API call failed', e)
  }
  userStore.logout()
  router.push({ name: 'Login' })
}
</script>

<template>
  <div class="admin-layout">
    <el-container style="height: 100vh">
      <el-aside width="220px" style="background: #304156">
        <div class="admin-logo">新闻管理后台</div>
        <el-menu
          :default-active="router.currentRoute.value.path"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          router
        >
          <el-menu-item index="/admin">概览</el-menu-item>
          <el-menu-item index="/admin/news">新闻管理</el-menu-item>
          <el-menu-item index="/admin/categories">分类管理</el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header style="background: #fff; display: flex; align-items: center; justify-content: flex-end; border-bottom: 1px solid #e6e6e6">
          <span style="margin-right: 16px; font-size: 14px">{{ userStore.username }}</span>
          <el-button size="small" @click="handleLogout">退出</el-button>
        </el-header>
        <el-main style="background: #f0f2f5">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.admin-logo { height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 16px; font-weight: bold; border-bottom: 1px solid rgba(255,255,255,0.1); }
</style>
