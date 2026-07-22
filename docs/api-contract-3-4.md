# 3号与4号需求拆解接口约定

## 一、文档信息

| 项目 | 内容 |
|---|---|
| 文档名称 | 3号 AI 智能体后端与 4号维护平台前端需求拆解接口约定 |
| 适用双方 | 3号 AI 模块、4号维护平台前端 |
| 当前版本 | v1.0 |
| 状态 | 待双方确认 |
| 关联接口 | `POST /api/ai/decompose` |

接口字段、路径、错误码如需修改，必须同步更新本文档和 `docs/swagger-api.md`，并通知 3号 AI 模块与 4号维护平台前端负责人确认。

## 二、接口基本信息

| 项目 | 约定 |
|---|---|
| 请求方法 | `POST` |
| 接口路径 | `/api/ai/decompose` |
| Base URL | `http://localhost:8080/api` |
| 认证方式 | `Authorization: Bearer <token>` |
| Content-Type | `application/json` |
| 统一返回 | `{ "code": 200, "data": {...}, "message": "success" }` |

由于 Base URL 已包含 `/api`，维护平台前端 Axios 调用时应使用：

```js
request.post('/ai/decompose', { requirement })
```

如果某个 Axios 实例的 `baseURL` 未包含 `/api`，才使用：

```text
POST /api/ai/decompose
```

禁止最终请求路径变成：

```text
/api/api/ai/decompose
```

当前维护平台前端真实调用位于 `frontend-platform/src/api/ai.js`：

```js
export function decomposeRequirement(requirement) {
  return request.post('/ai/decompose', { requirement })
}
```

## 三、请求体

正式请求体只能包含 `requirement` 一个字段：

```json
{
  "requirement": "在随访记录页面增加按随访日期范围筛选的功能"
}
```

| 字段 | 类型 | 必填 | 校验 | 说明 |
|---|---|---|---|---|
| `requirement` | String | 是 | 非空 | 用户自然语言需求描述 |

明确禁止发送以下字段：

- `extraRequirement`
- `prompt`
- `content`
- `userInput`
- `message`

如果前端页面存在“补充要求”输入，必须在提交前合并进 `requirement` 字符串后提交，不能单独发送额外字段。

## 四、成功返回结构

正式成功返回结构如下：

```json
{
  "code": 200,
  "data": {
    "summary": "随访记录页面增加日期范围筛选",
    "tasks": [
      {
        "type": "FRONTEND",
        "title": "随访列表页增加日期范围选择器",
        "description": "在搜索栏增加日期范围选择器",
        "filesToModify": [
          "frontend-target/src/views/followUp/FollowUpList.vue"
        ],
        "apiEndpoint": "GET /api/follow-ups?startDate=xxx&endDate=xxx",
        "acceptanceCriteria": "选择日期范围后查询，表格只显示该范围内记录"
      }
    ],
    "risk": "日期格式需要前后端统一为 YYYY-MM-DD"
  },
  "message": "success"
}
```

前端只在 `code === 200` 时视为成功，不得仅依据 HTTP 状态码或 `message` 文案判断成功。

## 五、字段定义

### 5.1 顶层字段

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `code` | Number | 是 | 业务状态码，`200` 表示成功 |
| `data` | Object / null | 是 | 成功时为拆解结果对象，失败时为 `null` |
| `message` | String | 是 | 返回消息，成功时可为 `success` |

### 5.2 data 字段

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `summary` | String | 是 | 需求拆解摘要 |
| `tasks` | Array<Task> | 是 | 拆解任务列表；无任务时返回空数组 `[]` |
| `risk` | String / null | 是 | 整体风险提示；无风险时返回 `null` |

### 5.3 task 字段

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `type` | String | 是 | 任务类型枚举，只能使用本文档第六节中的英文值 |
| `title` | String | 是 | 任务标题 |
| `description` | String | 是 | 任务描述 |
| `filesToModify` | String[] | 是 | 涉及文件路径；无涉及文件时返回空数组 `[]`，不要省略字段 |
| `apiEndpoint` | String / null | 是 | 相关接口路径；无对应接口时返回 `null` |
| `acceptanceCriteria` | String / null | 是 | 可执行、可验证的验收条件；暂无法定义时返回 `null` |

字段空值规则：

- 空字段保留为 `null`，不要省略字段。
- 数组字段无数据时返回空数组 `[]`，不要返回 `null`。
- 前端展示时才将 `null` 或空数组转换为 `--`。
- 后端不要直接返回前端展示用的 `--`。

## 六、任务类型枚举

`task.type` 只能使用以下枚举：

| 枚举值 | 中文含义 |
|---|---|
| `FRONTEND` | 前端任务 |
| `BACKEND` | 后端任务 |
| `DATABASE` | 数据库任务 |
| `TEST` | 测试任务 |
| `SECURITY` | 安全检查 |

约束：

- 同一 `type` 可以出现多条任务。
- `tasks` 顺序不作为业务逻辑依据。
- 前端按 `type` 分组展示。
- 不允许返回中文类型值。
- 不允许随意新增枚举。
- 如确实需要新增枚举，必须双方确认并更新本文档和 `docs/swagger-api.md`。

## 七、错误返回

错误码依据 `docs/swagger-api.md`：

| code | HTTP 语义 | 说明 | 前端处理 |
|---:|---|---|---|
| 400 | Bad Request | 参数校验失败 | 提示参数校验失败或接口返回的明确错误 |
| 401 | Unauthorized | 未登录或 token 过期 | 清理失效 token 并进入登录流程 |
| 403 | Forbidden | 无权限 | 提示无权限执行该操作 |
| 404 | Not Found | 资源不存在 | 提示请求的资源不存在 |
| 500 | Internal Server Error | 服务器内部错误 | 提示稍后重试，不展示后端堆栈 |

统一错误示例：

```json
{
  "code": 400,
  "data": null,
  "message": "requirement不能为空"
}
```

错误处理约定：

- HTTP 状态码和业务 `code` 应保持语义一致。
- 前端不得只依据 `message` 判断成功。
- 只有 `code === 200` 才视为成功。
- `401` 时前端清理失效 token 并进入登录流程。
- `500` 不向前端泄露堆栈信息、SQL、密钥、环境变量或内部路径。

## 八、字段命名红线：错误示例/禁止字段

正式字段必须使用 camelCase：

- `filesToModify`
- `apiEndpoint`
- `acceptanceCriteria`

禁止使用旧示例中的 snake_case 字段：

- `files_to_modify`
- `api_endpoint`
- `acceptance_criteria`

前后端字段名必须完全一致。任何字段命名调整都必须先修改 `docs/swagger-api.md` 和本文档，再同步前端与后端代码。

## 九、Mock 开发约定

第 3 周真实接口完成前，4号维护平台前端使用 Mock 数据开发。

Mock 约定：

- Mock 返回结构必须和正式接口完全一致。
- 切换真实接口时只替换数据来源，页面渲染逻辑不变。
- Mock 不得增加正式接口不存在的字段。
- `USE_MOCK` 的位置需在前端代码中清晰可查。
- Mock 延迟只用于模拟 loading，不属于接口规定。
- Mock 也必须使用 `filesToModify`、`apiEndpoint`、`acceptanceCriteria`。

当前前端 Mock 开关位于：

```text
frontend-platform/src/views/requirement/ChatPanel.vue
```

关键代码：

```js
// 第一周使用 Mock；切为 false 后会调用 src/api/ai.js 的正式拆解接口。
const USE_MOCK = true
```

## 十、验收示例

需求：

```text
在随访记录页面增加按随访日期范围筛选的功能
```

完整成功返回示例：

```json
{
  "code": 200,
  "data": {
    "summary": "随访记录页面增加日期范围筛选",
    "tasks": [
      {
        "type": "FRONTEND",
        "title": "随访列表页增加日期范围选择器",
        "description": "在随访列表页搜索栏增加开始日期和结束日期选择器，并将选择值作为查询参数传递。",
        "filesToModify": [
          "frontend-target/src/views/followUp/FollowUpList.vue"
        ],
        "apiEndpoint": "GET /api/follow-ups?startDate=xxx&endDate=xxx",
        "acceptanceCriteria": "选择 2026-07-01 至 2026-07-31 后点击查询，列表只展示该日期范围内的随访记录，清空筛选后恢复默认列表"
      },
      {
        "type": "BACKEND",
        "title": "随访列表查询支持日期范围参数",
        "description": "在随访分页查询接口中接收 startDate 和 endDate，并按 followUpDate 进行闭区间过滤。",
        "filesToModify": [
          "backend/src/main/java/com/example/followup/controller/FollowUpController.java",
          "backend/src/main/java/com/example/followup/service/FollowUpService.java"
        ],
        "apiEndpoint": "GET /api/follow-ups",
        "acceptanceCriteria": "请求 /api/follow-ups?startDate=2026-07-01&endDate=2026-07-31 时，返回记录的 followUpDate 均在该范围内"
      },
      {
        "type": "DATABASE",
        "title": "确认随访日期查询索引",
        "description": "检查随访记录表的 followUpDate 字段是否具备日期范围查询所需索引；如缺失，由后端和数据库负责人评估是否补充迁移脚本。",
        "filesToModify": [],
        "apiEndpoint": null,
        "acceptanceCriteria": "在测试数据量下执行日期范围查询，接口响应时间满足项目性能要求，且查询结果与数据库记录一致"
      },
      {
        "type": "TEST",
        "title": "补充随访日期范围筛选测试",
        "description": "补充开始日期、结束日期、完整日期范围和非法日期格式的接口测试，并覆盖前端查询参数传递。",
        "filesToModify": [
          "backend/src/test/java/com/example/followup/controller/FollowUpControllerTest.java",
          "frontend-target/src/views/followUp/FollowUpList.vue"
        ],
        "apiEndpoint": "GET /api/follow-ups",
        "acceptanceCriteria": "自动化测试覆盖 startDate、endDate、startDate+endDate、非法日期四类场景，并全部通过"
      },
      {
        "type": "SECURITY",
        "title": "校验随访查询权限与参数边界",
        "description": "确认未登录用户无法查询随访记录，非法日期格式不会触发服务器异常或泄露内部错误。",
        "filesToModify": [],
        "apiEndpoint": "GET /api/follow-ups",
        "acceptanceCriteria": "未携带 token 请求返回 401；非法日期格式请求返回 400；500 响应不包含堆栈、SQL 或内部路径"
      }
    ],
    "risk": "日期格式需要前后端统一为 YYYY-MM-DD"
  },
  "message": "success"
}
```

## 十一、联调检查清单

| 检查项 | 结果 |
|---|---|
| 请求路径是否正确 | □ |
| 是否重复拼接 `/api` | □ |
| `Authorization` 是否携带 | □ |
| `requirement` 是否非空 | □ |
| `code` 是否为 `200` | □ |
| `tasks` 是否为数组 | □ |
| `type` 是否属于枚举 | □ |
| 字段是否使用 camelCase | □ |
| 空数组和 `null` 是否符合约定 | □ |
| 前端能否展示同类型多任务 | □ |
| `400`、`401`、`500` 是否有明确处理 | □ |
| Mock 与正式返回是否一致 | □ |

## 十二、双方确认区

| 角色 | 确认人 | 确认日期 | 状态 |
|---|---|---|---|
| 3号 AI 接口负责人 | 待填写 | 待填写 | 待确认 |
| 4号维护平台前端负责人 | 待填写 | 待填写 | 待确认 |
