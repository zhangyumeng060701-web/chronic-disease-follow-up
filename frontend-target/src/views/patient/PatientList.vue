<template>
  <div class="patient-list">
    <div class="list-toolbar">
      <PatientSearchBar v-model="searchForm" @search="handleSearch" @reset="handleReset" />
      <el-button type="primary" @click="handleAdd">新增患者</el-button>
    </div>

    <TableError :error="error" @retry="retry" style="margin-top: 12px" />

    <el-table
      :data="tableData"
      v-loading="loading"
      :empty-text="EMPTY_TEXT.PATIENT"
      style="margin-top: 16px"
    >
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="60" />
      <el-table-column prop="age" label="年龄" width="60" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="diseaseType" label="慢病类型" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.diseaseType === 'HYPERTENSION'" type="primary">{{
            DISEASE_TYPES.HYPERTENSION
          }}</el-tag>
          <el-tag v-else-if="row.diseaseType === 'DIABETES'" type="success">{{
            DISEASE_TYPES.DIABETES
          }}</el-tag>
          <el-tag v-else-if="row.diseaseType === 'BOTH'" type="warning">{{
            DISEASE_TYPES.BOTH
          }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="最近随访" width="110">
        <template #default="{ row }">{{ row.lastFollowUpDate || '-' }}</template>
      </el-table-column>
      <el-table-column label="责任医生" width="100">
        <template #default="{ row }">{{ row.doctorName || row.doctorId || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === STATUS.ACTIVE ? 'success' : 'info'" size="small">
            {{ row.status === STATUS.ACTIVE ? '正常' : '已删除' }}
          </el-tag>
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
      v-if="pagination.total > 0"
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      layout="total, prev, pager, next"
      @current-change="handlePageChange"
      style="margin-top: 16px; justify-content: flex-end"
    />

    <PatientFormDialog
      ref="dialogRef"
      v-model:visible="dialogVisible"
      :title="dialogTitle"
      :form="formData"
      :rules="rules"
      :submitting="submitting"
      @submit="handleSubmit"
      @closed="resetForm"
    />
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { getPatientList, addPatient, updatePatient, deletePatient } from '@/api/patient';
import PatientSearchBar from '@/components/PatientSearchBar.vue';
import PatientFormDialog from '@/components/PatientFormDialog.vue';
import TableError from '@/components/TableError.vue';
import { useTable } from '@/composables/useTable';
import { toPatientPayload } from '@/utils/patientPayload';
import { DISEASE_TYPES, EMPTY_TEXT, STATUS } from '@/constants/domain';
import { ElMessage, ElMessageBox } from 'element-plus';

const searchForm = reactive({ name: '', diseaseType: '' });
const { loading, error, tableData, pagination, load, search, retry } = useTable({
  fetcher: getPatientList,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogRef = ref(null);
const submitting = ref(false);
const isEdit = ref(false);
const editId = ref(null);

const emptyForm = () => ({
  name: '',
  gender: '',
  age: null,
  diseaseType: '',
  phone: '',
  idCard: '',
  address: '',
  medicalHistory: '',
  medicationInfo: '',
  heightCm: null,
  weightKg: null,
  smoking: '',
  drinking: '',
  allergyHistory: '',
  medicationHistory: '',
});
const formData = reactive(emptyForm());
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  diseaseType: [{ required: true, message: '请选择慢病类型', trigger: 'change' }],
};

function queryParams() {
  return {
    name: searchForm.name || undefined,
    diseaseType: searchForm.diseaseType || undefined,
  };
}

function handleSearch() {
  search(queryParams());
}

function handlePageChange() {
  load();
}

function handleReset() {
  search(queryParams());
}

function handleAdd() {
  dialogTitle.value = '新增患者';
  isEdit.value = false;
  editId.value = null;
  resetForm();
  dialogVisible.value = true;
}

function handleEdit(row) {
  dialogTitle.value = '编辑患者';
  isEdit.value = true;
  editId.value = row.id;
  Object.assign(formData, emptyForm(), row);
  dialogVisible.value = true;
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除该患者吗？', '提示', { type: 'warning' });
    await deletePatient(row.id);
    ElMessage.success('删除成功');
    load();
  } catch {
    // 用户取消或删除失败，错误提示由请求层统一处理
  }
}

async function handleSubmit() {
  try {
    await dialogRef.value?.formRef?.validate();
  } catch {
    return;
  }
  if (submitting.value) return;
  submitting.value = true;
  try {
    const payload = toPatientPayload({ ...formData });
    if (isEdit.value) {
      await updatePatient(editId.value, payload);
      ElMessage.success('编辑成功');
    } else {
      await addPatient(payload);
      ElMessage.success('新增成功');
    }
    dialogVisible.value = false;
    load();
  } finally {
    submitting.value = false;
  }
}

function resetForm() {
  Object.assign(formData, emptyForm());
}

onMounted(() => load());
</script>

<style scoped>
.patient-list {
  padding: var(--layout-main-padding);
}

.list-toolbar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

@media (max-width: 768px) {
  .list-toolbar {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
