<template>
  <div class="not-found text-center py-20">
    <div class="seal mx-auto mb-6">
      <span class="text-white text-sm">404</span>
    </div>
    <h1 class="text-5xl font-bold text-ink mb-4 font-serif">页面未找到</h1>
    <p class="text-sepia mb-8">您访问的页面不存在或已移除</p>
    <el-button @click="router.push({ name: 'Home' })" class="home-btn">返回首页</el-button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style scoped>
.seal {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #c23a2b 0%, #8b2520 100%);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 3px 10px rgba(194, 58, 43, 0.4);
  transform: rotate(-5deg);
}

.text-ink { color: #1a1a1a; }
.text-sepia { color: #8b7355; }
.font-serif { font-family: 'Noto Serif SC', serif; }

.home-btn {
  background: linear-gradient(135deg, #c23a2b 0%, #a83224 100%);
  border: none;
  color: white;
  height: 44px;
  font-size: 16px;
  letter-spacing: 2px;
}
.home-btn:hover { box-shadow: 0 6px 20px rgba(194, 58, 43, 0.4); }
</style>
