# PR Review Checklist

每个阶段任务提交 PR 后，至少由 1 位非作者 Review 通过再合并。

## 通用检查

- [ ] 只包含本阶段相关改动
- [ ] 提交信息符合 `<type>: <描述>` 格式
- [ ] 无 `.env`、token、API Key、密码提交
- [ ] 无 console.log、System.out、调试文件
- [ ] 无重复文档、重复配置文件

## 后端

- [ ] `mvn -B clean test` 通过
- [ ] Controller 不直接返回实体
- [ ] 权限校验已覆盖
- [ ] 分页、参数校验、异常处理一致

## 前端

- [ ] `npm test`、`npm run lint`、`npm run build` 通过
- [ ] 无行内样式、无魔数
- [ ] 路由权限正确
- [ ] 空状态 / loading / 错误状态完整

## 合并要求

- [ ] GitHub Actions 全绿
- [ ] 至少 1 个 Reviewer 同意
- [ ] 合并后同步更新任务状态
