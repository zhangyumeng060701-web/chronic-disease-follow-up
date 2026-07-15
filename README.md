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
