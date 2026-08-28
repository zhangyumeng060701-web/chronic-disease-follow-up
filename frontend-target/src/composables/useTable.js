/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

import { reactive, ref } from 'vue';

export function useTable({ fetcher, initialPage = 1, initialSize = 20 }) {
  const loading = ref(false);
  const error = ref('');
  const tableData = ref([]);
  const pagination = reactive({ page: initialPage, size: initialSize, total: 0 });
  const lastParams = ref({});

  async function load(params = {}) {
    lastParams.value = params;
    loading.value = true;
    error.value = '';
    try {
      const res = await fetcher({ page: pagination.page, size: pagination.size, ...params });
      const data = res?.data || res || {};
      tableData.value = data.records || [];
      pagination.total = data.total || 0;
    } catch (err) {
      error.value = err?.message || '加载失败，请稍后重试';
      tableData.value = [];
    } finally {
      loading.value = false;
    }
  }

  function search(params = {}) {
    pagination.page = 1;
    return load(params);
  }

  function reset(params = {}) {
    return search(params);
  }

  function retry() {
    return load(lastParams.value);
  }

  return { loading, error, tableData, pagination, load, search, reset, retry };
}
