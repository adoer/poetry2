<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../store/user'
import { getPoesy, addFavorite, deleteFavorite } from '../../api'
import CategoryWriter from '../../components/common/CategoryWriter.vue'
import type { PoesyItem } from '../../types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const poemList = ref<PoesyItem[]>([])
const pageNum = ref(1)
const total = ref(4)
const loading = ref(false)
const mode = ref<'all' | 'tuijian'>('all')
const expandedIndex = ref<number | null>(null)
const likedMap = ref<Record<number, boolean>>({})

async function fetchData() {
  loading.value = true
  try {
    const keyword = (route.query.q as string) || mode.value
    const res = await getPoesy({ keyword, pageNum: pageNum.value })
    const data = res.data.data
    if (data && typeof data === 'object' && 'list' in data) {
      const list = data.list as PoesyItem[]
      poemList.value = list
      list.forEach((item: any) => { likedMap.value[item.id] = item.like || false })
      if ('total' in data) total.value = data.total as number
    }
  } catch (e) { console.error('Failed to load poems', e); poemList.value = [] }
  finally { loading.value = false }
}

function changeMode(m: 'all' | 'tuijian') {
  mode.value = m; pageNum.value = 1
  router.replace({ query: {} })
  fetchData()
}

function prevPage() { if (pageNum.value > 1) { pageNum.value--; fetchData() } }
function nextPage() { if (pageNum.value < total.value) { pageNum.value++; fetchData() } }

function toDetail(item: PoesyItem) {
  router.push({ name: 'PoesyDetail', params: { id: item.id } })
}

function copyClick(item: PoesyItem) {
  navigator.clipboard.writeText(item.content)
}

async function likeClick(item: PoesyItem) {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  const isLiked = likedMap.value[item.id]
  try {
    if (!isLiked) {
      const res = await addFavorite({
        id: String(item.id),
        type: 'Poesy',
        title: item.title,
        content: item.content,
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

onMounted(fetchData)
</script>

<template>
  <div class="grid lg:grid-cols-3 gap-8">
    <div class="lg:col-span-2 space-y-8">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-4">
          <div class="w-1 h-8 bg-vermillion rounded"></div>
          <h2 class="text-2xl font-semibold text-ink font-serif">诗文</h2>
        </div>
        <div class="flex gap-2">
          <span :class="['px-4 py-1.5 text-sm rounded-full cursor-pointer transition-all', mode === 'all' ? 'bg-vermillion text-white' : 'text-sepia bg-sepia/10']" @click="changeMode('all')">全部</span>
          <span :class="['px-4 py-1.5 text-sm rounded-full cursor-pointer transition-all', mode === 'tuijian' ? 'bg-vermillion text-white' : 'text-sepia bg-sepia/10']" @click="changeMode('tuijian')">推荐</span>
        </div>
      </div>

      <div v-if="loading" class="text-center py-16 text-sepia">加载中...</div>
      <template v-else>
        <div v-for="(item, index) in poemList" :key="item.id"
          class="poem-card rounded-xl relative overflow-hidden">
          <div class="flower-bg" :class="`flower-${(index % 4) + 1}`"></div>
          <div class="poem-content-wrapper">
            <div class="text-center mb-3">
              <h3 class="text-2xl font-bold text-ink font-brush cursor-pointer hover:text-vermillion transition-colors"
                @click="toDetail(item)">
                {{ item.title }}
              </h3>
            </div>
            <div class="flex items-center justify-center mb-5">
              <span class="text-sepia cursor-pointer hover:text-vermillion transition-colors"
                @click="router.push({ name: 'AuthorDetail', params: { id: item.writerId || item.writer } })">
                {{ item.writer }}
              </span>
              <span class="mx-2 text-sepia/40">·</span>
              <span class="dynasty-tag">{{ item.dynasty }}</span>
            </div>
            <div class="poem-text flex justify-center" :class="{ 'expanded': expandedIndex === index }">
              <div class="inline-block text-center"
                :class="expandedIndex !== index && item.content.length > 150 ? 'line-clamp-4' : ''">
                <p class="text-ink/80 leading-loose" style="white-space: pre-wrap;">
                  {{ item.content }}
                </p>
              </div>
            </div>
            <div v-if="item.content.length > 150" class="mt-3 text-center">
              <span @click="expandedIndex = expandedIndex === index ? null : index" class="expand-btn">
                {{ expandedIndex === index ? '▲ 收起' : '▼ 阅读全文' }}
              </span>
            </div>

            <div class="action-bar justify-center">
              <div @click="likeClick(item)" class="action-btn"
                :class="{ 'text-vermillion': likedMap[item.id] }" :title="likedMap[item.id] ? '取消收藏' : '收藏'">
                <el-icon size="18px"><Star /></el-icon>
                <span class="ml-1 text-sm">{{ likedMap[item.id] ? '已收藏' : '收藏' }}</span>
              </div>
              <div class="action-btn text-sepia hover:text-vermillion" title="复制" @click="copyClick(item)">
                <el-icon size="17px"><DocumentCopy /></el-icon>
                <span class="ml-1 text-sm">复制</span>
              </div>
            </div>
          </div>
        </div>

        <div class="flex justify-between">
          <div @click="nextPage" class="pagenumber flex-1 mr-2"
            :class="{ 'disabled': pageNum === total }">
            下一页
          </div>
          <div @click="prevPage" class="pagenumber w-24 text-center"
            :class="{ 'disabled': pageNum === 1 }">
            上一页
          </div>
        </div>
      </template>
    </div>
    <div class="space-y-6">
      <CategoryWriter layout="sidebar" :type-limit="0" :writer-limit="0" :navigate="false" />
    </div>
  </div>
</template>

<style scoped>
.poem-card {
  position: relative;
  background: linear-gradient(135deg, rgba(245, 240, 230, 0.95) 0%, rgba(255, 255, 255, 0.8) 100%);
  border: 1px solid rgba(139, 115, 85, 0.12);
  transition: all 0.3s ease;
  overflow: hidden;
}
.poem-card:hover { transform: translateY(-2px); }

.flower-bg {
  position: absolute;
  top: 0;
  right: 0;
  width: 120px;
  height: 120px;
  opacity: 0.2;
  pointer-events: none;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: top right;
}
.flower-bg.flower-1 {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 80 80'%3E%3Cpath d='M40 75 Q38 55 40 30 Q42 55 40 75' stroke='%235c4033' stroke-width='2' fill='none'/%3E%3Cg fill='%23c23a2b'%3E%3Ccircle cx='40' cy='25' r='6'/%3E%3Ccircle cx='28' cy='32' r='6'/%3E%3Ccircle cx='52' cy='32' r='6'/%3E%3Ccircle cx='22' cy='45' r='6'/%3E%3Ccircle cx='58' cy='45' r='6'/%3E%3C/g%3E%3Cg fill='%23e8554a'%3E%3Ccircle cx='40' cy='25' r='3'/%3E%3Ccircle cx='28' cy='32' r='3'/%3E%3Ccircle cx='52' cy='32' r='3'/%3E%3Ccircle cx='22' cy='45' r='3'/%3E%3Ccircle cx='58' cy='45' r='3'/%3E%3C/g%3E%3Ccircle cx='40' cy='40' r='7' fill='%23f5d060'/%3E%3Ccircle cx='40' cy='40' r='3.5' fill='%23c23a2b'/%3E%3Ccircle cx='38.5' cy='38.5' r='1.2' fill='%23f5e6a8'/%3E%3Cpath d='M35 25 L40 15 L45 25' stroke='%235c4033' stroke-width='1' fill='none'/%3E%3C/svg%3E");
}
.flower-bg.flower-2 {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 80 80'%3E%3Cpath d='M40 70 Q35 50 40 30 Q45 50 40 70' stroke='%23654321' stroke-width='1.5' fill='none'/%3E%3Cpath d='M40 30 Q25 22 20 35 Q30 28 40 35' fill='%23f8bbc4'/%3E%3Cpath d='M40 30 Q30 18 35 25 Q40 18 40 30' fill='%23fcc8d4'/%3E%3Cpath d='M40 30 Q50 18 45 25 Q40 18 40 30' fill='%23fcc8d4'/%3E%3Cpath d='M40 30 Q55 22 60 35 Q50 28 40 35' fill='%23f8bbc4'/%3E%3Ccircle cx='40' cy='38' r='6' fill='%23f5d060'/%3E%3Cpath d='M35 36 Q40 42 45 36' stroke='%23d4726a' stroke-width='1' fill='none'/%3E%3Ccircle cx='18' cy='40' r='3' fill='%23e8a0b8' opacity='0.5'/%3E%3Ccircle cx='62' cy='40' r='3' fill='%23e8a0b8' opacity='0.5'/%3E%3C/svg%3E");
}
.flower-bg.flower-3 {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 80 80'%3E%3Cpath d='M40 70 L40 25' stroke='%235c4033' stroke-width='2' fill='none'/%3E%3Cg fill='%23f5e6a8' stroke='%23d4a84a' stroke-width='0.5'%3E%3Cellipse cx='40' cy='20' rx='12' ry='6' transform='rotate(0 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='12' ry='6' transform='rotate(40 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='12' ry='6' transform='rotate(80 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='12' ry='6' transform='rotate(120 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='12' ry='6' transform='rotate(160 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='12' ry='6' transform='rotate(200 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='12' ry='6' transform='rotate(240 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='12' ry='6' transform='rotate(280 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='12' ry='6' transform='rotate(320 40 25)'/%3E%3C/g%3E%3Cg fill='%23f5d060'%3E%3Cellipse cx='40' cy='20' rx='4' ry='2.5' transform='rotate(20 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='4' ry='2.5' transform='rotate(60 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='4' ry='2.5' transform='rotate(100 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='4' ry='2.5' transform='rotate(140 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='4' ry='2.5' transform='rotate(180 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='4' ry='2.5' transform='rotate(220 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='4' ry='2.5' transform='rotate(260 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='4' ry='2.5' transform='rotate(300 40 25)'/%3E%3C/g%3E%3Ccircle cx='40' cy='25' r='3' fill='%23c23a2b'/%3E%3Ccircle cx='38.5' cy='23.5' r='1' fill='%23f5e6a8'/%3E%3C/svg%3E");
}
.flower-bg.flower-4 {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 80 80'%3E%3Cpath d='M40 70 Q38 50 40 30 Q42 50 40 70' stroke='%235a7d6c' stroke-width='2' fill='none'/%3E%3Cg fill='%238bc4a8' stroke='%235a7d6c' stroke-width='0.5'%3E%3Cellipse cx='40' cy='28' rx='10' ry='10'/%3E%3Cellipse cx='40' cy='28' rx='10' ry='10' transform='rotate(72 40 28)'/%3E%3Cellipse cx='40' cy='28' rx='10' ry='10' transform='rotate(144 40 28)'/%3E%3Cellipse cx='40' cy='28' rx='10' ry='10' transform='rotate(216 40 28)'/%3E%3Cellipse cx='40' cy='28' rx='10' ry='10' transform='rotate(288 40 28)'/%3E%3C/g%3E%3Cg fill='%23f5f0e6' opacity='0.85'%3E%3Cellipse cx='40' cy='28' rx='5' ry='5'/%3E%3Cellipse cx='40' cy='28' rx='5' ry='5' transform='rotate(72 40 28)'/%3E%3Cellipse cx='40' cy='28' rx='5' ry='5' transform='rotate(144 40 28)'/%3E%3Cellipse cx='40' cy='28' rx='5' ry='5' transform='rotate(216 40 28)'/%3E%3Cellipse cx='40' cy='28' rx='5' ry='5' transform='rotate(288 40 28)'/%3E%3C/g%3E%3Cpath d='M40 36 L40 44' stroke='%235a7d6c' stroke-width='1.5'/%3E%3Ccircle cx='40' cy='48' r='3' fill='%233d5247'/%3E%3Cpath d='M30 58 Q35 65 40 60 Q45 65 50 58' stroke='%23a8d4b8' stroke-width='1.5' fill='none' opacity='0.3'/%3E%3Cpath d='M25 65 Q32 72 40 68 Q48 72 55 65' stroke='%23a8d4b8' stroke-width='1' fill='none' opacity='0.2'/%3E%3C/svg%3E");
}

.dynasty-tag {
  padding: 2px 10px;
  font-size: 12px;
  background: linear-gradient(135deg, rgba(139, 115, 85, 0.15) 0%, rgba(139, 115, 85, 0.08) 100%);
  border-radius: 3px;
  color: #8b7355;
}

.poem-content-wrapper { padding: 24px; }

.poem-text { transition: all 0.3s ease; }

.line-clamp-4 {
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.expand-btn {
  display: inline-block;
  padding: 6px 16px;
  font-size: 13px;
  color: #8b7355;
  background: rgba(139, 115, 85, 0.1);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.expand-btn:hover {
  background: rgba(194, 58, 43, 0.1);
  color: #c23a2b;
}

.action-bar {
  display: flex;
  gap: 20px;
  padding-top: 20px;
  margin-top: 20px;
  border-top: 1px solid rgba(139, 115, 85, 0.1);
}

.action-btn {
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s ease;
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
.text-ink\/80 { color: rgba(26, 26, 26, 0.8); }
.text-sepia { color: #8b7355; }
.text-sepia\/40 { color: rgba(139, 115, 85, 0.4); }
.font-brush { font-family: 'Ma Shan Zheng', cursive; }
.hover\:text-vermillion:hover { color: #c23a2b; }
.bg-vermillion { background: #c23a2b; }


</style>
