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

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="username" label="操作人" width="100" />
      <el-table-column prop="operation" label="操作类型" width="140" />
      <el-table-column prop="targetType" label="对象类型" width="100" />
      <el-table-column prop="targetId" label="对象ID" width="80" />
      <el-table-column prop="ipAddress" label="IP地址" width="130" />
      <el-table-column prop="createTime" label="操作时间" width="160" />
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
import { getLogList } from '@/api/log'

const searchForm = reactive({ username: '', operation: '' })
const tableData = ref([])
const loading = ref(false)
const pagination = reactive({ page: 1, size: 20, total: 0 })

async function fetchData() {
  loading.value = true
  try {
    const res = await getLogList({
      page: pagination.page, size: pagination.size,
      username: searchForm.username || undefined,
      operation: searchForm.operation || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

function handleSearch() { pagination.page = 1; fetchData() }
function handleReset() { searchForm.username = ''; searchForm.operation = ''; handleSearch() }

onMounted(() => fetchData())
</script>
