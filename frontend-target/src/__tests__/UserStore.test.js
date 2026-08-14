import { beforeEach, describe, expect, it } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useUserStore } from '@/store/user'

describe('user store storage boundary', () => {
  beforeEach(() => {
    localStorage.clear()
    setActivePinia(createPinia())
  })

  it('logout removes only authentication keys', () => {
    localStorage.setItem('unrelated-preference', 'keep')
    const store = useUserStore()
    store.setLogin({ token: 'token', role: 'ADMIN', username: 'admin', realName: 'Admin' })
    store.logout()
    expect(localStorage.getItem('token')).toBeNull()
    expect(localStorage.getItem('role')).toBeNull()
    expect(localStorage.getItem('username')).toBeNull()
    expect(localStorage.getItem('realName')).toBeNull()
    expect(localStorage.getItem('unrelated-preference')).toBe('keep')
  })
})
