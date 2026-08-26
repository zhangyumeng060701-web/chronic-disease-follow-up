import request from './request'

export function assessPatientRisk(patientId) {
  return request.post(`/clinical/patients/${patientId}/risk-assessment`)
}

export function generateSuggestion(patientId) {
  return request.post(`/clinical/patients/${patientId}/suggestions`)
}

export function getSuggestionList(params) {
  return request.get('/clinical/suggestions', { params })
}

export function confirmSuggestion(id) {
  return request.put(`/clinical/suggestions/${id}/confirm`)
}

export function rejectSuggestion(id) {
  return request.put(`/clinical/suggestions/${id}/reject`)
}
