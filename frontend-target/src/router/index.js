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
        path: 'plans',
        name: 'FollowUpPlans',
        component: () => import('@/views/followUp/FollowUpPlanList.vue'),
        meta: { title: '随访计划' }
      },
      {
        path: 'follow-up-tasks',
        name: 'FollowUpTasks',
        component: () => import('@/views/followUp/FollowUpTaskList.vue'),
        meta: { title: '随访任务' }
      },
      {
        path: 'follow-up-templates',
        name: 'FollowUpTemplates',
        component: () => import('@/views/followUp/FollowUpTemplateList.vue'),
        meta: { title: '随访模板', requiresAdmin: true }
      },
      {
        path: 'alerts',
        name: 'Alerts',
        component: () => import('@/views/alert/AlertList.vue'),
        meta: { title: '预警中心' }
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('@/views/message/MessageList.vue'),
        meta: { title: '消息中心' }
      },
      {
        path: 'clinical-suggestions',
        name: 'ClinicalSuggestions',
        component: () => import('@/views/clinical/ClinicalSuggestionList.vue'),
        meta: { title: 'AI随访建议' }
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
