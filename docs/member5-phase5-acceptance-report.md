# 成员 5 第五阶段验收报告

## 验收对象

- 执行日期：2026-08-14（Asia/Shanghai）
- 工作分支：`feature/5-cross-stage-hardening`
- 基线提交：`8e957c5d317745f20206af4ad5023ad74a0ffe15`
- 候选状态：基线提交之上的未提交工作树；最终提交后应以新提交哈希替换本项
- 本地环境：Windows、OpenJDK 17.0.19、Maven 3.9.16、Node.js 24.13.0、npm 11.6.2
- CI 固定环境：Temurin JDK 11、Node.js 22

## 本阶段新增覆盖

- 10 条预警规则各包含“精确命中阈值”和“紧邻阈值下方不命中”测试，共 20 个边界用例。
- 验证首次异常不产生“连续 2 次”预警，以及缺失随访记录返回 404。
- 验证预警处理状态和处理时间写回，以及不存在预警返回 404。
- 验证统计总览完成率、空统计值和零分母处理。
- 验证操作日志只写入操作元数据，不记录患者请求正文。
- 验证用户编辑不能覆盖用户名、状态和 ID，用户列表使用无密码响应模型。
- 验证退出登录仅清除认证键，不删除无关站点数据。

## 实际执行结果

| 验收项 | 命令 | 结果 |
|---|---|---|
| 后端干净构建与全量测试 | `mvn clean test` | 78 项通过，0 失败，0 错误，0 跳过，退出码 0 |
| 前端干净安装 | `npm ci` | 成功，按锁文件安装 219 个包，退出码 0 |
| 前端全量测试 | `npm test` | 8 个测试文件、35 项测试全部通过，退出码 0 |
| 前端生产构建 | `npm run build` | 成功，退出码 0 |
| 依赖安全审计 | `npm audit --audit-level=high` | 0 个漏洞，退出码 0 |

后端 Surefire 原始报告生成于 `backend/target/surefire-reports/`，该目录属于构建产物，不提交 Git；CI 会将其上传为 `backend-surefire-reports` 构件。

## CI 门禁

`.github/workflows/quality-gate.yml` 在 Pull Request 以及 `main`、`develop` 推送时执行：

1. 后端 `mvn --batch-mode clean test`；
2. 前端 `npm ci`；
3. 前端 `npm test`；
4. 前端 `npm run build`；
5. 前端 `npm audit --audit-level=high`；
6. 无论后端测试成功或失败，均上传 Surefire 报告。

仓库管理员仍需在 GitHub 分支保护中把两个 Job 设置为必需状态检查；该设置属于远端仓库权限配置，不能仅通过代码提交强制开启。

## 已知非阻塞项

- Vite 构建提示部分产物超过 500 kB，主要来自 ECharts 与 UI 依赖；不影响正确性，后续可通过路由和图表按需拆包优化。
- `npm ci` 输出 `glob@10.5.0` 的弃用提示，但 `npm audit` 返回 0 个漏洞；建议依赖上游发布兼容版本后再受控升级。
- Windows 本地并行运行前端测试和生产构建时曾出现单个 worker 启动超时；按 CI 的串行顺序复跑后 35 项全部通过，因此门禁保持串行执行。
- CI 工作流需推送到远端后才能取得首次 GitHub Actions 运行记录。

## 结论

本地质量门禁通过。提交前需再次执行差异检查并更新本报告中的最终提交哈希；推送后需确认 GitHub Actions 两个 Job 通过，并由仓库管理员启用必需状态检查。
