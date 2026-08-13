<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in cards" :key="card.title">
        <el-card shadow="hover">
          <div class="card-value">{{ card.value }}</div>
          <div class="card-title">{{ card.title }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <el-card>
          <template #header><span>近12个月血压控制率趋势</span></template>
          <div ref="bpChart" style="height:320px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>近12个月血糖控制率趋势</span></template>
          <div ref="glucoseChart" style="height:320px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top:20px">
      <template #header><span>医生对比</span></template>
      <el-table :data="doctorData" border stripe>
        <el-table-column prop="doctorName" label="医生" width="120" />
        <el-table-column prop="patientCount" label="管理患者数" width="120" />
        <el-table-column prop="completionRate" label="随访完成率" width="120" />
        <el-table-column prop="highRiskCount" label="高危患者数" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getStatsOverview, getBpTrend, getGlucoseTrend, getDoctorComparison } from '@/api/dashboard'
import { buildTrendOption } from '@/utils/dashboardCharts'

const cards = reactive([
  { title: '管理患者总数', value: '--' },
  { title: '本月随访完成率', value: '--' },
  { title: '当前高危患者', value: '--' },
  { title: '当前失访患者', value: '--' }
])

const doctorData = ref([])
const bpChart = ref(null)
const glucoseChart = ref(null)
let bpInstance = null
let glucoseInstance = null

function makeChart(dom, data, name) {
  const instance = echarts.init(dom)
  instance.setOption(buildTrendOption(data, name))
  return instance
}

async function fetchData() {
  try {
    const [overview, bp, glucose, doctors] = await Promise.all([
      getStatsOverview(), getBpTrend(), getGlucoseTrend(), getDoctorComparison()
    ])
    const o = overview.data
    cards[0].value = o.totalPatients ?? '--'
    cards[1].value = o.completionRate ?? '--'
    cards[2].value = o.highRiskCount ?? '--'
    cards[3].value = o.lostFollowUpCount ?? '--'
    doctorData.value = doctors.data || []

    if (bpChart.value) {
      bpInstance = makeChart(bpChart.value, bp.data || [], '血压控制')
    }
    if (glucoseChart.value) {
      glucoseInstance = makeChart(glucoseChart.value, glucose.data || [], '血糖控制')
    }
  } catch { /* ignore */ }
}

function resizeCharts() {
  bpInstance?.resize()
  glucoseInstance?.resize()
}

onMounted(() => { fetchData(); window.addEventListener('resize', resizeCharts) })
onUnmounted(() => { window.removeEventListener('resize', resizeCharts); bpInstance?.dispose(); glucoseInstance?.dispose() })
</script>

<style scoped>
.card-value { font-size: 28px; font-weight: 600; color: #303133; }
.card-title { font-size: 14px; color: #909399; margin-top: 8px; }
</style>
