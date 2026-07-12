<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategoryDetail } from '../../api'
import CategoryWriter from '../../components/common/CategoryWriter.vue'

const route = useRoute()
const router = useRouter()
const items = ref<{ id: number; title: string; writer: string }[]>([])
const loading = ref(false)

const titleMap: Record<string, string> = {
  '唐诗三百首': '唐诗三百首',
  '宋词三百首': '宋词三百首',
  '古诗三百首': '古诗三百首',
  '小学古诗': '小学古诗',
  '初中古诗': '初中古诗',
  '高中古诗': '高中古诗',
  '小学文言文': '小学文言文',
  '初中文言文': '初中文言文',
  '高中文言文': '高中文言文',
  '宋词精选': '宋词精选',
  '古诗十九首': '古诗十九首',
  '诗经': '诗经',
  '乐府': '乐府',
  '楚辞': '楚辞',
}

async function fetchData() {
  loading.value = true
  try {
    const name = route.params.name as string
    const res = await getCategoryDetail(name)
    items.value = res.data.data || []
  } catch (e) { console.error('Failed to load category', e); items.value = [] }
  finally { loading.value = false }
}

const displayTitle = (name: string) => titleMap[name] || `关于${name}的古诗`

watch(() => route.params.name, fetchData)
onMounted(fetchData)
</script>

<template>
  <div class="grid lg:grid-cols-3 gap-8">
    <div class="lg:col-span-2">
      <div class="category-card rounded-xl p-6 mb-6">
        <div class="flex items-center gap-4 mb-4">
          <div class="w-1 h-8 bg-vermillion rounded"></div>
          <h2 class="text-2xl font-semibold text-ink font-serif">{{ displayTitle(route.params.name as string) }}</h2>
        </div>
      </div>
      <div class="category-card rounded-xl p-6">
        <div v-if="loading" class="text-center py-10 text-sepia">加载中...</div>
        <div v-else-if="items.length === 0" class="text-center py-10 text-sepia">该分类暂无诗词</div>
        <div v-else class="grid md:grid-cols-2 gap-4">
          <div v-for="el in items" :key="el.id" class="pItem cursor-pointer"
            @click="router.push({ name: 'PoesyDetail', params: { id: el.id } })">
            <span class="text-ink hover:text-vermillion transition-colors">{{ el.title }}</span>
            <span class="text-xs text-sepia ml-2">({{ el.writer }})</span>
          </div>
        </div>
      </div>
    </div>
    <div class="space-y-6">
      <CategoryWriter layout="sidebar" :type-limit="0" :writer-limit="0" />
    </div>
  </div>
</template>

<style scoped>
.category-card {
  background: linear-gradient(135deg, rgba(245, 240, 230, 0.95) 0%, rgba(255, 255, 255, 0.8) 100%);
  border: 1px solid rgba(139, 115, 85, 0.12);
  transition: all 0.3s ease;
}
.category-card:hover { box-shadow: 0 4px 20px rgba(26, 26, 26, 0.08); }

.pItem {
  padding: 8px 0;
  border-bottom: 1px dashed #DAD9D1;
}
.pItem:last-child { border-bottom: none; }

.text-ink { color: #1a1a1a; }
.text-sepia { color: #8b7355; }
.hover\:text-vermillion:hover { color: #c23a2b; }
.bg-vermillion { background: #c23a2b; }
.font-serif { font-family: 'Noto Serif SC', serif; }
.font-brush { font-family: 'Ma Shan Zheng', cursive; }


</style>
