import { beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises, mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

const push=vi.fn()
vi.mock('vue-router', () => ({ useRouter:()=>({push}) }))
vi.mock('@/api/auth', () => ({ login:vi.fn() }))
import { login } from '@/api/auth'
import LoginView from '@/views/login/LoginView.vue'
import { useUserStore } from '@/store/user'

const formStubs={
  'el-form': { template:'<form><slot /></form>', methods:{validate:()=>Promise.resolve(true)} },
  'el-form-item': { template:'<div><slot /></div>' },
  'el-input': { props:['modelValue'], emits:['update:modelValue'], template:'<input :value="modelValue" @input="$emit(\'update:modelValue\',$event.target.value)" />' }
}

describe('登录页', () => {
  beforeEach(() => { setActivePinia(createPinia()); push.mockClear() })
  it('成功登录后保存身份并跳转', async () => { login.mockResolvedValue({data:{token:'t',role:'ADMIN',realName:'管理员'}}); const w=mount(LoginView,{global:{stubs:formStubs}}); const inputs=w.findAll('input'); await inputs[0].setValue('admin'); await inputs[1].setValue('123456'); await w.find('button').trigger('click'); await flushPromises(); expect(login).toHaveBeenCalled(); expect(useUserStore().token).toBe('t'); expect(push).toHaveBeenCalledWith('/dashboard') })
  it('失败登录不保存Token', async () => { login.mockRejectedValue(new Error('bad')); const w=mount(LoginView,{global:{stubs:formStubs}}); await w.find('button').trigger('click'); await flushPromises(); expect(useUserStore().token).toBe('') })
  it('登录完成后loading恢复', async () => { login.mockResolvedValue({data:{token:'t',role:'DOCTOR'}}); const w=mount(LoginView,{global:{stubs:formStubs}}); await w.find('button').trigger('click'); await flushPromises(); expect(w.find('button').attributes('loading')).toBe('false') })
})
