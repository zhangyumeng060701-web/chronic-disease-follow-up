import { describe, expect, it } from 'vitest'
import { buildTrendOption, normalizeTrend } from '@/utils/dashboardCharts'

describe('Dashboard图表转换', () => {
  it('转换正常趋势数据', () => expect(normalizeTrend([{ month: '01', rate: '80' }])).toEqual([{ month: '01', rate: 80 }]))
  it('空值转换为空数组', () => { expect(normalizeTrend(null)).toEqual([]); expect(normalizeTrend(undefined)).toEqual([]) })
  it('异常rate安全转换为0', () => expect(normalizeTrend([{ month: '01', rate: 'bad' }])[0].rate).toBe(0))
  it('缺失字段使用安全默认值', () => expect(normalizeTrend([{}])).toEqual([{ month: '', rate: 0 }]))
  it('不修改原始数据', () => { const input=[{month:'01',rate:'90'}]; normalizeTrend(input); expect(input[0].rate).toBe('90') })
  it('生成ECharts横轴与序列', () => { const option=buildTrendOption([{month:'01',rate:88}], '血压控制'); expect(option.xAxis.data).toEqual(['01']); expect(option.series[0].data).toEqual([88]) })
  it('空数据仍生成合法option', () => expect(buildTrendOption([], '血糖控制').series[0].data).toEqual([]))
  it('tooltip包含业务名称', () => { const option=buildTrendOption([], '血糖控制'); expect(option.tooltip.formatter([{axisValue:'01',value:90}])).toContain('血糖控制率') })
})
