import request from './request';

export function getAlertList(params) {
  return request.get('/alerts', { params });
}

export function resolveAlert(id) {
  return request.put(`/alerts/${id}/resolve`);
}

export function contactAlert(id) {
  return request.put(`/alerts/${id}/contact`);
}

export function referAlert(id, data) {
  return request.put(`/alerts/${id}/refer`, data);
}
