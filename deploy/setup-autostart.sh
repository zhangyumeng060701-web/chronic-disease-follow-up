#!/usr/bin/env bash
set -euo pipefail

if [[ $EUID -ne 0 ]]; then
  echo "请使用 root 用户执行"
  exit 1
fi

mkdir -p /root/chronic-disease-follow-up/deploy
cp /root/chronic-disease-follow-up/deploy/follow-up-backend.service /etc/systemd/system/follow-up-backend.service
chmod +x /root/chronic-disease-follow-up/deploy/start-backend.sh

systemctl daemon-reload
systemctl enable mysql
systemctl enable nginx
systemctl enable follow-up-backend
systemctl restart follow-up-backend

echo "自动恢复配置完成：MySQL/Nginx/后端均随服务器开机自启"
