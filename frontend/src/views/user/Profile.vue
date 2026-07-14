<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElCountdown } from 'element-plus'
import { Star, EditPen, Iphone, Message, User, Setting } from '@element-plus/icons-vue'
import { useUserStore } from '../../store/user'
import { modifyPassword, getFavorites, deleteFavorite, sendVerificationEmail, verifyEmail } from '../../api'
import InkBlobCanvas from '../../components/InkBlobCanvas.vue'
import type { FavoriteItem } from '../../types'

const router = useRouter()
const userStore = useUserStore()

const passwordOld = ref('')
const passwordNew = ref('')
const changeUsername = ref(userStore.username)
const loading = ref(false)
const msg = ref('')
const activeName = ref('myFavoriteVal')
const favList = ref<FavoriteItem[]>([])
const favLoading = ref(false)

const iconMap: Record<string, any> = {
  myFavoriteVal: Star,
  changePasswordVal: EditPen,
  phoneNumberVal: Iphone,
  bindEmailVal: Message,
  quitLoginVal: User,
  adminVal: Setting,
}

const leftArr = [
  { name: '我的收藏', value: 'myFavoriteVal' },
  { name: '修改密码', value: 'changePasswordVal' },
  { name: '绑定手机号', value: 'phoneNumberVal' },
  { name: '绑定邮箱', value: 'bindEmailVal' },
  { name: '退出登录', value: 'quitLoginVal' },
]

const bindtel = computed(() => userStore.userInfo?.bindtel)

const leftArrCom = computed(() => {
  const arr = leftArr.map(el => ({ ...el }))
  if (bindtel.value) {
    const idx = arr.findIndex(el => el.name === '绑定手机号')
    if (idx >= 0) arr[idx].name = '修改手机号'
  }
  if (userStore.isAdmin) {
    arr.splice(arr.length - 1, 0, { name: '管理后台', value: 'adminVal' })
  }
  return arr
})

const ruleForm = reactive({
  username: '',
  verificationCode: '',
  newTel: '',
})
const rules = reactive({
  username: [{ required: true, message: '请输入', trigger: 'blur' }],
  newTel: [{ required: true, message: '请输入', trigger: 'blur' }],
  verificationCode: [{ required: true, message: '请输入', trigger: 'blur' }],
})
const countDownValue = ref(0)
const bindEmail = ref('')
const bindEmailCode = ref('')
const emailCountDown = ref(0)
const emailBindDone = ref(false)

function countDownFormatter(number: number) {
  let second: any = number / 1000
  second = second.toFixed()
  return `${second}`
}
function countFinish() { countDownValue.value = 0 }
function emailCountFinish() { emailCountDown.value = 0 }

async function handleSendEmailCode() {
  if (!bindEmail.value) { ElMessage.warning('请输入邮箱'); return }
  emailCountDown.value = Date.now() + 1000 * 60
  try {
    await sendVerificationEmail(bindEmail.value)
    ElMessage.success(`验证码已发送至 ${bindEmail.value}，请查收邮件`)
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '发送失败')
    emailCountDown.value = 0
  }
}

async function handleBindEmail() {
  if (!bindEmail.value || !bindEmailCode.value) { ElMessage.warning('请填写所有字段'); return }
  try {
    await verifyEmail(bindEmailCode.value)
    ElMessage.success('邮箱绑定成功')
    emailBindDone.value = true
    userStore.setLogin({ ...userStore.$state, email: bindEmail.value } as any)
    bindEmail.value = ''
    bindEmailCode.value = ''
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '绑定失败')
  }
}

async function fetchFavorites() {
  favLoading.value = true
  try {
    const res = await getFavorites()
    favList.value = res.data.data || []
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '加载失败')
  }
  finally { favLoading.value = false }
}

async function cancelFavorite(item: FavoriteItem) {
  try {
    const res = await deleteFavorite({ id: item.id })
    if (res.data.code === 200) {
      ElMessage.success('已取消收藏')
      fetchFavorites()
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

async function handleModifyPassword() {
  if (!passwordOld.value || !passwordNew.value) {
    msg.value = '请填写所有字段'; return
  }
  loading.value = true; msg.value = ''
  try {
    await modifyPassword({ passwordOld: passwordOld.value, passwordNew: passwordNew.value })
    msg.value = '修改成功'
    passwordOld.value = ''; passwordNew.value = ''
  } catch (e: any) {
    msg.value = e.response?.data?.message || '修改失败'
  } finally { loading.value = false }
}

async function handleLogout() {
  ElMessageBox.confirm('确认要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    userStore.logout()
    router.push('/')
  }).catch(() => {})
}

function onMenuClick(e: MouseEvent) {
  const target = (e.target as HTMLElement).closest('[data-value]') as HTMLElement
  if (!target) return
  const value = target.dataset.value
  if (!value) return

  if (value === 'quitLoginVal') {
    handleLogout()
    return
  }
  if (value === 'adminVal') {
    window.open('/admin', '_blank')
    return
  }
  if (value === 'bindEmailVal') {
    activeName.value = value
    emailBindDone.value = false
    return
  }
  if (value === 'myFavoriteVal') {
    activeName.value = value
    fetchFavorites()
    return
  }
  activeName.value = value
}

async function getCode() {
  if (!ruleForm.username) { ElMessage.warning('请输入手机号'); return }
  countDownValue.value = Date.now() + 1000 * 60
  ElMessage.success('验证码发送成功')
}

function bindTelClick() {
  ElMessage.success(bindtel.value ? '修改成功' : '绑定成功')
  ruleForm.username = ''
  ruleForm.verificationCode = ''
  ruleForm.newTel = ''
}

onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchFavorites()
  }
})
</script>

<template>
  <div class="max-w-3xl mx-auto py-8">
    <div class="scroll-container flex justify-center mb-10">
      <InkBlobCanvas :width="900" :height="200" class="hidden md:block opacity-20" />
      <div class="scroll-end left"></div>
      <div class="scroll-rod left"></div>
      <div class="scroll-content bg-rice/80 rounded-lg p-8 text-center">
        <div class="flex items-center justify-center gap-4 mb-4">
          <div class="seal">
            <span class="text-xs text-white/90">我的</span>
          </div>
          <h2 class="text-3xl font-bold text-ink font-brush">个人中心</h2>
        </div>
        <p class="text-sepia">诗书相伴，岁月静好</p>
      </div>
      <div class="scroll-rod right"></div>
      <div class="scroll-end right"></div>
    </div>

      <div class="grid md:grid-cols-12 gap-8">
        <div class="md:col-span-4">
          <div class="menu-card rounded-xl p-5">
            <div class="text-center mb-6 pb-4 border-b border-sepia/10">
              <div class="avatar-circle mx-auto mb-3">
                <span class="text-vermillion font-bold text-xl">{{ userStore.username?.charAt(0) || '诗' }}</span>
              </div>
              <p class="text-ink">{{ userStore.username || '诗词爱好者' }}</p>
              <p class="text-ink text-[12px] mt-[5px]" v-if="userStore.userInfo?.email">{{ userStore.userInfo?.email }}</p>
            </div>
            <div class="space-y-2" @click="onMenuClick">
              <div v-for="item in leftArrCom" :key="item.value"
                :data-value="item.value"
                class="menu-item cursor-pointer rounded-lg px-4 py-3 flex items-center gap-3 transition-all"
                :class="{ active: activeName === item.value }">
                <el-icon size="18"><component :is="iconMap[item.value] || Star" /></el-icon>
                <span>{{ item.name }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="md:col-span-8">
          <div class="content-card rounded-xl p-6 min-h-[500px]">
            <div v-show="activeName === 'changePasswordVal'">
              <el-alert v-if="msg" :title="msg" :type="msg === '修改成功' ? 'success' : 'error'" show-icon :closable="false" class="msg-alert" />
              <div class="mt-8" style="width: 300px;">
                <el-form @submit.prevent="handleModifyPassword" label-width="auto" status-icon>
                  <el-form-item label="账号">
                    <el-input v-model="changeUsername" disabled />
                  </el-form-item>
                  <el-form-item label="原密码">
                    <el-input v-model="passwordOld" type="password" show-password />
                  </el-form-item>
                  <el-form-item label="新密码">
                    <el-input v-model="passwordNew" type="password" show-password />
                  </el-form-item>
                  <el-form-item :label="'&nbsp;'">
                    <el-button class="w-full submit-btn" :loading="loading" @click="handleModifyPassword">确定</el-button>
                  </el-form-item>
                </el-form>
              </div>
            </div>

            <div v-show="activeName === 'myFavoriteVal'">
              <div class="myFavorite h-full w-full">
                <div class="item flex justify-between items-center" v-for="(item, index) in favList" :key="index">
                  <div>
                    <div v-if="item.type === 'Quotes'">
                      <a :href="`/poesy/${item.contentId}`" target="_blank">{{ item.content }} — {{ item.writer || '佚名' }}</a>
                    </div>
                    <div v-if="item.type === 'Poesy'">
                      <a :href="`/poesy/${item.contentId}`" target="_blank">《{{ item.title }}》 — {{ item.writer }}</a>
                    </div>
                  </div>
                  <div>
                    <div @click="cancelFavorite(item)" class="flex items-center cursor-pointer" title="取消收藏" style="color: #999999">
                      <el-icon size="18"><Star /></el-icon>
                    </div>
                  </div>
                </div>
                <div class="mt-2 text-center" style="color: #999999;" v-if="favList.length === 0">
                  暂无数据
                </div>
              </div>
            </div>

            <div v-show="activeName === 'bindEmailVal'" class="max-w-md mx-auto mt-4">
              <div class="text-center mb-8">
                <h3 class="text-xl text-ink font-medium">绑定邮箱</h3>
                <p v-if="userStore.userInfo?.email || emailBindDone" class="text-sm text-jade mt-2">邮箱已绑定：{{ userStore.userInfo?.email }}</p>
              </div>
              <el-form label-width="80px" class="profile-form" status-icon v-if="!userStore.userInfo?.email">
                <el-form-item label="邮箱">
                  <el-input v-model="bindEmail" placeholder="请输入邮箱地址" :disabled="!!userStore.userInfo?.email && !emailBindDone" />
                </el-form-item>
                <el-form-item label="验证码">
                  <div class="flex justify-between items-center w-full">
                    <el-input class="flex-1 mr-2" v-model="bindEmailCode" placeholder="请输入验证码" />
                    <el-button v-if="!emailCountDown" @click="handleSendEmailCode" class="code-btn">发送验证码</el-button>
                    <el-countdown v-else class="text-sm flex-shrink-0" @finish="emailCountFinish"
                      title="" suffix="秒" :value="emailCountDown" :formatter="countDownFormatter" />
                  </div>
                </el-form-item>
                <el-form-item class="mt-8">
                  <el-button type="primary" @click="handleBindEmail" class="submit-btn w-full">
                    确认绑定
                  </el-button>
                </el-form-item>
              </el-form>
            </div>

            <div v-show="activeName === 'phoneNumberVal'" class="max-w-md mx-auto mt-4">
              <div class="text-center mb-8">
                <h3 class="text-xl text-ink font-medium">{{ bindtel ? '修改手机号' : '绑定手机号' }}</h3>
              </div>
              <el-form :model="ruleForm" :rules="rules" label-width="80px" class="profile-form" status-icon>
                <el-form-item :label="`${bindtel ? '原号码' : '手机号'}`" prop="username">
                  <el-input v-model="ruleForm.username" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="验证码" prop="verificationCode">
                  <div class="flex justify-between items-center w-full">
                    <el-input class="flex-1 mr-2" v-model="ruleForm.verificationCode" placeholder="请输入验证码" />
                    <el-button v-if="!countDownValue" @click="getCode" class="code-btn">发送验证码</el-button>
                    <el-countdown v-else class="text-sm flex-shrink-0" @finish="countFinish"
                      title="" suffix="秒" :value="countDownValue" :formatter="countDownFormatter" />
                  </div>
                </el-form-item>
                <el-form-item v-if="bindtel" label="新号码" prop="newTel">
                  <el-input v-model="ruleForm.newTel" placeholder="请输入新手机号" />
                </el-form-item>
                <el-form-item class="mt-8">
                  <el-button type="primary" @click="bindTelClick" class="submit-btn w-full">
                    {{ bindtel ? '确认修改' : '立即绑定' }}
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.scroll-container {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 48rem;
  margin: 0 auto;
}

.scroll-end {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: linear-gradient(135deg, #5c4033 0%, #3d2817 100%);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}
.scroll-end.left { margin-right: 4px; }
.scroll-end.right { margin-left: 4px; }

.scroll-rod {
  height: 10px;
  width: 6px;
  background: linear-gradient(to bottom, #5c4033 0%, #3d2817 50%, #5c4033 100%);
  flex-shrink: 0;
}
.scroll-rod.left { border-radius: 0 0 4px 4px; margin-right: -2px; }
.scroll-rod.right { border-radius: 0 0 4px 4px; margin-left: -2px; }

.scroll-content {
  flex: 1;
  position: relative;
}

.seal {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #c23a2b 0%, #8b2520 100%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(194, 58, 43, 0.4);
  transform: rotate(-5deg);
}
.seal.w-10 { width: 40px; }
.seal.h-10 { height: 40px; }

.avatar-circle {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #F0EFE2 0%, #e8e4d5 100%);
  border: 2px solid rgba(194, 58, 43, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-card {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(139, 115, 85, 0.15);
  transition: all 0.3s ease;
}
.menu-card:hover { box-shadow: 0 8px 30px rgba(139, 119, 101, 0.15); }

.menu-item {
  color: #5D6146;
  font-size: 14px;
}
.menu-item:hover {
  background: #F0EFE2;
  transform: translateX(4px);
}
.menu-item.active {
  background: linear-gradient(135deg, #c23a2b 0%, #a83224 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(194, 58, 43, 0.3);
}
.menu-item.active:hover { transform: translateX(0); }

.content-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(139, 115, 85, 0.15);
  transition: all 0.3s ease;
}
.content-card:hover { box-shadow: 0 8px 30px rgba(139, 119, 101, 0.15); }

.submit-btn {
  background: linear-gradient(135deg, #c23a2b 0%, #a83224 100%);
  border: none;
  color: white;
  height: 44px;
  font-size: 16px;
  letter-spacing: 2px;
}
.submit-btn:hover { box-shadow: 0 4px 15px rgba(194, 58, 43, 0.4); }

.code-btn {
  background: transparent;
  border: 1px solid #c23a2b;
  color: #c23a2b;
}
.code-btn:hover {
  background: #c23a2b;
  color: white;
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

.myFavorite {
  padding: 0 10px;
  background-color: #f0efe2;
  border-radius: 4px;
  overflow-y: auto;
}
.myFavorite > .item {
  border-bottom: 1px dashed #DAD9D1;
  padding: 10px 0;
}
.myFavorite > .item:last-child {
  border-bottom: none;
}

.msg-alert { margin-bottom: 16px; }

@media (max-width: 768px) {
  .scroll-container { flex-wrap: wrap; }
  .scroll-end, .scroll-rod { display: none; }
  .scroll-content { width: 100%; }
}
</style>
