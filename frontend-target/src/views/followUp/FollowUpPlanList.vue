<template>
  <div class="plan-list">
    <div class="list-toolbar">
      <el-form :model="searchForm" inline>
        <el-form-item label="风险等级">
          <el-select v-model="searchForm.riskLevel" placeholder="全部" clearable>
            <el-option label="低风险" value="LOW" />
            <el-option label="中风险" value="MEDIUM" />
            <el-option label="高风险" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="进行中" value="ACTIVE" />
            <el-option label="已暂停" value="PAUSED" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="handleAdd">新增计划</el-button>
    </div>

    <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon style="margin-bottom:12px" />

    <el-table :data="tableData" v-loading="loading" empty-text="暂无随访计划">
      <el-table-column prop="patientName" label="患者" min-width="100" />
      <el-table-column label="风险等级" width="90">
        <template #default="{ row }">
          <el-tag :type="riskType(row.riskLevel)" size="small">{{ riskLabel(row.riskLevel) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="followUpFrequencyDays" label="频率(天)" width="90" />
      <el-table-column prop="followUpType" label="随访方式" width="90" />
      <el-table-column prop="nextFollowUpDate" label="下次日期" width="110" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="doctorName" label="责任人" width="100" />
      <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="primary" plain @click="handleAssess(row)">评估</el-button>
          <el-button size="small" type="success" plain @click="handleSuggest(row)">AI建议</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="pagination.total > 0"
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      layout="total, prev, pager, next"
      @current-change="handlePageChange"
      style="margin-top:16px;justify-content:flex-end"
    />

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="560px" @closed="resetForm">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="110px">
        <el-form-item label="患者" prop="patientId">
          <el-select v-model="formData.patientId" placeholder="请选择患者" filterable :disabled="isEdit">
            <el-option v-for="p in patientOptions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="风险等级" prop="riskLevel">
          <el-select v-model="formData.riskLevel">
            <el-option label="低风险" value="LOW" />
            <el-option label="中风险" value="MEDIUM" />
            <el-option label="高风险" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="随访频率" prop="followUpFrequencyDays">
          <el-input-number v-model="formData.followUpFrequencyDays" :min="1" :max="365" />
          <span class="field-unit">天</span>
        </el-form-item>
        <el-form-item label="随访方式" prop="followUpType">
          <el-select v-model="formData.followUpType">
            <el-option label="门诊" value="门诊" />
            <el-option label="电话" value="电话" />
            <el-option label="上门" value="上门" />
            <el-option label="微信" value="微信" />
          </el-select>
        </el-form-item>
        <el-form-item label="下次随访日期" prop="nextFollowUpDate">
          <el-date-picker v-model="formData.nextFollowUpDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formData.status">
            <el-option label="进行中" value="ACTIVE" />
            <el-option label="已暂停" value="PAUSED" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPlanList, createPlan, updatePlan, deletePlan } from '@/api/plan'
import { assessPatientRisk, generateSuggestion } from '@/api/clinical'
import { getPatientList } from '@/api/patient'
import { useTable } from '@/composables/useTable'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = reactive({ riskLevel: '', status: '' })
const patientOptions = ref([])
const { loading, error, tableData, pagination, load, search } = useTable({ fetcher: getPlanList })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const submitting = ref(false)
const isEdit = ref(false)
const editId = ref(null)

const emptyForm = () => ({
  patientId: '', riskLevel: 'MEDIUM', followUpFrequencyDays: 14,
  followUpType: '电话', nextFollowUpDate: '', status: 'ACTIVE', remark: ''
})
const formData = reactive(emptyForm())
const rules = {
  patientId: [{ required: true, message: '请选择患者', trigger: 'change' }],
  riskLevel: [{ required: true, message: '请选择风险等级', trigger: 'change' }],
  followUpFrequencyDays: [{ required: true, message: '请输入随访频率', trigger: 'change' }],
  followUpType: [{ required: true, message: '请选择随访方式', trigger: 'change' }],
  nextFollowUpDate: [{ required: true, message: '请选择下次随访日期', trigger: 'change' }]
}

async function fetchPatients() {
  try {
    const res = await getPatientList({ page: 1, size: 999 })
    patientOptions.value = res.data.records || []
  } catch {
    ElMessage.error('加载患者列表失败')
  }
}

function queryParams() {
  return {
    riskLevel: searchForm.riskLevel || undefined,
    status: searchForm.status || undefined
  }
}

function handleSearch() { search(queryParams()) }
function handleReset() { searchForm.riskLevel = ''; searchForm.status = ''; search(queryParams()) }
function handlePageChange() { load() }

function handleAdd() {
  dialogTitle.value = '新增随访计划'
  isEdit.value = false
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑随访计划'
  isEdit.value = true
  editId.value = row.id
  Object.assign(formData, {
    patientId: row.patientId, riskLevel: row.riskLevel,
    followUpFrequencyDays: row.followUpFrequencyDays, followUpType: row.followUpType,
    nextFollowUpDate: row.nextFollowUpDate, status: row.status, remark: row.remark || ''
  })
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('删除计划会取消未完成任务，确认继续吗？', '提示', { type: 'warning' })
    await deletePlan(row.id)
    ElMessage.success('删除成功')
    load()
  } catch {
    // user cancels or request layer shows error
  }
}

async function handleAssess(row) {
  await assessPatientRisk(row.patientId)
  ElMessage.success('风险分层已完成')
  load()
}

async function handleSuggest(row) {
  await generateSuggestion(row.patientId)
  ElMessage.success('AI建议已生成，请在AI随访建议中确认')
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  if (submitting.value) return
  submitting.value = true
  try {
    const payload = { ...formData }
    if (isEdit.value) {
      await updatePlan(editId.value, payload)
      ElMessage.success('编辑成功')
    } else {
      await createPlan(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    load()
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  Object.assign(formData, emptyForm())
  formRef.value?.resetFields()
}

function riskLabel(level) {
  return { LOW: '低风险', MEDIUM: '中风险', HIGH: '高风险' }[level] || level
}

function riskType(level) {
  return { LOW: 'success', MEDIUM: 'warning', HIGH: 'danger' }[level] || 'info'
}

function statusLabel(status) {
  return { ACTIVE: '进行中', PAUSED: '已暂停', COMPLETED: '已完成' }[status] || status
}

onMounted(() => { fetchPatients(); load() })
</script>

<style scoped>
.plan-list {
  padding: var(--layout-main-padding);
}

.list-toolbar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.field-unit {
  margin-left: 8px;
  color: var(--color-text-secondary);
}

@media (max-width: 768px) {
  .list-toolbar {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
