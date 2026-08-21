<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :xs="12" :sm="12" :md="6" v-for="card in cards" :key="card.title">
        <el-card shadow="hover" v-loading="overviewLoading">
          <div class="card-value">{{ card.value }}</div>
          <div class="card-title">{{ card.title }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :md="12">
        <el-card v-loading="chartLoading">
          <template #header><span>近12个月血压控制率趋势</span></template>
          <LineChart :data="bpData" name="血压控制" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card v-loading="chartLoading">
          <template #header><span>近12个月血糖控制率趋势</span></template>
          <LineChart :data="glucoseData" name="血糖控制" />
        </el-card>
      </el-col>
    </el-row>

    <el-card class="doctor-card" v-loading="doctorLoading">
      <template #header><span>医生对比</span></template>
      <el-table :data="doctorData" border stripe empty-text="暂无医生数据">
        <el-table-column prop="doctorName" label="医生" width="120" />
        <el-table-column prop="patientCount" label="管理患者数" width="120" />
        <el-table-column prop="completionRate" label="随访完成率" width="120" />
        <el-table-column prop="highRiskCount" label="高危患者数" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import LineChart from '@/components/LineChart.vue'
import { getStatsOverview, getBpTrend, getGlucoseTrend, getDoctorComparison } from '@/api/dashboard'

const cards = reactive([
  { title: '管理患者总数', value: '--' },
  { title: '本月随访完成率', value: '--' },
  { title: '当前高危患者', value: '--' },
  { title: '当前失访患者', value: '--' }
])

const doctorData = ref([])
const bpData = ref([])
const glucoseData = ref([])
const overviewLoading = ref(false)
const chartLoading = ref(false)
const doctorLoading = ref(false)

async function fetchOverview() {
  overviewLoading.value = true
  try {
    const res = await getStatsOverview()
    const o = res.data
    cards[0].value = o.totalPatients ?? '--'
    cards[1].value = o.completionRate ?? '--'
    cards[2].value = o.highRiskCount ?? '--'
    cards[3].value = o.lostFollowUpCount ?? '--'
  } catch {
    // 保持默认值
  } finally {
    overviewLoading.value = false
  }
}

async function fetchCharts() {
  chartLoading.value = true
  try {
    const [bp, glucose] = await Promise.all([getBpTrend(), getGlucoseTrend()])
    bpData.value = bp.data || []
    glucoseData.value = glucose.data || []
  } catch {
    bpData.value = []
    glucoseData.value = []
  } finally {
    chartLoading.value = false
  }
}

async function fetchDoctors() {
  doctorLoading.value = true
  try {
    const res = await getDoctorComparison()
    doctorData.value = res.data || []
  } catch {
    doctorData.value = []
  } finally {
    doctorLoading.value = false
  }
}

onMounted(() => {
  fetchOverview()
  fetchCharts()
  fetchDoctors()
})
</script>

<style scoped>
.dashboard {
  padding: var(--layout-main-padding);
  background: var(--color-bg);
}
.card-value {
  font-size: 28px;
  font-weight: 600;
  color: var(--color-text-primary);
}
.card-title {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-top: 8px;
}
.chart-row {
  margin-top: 20px;
}
.chart-row .el-col + .el-col {
  margin-top: 16px;
}
@media (min-width: 768px) {
  .chart-row .el-col + .el-col {
    margin-top: 0;
  }
}
.doctor-card {
  margin-top: 20px;
}
</style>
