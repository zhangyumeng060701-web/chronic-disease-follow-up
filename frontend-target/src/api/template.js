/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

import request from './request';

export function getTemplateList(params) {
  return request.get('/follow-up-templates', { params });
}

export function createTemplate(data) {
  return request.post('/follow-up-templates', data);
}

export function updateTemplate(id, data) {
  return request.put(`/follow-up-templates/${id}`, data);
}

export function toggleTemplate(id) {
  return request.put(`/follow-up-templates/${id}/toggle`);
}
