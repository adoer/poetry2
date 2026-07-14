<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { logout } from '../api'

const router = useRouter()
const userStore = useUserStore()

async function handleLogout() {
  try {
    await logout()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '退出失败')
  }
  userStore.logout()
  router.push({ name: 'Login' })
}
</script>

<template>
  <div class="admin-layout">
    <el-container style="height: 100vh">
      <el-aside width="220px">
        <div class="admin-logo">
          <div class="logo-seal">诗</div>
          <span class="logo-text">管理后台</span>
        </div>
        <el-menu
          :default-active="router.currentRoute.value.path"
          router
        >
          <el-menu-item index="/admin">概览</el-menu-item>
          <el-menu-item index="/admin/poesy">诗词管理</el-menu-item>
          <el-menu-item index="/admin/quotes">名句管理</el-menu-item>
          <el-menu-item index="/admin/authors">作者管理</el-menu-item>
          <el-menu-item index="/admin/users">用户管理</el-menu-item>
          <el-menu-item index="/admin/stats">使用情况</el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <span class="header-user">{{ userStore.username }}</span>
          <el-button size="small" class="header-btn" @click="handleLogout">退出</el-button>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style>
.admin-layout {
  font-family: 'Noto Serif SC', 'SimSun', 'STSong', serif;
  --admin-primary: #c23a2b;
  --admin-primary-dark: #a83224;
  --admin-jade: #5a7d6c;
  --admin-gold: #b8860b;
  --admin-bg: #f5f0e6;
  --admin-sidebar-bg: #2a2520;
  --admin-sidebar-text: #b8aea0;
  --admin-sidebar-active: #c23a2b;
  --admin-header-bg: #e8e3d8;
  --admin-card-bg: rgba(255, 255, 255, 0.85);
  --admin-border: rgba(139, 115, 85, 0.15);
  --admin-input-bg: #faf9f5;
  --admin-text: #1a1a1a;
  --admin-text-secondary: #8b7355;

  --el-color-primary: var(--admin-primary);
  --el-color-primary-dark-2: var(--admin-primary-dark);
  --el-color-success: var(--admin-jade);
  --el-color-warning: var(--admin-gold);
  --el-color-danger: var(--admin-primary);

  --el-card-bg-color: var(--admin-card-bg);
  --el-card-border-color: var(--admin-border);
  --el-card-padding: 20px;

  --el-table-header-bg-color: #e8e3d8;
  --el-table-border-color: rgba(139, 115, 85, 0.1);
  --el-table-row-hover-bg-color: rgba(194, 58, 43, 0.04);
  --el-table-tr-bg-color: transparent;
  --el-table-header-text-color: var(--admin-text);

  --el-input-bg-color: var(--admin-input-bg);
  --el-input-border-color: var(--admin-border);
  --el-input-focus-border-color: var(--admin-primary);
  --el-input-hover-border-color: var(--admin-primary);
  --el-input-text-color: var(--admin-text);
  --el-input-placeholder-color: var(--admin-text-secondary);

  --el-dialog-bg-color: #faf9f5;
  --el-dialog-border-color: var(--admin-border);

  --el-pagination-button-color: var(--admin-text-secondary);
  --el-pagination-hover-color: var(--admin-primary);

  --el-menu-bg-color: var(--admin-sidebar-bg);
  --el-menu-text-color: var(--admin-sidebar-text);
  --el-menu-active-color: var(--admin-sidebar-active);
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.05);
  --el-menu-hover-text-color: #f5f0e6;

  --el-popup-bg-color: #faf9f5;
  --el-popup-border-color: var(--admin-border);

  --el-messagebox-bg-color: #faf9f5;
  --el-messagebox-border-color: var(--admin-border);
}

.admin-layout .el-button {
  border-radius: 8px;
  font-family: inherit;
}

.admin-layout .el-button--primary {
  background: linear-gradient(135deg, var(--admin-primary) 0%, var(--admin-primary-dark) 100%);
  border: none;
  color: #fff;
}

.admin-layout .el-button--primary:hover {
  box-shadow: 0 4px 15px rgba(194, 58, 43, 0.4);
}

.admin-layout .el-button--primary:active {
  background: linear-gradient(135deg, var(--admin-primary-dark) 0%, #8b2520 100%);
}

.admin-layout .el-button--success {
  background: linear-gradient(135deg, var(--admin-jade) 0%, #4a6b5c 100%);
  border: none;
  color: #fff;
}

.admin-layout .el-button--success:hover {
  box-shadow: 0 4px 15px rgba(90, 125, 108, 0.4);
}

.admin-layout .el-button--default {
  border: 1px solid var(--admin-border);
  color: var(--admin-text-secondary);
  background: transparent;
}

.admin-layout .el-button--default:hover {
  border-color: var(--admin-primary);
  color: var(--admin-primary);
  background: rgba(194, 58, 43, 0.05);
}

.admin-layout .el-table {
  font-family: inherit;
  border-radius: 8px;
  overflow: hidden;
}

.admin-layout .el-table th.el-table__cell {
  font-weight: 600;
}

.admin-layout .el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background: rgba(139, 115, 85, 0.04);
}

.admin-layout .el-card {
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid var(--admin-border);
  transition: all 0.3s ease;
}

.admin-layout .el-card:hover {
  box-shadow: 0 8px 30px rgba(139, 119, 101, 0.12);
}

.admin-layout .el-card__header {
  border-bottom: 1px solid var(--admin-border);
  padding: 16px 20px;
  font-weight: 600;
  color: var(--admin-text);
}

.admin-layout .el-input__wrapper {
  box-shadow: 0 0 0 1px var(--admin-border) inset;
  border-radius: 8px;
  transition: box-shadow 0.2s;
}

.admin-layout .el-input__wrapper:hover {
  box-shadow: 0 0 0 1px var(--admin-primary) inset;
}

.admin-layout .el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px var(--admin-primary) inset;
}

.admin-layout .el-select .el-input__wrapper {
  box-shadow: 0 0 0 1px var(--admin-border) inset;
}

.admin-layout .el-pagination button:hover {
  color: var(--admin-primary);
}

.admin-layout .el-pagination .el-pager li.active {
  color: var(--admin-primary);
}

.admin-layout .el-dialog {
  border-radius: 12px;
}

.admin-layout .el-dialog__header {
  padding: 20px 24px 12px;
  border-bottom: 1px solid var(--admin-border);
}

.admin-layout .el-dialog__title {
  font-family: inherit;
  font-weight: 600;
  color: var(--admin-text);
}

.admin-layout .el-dialog__body {
  padding: 20px 24px;
}

.admin-layout .el-dialog__footer {
  padding: 12px 24px 20px;
  border-top: 1px solid var(--admin-border);
}

.admin-layout .el-message-box {
  border-radius: 12px;
}

.admin-layout .el-message-box__title {
  font-family: inherit;
}

.admin-layout .el-message-box__btns .el-button--primary {
  background: linear-gradient(135deg, var(--admin-primary) 0%, var(--admin-primary-dark) 100%);
  border: none;
}

.admin-layout .el-tag {
  border-radius: 6px;
}

.admin-layout .el-menu-item {
  font-size: 14px;
  letter-spacing: 0.5px;
}

.admin-layout .el-menu-item.is-active {
  position: relative;
}

.admin-layout .el-menu-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: var(--admin-primary);
  border-radius: 0 3px 3px 0;
}

.admin-layout .el-aside {
  background: var(--admin-sidebar-bg);
  overflow: hidden;
}

.admin-layout .el-header {
  background: var(--admin-header-bg);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  border-bottom: 1px solid var(--admin-border);
  height: 56px;
  padding: 0 20px;
}

.admin-layout .el-main {
  background: var(--admin-bg);
  padding: 20px;
}

.admin-layout .el-progress-bar__inner {
  border-radius: 4px;
}

.admin-layout .el-progress-bar__outer {
  background: rgba(139, 115, 85, 0.1);
  border-radius: 4px;
}
</style>

<style scoped>
.admin-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-seal {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #c23a2b 0%, #8b2520 100%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
  font-weight: bold;
  transform: rotate(-5deg);
  box-shadow: 0 2px 6px rgba(194, 58, 43, 0.4);
  flex-shrink: 0;
}

.logo-text {
  color: #f5f0e6;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 2px;
}

.header-user {
  margin-right: 16px;
  font-size: 14px;
  color: var(--admin-text);
}

.header-btn {
  flex-shrink: 0;
}
</style>
