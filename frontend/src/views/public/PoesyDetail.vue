<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPoesyDetail } from '../../api'
import type { PoesyItem } from '../../types'
import { ArrowLeft } from '@element-plus/icons-vue'
import CategoryWriter from '../../components/common/CategoryWriter.vue'

const route = useRoute()
const router = useRouter()
const poem = ref<PoesyItem | null>(null)
const loading = ref(true)

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getPoesyDetail(Number(route.params.id))
    poem.value = res.data.data
  } catch (e) { console.error('Failed to load poem', e); poem.value = null }
  finally { loading.value = false }
}

onMounted(fetchDetail)
</script>

<template>
  <div class="grid lg:grid-cols-3 gap-8">
    <div class="lg:col-span-2">
      <div v-if="loading" class="text-center py-16 text-sepia">加载中...</div>
      <div v-else-if="!poem" class="text-center py-16 text-sepia">诗词不存在</div>
      <div v-else class="detail-card rounded-xl p-8">
        <div class="flex items-center gap-2 mb-6 cursor-pointer text-sepia hover:text-vermillion transition-colors" @click="router.back()">
          <el-icon><ArrowLeft /></el-icon>
          <span class="text-sm">返回</span>
        </div>
        <div class="flex items-start mb-4">
          <div class="w-1 h-8 bg-vermillion rounded mr-4 flex-shrink-0"></div>
          <h2 class="text-2xl font-bold text-ink font-serif">{{ poem.title }}</h2>
        </div>
        <div class="flex items-center mb-6">
          <span class="grey text-sm">{{ poem.writer }}〔{{ poem.dynasty }}〕</span>
        </div>
        <div class="text-lg leading-loose text-ink/80 mb-8" style="white-space: pre-wrap;">
          {{ poem.content }}
        </div>

        <div class="mt-16">
          <div class="flex items-center gap-4 mb-6">
            <div class="w-1 h-6 bg-vermillion rounded"></div>
            <h3 class="text-xl font-semibold text-ink font-serif">译文及注释</h3>
          </div>
          <div class="mb-6">
            <div class="text-base font-semibold text-ink mb-2">译文</div>
            <p class="text-ink leading-relaxed" style="white-space: pre-wrap;">
              {{ poem.translation || '暂无数据' }}
            </p>
          </div>
          <div class="mb-6">
            <div class="text-base font-semibold text-ink mb-2">注释</div>
            <p class="text-ink leading-relaxed" style="white-space: pre-wrap;">
              {{ poem.remark || '暂无数据' }}
            </p>
          </div>
        </div>

        <div class="mt-16">
          <div class="flex items-center gap-4 mb-6">
            <div class="w-1 h-6 bg-jade rounded"></div>
            <h3 class="text-xl font-semibold text-ink font-serif">赏析</h3>
          </div>
          <p class="text-ink leading-relaxed" style="white-space: pre-wrap;">
            {{ poem.shangxi || '暂无数据' }}
          </p>
        </div>
      </div>
    </div>
    <div class="space-y-6">
      <CategoryWriter layout="sidebar" :type-limit="0" :writer-limit="0" :navigate="false" />
    </div>
  </div>
</template>

<style scoped>
.detail-card {
  background: linear-gradient(135deg, rgba(245, 240, 230, 0.95) 0%, rgba(255, 255, 255, 0.8) 100%);
  border: 1px solid rgba(139, 115, 85, 0.12);
  transition: all 0.3s ease;
}
.detail-card:hover { box-shadow: 0 4px 20px rgba(26, 26, 26, 0.08); }

.text-ink { color: #1a1a1a; }
.text-ink\/80 { color: rgba(26, 26, 26, 0.8); }
.text-sepia { color: #8b7355; }
.grey { color: #65645F; }
.hover\:text-vermillion:hover { color: #c23a2b; }
.bg-vermillion { background: #c23a2b; }
.bg-jade { background: #5a7d6c; }
.font-serif { font-family: 'Noto Serif SC', serif; }
.font-brush { font-family: 'Ma Shan Zheng', cursive; }


</style>
