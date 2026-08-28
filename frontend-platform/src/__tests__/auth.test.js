/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

import { describe, expect, it } from 'vitest';
import { clearAuthSession, getCurrentUserName, redirectToLogin } from '../utils/auth';

function createStorage(values = {}) {
  const data = new Map(Object.entries(values));
  return {
    getItem: (key) => data.get(key) ?? null,
    removeItem: (key) => data.delete(key),
    has: (key) => data.has(key),
  };
}

describe('auth session', () => {
  it('uses the same user identity fields as the target frontend', () => {
    expect(getCurrentUserName(createStorage({ username: 'doctor' }))).toBe('doctor');
    expect(getCurrentUserName(createStorage({ username: 'doctor', realName: '张医生' }))).toBe(
      '张医生',
    );
  });

  it('clears every shared auth field on logout', () => {
    const storage = createStorage({
      token: 't',
      role: 'ADMIN',
      username: 'u',
      realName: 'n',
      theme: 'light',
    });
    clearAuthSession(storage);

    expect(storage.has('token')).toBe(false);
    expect(storage.has('role')).toBe(false);
    expect(storage.has('username')).toBe(false);
    expect(storage.has('realName')).toBe(false);
    expect(storage.has('theme')).toBe(true);
  });

  it('redirects to the configured shared login path', () => {
    const assigned = [];
    redirectToLogin({ assign: (value) => assigned.push(value) });
    expect(assigned).toEqual(['http://localhost:5173/login']);
  });
});
