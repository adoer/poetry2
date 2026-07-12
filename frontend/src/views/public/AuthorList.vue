<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAuthors } from '../../api'
import type { AuthorItem } from '../../types'

const router = useRouter()
const authors = ref<AuthorItem[]>([])
const pageNum = ref(1)
const total = ref(4)
const loading = ref(false)
const imgErrors = ref<Set<number>>(new Set())

async function fetchData() {
  loading.value = true
  imgErrors.value = new Set()
  try {
    const res = await getAuthors({ page: pageNum.value })
    authors.value = res.data.data.content
  } catch (e) { console.error('Failed to load authors', e); authors.value = [] }
  finally { loading.value = false }
}

function prevPage() { if (pageNum.value > 1) { pageNum.value--; fetchData() } }
function nextPage() { if (pageNum.value < total.value) { pageNum.value++; fetchData() } }

function toDetail(item: AuthorItem) {
  router.push({ name: 'AuthorDetail', params: { id: item.id } })
}

function headImgUrl(id: number, url?: string): string {
  if (!url || !url.includes('image_')) return ''
  return `/images/writer/image_${id}.jpg`
}

function onImgError(id: number) {
  imgErrors.value = new Set([...imgErrors.value, id])
}

onMounted(fetchData)
</script>

<template>
  <div class="authors-layout">
    <div class="flex items-center gap-4 mb-6">
      <div class="w-1 h-8 bg-vermillion rounded"></div>
      <h2 class="text-2xl font-semibold text-ink font-serif">作者</h2>
    </div>
    <div v-if="loading" class="text-center py-16 text-sepia">加载中...</div>
    <div v-else class="flex flex-wrap justify-between">
      <div v-for="(item, index) in authors" :key="index"
        class="poem-card rounded-xl p-5 mb-6" style="width: calc(33.333% - 16px);">
        <div class="text-center mb-3">
          <div v-if="headImgUrl(item.id, item.headImageUrl) && !imgErrors.has(item.id)" class="mx-auto rounded-full mb-3 overflow-hidden"
            style="width: 40px; height: 40px; border: 2px solid rgba(194, 58, 43, 0.2);">
            <img :src="headImgUrl(item.id, item.headImageUrl)" :alt="item.name"
              class="w-full h-full object-cover" @error="onImgError(item.id)" />
          </div>
          <div v-else class="mx-auto rounded-full mb-3 flex items-center justify-center"
            style="width: 40px; height: 40px; background: linear-gradient(135deg, #F0EFE2 0%, #e8e4d5 100%); border: 2px solid rgba(194, 58, 43, 0.2);">
            <span class="text-vermillion font-bold text-base">{{ item.name?.charAt(0) }}</span>
          </div>
          <h3 class="text-xl font-bold text-ink font-brush cursor-pointer hover:text-vermillion transition-colors"
            @click="toDetail(item)">
            {{ item.name }}
          </h3>
        </div>
        <p class="text-sepia text-sm leading-relaxed line-clamp-3 mb-3 text-center">{{ item.simpleIntro }}</p>
        <div class="text-center">
          <span @click="toDetail(item)" class="text-vermillion text-sm hover:underline cursor-pointer">查看详情>></span>
        </div>
      </div>
    </div>
    <div class="flex justify-between mt-8">
      <div @click="nextPage" class="pagenumber flex-1 mr-2"
        :class="{ 'disabled': pageNum === total }">
        下一页
      </div>
      <div @click="prevPage" class="pagenumber w-24 text-center"
        :class="{ 'disabled': pageNum === 1 }">
        上一页
      </div>
    </div>
  </div>
</template>

<style scoped>
.authors-layout { max-width: 100%; }

.poem-card {
  position: relative;
  background: linear-gradient(135deg, rgba(245, 240, 230, 0.95) 0%, rgba(255, 255, 255, 0.8) 100%);
  border: 1px solid rgba(139, 115, 85, 0.12);
  transition: all 0.3s ease;
  overflow: hidden;
  padding: 24px 20px;
}
.poem-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(26, 26, 26, 0.08);
  border-color: rgba(194, 58, 43, 0.15);
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.pagenumber {
  line-height: 40px;
  height: 40px;
  margin-top: 20px;
  background-color: rgba(139, 115, 85, 0.08);
  text-align: center;
  font-size: 14px;
  border: 1px solid rgba(139, 115, 85, 0.15);
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.pagenumber.disabled {
  background-color: rgba(139, 115, 85, 0.05) !important;
  color: rgba(139, 115, 85, 0.5);
}
.pagenumber:hover:not(.disabled) {
  background: rgba(194, 58, 43, 0.1);
  border-color: rgba(194, 58, 43, 0.3);
}

.text-ink { color: #1a1a1a; }
.text-sepia { color: #8b7355; }
.text-vermillion { color: #c23a2b; }
.font-brush { font-family: 'Ma Shan Zheng', cursive; }
.hover\:text-vermillion:hover { color: #c23a2b; }
.bg-vermillion { background: #c23a2b; }
</style>
