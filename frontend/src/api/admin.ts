import { http } from './index'
import type { ApiResult, PoesyItem, QuotesItem, AuthorItem } from '../types'

export interface AdminUserItem {
  id: string
  username: string
  nickname: string
  phone: string
  email: string
  vipLevel: string
  createTime: string
  lastlogintime: string
  ip: string
}

export interface StatsOverview {
  poesyCount: number
  quotesCount: number
  authorsCount: number
  userCount: number
}

export interface RegistrationTrend {
  daily: Record<string, number>
}

export interface FavoriteDistribution {
  poesy: number
  quotes: number
}

// --- Poems ---

export function getAdminPoesyList(params: { page?: number; size?: number; keyword?: string }) {
  return http.get<ApiResult<{ content: PoesyItem[]; totalPages: number; totalElements: number }>>('/admin/poesy', { params })
}

export function getAdminPoesyDetail(id: number) {
  return http.get<ApiResult<PoesyItem>>(`/admin/poesy/${id}`)
}

export function createAdminPoesy(data: Partial<PoesyItem>) {
  return http.post<ApiResult<PoesyItem>>('/admin/poesy', data)
}

export function updateAdminPoesy(id: number, data: Partial<PoesyItem>) {
  return http.put<ApiResult<PoesyItem>>(`/admin/poesy/${id}`, data)
}

export function deleteAdminPoesy(id: number) {
  return http.delete<ApiResult<void>>(`/admin/poesy/${id}`)
}

// --- Quotes ---

export function getAdminQuotesList(params: { page?: number; size?: number; keyword?: string }) {
  return http.get<ApiResult<{ content: QuotesItem[]; totalPages: number; totalElements: number }>>('/admin/quotes', { params })
}

export function getAdminQuotesDetail(id: number) {
  return http.get<ApiResult<QuotesItem>>(`/admin/quotes/${id}`)
}

export function createAdminQuotes(data: Partial<QuotesItem>) {
  return http.post<ApiResult<QuotesItem>>('/admin/quotes', data)
}

export function updateAdminQuotes(id: number, data: Partial<QuotesItem>) {
  return http.put<ApiResult<QuotesItem>>(`/admin/quotes/${id}`, data)
}

export function deleteAdminQuotes(id: number) {
  return http.delete<ApiResult<void>>(`/admin/quotes/${id}`)
}

// --- Authors ---

export function getAdminAuthorsList(params: { page?: number; size?: number; keyword?: string }) {
  return http.get<ApiResult<{ content: AuthorItem[]; totalPages: number; totalElements: number }>>('/admin/authors', { params })
}

export function getAdminAuthorDetail(id: number) {
  return http.get<ApiResult<AuthorItem>>(`/admin/authors/${id}`)
}

export function createAdminAuthor(data: Partial<AuthorItem>) {
  return http.post<ApiResult<AuthorItem>>('/admin/authors', data)
}

export function updateAdminAuthor(id: number, data: Partial<AuthorItem>) {
  return http.put<ApiResult<AuthorItem>>(`/admin/authors/${id}`, data)
}

export function deleteAdminAuthor(id: number) {
  return http.delete<ApiResult<void>>(`/admin/authors/${id}`)
}

// --- Users ---

export function getAdminUsersList(params: { page?: number; size?: number; keyword?: string }) {
  return http.get<ApiResult<{ content: AdminUserItem[]; totalPages: number; totalElements: number }>>('/admin/users', { params })
}

export function getAdminUserDetail(id: string) {
  return http.get<ApiResult<AdminUserItem>>(`/admin/users/${id}`)
}

// --- Stats ---

export function getAdminStatsOverview() {
  return http.get<ApiResult<StatsOverview>>('/admin/stats/overview')
}

export function getAdminStatsRegistration() {
  return http.get<ApiResult<RegistrationTrend>>('/admin/stats/registration')
}

export function getAdminStatsFavorites() {
  return http.get<ApiResult<FavoriteDistribution>>('/admin/stats/favorites')
}
