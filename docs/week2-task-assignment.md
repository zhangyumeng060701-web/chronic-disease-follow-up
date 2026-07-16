# 第二周各号任务明细

## 1号（后端D+E合并，一人负责全部后端）

### 本周最重要的4件事

| 优先级 | 任务 | 验收标准 |
|---|---|---|
| P0 | 随访记录 CRUD | Postman 能增删改查 + 按 patientId 和日期范围筛选 |
| P0 | 预警列表 + 处理 | `GET /api/alerts` 分页查询 + `PUT /api/alerts/{id}/resolve` 标记已处理 |
| P1 | 逾期随访查询 | `GET /api/follow-ups/overdue` 查出超过 next_follow_up_date 7天以上的患者 |
| P1 | 统计接口 | `GET /api/stats/overview` 总览 + 血压/血糖趋势按月聚合 |
| P2 | 用户管理 CRUD | UserController 增删改查，密码 BCrypt 加密 |
| P2 | 操作日志 | 自动记录关键操作到 t_operation_log |
| P2 | 预警自动触发 | 新增随访时遍历 t_alert_rule 逐条匹配，匹配成功的插入 t_alert |
| P2 | 数据脱敏 | 非管理员角色返回数据时自动脱敏手机号/身份证/姓名/住址 |

### 本周禁止
- ❌ 不要跳过 Swagger 注解——每个接口必须加 @ApiOperation
- ❌ 不要在 Controller 里写预警触发逻辑——放到 Service 层
- ❌ 不要等前端联调再改接口——周五前用 Postman 自测全部通过

---

## 前端C（接替2号全部工作，现在是唯一前端）

### 本周最低验收线

| 优先级 | 任务 | 验收标准 |
|---|---|---|
| P0 | 登录页调真实接口 | LoginView.vue 调 POST /api/auth/login，成功后存token跳转 |
| P0 | 患者列表调真实接口 | PatientList.vue 替换 Mock 为真实 API，表格显示数据库数据 |
| P1 | 随访记录页面 | FollowUpList.vue 列表+搜索栏+新增编辑弹窗，先用Mock数据 |
| P1 | 预警页面 | AlertList.vue 预警列表+颜色标签+处理按钮 |
| P2 | 统计看板页面 | Dashboard.vue ECharts 图表+总览数据卡片 |

### 本周重点提醒
- 你是全组最忙的人。优先做 P0，P2 可以推到下周
- 调接口遇到字段名对不上，立刻在群里 @1号 确认，不要自己猜
- 4号会帮你 Review 前端代码，提交 PR 时指定4号

---

## 3号（AI智能体）

| 优先级 | 任务 | 验收标准 |
|---|---|---|
| P0 | Prompt 模板定稿 | task-decompose.txt 能稳定把需求拆成4类任务JSON |
| P0 | 拆解脚本 | decompose.py 读需求→调CodeArts→输出JSON，可运行 |
| P1 | 与4号联调 | 4号ChatPanel提交→你的脚本处理→返回拆解JSON，链路跑通 |

---

## 4号（维护平台前端 + UI规范）

| 优先级 | 任务 | 验收标准 |
|---|---|---|
| P0 | 调通与3号接口 | ChatPanel 提交需求→调 POST /api/ai/decompose→展示拆解结果 |
| P1 | 维护平台路由+布局 | PlatformLayout.vue 侧边栏+内容区切换 |
| P1 | Review 前端C的代码 | 每天看前端C的PR，检查是否符合前端开发标准 |
| P2 | UI规范落地 | 确保靶系统前端的配色/组件风格符合 ui-spec.md |

---

## 5号（测试+安全）

| 优先级 | 任务 | 验收标准 |
|---|---|---|
| P0 | 患者接口测试用例 | PatientControllerTest 覆盖增删改查+分页+边界值 |
| P1 | 脱敏规则文档 | desensitization-rules.md 4种脱敏规则写清楚 |
| P1 | 脱敏前端组件 | Desensitize.vue 手机号/身份证脱敏显示 |

---

## 6号（部署+文档+Git监督）

| 优先级 | 任务 | 验收标准 |
|---|---|---|
| P0 | Docker 全组验证 | 1号能用 docker-compose up 一键启动 MySQL+后端 |
| P1 | 技术报告大纲 | final-report/ 7个文件标题+一级大纲写完 |
| P1 | 检查全组 Git 规范 | 看提交历史，确认没人直接 push develop、没有 .env 泄露 |
| P2 | GitHub Actions 配置 | PR 自动编译，CI 流水线配好 |

---

## 组长A（本周重点）

| 任务 | 内容 |
|---|---|
| 开周总结会 | 按 week1-review-meeting-script.md 四大要点讲完 |
| 飞书看板 | 导入 task-breakdown-5person.csv |
| 接口争议仲裁 | 前端C和1号之间接口理解不一致时拍板 |
| 进度追踪 | 每天 GitHub 看 PR/提交，飞书看任务状态 |
| 技术报告大纲 | 参考 final-report 目录，写一级大纲 |
