import { expect, test } from '@playwright/test'

const apiBase = process.env.E2E_API_BASE_URL || 'http://127.0.0.1:8080'
const credentials = {
  admin: [process.env.E2E_ADMIN_USERNAME, process.env.E2E_ADMIN_PASSWORD],
  doctorA: [process.env.E2E_DOCTOR_A_USERNAME, process.env.E2E_DOCTOR_A_PASSWORD],
  doctorB: [process.env.E2E_DOCTOR_B_USERNAME, process.env.E2E_DOCTOR_B_PASSWORD]
}

function requireEnvironment() {
  const missing = Object.entries(credentials)
    .filter(([, pair]) => pair.some(value => !value))
    .map(([name]) => name)
  if (missing.length) throw new Error(`Missing E2E credentials for: ${missing.join(', ')}`)
}

async function login(request, [username, password]) {
  const response = await request.post(`${apiBase}/api/auth/login`, { data: { username, password } })
  expect(response.ok()).toBeTruthy()
  const body = await response.json()
  expect(body.code).toBe(200)
  return body.data.token
}

async function getJson(request, path, token) {
  const response = await request.get(`${apiBase}${path}`, {
    headers: { Authorization: `Bearer ${token}` }
  })
  return { response, body: await response.json() }
}

test.beforeAll(requireEnvironment)

test('doctor data scopes are isolated and sensitive fields are masked', async ({ request }) => {
  const doctorAToken = await login(request, credentials.doctorA)
  const doctorBToken = await login(request, credentials.doctorB)
  const a = await getJson(request, '/api/patients?page=1&size=100', doctorAToken)
  const b = await getJson(request, '/api/patients?page=1&size=100', doctorBToken)
  expect(a.response.ok()).toBeTruthy()
  expect(b.response.ok()).toBeTruthy()

  const aRecords = a.body.data.records
  const bRecords = b.body.data.records
  expect(aRecords.length).toBeGreaterThan(0)
  expect(bRecords.length).toBeGreaterThan(0)
  expect(aRecords.map(item => item.id)).not.toEqual(expect.arrayContaining(bRecords.map(item => item.id)))
  for (const patient of [...aRecords, ...bRecords]) {
    if (patient.phone) expect(patient.phone).toMatch(/^\d{3}\*{4}\d{4}$/)
    if (patient.idCard) expect(patient.idCard).toContain('*')
  }

  const crossAccess = await getJson(request, `/api/patients/${bRecords[0].id}`, doctorAToken)
  expect(crossAccess.response.status()).toBe(403)
  expect(crossAccess.body.code).toBe(403)
})

test('admin sees all ownership groups and doctor cannot access admin endpoints', async ({ request }) => {
  const adminToken = await login(request, credentials.admin)
  const doctorToken = await login(request, credentials.doctorA)
  const patients = await getJson(request, '/api/patients?page=1&size=100', adminToken)
  expect(patients.response.ok()).toBeTruthy()
  expect(new Set(patients.body.data.records.map(item => item.doctorId)).size).toBeGreaterThan(1)

  const userList = await getJson(request, '/api/users?page=1&size=20', doctorToken)
  expect(userList.response.status()).toBe(403)
  const logs = await getJson(request, '/api/logs?page=1&size=20', doctorToken)
  expect(logs.response.status()).toBe(403)
})

test('forged token is rejected', async ({ request }) => {
  const result = await getJson(request, '/api/patients?page=1&size=20', 'forged.invalid.token')
  expect(result.response.status()).toBe(401)
})
