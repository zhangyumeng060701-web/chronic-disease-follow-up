# 挑战杯作品提交核对清单

## 1. 整体介绍 PPT

要求内容：

- 场景背景与痛点分析
- 需求拆解
- 系统架构设计
- 华为云 CodeArts 辅助开发与调试
- 解决方案
- 核心技术难点与解决思路
- 测试报告
- 落地效果评估
- 场景扩展性说明

仓库现有材料：

- `docs/final-report/01-项目背景与问题分析.md`
- `docs/final-report/02-系统架构设计.md`
- `docs/final-report/03-核心功能与技术实现.md`
- `docs/final-report/04-智能化维护闭环方案.md`
- `docs/final-report/05-测试方案与安全策略.md`
- `docs/final-report/06-部署与运维方案.md`
- `docs/final-report/07-创新点总结.md`
- `docs/codearts-evidence.md`

待补：

- [ ] 输出正式 `.pptx` 文件
- [ ] 补充落地效果评估数据
- [ ] 补充场景扩展性说明

## 2. 视频演示链接

线上演示环境：

- 医生/管理端：http://124.70.90.96/
- 患者端 H5：http://124.70.90.96/p/
- 健康检查：http://124.70.90.96/api/health
- Swagger：http://124.70.90.96/doc.html

演示账号：

- 管理员：`admin` / `123456`
- 医生：`doctor` / `123456`
- 患者：`13800138000` / `110101199001011234`

待补：

- [ ] 录制演示视频
- [ ] 上传到可访问链接
- [ ] 在提交材料中填写视频链接

## 3. 程序代码压缩包

仓库现有材料：

- `README.md`
- `docs/deployment-guide.md`
- `docs/standards/PROJECT_STARTUP_GUIDE.md`
- `docker-compose.yml`
- `backend/pom.xml`
- `frontend-target/package.json`
- `frontend-platform/package.json`
- `patient-h5/package.json`
- `ai-agent/`
- `deploy/`

待补：

- [ ] 按提交要求打源码压缩包
- [ ] 排除 `.git`、`node_modules`、`target`、`dist` 等构建产物
- [ ] 压缩包内附完整环境依赖、部署步骤、运行指南

## 提交前检查

- [ ] `mvn test` 通过
- [ ] 前端 lint / test / build 通过
- [ ] 华为云线上环境可访问
- [ ] CodeArts 证据齐全
- [ ] PPT、视频链接、源码压缩包三项均已提交
