/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.request.PatientSaveRequest;
import com.example.followup.dto.request.PatientUpdateRequest;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PatientVO;
import com.example.followup.service.PatientService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

/**
 * PatientControllerTest 测试。
 *
 * @since 2026-08-28
 */
@ExtendWith(MockitoExtension.class)
class PatientControllerTest {
    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    @Test
    @DisplayName("分页查询应返回患者列表和分页信息")
    void list_shouldReturnPagedPatients() throws Exception {
        PageResponse<PatientVO> page = new PageResponse<>();
        page.setRecords(List.of(createPatientVO()));
        page.setTotal(1L);
        page.setPage(1);
        page.setSize(20);
        when(patientService.listPatients(any(PatientQuery.class))).thenReturn(page);

        mockMvc().perform(get("/api/patients")
                        .param("page", "1")
                        .param("size", "20")
                        .param("name", "张三")
                        .param("diseaseType", "HYPERTENSION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].id").value(1))
                .andExpect(jsonPath("$.data.records[0].name").value("张三"))
                .andExpect(jsonPath("$.data.records[0].diseaseType").value("HYPERTENSION"))
                .andExpect(jsonPath("$.data.records[0].doctorName").value("李医生"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.message").value("success"));

        ArgumentCaptor<PatientQuery> queryCaptor = ArgumentCaptor.forClass(PatientQuery.class);
        verify(patientService).listPatients(queryCaptor.capture());
        PatientQuery query = queryCaptor.getValue();
        assertEquals(1, query.getPage());
        assertEquals(20, query.getSize());
        assertEquals("张三", query.getName());
        assertEquals("HYPERTENSION", query.getDiseaseType());
    }

    @Test
    @DisplayName("按ID查询应返回患者详情")
    void getById_shouldReturnPatient() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(createPatientVO());

        mockMvc().perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("张三"))
                .andExpect(jsonPath("$.message").value("success"));

        verify(patientService).getPatientById(1L);
    }

    @Test
    @DisplayName("新增患者应将请求体传给服务层")
    void add_shouldPassPatientToService() throws Exception {
        mockMvc().perform(post("/api/patients")
                        .contentType("application/json")
                        .content(patientJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("success"));

        ArgumentCaptor<PatientSaveRequest> requestCaptor = ArgumentCaptor.forClass(PatientSaveRequest.class);
        verify(patientService).addPatient(requestCaptor.capture());
        assertEquals("张三", requestCaptor.getValue().getName());
        assertEquals("13812345678", requestCaptor.getValue().getPhone());
    }

    @Test
    @DisplayName("编辑患者应使用路径ID并将患者传给服务层")
    void update_shouldSetPathIdAndPassPatientToService() throws Exception {
        mockMvc().perform(put("/api/patients/9")
                        .contentType("application/json")
                        .content(patientJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));

        ArgumentCaptor<PatientUpdateRequest> requestCaptor = ArgumentCaptor.forClass(PatientUpdateRequest.class);
        verify(patientService).updatePatient(eq(9L), requestCaptor.capture());
        assertEquals("张三", requestCaptor.getValue().getName());
    }

    @Test
    @DisplayName("删除患者应将路径ID传给服务层")
    void delete_shouldPassPatientIdToService() throws Exception {
        mockMvc().perform(delete("/api/patients/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));

        verify(patientService).deletePatient(9L);
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(patientController).build();
    }

    private PatientVO createPatientVO() {
        PatientVO vo = new PatientVO();
        vo.setId(1L);
        vo.setName("张三");
        vo.setGender("男");
        vo.setAge(65);
        vo.setPhone("13812345678");
        vo.setIdCard("320102199001011234");
        vo.setAddress("南京市鼓楼区汉口路22号");
        vo.setDiseaseType("HYPERTENSION");
        vo.setMedicalHistory("高血压病史10年");
        vo.setMedicationInfo("硝苯地平 30mg qd");
        vo.setDoctorId(1L);
        vo.setDoctorName("李医生");
        vo.setStatus(1);
        return vo;
    }

    private String patientJson() {
        return "{"
                + "\"name\":\"张三\","
                + "\"gender\":\"男\","
                + "\"age\":65,"
                + "\"phone\":\"13812345678\","
                + "\"idCard\":\"320102199001011234\","
                + "\"address\":\"南京市鼓楼区汉口路22号\","
                + "\"diseaseType\":\"HYPERTENSION\","
                + "\"medicalHistory\":\"高血压病史10年\","
                + "\"medicationInfo\":\"硝苯地平 30mg qd\","
                + "\"doctorId\":1"
                + "}";
    }
}
