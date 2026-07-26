<template>
  <div class="followup-list">
    <el-form :model="searchForm" inline>
      <el-form-item label="患者">
        <el-select v-model="searchForm.patientId" placeholder="全部" clearable filterable>
          <el-option v-for="p in patientOptions" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="随访日期">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
          start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-button type="primary" @click="handleAdd">新增随访</el-button>

    <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="followUpDate" label="随访日期" width="110" />
      <el-table-column prop="followUpType" label="随访方式" width="80">
        <template #default="{ row }">
          <el-tag size="small">{{ row.followUpType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="血压" width="110">
        <template #default="{ row }">
          <span v-if="row.systolicBp || row.diastolicBp">{{ row.systolicBp || '-' }}/{{ row.diastolicBp || '-' }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="血糖" width="130">
        <template #default="{ row }">
          <span v-if="row.fastingGlucose">{{ row.fastingGlucose }}</span>
          <span v-else>-</span>
          <span v-if="row.postprandialGlucose"> / {{ row.postprandialGlucose }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="medicationAdherence" label="用药依从性" width="90">
        <template #default="{ row }">
          <el-tag v-if="row.medicationAdherence==='规律'" type="success" size="small">规律</el-tag>
          <el-tag v-else-if="row.medicationAdherence==='间断'" type="warning" size="small">间断</el-tag>
          <el-tag v-else-if="row.medicationAdherence==='不服药'" type="danger" size="small">不服药</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="nextFollowUpDate" label="下次随访" width="110">
        <template #default="{ row }">{{ row.nextFollowUpDate || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="650px" @closed="resetForm">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="110px">
        <el-form-item label="患者" prop="patientId">
          <el-select v-model="formData.patientId" placeholder="请选择患者" filterable :disabled="isEdit">
            <el-option v-for="p in patientOptions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="随访日期" prop="followUpDate">
          <el-date-picker v-model="formData.followUpDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="随访方式" prop="followUpType">
          <el-select v-model="formData.followUpType">
            <el-option label="门诊" value="门诊" />
            <el-option label="电话" value="电话" />
            <el-option label="上门" value="上门" />
          </el-select>
        </el-form-item>
        <el-form-item label="收缩压 (mmHg)">
          <el-input-number v-model="formData.systolicBp" :min="0" :max="300" placeholder="可不填" />
        </el-form-item>
        <el-form-item label="舒张压 (mmHg)">
          <el-input-number v-model="formData.diastolicBp" :min="0" :max="200" placeholder="可不填" />
        </el-form-item>
        <el-form-item label="空腹血糖 (mmol/L)">
          <el-input-number v-model="formData.fastingGlucose" :precision="1" :step="0.1" :min="0" :max="50" placeholder="可不填" />
        </el-form-item>
        <el-form-item label="餐后血糖 (mmol/L)">
          <el-input-number v-model="formData.postprandialGlucose" :precision="1" :step="0.1" :min="0" :max="50" placeholder="可不填" />
        </el-form-item>
        <el-form-item label="用药依从性">
          <el-select v-model="formData.medicationAdherence" placeholder="请选择" clearable>
            <el-option label="规律" value="规律" />
            <el-option label="间断" value="间断" />
            <el-option label="不服药" value="不服药" />
          </el-select>
        </el-form-item>
        <el-form-item label="症状描述">
          <el-input v-model="formData.symptoms" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="随访建议">
          <el-input v-model="formData.advice" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="下次随访日期">
          <el-date-picker v-model="formData.nextFollowUpDate" type="date" placeholder="可不填" value-format="YYYY-MM-DD" />
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
import { getFollowUpList, addFollowUp, updateFollowUp, deleteFollowUp } from '@/api/followUp'
import { getPatientList } from '@/api/patient'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = reactive({ patientId: '' })
const dateRange = ref([])
const patientOptions = ref([])
const tableData = ref([])
const loading = ref(false)
const pagination = reactive({ page: 1, size: 20, total: 0 })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const submitting = ref(false)
const isEdit = ref(false)
const editId = ref(null)

const emptyForm = () => ({
  patientId: '', followUpDate: '', followUpType: '',
  systolicBp: null, diastolicBp: null, fastingGlucose: null, postprandialGlucose: null,
  medicationAdherence: '', symptoms: '', advice: '', nextFollowUpDate: ''
})
const formData = reactive(emptyForm())
const rules = {
  patientId: [{ required: true, message: '请选择患者', trigger: 'change' }],
  followUpDate: [{ required: true, message: '请选择随访日期', trigger: 'change' }],
  followUpType: [{ required: true, message: '请选择随访方式', trigger: 'change' }]
}

async function fetchPatients() {
  try {
    const res = await getPatientList({ page: 1, size: 999 })
    patientOptions.value = res.data.records || []
  } catch { /* ignore */ }
}

async function fetchData() {
  loading.value = true
  try {
    const params = { page: pagination.page, size: pagination.size }
    if (searchForm.patientId) params.patientId = searchForm.patientId
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getFollowUpList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handleReset() { searchForm.patientId = ''; dateRange.value = []; handleSearch() }

function handleAdd() {
  dialogTitle.value = '新增随访'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑随访'
  isEdit.value = true
  editId.value = row.id
  Object.assign(formData, {
    patientId: row.patientId, followUpDate: row.followUpDate, followUpType: row.followUpType,
    systolicBp: row.systolicBp, diastolicBp: row.diastolicBp,
    fastingGlucose: row.fastingGlucose, postprandialGlucose: row.postprandialGlucose,
    medicationAdherence: row.medicationAdherence, symptoms: row.symptoms, advice: row.advice,
    nextFollowUpDate: row.nextFollowUpDate
  })
  dialogVisible.value = true
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该随访记录吗？', '提示', { type: 'warning' })
  await deleteFollowUp(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    const payload = { ...formData }
    if (isEdit.value) {
      await updateFollowUp(editId.value, payload)
    } else {
      await addFollowUp(payload)
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

function resetForm() { Object.assign(formData, emptyForm()); formRef.value?.resetFields() }

onMounted(() => { fetchPatients(); fetchData() })
</script>
