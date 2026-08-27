#!/usr/bin/env bash
set -euo pipefail

URL="${HEALTH_URL:-http://127.0.0.1:8080/api/health}"

if curl -fsS -m 5 "$URL" >/dev/null 2>&1; then
  echo "健康检查通过"
  exit 0
fi

echo "健康检查失败，尝试重启后端"
systemctl restart follow-up-backend
sleep 15

if curl -fsS -m 5 "$URL" >/dev/null 2>&1; then
  echo "后端已恢复"
else
  echo "后端恢复失败，请检查 /root/follow-up-backend.log"
  exit 1
fi
