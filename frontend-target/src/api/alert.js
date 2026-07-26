import request from './request'

export function getAlertList(params) {
  return request.get('/alerts', { params })
}

export function resolveAlert(id) {
  return request.put(`/alerts/${id}/resolve`)
}
