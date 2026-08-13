const AUTH_STORAGE_KEYS = ['token', 'role', 'username', 'realName']

export function clearAuthSession(storage = localStorage) {
  AUTH_STORAGE_KEYS.forEach((key) => storage.removeItem(key))
}

export function getCurrentUserName(storage = localStorage) {
  return storage.getItem('realName') || storage.getItem('username') || '开发维护人员'
}

export function redirectToLogin(location = window.location) {
  location.assign(import.meta.env.VITE_LOGIN_URL || 'http://localhost:5173/login')
}
