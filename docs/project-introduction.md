# 慢病随访系统智能化维护方案项目介绍

## 1. 项目概览

本项目面向基层社区医疗机构，聚焦高血压、糖尿病等慢病患者随访过程中的管理和维护痛点，建设一套前后端分离的慢病随访管理系统，并结合华为云 CodeArts 探索智能化维护闭环。

项目包含两层目标：

- 靶系统：慢病随访管理系统，覆盖登录、患者档案、随访记录、风险预警、统计看板、系统管理等功能。
- 智能维护方案：围绕 CodeArts 智能体，形成需求理解、架构分析、代码生成与修复、自动化测试、安全检查、部署运维的维护闭环。

当前仓库已完成系统骨架、数据库设计、后端登录与患者管理基础接口、前端登录/布局/患者管理页面，以及较完整的需求、接口、分工和开发标准文档。

## 2. 当前目录结构

```text
chronic-disease-follow-up/
├── backend/                 Spring Boot 后端
├── frontend-target/         Vue 3 靶系统前端
├── ai-agent/                AI 智能体目录，目前仅保留 outputs/.gitkeep
├── docs/                    需求、接口、标准、分工、报告文档
├── docker-compose.yml       MySQL 本地容器环境
├── README.md
└── package-lock.json
```

README 中提到的 `frontend-platform/` 维护平台目录当前尚未出现在工作区。

## 3. 技术架构

整体采用前后端分离架构：

```text
浏览器 / Vue 前端
    ↓ Axios /api
Vite 开发服务器
    ↓ proxy 到 localhost:8080
Spring Boot 后端
    ↓ MyBatis-Plus
MySQL 8.0
```

后端技术栈：

- Spring Boot 2.7.18
- Spring Security
- JWT
- MyBatis-Plus 3.5.5
- MySQL 8
- Knife4j / Swagger
- Lombok
- Java 11

前端技术栈：

- Vue 3
- Vite 5
- Element Plus
- Vue Router
- Pinia
- Axios
- ECharts

部署规划：

- Docker
- docker-compose
- 华为云 ECS / CodeArts 流水线

## 4. 后端架构

后端入口为 `backend/src/main/java/com/example/followup/FollowUpApplication.java`。

包结构按典型 Spring Boot 分层组织：

```text
com.example.followup
├── config          安全、跨域、Swagger、MyBatis-Plus 配置
├── controller      API 控制器
├── service         业务接口
├── service.impl    业务实现
├── mapper          MyBatis-Plus Mapper
├── entity          数据库实体
├── dto             请求与响应 DTO
├── exception       业务异常和全局异常处理
└── util            JWT 工具
```

当前已实现接口：

- `POST /api/auth/login`：登录，当前支持 `admin/123456` 和 `doctor/123456`
- `GET /api/health`：健康检查
- `GET /api/patients`：患者分页列表，支持姓名和慢病类型筛选
- `GET /api/patients/{id}`：患者详情
- `POST /api/patients`：新增患者
- `PUT /api/patients/{id}`：编辑患者
- `DELETE /api/patients/{id}`：软删除患者

统一返回格式：

```json
{
  "code": 200,
  "data": {},
  "message": "success"
}
```

分页返回格式：

```json
{
  "records": [],
  "total": 100,
  "page": 1,
  "size": 20
}
```

## 5. 数据库设计

数据库脚本位于 `backend/src/main/resources/db/schema.sql`，共设计 6 张核心表：

| 表名 | 说明 |
|---|---|
| `t_patient` | 患者档案表 |
| `t_follow_up` | 随访记录表 |
| `t_alert` | 预警记录表 |
| `t_alert_rule` | 异常规则表 |
| `t_user` | 用户表 |
| `t_operation_log` | 操作日志表 |

`t_alert_rule` 已初始化 10 条血压、血糖异常规则，覆盖收缩压、舒张压、空腹血糖和餐后血糖的 YELLOW / RED 阈值。

数据库命名规范：

- 表名统一使用 `t_` 前缀。
- 数据库字段使用下划线命名。
- Java 实体字段使用小驼峰命名。
- 主键统一为 `id BIGINT AUTO_INCREMENT`。
- 删除采用 `status=0` 软删除。

## 6. 前端架构

前端入口为 `frontend-target/src/main.js`，根组件为 `frontend-target/src/App.vue`。

前端目录结构：

```text
frontend-target/src/
├── api/             Axios 封装和业务接口
├── layout/          主布局
├── router/          路由与登录守卫
├── store/           Pinia 用户状态
└── views/           页面组件
```

已实现或已有入口的页面：

| 路由 | 页面 | 当前状态 |
|---|---|---|
| `/login` | 登录页 | 已实现 |
| `/dashboard` | 工作台 | 统计卡片占位 |
| `/patients` | 患者管理 | 已对接真实接口 |
| `/follow-ups` | 随访记录 | 占位 |
| `/alerts` | 预警中心 | 占位 |
| `/system/users` | 用户管理 | 占位 |
| `/system/logs` | 操作日志 | 占位 |

前端请求统一走 `frontend-target/src/api/request.js`：

- `baseURL` 为 `/api`。
- 请求拦截器自动携带 `Authorization: Bearer <token>`。
- 响应拦截器统一处理业务错误码。
- 401 时清理 token 并跳转登录页。

## 7. 核心功能规划

Swagger 文档规划了 24 个接口，分为 7 个模块：

1. 认证模块：登录、登出。
2. 患者管理：列表、详情、新增、编辑、软删除。
3. 随访记录：列表、详情、新增、编辑、逾期查询。
4. 风险预警：预警列表、处理预警、预警统计。
5. 统计看板：总览数据、血压控制率趋势、血糖控制率趋势。
6. 系统管理：用户管理、操作日志。
7. AI 智能体接口：需求拆解 `POST /api/ai/decompose`。

## 8. 智能化维护闭环

规划中的智能化维护链路如下：

```text
需求输入
  ↓
AI 需求理解与任务拆解
  ↓
识别涉及的前端、后端、数据库、测试文件
  ↓
生成或修复代码
  ↓
自动化测试
  ↓
安全检查与数据脱敏校验
  ↓
Docker / 云端部署
  ↓
反馈到维护平台
```

规划产物包括：

- `ai-agent/demo.py`
- `ai-agent/scripts/decompose.py`
- `ai-agent/prompts/task-decompose.txt`
- 维护平台 `ChatPanel`
- `/api/ai/decompose` 接口

当前这些内容主要仍处于文档规划阶段。

## 9. 团队分工

| 角色 | 职责 |
|---|---|
| 组长 A | 需求文档、Swagger、数据库规范、GitHub 管理、进度追踪、技术报告、答辩 PPT |
| 前端 C | 靶系统全部前端页面、API 封装、路由与状态管理 |
| 1 号后端 | 全部后端接口、数据库、权限、脱敏、日志、Swagger 注解 |
| 3 号 AI | CodeArts 调研、Prompt、需求拆解脚本、AI 接口联调 |
| 4 号维护平台 | 维护平台前端、UI 规范、ChatPanel、前端 Review |
| 5 号测试安全 | 自动化测试、安全规则、脱敏规则、权限测试、测试报告 |
| 6 号部署文档 | Docker、CI/CD、华为云部署、Git 监督、部署文档 |

## 10. 开发计划

### 第 1 周：项目骨架与基础模块

- 建立仓库和协作规范。
- 搭建 Spring Boot、Vue、MySQL。
- 完成 6 张数据库表。
- 完成健康检查、登录、患者 CRUD。
- 完成基础布局、登录页、患者页。

### 第 2 周：核心业务扩展

- 后端完成随访 CRUD、预警列表、预警处理、逾期随访、统计接口。
- 前端完成随访页面、预警页面、统计看板。
- AI 侧完成需求拆解 Prompt 和脚本。
- 测试侧完成患者接口测试和脱敏规则。
- 部署侧验证 Docker 环境。

### 第 3 周：前后端联调与权限

- 所有页面对接真实 API。
- 实现医生和管理员数据权限。
- 完成预警规则专项测试。
- 完成权限和脱敏测试。
- 补齐系统管理页面。

### 第 4 周：CI/CD、云部署、AI 集成

- 配置 GitHub Actions 或 CodeArts 流水线。
- 部署到华为云。
- 打通 CodeArts 代码生成链路。
- 完成端到端测试。

### 第 5 周：报告和演示

- 输出技术报告初稿。
- 录制演示视频。
- 整理测试报告和部署文档。

### 第 6 周：收尾与答辩

- 修复 Bug。
- 技术报告定稿。
- 制作答辩 PPT。
- 进行模拟答辩。

## 11. 验收标准

### 功能验收

- 登录成功后能保存 token 并进入主页。
- 患者管理支持分页、查询、新增、编辑、软删除。
- 随访记录支持新增、编辑、查询和日期范围筛选。
- 新增随访后能根据 `t_alert_rule` 自动触发预警。
- 预警列表支持筛选和处理。
- 统计看板能展示患者总数、随访完成率、高危数、失访数及趋势图。
- 管理员能管理用户和查看操作日志，医生无权限。

### 接口验收

- 所有接口统一返回 `Result`。
- 分页接口返回 `records + total + page + size`。
- 字段名严格遵守 Swagger。
- 删除接口只改 `status`，不物理删除。
- Knife4j / Swagger 页面可查看并测试接口。

### 测试验收

- 后端 JUnit / MockMvc 覆盖核心接口。
- 患者 CRUD、分页、边界值测试通过。
- 10 条预警规则边界测试通过。
- 权限隔离和数据脱敏测试通过。
- 端到端流程通过：登录、查询患者、新增随访、触发预警、处理预警。

### 安全验收

- 密码使用 BCrypt 存储。
- JWT 24 小时过期。
- 非管理员看到姓名、手机号、身份证、住址脱敏结果。
- 普通医生只能访问自己负责的数据。
- 不提交 `.env`、API Key、私钥。
- 无高危安全漏洞。

### 部署验收

- `docker-compose up` 能启动 MySQL。
- 后端能连接数据库并访问 `/api/health`。
- 前端 `npm run dev` 能启动并通过代理访问后端。
- 最终部署到华为云后公网可访问。
- CI/CD 能在 PR 时自动编译和测试。

## 12. 当前实现状态与风险

已完成：

- 项目骨架。
- 数据库建表脚本。
- 后端患者管理基础 CRUD。
- 登录接口雏形。
- JWT 工具类。
- 全局异常处理。
- 前端主布局。
- 前端登录页。
- 前端患者管理页。
- API 封装。
- 需求、接口、协作、分工文档。

未完成或仍是占位：

- 随访记录后端接口。
- 预警后端接口。
- 统计接口。
- 用户管理接口。
- 操作日志接口。
- AI 智能体脚本。
- 维护平台前端。
- 自动化测试。
- CI/CD。
- 华为云部署。
- 最终报告正文。

当前风险：

- 多个 Java/Vue 文件中的中文文案出现编码异常，需统一为 UTF-8 并编译验证。
- 后端 JWT 过滤器只设置 request attribute，未写入 Spring Security Authentication，受保护接口可能仍被判定未认证。
- `application.yml` 中存在明文数据库密码和 JWT secret，正式部署前应改为环境变量。
- 登录逻辑目前硬编码账号密码，尚未接入 `t_user` 表和 BCrypt。
- Controller 当前直接返回 `Patient` 实体，尚未按标准返回 VO/DTO 并进行脱敏。
- Docker Compose 当前只启动 MySQL，没有一键启动后端。

