# 3号 第一周标准 — CodeArts 智能体集成

## 本周产出物

| 产出 | 文件名 | 完成标志 |
|---|---|---|
| CodeArts 环境开通 | 华为云账号截图 | 控制台可访问 CodeArts 项目 |
| API 调研笔记 | `docs/codearts-research.md` | 覆盖 API 调用方式、格式、限制 |
| 最小 Demo | `ai-agent/demo.py` | 输入需求文本 → 调 API → 输出代码片段 |
| Prompt 模板定稿 | `ai-agent/prompts/task-decompose.txt` | 能将需求拆为前端/后端/数据库/测试四类 |

---

## 一、环境开通（本周一必须完成）

### 华为云 CodeArts 开通步骤

1. 访问 https://www.huaweicloud.com，注册华为云账号（实名认证）
2. 搜索"CodeArts"，进入控制台
3. 创建项目：名称 `chronic-follow-up`
4. 开通 CodeArts Snap（代码智能体）服务
5. 获取 API Key：控制台 → 我的凭证 → 访问密钥 → 新增访问密钥
6. 记录以下信息（后续代码里要用）：
   - IAM 账号名
   - 用户名
   - 密码/API Key
   - 项目 ID

---

## 二、API 调研笔记（`docs/codearts-research.md`）

本周四前输出这份文档，内容必须覆盖：

```markdown
# CodeArts API 调研笔记

## 1. 可用能力
- CodeArts Snap：代码生成、代码续写、代码解释、代码翻译
- CodeArts Pipeline：流水线编排
- CodeArts Check：代码检查
- （列出你实际开通的能力）

## 2. API 调用方式
- 认证方式：AK/SK / Token / 其他
- 请求格式：JSON / 其他
- 响应格式：完整的响应 JSON 样例

## 3. 代码生成 API 详情
- 接口 URL：
- 请求参数格式：
- 输入示例（自然语言需求 → API 输入）：
- 输出示例（API 返回的代码）：
- 单次调用限制（字数/Token 上限）：

## 4. 支持的编程语言
- Java: 是/否
- JavaScript/Vue: 是/否
- SQL: 是/否
- Python: 是/否

## 5. 当前限制与问题
- QPS 限制：
- 单次最长响应时间：
- 已知的不稳定情况：
```

---

## 三、最小 Demo（`ai-agent/demo.py`）

一个 Python 脚本，证明"需求输入 → API 调用 → 代码输出"这个链路能跑通。

```python
"""
CodeArts 智能体最小 Demo
输入：一句自然语言需求
输出：CodeArts Snap 生成的代码片段
运行：python demo.py
"""
import requests
import json

# ===== 配置（替换为你的实际值）=====
API_KEY = "你的API Key"
ENDPOINT = "https://codearts-api.cn-north-4.myhuaweicloud.com/v1/snap/generate"
PROJECT_ID = "你的项目ID"

# ===== 请求 =====
headers = {
    "Content-Type": "application/json",
    "X-Auth-Token": API_KEY   # 实际方式以 API 文档为准
}

# 自然语言需求输入
requirement = """
在 Spring Boot 项目中新增一个 GET 接口 /api/patients，
支持分页查询患者列表，参数有 page、size、name（模糊查询），
返回 {code, data: {records, total}, message} 格式。
"""

payload = {
    "prompt": requirement,
    "language": "java",
    "max_tokens": 1000
    # 实际参数以 API 文档为准
}

# ===== 调用 =====
response = requests.post(ENDPOINT, headers=headers, json=payload, timeout=30)

result = response.json()
print("=== 返回的代码 ===")
print(result.get("generated_code", "无返回内容"))
print("\n=== 完整响应 ===")
print(json.dumps(result, indent=2, ensure_ascii=False))
```

**完成标志**：运行 `python demo.py`，能在终端看到 CodeArts 返回的 Java 代码片段。

---

## 四、Prompt 模板设计（`ai-agent/prompts/task-decompose.txt`）

这是你本周最重要的产出——一个能让 CodeArts 把自然语言需求拆解为开发任务的 System Prompt。

### 拆解规则（Prompt 的核心逻辑）

```
你是一个软件开发任务拆解专家。

用户的自然语言需求会被拆解为以下四类任务：

类型1：前端页面
- 页面名称、路由路径
- 需要的 Element Plus 组件
- 接口调用的 API 地址

类型2：后端接口
- 接口方法（GET/POST/PUT/DELETE）
- URL 路径
- 请求参数类型和字段
- 返回数据格式

类型3：数据库变更
- 涉及的表
- 需要新增的字段或索引
- SQL 语句

类型4：测试用例
- 测试场景描述
- 输入数据
- 预期输出
```

### Prompt 模板正文

```markdown
# 系统角色
你是一个慢病随访系统的开发任务拆解助手。系统技术栈：前端 Vue3+Element Plus，后端 Spring Boot+MyBatis-Plus，数据库 MySQL。

# 拆解规则
收到用户的自然语言需求后，按以下格式输出 JSON：

{
  "summary": "一句话概括需求",
  "tasks": [
    {
      "type": "FRONTEND | BACKEND | DATABASE | TEST",
      "title": "任务标题",
      "description": "详细描述",
      "files_to_modify": ["文件路径1", "文件路径2"],
      "api_endpoint": "如果是新增接口，写 URL",
      "acceptance_criteria": "如何验证任务完成"
    }
  ],
  "risk": "潜在风险说明"
}

# 示例
用户需求："在随访记录页面增加按随访日期范围筛选的功能"

输出：
{
  "summary": "随访记录页面增加日期范围筛选",
  "tasks": [
    {
      "type": "FRONTEND",
      "title": "随访列表页增加日期范围选择器",
      "description": "在搜索栏增加两个 DatePicker：startDate 和 endDate，点击查询时作为参数传入",
      "files_to_modify": ["frontend-target/src/views/followUp/FollowUpList.vue"],
      "api_endpoint": "GET /api/follow-ups?startDate=xxx&endDate=xxx",
      "acceptance_criteria": "选择日期范围后点查询，表格只显示该范围内的随访记录"
    },
    {
      "type": "BACKEND",
      "title": "随访查询接口支持日期范围参数",
      "description": "在 FollowUpController 的 list 方法中增加 startDate 和 endDate 参数，Service 层增加日期范围过滤条件",
      "files_to_modify": [
        "backend/src/main/java/.../controller/FollowUpController.java",
        "backend/src/main/java/.../service/impl/FollowUpServiceImpl.java"
      ],
      "api_endpoint": "GET /api/follow-ups",
      "acceptance_criteria": "Postman 传 startDate=2026-07-01&endDate=2026-07-15 能正确过滤"
    },
    {
      "type": "TEST",
      "title": "补充日期范围筛选的测试用例",
      "description": "测试边界值：同一天、跨月、无效日期格式",
      "files_to_modify": ["backend/src/test/java/.../FollowUpControllerTest.java"],
      "acceptance_criteria": "单元测试全部通过"
    }
  ]
}
```

---

## 五、目录结构（`ai-agent/`）

```
ai-agent/
├── demo.py                              # 最小 Demo（本周必须）
├── requirements.txt                     # Python 依赖：requests
├── prompts/
│   ├── task-decompose.txt               # 需求拆解 Prompt（本周必须）
│   ├── code-generate.txt                # 代码生成 Prompt（第3周）
│   └── code-fix.txt                     # 代码修复 Prompt（第4周）
├── scripts/
│   ├── decompose.py                     # 需求拆解脚本（第3周）
│   └── generate.py                      # 代码生成脚本（第3周）
└── outputs/                             # 生成的代码片段存档
    └── .gitkeep
```

---

## 六、禁止事项

- ❌ 不要把 API Key 明文提交到 Git 仓库（用环境变量或 `.env` 文件，加到 `.gitignore`）
- ❌ 不要用 CodeArts 做需求拆解以外的能力验证——本周聚焦一个功能
- ❌ 不要跳过 API 调研直接写 Demo——调研不充分，第 3 周会返工

---

## 七、与 4 号的协作点

- 4 号做维护平台前端，需要你提供"需求输入 → 拆解结果"的接口格式
- 本周五前你们约定好接口格式：

```
4号前端调用 → 3号的 Python 脚本 → CodeArts API → 返回拆解结果 JSON
```

接口格式草案（你和 4 号本周确认）：

```
POST /api/ai/decompose
请求: { "requirement": "自然语言需求文本" }
返回: { "code": 200, "data": { "summary": "...", "tasks": [...] } }
```
