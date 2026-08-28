/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.followup.dto.request.LogQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.OperationLog;
import com.example.followup.service.OperationLogService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {
    @Mock
    private OperationLogService operationLogService;
    @InjectMocks
    private LogController logController;

    @Test
    void listReturnsLogs() throws Exception {
        PageResponse<OperationLog> page = new PageResponse<>();
        OperationLog log = new OperationLog();
        log.setId(1L);
        log.setUsername("admin");
        page.setRecords(List.of(log));
        page.setTotal(1);
        page.setPage(1);
        page.setSize(20);
        when(operationLogService.listLogs(any(LogQuery.class))).thenReturn(page);

        mockMvc().perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].username").value("admin"));
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(logController).build();
    }
}
