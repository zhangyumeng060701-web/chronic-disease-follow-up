export function toPatientPayload(data = {}) {
  return {
    name: data.name,
    gender: data.gender,
    age: data.age,
    phone: data.phone,
    idCard: data.idCard,
    address: data.address,
    diseaseType: data.diseaseType,
    medicalHistory: data.medicalHistory,
    medicationInfo: data.medicationInfo,
    doctorId: data.doctorId
  }
}
