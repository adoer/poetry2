<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCategories } from '../../composables/useCategories'
import { useWriters } from '../../composables/useWriters'

const props = withDefaults(defineProps<{
  layout?: 'grid' | 'sidebar'
  typeLimit?: number
  writerLimit?: number
  categories?: string[]
  writers?: { name: string; id: number }[]
  navigate?: boolean
}>(), {
  layout: 'grid',
  typeLimit: 30,
  writerLimit: 30,
  navigate: true
})

const router = useRouter()

const defaultCategories = useCategories()
const defaultWriters = useWriters()

const cats = computed(() => {
  const source = props.categories && props.categories.length > 0 ? props.categories : defaultCategories
  return props.typeLimit > 0 ? source.slice(0, props.typeLimit) : source
})

const wrs = computed(() => {
  const source = props.writers && props.writers.length > 0 ? props.writers : defaultWriters
  return props.writerLimit > 0 ? source.slice(0, props.writerLimit) : source
})

function goCategory(name: string) {
  router.push({ name: 'CategoryDetail', params: { name } })
}

function goAuthor(id: number) {
  router.push({ name: 'AuthorDetail', params: { id } })
}
</script>

<template>
  <div :class="layout === 'grid' ? 'grid md:grid-cols-2 gap-6' : 'space-y-6'">
    <div class="type-card rounded-xl p-5 relative overflow-hidden">
      <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-vermillion via-vermillion/60 to-transparent"></div>
      <div class="flex items-center gap-3 mb-5">
        <div class="seal-sm">
          <span class="text-white/90 text-xs">分</span>
        </div>
        <div class="text-xl font-bold text-ink font-brush">分类</div>
      </div>
      <div class="flex flex-wrap gap-3">
        <template v-for="(item, index) in cats" :key="index">
          <a v-if="layout === 'grid'"
            class="type-tag" :href="`/category/${item}`" target="_blank">
            <span class="relative z-10">{{ item }}</span>
            <div class="tag-decoration"></div>
          </a>
          <span v-else
            class="type-tag" :class="{ 'cursor-pointer': navigate }"
            @click="navigate && goCategory(item)">
            <span class="relative z-10">{{ item }}</span>
            <div class="tag-decoration"></div>
          </span>
        </template>
      </div>
    </div>
    <div class="writer-card rounded-xl p-5 relative overflow-hidden">
      <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-jade via-jade/60 to-transparent"></div>
      <div class="flex items-center gap-3 mb-5">
        <div class="seal-sm-jade">
          <span class="text-white/90 text-xs">作</span>
        </div>
        <div class="text-xl font-bold text-ink font-brush">名家</div>
      </div>
      <div class="flex flex-wrap gap-3">
        <template v-for="(item, index) in wrs" :key="index">
          <a v-if="layout === 'grid'"
            class="writer-tag" :href="`/authors/${item.id}`" target="_blank">
            <span class="relative z-10">{{ item.name }}</span>
            <div class="tag-decoration"></div>
          </a>
          <span v-else
            class="writer-tag" :class="{ 'cursor-pointer': navigate }"
            @click="navigate && goAuthor(item.id)">
            <span class="relative z-10">{{ item.name }}</span>
            <div class="tag-decoration"></div>
          </span>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.type-card, .writer-card {
  background: linear-gradient(135deg, rgba(245, 240, 230, 0.9) 0%, rgba(255, 255, 255, 0.7) 100%);
  border: 1px solid rgba(139, 115, 85, 0.15);
  box-shadow: 0 2px 12px rgba(30, 30, 30, 0.06), inset 0 0 30px rgba(139, 115, 85, 0.03);
  transition: all 0.3s ease;
}
.type-card:hover, .writer-card:hover { box-shadow: 0 4px 20px rgba(30, 30, 30, 0.1), inset 0 0 30px rgba(139, 115, 85, 0.05); }

.seal-sm {
  width: 24px; height: 24px;
  background: linear-gradient(135deg, #c23a2b 0%, #8b2520 100%);
  border-radius: 3px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 1px 3px rgba(194, 58, 43, 0.3);
  transform: rotate(-3deg);
}

.seal-sm-jade {
  width: 24px; height: 24px;
  background: linear-gradient(135deg, #5a7d6c 0%, #3d5247 100%);
  border-radius: 3px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 1px 3px rgba(90, 125, 108, 0.3);
  transform: rotate(-3deg);
}

.type-tag {
  position: relative;
  padding: 6px 14px;
  font-size: 14px;
  margin: 4px;
  text-align: center;
  background: linear-gradient(135deg, rgba(194, 58, 43, 0.08) 0%, rgba(194, 58, 43, 0.03) 100%);
  border: 1px solid rgba(194, 58, 43, 0.2);
  border-radius: 20px;
  color: #8b7355;
  cursor: pointer;
  transition: all 0.25s ease;
  overflow: hidden;
  text-decoration: none;
}
.type-tag:hover {
  background: linear-gradient(135deg, rgba(194, 58, 43, 0.15) 0%, rgba(194, 58, 43, 0.08) 100%);
  border-color: rgba(194, 58, 43, 0.5);
  color: #c23a2b;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(194, 58, 43, 0.2);
}

.type-tag:hover .tag-decoration,
.writer-tag:hover .tag-decoration {
  opacity: 1;
}

.writer-tag {
  position: relative;
  padding: 6px 14px;
  font-size: 14px;
  margin: 4px;
  text-align: center;
  background: linear-gradient(135deg, rgba(90, 125, 108, 0.08) 0%, rgba(90, 125, 108, 0.03) 100%);
  border: 1px solid rgba(90, 125, 108, 0.2);
  border-radius: 20px;
  color: #8b7355;
  cursor: pointer;
  transition: all 0.25s ease;
  overflow: hidden;
  text-decoration: none;
}
.writer-tag:hover {
  background: linear-gradient(135deg, rgba(90, 125, 108, 0.15) 0%, rgba(90, 125, 108, 0.08) 100%);
  border-color: rgba(90, 125, 108, 0.5);
  color: #5a7d6c;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(90, 125, 108, 0.2);
}

.tag-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, transparent 0%, rgba(194, 58, 43, 0.05) 50%, transparent 100%);
  opacity: 0;
  transition: opacity 0.25s ease;
  pointer-events: none;
}

.writer-tag .tag-decoration {
  background: linear-gradient(135deg, transparent 0%, rgba(90, 125, 108, 0.05) 50%, transparent 100%);
}

.text-ink { color: #1a1a1a; }
.font-brush { font-family: 'Ma Shan Zheng', cursive; }
.cursor-pointer { cursor: pointer; }
</style>
