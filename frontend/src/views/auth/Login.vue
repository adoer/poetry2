<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login, getCaptcha } from '../../api'
import { useUserStore } from '../../store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeName = ref('passLogin')
const username = ref('')
const password = ref('')
const verificationCode = ref('')
const smsCode = ref('')
const captchaImage = ref('')
const loading = ref(false)
const error = ref('')

async function refreshCaptcha() {
  try {
    const res = await getCaptcha()
    captchaImage.value = res.data.data.image
  } catch { error.value = '获取验证码失败' }
}

async function handleLogin() {
  if (!username.value) {
    error.value = '请输入账号'; return
  }
  if (activeName.value === 'passLogin') {
    if (!password.value || !verificationCode.value) {
      error.value = '请填写所有字段'; return
    }
  } else {
    if (!smsCode.value) {
      error.value = '请输入短信验证码'; return
    }
  }
  loading.value = true; error.value = ''
  try {
    const payload = activeName.value === 'passLogin'
      ? { username: username.value, password: password.value, verificationCode: verificationCode.value }
      : { username: username.value, password: smsCode.value, verificationCode: smsCode.value }
    const res = await login(payload)
    userStore.setLogin(res.data.data)
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (e: any) {
    error.value = e.response?.data?.message || '登录失败'
    refreshCaptcha()
  } finally { loading.value = false }
}

onMounted(refreshCaptcha)

function sendSmsCode() {
  if (!username.value) { error.value = '请输入手机号'; return }
  error.value = ''
}
</script>

<template>
  <div class="login-page">
    <div class="login-wrapper justify-center">
      <div class="scroll-decoration left">
        <div class="scroll-end"></div>
        <div class="scroll-rod"></div>
      </div>
      <div class="login-card bg-white/95 backdrop-blur-sm rounded-xl p-8 border border-sepia/20 shadow-lg relative overflow-hidden">
        <div class="ink-blob absolute top-0 right-0 w-32 h-32 opacity-10"></div>
        <div class="relative z-10">
          <div class="flex items-center gap-4 mb-6">
            <div class="w-1 h-8 bg-vermillion rounded"></div>
            <h2 class="text-2xl font-semibold text-ink font-serif">登录</h2>
          </div>
          <div class="flex flex-col md:flex-row gap-8">
            <div class="flex-shrink-0 flex flex-col items-center justify-center" style="width: 200px;">
              <div class="seal mb-4">
                <span class="text-white text-xs">诗</span>
              </div>
              <p class="text-lg font-semibold text-ink mb-4 font-brush">欢迎登录拾光古诗文</p>
            </div>
            <div class="flex-1">
              <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" class="error-alert" />
              <el-tabs v-model="activeName" class="login-tabs mb-4">
                <el-tab-pane label="账号密码登录" name="passLogin"></el-tab-pane>
                <el-tab-pane label="短信登录" name="telLogin"></el-tab-pane>
              </el-tabs>
              <el-form @submit.prevent="handleLogin" label-width="auto" status-icon>
                <el-form-item label="账号">
                  <el-input v-model="username" placeholder="手机号" />
                </el-form-item>
                <el-form-item label="密码" v-show="activeName === 'passLogin'">
                  <el-input v-model="password" type="password" show-password placeholder="请输入密码" />
                </el-form-item>
                <el-form-item label="验证码" v-show="activeName === 'passLogin'">
                  <div class="flex items-center gap-2 w-full">
                    <el-input v-model="verificationCode" placeholder="验证码" maxlength="4" class="flex-1" />
                    <img v-if="captchaImage" :src="captchaImage" class="captcha-img cursor-pointer rounded" @click="refreshCaptcha" />
                  </div>
                </el-form-item>
                <el-form-item label="验证码" v-show="activeName === 'telLogin'">
                  <div class="flex items-center w-full">
                    <el-input v-model="smsCode" placeholder="短信验证码" class="flex-1" />
                    <el-button class="ml-3 code-btn" type="primary" @click="sendSmsCode">获取验证码</el-button>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button :loading="loading" @click="handleLogin" class="login-btn w-full">登录</el-button>
                </el-form-item>
                <el-form-item style="margin-bottom: 0;">
                  <div class="flex items-center justify-center w-full gap-4 text-sm">
                    <router-link to="/forgot" class="grey">忘记密码</router-link>
                    <span class="text-sepia/30">|</span>
                    <router-link to="/signup" class="grey">立即注册</router-link>
                  </div>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </div>
      </div>
      <div class="scroll-decoration right">
        <div class="scroll-rod"></div>
        <div class="scroll-end"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  font-family: 'Noto Serif SC', serif;
}

.login-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.scroll-decoration {
  display: flex;
  align-items: center;
}
.scroll-decoration.left .scroll-end {
  width: 20px; height: 20px; border-radius: 50%;
  background: linear-gradient(135deg, #5c4033 0%, #3d2817 100%);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}
.scroll-decoration.left .scroll-rod {
  width: 4px; height: 8px;
  background: linear-gradient(to bottom, #5c4033 0%, #3d2817 50%, #5c4033 100%);
  margin-right: -2px;
}
.scroll-decoration.right .scroll-end {
  width: 20px; height: 20px; border-radius: 50%;
  background: linear-gradient(135deg, #5c4033 0%, #3d2817 100%);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}
.scroll-decoration.right .scroll-rod {
  width: 4px; height: 8px;
  background: linear-gradient(to bottom, #5c4033 0%, #3d2817 50%, #5c4033 100%);
  margin-left: -2px;
}

.login-card {
  transition: all 0.3s ease;
}
.login-card:hover {
  box-shadow: 0 12px 40px rgba(139, 119, 101, 0.2);
  transform: translateY(-2px);
}

.ink-blob {
  background: radial-gradient(circle at 30% 30%, #c23a2b 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(20px);
}

.seal {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, #c23a2b 0%, #8b2520 100%);
  border-radius: 6px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 3px 10px rgba(194, 58, 43, 0.4);
  transform: rotate(-5deg);
}

.login-btn {
  background: linear-gradient(135deg, #c23a2b 0%, #a83224 100%);
  border: none;
  height: 44px;
  font-size: 16px;
  letter-spacing: 2px;
  color: white;
}
.login-btn:hover { box-shadow: 0 6px 20px rgba(194, 58, 43, 0.4); }

.captcha-img { height: 36px; }

.code-btn {
  background: linear-gradient(135deg, #c23a2b 0%, #a83224 100%);
  border: none;
  color: white;
  white-space: nowrap;
}
.code-btn:hover { box-shadow: 0 4px 12px rgba(194, 58, 43, 0.4); }

.login-tabs :deep(.el-tabs__item) {
  color: #8b7765;
  font-size: 14px;
}
.login-tabs :deep(.el-tabs__item.is-active) {
  color: #c23a2b;
  font-weight: 600;
}
.login-tabs :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, #c23a2b, #a83224);
}
.login-tabs :deep(.el-tabs__nav-wrap::after) {
  background-color: rgba(139, 119, 101, 0.2);
}

:deep(.el-input__wrapper) {
  background: #faf9f5;
  border: 1px solid rgba(139, 119, 101, 0.2);
  box-shadow: none;
}
:deep(.el-input__wrapper:hover),
:deep(.el-input__wrapper.is-focus) {
  border-color: #c23a2b;
}
:deep(.el-form-item__label) { color: #5D6146; }
.error-alert { margin-bottom: 16px; }

@media (max-width: 768px) {
  .login-wrapper { flex-direction: column; }
  .scroll-decoration { display: none; }
  .login-card { padding: 24px; }
}
</style>
