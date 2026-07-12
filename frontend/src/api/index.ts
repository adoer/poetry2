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

axiosRetry(http, { retries: 3, retryDelay: axiosRetry.exponentialDelay })

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      if (routerInstance) {
        routerInstance.push('/login')
      } else {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  },
)

export function getCaptcha() {
  return http.get<ApiResult<{ image: string }>>('/captcha')
}

export function login(data: LoginRequest) {
  return http.post<ApiResult<LoginResponse>>('/login', data)
}

export function signup(data: SignupRequest) {
  return http.post<ApiResult<LoginResponse>>('/signup', data)
}

export function logout() {
  return http.post<ApiResult<null>>('/logout')
}

export function modifyPassword(data: { passwordOld: string; passwordNew: string }) {
  return http.put<ApiResult<null>>('/modifypassword', data)
}

export function getPoesy(params: { keyword?: string; pageNum?: number; id?: number }) {
  return http.get<ApiResult<{ pageNum: number; list: PoesyItem[] } | PoesyItem>>('/poesy', { params })
}

export function getAuthors(params: { id?: number; pageNum?: number }) {
  return http.get<ApiResult<AuthorItem[] | AuthorItem>>('/authors', { params })
}

export function getWriters() {
  return http.get<ApiResult<{ name: string; id: number }[]>>('/writers')
}

export function getQuotes(params: { pageNum?: number }) {
  return http.get<ApiResult<QuotesItem[]>>('/quotes', { params })
}

export function getCategories() {
  return http.get<ApiResult<string[]>>('/category')
}

export function getCategoryDetail(category: string) {
  return http.get<ApiResult<{ id: number; writer: string; title: string }[]>>('/category/detail', { params: { category } })
}

export function searchAll(keyword: string) {
  return http.get<ApiResult<SearchResult>>('/searchAll', { params: { keyword } })
}

export function getRecommend() {
  return http.get<ApiResult<RecommendItem[]>>('/recommend')
}

export function getFavorites() {
  return http.get<ApiResult<FavoriteItem[]>>('/favorite')
}
export function addFavorite(data: { id: string; type: string; title: string; content: string; writer: string }) {
  return http.post<ApiResult<boolean>>('/favorite', data)
}
export function deleteFavorite(data: { contentId?: string; id?: string }) {
  return http.delete<ApiResult<boolean>>('/favorite', { data })
}


