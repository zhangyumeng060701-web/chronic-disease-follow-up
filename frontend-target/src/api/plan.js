import request from './request';

export function getPlanList(params) {
  return request.get('/plans', { params });
}

export function createPlan(data) {
  return request.post('/plans', data);
}

export function updatePlan(id, data) {
  return request.put(`/plans/${id}`, data);
}

export function deletePlan(id) {
  return request.delete(`/plans/${id}`);
}
