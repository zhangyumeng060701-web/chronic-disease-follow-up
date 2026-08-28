import { describe, expect, it } from 'vitest';
import {
  maskAddress,
  maskIdCard,
  maskName,
  maskPhone,
  maskSensitiveText,
} from '@/utils/desensitize';

describe('desensitize utils', () => {
  it('masks patient name by keeping the first character', () => {
    expect(maskName('张三丰')).toBe('张**');
    expect(maskName('李')).toBe('*');
    expect(maskName('')).toBe('');
  });

  it('masks phone number by keeping first 3 and last 4 digits', () => {
    expect(maskPhone('13812345678')).toBe('138****5678');
  });

  it('masks id card by keeping first 6 and last 4 characters', () => {
    expect(maskIdCard('320102199001011234')).toBe('320102********1234');
  });

  it('masks address to district or county level when possible', () => {
    expect(maskAddress('南京市鼓楼区汉口路22号')).toBe('南京市鼓楼区****');
    expect(maskAddress('未知地址')).toBe('****');
    expect(maskAddress('')).toBe('');
  });

  it('routes by sensitive data type', () => {
    expect(maskSensitiveText('13812345678', 'phone')).toBe('138****5678');
    expect(maskSensitiveText('普通文本', 'unknown')).toBe('普通文本');
  });
});
