# 4号 第一周标准 — 维护平台前端 + 整体 UI

## 本周产出物

| 产出 | 文件名 | 完成标志 |
|---|---|---|
| 整体 UI 规范文档 | `docs/ui-spec.md` | 全组评审通过 |
| 维护平台项目初始化 | `frontend-platform/` | `npm run dev` 启动成功 |
| 需求输入页面（静态） | `views/requirement/ChatPanel.vue` | 左侧对话列表 + 右侧输入区可交互 |
| 与3号接口约定 | `docs/api-contract-3-4.md` | 双方确认接口格式 |

---

## 一、全局 UI 规范（`docs/ui-spec.md`）

这份文档是全组前端的圣经。2 号的靶系统前端和你的维护平台前端共用这份规范。

### 1.1 配色方案

```css
:root {
  /* 主色系 */
  --color-primary:       #409EFF;   /* 主按钮、链接、选中态 */
  --color-primary-light: #ECF5FF;   /* 主色浅底（表格悬停行） */
  --color-primary-dark:  #337ECC;   /* 主色深（按钮按下态） */

  /* 功能色 */
  --color-success:       #67C23A;   /* 正常、通过 */
  --color-warning:       #E6A23C;   /* 预警黄色、警告 */
  --color-danger:        #F56C6C;   /* 预警红色、删除、高危 */
  --color-info:          #909399;   /* 次要信息 */

  /* 中性色 */
  --color-bg:            #F5F7FA;   /* 页面背景 */
  --color-bg-card:       #FFFFFF;   /* 卡片/表格背景 */
  --color-border:        #DCDFE6;   /* 边框 */
  --color-text-primary:  #303133;   /* 主要文字 */
  --color-text-regular:  #606266;   /* 常规文字 */
  --color-text-secondary:#909399;   /* 次要文字 */
}
```

### 1.2 字体

| 层级 | 字号 | 字重 | 用途 |
|---|---|---|---|
| H1 | 20px | 600 | 页面标题 |
| H2 | 18px | 600 | 模块标题 |
| H3 | 16px | 500 | 卡片标题 |
| Body | 14px | 400 | 正文、表格内容、表单标签 |
| Caption | 12px | 400 | 辅助说明、时间戳 |

字体族：`PingFang SC, Microsoft YaHei, Helvetica Neue, Arial, sans-serif`

### 1.3 布局

```
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

### 1.4 组件规范

| 组件 | 标准 |
|---|---|
| 按钮 | 主操作 `type="primary"`，次要操作默认，危险操作 `type="danger"` |
| 表格 | `border stripe`，行高默认，空数据提示"暂无数据" |
| 弹窗 | 宽度 500px-700px（按内容），`close-on-click-modal=false` |
| 表单 | `label-width="100px"`，必填项 `required`，校验失败红色提示 |
| 分页 | `layout="total, sizes, prev, pager, next"` |
| 确认删除 | 统一用 `ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' })` |

### 1.5 数据展示规则

| 数据类型 | 展示方式 |
|---|---|
| 状态（正常/高危/失访） | `el-tag`：正常 green / 高危 red / 失访 orange |
| 血压 | `135/85 mmHg`（收缩压/舒张压） |
| 血糖 | `6.2 mmol/L`（保留 1 位小数） |
| 日期 | `YYYY-MM-DD`（如 2026-07-15） |
| 手机号（脱敏） | `138****5678`（中间 4 位星号） |
| 身份证（脱敏） | `320102********1234`（中间 8 位星号） |
| 空值 | `--`（两个短横，灰色） |

---

## 二、维护平台项目结构

```
frontend-platform/
├── package.json
├── vite.config.js
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── api/
│   │   ├── request.js                    # 同 2 号的封装
│   │   └── ai.js                         # 调 3 号的 AI 接口
│   ├── views/
│   │   └── requirement/
│   │       └── ChatPanel.vue             # 本周核心：需求输入面板
│   ├── layout/
│   │   └── PlatformLayout.vue            # 维护平台专用布局
│   ├── router/
│   │   └── index.js
│   └── store/
```

---

## 三、需求输入页面（`ChatPanel.vue`）— 本周核心

### 页面布局

```
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
│                  │  [测试任务]                        │
└──────────────────┴──────────────────────────────────┘
```

左侧 300px，右侧自适应。

### 核心代码骨架

```vue
<template>
  <div class="chat-panel">
    <!-- 左侧：历史列表 -->
    <div class="history-panel">
      <div class="panel-title">历史需求</div>
      <div class="history-list">
        <div v-for="item in historyList" :key="item.id"
             class="history-item"
             :class="{ active: item.id === activeId }"
             @click="selectHistory(item)">
          <div class="item-title">{{ item.title }}</div>
          <div class="item-time">{{ item.createTime }}</div>
        </div>
        <el-empty v-if="historyList.length === 0"
                  description="暂无历史需求" />
      </div>
    </div>

    <!-- 右侧：输入 + 结果 -->
    <div class="main-panel">
      <!-- 输入区 -->
      <div class="input-area">
        <el-input
          v-model="requirement"
          type="textarea"
          :rows="5"
          placeholder="请输入您的需求，例如：在随访页面增加血压趋势图、新增患者用药记录表、修改高危预警阈值为收缩压≥150"
        />
        <div class="input-actions">
          <el-button type="primary" :loading="submitting"
                     @click="handleSubmit">提交需求</el-button>
          <el-button @click="handleClear">清空</el-button>
        </div>
      </div>

      <!-- 结果区 -->
      <div v-if="result" class="result-area">
        <div class="result-title">需求拆解结果</div>
        <div class="result-summary">{{ result.summary }}</div>
        <el-tabs v-model="activeTaskTab">
          <el-tab-pane
            v-for="task in result.tasks" :key="task.type"
            :label="task.type" :name="task.type">
            <div class="task-card">
              <div class="task-title">{{ task.title }}</div>
              <div class="task-desc">{{ task.description }}</div>
              <div class="task-files">
                涉及文件：{{ task.files_to_modify.join('', '') }}
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>
```

---

## 四、与 3 号的接口约定（`docs/api-contract-3-4.md`）

本周五前和 3 号确认以下接口格式：

### 需求拆解接口

```
POST /api/ai/decompose

请求体:
{
  "requirement": "在随访页面增加血压趋势图"
}

返回体:
{
  "code": 200,
  "data": {
    "summary": "在随访记录页面增加血压趋势图组件",
    "tasks": [
      {
        "type": "FRONTEND",
        "title": "新增血压趋势图组件",
        "description": "在随访详情页增加 ECharts 折线图...",
        "files_to_modify": ["frontend-target/src/views/followUp/FollowUpDetail.vue"],
        "acceptance_criteria": "图表正确展示近12次随访的血压数据"
      },
      {
        "type": "BACKEND",
        "title": "新增血压趋势数据接口",
        "description": "...",
        "api_endpoint": "GET /api/follow-ups/{patientId}/bp-trend",
        "files_to_modify": ["...FollowUpController.java"],
        "acceptance_criteria": "接口返回最近12次随访的血压数据"
      }
    ]
  }
}
```

前端 Mock 数据：在第 3 周 3 号接口就绪之前，你和 3 号约定好格式后，前端用 Mock 数据开发，不要等。

---

## 五、禁止事项

- ❌ 不要自己设计配色——上面的 Token 就是最终版本
- ❌ 不要等 3 号的接口写好了再开发——先 Mock 假数据
- ❌ 维护平台不要和靶系统前端放在同一个 Vue 项目里——两个独立的 `frontend-target/` 和 `frontend-platform/`
- ❌ 不要写超出 Element Plus 默认样式的自定义 CSS——能用组件自带属性解决的不要写 style

---

## 六、与 2 号的协作点

- 你负责整体 UI 规范，2 号的靶系统前端页面风格以你的规范为准
- 本周三前把 `docs/ui-spec.md` 写好发给 2 号，让 2 号照着做
