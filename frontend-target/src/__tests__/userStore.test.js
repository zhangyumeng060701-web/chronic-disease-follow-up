import { beforeAll, beforeEach, describe, expect, it } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { useUserStore } from '@/store/user';

function createMockStorage() {
  const data = {};
  return {
    getItem: (key) => (key in data ? data[key] : null),
    setItem: (key, value) => {
      data[key] = String(value);
    },
    removeItem: (key) => {
      delete data[key];
    },
    clear: () => {
      Object.keys(data).forEach((key) => delete data[key]);
    },
  };
}

describe('user store', () => {
  const storage = createMockStorage();

  beforeAll(() => {
    globalThis.localStorage = storage;
  });

  beforeEach(() => {
    storage.clear();
    setActivePinia(createPinia());
  });

  it('setLogin stores token, role and user info', () => {
    const store = useUserStore();
    store.setLogin({ token: 't', role: 'ADMIN', username: 'admin', realName: '管理员' });

    expect(store.token).toBe('t');
    expect(store.isAdmin).toBe(true);
    expect(storage.getItem('role')).toBe('ADMIN');
  });

  it('logout clears all auth state', () => {
    const store = useUserStore();
    store.setLogin({ token: 't', role: 'DOCTOR', username: 'doctor', realName: '李医生' });
    store.logout();

    expect(store.token).toBe('');
    expect(store.isAdmin).toBe(false);
  });
});
