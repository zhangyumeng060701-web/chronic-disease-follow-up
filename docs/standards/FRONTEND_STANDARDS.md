# 前端开发标准（Vue 3 + Element Plus）

> 前端 B 和 C 必读 | 不按这个规范写的代码，PR 不给过

---

## 一、项目结构

```
frontend/src/
├── api/                    # 所有接口调用都放这里
│   ├── request.js          # Axios 封装（统一加 token、统一错误处理）
│   ├── patient.js          # 患者相关接口
│   ├── followUp.js         # 随访相关接口
│   ├── alert.js            # 预警相关接口
│   ├── stats.js            # 统计相关接口
│   └── user.js             # 用户管理接口
├── views/                  # 页面组件（一个文件 = 一个页面）
│   ├── login/
│   │   └── LoginView.vue   # 登录页
│   ├── patient/
│   │   └── PatientList.vue # 患者管理页
│   ├── followUp/
│   │   └── FollowUpList.vue# 随访记录页
│   ├── alert/
│   │   └── AlertList.vue   # 预警管理页
│   ├── dashboard/
│   │   └── Dashboard.vue   # 统计看板页
│   └── system/
│       ├── UserManage.vue  # 用户管理页
│       └── OperLog.vue     # 操作日志页
├── components/             # 可复用的组件
│   ├── AppLayout.vue       # 全局布局（侧边栏+顶栏+内容区）
│   ├── Pagination.vue      # 分页组件（如果不用 Element Plus 自带的）
│   └── Desensitize.vue     # 数据脱敏显示组件
├── router/
│   └── index.js            # 路由配置
├── store/
│   └── user.js             # Pinia 状态管理（存登录用户信息）
├── utils/
│   └── auth.js             # token 读写工具函数
├── App.vue
└── main.js
```

**规则**：
- `views/` 下面按功能模块建子目录，每个页面一个 `.vue` 文件
- `api/` 下面按功能模块建 `.js` 文件，接口调用全部收敛在这里，页面里不直接写 `axios.get(...)`
- `components/` 只放被多个页面复用的组件

---

## 二、命名规范

| 东西 | 规范 | 示例 |
|---|---|---|
| 文件夹 | 小驼峰 | `followUp` |
| .vue 文件 | 大驼峰 | `PatientList.vue` |
| .js 文件 | 小驼峰 | `request.js` |
| CSS class | 短横线 | `.search-bar` |
| 变量/方法 | 小驼峰 | `patientList`, `fetchData()` |
| 常量 | 全大写+下划线 | `PAGE_SIZE` |

---

## 三、Axios 封装（api/request.js）

这个文件是全局唯一的 Axios 实例，所有请求都经过它。**不要在每个页面里单独写 `axios.get`。**

```js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',          // 所有请求自动加 /api 前缀
  timeout: 10000            // 10 秒超时
})

// 请求拦截器：自动带上 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：统一错误处理
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }
    ElMessage.error('网络错误，请稍后重试')
    return Promise.reject(error)
  }
)

export default request
```

---

## 四、接口调用文件示例（api/patient.js）

```js
import request from './request'

// 分页查询患者列表
export function getPatientList(params) {
  return request.get('/patients', { params })
}

// 获取患者详情
export function getPatientById(id) {
  return request.get(`/patients/${id}`)
}

// 新增患者
export function addPatient(data) {
  return request.post('/patients', data)
}

// 编辑患者
export function updatePatient(id, data) {
  return request.put(`/patients/${id}`, data)
}

// 删除患者
export function deletePatient(id) {
  return request.delete(`/patients/${id}`)
}
```

**规则**：接口函数名用 `getXxx`、`addXxx`、`updateXxx`、`deleteXxx`，一眼能看出做什么。

---

## 五、页面组件模板

每个页面的标准结构如下（以患者列表页为例）：

```vue
<template>
  <div class="patient-list">
    <!-- 搜索栏 -->
    <el-form :model="searchForm" inline>
      <el-form-item label="姓名">
        <el-input v-model="searchForm.name" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="慢病类型">
        <el-select v-model="searchForm.diseaseType" placeholder="请选择" clearable>
          <el-option label="高血压" value="HYPERTENSION" />
          <el-option label="糖尿病" value="DIABETES" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-button type="primary" @click="handleAdd">新增患者</el-button>

    <!-- 数据表格 -->
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="60" />
      <el-table-column prop="age" label="年龄" width="60" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="diseaseType" label="慢病类型" width="100" />
      <el-table-column prop="doctorName" label="责任医生" width="100" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      layout="total, prev, pager, next"
      @change="fetchData"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <!-- 表单项 -->
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPatientList, addPatient, updatePatient, deletePatient } from '@/api/patient'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索表单
const searchForm = reactive({ name: '', diseaseType: '' })

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({ page: 1, size: 20, total: 0 })

// 弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const formData = reactive({ /* 字段见 Swagger 文档 */ })
const isEdit = ref(false)
const editId = ref(null)

// 获取数据
async function fetchData() {
  loading.value = true
  try {
    const res = await getPatientList({
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  pagination.page = 1
  fetchData()
}

// 重置
function handleReset() {
  searchForm.name = ''
  searchForm.diseaseType = ''
  handleSearch()
}

// 新增
function handleAdd() {
  dialogTitle.value = '新增患者'
  isEdit.value = false
  Object.assign(formData, { /* 初始空值 */ })
  dialogVisible.value = true
}

// 编辑
function handleEdit(row) {
  dialogTitle.value = '编辑患者'
  isEdit.value = true
  editId.value = row.id
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 删除
async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该患者吗？', '提示', { type: 'warning' })
  await deletePatient(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

// 提交表单
async function handleSubmit() {
  await formRef.value.validate()
  if (isEdit.value) {
    await updatePatient(editId.value, formData)
  } else {
    await addPatient(formData)
  }
  ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
  dialogVisible.value = false
  fetchData()
}

onMounted(() => fetchData())
</script>

<style scoped>
.patient-list { padding: 20px; }
</style>
```

**规则**：
- 每个页面必须处理三种状态：**加载中**（`v-loading`）、**空数据**（表格自带）、**错误**（拦截器统一弹窗）
- 搜索、新增、编辑、删除、分页的逻辑结构参考上面的模板，不要自己发明新写法

---

## 六、数据脱敏显示组件

对于需要脱敏的字段（手机号、身份证号），用这个组件：

```vue
<template>
  <span>{{ displayText }}</span>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/user'

const props = defineProps({
  text: String,
  type: String   // phone | idCard
})

const userStore = useUserStore()

const displayText = computed(() => {
  // 管理员看原始数据
  if (userStore.isAdmin) return props.text
  // 普通角色看脱敏数据
  if (props.type === 'phone') {
    return props.text?.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
  }
  if (props.type === 'idCard') {
    return props.text?.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')
  }
  return props.text
})
</script>
```

---

## 七、禁止事项

- ❌ 不要在 `.vue` 文件里直接用 `axios.get(...)`，必须通过 `api/` 目录下的封装函数调用
- ❌ 不要自己写分页逻辑，用 Element Plus 的 `el-pagination`
- ❌ 不要用 `var`，用 `const` 或 `let`
- ❌ 不要在控制台留 `console.log`，提交代码前删掉
- ❌ 不要给组件起名叫 `Page1.vue`、`Test.vue`，用有意义的名字
- ❌ 不要写行内样式（`style="color: red"`），用 `<style scoped>` 或 Element Plus 的 class

---

## 八、提交前自检清单

- [ ] 页面三种状态（加载中、正常、空数据）都测试过
- [ ] 新增/编辑提交成功后列表自动刷新
- [ ] 删除前有二次确认弹窗
- [ ] 表单有必填校验
- [ ] 没有 `console.log`
- [ ] 接口调用走的是 `api/` 封装函数
