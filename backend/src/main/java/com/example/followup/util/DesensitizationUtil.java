/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.util;

import org.springframework.util.StringUtils;

/**
 * 数据脱敏工具：非管理员访问患者数据时统一脱敏。
 *
 * @since 2026-08-28
 */
public final class DesensitizationUtil {
    private DesensitizationUtil() {
    }

    /**
    * 姓名：保留首位，其余替换为 *。
    */

    /**
     * 执行maskName操作。
     *
     * @param name 参数说明
     * @return 返回值
     */
    public static String maskName(String name) {
        if (!StringUtils.hasText(name)) {
            return name;
        }
        if (name.length() == 1) {
            return "*";
        }
        return name.charAt(0) + repeat('*', name.length() - 1);
    }

    /**
    * 手机号：保留前 3 位和后 4 位。
    */

    /**
     * 执行maskPhone操作。
     *
     * @param phone 参数说明
     * @return 返回值
     */
    public static String maskPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return phone;
        }
        String digits = phone.replaceAll("\\s", "");
        if (digits.length() < 7) {
            return repeat('*', digits.length());
        }
        return digits.substring(0, 3) + "****" + digits.substring(digits.length() - 4);
    }

    /**
    * 身份证号：保留前 3 位和后 4 位，中间按原长度隐藏。
    */

    /**
     * 执行maskIdCard操作。
     *
     * @param idCard 参数说明
     * @return 返回值
     */
    public static String maskIdCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            return idCard;
        }
        String value = idCard.trim();
        if (value.length() <= 7) {
            return repeat('*', value.length());
        }
        return value.substring(0, 3) + repeat('*', value.length() - 7) + value.substring(value.length() - 4);
    }

    /**
    * 住址：保留前 6 个字符，其余隐藏。
    */

    /**
     * 执行maskAddress操作。
     *
     * @param address 参数说明
     * @return 返回值
     */
    public static String maskAddress(String address) {
        if (!StringUtils.hasText(address)) {
            return address;
        }
        String value = address.trim();
        int keepLength = Math.min(6, value.length());
        return value.substring(0, keepLength) + "****";
    }

    private static String repeat(char ch, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }
}
