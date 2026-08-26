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
    heightCm: data.heightCm,
    weightKg: data.weightKg,
    smoking: data.smoking,
    drinking: data.drinking,
    allergyHistory: data.allergyHistory,
    medicationHistory: data.medicationHistory,
    doctorId: data.doctorId
  }
}
