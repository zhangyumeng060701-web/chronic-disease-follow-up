<template>
  <div class="oper-log">
    <el-form :model="searchForm" inline>
      <el-form-item label="操作人">
        <el-input v-model="searchForm.username" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="操作类型">
        <el-input v-model="searchForm.operation" placeholder="请输入" clearable />
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

    <el-table
      :data="tableData"
      v-loading="loading"
      :empty-text="EMPTY_TEXT.LOG"
      style="margin-top:16px"
    >
      <el-table-column prop="username" label="操作人" width="100" />
      <el-table-column prop="operation" label="操作类型" width="140" />
      <el-table-column prop="targetType" label="对象类型" width="100" />
      <el-table-column prop="targetId" label="对象ID" width="80" />
      <el-table-column prop="ipAddress" label="IP地址" width="130" />
      <el-table-column prop="createTime" label="操作时间" width="160" />
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
import { ref, reactive, onMounted } from 'vue'
import { EMPTY_TEXT } from '@/constants/domain'
import { getLogList } from '@/api/log'
import { useTable } from '@/composables/useTable'

const searchForm = reactive({ username: '', operation: '' })
const { loading, error, tableData, pagination, load, search } = useTable({
  fetcher: getLogList
})

function queryParams() {
  return {
    username: searchForm.username || undefined,
    operation: searchForm.operation || undefined
  }
}

function handlePageChange() {
  load()
}

function handleSearch() { search(queryParams()) }
function handleReset() { searchForm.username = ''; searchForm.operation = ''; search(queryParams()) }

onMounted(() => load())
</script>

<style scoped>
.oper-log {
  padding: var(--layout-main-padding);
}
</style>
