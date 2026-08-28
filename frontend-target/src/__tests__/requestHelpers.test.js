/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

import { describe, expect, it } from 'vitest';
import { buildAuthHeader, resolveResponseError } from '@/api/requestHelpers';

describe('request helpers', () => {
  it('builds bearer header when token exists', () => {
    expect(buildAuthHeader('abc')).toEqual({ Authorization: 'Bearer abc' });
  });

  it('returns empty header without token', () => {
    expect(buildAuthHeader('')).toEqual({});
  });

  it('maps 401 to login expired message', () => {
    expect(resolveResponseError({ response: { status: 401 } })).toEqual({
      code: 401,
      message: '登录已过期，请重新登录',
    });
  });

  it('maps network errors to retry message', () => {
    expect(resolveResponseError({ message: 'Network Error' })).toEqual({
      code: 500,
      message: '网络错误，请稍后重试',
    });
  });
});
