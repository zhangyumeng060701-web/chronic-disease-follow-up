/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

import request from './request';

export function decomposeRequirement(requirement) {
  return request.post('/ai/decompose', { requirement });
}
