import axios from 'axios'
import axiosRetry from 'axios-retry'
import type { ApiResult, Category, NewsItem, PageResult, LoginRequest, LoginResponse } from '../types'

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

export function login(data: LoginRequest) {
  return http.post<ApiResult<LoginResponse>>('/auth/login', data)
}

export function logout() {
  return http.post<ApiResult<null>>('/auth/logout')
}

export function getCategories() {
  return http.get<ApiResult<Category[]>>('/categories')
}

export function getNews(params: { categoryId?: number; keyword?: string; page?: number; size?: number }) {
  return http.get<ApiResult<PageResult<NewsItem>>>('/news', { params })
}

export function getNewsDetail(id: number) {
  return http.get<ApiResult<NewsItem>>(`/news/${id}`)
}

export function getAdminNews(params: { keyword?: string; page?: number; size?: number }) {
  return http.get<ApiResult<PageResult<NewsItem>>>('/admin/news', { params })
}

export function createNews(data: Partial<NewsItem>) {
  return http.post<ApiResult<NewsItem>>('/admin/news', data)
}

export function getAdminNewsDetail(id: number) {
  return http.get<ApiResult<NewsItem>>('/admin/news/' + id)
}

export function updateNews(id: number, data: Partial<NewsItem>) {
  return http.put<ApiResult<NewsItem>>(`/admin/news/${id}`, data)
}

export function deleteNews(id: number) {
  return http.delete<ApiResult<null>>(`/admin/news/${id}`)
}

export function createCategory(name: string) {
  return http.post<ApiResult<Category>>('/admin/categories', { name })
}

export function updateCategory(id: number, name: string) {
  return http.put<ApiResult<Category>>(`/admin/categories/${id}`, { name })
}

export function deleteCategory(id: number) {
  return http.delete<ApiResult<null>>(`/admin/categories/${id}`)
}
