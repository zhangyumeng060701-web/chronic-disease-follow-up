export function normalizeTrend(data) {
  if (!Array.isArray(data)) return []
  return data.map(item => ({
    month: item?.month ?? '',
    rate: Number.isFinite(Number(item?.rate)) ? Number(item.rate) : 0
  }))
}

export function buildTrendOption(data, name) {
  const normalized = normalizeTrend(data)
  return {
    tooltip: { trigger: 'axis', formatter: p => `${p[0].axisValue}<br/>${name}率: ${p[0].value}%` },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: { type: 'category', data: normalized.map(d => d.month) },
    yAxis: { type: 'value', min: 0, max: 100, axisLabel: { formatter: '{value}%' } },
    series: [{ data: normalized.map(d => d.rate), type: 'line', smooth: true,
      areaStyle: { color: 'rgba(64,158,255,0.15)' }, itemStyle: { color: '#409EFF' } }]
  }
}
