import axios from 'axios'
import { ElMessage } from 'element-plus'

const errorMessages = {
  400: '参数校验失败，请检查需求内容',
  401: '登录状态已失效，请重新登录',
  403: '无权限执行该操作',
  404: '请求的资源不存在',
  500: '服务器内部错误，请稍后重试'
}

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

request.interceptors.response.use(
  (response) => {
    const result = response.data || {}
    const { code, data, message } = result

    if (code === 200) {
      return data
    }

    if (code === 401) {
      localStorage.removeItem('token')
    }

    const errorMessage = errorMessages[code] || message || '请求失败'
    ElMessage.error(errorMessage)
    return Promise.reject(new Error(errorMessage))
  },
  (error) => {
    const status = error.response?.status

    if (status === 401) {
      localStorage.removeItem('token')
    }

    const errorMessage = errorMessages[status] || error.message || '接口调用失败，请稍后重试'
    ElMessage.error(errorMessage)
    return Promise.reject(error)
  }
)

export default request
