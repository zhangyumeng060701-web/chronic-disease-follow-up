# 慢病随访系统智能化维护方案

> 挑战杯揭榜挂帅 | 基于华为云 CodeArts 的基层慢病随访系统智能化维护方案

## 项目简介

本项目面向基层社区医疗机构，聚焦高血压、糖尿病等慢病患者随访过程中的系统维护痛点，
基于华为云 CodeArts 代码智能体，构建"需求理解—架构分析—代码生成与修复—自动化测试—安全检查—部署运维"的全流程智能化开发闭环。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端（靶系统） | Vue 3 + Element Plus + ECharts |
| 前端（维护平台） | Vue 3 + Element Plus |
| 后端 | Spring Boot 2.7 + MyBatis-Plus |
| 数据库 | MySQL 8.0 |
| 智能体 | 华为云 CodeArts Snap |
| 部署 | Docker + 华为云 |

## 项目结构

`
├── frontend-target/       # 靶系统前端（慢病随访管理页面）
├── frontend-platform/     # 维护平台前端（需求输入+智能维护面板）
├── backend/               # Spring Boot 后端
├── ai-agent/              # CodeArts 智能体集成脚本
├── docs/                  # 项目文档
│   ├── standards/         # 各角色开发标准
│   └── final-report/      # 最终报告
└── docker-compose.yml     # 本地开发环境
`

## 快速开始

见各目录下的 README 和 docs/standards/ 下的开发标准文档。

## 后端模块说明

| 模块 | 说明 |
|---|---|
| `controller` | REST API 入口，负责参数校验和统一返回 |
| `service` / `service.impl` | 业务逻辑、权限校验、事务和预警规则调用 |
| `mapper` | MyBatis-Plus 数据访问层 |
| `entity` | 数据库实体 |
| `dto/request` | 请求 DTO，禁止直接绑定实体 |
| `dto/response` | 返回 VO，避免泄露实体字段 |
| `security` | JWT 认证上下文和当前用户工具 |
| `engine` | 预警规则引擎等独立业务组件 |
| `util` | 脱敏、VO 映射等通用工具 |

## 接口索引

- `POST /api/auth/login`：登录，返回 JWT
- `GET /api/patients`、`GET /api/patients/{id}`、`POST /api/patients`、`PUT /api/patients/{id}`、`DELETE /api/patients/{id}`：患者管理
- `GET /api/follow-ups`、`GET /api/follow-ups/{id}`、`POST /api/follow-ups`、`PUT /api/follow-ups/{id}`、`DELETE /api/follow-ups/{id}`、`GET /api/follow-ups/overdue`：随访管理
- `GET /api/alerts`、`PUT /api/alerts/{id}/resolve`：预警管理
- `GET /api/stats/overview`、`/bp-trend`、`/glucose-trend`、`/doctor-comparison`：统计看板
- `GET /api/users`、`POST /api/users`、`PUT /api/users/{id}`、`PUT /api/users/{id}/toggle-status`：用户管理
- `GET /api/logs`：操作日志

接口详细字段见 `docs/swagger-api.md`，本地可通过 Knife4j `/doc.html` 查看。

## 数据库迁移

数据库脚本采用 Flyway 版本化迁移：

- `backend/src/main/resources/db/migration/` 是迁移脚本唯一来源
- `V1__init_schema.sql`：初始 6 张表
- `schema.sql` 仅保留为历史参考
- Docker Compose 启动时由 Flyway 自动执行迁移
