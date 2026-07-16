# Git 工作流 — 全组操作手册

> 每天都要用的东西，每个人必须会

---

## 一、一句话理解

```
你的电脑                    GitHub                      别人的电脑
────────                   ──────                      ────────
写代码 → commit → push →   feature/xxx  →  PR  →  Merge  →  pull
```

**不是**六个人最后一天拼代码。**是**每做完一个小功能就合并一次。

---

## 二、分支结构

```
main ─────────────────────────────────────────────→  最终上线（不准直接改）
  └── develop ────────────────────────────────────→  公共开发分支（汇总测试）
        ├── feature/patient-list-api ──→  1号后端：患者接口
        ├── feature/patient-list-page ─→  前端C：患者页面
        ├── feature/follow-up-api ────→  1号后端：随访接口
        ├── feature/alert-page ───────→  前端C：预警页面
        └── feature/test-patient ─────→  5号：患者测试
```

三条铁律：
- **main 分支受保护**，禁止直接 push，只能通过 PR 合并
- **develop 是公共开发分支**，所有人功能汇总后在这里测试
- **个人分支随便改**，不会影响别人

---

## 三、每天工作流（照做就行）

### 早上来第一件事

```bash
git checkout develop
git pull origin develop
```

把你不在的时候别人合并的代码同步下来。

### 开始写新功能

```bash
git checkout -b feature/你的功能名
```

分支名规则：`feature/做啥` 或 `fix/修啥`

### 写完一部分就提交（别攒一天）

```bash
git add .
git commit -m "feat: 完成了什么"
```

提交信息必须用下面这个格式：

```
feat:   新增功能    →  feat: 完成患者列表分页查询接口
fix:    修Bug      →  fix: 修复随访日期为空报错
docs:   改文档     →  docs: 更新API接口文档
test:   加测试     →  test: 补充预警规则测试用例
```

**不准写**："改了东西" "修了一下" "111"

### 做完推上去

```bash
git push origin feature/你的功能名
```

### 去 GitHub 网页提 Pull Request

1. 打开仓库页面，顶部会有黄色提示条，点 `Compare & pull request`
2. base 选 **develop**，compare 选你的分支
3. 标题写清楚做了什么
4. 点 `Create pull request`
5. 在 PR 页面 @组长 或同方向的队友 Review

### 合并后切回来继续干活

```bash
git checkout develop
git pull origin develop
git branch -d feature/你的功能名    # 删掉本地分支
```

---

## 四、冲突怎么办

两个人改了同一个文件时 Git 会标出来：

```
<<<<<<< HEAD
<el-button type="danger">删除</el-button>
=======
<el-button type="danger" size="small">删除</el-button>
>>>>>>> feature/xxx
```

处理步骤：
1. 手动选择保留哪个（或都保留）
2. 删掉 `<<<<<<<` `=======` `>>>>>>>` 标记
3. `git add .` → `git commit -m "fix: 解决合并冲突"` → `git push`

**防止冲突的最好办法：每个人只改自己负责的文件。**

---

## 五、完整流程示例

假设1号要做"随访记录列表接口"：

```bash
# 1. 早上同步
git checkout develop
git pull origin develop

# 2. 创建功能分支
git checkout -b feature/follow-up-list-api

# 3. 写代码...（在 IDEA 里写 FollowUpController.java）

# 4. 上午写完，提交一次
git add .
git commit -m "feat: 随访记录列表接口完成，支持按患者+日期筛选"

# 5. 下午写完测试，再提交一次
git add .
git commit -m "test: 补充随访记录接口测试用例"

# 6. 推上去
git push origin feature/follow-up-list-api

# 7. 去 GitHub 网页 → Pull requests → New pull request
#    base: develop ← compare: feature/follow-up-list-api
#    @组长 来 Review

# 8. Review 通过后，组长点 Merge
# 9. 切回 develop 继续
git checkout develop
git pull origin develop
```

---

## 六、GitHub 仓库设置（组长已配好）

| 设置 | 状态 |
|---|---|
| main 分支保护 | ✅ 禁止直接 push |
| PR 必须 Review | ✅ 至少1人 approve |
| 仓库地址 | `https://github.com/zhangyumeng060701-Web/chronic-disease-follow-up` |

---

## 七、常见错误 + 怎么修

| 报错 | 原因 | 解决 |
|---|---|---|
| `fatal: not a git repository` | 不在仓库目录里 | `cd` 到项目根目录 |
| `error: failed to push` | 别人先推了 | `git pull origin develop` 先同步 |
| `Permission denied` | 没被加为 Collaborator | 找组长加你 |
| merge conflict | 跟别人改了同一个文件 | 按上面第四节处理 |
| `detached HEAD` | 不在任何分支上 | `git checkout develop` |

---

## 八、绝对禁止

- ❌ 在 develop 上直接写代码（必须切 feature 分支）
- ❌ 攒一周代码周五一次性 push
- ❌ `git push --force`（强制推送会覆盖别人的代码）
- ❌ 把 `.env` / API Key / 密码提交到仓库
- ❌ 提交 `node_modules/` / `target/` 编译文件（.gitignore 已配好）
- ❌ commit 信息写"111" "改了点东西" "update"
