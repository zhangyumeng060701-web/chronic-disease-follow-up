# 第三阶段集成兼容性与安全交叉自检报告

## 执行范围

- 分支：`feature/5-phase3-integration-security-review`
- 基线：`feature/5-phase2-security-desensitization`
- 日期：2026-08-13
- 目标：关闭脱敏值回写、Entity过度绑定、账号密码兼容和前端错误契约风险。

## 完成内容

1. 患者新增、更新接口改用专用请求DTO，不再接收数据库Entity。
2. 更新操作从现有Entity合并允许字段，避免请求覆盖ID、状态和时间字段。
3. 医生更新时忽略姓名、手机号、身份证、地址和doctorId。
4. 管理员提交带`*`的敏感字段时返回400，防止脱敏占位值入库。
5. 管理员部分更新未传字段时保留数据库原值。
6. 前端医生编辑时锁定敏感字段，并在提交载荷中彻底移除。
7. 401清除全部本地身份信息；400、403、404展示后端业务消息。
8. 用户创建和密码更新持续强制BCrypt；空密码更新显式保留原哈希。
9. 修复`nanoid` High漏洞，前端审计降为0漏洞。
10. Vite配置改为ESM及`import.meta.dirname`，关闭未来原生配置加载警告。

## 自动化验证

后端：

```text
mvn.cmd -B -ntp clean test
Tests run: 50, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

前端：

```text
npm.cmd ci                         成功
npm.cmd test                       2个文件、7项测试通过
npm.cmd run build                  成功
npm.cmd audit --audit-level=high   0漏洞
```

## 已知非阻塞项

- ECharts和Element Plus相关构建块超过500kB，属于P2性能优化，不影响本阶段安全正确性。
- 禁用用户旧Token、角色变更后旧Token和JWT密钥外置需要在后续令牌生命周期阶段处理。
- 数据库真实双角色人工冒烟需要可用MySQL测试数据和ADMIN/DOCTOR账号后执行。

## 结论

第三阶段自动化质量门禁通过。核心P0风险“医生将脱敏展示值写回数据库”已由前后端两层控制关闭。
