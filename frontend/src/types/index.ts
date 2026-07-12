export interface PoesyItem {
  id: number
  title: string
  dynasty: string
  writer: string
  writerId?: string
  writerImg?: string
  content: string
  type?: string
  remark?: string
  shangxi?: string
  translation?: string
  audioUrl?: string
}

export interface QuotesItem {
  id: number
  content: string
  poetryId?: string
  poetryName?: string
  writer: string
  writerId?: string
}

export interface AuthorItem {
  id: number
  name: string
  simpleIntro?: string
  detailIntro?: string
  headImageUrl?: string
  dynasty: string
}

export interface FavoriteItem {
  id: number
  contentId: string
  type: string
  content: string
  title: string
  writer: string
  username: string
  createTime: string
}

export interface LoginRequest {
  username: string
  password: string
  verificationCode: string
}

export interface SignupRequest {
  username: string
  password: string
  verificationCode: string
}

export interface LoginResponse {
  token: string
  username: string
  role?: string
  bindwx: boolean
  bindtel: boolean
}

export interface RecommendItem {
  id?: number
  poetryId?: string
  poetryName?: string
  title?: string
  dynasty?: string
  writer: string
  writerId?: string
  writerImg?: string
  content?: string
  type?: string[]
  reComType: 'Poesy' | 'Quotes'
  like?: boolean
}

export interface SearchResult {
  Titles: { id: number; title: string; writer: string; dynasty: string }[]
  Quotes: { id: number; content: string; writer: string; poetryName: string }[]
  Authors: { id: number; name: string; dynasty: string }[]
  Poesy: { id: number; content: string; title: string; writer: string }[]
}

export interface Category {
  id: number
  name: string
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

export interface ApiResult<T> {
  code: number
  message: string
  data: T
}
