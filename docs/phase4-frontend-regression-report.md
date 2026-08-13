# 第四阶段前端大版本升级回归报告

## 执行基线

- 分支：`feature/5-phase4-frontend-regression`
- 基线：`feature/5-phase3-integration-security-review`
- 日期：2026-08-13
- Node：24.13.0
- npm：11.6.2
- Vite：8.2.0
- Vitest：4.1.10
- ECharts：6.1.0
- Vue插件：6.0.8

## 完成内容

1. 引入Vue Test Utils和jsdom组件测试环境。
2. Vitest使用单Worker线程池，解决当前Windows环境派生Worker启动超时。
3. 抽取Dashboard趋势数据转换和ECharts option生成逻辑。
4. 验证Dashboard四接口加载、统计卡片、两图表初始化、空数据和API失败。
5. 验证ECharts resize处理及组件卸载后的监听移除和实例释放。
6. 验证登录成功、失败和loading恢复。
7. 验证ADMIN/DOCTOR菜单差异、用户显示和退出登录。
8. 验证无Token、有Token的路由守卫行为。
9. 保留患者更新载荷和前端脱敏回归测试。

## 自动化结果

```text
npm.cmd ci                         成功
npm.cmd test                       7个文件、34项测试全部通过
npm.cmd run build                  成功，2257个模块完成转换
npm.cmd audit --audit-level=high   0漏洞
```

## 回归覆盖

| 模块 | 结果 |
| --- | --- |
| Dashboard图表转换 | 通过 |
| ECharts初始化/setOption | 通过 |
| 空趋势数据 | 通过 |
| API失败保持页面挂载 | 通过 |
| resize与dispose生命周期 | 通过 |
| 登录流程 | 通过 |
| 主布局角色菜单 | 通过 |
| 路由认证守卫 | 通过 |
| 患者安全更新载荷 | 通过 |
| 脱敏工具函数 | 通过 |

## 人工冒烟状态

当前执行环境没有可用的已登录浏览器会话和真实后端测试数据，因此视觉与全栈人工冒烟标记为“待统一候选环境执行”，未虚报为通过。最终候选阶段需使用ADMIN和DOCTOR账号检查登录、Dashboard图表、患者脱敏和错误处理。

## 已知非阻塞项

- ECharts和Element Plus相关构建块超过500kB，记录为P2性能优化。
- `@vue/test-utils -> js-beautify -> glob@10.5.0`产生弃用提示；`npm audit`为0漏洞，仅存在于开发测试依赖链。

## 结论

第四阶段自动化质量门禁通过，Vite/Vitest/Vue插件/ECharts大版本升级的核心页面行为已形成可重复验证证据。完成最终候选环境人工冒烟后，可正式关闭前端升级P0。
