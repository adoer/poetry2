import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

interface UserInfo {
  username: string
  role?: string
  bindwx?: boolean
  bindtel?: boolean
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  let savedUser: UserInfo | null = null
  try {
    const parsed = JSON.parse(localStorage.getItem('user') || 'null')
    if (parsed && typeof parsed === 'object' && 'username' in parsed) {
      savedUser = parsed as UserInfo
    }
  } catch {}
  const userInfo = ref<UserInfo | null>(savedUser)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  const username = computed(() => userInfo.value?.username || '')

  function setLogin(data: { token: string; username: string; role?: string; bindwx?: boolean; bindtel?: boolean }) {
    token.value = data.token
    const info: UserInfo = { username: data.username, role: data.role, bindwx: data.bindwx, bindtel: data.bindtel }
    userInfo.value = info
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, userInfo, isLoggedIn, isAdmin, username, setLogin, logout }
})
