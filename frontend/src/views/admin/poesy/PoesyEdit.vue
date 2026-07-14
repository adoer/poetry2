<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAdminPoesyDetail, createAdminPoesy, updateAdminPoesy } from '../../../api/admin'
import { getCategories } from '../../../api'
import type { PoesyItem } from '../../../types'

const route = useRoute()
const router = useRouter()
const isEdit = !!route.params.id
const loading = ref(false)
const categories = ref<string[]>([])

const form = ref<Partial<PoesyItem>>({
  title: '',
  dynasty: '',
  writer: '',
  writerId: '',
  writerImg: '',
  content: '',
  type: '',
  remark: '',
  shangxi: '',
  translation: '',
  audioUrl: '',
})

const typeArray = computed({
  get: () => {
    const t = form.value.type
    if (!t) return []
    try {
      return JSON.parse(t) as string[]
    } catch {
      return t ? [t] : []
    }
  },
  set: (val: string[]) => {
    form.value.type = val.length > 0 ? JSON.stringify(val) : ''
  },
})

onMounted(async () => {
  try {
    const res = await getCategories()
    categories.value = res.data.data || []
  } catch { /* ignore */ }

  if (isEdit) {
    loading.value = true
    try {
      const res = await getAdminPoesyDetail(Number(route.params.id))
      form.value = res.data.data
    } finally {
      loading.value = false
    }
  }
})

async function onSubmit() {
  loading.value = true
  try {
    if (isEdit) {
      await updateAdminPoesy(Number(route.params.id), form.value)
      ElMessage.success('更新成功')
    } else {
      await createAdminPoesy(form.value)
      ElMessage.success('创建成功')
    }
    router.push({ name: 'AdminPoesyList' })
  } finally {
    loading.value = false
  }
}

function onCancel() {
  router.push({ name: 'AdminPoesyList' })
}
</script>

<template>
  <el-card>
    <template #header>
      <span>{{ isEdit ? '编辑诗词' : '新增诗词' }}</span>
    </template>

    <el-form :model="form" label-width="100px" v-loading="loading" class="edit-form">
      <el-form-item label="标题">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="朝代">
        <el-input v-model="form.dynasty" placeholder="如：唐代" />
      </el-form-item>
      <el-form-item label="作者">
        <el-input v-model="form.writer" placeholder="作者姓名" />
      </el-form-item>
      <el-form-item label="作者ID">
        <el-input v-model="form.writerId" placeholder="关联作者ID" />
      </el-form-item>
      <el-form-item label="作者头像">
        <el-input v-model="form.writerImg" placeholder="头像URL" />
      </el-form-item>
      <el-form-item label="内容">
        <el-input v-model="form.content" type="textarea" :rows="8" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="typeArray" multiple filterable allow-create clearable placeholder="选择或输入分类" style="width: 100%">
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
      </el-form-item>
      <el-form-item label="赏析">
        <el-input v-model="form.shangxi" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="译文">
        <el-input v-model="form.translation" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="音频链接">
        <el-input v-model="form.audioUrl" placeholder="音频URL" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">保存</el-button>
        <el-button @click="onCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<style scoped>
.edit-form {
  max-width: 800px;
}
</style>
