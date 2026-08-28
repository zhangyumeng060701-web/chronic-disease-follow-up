/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

import request from './request';

export function getMessageList(params) {
  return request.get('/messages', { params });
}

export function createMessage(data) {
  return request.post('/messages', data);
}
