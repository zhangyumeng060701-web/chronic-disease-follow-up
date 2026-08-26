<template>
  <div class="task-list">
    <div class="list-toolbar">
      <el-form :model="searchForm" inline>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="待处理" value="PENDING" />
            <el-option label="进行中" value="IN_PROGRESS" />
            <el-option label="已联系" value="CONTACTED" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon style="margin-bottom:12px" />

    <el-table :data="tableData" v-loading="loading" empty-text="暂无随访任务">
      <el-table-column prop="patientName" label="患者" min-width="100" />
      <el-table-column prop="taskType" label="任务类型" width="110">
        <template #default="{ row }">{{ row.taskType === 'FOLLOW_UP' ? '随访任务' : '预警升级' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="channel" label="渠道" width="90" />
      <el-table-column prop="ownerName" label="责任人" width="100" />
      <el-table-column prop="dueDate" label="截止日期" width="110" />
      <el-table-column prop="completedTime" label="完成时间" width="160" />
      <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="!['COMPLETED', 'CANCELED'].includes(row.status)"
            size="small"
            type="primary"
            @click="handleComplete(row)"
          >完成</el-button>
          <el-button
            v-if="!['COMPLETED', 'CANCELED'].includes(row.status)"
            size="small"
            @click="handleCancel(row)"
          >取消</el-button>
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
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { getTaskList, completeTask, cancelTask } from '@/api/followUpTask'
import { useTable } from '@/composables/useTable'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = reactive({ status: '' })
const { loading, error, tableData, pagination, load, search } = useTable({ fetcher: getTaskList })

function queryParams() {
  return { status: searchForm.status || undefined }
}

function handleSearch() { search(queryParams()) }
function handleReset() { searchForm.status = ''; search(queryParams()) }
function handlePageChange() { load() }

async function handleComplete(row) {
  try {
    await ElMessageBox.confirm('确认完成该随访任务吗？', '提示', { type: 'warning' })
    await completeTask(row.id)
    ElMessage.success('任务已完成')
    load()
  } catch {
    // user cancels or request layer shows error
  }
}

async function handleCancel(row) {
  try {
    await ElMessageBox.confirm('确认取消该随访任务吗？', '提示', { type: 'warning' })
    await cancelTask(row.id)
    ElMessage.success('任务已取消')
    load()
  } catch {
    // user cancels or request layer shows error
  }
}

function statusLabel(status) {
  return {
    PENDING: '待处理', IN_PROGRESS: '进行中', CONTACTED: '已联系',
    COMPLETED: '已完成', CANCELED: '已取消'
  }[status] || status
}

function statusType(status) {
  return {
    PENDING: 'info', IN_PROGRESS: 'warning', CONTACTED: 'primary',
    COMPLETED: 'success', CANCELED: 'info'
  }[status] || 'info'
}
</script>

<style scoped>
.task-list {
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
