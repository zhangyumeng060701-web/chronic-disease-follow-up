<template>
  <div class="patient-list">
    <el-form :model="searchForm" inline>
      <el-form-item label="姓名">
        <el-input v-model="searchForm.name" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="慢病类型">
        <el-select v-model="searchForm.diseaseType" placeholder="请选择" clearable>
          <el-option label="高血压" value="HYPERTENSION" />
          <el-option label="糖尿病" value="DIABETES" />
          <el-option label="两者皆有" value="BOTH" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-button type="primary" @click="handleAdd">新增患者</el-button>

    <el-table :data="tableData" border stripe v-loading="loading" style="margin-top:16px">
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="60" />
      <el-table-column prop="age" label="年龄" width="60" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="diseaseType" label="慢病类型" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.diseaseType==='HYPERTENSION'" type="primary">高血压</el-tag>
          <el-tag v-else-if="row.diseaseType==='DIABETES'" type="success">糖尿病</el-tag>
          <el-tag v-else type="warning">两者皆有</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @closed="resetForm">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="formData.gender">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="formData.age" :min="0" :max="150" />
        </el-form-item>
        <el-form-item label="慢病类型" prop="diseaseType">
          <el-select v-model="formData.diseaseType">
            <el-option label="高血压" value="HYPERTENSION" />
            <el-option label="糖尿病" value="DIABETES" />
            <el-option label="两者皆有" value="BOTH" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="formData.idCard" />
        </el-form-item>
        <el-form-item label="住址">
          <el-input v-model="formData.address" />
        </el-form-item>
        <el-form-item label="病史">
          <el-input v-model="formData.medicalHistory" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="用药信息">
          <el-input v-model="formData.medicationInfo" type="textarea" :rows="2" />
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
import { getPatientList, addPatient, updatePatient, deletePatient } from '@/api/patient'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = reactive({ name: '', diseaseType: '' })
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
  name: '', gender: '', age: null, diseaseType: '',
  phone: '', idCard: '', address: '', medicalHistory: '', medicationInfo: ''
})
const formData = reactive(emptyForm())
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  diseaseType: [{ required: true, message: '请选择慢病类型', trigger: 'change' }]
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getPatientList({
      page: pagination.page, size: pagination.size,
      name: searchForm.name || undefined,
      diseaseType: searchForm.diseaseType || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handleReset() { searchForm.name = ''; searchForm.diseaseType = ''; handleSearch() }

function handleAdd() {
  dialogTitle.value = '新增患者'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑患者'
  isEdit.value = true
  editId.value = row.id
  Object.assign(formData, row)
  dialogVisible.value = true
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该患者吗？', '提示', { type: 'warning' })
  await deletePatient(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updatePatient(editId.value, formData)
    } else {
      await addPatient(formData)
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } finally { submitting.value = false }
}

function resetForm() { Object.assign(formData, emptyForm()); formRef.value?.resetFields() }

onMounted(() => fetchData())
</script>