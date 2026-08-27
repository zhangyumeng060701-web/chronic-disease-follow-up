# AgentArts 真实联调证据

## 验证时间

- 日期：2026-08-27
- 调用方式：真实 HTTP POST 到华为云 AgentArts
- 测试需求：在随访记录页面增加按随访日期范围筛选的功能，并支持查询结果分页

## 调用信息

接口地址：

```text
https://defaultgw-mu4gmyrsgi.cn-southwest-2.huaweicloud-agentarts.com/runtimes/agent-arts-603436e9c91b445aa942cd45b429a614/invocations
```

认证方式：

- `Authorization: Bearer <AGENT_ARTS_API_KEY>`
- `x-hw-agentarts-session-id: <AGENT_ARTS_SESSION_ID>`

密钥不写入仓库，只通过服务器环境变量注入。

## 真实响应结果

AgentArts 返回流式 SSE，最终 Python 脚本提取并解析为 JSON：

```json
{
  "code": 200,
  "data": {
    "summary": "在随访记录页面增加按随访日期范围筛选功能，并实现查询结果分页",
    "tasks": [
      {
        "type": "FRONTEND",
        "title": "随访记录页面增加日期范围选择器与分页组件",
        "description": "1. 在随访记录页面的搜索区域添加 DatePicker...",
        "files_to_modify": ["frontend/src/views/followup/FollowUpList.vue", "frontend/src/api/followup.js"],
        "api_endpoint": "GET /api/followup/records",
        "acceptance_criteria": "用户可选择开始和结束日期..."
      }
    ]
  },
  "message": "success"
}
```

## 字段契约发现

真实 AgentArts 返回字段为 snake_case：

- `files_to_modify`
- `api_endpoint`
- `acceptance_criteria`

前端契约要求 camelCase：

- `filesToModify`
- `apiEndpoint`
- `acceptanceCriteria`

已在 `AiController` 增加 `normalizeKeys`，后端统一转换为 camelCase 后再返回给前端。

## 结论

真实 AgentArts 链路已验证可用：

- 认证通过
- 请求成功
- 返回结构化任务拆解
- 后端已做字段归一化
- 未提交任何真实密钥

## 线上验证

2026-08-27 已通过线上接口 `POST /api/ai/decompose` 完成真实调用，返回结构已归一化为标准 camelCase：

- `summary`
- `tasks[].type`
- `tasks[].title`
- `tasks[].description`
- `tasks[].filesToModify`
- `tasks[].apiEndpoint`
- `tasks[].acceptanceCriteria`
- `risk`

后续正式演示时，需要在服务器配置：

```text
AGENT_ARTS_URL=
AGENT_ARTS_API_KEY=
AGENT_ARTS_SESSION_ID=
```
