/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

import request from './request';

export function getLogList(params) {
  return request.get('/logs', { params });
}
