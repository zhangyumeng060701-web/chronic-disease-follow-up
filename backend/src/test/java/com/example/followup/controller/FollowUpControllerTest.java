/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.followup.dto.request.FollowUpQuery;
import com.example.followup.dto.response.FollowUpVO;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.FollowUp;
import com.example.followup.service.FollowUpService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

/**
 * FollowUpControllerTest 测试。
 *
 * @since 2026-08-28
 */
@ExtendWith(MockitoExtension.class)
class FollowUpControllerTest {
    @Mock
    private FollowUpService followUpService;
    @InjectMocks
    private FollowUpController followUpController;

    @Test
    void listReturnsPagedFollowUps() throws Exception {
        PageResponse<FollowUpVO> page = new PageResponse<>();
        FollowUpVO vo = new FollowUpVO();
        vo.setId(1L);
        vo.setPatientName("张三");
        page.setRecords(List.of(vo));
        page.setTotal(1);
        page.setPage(1);
        page.setSize(20);
        when(followUpService.listFollowUps(any(FollowUpQuery.class))).thenReturn(page);

        mockMvc().perform(get("/api/follow-ups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].id").value(1));
    }

    @Test
    void getByIdReturnsFollowUp() throws Exception {
        FollowUp followUp = new FollowUp();
        followUp.setId(1L);
        when(followUpService.getFollowUpById(1L)).thenReturn(followUp);

        mockMvc().perform(get("/api/follow-ups/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void addCallsService() throws Exception {
        mockMvc().perform(post("/api/follow-ups")
                        .contentType("application/json")
                        .content("{\"patientId\":1,\"followUpDate\":\"2026-08-01\",\"followUpType\":\"门诊\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        verify(followUpService).addFollowUp(any(FollowUp.class));
    }

    @Test
    void updateCallsService() throws Exception {
        mockMvc().perform(put("/api/follow-ups/9")
                        .contentType("application/json")
                        .content("{\"patientId\":1,\"followUpDate\":\"2026-08-01\",\"followUpType\":\"门诊\"}"))
                .andExpect(status().isOk());
        verify(followUpService).updateFollowUp(any(FollowUp.class));
    }

    @Test
    void deleteCallsService() throws Exception {
        mockMvc().perform(delete("/api/follow-ups/9"))
                .andExpect(status().isOk());
        verify(followUpService).deleteFollowUp(eq(9L));
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(followUpController).build();
    }
}
