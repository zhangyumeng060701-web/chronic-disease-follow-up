# 开发工作流详解：从本地写代码到 GitHub 合并

> 给所有不知道"怎么合作写代码"的组员看

---

## 一、一句话解释整个流程

每个人在自己电脑上写自己负责的代码 → 写完一个功能就推到 GitHub 自己的分支上 → 提 Pull Request → 别人 Review 通过后合并 → 其他人拉下来就有了。

**不是**六个人最后一天把代码拼在一起。**是**每天、每完成一个小功能，就合并一次。

---

## 二、图解

```
你的电脑                         GitHub                        别人的电脑
─────────                      ────────                      ─────────
git clone ──────────────────→  仓库建好了
                                
写代码...
git add → git commit
git push ────────────────────→  feature/xxx 分支
                                
                                提 Pull Request
                                ↓
                                有人 Review
                                ↓
                                Merge 到 main ──────────────→  git pull
                                                              ↓
                                                              别人拿到你的代码
```

---

## 三、为什么不能一个人写完再合并

场景：前端 B 和后端 D 同时开发"患者管理"功能。

第 3 天：B 写完了患者列表页面，D 写完了患者列表接口。
→ 如果不合并：B 调 D 的接口不知道 URL 对不对，D 的接口不知道 B 需要什么字段。
→ 如果合并：B 立刻调，发现 `phone` 字段后端返回的是 `phoneNumber`，当天就能修正。

**越早合并，越早发现不一致，越少返工。**

---

## 四、从零到第一次提交（每个组员的完整操作）

### 第一步：克隆仓库到本地（只做一次）

```bash
# 打开终端，cd 到你放代码的目录
cd C:\Users\你的用户名\Documents

# 克隆仓库
git clone https://github.com/组长的用户名/chronic-disease-follow-up.git

# 进入仓库
cd chronic-disease-follow-up
```

### 第二步：创建你自己的分支

```bash
# 确保本地 main 是最新的
git checkout main
git pull origin main

# 创建并切换到新分支（以你要做的功能命名）
git checkout -b feature/patient-list-page
```

### 第三步：写代码

在你的 IDE 里正常写代码。比如前端在 `frontend/src/views/patient/PatientList.vue` 里写页面。

### 第四步：保存进度（提交）

```bash
# 看一下你改了什么
git status

# 添加你改的文件
git add .

# 提交（写清楚你做了什么）
git commit -m "feat: 完成患者列表页面，含搜索、分页、新增编辑弹窗"

# 推到 GitHub
git push origin feature/patient-list-page
```

第一次 push 某个分支时会提示你设置上游分支，终端会告诉你该运行什么命令，照着敲就行。

### 第五步：创建 Pull Request

1. 打开 GitHub 网页，进到你们的仓库
2. 通常顶部会出现黄色提示条："feature/patient-list-page had recent pushes"，点右边的 `Compare & pull request`
3. 如果没有黄色提示条，点 `Pull requests` → `New pull request`，base 选 `main`，compare 选你的分支
4. 标题写清楚做了什么
5. 在描述里 `@组员的GitHub用户名` 指定谁 Review
6. 点 `Create pull request`

### 第六步：等 Review 通过后合并

- Reviewer 在 PR 页面看你的代码改动，没问题就点 `Merge pull request`
- 合并完成后，PR 页面会提示"可以删除这个分支了"，点 `Delete branch` 删掉远程分支
- 你本地切回 main，拉最新代码：

```bash
git checkout main
git pull origin main
```

然后开始下一个功能的开发，重复第二步。

---

## 五、别人合并了代码我怎么拿到

```bash
git checkout main
git pull origin main
```

如果你的分支还没合入 main，需要把 main 的最新内容合并到你的分支：

```bash
git checkout feature/patient-list-page   # 切回你的分支
git merge main                           # 把 main 合进来
# 如果有冲突，手动解决冲突后：
git add .
git commit -m "fix: 合并 main 分支"
git push origin feature/patient-list-page
```

---

## 六、常见问题

### Q: 我写到一半，还没完成，要先提交吗？

要。提交不代表"做完了"，只代表"保存一个版本"。你可以随时 commit，随时 push。

```bash
git commit -m "wip: 患者列表页面开发中"   # wip = work in progress
```

### Q: 我和别人改了同一个文件，合并时报冲突怎么办？

Git 会在文件里标出冲突位置：

```
<<<<<<< HEAD
        <el-button type="danger">删除</el-button>
=======
        <el-button type="danger" size="small">删除</el-button>
>>>>>>> feature/xxx
```

你手动决定保留哪一个（或者都保留），删掉 `<<<<<<<` `=======` `>>>>>>>` 这些标记，然后：

```bash
git add .
git commit -m "fix: 解决与 feature/xxx 的合并冲突"
```

### Q: 我 git push 的时候提示权限不够？

你没有被加为仓库的 Collaborator。找组长，让他去 GitHub 仓库 Settings → Collaborators 里加你的 GitHub 账号，权限选 Write。

### Q: 我怎么知道接口调用的字段名是什么？

看 `docs/` 目录下的 Swagger 接口文档。所有字段名（JSON key）都在文档里写死了。
前端用文档里写的字段名调接口，后端按文档里写的字段名返回数据。

### Q: commit 信息写什么格式？

```
feat:  新增功能       → feat: 完成患者列表分页查询
fix:   修复 Bug       → fix: 修复血压超标判断条件错误
docs:  改文档         → docs: 更新 Swagger 接口文档
refactor: 重构代码    → refactor: 抽取公共分页逻辑
test:  加测试         → test: 补充预警规则单元测试
chore: 杂项           → chore: 更新依赖版本
```

---

## 七、每天的工作节奏（建议）

- **早上**：`git pull origin main` 拿最新代码 → 开始写
- **中午**：`git commit` 保存一次 → 继续写
- **下午收工前**：`git commit` → `git push` 推到自己的分支
- **功能做完**：去 GitHub 提 Pull Request

**禁止**：攒了一周的代码周五一次性 push。
**禁止**：直接在 main 分支上写代码。
