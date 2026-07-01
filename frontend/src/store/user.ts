import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  let savedUser = {}
  try {
    savedUser = JSON.parse(localStorage.getItem('user') || '{}')
  } catch {}
  const userInfo = ref(savedUser)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  const username = computed(() => userInfo.value?.username || '')

  function setLogin(data: { token: string; username: string; role: string }) {
    token.value = data.token
    const info = { username: data.username, role: data.role }
    userInfo.value = info
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, userInfo, isLoggedIn, isAdmin, username, setLogin, logout }
})
