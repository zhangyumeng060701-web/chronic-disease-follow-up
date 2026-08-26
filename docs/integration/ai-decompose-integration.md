# AI需求拆解接口真实联调记录

## 基本信息

<<<<<<< HEAD
- 日期：2026-08-17
- 前端分支：`feature/requirement-api-integration`
- 前端 Commit：`05e37665c92db09427b4c7d96392d0f1e8885fa4`
- 3号后端分支：`origin/feature-3`
- 3号后端 Commit：`e9fc2aaefcabd73bbd9bb17c007bd951a491bdf1`
=======
- 日期：2026-08-23
- 前端分支：`feature/requirement-api-integration-v2`
- 前端基线 Commit：`a904071`（与 PR #13 的 `fdc4948` 补丁等价）
- 3号后端分支：`origin/feature-3`
- 3号后端 Commit：`05fa171`（包含 `7b803d4` 的 JSON 对象解析修复）
>>>>>>> c8f34a48f907d24838044d64e6b88b0777c812c4
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

本次仅验证到 `ChatPanel` 开发服务器可启动。Spring Boot、Python 和 AgentArts 进程均未能在本机启动，因此没有将 Mock 结果作为真实链路结果。

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
<<<<<<< HEAD
- 当前 Controller 的 `jsonToMap` 直接返回 JSON 字符串，而不是解析后的对象。因此当前实现会把 Python JSON 作为 `data` 字符串返回，和前端所需的 `data.tasks` 对象结构不一致。
- Python 脚本的真实 AgentArts 输出未执行，无法确认其顶层字段、任务类型或是否包含 `risk`。
=======
- `origin/feature-3` 的 `7b803d4` 已改用 Jackson 将有效 Python JSON 解析为对象，目标返回为 `data: { summary, tasks, risk }`；该修复尚未进入当前 `origin/develop` 的后端代码，且未在本机实际启动验证。
- JSON 解析失败时，Controller 仍会降级返回字符串。后端必须将该情况作为失败处理，不能让前端把字符串当作 `data.tasks` 对象使用。
- Python 脚本的真实 AgentArts 输出未执行，无法确认其顶层字段、任务类型、`SECURITY` 任务或是否包含 `risk`。
>>>>>>> c8f34a48f907d24838044d64e6b88b0777c812c4

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
<<<<<<< HEAD
| Python AgentArts 调用 | 未验证 | 本机 `python` 和 `python3` 命令不可用。 |
| Spring Boot 启动 | 未验证 | 本机 `java`、`javac`、`mvn` 命令不可用，仓库无 Maven Wrapper。 |
| POST /api/ai/decompose | 未验证 | 后端未能启动；未产生真实 HTTP 状态或业务 code。 |
| 前端真实请求 | 未验证 | 当前 `ChatPanel.vue` 仍为 `USE_MOCK = true`，仅在后端接口成功后才应切换。 |
=======
| Python AgentArts 调用 | 未验证 | 本机 `python`、`python3` 不可用，`py` 启动器也未发现已安装解释器。 |
| Spring Boot 启动 | 未验证 | 本机 `java`、`javac`、`mvn` 命令不可用，仓库无 Maven Wrapper；8080 未监听。 |
| POST /api/ai/decompose | 未验证 | 后端未能启动；未产生真实 HTTP 状态或业务 code。 |
| 前端真实请求 | 未验证 | 当前 `VITE_USE_AI_MOCK=true`；仅在后端接口成功后才应在本地 `.env.local` 设置为 `false`。 |
>>>>>>> c8f34a48f907d24838044d64e6b88b0777c812c4
| 页面真实展示 | 未验证 | 无真实后端响应。 |
| 历史保存 | 未验证 | 未提交真实请求；避免将 Mock 作为证据。 |
| 错误处理 | 未验证 | 无可运行后端，未执行“后端停止后请求”的真实错误测试。 |
| Vitest | 未通过 | `frontend-platform/package.json` 未定义 `test` 脚本。 |
| build | 通过 | `npm.cmd run build` 于 2026-08-17 成功。 |
| 前端开发服务器 | 通过 | `npm.cmd run dev -- --host 127.0.0.1` 后，`/requirement` 返回 HTTP 200。 |

## 未解决问题

1. 需在本机或可访问的联调环境提供 Java 11、Maven 和 Python，并提供 AgentArts 所需的本地密钥配置；不得提交密钥。
2. 后端配置包含 MySQL 数据源，启动时需要可用数据库，或由后端负责人提供已有的无数据库联调 profile。
<<<<<<< HEAD
3. `AiController` 需要在 API 边界把 Python JSON 解析为对象，并保证 `data` 满足正式 camelCase 契约；当前直接返回字符串会导致前端无法读取任务数组。
4. Python 脚本中存在回退凭据逻辑，必须迁移至未提交的本地环境变量或受管密钥后，再进行真实 AgentArts 调用。
5. 当前前端使用硬编码 `USE_MOCK = true`，未实现 `VITE_USE_AI_MOCK`；在后端接口测试成功后，应以环境变量显式关闭 Mock，并保留 Mock 作为非默认备用模式。
6. `origin/feature-3` 与 `origin/develop` 的差异还包含无关 Prompt 修改；在运行环境具备后，应仅临时引入 `AiController.java` 和 `decompose.py`，或由 3 号将接口代码拆分为最小可审查提交。
=======
3. 真实联调环境需要使用包含 `7b803d4` 的 3 号后端版本，确保有效 Python JSON 在 API 边界解析为对象，并保证 `data` 满足正式 camelCase 契约。
4. Python 脚本中存在回退凭据逻辑，必须迁移至未提交的本地环境变量或受管密钥后，再进行真实 AgentArts 调用。
5. 当前前端已通过 `VITE_USE_AI_MOCK` 控制 Mock；在后端接口测试成功后，应只在未提交的 `.env.local` 中显式设为 `false`。
6. 当前 `origin/develop` 尚未包含 `origin/feature-3` 的 `7b803d4` / `05fa171` 后端修复。待运行环境具备后，应由 3 号将最小接口修复合并到 develop，或提供其已部署的联调服务；不得由前端分支重写后端。
>>>>>>> c8f34a48f907d24838044d64e6b88b0777c812c4
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

<<<<<<< HEAD
当前 `AiController` 使用 `ProcessBuilder` 调用 Python。静态检查显示其 `jsonToMap` 未解析 Python JSON，而是将原始 JSON 字符串直接置入 `data`。这会形成如下风险结构：
=======
`AiController` 使用 `ProcessBuilder` 调用 Python。旧版实现会将原始 JSON 字符串直接置入 `data`；`origin/feature-3` 的 `7b803d4` 已改为 Jackson 对象解析，但本机没有运行环境，尚未验证实际响应。若解析失败仍返回字符串，会形成如下风险结构：
>>>>>>> c8f34a48f907d24838044d64e6b88b0777c812c4

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

未取得真实 AgentArts 响应、未启动 Spring Boot、未发出真实 `POST /api/ai/decompose` 请求。因此本记录不声明“联调完成”。
