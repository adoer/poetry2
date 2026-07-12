<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../store/user'
import { getRecommend, getCategories, getWriters, addFavorite, deleteFavorite } from '../../api'
import InkBlobCanvas from '../../components/InkBlobCanvas.vue'
import CategoryWriter from '../../components/common/CategoryWriter.vue'
import type { RecommendItem } from '../../types'

const router = useRouter()
const userStore = useUserStore()
const recommendList = ref<RecommendItem[]>([])
const categories = ref<string[]>([])
const writers = ref<{ name: string; id: number }[]>([])
const likedMap = ref<Record<number, boolean>>({})
const writerImgErrors = ref<Set<number>>(new Set())

function writerImgUrl(item: RecommendItem): string {
  if (item.reComType !== 'Poesy' || !item.writerImg) return ''
  return item.writerImg.startsWith('/') ? item.writerImg : `/images/writer/${item.writerImg}`
}

function onWriterImgError(id: number) {
  writerImgErrors.value = new Set([...writerImgErrors.value, id])
}

onMounted(async () => {
  try {
    const res = await getRecommend()
    const list = res.data.data || []
    recommendList.value = list
    list.forEach((item: any) => {
      if (item.id) likedMap.value[item.id] = item.like || false
    })
  } catch (e) { console.error('Failed to load recommendations', e) }
  try {
    const res = await getCategories()
    categories.value = (res.data.data || []).map(c => c.name)
  } catch (e) { console.error('Failed to load categories', e) }
  try {
    const res = await getWriters()
    writers.value = res.data.data || []
  } catch (e) { console.error('Failed to load writers', e) }
})

function toDetail(item: RecommendItem) {
  if (item.reComType === 'Poesy') {
    const id = item.id ?? Number(item.poetryId)
    if (id) window.open(`${window.location.origin}/poesy/${id}`)
  }
}

async function likeClick(item: any) {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Login', query: { redirect: router.currentRoute.value.fullPath } })
    return
  }
  const wasLiked = likedMap.value[item.id]
  try {
    if (!wasLiked) {
      const res = await addFavorite({
        id: String(item.reComType === 'Quotes' ? item.poetryId : item.id),
        type: item.reComType,
        title: item.title || '',
        content: item.reComType === 'Quotes' ? item.content : '',
        writer: item.writer,
      })
      if (res.data.code === 200) {
        ElMessage.success('收藏成功')
        likedMap.value[item.id] = true
      } else if (res.data.code === 201) {
        ElMessage.warning('已收藏')
        likedMap.value[item.id] = true
      }
    } else {
      const res = await deleteFavorite({ contentId: String(item.id) })
      if (res.data.code === 200) {
        ElMessage.success('已取消收藏')
        likedMap.value[item.id] = false
      }
    }
  } catch {
    ElMessage.error('操作失败')
  }
}

function copyClick(item: any) {
  const text = item.content || ''
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('复制成功')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}
</script>

<template>
  <div class="space-y-8">
    <div v-if="recommendList.length > 0">
      <div class="flex justify-center mb-8">
        <div class="scroll-container relative flex justify-center" v-if="recommendList[0]">
          <InkBlobCanvas :width="800" :height="400" class="hidden md:block" />
          <div class="scroll-end left"></div>
          <div class="scroll-rod left"></div>
          <div class="scroll-content bg-rice/60 rounded-lg p-8 max-w-2xl text-center" v-if="recommendList[0]">
            <template v-if="recommendList[0].reComType === 'Poesy'">
              <div class="flex justify-center items-center gap-3 mb-4">
                <span class="px-4 py-1.5 bg-vermillion/10 text-vermillion text-sm font-medium rounded-full">
                  {{ recommendList[0].type?.[0] || '诗文' }}
                </span>
                <span class="text-sepia text-sm">精选推荐</span>
              </div>
              <h2 class="text-3xl font-bold text-ink mb-4 font-brush cursor-pointer hover:text-vermillion hover:scale-105 transition-all poem-title"
                @click="toDetail(recommendList[0])">
                {{ recommendList[0].title }}
              </h2>
              <div class="flex justify-center items-center gap-1.5 mb-5">
                <img v-if="writerImgUrl(recommendList[0]) && !writerImgErrors.has(recommendList[0].id!)"
                  :src="writerImgUrl(recommendList[0])" alt=""
                  class="w-[30px] h-[30px] rounded-full object-cover border flex-shrink-0"
                  @error="onWriterImgError(recommendList[0].id!)" />
                <a class="grey text-base font-medium" :href="`/authors/${recommendList[0].writerId}`" target="_blank">
                  {{ recommendList[0].writer }}〔{{ recommendList[0].dynasty }}〕
                </a>
              </div>
              <p class="text-lg text-ink/70 leading-relaxed mb-5 hover:scale-102 transition-all poem-content" style="white-space: pre-wrap;">
                {{ recommendList[0].content }}
              </p>
              <div class="flex items-center pt-4 border-t border-sepia/20">
                <div @click="likeClick(recommendList[0])" class="flex items-center cursor-pointer mr-6"
                  :class="{ 'text-vermillion': likedMap[recommendList[0].id || 0] }">
                  <el-icon size="20px"><Star /></el-icon>
                  <span class="ml-2">{{ likedMap[recommendList[0].id || 0] ? '已收藏' : '收藏' }}</span>
                </div>
                <div @click="copyClick(recommendList[0])" class="flex items-center cursor-pointer text-ink/60 hover:text-vermillion">
                  <el-icon size="18px"><DocumentCopy /></el-icon>
                  <span class="ml-2">复制</span>
                </div>
              </div>
              <div class="mt-4 pt-4 border-t border-sepia/20">
                <a v-for="ele in recommendList[0].type" :key="ele"
                  :href="`/category/${ele}`" target="_blank"
                  class="mr-3 text-sepia text-sm hover:text-vermillion">
                  {{ ele }}
                </a>
              </div>
            </template>
            <template v-if="recommendList[0].reComType === 'Quotes'">
              <div class="text-center mb-4">
                <span class="px-3 py-1 bg-gold/10 text-gold text-sm rounded-full mb-3 inline-block">名句</span>
                <a :href="`/poesy/${recommendList[0].poetryId}`" target="_blank"
                  class="text-2xl font-bold text-ink block hover:text-vermillion transition-colors font-brush">
                  {{ recommendList[0].content }}
                </a>
              </div>
              <div class="text-center text-sepia">
                {{ recommendList[0].poetryName }} ({{ recommendList[0].writer || '佚名' }})
              </div>
            </template>
            <div class="seal absolute bottom-4 right-4">
              <span class="text-xs text-white/80">精选</span>
            </div>
          </div>
          <div class="scroll-rod right"></div>
          <div class="scroll-end right"></div>
        </div>
      </div>

      <div class="columns-1 md:columns-2 lg:columns-3 gap-6 space-y-6">
        <div v-for="(item, index) in recommendList.slice(1)" :key="index"
          class="poem-card rounded-xl p-6 break-inside-avoid"
          :class="item.reComType === 'Quotes' ? 'quotes-card-minimal' : 'poesy-card-minimal'">
          <template v-if="item.reComType === 'Poesy'">
            <h3 class="text-2xl font-bold text-ink mb-2 font-brush cursor-pointer hover:text-vermillion transition-colors text-center"
              @click="toDetail(item)">
              {{ item.title }}
            </h3>
            <div class="flex justify-center items-center gap-1 mb-4">
              <img v-if="writerImgUrl(item) && !writerImgErrors.has(item.id!)"
                :src="writerImgUrl(item)" alt=""
                class="w-[30px] h-[30px] rounded-full object-cover border border-vermillion/20 flex-shrink-0"
                @error="onWriterImgError(item.id!)" />
              <span class="grey text-sm">{{ item.writer }}〔{{ item.dynasty }}〕</span>
            </div>
            <p class="text-base text-ink/90 leading-relaxed line-clamp-6 text-center" style="white-space: pre-wrap;">
              {{ item.content }}
            </p>
            <div class="mt-4 flex justify-center flex-wrap gap-2">
              <a v-for="ele in item.type" :key="ele" :href="`/category/${ele}`" target="_blank"
                class="px-2 py-0.5 bg-jade/10 text-jade text-xs rounded-full hover:bg-jade/20 transition-colors">
                {{ ele }}
              </a>
            </div>
          </template>
          <template v-if="item.reComType === 'Quotes'">
            <a :href="`/poesy/${item.poetryId}`" target="_blank"
              class="text-xl font-bold text-ink block hover:text-vermillion transition-colors font-brush leading-relaxed text-center">
              {{ item.content }}
            </a>
            <div class="mt-4 text-sm text-sepia/80 text-center">
              —— {{ item.poetryName }} &middot; {{ item.writer || '佚名' }}
            </div>
          </template>
          <div class="gradient-divider"></div>
          <div class="flex justify-center items-center pt-3">
            <div @click="likeClick(item)" class="flex items-center cursor-pointer mr-8 text-sm text-sepia/70 hover:text-vermillion"
              :class="{ 'text-vermillion': likedMap[item.id || 0] }">
              <el-icon size="16px"><Star /></el-icon>
              <span class="ml-1">{{ likedMap[item.id || 0] ? '已收藏' : '收藏' }}</span>
            </div>
            <div @click="copyClick(item)" class="flex items-center cursor-pointer text-sepia/70 hover:text-vermillion text-sm">
              <el-icon size="16px"><DocumentCopy /></el-icon>
              <span class="ml-1">复制</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="pt-24 border-t pb-24 border-sepia/20">
      <!-- <CategoryWriter :categories="categories" :writers="writers" :type-limit="30" :writer-limit="30" /> -->
      <CategoryWriter layout="sidebar" :type-limit="0" :writer-limit="0" />
    </div>
  </div>
</template>

<style scoped>
.scroll-container {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 42rem;
}

.scroll-end {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #5c4033 0%, #3d2817 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3), inset 0 1px 2px rgba(255, 255, 255, 0.1);
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}
.scroll-end.left { margin-right: 4px; }
.scroll-end.right { margin-left: 4px; }

.scroll-rod {
  height: 12px;
  width: 8px;
  background: linear-gradient(to bottom, #5c4033 0%, #3d2817 50%, #5c4033 100%);
  flex-shrink: 0;
}
.scroll-rod.left { border-radius: 0 0 6px 6px; margin-right: -2px; }
.scroll-rod.right { border-radius: 0 0 6px 6px; margin-left: -2px; }

.scroll-content {
  flex: 1;
  position: relative;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}
.scroll-content:hover { transform: translateY(-6px); }

.seal {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #c23a2b 0%, #8b2520 100%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(194, 58, 43, 0.4);
  transform: rotate(-5deg);
  opacity: 0.85;
}

.poem-card {
  transition: transform 0.3s ease;
}
.poem-card:hover { transform: translateY(-2px); }

.poem-title { transition: transform 0.3s ease, color 0.3s ease; }
.poem-content { transition: transform 0.3s ease; }

.gradient-divider {
  height: 1px;
  margin-top: 1rem;
  background: linear-gradient(to right, transparent, rgba(139, 119, 101, 0.3) 30%, rgba(139, 119, 101, 0.3) 70%, transparent);
}

.line-clamp-6 {
  display: -webkit-box;
  -webkit-line-clamp: 6;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (max-width: 768px) {
  .scroll-container { flex-wrap: wrap; }
  .scroll-end, .scroll-rod { display: none; }
  .scroll-content { width: 100%; }
}


</style>
