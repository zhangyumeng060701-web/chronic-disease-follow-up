import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import storage from '@/utils/storage'

export const useUserStore = defineStore('user', () => {
  const token = ref(storage.get('token'))
  const role = ref(storage.get('role'))
  const username = ref(storage.get('username'))
  const realName = ref(storage.get('realName'))

  const isAdmin = computed(() => role.value === 'ADMIN')

  function setLogin(data) {
    token.value = data.token
    role.value = data.role
    username.value = data.username || ''
    realName.value = data.realName || ''
    storage.set('token', data.token)
    storage.set('role', data.role)
    storage.set('username', data.username || '')
    storage.set('realName', data.realName || '')
  }

  function logout() {
    token.value = ''
    role.value = ''
    username.value = ''
    realName.value = ''
    storage.clear()
  }

  return { token, role, username, realName, isAdmin, setLogin, logout }
})
