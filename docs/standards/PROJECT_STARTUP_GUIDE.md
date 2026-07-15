# 慢病随访系统智能化维护方案 — 项目启动指南

> 挑战杯揭榜挂帅 | 8周 | 6人团队
> 技术栈：Vue 3 + Spring Boot + MySQL + CodeArts

---

## 一、技术栈（已锁定，不再变动）

| 层 | 选型 | 版本 | 官方文档 |
|---|---|---|---|
| 前端框架 | Vue 3 | 3.x | https://cn.vuejs.org |
| UI 组件库 | Element Plus | 2.x | https://element-plus.org/zh-CN |
| 图表库 | ECharts | 5.x | https://echarts.apache.org/zh |
| HTTP 请求 | Axios | 1.x | https://axios-http.com |
| 后端框架 | Spring Boot | 2.7.x | https://spring.io |
| ORM | MyBatis-Plus | 3.5.x | https://baomidou.com |
| 接口文档 | Knife4j (Swagger) | 4.x | https://doc.xiaominfo.com |
| 数据库 | MySQL | 8.0 | — |
| 认证 | Spring Security + JWT | — | — |
| 测试 | JUnit 5 + Mockito | — | — |
| 部署 | 华为云 + Docker | — | — |

---

## 二、系统功能模块

### 模块 1：患者档案管理
- 患者基本信息（姓名、性别、年龄、身份证号、联系方式）
- 慢病类型（高血压 / 糖尿病 / 两者皆有）
- 病史记录、用药记录
- 建档日期、责任医生

### 模块 2：随访记录管理
- 随访日期、随访方式（门诊/电话/上门）
- 血压（收缩压/舒张压）、血糖（空腹/餐后）
- 用药依从性评估（规律/间断/不服药）
- 症状与体征记录
- 随访小结与建议
- 下次随访日期

### 模块 3：风险预警
- 规则：连续两次随访血压/血糖超标 → 自动标记"高危"
- 规则：超过下次随访日期 7 天未随访 → "失访预警"
- 预警列表 + 颜色标记（红/黄/绿）

### 模块 4：数据统计看板
- 管理慢病患者总数、本月随访完成率
- 血压/血糖控制率趋势图
- 高危患者占比
- 各社区/医生管辖数据对比

### 模块 5：系统管理
- 医生账号管理（CRUD）
- 角色权限（管理员 vs 普通医生）
- 数据脱敏开关（患者姓名/身份证号部分隐藏）
- 操作日志查看

---

## 三、接口文档（Swagger 规范）

前后端协作的唯一依据。**组长在第 2 周结束前必须完成此文档**。

### 3.1 通用规范

- Base URL: `http://localhost:8080/api`
- 认证方式: 请求头 `Authorization: Bearer <token>`
- 统一返回格式:
```json
{
  "code": 200,
  "data": { ... },
  "message": "success"
}
```
- 分页请求: `?page=1&size=20`
- 分页返回:
```json
{
  "code": 200,
  "data": {
    "records": [ ... ],
    "total": 100,
    "page": 1,
    "size": 20
  }
}
```

### 3.2 接口清单（共约 25 个接口）

#### 认证模块（2 个）
```
POST   /api/auth/login          # 登录，返回 JWT token
POST   /api/auth/logout         # 登出
```

#### 患者管理（5 个）
```
GET    /api/patients            # 分页查询患者列表（支持姓名/慢病类型筛选）
GET    /api/patients/{id}       # 患者详情
POST   /api/patients            # 新增患者
PUT    /api/patients/{id}       # 编辑患者信息
DELETE /api/patients/{id}       # 删除患者（软删除）
```

#### 随访记录（5 个）
```
GET    /api/follow-ups          # 分页查询随访记录（按患者ID/日期范围筛选）
GET    /api/follow-ups/{id}     # 随访详情
POST   /api/follow-ups          # 新增随访记录
PUT    /api/follow-ups/{id}     # 编辑随访记录
GET    /api/follow-ups/overdue  # 获取逾期未随访的患者列表
```

#### 风险预警（3 个）
```
GET    /api/alerts              # 预警列表（高危/失访，支持筛选）
PUT    /api/alerts/{id}/resolve # 处理预警
GET    /api/alerts/stats        # 预警统计（高危数/失访数）
```

#### 统计看板（3 个）
```
GET    /api/stats/overview      # 总览数据（患者总数、随访完成率等）
GET    /api/stats/blood-pressure # 血压控制率趋势（按月）
GET    /api/stats/blood-sugar   # 血糖控制率趋势（按月）
```

#### 系统管理（4 个）
```
GET    /api/users               # 用户列表
POST   /api/users               # 新增用户（医生）
PUT    /api/users/{id}          # 编辑用户
DELETE /api/users/{id}          # 删除用户
GET    /api/logs                # 操作日志
```

### 3.3 接口详细示例（以"新增随访记录"为例）

```
POST /api/follow-ups
Content-Type: application/json
Authorization: Bearer <token>

请求体:
{
  "patientId": 1,
  "followUpDate": "2026-07-15",
  "followUpType": "门诊",          // 门诊 / 电话 / 上门
  "systolicBP": 135,               // 收缩压 mmHg，可为 null
  "diastolicBP": 85,               // 舒张压 mmHg，可为 null
  "fastingGlucose": 6.2,          // 空腹血糖 mmol/L，可为 null
  "postprandialGlucose": 8.5,     // 餐后血糖 mmol/L，可为 null
  "medicationAdherence": "规律",   // 规律 / 间断 / 不服药
  "symptoms": "偶有头晕",          // 可为空
  "advice": "继续用药，注意饮食",
  "nextFollowUpDate": "2026-08-15"
}

返回:
{
  "code": 200,
  "data": { "id": 1 },
  "message": "success"
}
```

### 3.4 业务规则（后端必须实现）

1. **新增随访时自动触发预警检查**：如果该患者最近两次随访血压均超标（收缩压≥140 或舒张压≥90），或血糖均超标（空腹≥7.0 或餐后≥11.1），自动生成一条高危预警。
2. **数据脱敏**：非管理员角色查询患者列表时，身份证号只显示前 6 位 + 后 4 位（如 `320102****1234`），手机号中间 4 位隐藏（如 `138****5678`）。
3. **权限校验**：普通医生只能查看自己名下的患者和随访记录；管理员可查看全部。

---

## 四、GitHub 协作规范

### 4.1 仓库设置（组长操作）

1. 在 GitHub 创建仓库 `chronic-disease-follow-up`
2. Settings → Branches → Add rule：保护 `main` 分支
   - ☑ Require a pull request before merging
   - ☑ Require approvals (1 个即可)
3. Settings → Collaborators：邀请 5 位组员，权限给 Write

### 4.2 分支命名规则

```
feature/patient-crud        # 功能分支
fix/swagger-response-format # 修复分支
```

### 4.3 每日工作流（每个组员）

```bash
# 1. 早上第一件事：同步最新代码
git checkout main
git pull origin main

# 2. 切新分支干活
git checkout -b feature/xxx

# 3. 写代码...

# 4. 提交（小步提交，别攒一天）
git add .
git commit -m "feat: 完成患者列表接口"

# 5. 推送到 GitHub
git push origin feature/xxx

# 6. 去 GitHub 网页上点 "Create Pull Request"
# 7. 在 PR 描述里 @ 另一个同学 review
# 8. Review 通过后点 Merge，然后删掉远程分支
```

### 4.4 提交信息格式（强制）

```
feat: 新增患者列表分页查询接口
fix: 修复随访日期为空时的空指针异常
docs: 更新 Swagger 接口文档
test: 补充预警规则单元测试
```

### 4.5 仓库目录结构

```
chronic-disease-follow-up/
├── frontend/                 # Vue 3 前端项目
│   ├── src/
│   │   ├── views/           # 页面
│   │   ├── components/      # 公共组件
│   │   ├── api/             # 接口调用（对应 Swagger 文档）
│   │   ├── router/          # 路由
│   │   └── store/           # 状态管理（Pinia）
│   └── package.json
├── backend/                  # Spring Boot 后端项目
│   ├── src/main/java/
│   │   └── com/example/followup/
│   │       ├── controller/  # 控制器（接口入口）
│   │       ├── service/     # 业务逻辑
│   │       ├── mapper/      # 数据库操作
│   │       ├── entity/      # 数据表实体
│   │       ├── dto/         # 请求/响应对象
│   │       └── config/      # 安全/跨域等配置
│   ├── src/main/resources/
│   │   ├── application.yml  # 数据库等配置
│   │   └── db/migration/    # SQL 建表脚本
│   └── pom.xml
├── docs/
│   ├── swagger-api.yml      # Swagger 接口文档
│   ├── requirements.md      # 需求文档
│   └── architecture.md      # 架构说明
└── README.md
```

---

## 五、六人分工与八周计划

### 5.1 角色分配

| 编号 | 角色 | 人数 | 具体职责 |
|---|---|---|---|
| A | **组长（你）** | 1 | 需求文档、Swagger 接口文档定稿、GitHub 管理、进度追踪、技术报告、答辩 PPT |
| B | 前端主力 | 1 | Vue 项目搭建、路由/权限/布局框架、患者管理页面、统计看板页面 |
| C | 前端辅助 | 1 | 随访记录页面、预警页面、系统管理页面、配合联调 |
| D | 后端主力 | 1 | Spring Boot 项目搭建、数据库设计、患者/随访/预警核心接口 |
| E | 后端辅助 | 1 | 统计接口、用户/日志接口、权限校验、数据脱敏逻辑 |
| F | 测试+部署 | 1 | 测试用例编写、GitHub Actions CI/CD、华为云部署、安全扫描 |

### 5.2 八周详细计划

#### 第 1 周：环境搭建 + 学习

| 人员 | 任务 |
|---|---|
| 全员 | 安装：VS Code / IDEA、Node.js 18+、JDK 11+、MySQL 8.0、Git、Postman |
| B+C | 通读 Vue 3 官方教程（至少到"组件基础"）、跑通 Element Plus 示例 |
| D+E | 通读 Spring Boot 官方 Quick Start、MyBatis-Plus 快速开始、跑通一个 Hello World |
| F | 学习 JUnit 5、GitHub Actions 基础 |
| A（你） | 写需求文档初稿、建 GitHub 仓库、建项目文件夹骨架上传 |

**第 1 周检查点**：每个人都能在本地跑起来自己那部分的基础项目。

#### 第 2 周：接口文档定稿 + 数据库建表

| 人员 | 任务 |
|---|---|
| A（你） | **写完所有 Swagger 接口文档**（这是你最重要的产出） |
| D+E | 数据库 ER 图设计、建表 SQL、Spring Boot 连接数据库跑通 |
| B+C | 搭好 Vue 项目脚手架、路由配置、登录页面静态版 |
| F | 写 GitHub Actions 配置（自动编译检查）、准备测试数据 |

**第 2 周检查点**：Swagger 文档完成并与后端确认每个接口可实现性；数据库表建完。

#### 第 3-4 周：并行开发

| 人员 | 任务 |
|---|---|
| B+C | 按接口文档开发前端页面（患者管理 → 随访记录 → 预警 → 统计看板 → 系统管理） |
| D+E | 按接口文档开发后端接口（同上顺序） |
| F | 每完成一个接口就开始写测试用例 |
| A（你） | 每天看 PR、解决组员"接口理解不一致"的问题、更新文档 |

**第 4 周检查点**：所有 CRUD 接口可调通；前端页面静态版完成。

#### 第 5 周：前后端联调

| 人员 | 任务 |
|---|---|
| B+C+D+E | 前后端对接，修正所有接口不一致的地方 |
| F | 端到端测试、边界条件测试 |
| A（你） | 建 Bug 清单、分配修复任务、开始写技术报告大纲 |

**第 5 周检查点**：所有页面能从后端拿到真实数据并正确展示。

#### 第 6 周：测试 + 安全 + CodeArts 集成

| 人员 | 任务 |
|---|---|
| F | 完整测试报告、GitHub Actions 跑通、部署到华为云测试环境 |
| D+E | 安全加固（SQL 注入防护、XSS 防护、数据脱敏验证、权限校验验证） |
| B+C | UI 细节打磨、异常状态处理（加载中、空数据、网络错误） |
| A（你） | 技术报告初稿 |

**第 6 周检查点**：系统部署到云端可访问；安全扫描无高危漏洞。

#### 第 7 周：报告 + 演示视频

| 人员 | 任务 |
|---|---|
| A（你） | 技术报告定稿、答辩 PPT |
| B+C | 配合录制演示视频的界面操作部分 |
| D+E | 配合录制演示视频的后台/部署部分 |
| F | 整理测试报告、部署文档 |

**第 7 周检查点**：技术报告完成、演示视频录制完毕。

#### 第 8 周：修 Bug + 演练

| 人员 | 任务 |
|---|---|
| 全员 | Bug 修复、答辩模拟演练、材料最终检查 |

---

## 六、组长前两周操作清单（你现在就能做）

### 立刻（今天）

1. 把这本文档发到团队群，让大家看一遍
2. 拉群开会，确认每人的角色（B~F 各是谁）
3. 每人确认自己电脑上能装哪些软件

### 第 1 天

1. 去 github.com 注册账号，创建仓库 `chronic-disease-follow-up`
2. 在本地创建项目文件夹骨架，推送到 GitHub
3. 把组员加为 Collaborator

### 第 1 周内

1. 写需求文档（照着上面的功能模块展开即可）
2. 确保每个人都能跑通自己的 Hello World

### 第 2 周内

1. 对着上面的接口清单，写出每个接口的详细 Swagger 定义
2. 和后端主力确认每个接口能不能实现
3. 把 Swagger 文档放到仓库 `docs/` 目录下

---

## 七、常用命令速查

### Git（每个人都要会）
```bash
git clone https://github.com/你的用户名/chronic-disease-follow-up.git
git status                    # 看改了哪些文件
git add .                     # 暂存所有修改
git commit -m "feat: xxx"     # 提交
git pull origin main          # 拉最新代码
git push origin feature/xxx   # 推送自己的分支
git branch -a                 # 看所有分支
```

### 前端（Vue 3）
```bash
cd frontend
npm install                   # 装依赖（第一次）
npm run dev                   # 启动开发服务器（默认 http://localhost:5173）
```

### 后端（Spring Boot）
```bash
cd backend
mvn clean install             # 编译
mvn spring-boot:run           # 启动（默认 http://localhost:8080）
```
Swagger 文档地址：启动后访问 http://localhost:8080/doc.html

### 数据库（MySQL）
```sql
CREATE DATABASE follow_up DEFAULT CHARACTER SET utf8mb4;
-- 执行 docs/db/init.sql 建表
```

---

> **这份文档就是你给团队下达任务的总依据。每个组员只需要看自己角色对应的部分即可开展工作。**
> 有任何一项不清楚，现在就问我。
