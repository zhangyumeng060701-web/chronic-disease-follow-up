# 华为云 CodeArts 使用证据包

## 1. 目的

用于证明项目基于华为云 CodeArts / AgentArts 完成智能化开发与维护闭环，满足比赛“作品完整性”中关于 CodeArts 使用记录的要求。

## 2. 当前已具备的真实证据

| 证据 | 位置 | 状态 |
| --- | --- | --- |
| AgentArts 真实调用 | `docs/ai-eval/agentarts-real-evidence.md` | 已完成 |
| AI 需求拆解脚本 | `ai-agent/scripts/decompose.py` | 已完成 |
| AI Prompt 模板 | `ai-agent/prompts/task-decompose.txt` | 已完成 |
| 后端 AI 接口 | `backend/.../controller/AiController.java` | 已完成 |
| 自动化测试 | 后端 JUnit + 前端 Vitest | 已完成 |
| CI | `.github/workflows/ci.yml` | 已完成 |
| 华为云 ECS 部署 | `http://124.70.90.96/` | 已完成 |
| 部署文档 | `docs/deployment-guide.md` | 已完成 |

## 3. 必须补充的 CodeArts 控制台截图

以下截图必须由实际使用 CodeArts 的组员，在华为云控制台或 IDE 插件中真实截取，不能由仓库代码替代。

### 3.1 项目与团队

- [ ] CodeArts 项目首页，显示项目名称与成员
- [ ] CodeArts 项目管理看板，显示需求/任务
- [ ] 团队分工页面

### 3.2 CodeArts Snap 使用记录

- [ ] IDE 中安装并启用 CodeArts Snap 插件
- [ ] 使用代码生成功能，生成项目代码的截图
- [ ] 使用代码续写功能，补全代码的截图
- [ ] 使用研发知识问答功能，回答项目技术问题的截图
- [ ] 使用单元测试生成功能，生成测试代码的截图
- [ ] Codebase 索引页面或功能截图

### 3.3 规范驱动开发

- [ ] 使用 CodeArts 规范模板创建项目的截图
- [ ] 代码检查结果页面，显示通过/告警
- [ ] 根据 CodeArts 提示修复代码的前后对比截图

### 3.4 CodeArts 工程能力

- [ ] CodeArts Repo 代码仓库截图
- [ ] CodeArts Pipeline 流水线截图
- [ ] CodeArts Check 检查结果截图
- [ ] CodeArts Artifact 构建产物截图

### 3.5 AgentArts / 大模型能力

- [ ] AgentArts 控制台或应用列表截图
- [ ] AgentArts 调用日志截图
- [ ] AI 返回结构化拆解结果的截图
- [ ] 服务器配置 `AGENT_ARTS_*` 环境变量说明（不展示密钥）

## 4. 证据文件命名建议

最终放入 ZIP 的 `07-CodeArts与AI证据/`：

```text
01-codearts-project.png
02-codearts-snap-codegen.png
03-codearts-snap-qa.png
04-codearts-snap-utgen.png
05-codearts-check.png
06-codearts-pipeline.png
07-agentarts-real-call.png
08-codearts-summary.md
```

## 5. 代码仓库中已可追溯的开发过程证据

评审还可以通过以下内容验证项目真实开发过程：

- GitHub 提交历史
- 分支与 PR 记录
- CI 运行记录
- 测试报告
- 部署验证截图

## 6. 结论

当前仓库已有 **AgentArts 真实调用**和完整工程交付证据；是否满足“全程使用 CodeArts 代码智能体开发”的核心要求，取决于第 3 节截图能否补齐。

如无法补齐真实 CodeArts Snap 使用记录，报告中应如实说明：本项目使用了华为云 AgentArts 实现智能化维护闭环，并在代码仓库/CI/测试/部署中形成可验证工程证据。
