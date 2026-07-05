<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAdminNewsDetail, createNews, updateNews, getCategories } from '../../api'
import type { Category } from '../../types'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const loading = ref(false)
const categories = ref<Category[]>([])

const form = ref({
  title: '',
  content: '',
  summary: '',
  coverImage: '',
  categoryId: undefined as number | undefined,
  status: 'DRAFT' as 'DRAFT' | 'PUBLISHED',
})

async function loadCategories() {
  try {
    const res = await getCategories()
    categories.value = res.data.data
  } catch (e) {
    console.error('Failed to load categories', e)
  }
}

async function loadNews() {
  const id = Number(route.params.id)
  if (isNaN(id)) {
    ElMessage.error('无效的新闻ID')
    router.push({ name: 'AdminNews' })
    return
  }
  try {
    const res = await getAdminNewsDetail(id)
    const item = res.data.data
    form.value = {
      title: item.title,
      content: item.content,
      summary: item.summary,
      coverImage: item.coverImage,
      categoryId: item.categoryId,
      status: item.status,
    }
  } catch (e) {
    console.error('Failed to load news', e)
    ElMessage.error('新闻不存在')
    router.push({ name: 'AdminNews' })
  }
}

onMounted(async () => {
  await loadCategories()
  if (route.params.id) {
    await loadNews()
  }
})

watch(() => route.params.id, (newId) => {
  if (newId) {
    loadNews()
  }
})

function getNumericId(): number {
  const id = Number(route.params.id)
  return isNaN(id) ? 0 : id
}

async function handleSave() {
  if (!form.value.title.trim()) {
    ElMessage.warning('请输入新闻标题')
    return
  }
  if (!form.value.content.trim()) {
    ElMessage.warning('请输入新闻内容')
    return
  }
  if (!form.value.categoryId) {
    ElMessage.warning('请选择新闻分类')
    return
  }

  loading.value = true
  try {
    if (isEdit.value) {
      await updateNews(getNumericId(), form.value)
      ElMessage.success('更新成功')
    } else {
      await createNews(form.value)
      ElMessage.success('发布成功')
    }
    router.push({ name: 'AdminNews' })
  } catch (e) {
    console.error('Failed to save news', e)
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <el-card>
    <h2 style="margin-bottom: 24px">{{ isEdit ? '编辑新闻' : '发布新闻' }}</h2>
    <el-form :model="form" label-width="80px">
      <el-form-item label="标题" required>
        <el-input v-model="form.title" placeholder="新闻标题" />
      </el-form-item>
      <el-form-item label="摘要">
        <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="新闻摘要（可选）" />
      </el-form-item>
      <el-form-item label="内容" required>
        <el-input v-model="form.content" type="textarea" :rows="12" placeholder="新闻正文内容" />
      </el-form-item>
      <el-form-item label="分类" required>
        <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="封面图">
        <el-input v-model="form.coverImage" placeholder="封面图片 URL（可选）" />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio value="DRAFT">草稿</el-radio>
          <el-radio value="PUBLISHED">发布</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSave">保存</el-button>
        <el-button @click="router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
