export function buildAuthHeader(token) {
  return token ? { Authorization: `Bearer ${token}` } : {}
}

export function resolveResponseError(error) {
  if (error.response?.status === 401) {
    return { code: 401, message: '登录已过期，请重新登录' }
  }
  return { code: 500, message: '网络错误，请稍后重试' }
}
