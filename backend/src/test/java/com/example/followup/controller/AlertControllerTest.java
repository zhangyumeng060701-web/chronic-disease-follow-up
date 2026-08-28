/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.followup.dto.request.AlertQuery;
import com.example.followup.dto.response.AlertVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.service.AlertService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

/**
 * AlertControllerTest 测试。
 *
 * @since 2026-08-28
 */
@ExtendWith(MockitoExtension.class)
class AlertControllerTest {
    @Mock
    private AlertService alertService;
    @InjectMocks
    private AlertController alertController;

    @Test
    void listReturnsPagedAlerts() throws Exception {
        PageResponse<AlertVO> page = new PageResponse<>();
        AlertVO vo = new AlertVO();
        vo.setId(1L);
        vo.setPatientName("张三");
        page.setRecords(List.of(vo));
        page.setTotal(1);
        page.setPage(1);
        page.setSize(20);
        when(alertService.listAlerts(any(AlertQuery.class))).thenReturn(page);

        mockMvc().perform(get("/api/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].id").value(1));
    }

    @Test
    void resolveCallsService() throws Exception {
        mockMvc().perform(put("/api/alerts/9/resolve"))
                .andExpect(status().isOk());
        verify(alertService).resolveAlert(9L);
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(alertController).build();
    }
}
