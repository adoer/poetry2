<script setup lang="ts">
import { useUserStore } from '../../store/user'
const userStore = useUserStore()
</script>

<template>
  <el-card>
    <h2>欢迎回来，{{ userStore.username }}</h2>
    <p style="color: #666; margin-top: 12px">在这里管理新闻内容和分类。</p>
  </el-card>
</template>
