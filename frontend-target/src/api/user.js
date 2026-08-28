import request from './request';

export function getUserList(params) {
  return request.get('/users', { params });
}

export function createUser(data) {
  return request.post('/users', data);
}

export function updateUser(id, data) {
  return request.put(`/users/${id}`, data);
}

export function toggleUserStatus(id) {
  return request.put(`/users/${id}/toggle-status`);
}
