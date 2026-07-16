# 慢病随访系统 — 第一周总结会发言稿

> 日期：2026年7月16日 | 时长：约60分钟

---

## 要点一：统一技术方向（10分钟）

### 我们为什么选这套技术栈

六个字：**中文资料最多，Bug最好搜。**

| 层 | 选型 | 原因 |
|---|---|---|
| 前端 | Vue 3 + Element Plus | 中文文档全（cn.vuejs.org），组件库开箱即用，不需要写CSS |
| 后端 | Spring Boot 2.7 + MyBatis-Plus | 国内90%Java后端用这个，MyBatis-Plus让你不写SQL操作数据库 |
| 数据库 | MySQL 8.0 | 医疗系统一律关系型数据库，没有争议 |
| 认证 | Spring Security + JWT | 登录后发token，带token访问接口，标准方案 |
| 接口文档 | Knife4j (Swagger) | 自动生成网页版接口文档，前端直接在上面测试 |
| 部署 | 华为云 + Docker | 比赛要求华为云环境 |

### 每个组员必须统一的事

1. **接口调用**：所有人用同一套 URL 和字段名——我已经写在 [docs/swagger-api.md] 里了——24个接口的URL、字段名全在里面，不准自己发明
2. **返回格式**：后端所有接口返回 `{code:200, data:..., message:"success"}`，不准直接返回字符串
3. **数据库字段**：后端 Java 用小驼峰（`diseaseType`），数据库用下划线（`disease_type`），MyBatis-Plus 自动转换
4. **软删除**：不准用 DELETE 语句，全部改 status 字段
5. **提交信息**：必须用 `feat:` `fix:` `docs:` 格式，不准写"改了点东西"

---

## 要点二：飞书协作 + GitHub + AI（15分钟）

### 飞书看板

我已经把任务拆分表格做好了（`docs/task-breakdown.csv`），一共有42条任务，按周次和角色分好。散会后我导入飞书看板，每个人登录飞书就能看到自己名下要做的事。

**看板用法**：
- 每人每天开工前看一眼自己的任务栏
- 完成任务后把状态改成"已完成"
- 遇到阻塞标成红色，我在群里协调

### GitHub 协作

仓库地址：`https://github.com/zhangyumeng060701-Web/chronic-disease-follow-up`

**分支结构**：
```
main        ← 最终上线代码，不准直接改
develop     ← 公共开发分支，所有人汇总测试
feature/xxx ← 你自己的分支，随便改
```

**每天工作流（每个人都要会）**：
```bash
git checkout develop
git pull origin develop
git checkout -b feature/你的功能名
# 写代码
git add .
git commit -m "feat: 做了什么"
git push origin feature/你的功能名
# 去 GitHub 网页提 Pull Request，指定我 Review
```

**三个绝对禁止**：
- ❌ 禁止直接在 develop 上写代码
- ❌ 禁止攒一周代码周五一次性 push
- ❌ 禁止把密码/API Key 提交到仓库

### AI 辅助开发

我们全组零基础，AI 是唯一加速器。但要讲方法：

1. **不要问太宽的问题**："帮我写一个患者管理系统" → AI 会给一堆不能用的代码
2. **要拆成小问题问**："用 Spring Boot 写一个分页查询接口，参数有 page/size/name，返回 {records, total} 格式"
3. **AI 给的代码必须看懂再抄**——看不懂的代码出了 Bug 你没法修
4. **提交前自己测一遍**——AI 经常把接口 URL 写错、字段名编造

---

## 要点三：第一周成果查收（15分钟）

> **重要通知：2号退出，团队调整为5人。前端C接替2号全部前端工作。**

### 1号（后端D主力）——我要查收的4样东西

| 序号 | 查收内容 | 怎么验收 |
|---|---|---|
| ① | 数据库建表 | MySQL 里 `follow_up` 库下能看到6张表 + `t_alert_rule` 有10条数据 |
| ② | Spring Boot 工程能启动 | 浏览器访问 `http://localhost:8080/api/health` → 返回 `{"code":200}` |
| ③ | 患者管理 CRUD | Postman 调增删改查+分页，5个接口全部能通 |
| ④ | JWT 登录 | `POST /api/auth/login` 输入 admin/123456 → 返回 token |

### 2号（前端主力）——已退出

原任务由**前端C**接替。C的工作量会加大，后端D和E在联调阶段需要帮C分担接口对接。

### 3号（AI）——我要查收的3样东西

| 序号 | 查收内容 | 怎么验收 |
|---|---|---|
| ① | 华为云 CodeArts 开通 | 截图控制台能看到项目 `chronic-follow-up` |
| ② | API 调研笔记 | `docs/codearts-research.md` 文件写了 API 调用方式、格式、限制 |
| ③ | 最小 Demo | 运行 `python ai-agent/demo.py` 能调通 CodeArts API 并返回代码片段 |

### 4号（维护平台前端）——我要查收的3样东西

| 序号 | 查收内容 | 怎么验收 |
|---|---|---|
| ① | UI 规范文档 | `docs/ui-spec.md` 写完，配色/字体/组件规范齐全 |
| ② | 维护平台脚手架 + 需求面板 | `npm run dev` 能启动，ChatPanel.vue 左侧列表+右侧输入区可交互 |
| ③ | 与3号接口约定 | `docs/api-contract-3-4.md` 双方确认了 `/api/ai/decompose` 接口格式 |

### 5号（测试+安全）——我要查收的3样东西

| 序号 | 查收内容 | 怎么验收 |
|---|---|---|
| ① | 测试框架搭建 | 后端 `mvn test` 至少 HealthControllerTest 通过；前端 `npm run test` 通过 |
| ② | 预警规则清单 | `docs/alert-rules.md` 写了10条规则（血压6条+血糖4条+随访管理3条） |
| ③ | 异常规则初始数据 | 10条 INSERT 语句已给1号，`t_alert_rule` 表中有数据 |

### 6号（部署+文档）——我要查收的3样东西

| 序号 | 查收内容 | 怎么验收 |
|---|---|---|
| ① | Git 协作文档 | `docs/git-workflow.md` 写好了分支策略+提交规范 |
| ② | Docker 环境 | `docker-compose.yml` 能一键启动 MySQL |
| ③ | 赛题资料汇总 | `docs/competition-resources.md` 记录了评分标准+往届案例+参考文档 |

---

## 要点四：下周（第2周）任务讲解（10分钟）

### 全局目标

**第2周结束时：后端接口全部可调，前端页面全部静态版完成，前后端能开始联调。**

### 1号（后端D主力）

| 任务 | 具体内容 |
|---|---|
| 随访记录 CRUD | FollowUpController 增删改查+按患者ID/日期范围筛选 |
| 预警列表接口 | `GET /api/alerts` 分页查询，支持按类型/等级筛选 |
| 预警处理接口 | `PUT /api/alerts/{id}/resolve` 标记已处理 |
| 逾期随访查询 | `GET /api/follow-ups/overdue` 查出所有超过下次随访日期7天的患者 |

### 3号（AI）

| 任务 | 具体内容 |
|---|---|
| 任务拆解 Prompt 定稿 | `ai-agent/prompts/task-decompose.txt` 最终版，能稳定输出四类任务的 JSON |
| 拆解脚本 | `ai-agent/scripts/decompose.py` 读入需求文本 → 调 CodeArts → 输出拆解结果 |
| 与4号联调 | 4号 ChatPanel 提交需求 → 你的脚本处理 → 返回拆解 JSON，链路跑通 |

### 4号（维护平台前端）

| 任务 | 具体内容 |
|---|---|
| 调通与3号的接口 | ChatPanel 提交需求 → 调 POST /api/ai/decompose → 展示拆解结果 |
| 维护平台路由+布局 | PlatformLayout.vue 侧边栏+内容区，页面切换无闪烁 |
| UI 规范落地 | 确保靶系统前端（C做）的配色/组件风格符合你的 ui-spec.md |

### 5号（测试+安全）

| 任务 | 具体内容 |
|---|---|
| 患者接口测试用例 | PatientControllerTest 覆盖增删改查+分页+边界值 |
| 脱敏规则文档 | `docs/desensitization-rules.md` 写完，给1号实现后端脱敏 |
| 脱敏前端组件 | `components/Desensitize.vue` 手机号/身份证号脱敏显示 |

### 6号（部署+文档）

| 任务 | 具体内容 |
|---|---|
| Docker 一键启动全组验证 | 确保1号能用 `docker-compose up` 启动 MySQL+后端 |
| 技术报告大纲 | `docs/final-report/` 7个文件的标题+一级大纲写完 |
| 检查全组 Git 规范 | 看提交历史，确认没人直接在 develop 上提交、没有 .env 泄露 |

### 前端C——已接替2号全部工作

| 任务 | 具体内容 |
|---|---|
| 登录页调通真实接口 | LoginView.vue 改调1号的 /api/auth/login |
| 患者列表调真实接口 | PatientList.vue 替换 Mock 数据为真实 API 调用 |
| 随访记录页面 | FollowUpList.vue 列表+搜索+新增编辑弹窗，先用 Mock 数据 |
| 预警页面 | AlertList.vue 预警列表+颜色标签+处理按钮 |

---

## 会后行动

1. 各号今晚查收自己第一周的产出，在群里报告完成情况
2. 我把任务拆分表导入飞书看板，大家确认自己的任务栏
3. 明早每人 git pull 最新代码，切自己的 feature 分支开始第2周开发
4. 前端C重点确认：患者列表能调到1号的真实接口

