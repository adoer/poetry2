<template>
  <canvas ref="canvasRef" class="chinese-canvas" :width="width" :height="height"></canvas>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { getWriters } from '../api'

interface Props {
  width?: number
  height?: number
}

const props = withDefaults(defineProps<Props>(), {
  width: 1920,
  height: 560
})

const FALLBACK_POETS = ['李白','杜甫','白居易','王维','苏轼','李清照','辛弃疾','李商隐','杜牧','陶渊明','曹操','纳兰性德']

const canvasRef = ref<HTMLCanvasElement | null>(null)
let ctx: CanvasRenderingContext2D | null = null
let animationId: number | null = null
let poets: any[] = []
let isVisible = true
let lastTime = 0
let lastSpawnTime = 0
let spawnInterval = 2000
let maxPoets = 60
let animationTime = 0
let poetNames: string[] = []

function createPoet(x: number, y: number) {
  const baseSize = 16 + Math.random() * 20
  const baseOpacity = 0.15 + Math.random() * 0.2
  const speed = 0.15 + Math.random() * 0.1
  const maxLife = 25000 + Math.random() * 5000

  return {
    name: poetNames[Math.floor(Math.random() * poetNames.length)],
    x,
    y,
    vx: 0,
    vy: -speed,
    size: baseSize,
    baseSize,
    opacity: baseOpacity,
    baseOpacity,
    birthTime: animationTime,
    maxLife
  }
}

function initPoets() {
  poets = []
}

function handlePoetryChange() {
  const count = 5 + Math.floor(Math.random() * 6)
  for (let i = 0; i < count; i++) {
    const x = Math.random() * props.width
    const y = props.height + 30 + Math.random() * 50
    poets.push(createPoet(x, y))
  }
}

function animate(timestamp: number = 0) {
  if (!ctx || !canvasRef.value) return

  const now = timestamp
  let dt = lastTime === 0 ? 16 : now - lastTime
  if (dt > 32) dt = 16
  lastTime = now
  animationTime += dt

  ctx.clearRect(0, 0, props.width, props.height)

  if (animationTime - lastSpawnTime > spawnInterval && poets.length < maxPoets) {
    const count = 5 + Math.floor(Math.random() * 6)
    for (let i = 0; i < count; i++) {
      const x = Math.random() * props.width
      const y = props.height + 30 + Math.random() * 50
      poets.push(createPoet(x, y))
    }
    lastSpawnTime = animationTime
    spawnInterval = 1500 + Math.random() * 1000
  }

  poets = poets.filter(p => {
    const age = animationTime - p.birthTime
    if (p.y < -50 || age > p.maxLife) return false
    return true
  })

  ctx.save()
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'

  for (const p of poets) {
    const deltaTime = dt * 0.1
    p.y += p.vy * deltaTime

    const normalizedY = p.y / props.height
    const sizeGrow = 0.4 + normalizedY * 0.6
    p.size = p.baseSize * sizeGrow

    p.opacity = p.baseOpacity * (0.3 + normalizedY * 1.0)
    if (p.opacity > 0.85) p.opacity = 0.85

    ctx.font = `${Math.round(p.size)}px "KaiTi", "楷体", "STKaiti", serif`
    ctx.fillStyle = `rgba(30, 25, 20, ${p.opacity.toFixed(2)})`
    ctx.fillText(p.name, p.x, p.y)
  }

  ctx.restore()

  if (isVisible) {
    animationId = requestAnimationFrame(animate)
  }
}

function startAnimation() {
  if (animationId) cancelAnimationFrame(animationId)
  isVisible = true
  lastTime = 0
  animate()
}

function stopAnimation() {
  isVisible = false
  if (animationId) {
    cancelAnimationFrame(animationId)
    animationId = null
  }
}

function handleVisibilityChange() {
  if (document.hidden) {
    stopAnimation()
  } else {
    startAnimation()
  }
}

onMounted(async () => {
  poetNames = [...FALLBACK_POETS]
  try {
    const res = await getWriters()
    const writers = res.data.data
    if (Array.isArray(writers) && writers.length > 0) {
      poetNames = writers.map((w: any) => w.name)
    }
  } catch {}
  if (canvasRef.value) {
    ctx = canvasRef.value.getContext('2d')
    initPoets()
    handlePoetryChange()
    startAnimation()
    document.addEventListener('visibilitychange', handleVisibilityChange)
  }
})

onUnmounted(() => {
  stopAnimation()
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})

watch(() => [props.width, props.height], () => {
  initPoets()
})
</script>

<style scoped>
.chinese-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}
</style>
