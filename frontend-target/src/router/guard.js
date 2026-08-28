export function resolveRoute(to, { token, role }) {
  if (to.path !== '/login' && !token) {
    return '/login';
  }
  if (to.path === '/login' && token) {
    return '/dashboard';
  }
  if (to.meta?.requiresAdmin && role !== 'ADMIN') {
    return '/dashboard';
  }
  return true;
}
