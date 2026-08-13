import { beforeEach, describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

const push=vi.fn()
vi.mock('vue-router', () => ({ useRoute:()=>({path:'/dashboard',meta:{title:'工作台'}}), useRouter:()=>({push}) }))
import MainLayout from '@/layout/MainLayout.vue'
import { useUserStore } from '@/store/user'

describe('主布局', () => {
  beforeEach(() => { setActivePinia(createPinia()); push.mockClear() })
  it('管理员显示系统管理菜单', () => { const store=useUserStore(); store.role='ADMIN'; expect(mount(MainLayout).text()).toContain('用户管理') })
  it('医生隐藏系统管理菜单', () => { const store=useUserStore(); store.role='DOCTOR'; expect(mount(MainLayout).text()).not.toContain('用户管理') })
  it('显示当前用户姓名', () => { const store=useUserStore(); store.realName='王医生'; expect(mount(MainLayout).text()).toContain('王医生') })
  it('退出清理身份并跳转登录', async () => { const store=useUserStore(); store.setLogin({token:'x',role:'ADMIN',username:'admin',realName:'管理员'}); const wrapper=mount(MainLayout); await wrapper.findAll('button').at(-1).trigger('click'); expect(store.token).toBe(''); expect(push).toHaveBeenCalledWith('/login') })
})
