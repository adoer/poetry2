import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { setRouter } from '../api'

function safeJsonParse<T>(str: string | null, fallback: T): T {
  if (!str) return fallback
  try {
    return JSON.parse(str) as T
  } catch {
    return fallback
  }
}

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('../layouts/PublicLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/public/Home.vue') },
      { path: 'poesy', name: 'PoesyList', component: () => import('../views/public/PoesyList.vue') },
      { path: 'poesy/:id', name: 'PoesyDetail', component: () => import('../views/public/PoesyDetail.vue') },
      { path: 'authors', name: 'AuthorList', component: () => import('../views/public/AuthorList.vue') },
      { path: 'authors/:id', name: 'AuthorDetail', component: () => import('../views/public/AuthorDetail.vue') },
      { path: 'quotes', name: 'QuotesList', component: () => import('../views/public/QuotesList.vue') },
      { path: 'category/:name', name: 'CategoryDetail', component: () => import('../views/public/CategoryDetail.vue') },
      { path: 'profile', name: 'Profile', component: () => import('../views/user/Profile.vue'), meta: { requiresAuth: true } },
      { path: 'favorites', name: 'Favorites', component: () => import('../views/user/Favorites.vue'), meta: { requiresAuth: true } },
      { path: 'login', name: 'Login', component: () => import('../views/auth/Login.vue') },
      { path: 'signup', name: 'Signup', component: () => import('../views/auth/Signup.vue') },
      { path: 'forgot', name: 'ForgotPassword', component: () => import('../views/auth/ForgotPassword.vue') },
      { path: 'agreement', name: 'Agreement', component: () => import('../views/public/Agreement.vue') },
    ],
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      { path: '', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue') },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/public/NotFound.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

setRouter(router)

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  const user = safeJsonParse<{ role?: string }>(localStorage.getItem('user'), {})

  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.meta.role && user.role !== to.meta.role) {
    next({ name: 'Home' })
  } else {
    next()
  }
})

export default router
