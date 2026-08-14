import { createRouter, createWebHistory } from 'vue-router'
import PlatformLayout from '../layout/PlatformLayout.vue'
import ChatPanel from '../views/requirement/ChatPanel.vue'

const routes = [
  {
    path: '/',
    redirect: '/requirement'
  },
  {
    path: '/requirement',
    component: PlatformLayout,
    children: [
      {
        path: '',
        name: 'RequirementInput',
        component: ChatPanel
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
