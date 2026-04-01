import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    component: () => import('../views/auth/Login.vue')
  },
  {
    path: '/register',
    component: () => import('../views/auth/Register.vue')
  },
  {
    path: '/student',
    component: () => import('../views/student/Dashboard.vue'),
    meta: { requiresAuth: true, role: 'student' },
    children: [
      {
        path: '',
        component: () => import('../views/student/StudentDashboard.vue')
      }
    ]
  },
  {
    path: '/student/dormitory',
    component: () => import('../views/student/Dormitory.vue'),
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/student/repair',
    component: () => import('../views/student/RepairOrder.vue'),
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/student/device',
    component: () => import('../views/student/DeviceType.vue'),
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/admin',
    component: () => import('../views/admin/Dashboard.vue'),
    meta: { requiresAuth: true, role: 'admin' },
    children: [
      {
        path: '',
        component: () => import('../views/admin/AdminDashboard.vue')
      }
    ]
  },
  {
    path: '/admin/repair',
    component: () => import('../views/admin/RepairOrder.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const requiredRole = to.meta.role

  if (requiresAuth) {
    if (!userStore.isLoggedIn) {
      next('/login')
    } else {
      if (requiredRole && userStore.user && userStore.user.role !== requiredRole) {
        next(from.path)
      } else {
        next()
      }
    }
  } else {
    next()
  }
})

export default router