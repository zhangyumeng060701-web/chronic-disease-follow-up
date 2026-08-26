# 正式测试与安全验收报告

## 1. 文档状态

- 验收分支：`integration/phase1-phase2`
- 基线：`origin/main@f1604df`
- 报告日期：2026-08-27
- 本文档是唯一正式测试与安全验收入口；历史 `test-security-audit.md` 仅保留为阶段审计记录。

## 2. 验收范围

本轮覆盖跨阶段质量门禁、7/30 天失访预警、JWT 认证、患者/随访/预警归属、管理端权限边界、后端与前端脱敏、防止掩码数据写回、Flyway 历史升级、三角色 API E2E 以及浏览器登录安全闭环。

## 3. 安全控制结论

| 控制项 | 结果 | 证据 |
|---|---|---|
| 未认证及伪造令牌 | HTTP 401 | Spring Security + Playwright forged-token 用例 |
| 用户与日志管理 | 仅 ADMIN；医生返回 403 | FilterChain、Service 双层校验及三角色 E2E |
| 患者数据隔离 | 医生仅访问本人患者，跨医生详情返回 403 | `PatientServiceImpl`、边界单测、真实 MySQL E2E |
| 随访与预警隔离 | 禁止伪造 doctorId，列表及处理按责任医生过滤 | `FollowUpServiceImpl`、`AlertServiceImpl`、边界单测 |
| 敏感字段脱敏 | 非管理员姓名、手机、身份证、地址脱敏 | 服务单测与 Playwright 脱敏断言 |
| 防止掩码写回 | 含 `*` 的敏感字段更新返回 400 | `PatientServiceAccessControlTest` |
| 医生统计隔离 | 管理员看到 2 个医生；doctorA/doctorB 分别仅返回 102/103 | 隔离 MySQL 三令牌实测 |
| 失访预警 | 7 天黄色、30 天红色、同周期幂等，随访后自动关闭 | 专项单测、Flyway V2、服务实现 |

## 4. 自动化结果

| 测试层 | 命令/方式 | 结果 |
|---|---|---|
| 后端全量测试 | `mvn --batch-mode verify` | 73 项，0 失败、0 错误、0 跳过 |
| 后端规范门禁 | `mvn --batch-mode checkstyle:check` | 0 违规，严格失败开关已启用 |
| 前端单元测试 | `npm test` | 8 个文件、23 项通过 |
| 前端 Lint | `npm run lint` | 通过 |
| 前端生产构建 | `npm run build` | 通过 |
| npm 安全审计 | `npm audit --audit-level=high` | 0 个漏洞 |
| Playwright API/浏览器 E2E | `npm run test:e2e` | 2 个文件、4 项全部通过 |

## 5. 数据库迁移验收

- 隔离环境：MySQL 9.5，临时端口 33307，不使用本机既有实例。
- 空库启动：Flyway V1、V2 均成功，历史表记录版本 1/2 且 `success=1`。
- 历史升级：先执行 V1，写入旧格式失访预警，再执行 V2；旧记录保留，新增 `source_due_date` 为 NULL。
- 结构检查：`source_due_date DATE` 存在；`uk_lost_follow_up_cycle` 唯一复合索引完整存在。
- JDBC 兼容修复：连接参数从 Java 不识别的 `utf8mb4` 改为 `UTF-8`；数据库字符集仍为 `utf8mb4`。

## 6. 三角色端到端矩阵

| 场景 | admin | doctorA | doctorB |
|---|---|---|---|
| A/B 患者列表 | 全部可见 | 仅 A | 仅 B |
| 他人患者详情 | 可见 | 403 | 403 |
| 敏感字段 | 管理员策略 | 脱敏 | 脱敏 |
| 用户管理/操作日志 | 允许 | 403 | 403 |
| 医生对比统计 | 全部医生 | 仅本人 | 仅本人 |
| 伪造令牌 | 401 | 401 | 401 |
| 登录→退出→访问受保护路由 | 退出后重定向登录页 | 同一守卫策略 | 同一守卫策略 |

## 7. 交叉自检与发布结论

交叉自检采用三条独立证据链：服务层单元/边界测试、真实 MySQL 的 API E2E、Chromium 浏览器安全流程。历史 V1→V2 升级另行从空白历史库验证，避免只依赖应用启动日志。

阶段四环境级验收通过，可进入合并候选分支检查。合并到 `main` 与推送远端仍需在候选分支 CI 全绿后执行。
