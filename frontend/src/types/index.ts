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
  bindtel: boolean
  email?: string
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

export interface ApiResult<T> {
  code: number
  message: string
  data: T
}
