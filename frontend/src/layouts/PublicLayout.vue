<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import SearchAll from '../components/SearchAll.vue'
import ChineseCanvas from '../components/ChineseCanvas.vue'

const router = useRouter()
const route = useRoute()
const currentYear = new Date().getFullYear()

function toHome() {
  router.push({ name: 'Home' })
}

function tabChange(name: string) {
  router.push({ name })
}

function isActive(name: string) {
  return route.name === name
}

function isActivePrefix(prefixes: string[]) {
  return prefixes.some(p => (route.name as string)?.startsWith(p))
}
</script>

<template>
  <div class="min-h-screen paper-texture flex flex-col items-center">
    <header class="w-full sticky top-0 z-50 bg-rice-dark border-b border-sepia/20 flex-shrink-0">
      <div class="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between">
        <div class="flex items-center gap-3 cursor-pointer" @click="toHome">
          <div class="w-10 h-10 bg-vermillion rounded flex items-center justify-center text-white font-bold text-xl">诗</div>
          <span class="text-2xl font-semibold text-ink font-serif">拾光古诗文</span>
        </div>
        <nav class="hidden md:flex items-center gap-8">
          <div @click="toHome"
            :class="{ 'text-vermillion font-semibold': isActive('Home') }"
            class="cursor-pointer text-ink/80 hover:text-vermillion transition-colors">
            每日推荐
          </div>
          <div @click="tabChange('PoesyList')"
            :class="{ 'text-vermillion font-semibold': isActive('PoesyList') || isActive('PoesyDetail') || isActivePrefix(['Category']) }"
            class="cursor-pointer text-ink/80 hover:text-vermillion transition-colors">
            诗文
          </div>
          <div @click="tabChange('QuotesList')" :class="{ 'text-vermillion font-semibold': isActive('QuotesList') }"
            class="cursor-pointer text-ink/80 hover:text-vermillion transition-colors">
            名句
          </div>
          <div @click="tabChange('AuthorList')"
            :class="{ 'text-vermillion font-semibold': isActive('AuthorList') || isActive('AuthorDetail') }"
            class="cursor-pointer text-ink/80 hover:text-vermillion transition-colors">
            作者
          </div>
          <div @click="tabChange('Profile')"
            :class="{ 'text-vermillion font-semibold': isActive('Profile') || isActive('Signup') || isActive('ForgotPassword') || isActive('Agreement') || isActive('Favorites') }"
            class="cursor-pointer text-ink/80 hover:text-vermillion transition-colors">
            我的
          </div>
        </nav>
        <div v-if="route.name !== 'Home'">
          <SearchAll :width="260" :height="36" />
        </div>
      </div>
    </header>

    <div v-if="route.name === 'Home'" class="flex-shrink-0 w-full index-head relative flex items-center justify-center" style="height: 560px; overflow: hidden;">
      <ChineseCanvas ref="chineseCanvasRef" :width="1920" :height="560" class="hidden md:block" />
      <div class="relative z-10 w-full max-w-2xl mx-auto px-4 flex flex-col items-center justify-center h-full py-16">
        <h1 class="text-5xl md:text-6xl font-bold text-ink text-center mb-6 font-serif">风雅颂古今</h1>
        <p class="text-xl text-sepia text-center mb-10">品经典诗词，悟人生百态</p>
        <SearchAll :width="600" :height="56" class="home-search" />
      </div>
    </div>

    <main class="flex-1 w-full px-2 xl:!w-[1200px] xl:!px-0 py-8">
      <router-view />
    </main>

    <footer class="footer-section relative w-full flex-shrink-0">
      <div class="footer-decoration top"></div>
      <div class="max-w-6xl mx-auto px-6 py-10">
        <div class="flex flex-col md:flex-row justify-between items-center gap-8">
          <div class="flex items-center gap-4">
            <div class="seal-footer">
              <span class="text-white text-sm">诗</span>
            </div>
            <div>
              <div class="text-xl font-brush text-rice">拾光古诗文</div>
              <div class="text-sm text-rice/60 mt-1">传承经典，弘扬中华文化</div>
            </div>
          </div>
          <div class="divider-vertical hidden md:block"></div>
          <div class="flex flex-col md:flex-row items-center gap-6">
            <div class="flex gap-6 text-sm text-rice/70">
              <a href="#" class="footer-link" @click.prevent>关于我们</a>
              <a href="#" class="footer-link" @click.prevent>联系方式</a>
              <a href="#" class="footer-link" @click.prevent>版权声明</a>
              <a href="#" class="footer-link" @click.prevent>隐私政策</a>
            </div>
          </div>
        </div>
        <div class="footer-line"></div>
        <div class="text-center text-sm text-rice/40 mt-6">
          &copy; {{ currentYear }} 拾光古诗文 &middot; 让诗词走进生活
        </div>
      </div>
      <div class="footer-decoration bottom"></div>
    </footer>
  </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;600;700&family=Ma+Shan+Zheng&display=swap');
</style>

<style scoped>
.paper-texture {
  background-color: #f5f0e6;
  font-family: 'Noto Serif SC', serif;
}

.index-head {
  background: url('/images/headBg.jpg') center center no-repeat;
  background-size: cover;
}

.footer-section {
  background: linear-gradient(180deg, #2a2520 0%, #1a1815 100%);
  border-top: 1px solid rgba(139, 115, 85, 0.25);
  position: relative;
}

.footer-decoration {
  position: absolute;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg,
    transparent 0%,
    rgba(194, 58, 43, 0.3) 20%,
    rgba(90, 125, 108, 0.3) 50%,
    rgba(194, 58, 43, 0.3) 80%,
    transparent 100%
  );
}
.footer-decoration.top { top: 0; }
.footer-decoration.bottom { bottom: 0; }

.seal-footer {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #c23a2b 0%, #8b2520 100%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(194, 58, 43, 0.4);
  transform: rotate(-5deg);
}

.divider-vertical {
  width: 1px;
  height: 40px;
  background: linear-gradient(180deg, transparent, rgba(139, 115, 85, 0.2), transparent);
}

.footer-link {
  position: relative;
  padding: 4px 0;
  color: #b8aea0;
  transition: all 0.25s ease;
}
.footer-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, #c23a2b, transparent);
  transition: all 0.25s ease;
  transform: translateX(-50%);
}
.footer-link:hover {
  color: #f5f0e6;
}
.footer-link:hover::after {
  width: 100%;
}

.footer-line {
  height: 1px;
  margin-top: 24px;
  background: linear-gradient(90deg,
    transparent 0%,
    rgba(139, 115, 85, 0.15) 20%,
    rgba(139, 115, 85, 0.15) 80%,
    transparent 100%
  );
}
</style>
