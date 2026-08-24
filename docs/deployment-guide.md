# 华为云 ECS 部署验证文档
## 服务器信息
- **公网 IP**：`待填入`
- **操作系统**：`待填入`
- **实例规格**：`待填入`
## 部署验证步骤
1. SSH 登录服务器：`ssh root@待填入IP`
2. 拉取项目代码并启动：`docker-compose up -d`
3. 本地验证：`curl http://localhost:8080/api/health` 返回 `{"code":200}`
4. 公网验证：浏览器访问 `http://待填入IP:8080/api/health`，截图如下：
## 验证截图
（截图）
