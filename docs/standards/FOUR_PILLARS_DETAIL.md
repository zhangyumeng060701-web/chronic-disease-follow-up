# 四定详解：技术栈、接口、分工、协作流程

> 组长专用 | 读完这份文档，你就能给团队开会下达任务

---

## 一、定技术栈 — 为什么是这套，以及怎么搭

### 1.1 核心原则：别自己选，用"最大公约数"

你们六个人都不会写代码，这意味着技术选型只有一个标准：
**哪个方案的中文教程最多、出 Bug 时最容易搜到答案。**

大学生做比赛的主流组合就是 **Vue 3 + Spring Boot + MySQL**，没有之一。
这不是最优解，但它是"最不会翻车"的解。

### 1.2 每一层的选择理由

#### 前端：Vue 3 + Element Plus

- Vue 3 中文文档是全的（cn.vuejs.org），React 中文资料少且散
- Element Plus 是一个"已经写好样式的组件库"——按钮、表格、表单、弹窗都是现成的，拿来就用
- 你不需要写 CSS，直接用 Element Plus 的组件拼页面
- 学习路径：Vue 3 官方教程 2 天 → Element Plus 组件 demo 跑一遍 1 天 → 就能干活

替代品为什么不选：
- React：学习曲线陡，中文资料少，你八周耗不起
- 原生 HTML+JS：没有组件库加持，写表格和表单会累死

#### 后端：Spring Boot 2.7 + MyBatis-Plus

- Spring Boot 是 Java 生态里"开箱即用"的框架，国内 90% 的 Java 后端项目用它
- MyBatis-Plus 让你**不写 SQL**就能操作数据库（增删改查一句话的事）
- 华为 CodeArts 对 Java/Spring Boot 有原生支持（代码生成、安全扫描等）
- 学习路径：B站搜"Spring Boot 入门"挑播放量最高的看前 5 集，3 天能跑通一个接口

替代品为什么不选：
- Python Flask：轻量但 CodeArts 支持弱，且做权限系统麻烦
- Node.js Express：异步模型对新手不友好，报错信息难读

#### 数据库：MySQL 8.0

- 没有争议的选择。医疗系统一律关系型数据库。
- 学习路径：会 `CREATE TABLE`、`SELECT`、`INSERT`、`UPDATE`、`DELETE` 即可。MyBatis-Plus 帮你写了 90% 的 SQL。

### 1.3 每人需要装什么

| 角色 | 软件 |
|---|---|
| 全员 | Git、VS Code（前端用）/ IntelliJ IDEA Community（后端用） |
| 前端 B+C | Node.js 18+（去 nodejs.org 下载 LTS 版） |
| 后端 D+E | JDK 11+（去 adoptium.net 下载）、MySQL 8.0 |
| 测试 F | Postman（调接口）、JDK、Node.js |

---

## 二、定接口 — 什么是接口，以及你为什么是"接口负责人"

### 2.1 用一句话理解"接口"

**接口 = 前端和后端之间的合同。**

前端说"我要患者列表"，后端说"给你"。合同上写清楚：
- 前端用哪个 URL 请求（`GET /api/patients`）
- 前端传什么参数（`?page=1&name=张三`）
- 后端返回什么数据（`{ records: [...], total: 100 }`）

合同定了之后，前端和后端各自开发，最后对接时不需要"商量"，合同怎么写怎么调。

### 2.2 接口文档长什么样

以上面项目中的"获取患者列表"为例，一份完整的接口定义包含：

```
接口名称：分页查询患者列表
请求方式：GET
请求路径：/api/patients
请求参数：
  - page      整数  必填  页码（默认 1）
  - size      整数  必填  每页条数（默认 20）
  - name      字符串 选填  患者姓名（模糊查询）
  - diseaseType 字符串 选填  慢病类型：HYPERTENSION / DIABETES / BOTH
返回数据：
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "name": "张三",
        "gender": "男",
        "age": 65,
        "phone": "138****5678",        // 脱敏后
        "idCard": "320102****1234",    // 脱敏后
        "diseaseType": "HYPERTENSION",
        "doctorName": "李医生",
        "createTime": "2026-01-15"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 20
  },
  "message": "success"
}
```

### 2.3 Swagger / Knife4j 是什么

Swagger 是一套"把接口文档自动生成网页"的工具。后端写接口时加几个注解，启动项目后访问 `http://localhost:8080/doc.html` 就能看到一个网页版接口文档，前端在这上面可以直接点"发送请求"测试。

Knife4j 是 Swagger 的国内增强版，界面更好看，中文友好。

### 2.4 你为什么是接口负责人

因为接口是前后端之间的唯一桥梁。如果你的接口定义不清楚，前端就会乱调、后端就会乱写，最后联调时全员抓狂。

你的工作模式：
1. 第 2 周，你写出所有接口的详细定义（参考上面 25 个接口清单）
2. 把定义发给后端组，让他们确认每个接口技术上能不能实现
3. 确认后，接口文档就是冻结状态。任何改动必须经过你同意
4. 前后端开发期间，你是"接口仲裁者"——谁对接口理解有分歧，你来拍板

---

## 三、定分工 — 六个人，八个模块，怎么分

### 3.1 分工原则

- 原则一：**一个人只学一个方向**。别让同一个人又写前端又写后端，学习成本翻倍。
- 原则二：**主力搭骨架，辅助填血肉**。每个方向一个主力搭框架、写核心逻辑，辅助负责具体页面/接口。
- 原则三：**测试独立**。写测试的人不能是写代码的人，否则 bug 测不出来。

### 3.2 各角色具体任务拆解

#### 组长 A（你）

八周产出物清单：
- 第 1 周：需求文档
- 第 2 周：Swagger 接口文档（25 个接口完整定义）
- 第 3-6 周：GitHub 管理、进度追踪、接口争议仲裁
- 第 7 周：技术报告（~8000 字）
- 第 8 周：答辩 PPT + 模拟演练

你不是"不用写代码"，你是"写的东西比代码更重要"。

#### 前端主力 B

学习路线：
1. Vue 3 基础（模板语法、响应式、组件）→ 3 天
2. Element Plus 组件（表格、表单、弹窗、布局）→ 2 天
3. Axios 发请求、Vue Router 路由 → 2 天

开发任务（按顺序）：
1. 搭项目脚手架、路由配置、全局布局（侧边栏 + 顶栏 + 内容区）
2. 登录页面 + 登录逻辑
3. 患者管理页面（列表 + 搜索 + 新增/编辑弹窗）
4. 统计看板页面（ECharts 图表）

#### 前端辅助 C

学习路线：同 B，但可以比 B 慢半拍

开发任务：
1. 随访记录页面（列表 + 新增/编辑弹窗）
2. 预警页面（预警列表 + 处理按钮）
3. 系统管理页面（用户列表 + 操作日志）
4. 辅助 B 联调和修 UI bug

#### 后端主力 D

学习路线：
1. Java 基础语法（如果 Java 也不会，需要 3 天恶补）
2. Spring Boot 入门（B站视频前 5 集）→ 3 天
3. MyBatis-Plus 快速开始 → 1 天
4. Knife4j 集成 → 1 天

开发任务（按顺序）：
1. 搭 Spring Boot 项目、连数据库、配 Knife4j
2. 数据库建表（5 张表：patient、follow_up、alert、user、operation_log）
3. 认证模块（登录/登出 + JWT）
4. 患者管理接口（CRUD）
5. 随访记录接口（CRUD）
6. 预警接口（自动触发逻辑）

#### 后端辅助 E

学习路线：同 D

开发任务：
1. 统计看板接口（聚合查询）
2. 用户管理接口（CRUD）
3. 操作日志接口
4. 数据脱敏逻辑
5. 权限校验（医生只能看自己的患者）

#### 测试 + 部署 F

学习路线：
1. Postman 接口测试 → 1 天
2. JUnit 5 单元测试 → 2 天
3. GitHub Actions → 2 天
4. Docker 基础 → 2 天

开发任务：
1. 每完成一个接口，写对应的集成测试
2. 预警规则专项测试（造边界数据触发预警）
3. 权限测试（用不同角色登录验证数据隔离）
4. 配置 GitHub Actions：每次 PR 自动跑编译 + 测试
5. Dockerfile 编写 + 部署到华为云

### 3.3 为什么垂直分工而不是水平分工

❌ 水平分工（不要这样）：A 做所有页面、B 做所有接口、C 做所有数据库
→ 问题：B 的接口必须等 C 的数据库建完才能写，阻塞整个链条

✅ 垂直分工（应该这样）：按功能模块分
→ 患者模块：C 写患者页面，D 写患者接口，不互相阻塞
→ 接口文档先定好，前端 mock 假数据开发，后端对着接口文档写逻辑，互不依赖

---

## 四、定协作流程 — GitHub 从建仓库到合并代码全流程

### 4.1 什么是 Git 和 GitHub（用一句话说清楚）

- **Git**：一个"存档"工具，每次 `commit` 就是给代码拍一张快照，随时可以回退到任意版本
- **GitHub**：一个放 Git 存档的网站，让你和组员共享同一份代码，各改各的然后合并

### 4.2 仓库结构怎么设计

你们的 GitHub 仓库 (`chronic-disease-follow-up`) 里面放两个独立项目：

```
chronic-disease-follow-up/    ← 这是 GitHub 仓库的根
├── frontend/                 ← Vue 3 项目（前端 B+C 的工作区）
│   ├── src/
│   ├── package.json
│   └── ...
├── backend/                  ← Spring Boot 项目（后端 D+E 的工作区）
│   ├── src/
│   ├── pom.xml
│   └── ...
├── docs/                     ← 文档（组长 A 的工作区）
│   ├── swagger-api.yml
│   ├── requirements.md
│   └── ...
└── README.md
```

关键：前端和后端虽然在同一个仓库里，但是两个独立的项目。前端用 `npm` 管理依赖，后端用 `maven` 管理依赖，互不干扰。

### 4.3 组长建仓库步骤（一步一截图级详细）

#### 第一步：在 GitHub 网站建仓库

1. 打开 https://github.com，登录你的账号
2. 点右上角 `+` → `New repository`
3. Repository name：`chronic-disease-follow-up`
4. Description：`挑战杯-慢病随访系统智能化维护方案`
5. 选 Public（公开仓库不收费）
6. ☑ Add a README file（勾上）
7. 点 `Create repository`

#### 第二步：设置分支保护

1. 进仓库页面 → 点 `Settings`
2. 左边菜单点 `Branches`
3. 在 "Branch protection rules" 点 `Add rule`
4. Branch name pattern 填 `main`
5. 勾选：
   - ☑ Require a pull request before merging
   - ☑ Require approvals（默认 1 即可）
6. 点 `Create`

这意味着：**任何人都不能直接改 main 分支的代码，必须通过 Pull Request 让别人 Review 后才能合并。** 这个规则防止有人手滑把坏代码直接推到主线。

#### 第三步：加组员

1. `Settings` → `Collaborators` → `Add people`
2. 搜索组员的 GitHub 用户名，添加
3. 权限选 `Write`（不是 Admin）

#### 第四步：把仓库拉到本地

打开终端（PowerShell），执行：

```bash
#  cd 到你放项目的目录
cd C:\Users\86176\Documents\Codex\2026-07-15\codearts-codearts

# 把你的仓库克隆到本地
git clone https://github.com/你的用户名/chronic-disease-follow-up.git

# 进入仓库
cd chronic-disease-follow-up
```

#### 第五步：创建文件夹骨架并推送

```bash
# 创建目录
mkdir frontend\src, backend\src, docs

# 创建占位文件
echo "# 慢病随访系统 - 前端" > frontend\README.md
echo "# 慢病随访系统 - 后端" > backend\README.md
echo "# 文档目录" > docs\README.md

# 提交并推送
git add .
git commit -m "chore: 初始化项目目录结构"
git push origin main
```

现在组员就能 `git clone` 这个仓库了。

### 4.4 组员日常工作流（每个人都要会）

假设后端 D 要开发"患者列表接口"：

```bash
# 1. 确保本地 main 是最新的
git checkout main
git pull origin main

# 2. 创建功能分支
git checkout -b feature/patient-list-api

# 3. 写代码...（在 IDE 里写）

# 4. 随时保存进度
git add .
git commit -m "feat: 新增患者列表分页查询接口"

# 5. 推到 GitHub
git push origin feature/patient-list-api
```

然后在 GitHub 网页上：
1. 点 `Compare & pull request`
2. 标题写清楚做了什么
3. 在描述里 `@` 另一个后端同学作为 Reviewer
4. 点 `Create pull request`

Reviewer 收到通知后查看代码，没问题就点 `Merge pull request`。

### 4.5 冲突怎么办

两个人都改了同一个文件时 Git 会报冲突。解决步骤：

1. 找到被标记冲突的文件（Git 会用 `<<<<<<<` 和 `>>>>>>>` 标出来）
2. 手动选择保留哪一方的修改（或者都保留）
3. 删掉 `<<<<<<<` `=======` `>>>>>>>` 这些标记
4. `git add .` → `git commit -m "fix: 解决合并冲突"`
5. `git push`

**防止冲突的最佳实践：每个人只改自己负责的目录。** 前端的人别动 `backend/`，后端的人别动 `frontend/`。

### 4.6 每日检查清单（组长专用）

每天早上打开 GitHub 仓库看一眼：

| 检查项 | 怎么看 |
|---|---|
| 有没有新的 PR 没人 Review | Pull requests 页面，看"Open"的列表 |
| main 分支有没有坏 | 看仓库顶部有没有红色 × |
| 有人三天没提交了 | 点 Insights → Contributors，看提交日历 |

这些是你每天花 10 分钟就能做完的事，不需要会写代码。

---

## 五、你现在就可以做的事（按顺序）

1. **立刻**：把本文档和前一份 PROJECT_STARTUP_GUIDE.md 发到团队群
2. **今天**：拉群开会，确定谁当 B/C/D/E/F
3. **今天**：去 github.com 建仓库，按上面第四节的步骤走一遍
4. **本周内**：盯着每个人装好软件、跑通 Hello World
5. **下周末前**：把需求文档和 Swagger 接口文档写完

---

> 两份文档加在一起 ≈ 你的组长操作手册。遇到任何具体问题（GitHub 操作报错、某个接口不知道怎么定义、组员不配合），直接问我。
