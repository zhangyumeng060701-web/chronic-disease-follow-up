import request from './request'

export function getPatientList(params) {
  return request.get('/patients', { params })
}

export function getPatientById(id) {
  return request.get(`/patients/${id}`)
}

export function addPatient(data) {
  return request.post('/patients', data)
}

export function updatePatient(id, data) {
  return request.put(`/patients/${id}`, data)
}

export function deletePatient(id) {
  return request.delete(`/patients/${id}`)
}