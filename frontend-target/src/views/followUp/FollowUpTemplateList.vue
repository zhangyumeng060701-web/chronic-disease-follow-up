/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

<template>
  <div class="template-list">
    <div class="list-toolbar">
      <el-form :model="searchForm" inline>
        <el-form-item label="模板编码">
          <el-input v-model="searchForm.templateCode" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="handleAdd">新增模板</el-button>
    </div>

    <TableError :error="error" @retry="retry" />

    <el-table :data="tableData" v-loading="loading" empty-text="暂无随访模板">
      <el-table-column prop="templateCode" label="模板编码" width="130" />
      <el-table-column prop="templateName" label="模板名称" min-width="120" />
      <el-table-column label="风险等级" width="90">
        <template #default="{ row }">{{ riskLabel(row.riskLevel) }}</template>
      </el-table-column>
      <el-table-column prop="frequencyDays" label="频率(天)" width="90" />
      <el-table-column prop="followUpType" label="随访方式" width="90" />
      <el-table-column
        prop="defaultContent"
        label="默认内容"
        min-width="180"
        show-overflow-tooltip
      />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.isActive === 1 ? 'success' : 'info'" size="small">{{
            row.isActive === 1 ? '启用' : '停用'
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button
            size="small"
            :type="row.isActive === 1 ? 'warning' : 'success'"
            @click="handleToggle(row)"
          >
            {{ row.isActive === 1 ? '停用' : '启用' }}
          </el-button>
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="620px" @closed="resetForm">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="110px">
        <el-form-item label="模板编码" prop="templateCode">
          <el-input v-model="formData.templateCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="formData.templateName" />
        </el-form-item>
        <el-form-item label="风险等级" prop="riskLevel">
          <el-select v-model="formData.riskLevel">
            <el-option label="低风险" value="LOW" />
            <el-option label="中风险" value="MEDIUM" />
            <el-option label="高风险" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="随访频率" prop="frequencyDays">
          <el-input-number v-model="formData.frequencyDays" :min="1" :max="365" />
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
        <el-form-item label="默认内容">
          <el-input v-model="formData.defaultContent" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.isActive" :active-value="1" :inactive-value="0" />
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
import { ref, reactive, onMounted } from 'vue';
import { getTemplateList, createTemplate, updateTemplate, toggleTemplate } from '@/api/template';
import { useTable } from '@/composables/useTable';
import TableError from '@/components/TableError.vue';
import { ElMessage } from 'element-plus';

const searchForm = reactive({ templateCode: '' });
const { loading, error, tableData, pagination, load, search, retry } = useTable({
  fetcher: getTemplateList,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref(null);
const submitting = ref(false);
const isEdit = ref(false);
const editId = ref(null);

const emptyForm = () => ({
  templateCode: '',
  templateName: '',
  riskLevel: 'MEDIUM',
  frequencyDays: 14,
  followUpType: '电话',
  defaultContent: '',
  isActive: 1,
});
const formData = reactive(emptyForm());
const rules = {
  templateCode: [{ required: true, message: '请输入模板编码', trigger: 'blur' }],
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  riskLevel: [{ required: true, message: '请选择风险等级', trigger: 'change' }],
  frequencyDays: [{ required: true, message: '请输入随访频率', trigger: 'change' }],
  followUpType: [{ required: true, message: '请选择随访方式', trigger: 'change' }],
};

function queryParams() {
  return { templateCode: searchForm.templateCode || undefined };
}

function handleSearch() {
  search(queryParams());
}
function handleReset() {
  searchForm.templateCode = '';
  search(queryParams());
}
function handlePageChange() {
  load();
}

function handleAdd() {
  dialogTitle.value = '新增随访模板';
  isEdit.value = false;
  editId.value = null;
  resetForm();
  dialogVisible.value = true;
}

function handleEdit(row) {
  dialogTitle.value = '编辑随访模板';
  isEdit.value = true;
  editId.value = row.id;
  Object.assign(formData, {
    templateCode: row.templateCode,
    templateName: row.templateName,
    riskLevel: row.riskLevel,
    frequencyDays: row.frequencyDays,
    followUpType: row.followUpType,
    defaultContent: row.defaultContent || '',
    isActive: row.isActive,
  });
  dialogVisible.value = true;
}

async function handleToggle(row) {
  await toggleTemplate(row.id);
  ElMessage.success(row.isActive === 1 ? '模板已停用' : '模板已启用');
  load();
}

async function handleSubmit() {
  try {
    await formRef.value.validate();
  } catch {
    return;
  }
  if (submitting.value) return;
  submitting.value = true;
  try {
    if (isEdit.value) {
      await updateTemplate(editId.value, { ...formData });
      ElMessage.success('编辑成功');
    } else {
      await createTemplate({ ...formData });
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
  formRef.value?.resetFields();
}

function riskLabel(level) {
  return { LOW: '低风险', MEDIUM: '中风险', HIGH: '高风险' }[level] || level;
}

onMounted(() => load());
</script>

<style scoped>
.template-list {
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
