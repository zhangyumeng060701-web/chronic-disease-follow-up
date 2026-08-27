#!/usr/bin/env bash
set -euo pipefail

if [[ $EUID -ne 0 ]]; then
  echo "请使用 root 用户执行"
  exit 1
fi

if [ -f /root/chronic-disease-follow-up/.env ]; then
  set -a
  . /root/chronic-disease-follow-up/.env
  set +a
fi

DB_PASSWORD="${DB_PASSWORD:-root123}"
BACKUP_DIR="/root/backups"
STAMP="$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

MYSQL_PWD="$DB_PASSWORD" mysqldump -uroot --single-transaction --routines --triggers follow_up > "$BACKUP_DIR/follow_up_$STAMP.sql"
find "$BACKUP_DIR" -name 'follow_up_*.sql' -mtime +7 -delete

echo "数据库备份完成：$BACKUP_DIR/follow_up_$STAMP.sql"
