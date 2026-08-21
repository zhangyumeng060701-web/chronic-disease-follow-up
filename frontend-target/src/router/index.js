import { createRouter, createWebHistory } from 'vue-router'
import { resolveRoute } from './guard'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue')
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'patients',
        name: 'Patients',
        component: () => import('@/views/patient/PatientList.vue'),
        meta: { title: '患者管理' }
      },
      {
        path: 'follow-ups',
        name: 'FollowUps',
        component: () => import('@/views/followUp/FollowUpList.vue'),
        meta: { title: '随访记录' }
      },
      {
        path: 'alerts',
        name: 'Alerts',
        component: () => import('@/views/alert/AlertList.vue'),
        meta: { title: '预警中心' }
      },
      {
        path: 'system/users',
        name: 'Users',
        component: () => import('@/views/system/UserManage.vue'),
        meta: { title: '用户管理', requiresAdmin: true }
      },
      {
        path: 'system/logs',
        name: 'Logs',
        component: () => import('@/views/system/OperLog.vue'),
        meta: { title: '操作日志', requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const decision = resolveRoute(to, {
    token: localStorage.getItem('token'),
    role: localStorage.getItem('role')
  })
  if (decision === true) {
    next()
  } else {
    next(decision)
  }
})

export default router
