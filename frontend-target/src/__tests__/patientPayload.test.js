import { describe, expect, it } from 'vitest'
import { buildPatientUpdatePayload } from '@/utils/patientPayload'

const row = {
  id: 1, name: '张*', phone: '138****5678', idCard: '320102********1234',
  address: '南京市鼓楼区****', doctorId: 7, status: 1,
  createTime: '2026-01-01', updateTime: '2026-01-02',
  gender: '男', diseaseType: 'HYPERTENSION', medicalHistory: '已改善'
}

describe('患者更新载荷', () => {
  it('医生更新时移除敏感字段和服务端字段', () => {
    expect(buildPatientUpdatePayload(row, false)).toEqual({
      gender: '男', diseaseType: 'HYPERTENSION', medicalHistory: '已改善'
    })
  })

  it('管理员保留敏感字段但移除服务端字段', () => {
    const payload = buildPatientUpdatePayload({ ...row, name: '张三', phone: '13812345678' }, true)
    expect(payload.name).toBe('张三')
    expect(payload.phone).toBe('13812345678')
    expect(payload).not.toHaveProperty('id')
    expect(payload).not.toHaveProperty('doctorId')
  })
})
