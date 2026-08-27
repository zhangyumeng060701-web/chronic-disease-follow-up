# 凭据轮换与清理

## 1. GitHub Token

1. 打开 GitHub → Settings → Developer settings → Personal access tokens。
2. 删除所有已泄露的 Token，包括：
   - `ghp_d6wh...`
   - `ghp_TUpP...`
   - `ghp_fe80...`
   - `ghp_SxN1...`
   - 任何出现在聊天、终端或 `.command` 文件中的 Token。
3. 新建一个 Token，仅授予当前仓库所需权限。
4. 更新仓库外 `push-*.command` 脚本中的 Token 字符串。
5. 不要在源码、文档、聊天中保存 Token。

## 2. 服务器 root 密码

1. 登录服务器：
   ```bash
   ssh root@124.70.90.96
   ```
2. 修改 root 密码：
   ```bash
   passwd
   ```
3. 不要在聊天或终端历史中长期保留新密码。

## 3. MySQL 数据库密码

1. 登录服务器后用 MySQL root 进入：
   ```bash
   mysql -uroot -p
   ```
2. 修改密码：
   ```sql
   ALTER USER 'root'@'localhost' IDENTIFIED BY '新的强密码';
   FLUSH PRIVILEGES;
   ```
3. 同步更新：
   - `/root/chronic-disease-follow-up/.env`
   - `deploy-backend-jar.command`
   - `repair-server.command`
   - `docs/deployment-guide.md`
4. 不要继续使用 `root123` 作为部署密码。

## 4. JWT 与 AI 密钥

1. 重新生成 `JWT_SECRET`。
2. 重新生成 `DATA_ENCRYPTION_KEY`，已加密数据需要保留旧密钥用于迁移，新数据使用新密钥。
3. 重新申请或轮换 `AGENT_ARTS_API_KEY`、`AGENT_ARTS_SESSION_ID`。
4. 所有密钥只保存在服务器环境变量或 `/root/.data_encryption_key`，不提交到 GitHub。
