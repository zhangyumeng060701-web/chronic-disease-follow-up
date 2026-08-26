# 正式测试与安全验收报告

## 1. 文档状态

- 验收分支：`integration/phase1-phase2`
- 基线：`origin/main@f1604df`
- 报告日期：2026-08-27
- 本文档是当前唯一正式测试与安全验收入口；历史 `test-security-audit.md` 仅作为第一周审计记录，不再代表最终状态。

## 2. 本轮范围

本轮覆盖跨阶段质量门禁、7/30 天失访预警、JWT 认证、患者与随访归属、预警处理权限、用户与日志管理员边界、后端脱敏、防止脱敏数据写回、前端单测及 Playwright 多角色用例。

## 3. 已实现的安全控制

| 控制项 | 结果 | 证据 |
|---|---|---|
| 未认证请求 | 返回 HTTP 401 | `SecurityConfig` authentication entry point |
| 用户与日志管理 | 仅 `ADMIN` | FilterChain + Service 双层校验 |
| 患者数据隔离 | 医生仅访问本人患者 | `PatientServiceImpl` 与访问控制测试 |
| 随访数据隔离 | 医生不能为他人患者新增或伪造 doctorId | `FollowUpServiceImpl` 与 `AuthorizationBoundaryTest` |
| 预警数据隔离 | 列表及处理均按患者责任医生过滤 | `AlertServiceImpl` 与 `AuthorizationBoundaryTest` |
| 后端脱敏 | 非管理员姓名、手机、身份证、地址脱敏 | `PatientServiceAccessControlTest` |
| 防止掩码写回 | 含 `*` 的敏感字段更新返回 400 | `PatientServiceAccessControlTest` |
| 失访预警 | 7 天黄色、30 天红色、同周期幂等 | 失访专项测试与 Flyway V2 |

## 4. 自动化结果

| 测试层 | 命令/方式 | 当前结果 |
|---|---|---|
| 前端单元测试 | `npm test` | 8 个文件、23 个用例通过 |
| 前端 Lint | `npm run lint` | 通过 |
| 前端生产构建 | `npm run build` | 通过 |
| npm 安全审计 | `npm audit --audit-level=high` | 0 个漏洞 |
| Playwright 用例发现 | `npm run test:e2e -- --list` | 2 个文件、4 个用例成功加载 |
| Playwright 实跑 | 需要三角色账号和运行中的前后端 | 待环境验收 |
| 后端 Maven 全量测试 | `mvn clean verify checkstyle:check` | 本机 JDK 下载不完整，待 CI/完整工具链验收 |

## 5. 多角色端到端矩阵

| 场景 | admin | doctorA | doctorB |
|---|---|---|---|
| A/B 患者列表 | 全部可见 | 仅 A | 仅 B |
| 他人患者详情 | 可见 | 403 | 403 |
| 患者敏感字段 | 管理员策略 | 脱敏 | 脱敏 |
| 用户管理 | 允许 | 403 | 403 |
| 操作日志 | 允许 | 403 | 403 |
| 他人预警处理 | 允许 | 403 | 403 |

## 6. 待验收项

1. 在具备完整 JDK/Maven 的环境运行后端全量测试和 Checkstyle。
2. 使用 MySQL 从 V1 升级至 V2，验证历史预警与唯一索引兼容。
3. 配置 admin、doctorA、doctorB 三组运行时凭据后执行 Playwright 实跑。
4. 检查医生统计接口在真实数据库中的跨医生隔离结果。

在上述四项完成前，结论为“代码级验收通过，环境级验收待完成”，不得标记为最终发布通过。
