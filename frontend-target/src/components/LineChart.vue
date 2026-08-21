<template>
  <div ref="chartEl" class="line-chart"></div>
</template>

<script setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: { type: Array, default: () => [] },
  name: { type: String, default: '控制率' }
})

const chartEl = ref(null)
let instance = null

function render() {
  if (!instance) return
  instance.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: p => `${p[0].axisValue}<br/>${props.name}率: ${p[0].value}%`
    },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: { type: 'category', data: props.data.map(d => d.month) },
    yAxis: { type: 'value', min: 0, max: 100, axisLabel: { formatter: '{value}%' } },
    series: [{
      data: props.data.map(d => d.rate),
      type: 'line',
      smooth: true,
      areaStyle: { color: 'rgba(64,158,255,0.15)' },
      itemStyle: { color: 'var(--color-primary, #409EFF)' }
    }]
  })
}

function resize() {
  instance?.resize()
}

onMounted(() => {
  instance = echarts.init(chartEl.value)
  render()
  window.addEventListener('resize', resize)
})

watch(() => props.data, render, { deep: true })

onUnmounted(() => {
  window.removeEventListener('resize', resize)
  instance?.dispose()
})
</script>

<style scoped>
.line-chart {
  width: 100%;
  height: 320px;
}
</style>
