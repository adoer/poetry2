import axios from 'axios'
import axiosRetry from 'axios-retry'
import type {
  ApiResult, LoginRequest, LoginResponse, SignupRequest,
  PoesyItem, AuthorItem, QuotesItem, FavoriteItem,
  RecommendItem, SearchResult
} from '../types'

let routerInstance: ReturnType<typeof import('vue-router').createRouter> | null = null

export function setRouter(router: ReturnType<typeof import('vue-router').createRouter>) {
  routerInstance = router
}

const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

axiosRetry(http, {
  retries: 0,
  shouldResetTimeout: true,
})

http.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      if (routerInstance) {
        routerInstance.push({ name: 'Login' })
      }
    }
    return Promise.reject(error)
  }
)

// --- Auth ---

export function login(data: LoginRequest) {
  return http.post<ApiResult<LoginResponse>>('/login', data)
}

export function signup(data: SignupRequest) {
  return http.post<ApiResult<LoginResponse>>('/signup', data)
}

export function logout() {
  return http.post<ApiResult<void>>('/logout')
}

export function getCaptcha() {
  return http.get<ApiResult<{ image: string }>>('/captcha')
}

export function sendVerificationEmail(email: string) {
  return http.post<ApiResult<void>>('/send-verification-email', { email })
}

export function verifyEmail(code: string) {
  return http.post<ApiResult<void>>('/verify-email', { code })
}

export function forgotSendCode(email: string) {
  return http.post<ApiResult<void>>('/forgot/send-code', { email })
}

export function forgotReset(data: { email: string; code: string; password: string }) {
  return http.post<ApiResult<void>>('/forgot/reset', data)
}

// --- Poems (poesy) ---

export function getPoesyList(params: { page?: number; size?: number; type?: string; writer?: string; keyword?: string }) {
  return http.get<ApiResult<{ content: PoesyItem[]; totalPages: number; totalElements: number }>>('/poesy/list', { params })
}

export function getPoesyDetail(id: number) {
  return http.get<ApiResult<PoesyItem>>(`/poesy/${id}`)
}

// --- Authors ---

export function getAuthors(params: { page?: number; size?: number; keyword?: string }) {
  return http.get<ApiResult<{ content: AuthorItem[]; totalPages: number; totalElements: number }>>('/authors/list', { params })
}

export function getAuthorDetail(id: number) {
  return http.get<ApiResult<AuthorItem>>(`/authors/${id}`)
}

// --- Quotes ---

export function getQuotes(params: { page?: number; size?: number; keyword?: string }) {
  return http.get<ApiResult<{ content: QuotesItem[]; totalPages: number; totalElements: number }>>('/quotes/list', { params })
}

// --- Search ---

export function searchAll(params: { keyword: string; size?: number }) {
  return http.get<ApiResult<SearchResult>>('/searchAll', { params })
}

// --- Recommend ---

export function getRecommend() {
  return http.get<ApiResult<RecommendItem[]>>('/recommend')
}

// --- Categories (poetry) ---

export function getCategories() {
  return http.get<ApiResult<string[]>>('/category')
}

export function getCategoryDetail(category: string) {
  return http.get<ApiResult<{ id: number; writer: string; title: string }[]>>('/category/detail', { params: { category } })
}

// --- Writer (famous writers from poesy) ---

export function getWriters() {
  return http.get<ApiResult<string[]>>('/writers')
}

// --- Favorites ---

export function getFavorites(params?: { type?: string; page?: number; size?: number }) {
  return http.get<ApiResult<FavoriteItem[]>>('/favorite', { params })
}

export function addFavorite(data: { id: string | number; type: string; title?: string; content?: string; writer?: string }) {
  return http.post<ApiResult<void>>('/favorite', data)
}

export function deleteFavorite(data: { contentId?: string | number; id?: string | number }) {
  return http.delete<ApiResult<void>>('/favorite', { data })
}

// --- User Profile ---

export function updateProfile(data: { nickname?: string; avatar?: string }) {
  return http.put<ApiResult<void>>('/profile', data)
}

export function modifyPassword(data: { passwordOld: string; passwordNew: string }) {
  return http.put<ApiResult<void>>('/profile/password', data)
}
