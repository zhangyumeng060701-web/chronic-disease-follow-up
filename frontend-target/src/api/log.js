import request from './request'

export function getLogList(params) {
  return request.get('/logs', { params })
}
