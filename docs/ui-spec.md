# 基层慢病随访系统智能化维护平台 UI 规范

> 文件建议位置：`docs/ui-spec.md`  
> 适用范围：2 号靶系统前端 `frontend-target/` 与 4 号维护平台前端 `frontend-platform/`  
> 版本：v1.1（接口契约校正版）  
> 目标：统一全组前端视觉、组件、数据展示与页面交互规范，保证靶系统与维护平台风格一致、实现成本可控、便于后续 Vue3 + Element Plus 开发。

---

## 0. 设计定位

本项目面向“基于华为云 CodeArts 的基层慢病随访系统智能化维护方案”，前端界面需要同时满足两类使用场景：

1. **基层慢病随访靶系统**：医生、患者或管理员使用的慢病随访业务系统。
2. **维护平台前端**：面向开发维护人员的需求输入、任务拆解、代码修复、测试与安全检查入口。

因此，整体 UI 不追求复杂装饰或科技大屏效果，而采用 **Element Plus 风格的医疗信息系统界面**。设计关键词为：

```text
清晰、稳重、可信、低学习成本、易开发、可扩展
```

设计原则：

- 统一使用本文件定义的颜色 Token、字体层级、布局规则。
- 优先使用 Element Plus 默认组件，不额外设计复杂自定义组件。
- 能用组件属性解决的样式，不额外写大段 CSS。
- 页面重点突出功能闭环，而不是视觉炫技。
- 靶系统前端与维护平台前端保持同一视觉语言。

---

## 1. 全局配色规范

所有页面统一使用以下 CSS 变量。后续如需调整颜色，只允许修改 Token，不允许在页面中随意写新的主色。

```css
:root {
  /* 主色系 */
  --color-primary:       #409EFF;   /* 主按钮、链接、选中态 */
  --color-primary-light: #ECF5FF;   /* 主色浅底，如表格悬停行、选中浅底 */
  --color-primary-dark:  #337ECC;   /* 主色深，如按钮按下态 */

  /* 功能色 */
  --color-success:       #67C23A;   /* 正常、通过、成功 */
  --color-warning:       #E6A23C;   /* 预警、待确认、警告 */
  --color-danger:        #F56C6C;   /* 高危、删除、失败 */
  --color-info:          #909399;   /* 次要信息 */

  /* 中性色 */
  --color-bg:            #F5F7FA;   /* 页面背景 */
  --color-bg-card:       #FFFFFF;   /* 卡片、表格、表单背景 */
  --color-border:        #DCDFE6;   /* 默认边框 */
  --color-text-primary:  #303133;   /* 主要文字 */
  --color-text-regular:  #606266;   /* 常规文字 */
  --color-text-secondary:#909399;   /* 次要文字、辅助说明 */
}
```

### 1.1 颜色使用规则

| 类型 | 颜色 | 使用场景 |
|---|---|---|
| 主色 | `--color-primary` | 主按钮、链接、菜单选中态、当前任务高亮 |
| 主色浅底 | `--color-primary-light` | 选中卡片背景、表格 hover、提示浅底 |
| 主色深 | `--color-primary-dark` | 主按钮按下态 |
| 成功色 | `--color-success` | 正常、测试通过、保存成功、部署成功 |
| 警告色 | `--color-warning` | 预警、待确认、失访、信息不完整 |
| 危险色 | `--color-danger` | 高危、删除、测试失败、安全风险 |
| 信息色 | `--color-info` | 次级标签、辅助状态、灰色信息 |
| 页面背景 | `--color-bg` | 页面最外层背景 |
| 卡片背景 | `--color-bg-card` | 卡片、表格、表单容器 |
| 边框 | `--color-border` | 卡片边框、输入框边框、分割线 |
| 主文字 | `--color-text-primary` | 标题、重要字段 |
| 常规文字 | `--color-text-regular` | 正文、表单标签、表格内容 |
| 次要文字 | `--color-text-secondary` | 时间戳、说明文字、空值 |

### 1.2 禁止事项

- 不再使用旧版 UI 规范中的 `#1677FF`、`#13C2C2` 作为全局主色。
- 不使用大面积渐变背景。
- 不使用深色科技大屏风格作为主界面。
- 不在单个页面中私自定义新的主色系。

---

## 2. 字体与字号规范

### 2.1 字体族

统一字体族：

```css
font-family: "PingFang SC", "Microsoft YaHei", "Helvetica Neue", Arial, sans-serif;
```

### 2.2 字号层级

| 层级 | 字号 | 字重 | 用途 |
|---|---:|---:|---|
| H1 | 20px | 600 | 页面标题 |
| H2 | 18px | 600 | 模块标题 |
| H3 | 16px | 500 | 卡片标题、弹窗标题 |
| Body | 14px | 400 | 正文、表格内容、表单标签 |
| Caption | 12px | 400 | 辅助说明、时间戳、标签文字 |

### 2.3 使用规则

- 页面标题只使用 H1，不得过大。
- 模块标题使用 H2。
- 卡片标题使用 H3。
- 表格、表单、按钮文字默认使用 14px。
- 时间戳、提示说明、空状态说明使用 12px。
- 重要数字可加粗，但不单独放大到破坏层级。

---

## 3. 全局布局规范

### 3.1 基础框架

全组页面统一采用“顶栏 + 侧边栏 + 主内容区”的后台系统布局。

```text
┌─────────────────────────────────────────────────────┐
│  Logo + 系统名                      用户名 + 退出   │  ← 顶栏 56px
├──────────┬──────────────────────────────────────────┤
│ 菜单1    │                                          │
│ 菜单2    │         主内容区                          │
│ 菜单3    │     padding: 20px                        │
│          │     最小宽度: 1200px - 220px              │
│          │                                          │
├──────────┴──────────────────────────────────────────┤
│  侧边栏  │                                          │
│  220px   │                                          │
└─────────────────────────────────────────────────────┘
```

### 3.2 尺寸规范

| 区域 | 尺寸 |
|---|---:|
| 顶栏高度 | 56px |
| 全局侧边栏宽度 | 220px |
| 主内容区 padding | 20px |
| 页面最小宽度 | 1200px |
| 卡片内边距 | 16px 或 20px |
| 模块间距 | 16px 或 20px |
| 表单项间距 | 16px |

### 3.3 主内容区

主内容区统一使用浅灰背景：

```css
.page-container {
  min-height: calc(100vh - 56px);
  padding: 20px;
  background: var(--color-bg);
}
```

主要内容使用白色卡片承载：

```css
.content-card {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  padding: 20px;
}
```

### 3.4 维护平台页面例外

维护平台的需求输入页 `ChatPanel.vue` 采用左右两栏布局：

```text
┌──────────────────┬──────────────────────────────────┐
│  历史需求列表     │                                  │
│  300px           │    需求输入区 + 拆解结果区          │
│                  │                                  │
└──────────────────┴──────────────────────────────────┘
```

其中：

- 左侧历史需求列表：300px。
- 右侧主区域：自适应。
- 左右两栏之间：20px。
- 页面外层仍然放在主内容区内，遵守 `padding: 20px`。

---

## 4. 组件规范

本项目默认基于 **Vue3 + Element Plus**。除非业务必须，组件优先使用 Element Plus 默认能力。

### 4.1 按钮

| 类型 | Element Plus 写法 | 使用场景 |
|---|---|---|
| 主按钮 | `type="primary"` | 提交、保存、生成、查询 |
| 次按钮 | 默认按钮 | 清空、取消、返回、重置 |
| 危险按钮 | `type="danger"` | 删除、回滚、清除记录 |
| 文本按钮 | `link` 或 `text` | 查看详情、复制、展开 |

使用规范：

```vue
<el-button type="primary">提交需求</el-button>
<el-button>清空</el-button>
<el-button type="danger">删除</el-button>
```

按钮状态：

| 状态 | 规则 |
|---|---|
| 默认态 | 使用 Element Plus 默认样式 |
| 悬停态 | 使用 Element Plus 默认 hover |
| 按下态 | 主按钮按下态应接近 `--color-primary-dark` |
| 加载态 | 提交、生成、保存等异步操作必须使用 `:loading` |
| 禁用态 | 不可提交时使用 `disabled` |

禁止事项：

- 不使用渐变按钮。
- 不单独写复杂按钮样式。
- 不用红色按钮做普通提交操作。

### 4.2 表单

| 项目 | 标准 |
|---|---|
| 表单标签宽度 | `label-width="100px"` |
| 必填项 | 使用 `required` |
| 校验失败 | 使用 Element Plus 红色提示 |
| 输入框宽度 | 与容器宽度一致，避免固定死宽 |
| 多行输入框 | 需求输入类使用 `type="textarea"` |

示例：

```vue
<el-form label-width="100px">
  <el-form-item label="患者姓名" required>
    <el-input placeholder="请输入患者姓名" />
  </el-form-item>
</el-form>
```

### 4.3 表格

表格统一使用：

```vue
<el-table border stripe>
</el-table>
```

规则：

| 项目 | 标准 |
|---|---|
| 表格样式 | `border stripe` |
| 行高 | 使用 Element Plus 默认行高 |
| 空数据 | 显示“暂无数据” |
| 操作列 | 固定在右侧，按钮使用文本按钮 |
| 时间字段 | 使用 `YYYY-MM-DD` |
| 空值 | 显示 `--` |

### 4.4 弹窗

| 项目 | 标准 |
|---|---|
| 宽度 | 500px–700px，按内容确定 |
| 点击遮罩关闭 | `close-on-click-modal=false` |
| 底部操作 | 右下角：取消 + 确定 |
| 标题 | 16px / 500 |
| 内容 | 14px |

示例：

```vue
<el-dialog
  title="编辑随访记录"
  width="600px"
  :close-on-click-modal="false"
>
</el-dialog>
```

### 4.5 分页

统一使用：

```vue
<el-pagination
  layout="total, sizes, prev, pager, next"
/>
```

分页位置：

- 表格底部右侧。
- 与表格之间间距 16px。

### 4.6 确认删除

所有删除、清空、回滚等高风险操作统一使用：

```js
ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' })
```

禁止直接删除，不弹确认框。

### 4.7 卡片

卡片用于模块承载、任务结果、需求历史项。

```css
.card {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  padding: 16px;
}
```

规则：

- 默认不使用重阴影。
- 不使用大面积彩色卡片。
- 选中态可使用 `--color-primary-light` 作为背景。
- 卡片标题使用 H3。

### 4.8 标签

状态统一使用 `el-tag`。

| 状态 | 类型 | 说明 |
|---|---|---|
| 正常 / 通过 / 已完成 | `success` | 正常、测试通过、部署成功 |
| 高危 / 失败 / 删除 | `danger` | 高危患者、测试失败、安全风险 |
| 失访 / 待确认 / 预警 | `warning` | 失访、待确认、规则预警 |
| 待处理 / 普通信息 | `info` | 待生成、未开始、普通状态 |

---

## 5. 数据展示规则

### 5.1 慢病业务数据

| 数据类型 | 展示方式 |
|---|---|
| 状态（正常/高危/失访） | `el-tag`：正常 green / 高危 red / 失访 orange |
| 血压 | `135/85 mmHg`（收缩压/舒张压） |
| 血糖 | `6.2 mmol/L`（保留 1 位小数） |
| 日期 | `YYYY-MM-DD`（如 2026-07-15） |
| 手机号（脱敏） | `138****5678`（中间 4 位星号） |
| 身份证（脱敏） | `320102********1234`（中间 8 位星号） |
| 空值 | `--`（两个短横，灰色） |

### 5.2 维护平台数据

| 数据类型 | 展示方式 |
|---|---|
| 任务类型 | `el-tag`：前端任务、后端任务、数据库任务、测试任务、安全检查 |
| 任务状态 | 待生成 / 生成中 / 已生成 / 待测试 / 测试通过 / 测试失败 |
| 文件路径 | 使用等宽字体或浅灰背景块展示 |
| 接口路径 | 使用等宽字体展示，如 `GET /api/follow-ups?patientId=1&startDate=2026-07-01&endDate=2026-07-31` |
| 需求时间 | 后端时间字段按 ISO 8601 接收（如 `2026-07-15T09:00:00`），界面格式化为 `YYYY-MM-DD HH:mm` |
| 任务数量 | 如 `共 4 项任务` |
| 空拆解结果 | 显示 `el-empty`，文案为“暂无拆解结果” |

---

## 6. 页面状态与反馈规范

### 6.1 Loading

异步请求必须有加载态。

示例：

```vue
<el-button type="primary" :loading="submitting">提交需求</el-button>
```

维护平台常用 loading 文案：

```text
正在拆解需求，请稍候...
正在生成维护任务...
正在检查接口返回结果...
```

### 6.2 成功反馈

使用：

```js
ElMessage.success('提交成功')
```

常用文案：

```text
需求提交成功
任务拆解完成
保存成功
测试通过
```

### 6.3 失败反馈

使用：

```js
ElMessage.error('提交失败，请稍后重试')
```

常用文案：

```text
接口调用失败，请稍后重试
需求不能为空
需求拆解失败，请补充描述后重试
保存失败
```

### 6.4 空状态

统一使用 `el-empty`。

| 场景 | 文案 |
|---|---|
| 无历史需求 | 暂无历史需求 |
| 无拆解结果 | 暂无拆解结果 |
| 无表格数据 | 暂无数据 |
| 无测试报告 | 暂无测试报告 |

---

## 7. 维护平台需求输入页面规范

页面文件：

```text
frontend-platform/src/views/requirement/ChatPanel.vue
```

### 7.1 页面定位

该页面是 4 号第一周核心页面，用于展示：

```text
左侧历史需求列表 + 右侧需求输入区 + 拆解结果区
```

本页面不采用旧版“三栏式工作台 + 右侧独立流程区”，而改为标准文件指定的 **左右两栏式 ChatPanel 布局**。

### 7.2 页面布局

```text
┌──────────────────┬──────────────────────────────────┐
│  历史需求列表     │                                  │
│                  │    需求输入区                      │
│  ┌────────────┐  │                                  │
│  │ 需求1: xxx │  │   [大文本框]                      │
│  │ 2026-07-15 │  │   请输入您的需求，例如：            │
│  └────────────┘  │   "在随访页面增加血压趋势图"       │
│  ┌────────────┐  │                                  │
│  │ 需求2: xxx │  │   [提交按钮]  [清空按钮]           │
│  └────────────┘  │                                  │
│                  │  ─── 拆解结果 ───                  │
│                  │  [前端任务] [后端任务] [数据库]     │
│                  │  [测试任务] [安全检查]              │
└──────────────────┴──────────────────────────────────┘
```

布局尺寸：

| 区域 | 规范 |
|---|---|
| 页面容器 | `display: flex; gap: 20px;` |
| 左侧历史栏 | `width: 300px; flex-shrink: 0;` |
| 右侧主面板 | `flex: 1; min-width: 0;` |
| 输入区卡片 | 白底、边框、圆角 4px、padding 20px |
| 结果区卡片 | 白底、边框、圆角 4px、padding 20px、与输入区间距 20px |

### 7.3 左侧历史需求列表

内容结构：

```text
历史需求
- 需求标题
- 创建时间
- 当前状态
```

历史项样式：

| 状态 | 样式 |
|---|---|
| 默认 | 白底 + 边框 |
| 悬停 | 背景 `--color-primary-light` |
| 选中 | 背景 `--color-primary-light` + 左边框主色 |
| 空列表 | `el-empty description="暂无历史需求"` |

示例数据：

```text
新增家庭血压均值字段
2026-07-15
已完成

在随访页面增加血压趋势图
2026-07-15
生成中

修改高危预警阈值
2026-07-14
待处理
```

### 7.4 右侧需求输入区

输入框使用：

```vue
<el-input
  v-model="requirement"
  type="textarea"
  :rows="5"
  placeholder="请输入您的需求，例如：在随访页面增加血压趋势图、新增患者用药记录表、修改高危预警阈值为收缩压≥150"
/>
```

按钮：

```vue
<el-button type="primary" :loading="submitting">提交需求</el-button>
<el-button>清空</el-button>
```

规则：

- 主需求不能为空。
- 点击提交后进入 loading。
- 提交成功后显示拆解结果。
- 清空按钮清空输入和当前拆解结果。

### 7.5 补充要求输入

为兼容罕见需求和特殊约束，可在主需求输入框下方增加“补充要求”输入框，但该输入框不改变标准布局。**该字段仅为前端辅助输入，不是后端接口字段。提交前必须与主需求合并为一个 `requirement` 字符串，禁止向接口发送 `extraRequirement`。**

补充要求示例：

```text
仅医生角色可查看该字段；患者端需隐藏身份证号；接口需兼容旧版本字段。
```

推荐写法：

```vue
<el-input
  v-model="extraRequirement"
  type="textarea"
  :rows="3"
  placeholder="可选：填写权限范围、脱敏要求、兼容性要求、指定页面或指定接口"
/>
```

### 7.6 拆解结果区

标题：

```text
需求拆解结果
```

拆解结果展示方式：

- 上方展示 summary。
- 下方使用 `el-tabs` 切换任务类型。
- 每个任务类型展示任务标题、描述、涉及文件、验收标准。

任务类型展示支持：

```text
FRONTEND：前端任务
BACKEND：后端任务
DATABASE：数据库任务
TEST：测试任务
SECURITY：安全检查
```

Tab 中文显示：

```text
前端任务
后端任务
数据库
测试任务
安全检查
```

### 7.7 拆解结果卡片

卡片内容：

```text
任务标题
任务描述
涉及文件
验收标准
```

文件路径展示（字段名必须读取 `filesToModify`）：

```text
frontend-target/src/views/followUp/FollowUpDetail.vue
```

建议样式：

```css
.file-path {
  font-family: "Consolas", "Monaco", monospace;
  font-size: 12px;
  color: var(--color-text-regular);
  background: var(--color-bg);
  padding: 2px 6px;
  border-radius: 4px;
}
```

---

## 8. 图标规范

图标统一使用 Element Plus Icons。

使用规则：

- 不混用不同风格图标库。
- 不使用彩色插画图标替代功能图标。
- 菜单和按钮图标保持线性风格。
- 图标颜色默认继承文字颜色。

建议语义：

| 场景 | 推荐图标 |
|---|---|
| 历史需求 | `Clock` |
| 需求输入 | `Edit` |
| 任务拆解 | `Operation` |
| 前端任务 | `Monitor` |
| 后端任务 | `Cpu` |
| 数据库 | `Coin` |
| 测试通过 | `CircleCheck` |
| 安全检查 | `Lock` |
| 警告 | `Warning` |

---

## 9. Figma 草图规范

如果使用 Figma 绘制页面草图，必须按照本 UI 规范，不再单独设计另一套颜色与组件风格。

### 9.1 推荐画布

```text
Desktop 1440 × 900
```

### 9.2 页面结构

```text
顶栏 56px
侧边栏 220px
主内容区 padding 20px
ChatPanel 内部左侧历史栏 300px
ChatPanel 内部右侧输入与结果区自适应
```

### 9.3 必须体现的模块

- 顶栏：Logo + 系统名 + 用户名 + 退出。
- 侧边栏：维护平台菜单。
- 左侧历史需求列表。
- 右侧需求输入区。
- 提交需求与清空按钮。
- 拆解结果区。
- 前端、后端、数据库、测试、安全检查任务分类。

---


## 10. API 接入与字段契约（强制）

本节以 `swagger-api.md` 为前后端协作唯一依据。UI 实现不得自行发明接口、字段名或返回结构。

### 10.1 请求基础规范

| 项目 | 强制要求 |
|---|---|
| Base URL | `http://localhost:8080/api` |
| 认证请求头 | `Authorization: Bearer <token>` |
| AI 需求拆解接口 | `POST /api/ai/decompose` |
| 请求体 | 只能发送 `{ "requirement": "..." }` |
| 统一返回 | `{ "code": 200, "data": {...}, "message": "success" }` |
| 成功判断 | 以 `code === 200` 为准，不仅依赖 HTTP 状态码 |
| 空值 | 后端字段值为 `null` 且字段不得省略；前端展示时统一转换为 `--` |

请求示例：

```js
await request.post('/ai/decompose', {
  requirement: mergedRequirement
})
```

其中 `mergedRequirement` 为主需求与补充要求在前端合并后的单一字符串。

### 10.2 AI 拆解返回字段

字段名必须严格使用 camelCase：

| 字段 | 类型 | 用途 |
|---|---|---|
| `summary` | String | 需求摘要 |
| `tasks` | Array | 任务列表 |
| `type` | String | 任务类型 |
| `title` | String | 任务标题 |
| `description` | String | 任务描述 |
| `filesToModify` | Array<String> | 涉及文件路径 |
| `apiEndpoint` | String | 相关接口路径 |
| `acceptanceCriteria` | String | 验收标准 |
| `risk` | String | 整体风险提示 |

严禁使用以下错误字段：

```text
files_to_modify
acceptance_criteria
api_endpoint
```

### 10.3 错误码反馈

| code | 前端处理 |
|---:|---|
| 400 | 提示“参数校验失败，请检查需求内容” |
| 401 | 清除失效 token，并跳转登录页 |
| 403 | 提示“无权限执行该操作” |
| 404 | 提示“请求的资源不存在” |
| 500 | 提示“服务器内部错误，请稍后重试” |

### 10.4 历史需求列表的数据边界

当前 API 文档只定义了 `POST /api/ai/decompose`，**未定义历史需求查询、保存或删除接口**。因此第一周历史需求列表只能：

- 使用 Mock 数据；或
- 使用浏览器 `localStorage` 暂存。

不得自行调用 `/api/requirements`、`/api/history` 等未定义接口。后续如新增接口，必须先更新 API 文档并通知全组。

### 10.5 现有慢病接口的 UI 映射

- 患者、随访、预警、统计、用户和日志页面必须使用 API 文档已有路径。
- 分页数据必须读取 `data.records` 与 `data.total`。
- 日期查询参数使用 `YYYY-MM-DD`。
- 后端 ISO 时间（如 `2026-07-15T09:00:00`）仅在前端格式化展示，不改变原始字段。
- 删除患者、删除用户均为软删除；UI 仍需二次确认，但不得描述为物理删除。

## 11. 与 2 号、3 号协作约定

### 11.1 与 2 号协作

2 号靶系统前端必须复用本文件中的：

- 配色 Token。
- 字体层级。
- 顶栏、侧边栏、主内容区布局。
- 表格、表单、按钮、弹窗、分页规范。
- 慢病业务数据展示规则。

### 11.2 与 3 号协作

4 号维护平台前端在第 3 周接入 3 号 AI 接口前，先使用 Mock 数据开发。接口格式另见：

```text
docs/api-contract-3-4.md
```

前端不等待接口完成，先按约定字段开发：

```json
{
  "code": 200,
  "data": {
    "summary": "在随访记录页面增加血压趋势图组件",
    "tasks": [
      {
        "type": "FRONTEND",
        "title": "新增血压趋势图组件",
        "description": "在随访详情页增加 ECharts 折线图",
        "filesToModify": [
          "frontend-target/src/views/followUp/FollowUpDetail.vue"
        ],
        "apiEndpoint": "GET /api/follow-ups?patientId=1",
        "acceptanceCriteria": "图表正确展示该患者近12次随访的血压数据"
      }
    ],
    "risk": "日期格式必须统一为 YYYY-MM-DD"
  },
  "message": "success"
}
```

---

## 12. 禁止事项

- 不要自己重新设计配色，必须使用本文件 Token。
- 不要使用旧版蓝青渐变按钮或大面积渐变背景。
- 不要等 3 号接口完成后再开发，先用 Mock 数据。
- 不要把维护平台和靶系统放进同一个 Vue 项目。
- 不要写超出 Element Plus 默认样式的大量自定义 CSS。
- 不要随意改变血压、血糖、手机号、身份证、空值等数据展示格式。
- 不要直接删除数据，删除操作必须二次确认。

---

## 13. 第一周完成检查清单

| 检查项 | 是否完成 |
|---|---|
| 是否使用 `#409EFF` 作为主色 | □ |
| 是否使用 Element Plus 默认组件风格 | □ |
| 是否采用顶栏 56px、侧边栏 220px、主内容区 padding 20px | □ |
| 是否完成 `ChatPanel.vue` 左 300px + 右自适应布局 | □ |
| 是否完成历史需求列表 | □ |
| 是否完成需求输入框 | □ |
| 是否完成提交与清空按钮 | □ |
| 是否完成拆解结果 Mock 展示 | □ |
| 是否严格使用 `POST /api/ai/decompose` | □ |
| 是否只发送 `requirement` 字段 | □ |
| 是否使用 `filesToModify`、`apiEndpoint`、`acceptanceCriteria` | □ |
| 是否处理 `code` 与 400/401/403/404/500 错误码 | □ |
| 历史需求是否仅使用 Mock 或 localStorage | □ |
| 是否包含前端、后端、数据库、测试、安全检查任务类型 | □ |
| 是否使用统一数据展示规则 | □ |
| 是否避免大量自定义 CSS | □ |
| 是否已发给 2 号同步使用 | □ |


---

## 14. 文档提交规范

本文件修改后按全组 Git 工作流提交：

```bash
git checkout develop
git pull origin develop
git checkout -b feature/ui-spec-api-alignment
git add docs/ui-spec.md
git commit -m "docs: 校正UI规范与API字段及错误处理约定"
git push origin feature/ui-spec-api-alignment
```

随后在 GitHub 创建目标为 `develop` 的 Pull Request。禁止直接向 `develop` 或 `main` 推送，禁止使用 `git push --force`。
