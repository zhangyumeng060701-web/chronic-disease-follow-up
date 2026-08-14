import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const role = ref(localStorage.getItem('role') || '')
  const username = ref(localStorage.getItem('username') || '')
  const realName = ref(localStorage.getItem('realName') || '')

  const isAdmin = computed(() => role.value === 'ADMIN')

  function setLogin(data) {
    token.value = data.token
    role.value = data.role
    username.value = data.username || ''
    realName.value = data.realName || ''
    localStorage.setItem('token', data.token)
    localStorage.setItem('role', data.role)
    localStorage.setItem('username', data.username || '')
    localStorage.setItem('realName', data.realName || '')
  }

  function logout() {
    token.value = ''
    role.value = ''
    username.value = ''
    realName.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('username')
    localStorage.removeItem('realName')
  }

  return { token, role, username, realName, isAdmin, setLogin, logout }
})
