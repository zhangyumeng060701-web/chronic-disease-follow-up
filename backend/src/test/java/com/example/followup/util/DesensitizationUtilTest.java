/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DesensitizationUtilTest {
    @Test
    @DisplayName("姓名脱敏保留首字")
    void maskName() {
        assertEquals("张**", DesensitizationUtil.maskName("张三丰"));
        assertEquals("*", DesensitizationUtil.maskName("李"));
        assertEquals("", DesensitizationUtil.maskName(""));
        assertEquals(null, DesensitizationUtil.maskName(null));
    }

    @Test
    @DisplayName("手机号脱敏保留前3后4")
    void maskPhone() {
        assertEquals("138****5678", DesensitizationUtil.maskPhone("13812345678"));
        assertEquals("", DesensitizationUtil.maskPhone(""));
        assertEquals(null, DesensitizationUtil.maskPhone(null));
    }

    @Test
    @DisplayName("身份证脱敏保留前3后4")
    void maskIdCard() {
        assertEquals("320***********1234", DesensitizationUtil.maskIdCard("320102199001011234"));
        assertEquals("", DesensitizationUtil.maskIdCard(""));
        assertEquals(null, DesensitizationUtil.maskIdCard(null));
    }

    @Test
    @DisplayName("地址脱敏保留区县")
    void maskAddress() {
        assertEquals("南京市鼓楼区****", DesensitizationUtil.maskAddress("南京市鼓楼区汉口路22号"));
        assertEquals("", DesensitizationUtil.maskAddress(""));
        assertEquals(null, DesensitizationUtil.maskAddress(null));
    }
}
