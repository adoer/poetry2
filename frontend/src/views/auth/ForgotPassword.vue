<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const verificationCode = ref('')
const loading = ref(false)

function handleSubmit() {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    router.push('/login')
  }, 1000)
}
</script>

<template>
  <div class="forgot-page">
    <div class="login-wrapper justify-center">
      <div class="scroll-decoration left">
        <div class="scroll-end"></div>
        <div class="scroll-rod"></div>
      </div>
      <div class="forgot-card bg-white/95 backdrop-blur-sm rounded-xl p-8 border border-sepia/20 shadow-lg relative overflow-hidden">
        <div class="ink-blob absolute top-0 right-0 w-32 h-32 opacity-10"></div>
        <div class="relative z-10">
          <div class="flex items-center gap-4 mb-6">
            <div class="w-1 h-8 bg-vermillion rounded"></div>
            <h2 class="text-2xl font-semibold text-ink">忘记密码</h2>
          </div>
          <div class="flex flex-col md:flex-row gap-8">
            <div class="flex-shrink-0 flex flex-col items-center justify-center" style="width: 200px;">
              <div class="seal mb-4">
                <span class="text-white text-xs">密</span>
              </div>
              <p class="text-lg font-semibold text-ink mb-4 font-brush">重置密码</p>
            </div>
            <div class="flex-1">
              <el-form @submit.prevent="handleSubmit" label-width="auto" status-icon>
                <el-form-item label="账号" prop="username">
                  <el-input v-model="username" placeholder="手机号" />
                </el-form-item>
                <el-form-item label="新密码" prop="password">
                  <el-input v-model="password" type="password" show-password placeholder="请输入新密码" />
                </el-form-item>
                <el-form-item label="验证码" prop="verificationCode">
                  <div class="flex items-center gap-2 w-full">
                    <el-input v-model="verificationCode" placeholder="验证码" maxlength="4" class="flex-1" />
                    <el-button class="code-btn">发送验证码</el-button>
                  </div>
                </el-form-item>
                <el-form-item>
                  <div class="w-full flex justify-between items-center">
                    <el-button :loading="loading" native-type="submit" class="login-btn flex-1">确定</el-button>
                    <router-link to="/login" class="text-sm text-sepia ml-3 hover:text-vermillion transition-colors">已有账号 ></router-link>
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
.forgot-page {
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

.forgot-card {
  transition: all 0.3s ease;
}
.forgot-card:hover {
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

.code-btn {
  background: linear-gradient(135deg, #c23a2b 0%, #a83224 100%);
  border: none;
  color: white;
  flex-shrink: 0;
}
.code-btn:hover { box-shadow: 0 4px 12px rgba(194, 58, 43, 0.4); }

.login-btn {
  background: linear-gradient(135deg, #c23a2b 0%, #a83224 100%);
  border: none;
  height: 44px;
  font-size: 16px;
  letter-spacing: 2px;
  color: white;
}
.login-btn:hover { box-shadow: 0 6px 20px rgba(194, 58, 43, 0.4); }

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

@media (max-width: 768px) {
  .login-wrapper { flex-direction: column; }
  .scroll-decoration { display: none; }
  .forgot-card { padding: 24px; }
}
</style>
