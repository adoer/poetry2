<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { forgotSendCode, forgotReset } from '../../api'

const router = useRouter()
const email = ref('')
const code = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')
const codeSent = ref(false)
const codeCountDown = ref(0)

function countDownFormatter(number: number) {
  let second: any = number / 1000
  second = second.toFixed()
  return `${second}`
}
function countFinish() { codeCountDown.value = 0 }

async function handleSendCode() {
  if (!email.value) { error.value = '请输入邮箱'; return }
  error.value = ''
  codeCountDown.value = Date.now() + 1000 * 60
  try {
    await forgotSendCode(email.value)
    ElMessage.success(`验证码已发送至 ${email.value}，请查收邮件`)
    codeSent.value = true
  } catch (e: any) {
    error.value = e.response?.data?.message || '发送失败'
    codeCountDown.value = 0
  }
}

async function handleReset() {
  if (!email.value || !code.value || !password.value) {
    error.value = '请填写所有字段'; return
  }
  loading.value = true; error.value = ''
  try {
    await forgotReset({ email: email.value, code: code.value, password: password.value })
    ElMessage.success('密码重置成功')
    router.push('/login')
  } catch (e: any) {
    error.value = e.response?.data?.message || '重置失败'
  } finally { loading.value = false }
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
              <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" class="error-alert" />
              <el-form @submit.prevent="handleReset" label-width="auto" status-icon>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="email" placeholder="请输入绑定的邮箱" />
                </el-form-item>
                <el-form-item label="验证码" prop="code">
                  <div class="flex items-center gap-2 w-full">
                    <el-input v-model="code" placeholder="验证码" maxlength="6" class="flex-1" />
                    <el-button v-if="!codeCountDown" class="code-btn" @click="handleSendCode">{{ codeSent ? '重新发送' : '发送验证码' }}</el-button>
                    <el-countdown v-else class="text-sm flex-shrink-0" @finish="countFinish"
                      title="" suffix="秒" :value="codeCountDown" :formatter="countDownFormatter" />
                  </div>
                </el-form-item>
                <el-form-item label="新密码" prop="password">
                  <el-input v-model="password" type="password" show-password placeholder="请输入新密码" />
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
.error-alert { margin-bottom: 16px; }

@media (max-width: 768px) {
  .login-wrapper { flex-direction: column; }
  .scroll-decoration { display: none; }
  .forgot-card { padding: 24px; }
}
</style>
