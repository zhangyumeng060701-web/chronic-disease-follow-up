const SENSITIVE_FIELDS = ['name', 'phone', 'idCard', 'address']

export function buildPatientUpdatePayload(formData, isAdmin) {
  const payload = { ...formData }
  if (!isAdmin) {
    SENSITIVE_FIELDS.forEach(field => delete payload[field])
  }
  delete payload.id
  delete payload.status
  delete payload.createTime
  delete payload.updateTime
  delete payload.doctorId
  return payload
}
