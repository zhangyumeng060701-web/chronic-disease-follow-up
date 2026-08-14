# 测试与安全验证报告

## 1. 执行范围

本报告对应 `docs/standards/STANDARDS_5_TEST_SECURITY.md` 的第一周任务，覆盖自动化测试框架、慢病异常规则、数据脱敏规则、前端脱敏测试、后端最小测试基线和依赖安全审计。

## 2. 标准条目与测试用例映射

| 标准条目 | 落地文件 | 测试或验证方式 | 当前结果 |
|---|---|---|---|
| 后端 `src/test/` 测试框架 | `backend/src/test/java/com/example/followup/controller/HealthControllerTest.java` | MockMvc 验证 `/api/health` 返回统一成功响应 | 已编写，当前环境缺 JDK/Maven，未执行 |
| 后端 Service 示例测试 | `backend/src/test/java/com/example/followup/service/PatientServiceTest.java` | Mockito mock `PatientMapper`，验证分页、查询异常、新增默认状态、软删除 | 已编写，当前环境缺 JDK/Maven，未执行 |
| 前端 `__tests__/` 测试框架 | `frontend-target/src/__tests__/desensitize.test.js` | Vitest 验证脱敏纯函数 | 已执行，5 个用例通过 |
| 慢病异常规则清单 | `docs/alert-rules.md` | 对照标准文档和 `schema.sql` 中 10 条初始化规则 | 已完成 |
| 异常规则表设计 | `backend/src/main/resources/db/schema.sql` | 检查 `t_alert_rule` 建表和初始化 INSERT | 原项目已包含 |
| 数据脱敏规则文档 | `docs/desensitization-rules.md` | 覆盖姓名、手机号、身份证号、住址 | 已完成 |
| 前端脱敏组件 | `frontend-target/src/components/Desensitize.vue` | 组件复用 `maskSensitiveText`，管理员展示明文，非管理员展示脱敏值 | 已完成基础实现 |
| 与 CI/CD 协作 | `frontend-target/package.json` | 提供 `npm test` 命令 | 前端已可执行；后端需 JDK/Maven 环境 |

## 3. 新增和修改文件

新增文件：

- `docs/project-introduction.md`
- `docs/alert-rules.md`
- `docs/desensitization-rules.md`
- `docs/test-security-audit.md`
- `frontend-target/src/utils/desensitize.js`
- `frontend-target/src/components/Desensitize.vue`
- `frontend-target/src/__tests__/desensitize.test.js`
- `backend/src/test/java/com/example/followup/controller/HealthControllerTest.java`
- `backend/src/test/java/com/example/followup/service/PatientServiceTest.java`

修改文件：

- `frontend-target/package.json`
- `frontend-target/package-lock.json`

## 4. 测试命令与真实结果

### 前端测试

命令：

```bash
cd frontend-target
npm.cmd test
```

结果：

```text
Test Files  1 passed (1)
Tests       5 passed (5)
```

说明：测试覆盖姓名、手机号、身份证号、住址和按类型分发的脱敏逻辑。

### 后端测试

命令：

```bash
cd backend
mvn test
```

结果：

```text
mvn : 无法将“mvn”项识别为 cmdlet、函数、脚本文件或可运行程序的名称。
```

环境检查：

```bash
where.exe mvn
where.exe java
```

结果：当前环境均未找到 Maven 和 Java。

结论：后端测试代码已补充，但当前机器缺少 JDK/Maven，尚未执行。需要安装 JDK 11 和 Maven，或在 CI 环境中执行 `mvn test`。

## 5. 依赖安全审计

命令：

```bash
cd frontend-target
npm.cmd audit --audit-level=high
```

真实结果摘要：

- `echarts < 6.1.0`：存在 XSS 漏洞，修复需要升级到 `echarts@6.1.0`，属于破坏性升级。
- `esbuild <= 0.24.2`：开发服务器相关漏洞，影响 `vite`、`vitest` 依赖链。
- 审计汇总：`6 vulnerabilities (4 moderate, 1 high, 1 critical)`。

处理建议：

- 不建议在当前任务中直接执行 `npm audit fix --force`，因为会升级到破坏性版本，可能影响前端构建和页面行为。
- 建议后续单独建立 `fix/frontend-dependency-audit` 分支，升级 Vite、Vitest、ECharts 后完整执行 `npm test`、`npm run build` 和页面回归。

## 6. 通过项、失败项、未覆盖项

通过项：

- 前端测试框架已建立。
- 前端脱敏工具函数测试通过。
- 慢病异常规则清单已完成。
- 数据脱敏规则文档已完成。
- 前端脱敏组件基础实现已完成。

失败或阻塞项：

- 后端 `mvn test` 未执行成功，原因是当前环境未安装或未配置 Maven/JDK。
- 前端依赖安全审计发现高危和严重漏洞，但修复需要破坏性升级，未在本次任务中自动修复。

未覆盖项：

- 后端真实 Controller 集成测试尚未执行。
- 登录成功/失败测试尚未覆盖。
- JWT 鉴权链路尚未覆盖。
- 医生与管理员的数据权限测试尚未覆盖。
- 后端脱敏尚未实现，因此无法测试后端脱敏结果。
- 随访、预警、统计、用户、日志模块尚未实现或仍是占位，暂无法测试。

## 7. 剩余安全风险

- `application.yml` 中存在明文数据库密码和 JWT secret，建议改为环境变量。
- 登录逻辑当前硬编码账号密码，未接入 `t_user` 表和 BCrypt。
- JWT 过滤器未设置 Spring Security `Authentication`，受保护接口可能无法被真正认证。
- 患者接口直接返回实体，后端尚未统一脱敏。
- CORS 当前允许任意来源，生产环境应收敛到可信域名。
- 前端依赖存在审计风险，需要独立升级和回归。

## 8. 建议 Git 提交信息

```bash
git commit -m "test: add test framework and desensitization checks"
```

## 9. 分支推送命令

仅推送当前测试分支，不推送 `develop` 或 `main`：

```bash
git push origin feature/5-test-security
```

