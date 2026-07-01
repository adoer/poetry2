<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { getCategories } from '../api'
import type { Category } from '../types'

const router = useRouter()
const userStore = useUserStore()
const categories = ref<Category[]>([])
const activeCategory = ref<number | undefined>(undefined)
const keyword = ref('')

onMounted(async () => {
  try {
    const res = await getCategories()
    categories.value = res.data.data
  } catch (e) {
    console.error('Failed to fetch categories', e)
  }
})

function selectCategory(id?: number) {
  activeCategory.value = id
  router.push({ name: 'NewsList', query: { categoryId: id } })
}

function search() {
  router.push({ name: 'NewsList', query: { keyword: keyword.value } })
}
</script>

<template>
  <div class="public-layout">
    <header class="header">
      <div class="header-inner">
        <!-- <div class="logo" @click="router.push({ name: 'NewsList' })">新闻网站</div> -->
        <div class="nav">
          <span
            :class="{ active: !activeCategory }"
            @click="selectCategory(undefined)"
          >全部</span>
          <span
            v-for="cat in categories"
            :key="cat.id"
            :class="{ active: activeCategory === cat.id }"
            @click="selectCategory(cat.id)"
          >{{ cat.name }}</span>
        </div>
        <div class="header-right">
          <el-input
            v-model="keyword"
            placeholder="搜索新闻..."
            size="small"
            style="width: 180px; margin-right: 12px"
            @keyup.enter="search"
          />
          <router-link to="/admin" v-if="userStore.isLoggedIn">管理</router-link>
          <router-link to="/login" v-else>登录</router-link>
        </div>
      </div>
    </header>
    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.public-layout { min-height: 100vh; background: #f5f5f5; }
.header { background: #fff; border-bottom: 1px solid #e0e0e0; position: sticky; top: 0; z-index: 100; }
.header-inner { max-width: 1200px; margin: 0 auto; display: flex; align-items: center; padding: 0 20px; height: 60px; }
.logo { font-size: 20px; font-weight: bold; color: #409eff; cursor: pointer; margin-right: 32px; }
.nav { display: flex; gap: 16px; flex: 1; }
.nav span { cursor: pointer; padding: 6px 12px; border-radius: 4px; font-size: 14px; }
.nav span:hover { color: #409eff; }
.nav span.active { color: #409eff; background: #ecf5ff; }
.header-right { display: flex; align-items: center; font-size: 14px; }
.main { max-width: 1200px; margin: 0 auto; padding: 20px; }
</style>
