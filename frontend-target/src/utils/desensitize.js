/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

export function maskName(value = '') {
  if (!value) {
    return '';
  }
  if (value.length === 1) {
    return '*';
  }
  return value[0] + '*'.repeat(value.length - 1);
}

export function maskPhone(value = '') {
  return value.replace(/^(?<prefix>\d{3})\d{4}(?<suffix>\d{4})$/, '$<prefix>****$<suffix>');
}

export function maskIdCard(value = '') {
  return value.replace(/^(?<prefix>.{6}).{8}(?<suffix>.{4})$/, '$<prefix>********$<suffix>');
}

export function maskAddress(value = '') {
  if (!value) {
    return '';
  }
  const match = value.match(/^(?<prefix>.+?[\u533a\u53bf])/);
  return match?.groups?.prefix ? `${match.groups.prefix}****` : '****';
}

export function maskSensitiveText(value = '', type = '') {
  const rules = {
    name: maskName,
    phone: maskPhone,
    idCard: maskIdCard,
    address: maskAddress,
  };
  return (rules[type] || (() => value))(value);
}
