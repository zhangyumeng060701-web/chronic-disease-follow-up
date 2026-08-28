/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

export function buildLineChartOption(data = [], name = '控制率') {
  return {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(26, 29, 33, 0.94)',
      borderWidth: 0,
      textStyle: { color: '#FFFFFF', fontSize: 12 },
      formatter: (p) => `${p[0].axisValue}<br/>${name}率: ${p[0].value}%`,
    },
    grid: { left: 48, right: 20, top: 28, bottom: 32 },
    xAxis: {
      type: 'category',
      data: data.map((d) => d.month),
      axisLine: { lineStyle: { color: '#E5E7EB' } },
      axisTick: { show: false },
      axisLabel: { color: '#6B7280', fontSize: 11 },
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: { color: '#6B7280', fontSize: 11, formatter: '{value}%' },
      splitLine: { lineStyle: { color: '#EEF0F3', type: 'dashed' } },
    },
    series: [
      {
        data: data.map((d) => d.rate),
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { width: 2, color: '#0FA47F' },
        itemStyle: { color: '#0FA47F', borderColor: '#FFFFFF', borderWidth: 1 },
        areaStyle: { color: 'rgba(15, 164, 127, 0.14)' },
      },
    ],
  };
}
