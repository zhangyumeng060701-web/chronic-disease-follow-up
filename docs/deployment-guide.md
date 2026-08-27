# 华为云 ECS 部署与运维文档

## 1. 服务器信息

- 公网 IP：124.70.90.96
- 操作系统：Ubuntu 22.04
- 当前实例规格：建议 2vCPUs / 4GiB，当前实际内存约 1GiB，强烈建议升级
- 端口：80 前端、8080 后端、3306 数据库（仅服务器内部访问）

## 2. 部署架构

当前线上不使用 Docker Compose，采用轻量宿主机部署：

- 前端：Nginx 托管 `/var/www/follow-up`
- 患者端 H5：Nginx `/p/` 路径
- 后端：`/root/follow-up-app.jar`
- 数据库：系统 MySQL
- 敏感字段：`DATA_ENCRYPTION_KEY` 加密存储

## 3. 更新部署

后端：

```bash
bash /Users/zhangyumeng/Documents/Codex/2026-07-27/ni/work/deploy-backend-jar.command
```

前端：

```bash
bash /Users/zhangyumeng/Documents/Codex/2026-07-27/ni/work/deploy-frontend.command
```

## 4. 服务器自动恢复

后端通过 systemd 管理，服务器重启后自动启动：

```bash
bash /root/chronic-disease-follow-up/deploy/setup-autostart.sh
```

启用服务：

```bash
systemctl enable mysql
systemctl enable nginx
systemctl enable follow-up-backend
```

验证：

```bash
systemctl status follow-up-backend --no-pager
curl http://127.0.0.1:8080/api/health
```

## 5. 健康监控

健康检查脚本：

```bash
bash /root/chronic-disease-follow-up/deploy/monitor-health.sh
```

可加入 crontab，每 5 分钟检查一次：

```bash
*/5 * * * * /root/chronic-disease-follow-up/deploy/monitor-health.sh >> /root/monitor-health.log 2>&1
```

## 6. 数据库备份

手动备份：

```bash
bash /root/chronic-disease-follow-up/deploy/backup-mysql.sh
```

每日凌晨备份：

```bash
0 2 * * * /root/chronic-disease-follow-up/deploy/backup-mysql.sh >> /root/backup.log 2>&1
```

备份保留 7 天，存放在 `/root/backups`。

## 7. 华为云服务器升级

1. 打开华为云 ECS 控制台。
2. 选择当前实例 `124.70.90.96`。
3. 操作 → 变更规格。
4. 选择 2vCPUs / 4GiB 或更高。
5. 确认费用并执行。
6. 升级后重新验证 `free -h`、`curl http://127.0.0.1:8080/api/health`。

## 8. HTTPS 域名配置

正式微信小程序和评审演示需要 HTTPS 域名。

1. 购买/已有备案域名，例如 `api.yourdomain.com`。
2. 在域名 DNS 添加 A 记录指向 `124.70.90.96`。
3. 申请 SSL 证书。
4. 将证书放入 `/etc/nginx/ssl/`。
5. 参考 `deploy/nginx-https.conf.example` 配置 Nginx。
6. 执行：

```bash
nginx -t
systemctl restart nginx
```

7. 微信小程序后台配置 request 合法域名：`https://api.yourdomain.com`。

## 9. 数据库迁移验证

后端启动时 Flyway 自动执行迁移。

验证迁移版本：

```bash
mysql -uroot -p -e "select version, description, success from follow_up.flyway_schema_history order by installed_rank;"
```

当前预期版本：

- `V1__init_schema.sql`
- `V2__follow_up_workflow.sql`
- `V3__patient_portal.sql`
- `V4__clinical_decision_support.sql`
- `V5__data_governance.sql`
- `V6__ai_suggestion_quality.sql`
- `V7__operation_log_detail.sql`
- `V8__enlarge_sensitive_columns.sql`

验证敏感字段加密：

```bash
mysql -uroot -p -e "select id, phone, id_card from follow_up.t_patient;"
```

启用 `DATA_ENCRYPTION_KEY` 后，`phone`、`id_card` 应以 `enc:` 开头。

## 10. 安全组

华为云安全组放行：

- `80`：前端
- `443`：HTTPS
- `8080`：后端 API
- `3306`：不建议公网放行

## 11. 验证命令

```bash
curl -I http://127.0.0.1/
curl http://127.0.0.1:8080/api/health
curl -X POST http://127.0.0.1:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}'
```

- 公网前端：http://124.70.90.96/
- 患者端：http://124.70.90.96/p/
- 健康检查：http://124.70.90.96:8080/api/health
