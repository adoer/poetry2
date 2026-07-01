export interface Category {
  id: number
  name: string
  createdAt: string
  updatedAt: string
}

export interface NewsItem {
  id: number
  title: string
  content: string
  summary: string
  coverImage: string
  categoryId: number
  categoryName: string
  status: 'DRAFT' | 'PUBLISHED'
  createdAt: string
  updatedAt: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  username: string
  role: string
}

export interface PageResult<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface ApiResult<T> {
  code: number
  message: string
  data: T
}
