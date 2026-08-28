/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.followup.controller.AiController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

class AiServiceTest {
    private AiController aiController;

    @BeforeEach
    void setUp() {
        aiController = new AiController();
    }

    @Test
    @DisplayName("空需求返回400")
    void emptyRequirementReturns400() {
        ReflectionTestUtils.setField(aiController, "agentArtsApiKey", "test-key");
        Map<String, Object> result = aiController.decompose(Map.of("requirement", ""));

        assertEquals(400, result.get("code"));
    }

    @Test
    @DisplayName("未配置密钥时不发起AI调用并返回500")
    void missingKeyReturns500() {
        ReflectionTestUtils.setField(aiController, "agentArtsApiKey", "");
        Map<String, Object> result = aiController.decompose(Map.of("requirement", "测试需求"));

        assertEquals(500, result.get("code"));
    }
}
