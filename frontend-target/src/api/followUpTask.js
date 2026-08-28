import request from './request';

export function getTaskList(params) {
  return request.get('/follow-up-tasks', { params });
}

export function completeTask(id) {
  return request.put(`/follow-up-tasks/${id}/complete`);
}

export function cancelTask(id) {
  return request.put(`/follow-up-tasks/${id}/cancel`);
}
