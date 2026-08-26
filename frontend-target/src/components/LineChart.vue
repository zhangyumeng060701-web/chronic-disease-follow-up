<template>
  <div ref="chartEl" class="line-chart"></div>
</template>

<script setup>
import { onMounted, onUnmounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import { buildLineChartOption } from '@/utils/dashboardCharts'

const props = defineProps({
  data: { type: Array, default: () => [] },
  name: { type: String, default: '控制率' }
})

const chartEl = ref(null)
let instance = null

function render() {
  if (!instance) return
  instance.setOption(buildLineChartOption(props.data, props.name))
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
