# 团队协作规范

> 全组必须遵守 | 组长是最终执行监督者

---

## 一、Git 分支策略

```
main          ← 稳定最终上线代码，仅测试完全通过后合并（受保护，禁止直接 push）
develop       ← 公共开发分支，所有人功能完成后汇总测试
feature/xxx   ← 个人模块分支，每位组员单独创建，仅自己操作
fix/xxx       ← Bug 修复分支
```

### 操作流程

1. 每人从 `develop` 新建专属功能分支独立开发
2. 完成后提交 Pull Request，指定组长审查
3. 审查无误合并至 `develop` 分支，全量跑测试
4. 全阶段测试全部通过，再合并到 `main` 分支
5. 出现 bug 可快速撤销回退，不破坏主代码

### 每日工作流

```bash
# 早上第一件事
git checkout develop
git pull origin develop

# 创建/切换功能分支
git checkout -b feature/patient-list-api

# 写代码...

# 随时提交（小步提交，别攒一天）
git add .
git commit -m "feat: 完成患者列表分页查询"

# 推到 GitHub
git push origin feature/patient-list-api

# 去 GitHub 提 Pull Request，指定组长 Review
```

---

## 二、提交信息规范（强制）

```
格式: <type>: <简短描述>

type:
  feat     新增功能
  fix      修复 Bug
  docs     文档修改
  refactor 代码重构
  test     测试相关
  chore    构建/依赖/工具

示例:
  feat: 新增患者列表分页查询接口
  fix: 修复随访日期为空时的 NPE
  docs: 更新 Swagger 接口文档
```

---

## 三、分支保护规则

在 GitHub 仓库 Settings → Branches → Add rule：
- Branch name pattern: `main`
- ☑ Require a pull request before merging
- ☑ Require approvals: 1

---

## 四、禁止事项（全员）

- ❌ 禁止直接在 main 分支上写代码
- ❌ 禁止攒一周代码一次性 push
- ❌ 禁止把密码/密钥/API Key 提交到仓库
- ❌ 禁止在 Controller 里写业务逻辑
- ❌ 禁止在 .vue 文件里直接 axios.get，必须通过 api/ 封装
- ❌ 禁止用 `var`，用 `const`/`let`
- ❌ 禁止提交 `console.log` 或 `System.out.println`
- ❌ 禁止用 `DELETE` 物理删除，全部软删除
- ❌ 禁止硬编码配置值（数据库连接、密码等放 application.yml）

---

## 五、工具栈

| 用途 | 工具 |
|---|---|
| 代码存储 | GitHub |
| 版本协作 | Git |
| 任务管理 | 飞书看板（导入 task-breakdown.csv） |
| 接口测试 | Postman / Knife4j 文档页 |
| 沟通 | 微信/飞书群 |

---

## 六、Code Review 流程

1. 开发者推送分支后，创建 Pull Request
2. 指定同方向的人作为 Reviewer（前端 Review 前端，后端 Review 后端）
3. Reviewer 检查：
   - 代码是否符合本方向开发标准
   - 是否有 console.log / System.out.println
   - 是否有硬编码配置值
   - 是否写在了错误的分层
4. Review 通过后点 Merge → Delete branch
