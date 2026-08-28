/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

export function useDebounce(fn, delay = 300) {
  let timer = null;

  return (...args) => {
    if (timer) {
      clearTimeout(timer);
    }
    timer = setTimeout(() => fn(...args), delay);
  };
}
