/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

import request from './request';

export function login(data) {
  return request.post('/auth/login', data);
}
