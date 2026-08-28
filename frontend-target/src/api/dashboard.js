import request from './request';

export function getStatsOverview() {
  return request.get('/stats/overview');
}

export function getBpTrend() {
  return request.get('/stats/bp-trend');
}

export function getGlucoseTrend() {
  return request.get('/stats/glucose-trend');
}

export function getDoctorComparison() {
  return request.get('/stats/doctor-comparison');
}

export function getWorkbench() {
  return request.get('/dashboard/workbench');
}
