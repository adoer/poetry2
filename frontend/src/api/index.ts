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

export function forgotPassword(data: { email: string; captcha: string }) {
  return http.post<ApiResult<void>>('/forgot', data)
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
  return http.get<ApiResult<{ content: FavoriteItem[]; totalPages: number; totalElements: number }>>('/favorites', { params })
}

export function addFavorite(data: { targetId: number; type: string; [key: string]: any }) {
  return http.post<ApiResult<void>>('/favorites', data)
}

export function deleteFavorite(id: number) {
  return http.delete<ApiResult<void>>(`/favorites/${id}`)
}

// --- User Profile ---

export function updateProfile(data: { nickname?: string; avatar?: string }) {
  return http.put<ApiResult<void>>('/profile', data)
}

export function modifyPassword(data: { passwordOld: string; passwordNew: string }) {
  return http.put<ApiResult<void>>('/profile/password', data)
}
