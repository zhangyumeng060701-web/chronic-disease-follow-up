<template>
  <div class="alert-list">
    <el-form :model="searchForm" inline>
      <el-form-item label="预警类型">
        <el-select v-model="searchForm.alertType" placeholder="全部" clearable>
          <el-option label="高危" value="HIGH_RISK" />
          <el-option label="失访" value="LOST_FOLLOW_UP" />
        </el-select>
      </el-form-item>
      <el-form-item label="等级">
        <el-select v-model="searchForm.alertLevel" placeholder="全部" clearable>
          <el-option label="RED" value="RED" />
          <el-option label="YELLOW" value="YELLOW" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.isResolved" placeholder="全部" clearable>
          <el-option label="未处理" :value="0" />
          <el-option label="已处理" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" border stripe v-loading="loading" :empty-text="EMPTY_TEXT.ALERT">
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="alertType" label="预警类型" width="90">
        <template #default="{ row }">
          <el-tag :type="row.alertType==='HIGH_RISK'?'danger':'warning'" size="small">
            {{ row.alertType === 'HIGH_RISK' ? '高危' : '失访' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="alertLevel" label="等级" width="80">
        <template #default="{ row }">
          <el-tag :type="(ALERT_LEVELS[row.alertLevel]||{}).type || 'info'" effect="dark">
            {{ (ALERT_LEVELS[row.alertLevel]||{}).label || row.alertLevel }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="alertReason" label="触发原因" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.isResolved ? 'success' : 'info'" size="small">
            {{ row.isResolved ? '已处理' : '未处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right">
        <template #default="{ row }">
          <el-button v-if="!row.isResolved" size="small" type="primary" @click="handleResolve(row)">
            处理
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      layout="total, prev, pager, next"
      @current-change="fetchData"
      style="margin-top:16px;justify-content:flex-end"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAlertList, resolveAlert } from '@/api/alert'
import { ALERT_LEVELS, EMPTY_TEXT } from '@/constants/domain'
import { ElMessage } from 'element-plus'

const searchForm = reactive({ alertType: '', alertLevel: '', isResolved: '' })
const tableData = ref([])
const loading = ref(false)
const pagination = reactive({ page: 1, size: 20, total: 0 })

async function fetchData() {
  loading.value = true
  try {
    const params = { page: pagination.page, size: pagination.size }
    if (searchForm.alertType) params.alertType = searchForm.alertType
    if (searchForm.alertLevel) params.alertLevel = searchForm.alertLevel
    if (searchForm.isResolved !== '') params.isResolved = searchForm.isResolved
    const res = await getAlertList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handleReset() {
  searchForm.alertType = ''; searchForm.alertLevel = ''; searchForm.isResolved = ''
  handleSearch()
}

async function handleResolve(row) {
  await resolveAlert(row.id)
  ElMessage.success('预警已处理')
  fetchData()
}

onMounted(() => fetchData())
</script>
