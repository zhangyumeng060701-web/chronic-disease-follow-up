/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * HealthControllerTest 测试。
 *
 * @since 2026-08-28
 */
class HealthControllerTest {
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new HealthController())
            .build();

    @Test
    @DisplayName("健康检查应返回统一成功响应")
    void healthCheck_shouldReturnSuccessResult() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("ok"))
                .andExpect(jsonPath("$.message").value("success"));
    }
}
