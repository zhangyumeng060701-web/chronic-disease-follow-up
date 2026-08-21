import { describe, expect, it } from 'vitest'
import { toPatientPayload } from '@/utils/patientPayload'

describe('patient payload', () => {
  it('only keeps editable fields', () => {
    const payload = toPatientPayload({
      id: 99,
      name: '张三',
      status: 0,
      createTime: '2026-01-01',
      updateTime: '2026-02-01',
      diseaseType: 'HYPERTENSION'
    })

    expect(payload.id).toBeUndefined()
    expect(payload.status).toBeUndefined()
    expect(payload.createTime).toBeUndefined()
    expect(payload.updateTime).toBeUndefined()
    expect(payload.name).toBe('张三')
    expect(payload.diseaseType).toBe('HYPERTENSION')
  })
})
