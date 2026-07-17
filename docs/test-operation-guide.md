# 测试操作介绍与团队指南

## 1. 适用范围

本文面向团队后续开发、联调、CI/CD 和运维阶段，说明慢病随访系统的测试职责、测试命令、测试目录、常见问题排查和安全检查方法。

适用模块：

- 后端 Spring Boot 接口与业务逻辑测试。
- 前端 Vue/Vite 单元测试。
- 数据脱敏和安全规则验证。
- 后续 CI/CD 流水线接入。

## 2. 测试环境要求

后端测试需要：

- JDK 11 或更高版本。
- Maven 3.8 或更高版本。
- 推荐命令行能直接执行 `java -version` 和 `mvn -version`。

前端测试需要：

- Node.js 18 或更高版本。
- npm。
- 推荐 Windows PowerShell 下使用 `npm.cmd`，避免执行策略拦截 `npm.ps1`。

数据库相关测试需要：

- MySQL 8.0。
- 或通过根目录 `docker-compose.yml` 启动 MySQL。

## 3. 测试目录说明

后端测试目录：

```text
backend/src/test/java/com/example/followup/
├── controller/
│   └── HealthControllerTest.java
└── service/
    └── PatientServiceTest.java
```

当前后端测试覆盖：

- `HealthControllerTest`：使用 MockMvc 验证 `/api/health` 统一响应。
- `PatientServiceTest`：使用 Mockito 验证患者分页、查询异常、新增默认状态和软删除。

前端测试目录：

```text
frontend-target/src/__tests__/
└── desensitize.test.js
```

当前前端测试覆盖：

- 姓名脱敏。
- 手机号脱敏。
- 身份证号脱敏。
- 地址脱敏。
- 按敏感字段类型分发脱敏规则。

## 4. 常用测试命令

### 4.0 使用项目内便携 JDK/Maven

如果本机没有安装 JDK 和 Maven，可以使用项目内 `.tools/` 下的便携工具链：

```powershell
$env:JAVA_HOME=(Resolve-Path '.tools\jdk-17.0.19+10').Path
$env:MAVEN_HOME=(Resolve-Path '.tools\apache-maven-3.9.16').Path
$env:Path="$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:Path"
java -version
mvn -version
```

进入 `backend/` 后使用：

```powershell
$env:JAVA_HOME=(Resolve-Path '..\.tools\jdk-17.0.19+10').Path
$env:MAVEN_HOME=(Resolve-Path '..\.tools\apache-maven-3.9.16').Path
$env:Path="$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:Path"
mvn test
```

### 4.1 前端测试

```bash
cd frontend-target
npm.cmd install
npm.cmd test
```

预期结果：

```text
Test Files  1 passed
Tests       5 passed
```

### 4.2 后端测试

```bash
cd backend
mvn test
```

预期结果：

```text
BUILD SUCCESS
```

### 4.3 前端构建检查

```bash
cd frontend-target
npm.cmd run build
```

### 4.4 依赖安全审计

```bash
cd frontend-target
npm.cmd audit --audit-level=high
```

注意：不要在业务分支中直接执行 `npm audit fix --force`。该命令可能升级到破坏性版本，应单独开依赖升级分支并做完整回归。

## 5. 测试流程建议

每个功能分支提交前执行：

1. 同步目标分支最新代码。
2. 运行与本次修改相关的单元测试。
3. 运行前端或后端全量测试。
4. 检查 `git diff`，确认没有提交密钥、密码、Token 或真实患者数据。
5. 提交 PR，并在 PR 描述中写明测试命令和测试结果。

推荐 PR 描述格式：

```text
测试范围：
- 患者服务单元测试
- 前端脱敏规则测试

测试命令：
- cd backend && mvn test
- cd frontend-target && npm.cmd test

测试结果：
- 后端：通过 / 未执行，原因：...
- 前端：通过

剩余风险：
- ...
```

## 6. 安全测试关注点

### 6.1 敏感配置

检查文件：

- `backend/src/main/resources/application.yml`
- `.env`
- `.env.local`

禁止提交：

- 数据库真实密码。
- JWT secret。
- API Key。
- 证书私钥。
- 真实患者数据。

### 6.2 数据脱敏

后端应作为主要安全边界：

- 非管理员查询患者数据时，后端应返回脱敏数据。
- 前端 `Desensitize.vue` 仅作为展示兜底。

当前前端脱敏测试命令：

```bash
cd frontend-target
npm.cmd test
```

### 6.3 权限与认证

后续需要重点补充：

- 未登录访问受保护接口应返回 401。
- 医生不能查看其他医生负责的患者。
- 管理员可以查看全部患者。
- JWT 过期或伪造时应拒绝访问。

### 6.4 依赖漏洞

定期执行：

```bash
cd frontend-target
npm.cmd audit --audit-level=high
```

发现高危漏洞后，建议单独建立修复分支：

```bash
git checkout -b fix/frontend-dependency-audit
```

升级依赖后必须执行：

```bash
npm.cmd test
npm.cmd run build
```

## 7. 后端测试编写规范

Controller 测试建议：

- 使用 MockMvc。
- 验证 HTTP 状态码。
- 验证统一响应字段：`code`、`data`、`message`。

Service 测试建议：

- 使用 Mockito mock Mapper。
- 不依赖真实数据库。
- 验证业务规则，例如软删除、默认状态、异常抛出。

命名建议：

```text
方法名_shouldExpectedBehavior_whenCondition
```

示例：

```text
deletePatient_shouldSoftDeletePatient
getPatientById_shouldThrowWhenPatientMissing
```

## 8. 前端测试编写规范

优先测试纯函数：

- 脱敏规则。
- 日期格式化。
- 状态映射。
- 表单校验辅助函数。

原因：

- 运行快。
- 不依赖浏览器。
- 失败定位清晰。

页面级测试和 E2E 测试建议后续在功能稳定后补充。

## 9. 常见问题排查

### 9.1 PowerShell 无法执行 npm

现象：

```text
无法加载 npm.ps1，因为在此系统上禁止运行脚本
```

处理：

```bash
npm.cmd test
```

### 9.2 找不到 mvn

现象：

```text
mvn 不是内部或外部命令
```

处理：

- 安装 Maven。
- 配置 `MAVEN_HOME`。
- 将 `%MAVEN_HOME%\bin` 加入 PATH。
- 重新打开终端后执行 `mvn -version`。

### 9.3 找不到 java

现象：

```text
java 不是内部或外部命令
```

处理：

- 安装 JDK 11 或更高版本。
- 配置 `JAVA_HOME`。
- 将 `%JAVA_HOME%\bin` 加入 PATH。
- 重新打开终端后执行 `java -version`。

## 10. CI/CD 接入建议

后续流水线建议包含：

```bash
cd backend
mvn test

cd ../frontend-target
npm.cmd ci
npm.cmd test
npm.cmd run build
npm.cmd audit --audit-level=high
```

如果流水线使用 Linux 环境，将 `npm.cmd` 改为 `npm`。

## 11. 当前已知限制

- 后端测试依赖 JDK/Maven，本地未配置时无法执行。
- 后端脱敏尚未实现，因此目前只能测试前端展示兜底。
- 随访、预警、统计、用户、日志模块尚未完整实现，对应测试需要后续补齐。
- 前端依赖审计已发现风险，建议单独分支处理依赖升级。
