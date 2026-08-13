import { beforeEach, describe, expect, it, vi } from 'vitest'
import { authGuard } from '@/router'

describe('路由认证守卫', () => {
  beforeEach(() => localStorage.clear())
  it('无Token访问业务页跳转登录', () => { const next=vi.fn(); authGuard({path:'/dashboard'}, {}, next); expect(next).toHaveBeenCalledWith('/login') })
  it('有Token访问登录页跳转工作台', () => { localStorage.setItem('token','x'); const next=vi.fn(); authGuard({path:'/login'}, {}, next); expect(next).toHaveBeenCalledWith('/dashboard') })
  it('有Token访问业务页正常放行', () => { localStorage.setItem('token','x'); const next=vi.fn(); authGuard({path:'/patients'}, {}, next); expect(next).toHaveBeenCalledWith() })
  it('无Token访问登录页正常放行', () => { const next=vi.fn(); authGuard({path:'/login'}, {}, next); expect(next).toHaveBeenCalledWith() })
})
