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
      <el-form-item label="处理进度">
        <el-select v-model="searchForm.alertStatus" placeholder="全部" clearable>
          <el-option label="未处理" value="PENDING" />
          <el-option label="已联系" value="CONTACTED" />
          <el-option label="已处理" value="RESOLVED" />
          <el-option label="转门诊" value="REFERRED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-alert
      v-if="error"
      :title="error"
      type="error"
      :closable="false"
      show-icon
      style="margin-bottom:12px"
    />

    <el-table :data="tableData" v-loading="loading" :empty-text="EMPTY_TEXT.ALERT">
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
          <el-tag :type="statusType(row.alertStatus)" size="small">
            {{ statusLabel(row.alertStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.alertStatus === 'PENDING'" size="small" type="primary" @click="handleContact(row)">
            联系
          </el-button>
          <el-button v-if="!['RESOLVED', 'REFERRED'].includes(row.alertStatus)" size="small" @click="handleResolve(row)">
            处理
          </el-button>
          <el-button v-if="!['RESOLVED', 'REFERRED'].includes(row.alertStatus)" size="small" type="danger" @click="handleRefer(row)">
            转门诊
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      layout="total, prev, pager, next"
      @current-change="handlePageChange"
      style="margin-top:16px;justify-content:flex-end"
    />
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { getAlertList, contactAlert, resolveAlert, referAlert } from '@/api/alert'
import { useTable } from '@/composables/useTable'
import { ALERT_LEVELS, EMPTY_TEXT } from '@/constants/domain'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = reactive({ alertType: '', alertLevel: '', isResolved: '', alertStatus: '' })
const { loading, error, tableData, pagination, load, search } = useTable({
  fetcher: getAlertList
})

function queryParams() {
  const params = {}
  if (searchForm.alertType) params.alertType = searchForm.alertType
  if (searchForm.alertLevel) params.alertLevel = searchForm.alertLevel
  if (searchForm.isResolved !== '') params.isResolved = searchForm.isResolved
  if (searchForm.alertStatus) params.alertStatus = searchForm.alertStatus
  return params
}

function handlePageChange() {
  load()
}

function handleSearch() {
  search(queryParams())
}

function handleReset() {
  searchForm.alertType = ''
  searchForm.alertLevel = ''
  searchForm.isResolved = ''
  searchForm.alertStatus = ''
  search(queryParams())
}

async function handleContact(row) {
  await contactAlert(row.id)
  ElMessage.success('预警已标记为已联系')
  load()
}

async function handleResolve(row) {
  await resolveAlert(row.id)
  ElMessage.success('预警已处理')
  load()
}

async function handleRefer(row) {
  try {
    const { value } = await ElMessageBox.prompt('请输入转门诊原因', '转门诊', {
      confirmButtonText: '确认转诊',
      cancelButtonText: '取消',
      inputValidator: value => (value && value.trim() ? true : '转诊原因不能为空')
    })
    await referAlert(row.id, { referralReason: value.trim() })
    ElMessage.success('已转门诊')
    load()
  } catch {
    // user cancels or request layer shows error
  }
}

function statusLabel(status) {
  return {
    PENDING: '未处理', CONTACTED: '已联系', RESOLVED: '已处理', REFERRED: '转门诊'
  }[status] || '未处理'
}

function statusType(status) {
  return {
    PENDING: 'info', CONTACTED: 'primary', RESOLVED: 'success', REFERRED: 'danger'
  }[status] || 'info'
}

onMounted(() => load())
</script>

<style scoped>
.alert-list {
  padding: var(--layout-main-padding);
}
</style>
