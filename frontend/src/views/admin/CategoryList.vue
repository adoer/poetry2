<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCategories, createCategory, updateCategory, deleteCategory } from '../../api'
import type { Category } from '../../types'

const categories = ref<Category[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const editingId = ref<number | null>(null)
const formName = ref('')
const loading = ref(false)

async function fetchData() {
  try {
    const res = await getCategories()
    categories.value = res.data.data
  } catch (e) {
    console.error('Failed to fetch categories', e)
  }
}

function openCreate() {
  dialogTitle.value = '新增分类'
  editingId.value = null
  formName.value = ''
  dialogVisible.value = true
}

function openEdit(cat: Category) {
  dialogTitle.value = '编辑分类'
  editingId.value = cat.id
  formName.value = cat.name
  dialogVisible.value = true
}

async function handleSave() {
  if (!formName.value.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  loading.value = true
  try {
    if (editingId.value) {
      await updateCategory(editingId.value, formName.value)
      ElMessage.success('更新成功')
    } else {
      await createCategory(formName.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await fetchData()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  } finally {
    loading.value = false
  }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该分类吗？', '提示')
    await deleteCategory(id)
    ElMessage.success('删除成功')
    await fetchData()
  } catch (e: any) {
    if (e === 'cancel') return
    console.error('Failed to delete category', e)
    ElMessage.error(e.response?.data?.message || '删除失败')
  }
}

onMounted(fetchData)
</script>

<template>
  <el-card>
    <div style="display: flex; justify-content: space-between; margin-bottom: 16px">
      <h2>分类管理</h2>
      <el-button type="primary" @click="openCreate">新增分类</el-button>
    </div>
    <el-table :data="categories" stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px">
      <el-form @submit.prevent>
        <el-form-item label="名称">
          <el-input v-model="formName" placeholder="分类名称" @keyup.enter="handleSave" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>
