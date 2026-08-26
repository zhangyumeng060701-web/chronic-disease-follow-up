<template>
  <div class="dashboard">
    <div class="dashboard-head">
      <div>
        <h2>工作台</h2>
        <p>随访质量与风险概况</p>
      </div>
      <span class="head-badge">实时数据</span>
    </div>

    <div class="metric-grid">
      <article
        v-for="card in cards"
        :key="card.title"
        class="metric"
        :class="`tone-${card.tone}`"
        v-loading="overviewLoading"
      >
        <div class="metric-top">
          <span class="metric-label">{{ card.title }}</span>
          <span class="metric-mark"></span>
        </div>
        <div class="metric-value">
          <span>{{ card.value }}</span>
          <small>{{ card.unit }}</small>
        </div>
      </article>
    </div>

    <div class="chart-grid">
      <section class="panel" v-loading="chartLoading">
        <header>
          <h3>血压控制率趋势</h3>
          <span>近 12 个月</span>
        </header>
        <LineChart :data="bpData" name="血压控制" />
      </section>
      <section class="panel" v-loading="chartLoading">
        <header>
          <h3>血糖控制率趋势</h3>
          <span>近 12 个月</span>
        </header>
        <LineChart :data="glucoseData" name="血糖控制" />
      </section>
    </div>

    <section class="panel doctor-panel" v-loading="doctorLoading">
      <header>
        <h3>医生管理对比</h3>
        <span>患者、随访与风险概况</span>
      </header>
      <el-table :data="doctorData" empty-text="暂无医生数据">
        <el-table-column prop="doctorName" label="医生" min-width="140" />
        <el-table-column prop="patientCount" label="管理患者数" min-width="120" />
        <el-table-column prop="completionRate" label="随访完成率" min-width="120" />
        <el-table-column prop="highRiskCount" label="高危患者数" min-width="120" />
      </el-table>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import LineChart from '@/components/LineChart.vue'
import { getStatsOverview, getBpTrend, getGlucoseTrend, getDoctorComparison } from '@/api/dashboard'

const cards = reactive([
  { title: '管理患者总数', value: '--', unit: '人', tone: 'accent' },
  { title: '本月随访完成率', value: '--', unit: '%', tone: 'success' },
  { title: '当前高危患者', value: '--', unit: '人', tone: 'danger' },
  { title: '当前失访患者', value: '--', unit: '人', tone: 'warning' }
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

.dashboard-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.dashboard-head h2 {
  margin: 0;
  color: var(--color-text-primary);
  font-size: 22px;
  font-weight: 600;

}

.dashboard-head p {
  margin: 6px 0 0;
  color: var(--color-text-secondary);
  font-size: 13px;
}

.head-badge {
  padding: 5px 10px;
  color: var(--color-primary);
  font-size: 12px;
  font-weight: 600;

  border: 1px solid rgba(14, 143, 156, 0.3);
  border-radius: 4px;
  background: rgba(14, 143, 156, 0.06);
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.metric {
  position: relative;
  min-width: 0;
  padding: 18px 18px 16px;
  overflow: hidden;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: 6px;
}

.metric::before {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  height: 3px;
  background: var(--metric-accent);
}

.tone-accent {
  --metric-accent: var(--color-accent);
}

.tone-success {
  --metric-accent: var(--color-success);
}

.tone-danger {
  --metric-accent: var(--color-danger);
}

.tone-warning {
  --metric-accent: var(--color-warning);
}

.metric-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.metric-label {
  overflow: hidden;
  color: var(--color-text-secondary);
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.metric-mark {
  width: 8px;
  height: 8px;
  flex-shrink: 0;
  border-radius: 50%;
  background: var(--metric-accent);
  box-shadow: 0 0 0 4px color-mix(in srgb, var(--metric-accent) 12%, transparent);
}

.metric-value {
  display: flex;
  align-items: baseline;
  gap: 6px;
  margin-top: 14px;
  color: var(--color-text-primary);
  font-size: 32px;
  font-weight: 600;
  font-variant-numeric: tabular-nums;

  white-space: nowrap;
}

.metric-value small {
  color: var(--color-text-secondary);
  font-size: 13px;
  font-weight: 500;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 14px;
}

.panel {
  min-width: 0;
  overflow: hidden;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: 6px;
}

.panel header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 18px;
  border-bottom: 1px solid var(--color-border);
}

.panel h3 {
  margin: 0;
  color: var(--color-text-primary);
  font-size: 15px;
  font-weight: 600;
}

.panel header span {
  color: var(--color-text-secondary);
  font-size: 12px;
}

.doctor-panel {
  margin-top: 14px;
}

@media (max-width: 900px) {
  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .chart-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 520px) {
  .metric-grid {
    grid-template-columns: 1fr;
  }

  .dashboard-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .metric-value {
    font-size: 28px;
  }
}
</style>
