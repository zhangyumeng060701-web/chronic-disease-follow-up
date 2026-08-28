/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

export const DISEASE_TYPES = {
  HYPERTENSION: '高血压',
  DIABETES: '糖尿病',
  BOTH: '两者皆有',
};

export const ROLES = {
  ADMIN: '管理员',
  DOCTOR: '医生',
};

export const ALERT_LEVELS = {
  RED: { label: '高危', type: 'danger' },
  YELLOW: { label: '警告', type: 'warning' },
};

export const STATUS = {
  ACTIVE: 1,
  DISABLED: 0,
};

export const EMPTY_TEXT = {
  TABLE: '暂无数据',
  PATIENT: '暂无患者数据',
  FOLLOW_UP: '暂无随访记录',
  ALERT: '暂无预警数据',
  USER: '暂无用户数据',
  LOG: '暂无操作日志',
};
