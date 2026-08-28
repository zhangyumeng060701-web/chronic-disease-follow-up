/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

import request from './request';

export function getFollowUpList(params) {
  return request.get('/follow-ups', { params });
}

export function getFollowUpById(id) {
  return request.get(`/follow-ups/${id}`);
}

export function addFollowUp(data) {
  return request.post('/follow-ups', data);
}

export function updateFollowUp(id, data) {
  return request.put(`/follow-ups/${id}`, data);
}

export function deleteFollowUp(id) {
  return request.delete(`/follow-ups/${id}`);
}

export function getOverdueFollowUps() {
  return request.get('/follow-ups/overdue');
}
