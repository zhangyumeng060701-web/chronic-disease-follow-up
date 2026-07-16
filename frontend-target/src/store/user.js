import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const role = ref(localStorage.getItem('role') || '')
  const username = ref(localStorage.getItem('username') || '')

  const isAdmin = computed(() => role.value === 'ADMIN')

  function setLogin(data) {
    token.value = data.token
    role.value = data.role
    username.value = data.username || ''
    localStorage.setItem('token', data.token)
    localStorage.setItem('role', data.role)
    localStorage.setItem('username', data.username || '')
  }

  function logout() {
    token.value = ''
    role.value = ''
    username.value = ''
    localStorage.clear()
  }

  return { token, role, username, isAdmin, setLogin, logout }
})