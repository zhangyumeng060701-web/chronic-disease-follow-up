import { describe, expect, it } from 'vitest'
import {
  maskAddress,
  maskIdCard,
  maskName,
  maskPhone,
  maskSensitiveText
} from '@/utils/desensitize'

describe('desensitize utils', () => {
  it('masks patient name by keeping the first character', () => {
    expect(maskName('\u5f20\u4e09\u4e30')).toBe('\u5f20**')
    expect(maskName('\u674e')).toBe('\u674e')
    expect(maskName('')).toBe('')
  })

  it('masks phone number by keeping first 3 and last 4 digits', () => {
    expect(maskPhone('13812345678')).toBe('138****5678')
  })

  it('masks id card by keeping first 6 and last 4 characters', () => {
    expect(maskIdCard('320102199001011234')).toBe('320102********1234')
  })

  it('masks address to district or county level when possible', () => {
    expect(maskAddress('\u5357\u4eac\u5e02\u9f13\u697c\u533a\u6c49\u53e3\u8def22\u53f7')).toBe('\u5357\u4eac\u5e02\u9f13\u697c\u533a****')
    expect(maskAddress('\u672a\u77e5\u5730\u5740')).toBe('****')
    expect(maskAddress('')).toBe('')
  })

  it('routes by sensitive data type', () => {
    expect(maskSensitiveText('13812345678', 'phone')).toBe('138****5678')
    expect(maskSensitiveText('\u666e\u901a\u6587\u672c', 'unknown')).toBe('\u666e\u901a\u6587\u672c')
  })
})

