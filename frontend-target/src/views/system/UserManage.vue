<template>
  <div class="user-manage">
    <el-form :model="searchForm" inline>
      <el-form-item label="用户名">
        <el-input v-model="searchForm.username" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="searchForm.role" placeholder="全部" clearable>
          <el-option label="管理员" value="ADMIN" />
          <el-option label="医生" value="DOCTOR" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-button type="primary" @click="handleAdd">新增用户</el-button>

    <el-alert
      v-if="error"
      :title="error"
      type="error"
      :closable="false"
      show-icon
      style="margin-top:12px"
    />

    <el-table
      :data="tableData"
      border
      stripe
      v-loading="loading"
      :empty-text="EMPTY_TEXT.USER"
      style="margin-top:16px"
    >
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="100" />
      <el-table-column prop="role" label="角色" width="80">
        <template #default="{ row }">
          <el-tag :type="row.role==='ADMIN'?'danger':'primary'" size="small">
            {{ ROLES[row.role] || row.role }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status===STATUS.ACTIVE?'success':'info'" size="small">
            {{ row.status === STATUS.ACTIVE ? '正常' : '已禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button
            size="small"
            :type="row.status===1?'warning':'success'"
            @click="handleToggle(row)"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
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
      style="margin-top:16px;justify-content:flex-end"
    />

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @closed="resetForm">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit?'':'password'">
          <el-input
            v-model="formData.password"
            type="password"
            show-password
            :placeholder="isEdit ? '留空则不修改' : ''"
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="医生" value="DOCTOR" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" />
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
import { getUserList, createUser, updateUser, toggleUserStatus } from '@/api/user'
import { EMPTY_TEXT, ROLES, STATUS } from '@/constants/domain'
import { useTable } from '@/composables/useTable'
import { ElMessage } from 'element-plus'

const searchForm = reactive({ username: '', role: '' })
const { loading, error, tableData, pagination, load, search } = useTable({
  fetcher: getUserList
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const submitting = ref(false)
const isEdit = ref(false)
const editId = ref(null)

const emptyForm = () => ({ username: '', password: '', realName: '', role: '', phone: '' })
const formData = reactive(emptyForm())
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

function queryParams() {
  return {
    username: searchForm.username || undefined,
    role: searchForm.role || undefined
  }
}

function handlePageChange() {
  load()
}

function handleSearch() { search(queryParams()) }
function handleReset() { searchForm.username = ''; searchForm.role = ''; search(queryParams()) }

function handleAdd() {
  dialogTitle.value = '新增用户'
  isEdit.value = false
  editId.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑用户'
  isEdit.value = true
  editId.value = row.id
  Object.assign(formData, {
    username: row.username,
    password: '',
    realName: row.realName,
    role: row.role,
    phone: row.phone || ''
  })
  dialogVisible.value = true
}

async function handleToggle(row) {
  try {
    await toggleUserStatus(row.id)
    ElMessage.success(row.status === 1 ? '已禁用' : '已启用')
    load()
  } catch {
    ElMessage.error('操作失败，请稍后重试')
  }
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateUser(editId.value, { ...formData })
      ElMessage.success('编辑成功')
    } else {
      await createUser({ ...formData })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    load()
  } catch {
    ElMessage.error('保存失败，请稍后重试')
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
.user-manage {
  padding: 16px;
}
</style>
