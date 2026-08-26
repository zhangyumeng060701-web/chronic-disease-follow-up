<template>
  <div class="message-list">
    <div class="list-toolbar">
      <el-form :model="searchForm" inline>
        <el-form-item label="渠道">
          <el-select v-model="searchForm.channel" placeholder="全部" clearable>
            <el-option label="站内信" value="IN_APP" />
            <el-option label="短信" value="SMS" />
            <el-option label="微信" value="WECHAT" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="已发送" value="SENT" />
            <el-option label="已读" value="READ" />
            <el-option label="待发送" value="PENDING" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="dialogVisible = true">发送消息</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" empty-text="暂无消息">
      <el-table-column prop="recipientType" label="接收方" width="90" />
      <el-table-column prop="recipientId" label="接收方ID" width="90" />
      <el-table-column prop="channel" label="渠道" width="90" />
      <el-table-column prop="title" label="标题" min-width="140" />
      <el-table-column prop="content" label="内容" min-width="220" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="90" />
      <el-table-column prop="createTime" label="发送时间" width="160" />
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

    <el-dialog v-model="dialogVisible" title="发送消息" width="520px" @closed="resetForm">
      <el-form :model="formData" ref="formRef" :rules="rules" label-width="90px">
        <el-form-item label="接收方" prop="recipientType">
          <el-select v-model="formData.recipientType">
            <el-option label="患者" value="PATIENT" />
            <el-option label="医生" value="DOCTOR" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="接收方ID" prop="recipientId">
          <el-input-number v-model="formData.recipientId" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="渠道" prop="channel">
          <el-select v-model="formData.channel">
            <el-option label="站内信" value="IN_APP" />
            <el-option label="短信" value="SMS" />
            <el-option label="微信模板消息" value="WECHAT" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="模板编码">
          <el-input v-model="formData.templateCode" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMessageList, createMessage } from '@/api/message'
import { useTable } from '@/composables/useTable'
import { ElMessage } from 'element-plus'

const searchForm = reactive({ channel: '', status: '' })
const { loading, tableData, pagination, load, search } = useTable({ fetcher: getMessageList })

const dialogVisible = ref(false)
const formRef = ref(null)
const submitting = ref(false)
const emptyForm = () => ({
  recipientType: 'PATIENT', recipientId: 1, channel: 'IN_APP',
  title: '', content: '', templateCode: ''
})
const formData = reactive(emptyForm())
const rules = {
  recipientType: [{ required: true, message: '请选择接收方', trigger: 'change' }],
  recipientId: [{ required: true, message: '请输入接收方ID', trigger: 'change' }],
  channel: [{ required: true, message: '请选择渠道', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

function queryParams() {
  return {
    channel: searchForm.channel || undefined,
    status: searchForm.status || undefined
  }
}

function handleSearch() { search(queryParams()) }
function handleReset() { searchForm.channel = ''; searchForm.status = ''; search(queryParams()) }
function handlePageChange() { load() }

async function handleSubmit() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  if (submitting.value) return
  submitting.value = true
  try {
    await createMessage({ ...formData })
    ElMessage.success('消息已发送')
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

onMounted(() => load())
</script>

<style scoped>
.message-list {
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
