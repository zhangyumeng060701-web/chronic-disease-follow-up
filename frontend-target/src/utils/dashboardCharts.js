export function buildLineChartOption(data = [], name = '控制率') {
  return {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(15, 41, 46, 0.92)',
      borderWidth: 0,
      textStyle: { color: '#FFFFFF', fontSize: 12 },
      formatter: p => `${p[0].axisValue}<br/>${name}率: ${p[0].value}%`
    },
    grid: { left: 48, right: 20, top: 28, bottom: 32 },
    xAxis: {
      type: 'category',
      data: data.map(d => d.month),
      axisLine: { lineStyle: { color: '#C8D6DA' } },
      axisTick: { show: false },
      axisLabel: { color: '#71858A', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: { color: '#71858A', fontSize: 11, formatter: '{value}%' },
      splitLine: { lineStyle: { color: '#E7EDEF', type: 'dashed' } }
    },
    series: [{
      data: data.map(d => d.rate),
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { width: 2, color: '#0E8F9C' },
      itemStyle: { color: '#0E8F9C', borderColor: '#FFFFFF', borderWidth: 1 },
      areaStyle: { color: 'rgba(14, 143, 156, 0.14)' }
    }]
  }
}
