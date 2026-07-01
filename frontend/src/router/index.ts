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
    name: 'Home',
    component: () => import('../layouts/PublicLayout.vue'),
    children: [
      {
        path: '',
        name: 'NewsList',
        component: () => import('../views/public/NewsList.vue'),
      },
      {
        path: 'news/:id',
        name: 'NewsDetail',
        component: () => import('../views/public/NewsDetail.vue'),
      },
    ],
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue'),
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue'),
      },
      {
        path: 'news',
        name: 'AdminNews',
        component: () => import('../views/admin/NewsList.vue'),
      },
      {
        path: 'news/create',
        name: 'AdminNewsCreate',
        component: () => import('../views/admin/NewsForm.vue'),
      },
      {
        path: 'news/:id/edit',
        name: 'AdminNewsEdit',
        component: () => import('../views/admin/NewsForm.vue'),
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('../views/admin/CategoryList.vue'),
      },
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
    next({ name: 'NewsList' })
  } else {
    next()
  }
})

export default router
