<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAdminQuotesDetail, createAdminQuotes, updateAdminQuotes } from '../../../api/admin'
import type { QuotesItem } from '../../../types'

const route = useRoute()
const router = useRouter()
const isEdit = !!route.params.id
const loading = ref(false)

const form = ref<Partial<QuotesItem>>({
  content: '',
  poetryId: '',
  poetryName: '',
  writer: '',
  writerId: '',
})

onMounted(async () => {
  if (isEdit) {
    loading.value = true
    try {
      const res = await getAdminQuotesDetail(Number(route.params.id))
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
      await updateAdminQuotes(Number(route.params.id), form.value)
      ElMessage.success('更新成功')
    } else {
      await createAdminQuotes(form.value)
      ElMessage.success('创建成功')
    }
    router.push({ name: 'AdminQuotesList' })
  } finally {
    loading.value = false
  }
}

function onCancel() {
  router.push({ name: 'AdminQuotesList' })
}
</script>

<template>
  <el-card>
    <template #header>
      <span>{{ isEdit ? '编辑名句' : '新增名句' }}</span>
    </template>

    <el-form :model="form" label-width="100px" v-loading="loading" style="max-width: 600px">
      <el-form-item label="内容">
        <el-input v-model="form.content" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="出处诗词ID">
        <el-input v-model="form.poetryId" placeholder="关联诗词ID" />
      </el-form-item>
      <el-form-item label="出处诗词名">
        <el-input v-model="form.poetryName" placeholder="诗词标题" />
      </el-form-item>
      <el-form-item label="作者">
        <el-input v-model="form.writer" placeholder="作者姓名" />
      </el-form-item>
      <el-form-item label="作者ID">
        <el-input v-model="form.writerId" placeholder="关联作者ID" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">保存</el-button>
        <el-button @click="onCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
