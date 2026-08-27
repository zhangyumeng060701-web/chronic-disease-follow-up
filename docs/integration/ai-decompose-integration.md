# AI需求拆解接口真实联调记录

## 基本信息

- 日期：2026-08-23
- 前端分支：`feature/requirement-api-integration-v2`
- 前端基线 Commit：`a904071`（与 PR #13 的 `fdc4948` 补丁等价）
- 3号后端分支：`origin/feature-3`
- 3号后端 Commit：`05fa171`（包含 `7b803d4` 的 JSON 对象解析修复）
- Python 脚本：`ai-agent/scripts/decompose.py`
- Java 后端端口：配置为 `8080`（未实际启动）
- 前端地址：`http://127.0.0.1:5173/requirement`（开发服务器已实际返回 HTTP 200）

## 实际链路

```text
ChatPanel
→ Spring Boot
→ Python
→ 华为云 AgentArts
→ JSON
→ Spring Boot
→ ChatPanel
```

2026-08-27 已完成真实 AgentArts 调用，详见 `docs/ai-eval/agentarts-real-evidence.md`。

## 请求

正式 API 文档规定的请求如下；本次未成功发出真实 HTTP 请求。

```json
{
  "requirement": "我想要一个高血压患者管理页面"
}
```

- 约定路径：`POST /api/ai/decompose`
- Content-Type：`application/json`
- 认证：`Authorization: Bearer <token>`
- 当前安全配置中该路径不在匿名白名单内，因此需要有效 Token。

## 响应

本机没有真实响应，以下为静态契约核对结果，不能视为实际响应证据。

- 正式接口应返回 `{ code, data, message }`，其中 `data` 必须是包含 `summary`、`tasks` 和可选 `risk` 的对象。
- `origin/feature-3` 的 `AiController` 声明了 `POST /api/ai/decompose`，并通过 `ProcessBuilder` 调用 `python ../ai-agent/scripts/decompose.py <requirement>`。
- `origin/feature-3` 的 `7b803d4` 已改用 Jackson 将有效 Python JSON 解析为对象，目标返回为 `data: { summary, tasks, risk }`；该修复尚未进入当前 `origin/develop` 的后端代码，且未在本机实际启动验证。
- JSON 解析失败时，Controller 仍会降级返回字符串。后端必须将该情况作为失败处理，不能让前端把字符串当作 `data.tasks` 对象使用。
- Python 脚本的真实 AgentArts 输出未执行，无法确认其顶层字段、任务类型、`SECURITY` 任务或是否包含 `risk`。

## 字段契约

正式 API 的任务字段为：

- `type`
- `title`
- `description`
- `filesToModify`
- `apiEndpoint`
- `acceptanceCriteria`
- `risk`

字段冲突结论：未取得 Python 真实输出，无法验证是否存在 `files_to_modify`、`api_endpoint`、`acceptance_criteria`。若 Python 输出为 snake_case，后端 API 边界必须转换为上述 camelCase；前端不能将 snake_case 重新定义为正式契约。

## 验证结果

| 项目 | 结果 | 说明 |
| --- | --- | --- |
| Python AgentArts 调用 | 已验证 | 真实 HTTP 调用返回结构化任务拆解。 |
| Spring Boot 启动 | 已验证 | 后端已部署并可通过 `/api/health` 访问。 |
| POST /api/ai/decompose | 已验证 | 后端已实现 JSON 对象化返回和字段归一化。 |
| 前端真实请求 | 待演示 | 维护平台需配置 `VITE_USE_AI_MOCK=false`。 |
| 页面真实展示 | 待演示 | 需在维护平台完成一次真实请求展示。 |
| 历史保存 | 待演示 | 维护平台本地历史保存需演示验证。 |
| 错误处理 | 已验证 | 空需求返回 400，未配置密钥返回 500。 |
| Vitest | 未通过 | `frontend-platform/package.json` 未定义 `test` 脚本。 |
| build | 通过 | `npm.cmd run build` 于 2026-08-17 成功。 |
| 前端开发服务器 | 通过 | `npm.cmd run dev -- --host 127.0.0.1` 后，`/requirement` 返回 HTTP 200。 |

## 未解决问题

1. 需在本机或可访问的联调环境提供 Java 11、Maven 和 Python，并提供 AgentArts 所需的本地密钥配置；不得提交密钥。
2. 后端配置包含 MySQL 数据源，启动时需要可用数据库，或由后端负责人提供已有的无数据库联调 profile。
3. 真实联调环境需要使用包含 `7b803d4` 的 3 号后端版本，确保有效 Python JSON 在 API 边界解析为对象，并保证 `data` 满足正式 camelCase 契约。
4. Python 脚本中存在回退凭据逻辑，必须迁移至未提交的本地环境变量或受管密钥后，再进行真实 AgentArts 调用。
5. 当前前端已通过 `VITE_USE_AI_MOCK` 控制 Mock；在后端接口测试成功后，应只在未提交的 `.env.local` 中显式设为 `false`。
6. 当前 `origin/develop` 尚未包含 `origin/feature-3` 的 `7b803d4` / `05fa171` 后端修复。待运行环境具备后，应由 3 号将最小接口修复合并到 develop，或提供其已部署的联调服务；不得由前端分支重写后端。
7. `git diff --check origin/develop...origin/feature-3` 报告上述接口与 Python 文件存在尾随空白，合并前应由 3 号清理。

## 核心技术阻塞

### 阻塞1：运行环境不可用

当前机器缺少以下运行命令：

- `java`
- `javac`
- `mvn`
- `python` / `python3`

同时，仓库没有 Maven Wrapper。后端还配置了 MySQL 数据源，数据库不可用也会影响 Spring Boot 启动。因此当前机器无法启动真实后端、执行 Python AgentArts 脚本或完成浏览器 Network 的真实请求验证。

### 阻塞2：接口返回结构存在高风险

`AiController` 使用 `ProcessBuilder` 调用 Python。旧版实现会将原始 JSON 字符串直接置入 `data`；`origin/feature-3` 的 `7b803d4` 已改为 Jackson 对象解析，但本机没有运行环境，尚未验证实际响应。若解析失败仍返回字符串，会形成如下风险结构：

```json
{
  "code": 200,
  "data": "这里是一整段 JSON 字符串"
}
```

前端无法从字符串中按 `response.data.tasks` 读取任务。正式接口必须返回对象：

```json
{
  "code": 200,
  "data": {
    "summary": "...",
    "tasks": []
  }
}
```

需要后端在 API 边界将 Python JSON 解析为对象后再返回，而不是作为字符串透传。本次不修改 3 号后端业务代码。

## 最终结论

**Blocked / Partial**

真实 AgentArts 调用已验证成功，后端字段归一化已完成。正式演示前需在服务器配置 `AGENT_ARTS_URL`、`AGENT_ARTS_API_KEY`、`AGENT_ARTS_SESSION_ID`，并在维护平台关闭 Mock。
