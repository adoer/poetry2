<template>
  <canvas ref="canvasRef" class="ink-blob-canvas" :width="width" :height="height"></canvas>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'

interface Props {
  width?: number
  height?: number
}

const props = withDefaults(defineProps<Props>(), {
  width: 800,
  height: 400
})

const canvasRef = ref<HTMLCanvasElement | null>(null)
let ctx: CanvasRenderingContext2D | null = null
let animationId: number | null = null
let blobs: any[] = []
let ripples: any[] = []
let isVisible = true

function createBlob() {
  const radius = Math.random() * 30 + 20
  const blobPoints: number[] = []
  const pointCount = Math.floor(Math.random() * 5) + 8

  for (let i = 0; i < pointCount; i++) {
    blobPoints.push(Math.random() * 0.4 + 0.8)
  }

  return {
    x: Math.random() * props.width * 0.6 + props.width * 0.2,
    y: Math.random() * props.height * 0.6 + props.height * 0.2,
    radius: 0,
    maxRadius: radius,
    opacity: Math.random() * 0.35 + 0.15,
    blobPoints,
    rotation: Math.random() * Math.PI * 2,
    isVermillion: Math.random() < 0.1,
    phase: Math.random() * Math.PI * 2
  }
}

function initBlobs() {
  blobs = []
  const count = Math.min(10, Math.floor(props.width / 80))
  for (let i = 0; i < count; i++) {
    blobs.push(createBlob())
  }
}

function drawBlob(blob: any) {
  if (!ctx) return

  const { x, y, radius, opacity, blobPoints, rotation, isVermillion } = blob
  const pointCount = blobPoints.length

  ctx.save()
  ctx.translate(x, y)
  ctx.rotate(rotation)
  ctx.globalAlpha = opacity

  const gradient = ctx.createRadialGradient(0, 0, 0, 0, 0, radius)

  if (isVermillion) {
    gradient.addColorStop(0, 'rgba(180, 60, 60, 0.9)')
    gradient.addColorStop(0.5, 'rgba(160, 50, 50, 0.6)')
    gradient.addColorStop(1, 'rgba(140, 40, 40, 0.2)')
  } else {
    gradient.addColorStop(0, 'rgba(30, 30, 30, 0.7)')
    gradient.addColorStop(0.5, 'rgba(30, 30, 30, 0.4)')
    gradient.addColorStop(1, 'rgba(30, 30, 30, 0.1)')
  }

  ctx.beginPath()
  for (let i = 0; i <= pointCount; i++) {
    const angle = ((i % pointCount) * Math.PI * 2) / pointCount - Math.PI / 2
    const variance = blobPoints[i % pointCount]
    const px = Math.cos(angle) * radius * variance
    const py = Math.sin(angle) * radius * variance

    if (i === 0) {
      ctx.moveTo(px, py)
    } else {
      const prevAngle = (((i - 1) % pointCount) * Math.PI * 2) / pointCount - Math.PI / 2
      const prevVariance = blobPoints[(i - 1) % pointCount]
      const prevX = Math.cos(prevAngle) * radius * prevVariance
      const prevY = Math.sin(prevAngle) * radius * prevVariance
      const cpX = (prevX + px) / 2
      const cpY = (prevY + py) / 2
      ctx.quadraticCurveTo(prevX, prevY, cpX, cpY)
    }
  }
  ctx.closePath()
  ctx.fillStyle = gradient
  ctx.fill()

  ctx.restore()
}

function drawRipple(ripple: any) {
  if (!ctx) return
  ctx.save()
  ctx.globalAlpha = ripple.opacity
  ctx.beginPath()
  ctx.arc(ripple.x, ripple.y, ripple.radius, 0, Math.PI * 2)
  ctx.strokeStyle = 'rgba(30, 30, 30, 0.3)'
  ctx.lineWidth = 2
  ctx.stroke()
  ctx.restore()
}

function animate() {
  if (!ctx || !canvasRef.value) return

  ctx.clearRect(0, 0, props.width, props.height)

  blobs.forEach((blob, index) => {
    blob.phase += 0.00225
    const sway = Math.sin(blob.phase) * 0.3
    blob.x += sway

    if (blob.radius < blob.maxRadius) {
      blob.radius += 0.0825
    }
    if (blob.opacity > 0.001) {
      blob.opacity -= 0.00019
    }
    blob.rotation += 0.0003

    if (blob.opacity <= 0.001) {
      blobs[index] = createBlob()
    }

    drawBlob(blob)
  })

  ripples.forEach((ripple, index) => {
    ripple.radius += 1
    ripple.opacity -= 0.004
    if (ripple.opacity <= 0) {
      ripples.splice(index, 1)
    } else {
      drawRipple(ripple)
    }
  })

  if (isVisible) {
    animationId = requestAnimationFrame(animate)
  }
}

function startAnimation() {
  if (animationId) cancelAnimationFrame(animationId)
  isVisible = true
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

onMounted(() => {
  if (canvasRef.value) {
    ctx = canvasRef.value.getContext('2d')
    initBlobs()
    startAnimation()
    document.addEventListener('visibilitychange', handleVisibilityChange)
  }
})

onUnmounted(() => {
  stopAnimation()
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})

watch(() => [props.width, props.height], () => {
  initBlobs()
})
</script>

<style scoped>
.ink-blob-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}
</style>
