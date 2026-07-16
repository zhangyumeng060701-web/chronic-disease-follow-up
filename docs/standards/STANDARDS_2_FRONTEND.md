# 2号 第一周标准 — 靶系统前端

## 本周产出物

| 产出 | 完成标志 |
|---|---|
| Vue3 + Element Plus 脚手架 | `npm run dev` 启动成功，浏览器可见页面 |
| 登录页（医生/患者双入口） | 两个 Tab 切换登录，调通 1 号的 `/api/auth/login` |
| 全局布局框架 | 侧边栏 + 顶栏 + 内容区，页面切换无闪烁 |
| 患者列表页（静态） | Mock 数据渲染表格：姓名/性别/年龄/病种/最近随访日期 |

---

## 一、项目结构（不要改）

```
frontend-target/                          # 前端项目根
├── index.html
├── package.json
├── vite.config.js
├── src/
│   ├── main.js                           # 入口：注册 Element Plus、Router、Pinia
│   ├── App.vue                           # 根组件
│   ├── api/                              # 接口调用层
│   │   ├── request.js                    # Axios 封装（统一 token、错误处理）
│   │   └── auth.js                       # 登录接口
│   ├── views/                            # 页面
│   │   ├── login/
│   │   │   └── LoginView.vue             # 登录页
│   │   ├── patient/
│   │   │   └── PatientList.vue           # 患者列表
│   │   ├── followUp/                     # 后续周再加
│   │   ├── alert/                        # 后续周再加
│   │   └── dashboard/                    # 后续周再加
│   ├── layout/
│   │   └── MainLayout.vue                # 全局布局：侧边栏+顶栏+内容区
│   ├── router/
│   │   └── index.js                      # 路由配置
│   ├── store/
│   │   └── user.js                       # Pinia：存 token+用户信息
│   └── assets/
│       └── logo.png                      # 系统 Logo
```

---

## 二、UI 设计 Token（全组统一）

这是 4 号定稿的全局 UI 规范，2 号前端也要遵守：

| Token | 值 | 用途 |
|---|---|---|
| 主色 primary | `#409EFF` | 按钮、链接、选中态 |
| 成功 success | `#67C23A` | 正常状态标记 |
| 警告 warning | `#E6A23C` | 预警黄色 |
| 危险 danger | `#F56C6C` | 预警红色、删除按钮 |
| 字体 | `PingFang SC, Microsoft YaHei, sans-serif` | 全局 |
| 侧边栏宽度 | `220px` | 固定，不可调 |
| 页面最小宽度 | `1200px` | 低于此宽度出横向滚动条 |
| 表格行高 | 默认 | 用 Element Plus 默认 |

---

## 三、代码标准

### 3.1 Axios 封装（`api/request.js`）— 所有接口调用的唯一入口

```js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截：自动带 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截：统一错误处理
request.interceptors.response.use(
  res => {
    if (res.data.code !== 200) {
      ElMessage.error(res.data.message || '请求失败')
      return Promise.reject(new Error(res.data.message))
    }
    return res.data
  },
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
    return Promise.reject(err)
  }
)

export default request
```

### 3.2 接口调用文件（`api/auth.js`）

```js
import request from './request'

export function login(data) {
  return request.post('/auth/login', data)
}
```

### 3.3 登录页（`views/login/LoginView.vue`）

要求：
- 两个 Tab：医生登录 / 患者登录（实际都调同一个接口，role 字段区分）
- 表单字段：用户名 + 密码
- 登录成功后：存 token 到 localStorage → 跳转到主页
- 登录失败：弹错误提示

```vue
<template>
  <div class="login-container">
    <div class="login-card">
      <h2>慢病随访管理系统</h2>
      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="医生登录" name="doctor" />
        <el-tab-pane label="患者登录" name="patient" />
      </el-tabs>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码"
                    prefix-icon="Lock" show-password
                    @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn"
                     @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
```

### 3.4 全局布局（`layout/MainLayout.vue`）

要求：
- 左侧：侧边栏（菜单项：工作台、患者管理、随访记录、预警中心、系统管理）
- 右侧上：顶栏（系统名 + 当前用户 + 退出按钮）
- 右侧下：`<router-view />` 内容区

菜单路由对照表：

| 菜单项 | 路由路径 | 对应页面组件 |
|---|---|---|
| 工作台 | `/dashboard` | `views/dashboard/Dashboard.vue` |
| 患者管理 | `/patients` | `views/patient/PatientList.vue` |
| 随访记录 | `/follow-ups` | `views/followUp/FollowUpList.vue` |
| 预警中心 | `/alerts` | `views/alert/AlertList.vue` |
| 系统管理 | `/system` | 子菜单：用户管理 `/system/users`、操作日志 `/system/logs` |

### 3.5 路由配置（`router/index.js`）

```js
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue')
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'patients',
        name: 'Patients',
        component: () => import('@/views/patient/PatientList.vue'),
        meta: { title: '患者管理' }
      },
      // 其余路由先占位，后续周补充
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录自动跳登录页
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
```

### 3.6 患者列表页（本周静态版）

本周只需要 Mock 数据渲染，不调后端接口。用 `ref` 定义假数据：

```js
const tableData = ref([
  { id: 1, name: '张三', gender: '男', age: 65, diseaseType: '高血压',
    lastFollowUpDate: '2026-07-10', doctorName: '李医生', status: '正常' },
  { id: 2, name: '李四', gender: '女', age: 58, diseaseType: '糖尿病',
    lastFollowUpDate: '2026-07-08', doctorName: '王医生', status: '高危' },
  { id: 3, name: '王五', gender: '男', age: 72, diseaseType: '两者皆有',
    lastFollowUpDate: '2026-06-30', doctorName: '李医生', status: '失访' },
])
```

表格列：姓名 / 性别 / 年龄 / 慢病类型 / 最近随访日期 / 责任医生 / 状态（带颜色标签）

状态列用 Element Plus 的 `el-tag`：
- 正常 → `type="success"`
- 高危 → `type="danger"`
- 失访 → `type="warning"`

---

## 四、命名规范

| 东西 | 规范 | 正确示例 | 错误示例 |
|---|---|---|---|
| 文件夹 | 小驼峰 | `patient`, `followUp` | `Patient`, `follow-up` |
| .vue 文件 | 大驼峰 | `PatientList.vue` | `patientList.vue` |
| .js 文件 | 小驼峰 | `request.js` | `Request.js` |
| 变量/方法 | 小驼峰 | `tableData`, `fetchList()` | `table_data`, `FetchList()` |
| 路由路径 | 短横线 | `/follow-ups` | `/followUps` |
| CSS class | 短横线 | `.search-bar` | `.searchBar` |

---

## 五、禁止事项

- ❌ 不要在 `.vue` 文件里直接 `axios.get(...)`，必须通过 `api/` 调用
- ❌ 不要用 `var`，用 `const`/`let`
- ❌ 不要在控制台留 `console.log`（提交前删干净）
- ❌ 不要给组件起名叫 `Page1`、`Test`
- ❌ 不要用行内样式 `style="color:red"`，用 `<style scoped>` 或 Element Plus class
- ❌ 不要自己写 CSS 实现表格/分页/弹窗——Element Plus 都有现成的

---

## 六、本周自检清单（周四前完成）

- [ ] `npm run dev` 能启动，浏览器打开能看到页面
- [ ] 登录页两个 Tab（医生/患者）能切换
- [ ] 输入用户名密码点登录，Network 面板能看到发出了 POST 请求
- [ ] 登录成功后跳转到 `/dashboard`
- [ ] 左侧菜单点"患者管理"能切换到患者列表页面
- [ ] 患者列表页能看到 3 条假数据的表格，状态列有颜色标签
- [ ] 未登录直接访问 `/patients` 会自动跳转到登录页

---

## 七、与 1 号的协作点

- 1 号周四前给你一个可调的患者列表接口。你拿到后把 Mock 数据替换为真实请求
- 接口地址：`GET /api/patients?page=1&size=20`
- 如果 1 号还没写完，你用 Mock 数据先做，不要等

## 与 4 号的协作点

- 4 号负责整体 UI 规范，配色和组件风格以 4 号的文档为准
- 如果 4 号的规范还没出来，你先用 Element Plus 默认样式，后续再统一调整
