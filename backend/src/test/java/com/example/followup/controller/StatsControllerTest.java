/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.followup.dto.response.DoctorStats;
import com.example.followup.dto.response.StatsOverview;
import com.example.followup.dto.response.TrendItem;
import com.example.followup.service.StatsService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {
    @Mock
    private StatsService statsService;
    @InjectMocks
    private StatsController statsController;

    @Test
    void overviewReturnsStats() throws Exception {
        when(statsService.getOverview()).thenReturn(overview());

        mockMvc().perform(get("/api/stats/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalPatients").value(10));
    }

    @Test
    void trendsReturnItems() throws Exception {
        when(statsService.getBpTrend()).thenReturn(List.of(new TrendItem("2026-07", 50.0)));
        when(statsService.getGlucoseTrend()).thenReturn(List.of(new TrendItem("2026-07", 60.0)));

        mockMvc().perform(get("/api/stats/bp-trend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].month").value("2026-07"));
        mockMvc().perform(get("/api/stats/glucose-trend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].rate").value(60.0));
    }

    @Test
    void doctorComparisonReturnsData() throws Exception {
        when(statsService.getDoctorComparison())
                .thenReturn(List.of(new DoctorStats(1L, "李医生", 5L, "80.0%", 1L)));

        mockMvc().perform(get("/api/stats/doctor-comparison"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].doctorName").value("李医生"));
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(statsController).build();
    }

    private StatsOverview overview() {
        StatsOverview overview = new StatsOverview();
        overview.setTotalPatients(10L);
        overview.setMonthlyCompleted(8);
        overview.setMonthlyExpected(10);
        overview.setCompletionRate("80.0%");
        overview.setHighRiskCount(2L);
        overview.setLostFollowUpCount(1L);
        return overview;
    }
}
