/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

import { describe, expect, it } from 'vitest';
import { resolveRoute } from '@/router/guard';

describe('router guard', () => {
  it('redirects unauthenticated users to login', () => {
    expect(resolveRoute({ path: '/patients', meta: {} }, { token: '', role: '' })).toBe('/login');
  });

  it('redirects logged-in users away from login', () => {
    expect(resolveRoute({ path: '/login', meta: {} }, { token: 't', role: 'DOCTOR' })).toBe(
      '/dashboard',
    );
  });

  it('blocks non-admin from system management', () => {
    expect(
      resolveRoute(
        { path: '/system/users', meta: { requiresAdmin: true } },
        { token: 't', role: 'DOCTOR' },
      ),
    ).toBe('/dashboard');
  });

  it('allows admin to access system management', () => {
    expect(
      resolveRoute(
        { path: '/system/users', meta: { requiresAdmin: true } },
        { token: 't', role: 'ADMIN' },
      ),
    ).toBe(true);
  });
});
