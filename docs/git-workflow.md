# 团队 Git 协作文档
## 1.1 分支策略
main                    ← 永远可部署的稳定分支（受保护，禁止直接 push）
├── feature/xxx         ← 功能分支（开发用，合并后删除）
├── fix/xxx             ← Bug 修复分支
└── docs/xxx            ← 文档分支

## 1.2 分支保护规则
在 GitHub 仓库 Settings → Branches → Add rule：
- Branch name pattern: `main`
- ☑ Require a pull request before merging
- ☑ Require approvals: 1

## 1.3 提交信息规范
格式: `<type>: <简短描述>`
type:
- feat     新增功能
- fix      修复 Bug
- docs     文档修改
- refactor 代码重构
- test     测试相关
- chore    构建/依赖/工具变更
- style    代码格式
示例:
- feat: 新增患者列表分页查询接口
- fix: 修复随访日期为空时的 NPE
- docs: 更新 Swagger 接口文档

## 1.4 .gitignore 模板
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
