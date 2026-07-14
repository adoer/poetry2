<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAdminAuthorDetail, createAdminAuthor, updateAdminAuthor } from '../../../api/admin'
import type { AuthorItem } from '../../../types'

const route = useRoute()
const router = useRouter()
const isEdit = !!route.params.id
const loading = ref(false)

const form = ref<Partial<AuthorItem>>({
  name: '',
  dynasty: '',
  simpleIntro: '',
  detailIntro: '',
  headImageUrl: '',
})

onMounted(async () => {
  if (isEdit) {
    loading.value = true
    try {
      const res = await getAdminAuthorDetail(Number(route.params.id))
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
      await updateAdminAuthor(Number(route.params.id), form.value)
      ElMessage.success('更新成功')
    } else {
      await createAdminAuthor(form.value)
      ElMessage.success('创建成功')
    }
    router.push({ name: 'AdminAuthorsList' })
  } finally {
    loading.value = false
  }
}

function onCancel() {
  router.push({ name: 'AdminAuthorsList' })
}
</script>

<template>
  <el-card>
    <template #header>
      <span>{{ isEdit ? '编辑作者' : '新增作者' }}</span>
    </template>

    <el-form :model="form" label-width="120px" v-loading="loading" style="max-width: 600px">
      <el-form-item label="姓名">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="朝代">
        <el-input v-model="form.dynasty" placeholder="如：唐代" />
      </el-form-item>
      <el-form-item label="头像链接">
        <el-input v-model="form.headImageUrl" placeholder="头像URL" />
      </el-form-item>
      <el-form-item label="简介">
        <el-input v-model="form.simpleIntro" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="详细介绍">
        <el-input v-model="form.detailIntro" type="textarea" :rows="6" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">保存</el-button>
        <el-button @click="onCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
