/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

<template>
  <div class="suggestion-list">
    <div class="list-toolbar">
      <el-form :model="searchForm" inline>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待确认" value="PENDING" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <TableError :error="error" @retry="retry" />

    <el-table :data="tableData" v-loading="loading" empty-text="暂无AI随访建议">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="patientId" label="患者ID" width="90" />
      <el-table-column prop="riskLevel" label="风险" width="90" />
      <el-table-column prop="confidence" label="置信度" width="80" />
      <el-table-column prop="content" label="建议内容" min-width="260" show-overflow-tooltip />
      <el-table-column prop="evidence" label="判定依据" min-width="180" show-overflow-tooltip />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="doctorId" label="确认医生" width="90" />
      <el-table-column prop="confirmTime" label="确认时间" width="160" />
      <el-table-column prop="createTime" label="生成时间" width="160" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 'PENDING'">
            <el-button size="small" type="primary" @click="handleConfirm(row)">确认落库</el-button>
            <el-button size="small" @click="handleReject(row)">驳回</el-button>
          </template>
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
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import { getSuggestionList, confirmSuggestion, rejectSuggestion } from '@/api/clinical';
import { useTable } from '@/composables/useTable';
import TableError from '@/components/TableError.vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const searchForm = reactive({ status: 'PENDING' });
const { loading, error, tableData, pagination, load, search, retry } = useTable({
  fetcher: getSuggestionList,
});

function queryParams() {
  return { status: searchForm.status || undefined };
}

function handleSearch() {
  search(queryParams());
}
function handleReset() {
  searchForm.status = 'PENDING';
  search(queryParams());
}
function handlePageChange() {
  load();
}

async function handleConfirm(row) {
  try {
    await ElMessageBox.confirm('确认后建议将写入随访记录，是否继续？', '提示', { type: 'warning' });
    await confirmSuggestion(row.id);
    ElMessage.success('建议已落库');
    load();
  } catch {
    // user cancels or request layer shows error
  }
}

async function handleReject(row) {
  try {
    await ElMessageBox.confirm('确认驳回该AI建议吗？', '提示', { type: 'warning' });
    await rejectSuggestion(row.id);
    ElMessage.success('建议已驳回');
    load();
  } catch {
    // user cancels or request layer shows error
  }
}

function statusLabel(status) {
  return { PENDING: '待确认', CONFIRMED: '已确认', REJECTED: '已驳回' }[status] || status;
}

function statusType(status) {
  return { PENDING: 'warning', CONFIRMED: 'success', REJECTED: 'info' }[status] || 'info';
}

onMounted(() => load());
</script>

<style scoped>
.suggestion-list {
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
