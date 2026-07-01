<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../store/user'
import { login } from '../../api'
import type { LoginRequest } from '../../types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const form = ref<LoginRequest>({ username: '', password: '' })
const loading = ref(false)
const errorMsg = ref('')

async function handleLogin() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await login(form.value)
    userStore.setLogin(res.data.data)
    const redirect = (route.query.redirect as string) || '/admin'
    router.push(redirect)
  } catch (e: unknown) {
    errorMsg.value = (e as any).response?.data?.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <el-card class="login-card" shadow="always">
      <h2 style="text-align: center; margin-bottom: 24px">管理员登录</h2>
      <el-form @submit.prevent="handleLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-alert v-if="errorMsg" :title="errorMsg" type="error" show-icon style="margin-bottom: 16px" />
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" native-type="submit">登录</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: center">
        <router-link to="/" style="color: #409eff; font-size: 13px">返回首页</router-link>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.login-page { height: 100vh; display: flex; align-items: center; justify-content: center; background: #f0f2f5; }
.login-card { width: 400px; }
</style>
