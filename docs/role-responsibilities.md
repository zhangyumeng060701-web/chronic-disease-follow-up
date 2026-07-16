# 团队角色职责卡

---

## 组长A — 项目总管

| 职责 | 具体做什么 |
|---|---|
| 文档定稿 | swagger-api.md（24个接口定义）、数据库命名规范、技术报告 |
| GitHub 管理 | 建仓库、设分支保护、邀请组员、合并 PR |
| 进度追踪 | 每天看 GitHub PR + 飞书看板，谁逾期就 @谁 |
| 接口仲裁 | 前端和后端对字段名/URL 有分歧时，你拍板 |
| 会议组织 | 启动会、每周总结会、答辩演练 |
| 答辩准备 | 8000字技术报告 + 答辩 PPT |

**你不写代码，但你产出决定全组方向。**

---

## 前端C — 靶系统唯一前端（接替2号全部工作）

| 职责 | 具体做什么 |
|---|---|
| Vue3 脚手架 | vite + Element Plus + Router + Pinia 搭好 |
| 全局布局 | MainLayout.vue 侧边栏220px + 顶栏 + 内容区 |
| 登录页 | LoginView.vue 医生/患者双Tab → 调 POST /api/auth/login |
| 患者管理 | PatientList.vue 表格+搜索+新增编辑弹窗+软删除 |
| 随访记录 | FollowUpList.vue 列表+搜索+新增编辑 |
| 预警中心 | AlertList.vue 预警列表+颜色标签+处理按钮 |
| 统计看板 | Dashboard.vue ECharts 图表+数据卡片 |
| 系统管理 | UserManage.vue + OperLog.vue |

**所有接口调用走 api/ 封装，不准在 .vue 里直接 axios.get。**

---

## 1号-后端 — 全部后端 + 数据库

| 职责 | 具体做什么 |
|---|---|
| 数据库 | MySQL 建6张表 + 10条预警规则 + 管理员初始数据 |
| 认证 | JWT 登录/登出 + Spring Security 配置 |
| 患者管理 | PatientController 增删改查+分页+软删除 |
| 随访记录 | FollowUpController 增删改查+日期筛选+逾期查询 |
| 风险预警 | AlertController 列表+处理+统计 + 自动触发逻辑 |
| 统计看板 | StatsController 总览+血压趋势+血糖趋势 |
| 系统管理 | UserController CRUD + 操作日志 + 数据脱敏 |
| 接口文档 | 后端代码中加 Knife4j 注解，Swagger 页可测试 |

**所有接口返回 Result 包装，所有删除用软删除，不准硬编码。**

---

## 3号-AI — CodeArts 智能体集成

| 职责 | 具体做什么 |
|---|---|
| 环境开通 | 华为云 CodeArts 项目 + API Key 获取 |
| API 调研 | 写 docs/codearts-research.md 覆盖调用方式/格式/限制 |
| 最小 Demo | ai-agent/demo.py 需求文本 → API → 代码片段 |
| Prompt 模板 | task-decompose.txt 把需求拆成四类任务 JSON |
| 拆解脚本 | decompose.py 读需求 → 调 CodeArts → 输出 JSON |
| 与4号联调 | 提供 POST /api/ai/decompose 接口给4号 ChatPanel 调用 |

**API Key 不准提交到 Git，用环境变量或 .env（已加入 .gitignore）。**

---

## 4号-维护平台 — 维护平台前端 + UI 规范 + Review 前端C

| 职责 | 具体做什么 |
|---|---|
| UI 规范 | docs/ui-spec.md 配色/字体/布局/组件/数据展示 |
| 维护平台 | Vue3 脚手架 + PlatformLayout + ChatPanel 需求输入面板 |
| 调3号接口 | ChatPanel 提交需求 → 调 /api/ai/decompose → 展示拆解结果 |
| Review 前端C | 每天看C的 PR，检查规范/console.log/行内样式 |
| UI 落地 | 确保靶系统前端页面配色+组件风格符合 ui-spec.md |

**不要等3号接口写完再开发——先用 Mock 数据做 ChatPanel。**

---

## 5号-测试安全 — 测试 + 安全 + 脱敏

| 职责 | 具体做什么 |
|---|---|
| 测试框架 | 后端 JUnit5 + MockMvc / 前端 Vitest 搭建 |
| 预警规则 | 10条规则清单（高血压6+糖尿病4）来自临床指南 |
| 初始数据 | 10条 INSERT 给1号插入 t_alert_rule |
| 接口测试 | 每完成一个后端接口 → 写对应测试用例 |
| 脱敏规则 | 文档：姓名/身份证/手机号/住址 4种规则 |
| 脱敏组件 | Desensitize.vue 给前端C用 |
| 权限测试 | 不同角色登录验证数据隔离和脱敏 |
| 专项测试 | 预警边界值测试、完整用户流程端到端测试 |

**每完成一个接口就测，不要攒到最后一周。**

---

## 6号-部署文档 — 部署 + 文档 + Git 监督

| 职责 | 具体做什么 |
|---|---|
| Git 规范 | 写 git-workflow.md，监督全组遵守 |
| Docker | docker-compose.yml 一键启动 MySQL + 后端 |
| 全组验证 | 确保每人 docker-compose up 能跑通 |
| 赛题资料 | 评分标准+CodeArts文档+往届案例汇总 |
| 技术报告大纲 | final-report/ 7个文件标题+一级大纲 |
| Git 检查 | 每周五查提交历史：没人直接push develop、无 .env 泄露 |
| CI/CD | GitHub Actions 配置 PR 自动编译+测试 |

**先把 Docker 环境搞定——这是所有开发的前置条件。**

---

## 一页速查

| 角色 | 一句话职责 | 产出物 |
|---|---|---|
| 组长A | 管方向、管进度、管文档 | swagger-api.md + 技术报告 + 答辩PPT |
| 前端C | 靶系统全部页面 | 8个 .vue 页面 + api/ 封装 |
| 1号-后端 | 全部接口 + 数据库 | 24个接口 + 6张表 |
| 3号-AI | CodeArts 集成 | 调研文档 + Demo + 拆解脚本 + API |
| 4号-维护平台 | 维护平台前端 + UI规范 + Review | ui-spec.md + ChatPanel |
| 5号-测试 | 测试 + 安全 + 脱敏 | 测试用例 + 脱敏组件 |
| 6号-部署 | Docker + CI/CD + Git监督 | docker-compose.yml + GitHub Actions |
