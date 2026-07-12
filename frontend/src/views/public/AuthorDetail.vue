<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAuthorDetail } from '../../api'
import type { AuthorItem } from '../../types'
import { ArrowLeft } from '@element-plus/icons-vue'
import CategoryWriter from '../../components/common/CategoryWriter.vue'

const route = useRoute()
const router = useRouter()
const author = ref<AuthorItem | null>(null)
const loading = ref(false)
const imgError = ref(false)

async function fetchData() {
  loading.value = true
  imgError.value = false
  try {
    const res = await getAuthorDetail(Number(route.params.id))
    author.value = res.data.data
  } catch (e) { console.error('Failed to load author', e); author.value = null }
  finally { loading.value = false }
}

function headImgUrl(id: number, url?: string): string {
  if (!url || !url.includes('image_')) return ''
  return `/images/writer/image_${id}.jpg`
}

onMounted(fetchData)
</script>

<template>
  <div class="grid lg:grid-cols-3 gap-8">
    <div class="lg:col-span-2 space-y-6">
      <div class="flex items-center gap-2 cursor-pointer text-sepia hover:text-vermillion transition-colors mb-2" @click="router.back()">
        <el-icon><ArrowLeft /></el-icon>
        <span class="text-sm">返回</span>
      </div>
      <div v-if="loading" class="text-center py-16 text-sepia">加载中...</div>
      <div v-else-if="!author" class="text-center py-16 text-sepia">作者不存在</div>
      <template v-else>
        <div class="detail-card rounded-xl p-6">
          <div class="flex items-start gap-4">
            <div v-if="headImgUrl(author.id, author.headImageUrl) && !imgError" class="flex-shrink-0 rounded-full overflow-hidden"
              style="width: 40px; height: 40px; border: 2px solid rgba(194, 58, 43, 0.2);">
              <img :src="headImgUrl(author.id, author.headImageUrl)" :alt="author.name"
                class="w-full h-full object-cover" @error="imgError = true" />
            </div>
            <div v-else class="flex-shrink-0 flex items-center justify-center rounded-full"
              style="width: 40px; height: 40px; background: linear-gradient(135deg, #F0EFE2 0%, #e8e4d5 100%); border: 2px solid rgba(194, 58, 43, 0.2);">
              <span class="text-vermillion font-bold">{{ author.name?.charAt(0) }}</span>
            </div>
            <div>
              <h2 class="text-2xl font-bold text-ink font-serif mb-2">{{ author.name }}</h2>
              <p class="text-ink leading-relaxed" style="white-space: pre-wrap;">{{ author.simpleIntro }}</p>
            </div>
          </div>
        </div>

        <div class="detail-card rounded-xl p-6">
          <div class="flex items-center gap-4 mb-4">
            <div class="w-1 h-6 bg-vermillion rounded"></div>
            <h3 class="text-xl font-semibold text-ink font-serif">生平</h3>
          </div>
          <p class="text-ink leading-relaxed" style="white-space: pre-wrap;">
            {{ author.detailIntro || '暂无数据' }}
          </p>
        </div>
      </template>
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
.detail-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(26, 26, 26, 0.15);
}

.text-ink { color: #1a1a1a; }
.text-sepia { color: #8b7355; }
.text-vermillion { color: #c23a2b; }
.hover\:text-vermillion:hover { color: #c23a2b; }
.bg-vermillion { background: #c23a2b; }
.font-serif { font-family: 'Noto Serif SC', serif; }
.font-brush { font-family: 'Ma Shan Zheng', cursive; }


</style>
