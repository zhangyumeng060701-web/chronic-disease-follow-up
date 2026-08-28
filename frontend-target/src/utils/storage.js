const storage = {
  get(key, fallback = '') {
    try {
      const value = sessionStorage.getItem(key);
      return value === null ? fallback : value;
    } catch {
      return fallback;
    }
  },
  set(key, value) {
    sessionStorage.setItem(key, value);
  },
  remove(key) {
    sessionStorage.removeItem(key);
  },
  clear() {
    sessionStorage.clear();
  },
};

export default storage;
