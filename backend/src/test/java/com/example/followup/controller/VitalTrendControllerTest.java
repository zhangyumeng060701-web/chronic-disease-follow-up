package com.example.followup.controller;

import com.example.followup.entity.PatientVital;
import com.example.followup.mapper.PatientVitalMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VitalTrendControllerTest {
    @Mock PatientVitalMapper mapper;
    @InjectMocks VitalTrendController controller;

    @Test void returnsMetricTrend() throws Exception {
        PatientVital vital = new PatientVital(); vital.setPatientId(2L); vital.setMetricType("SYSTOLIC_BP");
        vital.setMetricValue(new BigDecimal("138")); vital.setMeasuredAt(LocalDateTime.of(2026, 8, 1, 8, 30));
        when(mapper.selectList(any())).thenReturn(List.of(vital));
        mvc().perform(get("/api/vitals/trend").param("patientId", "2").param("metricType", "SYSTOLIC_BP").param("days", "30"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data[0].metricType").value("SYSTOLIC_BP"))
                .andExpect(jsonPath("$.data[0].metricValue").value(138));
    }

    @Test void emptyTrendReturnsEmptyArray() throws Exception {
        when(mapper.selectList(any())).thenReturn(List.of());
        mvc().perform(get("/api/vitals/trend").param("patientId", "2"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").isEmpty());
    }

    @Test void patientIdIsRequired() throws Exception {
        mvc().perform(get("/api/vitals/trend")).andExpect(status().isBadRequest());
    }

    private MockMvc mvc() { return MockMvcBuilders.standaloneSetup(controller).build(); }
}
