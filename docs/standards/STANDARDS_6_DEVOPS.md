# 6号 第一周标准 — 部署运维 + 文档/答辩

## 本周产出物

| 产出 | 文件名 | 完成标志 |
|---|---|---|
| 团队 Git 协作文档 | `docs/git-workflow.md` | 全组 Review 通过 |
| 开发环境 Docker 化 | `docker-compose.yml` | 全组 `docker-compose up` 能跑通 |
| 华为云 DevOps 环境开通 | 控制台截图 | 代码仓库、流水线、镜像仓库可访问 |
| 赛题资料汇总 | `docs/competition-resources.md` | 评分标准 + 往届案例 + CodeArts 文档索引 |

---

## 一、团队 Git 协作文档（`docs/git-workflow.md`）

你是全组 Git 规范的最终制定者和执行监督者。以下规范本周一就要发到群里。

### 1.1 分支策略

```
main                    ← 永远可部署的稳定分支（受保护，禁止直接 push）
├── feature/xxx         ← 功能分支（开发用，合并后删除）
├── fix/xxx             ← Bug 修复分支
└── docs/xxx            ← 文档分支
```

### 1.2 分支保护规则（你来设）

在 GitHub 仓库 Settings → Branches → Add rule：
- Branch name pattern: `main`
- ☑ Require a pull request before merging
- ☑ Require approvals: 1
- ☑ Require status checks to pass before merging（第 6 周 CI/CD 就绪后启用）

### 1.3 提交信息规范

```
格式: <type>: <简短描述>

type:
  feat     新增功能
  fix      修复 Bug
  docs     文档修改
  refactor 代码重构（不改变功能）
  test     测试相关
  chore    构建/依赖/工具变更
  style    代码格式（空格、分号等，不影响逻辑）

示例:
  feat: 新增患者列表分页查询接口
  fix: 修复随访日期为空时的 NPE
  docs: 更新 Swagger 接口文档
  chore: 升级 Spring Boot 到 2.7.18
```

### 1.4 .gitignore 模板

```gitignore
# ===== 通用 =====
*.log
*.tmp
.DS_Store
Thumbs.db

# ===== IDE =====
.idea/
.vscode/
*.iml
*.iws

# ===== 后端 =====
backend/target/
backend/*.jar
backend/*.war

# ===== 前端 =====
frontend-target/node_modules/
frontend-target/dist/
frontend-platform/node_modules/
frontend-platform/dist/

# ===== 环境变量 =====
.env
.env.local
*.pem
*.key

# ===== Python =====
ai-agent/__pycache__/
ai-agent/*.pyc
ai-agent/.env
```

### 1.5 Code Review 流程

1. 开发者完成功能后，推送自己的分支到 GitHub
2. 创建 Pull Request，标题写 `feat: xxx`
3. 指定**同方向的人**作为 Reviewer（前端 Review 前端，后端 Review 后端）
4. Reviewer 检查：
   - 代码是否符合本方向的开发标准
   - 是否有 `console.log` 或 `System.out.println`
   - 是否有硬编码的配置值
   - 是否写在了错误的分层（如 Controller 里写业务逻辑）
5. Review 通过后点 Merge → Delete branch

---

## 二、Docker 开发环境（`docker-compose.yml`）

本周完成以下文件，全组成员拉下来后只需一行命令就能跑起来。

### 2.1 项目根目录 `docker-compose.yml`

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: follow-up-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: follow_up
    ports:
      - "3306:3306"
    volumes:
      - ./backend/src/main/resources/db/schema.sql:/docker-entrypoint-initdb.d/01-schema.sql
      - mysql-data:/var/lib/mysql
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

volumes:
  mysql-data:
```

### 2.2 后端 `Dockerfile`（`backend/Dockerfile`）

```dockerfile
FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2.3 全组环境自检清单（你来验收）

每人执行以下步骤：

```bash
# 1. 克隆仓库
git clone https://github.com/xxx/chronic-disease-follow-up.git
cd chronic-disease-follow-up

# 2. 启动 MySQL + 后端
docker-compose up -d

# 3. 验证
curl http://localhost:8080/api/health
# 预期返回: {"code":200,"data":"ok","message":"success"}

# 4. 前端（不需要 Docker）
cd frontend-target
npm install
npm run dev
# 浏览器访问 http://localhost:5173
```

---

## 三、华为云 DevOps 环境开通

### 开通清单

| 服务 | 用途 | 开通地址 |
|---|---|---|
| CodeArts Repo | 代码仓库（如不用 GitHub） | 华为云控制台 |
| CodeArts Pipeline | CI/CD 流水线 | 同上 |
| SWR（容器镜像服务） | 存放 Docker 镜像 | 同上 |
| ECS（弹性云服务器） | 部署靶系统（后期） | 同上 |

本周只需要开通 + 记录访问凭证。实际 CI/CD 流水线到第 6 周再配。

---

## 四、赛题资料汇总（`docs/competition-resources.md`）

```markdown
# 挑战杯揭榜挂帅 — 赛题资料索引

## 1. 评分标准
- [ ] 官网下载评分细则 PDF
- [ ] 提取关键得分点（技术难度 ×%、创新性 ×%、实用性 ×%、展示效果 ×%）

## 2. 华为云 CodeArts 官方文档
- CodeArts 产品首页: https://www.huaweicloud.com/product/codearts.html
- CodeArts Snap 文档: （搜索到的文档链接）
- CodeArts Pipeline 文档: （搜索到的文档链接）
- API 参考: （搜索到的 API 文档地址）

## 3. 往届获奖案例（搜索关键词）
- "挑战杯 揭榜挂帅 获奖"
- "华为云 CodeArts 竞赛"
- "智慧医疗 随访系统 竞赛"

## 4. 慢病随访业务参考资料
- 《国家基本公共卫生服务规范》— 高血压/糖尿病患者健康管理
- 《中国高血压防治指南（2024年修订版）》
- 《中国2型糖尿病防治指南（2024版）》
- 基层慢病随访管理信息系统相关论文（知网搜索）

## 5. 技术报告大纲（初稿）
（后续周补充）
```

---

## 五、技术报告预填框架

虽然报告第 7 周才写，但本周你可以建一个骨架，后面边做边填：

```
docs/final-report/
├── 01-项目背景与问题分析.md
├── 02-系统架构设计.md
├── 03-核心功能与技术实现.md
├── 04-智能化维护闭环方案.md
├── 05-测试方案与安全策略.md
├── 06-部署与运维方案.md
├── 07-创新点总结.md
└── 附录-接口文档.md（引用 Swagger 即可）
```

每个文件先写标题和一级大纲，第 7 周填入详细内容。

---

## 六、本周禁止事项

- ❌ 不要配复杂的 Git Flow（如 develop/release/hotfix 分支）——你们 6 个人用 Feature Branch 就够了
- ❌ 不要在这个阶段搞 Kubernetes——Docker Compose 足够
- ❌ 不要把密码/密钥提交到仓库——帮全组检查一遍 `.gitignore`
- ❌ 不要等——资料收集本周就做，别拖到最后一周才发现找不到评分标准

---

## 七、与全组的协作点

- **周一**：把 `.gitignore` 和分支规范发到群里，所有人第一次提交前必须遵守
- **周三**：把 `docker-compose.yml` 发给 1 号，让 1 号验证 MySQL + 后端能否一键启动
- **周五**：检查全组的 Git 提交历史，确保没人直接在 main 上提交、没有 `.env` 文件泄露
