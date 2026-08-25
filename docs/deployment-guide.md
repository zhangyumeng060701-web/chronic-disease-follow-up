# 华为云 ECS 部署验证文档
## 1. 服务器信息
- **公网 IP**：124.70.90.96
- **操作系统**：Ubuntu 22.04
- **实例规格**：2vCPUs | 4GiB

## 2. 部署架构
- 使用 Docker Compose 部署（后端 + MySQL）
- 后端框架：Spring Boot 2.7.18
- 暴露端口：8080

## 3. 部署步骤
```bash
# 1. SSH 登录服务器
ssh root@124.70.90.96
# 2. 克隆仓库
git clone https://github.com/zhangyumeng060701-web/chronic-disease-follow-up.git
cd chronic-disease-follow-up
# 3. 启动服务（Docker Compose 方式）
docker-compose up -d
#4. 验证结果
· 公网访问地址：http://124.70.90.96:8080/api/health
· 预期返回：{"code":200,"data":"ok","message":"success"}
· 验证截图：见 deployment-proof.md


