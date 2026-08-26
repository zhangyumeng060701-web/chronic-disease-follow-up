#!/usr/bin/env bash
set -euo pipefail

if [[ $EUID -ne 0 ]]; then
  echo "请使用 root 用户执行此脚本"
  exit 1
fi

SOURCE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="/var/www/follow-up"

mkdir -p "$FRONTEND_DIR"
cp -R "$SOURCE_DIR/dist/." "$FRONTEND_DIR/"

if ! command -v nginx >/dev/null 2>&1; then
  apt-get update
  apt-get install -y nginx
fi

cp "$SOURCE_DIR/nginx-follow-up.conf" /etc/nginx/conf.d/follow-up.conf
rm -f /etc/nginx/sites-enabled/default

nginx -t
systemctl enable nginx
systemctl restart nginx

echo "前端部署完成：http://124.70.90.96/"
