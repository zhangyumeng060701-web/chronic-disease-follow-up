export function buildLineChartOption(data = [], name = '控制率') {
  return {
    tooltip: {
      trigger: 'axis',
      formatter: p => `${p[0].axisValue}<br/>${name}率: ${p[0].value}%`
    },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: { type: 'category', data: data.map(d => d.month) },
    yAxis: { type: 'value', min: 0, max: 100, axisLabel: { formatter: '{value}%' } },
    series: [{
      data: data.map(d => d.rate),
      type: 'line',
      smooth: true,
      areaStyle: { color: 'rgba(64,158,255,0.15)' },
      itemStyle: { color: 'var(--color-primary, #409EFF)' }
    }]
  }
}
