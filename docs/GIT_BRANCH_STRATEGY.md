# Git 三级分支协作规范

## 分支结构

```
main            ← 稳定上线代码，只能从 develop 合并，禁止直接 push
  └── develop   ← 公共开发分支，功能汇总测试，只能从个人分支合并
       ├── feature/1-patient-crud      ← 1号：患者管理
       ├── feature/1-followup-api      ← 1号：随访接口
       ├── feature/2-login-page        ← 2号：登录页
       ├── feature/2-patient-page      ← 2号：患者页面
       ├── feature/3-codearts-demo     ← 3号：智能体 Demo
       ├── feature/4-chat-panel        ← 4号：需求输入面板
       ├── feature/5-alert-rules       ← 5号：预警规则
       └── feature/6-docker-setup       ← 6号：Docker 环境
```

## 分支保护规则（组长在 GitHub 设置）

### main 分支
- ☑ Require a pull request before merging
- ☑ Require approvals: **2**（需要两个人 Review）
- ☑ Require status checks to pass before merging
- ☐ 禁止任何人直接 push

### develop 分支
- ☑ Require a pull request before merging
- ☑ Require approvals: **1**

## 操作流程

### 组员日常（以 1 号开发随访接口为例）

```bash
# 1. 每天早上：同步 develop 最新代码
git checkout develop
git pull origin develop

# 2. 创建个人功能分支
git checkout -b feature/1-followup-api

# 3. 写代码...

# 4. 提交（在个人分支上）
git add .
git commit -m "feat: 新增随访记录分页查询接口"

# 5. 推送到 GitHub
git push origin feature/1-followup-api

# 6. 去 GitHub 网页提 Pull Request
#    base: develop  ← compare: feature/1-followup-api
#    Reviewer 指定组长
```

### 组长日常

```bash
# 1. Review PR
#    在 GitHub 网页上查看代码变更，检查：
#    - 代码是否符合本方向标准
#    - 是否有 console.log / System.out.println
#    - 是否写在了错误的分层
#    - 字段名是否和 Swagger 文档一致

# 2. Merge PR（通过后点 Merge）
#    GitHub 网页上点 "Merge pull request"

# 3. 删除远程分支（Merge 后 GitHub 会提示）

# 4. 同步本地
git checkout develop
git pull origin develop
```

### 每周末：develop → main

```bash
# 组长操作
git checkout main
git merge develop
git push origin main
# 此时 main 分支更新为本周最新稳定版本
```

## 回滚方案

### 回滚一个 commit（保留改动在工作区）
```bash
git revert <commit-id>
git push
```

### 回滚整个分支到某个版本（危险操作）
```bash
git reset --hard <commit-id>
git push --force   # 仅在个人分支使用，永远不要 force push main 或 develop
```

## 冲突处理

### 预防
- 每个人只改自己负责的目录文件
- 提交前先 `git pull origin develop` 同步

### 解决
```bash
# 1. 在你的功能分支上
git checkout feature/1-followup-api
git merge develop   # 把 develop 的最新代码合并进来

# 2. 如果有冲突，Git 会提示哪些文件冲突
#    打开冲突文件，找到 <<<<<<< 和 >>>>>>> 标记
#    手动选择保留哪边，删掉标记

# 3. 标记冲突已解决
git add .
git commit -m "fix: 解决与 develop 的合并冲突"
git push origin feature/1-followup-api
```

## 组长设置清单

- [ ] GitHub 仓库 Settings → Branches → 创建 `main` 保护规则
- [ ] 同上 → 创建 `develop` 保护规则
- [ ] 本地创建 develop 分支并推送：
  ```bash
  git checkout -b develop
  git push origin develop
  ```
- [ ] 把现有的 feature 分支都 rebase 到 develop 上