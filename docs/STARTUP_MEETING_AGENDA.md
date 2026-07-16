# 第一次全员启动会 — 议程与讲稿

> 时长：60-90分钟 | 形式：腾讯会议/线下 | 主讲：组长

---

## 第一部分：项目全貌（15分钟）

### 1.1 这个系统做什么（3分钟）
打开 http://localhost:5173 ，现场演示：
- 登录 → 患者管理 → 新增患者 → 编辑 → 删除
- 说明：这就是我们要做的系统。现在只有患者管理，后续会加上随访、预警、统计看板。

### 1.2 技术全链路（5分钟）
一组数据从用户操作到数据库的完整旅程：

```
浏览器（Vue3）点击"查询"
  → GET /api/patients?name=张三
  → Spring Boot PatientController
  → PatientService
  → PatientMapper → MySQL t_patient 表
  → 返回 JSON → Element Plus 表格渲染
```

用这张图让每个人理解：前端管页面、后端管逻辑、数据库管存储，中间靠接口连接。

### 1.3 我们的八大模块（5分钟）
按开发顺序：

| 顺序 | 模块 | 谁做 |
|---|---|---|
| 1 | 患者管理 | ✅ 已完成 |
| 2 | 随访记录 | 1号+2号 第2-3周 |
| 3 | 预警中心 | 1号+5号 第3-4周 |
| 4 | 统计看板 | 1号+2号 第4周 |
| 5 | 系统管理 | 1号+2号 第4周 |
| 6 | CodeArts 智能体 | 3号 第1-4周 |
| 7 | 维护平台前端 | 4号 第1-4周 |
| 8 | 测试+部署 | 5号+6号 全程 |

### 1.4 八周里程碑（2分钟）

| 周次 | 里程碑 |
|---|---|
| 第1周 | 环境就绪 + 后端骨架 + 前端骨架 |
| 第2周 | 随访记录 + 预警模块后端 |
| 第3周 | 统计看板 + 系统管理 |
| 第4周 | CodeArts 集成 + 维护平台 |
| 第5周 | 全模块联调 + 测试 |
| 第6周 | CI/CD + 部署 |
| 第7周 | 报告 + 视频 |
| 第8周 | 冲刺 + 答辩 |

---

## 第二部分：Git 协作规范（15分钟）

### 2.1 三级分支结构

```
main          ← 永远可运行的稳定版（只从 develop 合并）
  └── develop ← 公共开发分支（只从个人分支合并）
       ├── feature/patient-crud    ← 1号的个人分支
       ├── feature/login-page      ← 2号的个人分支
       └── feature/codearts-demo   ← 3号的个人分支
```

### 2.2 每人日常操作（现场演示）

```bash
# 每天开始
git checkout develop
git pull origin develop

# 创建自己的分支干活
git checkout -b feature/xxx

# 写代码...

# 提交
git add .
git commit -m "feat: xxx"

# 推送到 GitHub
git push origin feature/xxx
```

### 2.3 合并流程（组长操作）

- 组员在 GitHub 上从 `feature/xxx` → `develop` 提 Pull Request
- 组长 Review 代码，通过后 Merge
- 每周末 develop 跑通全量测试 → 合并到 main

### 2.4 冲突怎么办

- 原则：每个人只改自己负责的文件，不同时动同一个文件
- 如果冲突：Git 会标记 `<<<<<<<` 和 `>>>>>>>`，手动选保留哪边，删掉标记，重新提交

---

## 第三部分：AI 辅助开发（15分钟）

### 3.1 我们怎么用 AI

每个人开 Codex 对话，直接用自然语言让 AI 写代码。核心规则：

**正确的 Prompt 公式 = 角色 + 上下文 + 具体要求 + 输出格式**

### 3.2 前端 Prompt 示例

```
我在做慢病随访系统的前端，用 Vue3 + Element Plus。
项目仓库在 frontend-target/src/views/followUp/ 目录下。
请帮我在 FollowUpList.vue 中新增一个按日期范围筛选的功能：
- 在搜索栏加两个 el-date-picker：startDate 和 endDate
- 点查询时把日期作为参数传给 GET /api/follow-ups 接口
- 参考 PatientList.vue 的写法
```

### 3.3 后端 Prompt 示例

```
我在做慢病随访系统的后端，用 Spring Boot + MyBatis-Plus。
请参考 PatientController.java 的写法，帮我写 FollowUpController：
- GET /api/follow-ups 分页查询，支持按 patientId、startDate、endDate 筛选
- POST /api/follow-ups 新增随访记录
- 新增随访时检查预警规则表 t_alert_rule
```

### 3.4 常见 AI 错误和规避方法

| 错误 | 表现 | 规避方法 |
|---|---|---|
| 幻觉字段 | AI 编造不存在的数据库字段 | 把 schema.sql 贴给 AI 看 |
| 错误版本 | 用了我们没装的依赖版本 | 把 pom.xml 或 package.json 贴给 AI |
| 不完整代码 | 只给片段，缺 import | 追加要求："请给出完整文件，含 import" |
| 风格不一致 | 和已有代码风格冲突 | 把现有代码文件内容贴给 AI 参考 |

---

## 第四部分：统一标准（10分钟）

### 4.1 技术栈（不可更改）

| 层 | 技术 |
|---|---|
| 前端 | Vue 3 + Element Plus + Axios + ECharts |
| 后端 | Spring Boot 2.7 + MyBatis-Plus + Knife4j |
| 数据库 | MySQL 8.0 |
| 协作 | GitHub + Git |

### 4.2 数据库字段（已定稿，不可更改）

打开 GitHub 仓库 `backend/src/main/resources/db/schema.sql`，现场过一遍 6 张表。

### 4.3 接口标准（已定稿）

- 统一返回格式：`{"code":200, "data":{...}, "message":"success"}`
- 分页格式：`{"records":[...], "total":100, "page":1, "size":20}`
- 接口文档：http://localhost:8080/doc.html

### 4.4 提交信息格式

```
feat: 新增xxx功能
fix: 修复xxx问题
docs: 更新文档
```

---

## 第五部分：Q&A + 分工确认（10分钟）

### 检查清单

- [ ] 每个人确认自己的角色（1号~6号）
- [ ] 每个人确认本周三个产出物（见各自的 STANDARDS_X_xxx.md）
- [ ] 每个人确认自己需要装的软件
- [ ] 每个人发 GitHub 用户名给组长
- [ ] 约定下次例会时间（建议每两天一次，15分钟站会）