# 华为云 ECS 部署验证文档

## 1. 服务器信息
- **公网 IP**：124.70.90.96
- **操作系统**：Ubuntu 22.04
- **实例规格**：2vCPUs | 4GiB

## 2. 部署架构
- 使用 Docker Compose 部署：MySQL + 后端 + 前端
- 后端框架：Spring Boot 2.7.18
- 前端框架：Vue 3 + Vite + Nginx
- 暴露端口：80（前端）、8080（后端）
- 前端 Nginx 将 `/api/` 反向代理到后端服务，浏览器保持同源请求

## 3. 首次部署步骤
```bash
# 1. SSH 登录服务器
ssh root@124.70.90.96

# 2. 克隆仓库
git clone https://github.com/zhangyumeng060701-web/chronic-disease-follow-up.git
cd chronic-disease-follow-up

# 3. GitHub 网络不稳定时，强制使用 HTTP/1.1
git config http.version HTTP/1.1

# 4. 安装 Docker Compose v2 插件（首次部署需要）
apt-get update
apt-get install -y docker-compose-plugin
docker compose version

# 5. 复制环境变量模板（按需修改密码与密钥）
cp .env.example .env

# 6. 启动全部服务
docker compose up -d --build
```

后续更新部署：

```bash
cd chronic-disease-follow-up
git pull
docker compose up -d --build
```

## 4. 验证结果

```bash
# 前端入口
curl -I http://127.0.0.1/

# 后端健康检查
curl http://127.0.0.1:8080/api/health

# 默认账号登录
curl -X POST http://127.0.0.1:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}'
```

- 公网前端地址：http://124.70.90.96/
- 公网健康检查：http://124.70.90.96:8080/api/health
- 预期返回：`{"code":200,"data":"ok","message":"success"}`
- 验证截图：见 `deployment-proof.md`

## 5. 华为云安全组

需放行以下入方向端口：

- `80`：前端 Nginx
- `8080`：后端 API
- `3306`：仅本地部署联调使用，生产环境不建议公网放行

