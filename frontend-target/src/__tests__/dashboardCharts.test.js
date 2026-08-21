import { describe, expect, it } from 'vitest'
import { buildLineChartOption } from '@/utils/dashboardCharts'

describe('dashboard charts', () => {
  it('builds line chart option from trend data', () => {
    const option = buildLineChartOption([
      { month: '2026-07', rate: 50 },
      { month: '2026-08', rate: 60 }
    ], '血压控制')

    expect(option.xAxis.data).toEqual(['2026-07', '2026-08'])
    expect(option.series[0].data).toEqual([50, 60])
    expect(option.series[0].type).toBe('line')
  })

  it('handles empty data', () => {
    const option = buildLineChartOption([], '血糖控制')
    expect(option.xAxis.data).toEqual([])
    expect(option.series[0].data).toEqual([])
  })
})
