<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getQuotes } from '../../api'
import type { QuotesItem } from '../../types'

const router = useRouter()
const quotes = ref<QuotesItem[]>([])
const pageNum = ref(1)
const total = ref(4)
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const res = await getQuotes({ pageNum: pageNum.value })
    quotes.value = res.data.data || []
  } catch (e) { console.error('Failed to load quotes', e); quotes.value = [] }
  finally { loading.value = false }
}

function prevPage() { if (pageNum.value > 1) { pageNum.value--; fetchData() } }
function nextPage() { if (pageNum.value < total.value) { pageNum.value++; fetchData() } }

function toDetail(item: QuotesItem) {
  if (item.poetryId) {
    router.push({ name: 'PoesyDetail', params: { id: item.poetryId } })
  }
}

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="flex items-center gap-4 mb-6">
      <div class="w-1 h-8 bg-vermillion rounded"></div>
      <h2 class="text-2xl font-semibold text-ink font-serif">名句</h2>
    </div>
    <div v-if="loading" class="text-center py-16 text-sepia">加载中...</div>
    <div v-else class="columns-1 md:columns-2 lg:columns-3 gap-6 space-y-6">
      <div v-for="(item, index) in quotes" :key="index"
        class="quote-card rounded-xl relative overflow-hidden break-inside-avoid">
        <div class="flower-bg" :class="`flower-${(index % 4) + 1}`"></div>
        <div class="poem-content-wrapper text-center">
          <div @click="toDetail(item)"
            class="text-xl font-brush text-ink hover:text-vermillion transition-colors cursor-pointer inline-block text-left leading-relaxed">
            {{ item.content }}
          </div>
          <div class="mt-4 text-sepia">
            <span>{{ item.writer || '佚名' }}</span>
          </div>
          <div class="divider-ornament"></div>
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
.quote-card {
  position: relative;
  transition: all 0.3s ease;
  overflow: visible;
  padding: 40px 16px 36px;
  background: linear-gradient(180deg,
    rgba(255, 252, 247, 0.98) 0%,
    rgba(250, 246, 240, 0.98) 50%,
    rgba(245, 240, 232, 0.98) 100%
  );
  border: 1px solid rgba(139, 115, 85, 0.2);
  border-radius: 15px 15px 100px 100px / 15px 15px 80px 80px;
  box-shadow:
    0 4px 12px rgba(139, 115, 85, 0.1),
    inset 0 0 40px rgba(255, 252, 245, 0.7),
    inset 0 -20px 30px rgba(210, 180, 140, 0.1);
}
.quote-card::before {
  content: '';
  position: absolute;
  bottom: 6px;
  left: 50%;
  transform: translateX(-50%);
  width: 50%;
  height: 12px;
  background: linear-gradient(90deg,
    transparent 0%,
    rgba(92, 64, 51, 0.25) 20%,
    rgba(92, 64, 51, 0.4) 50%,
    rgba(92, 64, 51, 0.25) 80%,
    transparent 100%
  );
  border-radius: 50%;
  filter: blur(1px);
}
.quote-card::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60%;
  height: 8px;
  background: linear-gradient(90deg,
    rgba(92, 64, 51, 0.3) 0%,
    rgba(92, 64, 51, 0.5) 30%,
    rgba(92, 64, 51, 0.3) 100%
  );
  border-radius: 50%;
}
.quote-card:hover {
  transform: translateY(-2px);
  box-shadow:
    0 6px 16px rgba(139, 115, 85, 0.15),
    inset 0 0 40px rgba(255, 252, 245, 0.7),
    inset 0 -20px 30px rgba(210, 180, 140, 0.15);
}

.flower-bg {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 60px;
  opacity: 0.35;
  pointer-events: none;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
}
.flower-bg.flower-1 {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 80 80'%3E%3Cpath d='M40 75 Q38 55 40 30 Q42 55 40 75' stroke='%235c4033' stroke-width='2' fill='none'/%3E%3Cg fill='%23c23a2b'%3E%3Ccircle cx='40' cy='25' r='6'/%3E%3Ccircle cx='28' cy='32' r='6'/%3E%3Ccircle cx='52' cy='32' r='6'/%3E%3Ccircle cx='22' cy='45' r='6'/%3E%3Ccircle cx='58' cy='45' r='6'/%3E%3C/g%3E%3Cg fill='%23e8554a'%3E%3Ccircle cx='40' cy='25' r='3'/%3E%3Ccircle cx='28' cy='32' r='3'/%3E%3Ccircle cx='52' cy='32' r='3'/%3E%3Ccircle cx='22' cy='45' r='3'/%3E%3Ccircle cx='58' cy='45' r='3'/%3E%3C/g%3E%3Ccircle cx='40' cy='40' r='7' fill='%23f5d060'/%3E%3Ccircle cx='40' cy='40' r='3.5' fill='%23c23a2b'/%3E%3C/svg%3E");
}
.flower-bg.flower-2 {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 80 80'%3E%3Cpath d='M40 70 Q35 50 40 30 Q45 50 40 70' stroke='%23654321' stroke-width='1.5' fill='none'/%3E%3Cpath d='M40 30 Q25 22 20 35 Q30 28 40 35' fill='%23f8bbc4'/%3E%3Cpath d='M40 30 Q30 18 35 25 Q40 18 40 30' fill='%23fcc8d4'/%3E%3Cpath d='M40 30 Q50 18 45 25 Q40 18 40 30' fill='%23fcc8d4'/%3E%3Cpath d='M40 30 Q55 22 60 35 Q50 28 40 35' fill='%23f8bbc4'/%3E%3Ccircle cx='40' cy='38' r='6' fill='%23f5d060'/%3E%3C/svg%3E");
}
.flower-bg.flower-3 {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 80 80'%3E%3Cpath d='M40 70 L40 25' stroke='%235c4033' stroke-width='2' fill='none'/%3E%3Cg fill='%23f5e6a8' stroke='%23d4a84a' stroke-width='0.5'%3E%3Cellipse cx='40' cy='20' rx='12' ry='6' transform='rotate(0 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='12' ry='6' transform='rotate(45 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='12' ry='6' transform='rotate(90 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='12' ry='6' transform='rotate(135 40 25)'/%3E%3C/g%3E%3Cg fill='%23f5d060'%3E%3Cellipse cx='40' cy='20' rx='4' ry='2' transform='rotate(22 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='4' ry='2' transform='rotate(67 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='4' ry='2' transform='rotate(112 40 25)'/%3E%3Cellipse cx='40' cy='20' rx='4' ry='2' transform='rotate(157 40 25)'/%3E%3C/g%3E%3Ccircle cx='40' cy='25' r='3' fill='%23c23a2b'/%3E%3C/svg%3E");
}
.flower-bg.flower-4 {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 80 80'%3E%3Cpath d='M40 70 Q38 50 40 30 Q42 50 40 70' stroke='%235a7d6c' stroke-width='2' fill='none'/%3E%3Cg fill='%238bc4a8' stroke='%235a7d6c' stroke-width='0.5'%3E%3Cellipse cx='40' cy='28' rx='10' ry='10'/%3E%3Cellipse cx='40' cy='28' rx='10' ry='10' transform='rotate(72 40 28)'/%3E%3Cellipse cx='40' cy='28' rx='10' ry='10' transform='rotate(144 40 28)'/%3E%3Cellipse cx='40' cy='28' rx='10' ry='10' transform='rotate(216 40 28)'/%3E%3C/g%3E%3Cg fill='%23f5f0e6' opacity='0.85'%3E%3Cellipse cx='40' cy='28' rx='5' ry='5'/%3E%3Cellipse cx='40' cy='28' rx='5' ry='5' transform='rotate(72 40 28)'/%3E%3Cellipse cx='40' cy='28' rx='5' ry='5' transform='rotate(144 40 28)'/%3E%3Cellipse cx='40' cy='28' rx='5' ry='5' transform='rotate(216 40 28)'/%3E%3C/g%3E%3C/svg%3E");
}

.poem-content-wrapper { padding: 24px; }

.divider-ornament {
  height: 1px;
  margin: 16px auto;
  width: 60%;
  background: linear-gradient(90deg,
    transparent 0%,
    rgba(139, 115, 85, 0.3) 30%,
    rgba(139, 115, 85, 0.5) 50%,
    rgba(139, 115, 85, 0.3) 70%,
    transparent 100%
  );
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
.font-brush { font-family: 'Ma Shan Zheng', cursive; }
.hover\:text-vermillion:hover { color: #c23a2b; }
.bg-vermillion { background: #c23a2b; }
</style>
