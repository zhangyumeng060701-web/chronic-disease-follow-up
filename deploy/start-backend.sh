#!/usr/bin/env bash
set -e

if [ -f /root/chronic-disease-follow-up/.env ]; then
  set -a
  . /root/chronic-disease-follow-up/.env
  set +a
fi

if [ ! -s /root/.data_encryption_key ]; then
  openssl rand -hex 32 > /root/.data_encryption_key
  chmod 600 /root/.data_encryption_key
fi
DATA_ENCRYPTION_KEY="$(cat /root/.data_encryption_key)"

exec java -Xmx256m -XX:MaxMetaspaceSize=128m \
  -DDATA_ENCRYPTION_KEY="$DATA_ENCRYPTION_KEY" \
  -Dspring.datasource.url="${SPRING_DATASOURCE_URL:-jdbc:mysql://localhost:3306/follow_up?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8}" \
  -Dspring.datasource.username="${DB_USERNAME:-root}" \
  -Dspring.datasource.password="${DB_PASSWORD:-root123}" \
  -jar /root/follow-up-app.jar
