/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';
import storage from '@/utils/storage';
import { buildAuthHeader, resolveResponseError } from './requestHelpers';

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
});

request.interceptors.request.use((config) => {
  const token = storage.get('token');
  Object.assign(config.headers, buildAuthHeader(token));
  return config;
});

request.interceptors.response.use(
  (res) => {
    if (res.data.code !== 200) {
      ElMessage.error(res.data.message || '请求失败');
      return Promise.reject(new Error(res.data.message));
    }
    return res.data;
  },
  (err) => {
    const { code, message } = resolveResponseError(err);
    if (code === 401) {
      storage.remove('token');
      router.push('/login');
    }
    ElMessage.error(message);
    return Promise.reject(err);
  },
);

export default request;
