package com.example.followup.controller;

import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PatientVO;
import com.example.followup.service.PatientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

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

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    @Test
    @DisplayName("list should return paged patient VO with camelCase fields")
    void list_shouldReturnPagedPatientVoWithCamelCaseFields() throws Exception {
        PageResponse<PatientVO> page = new PageResponse<>();
        page.setRecords(List.of(createPatientVO()));
        page.setTotal(1L);
        page.setPage(1);
        page.setSize(20);
        when(patientService.listPatients(any(), eq("DOCTOR"))).thenReturn(page);

        mockMvc().perform(get("/api/patients")
                        .param("page", "1")
                        .param("size", "20")
                        .requestAttr("role", "DOCTOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].id").value(1))
                .andExpect(jsonPath("$.data.records[0].diseaseType").value("HYPERTENSION"))
                .andExpect(jsonPath("$.data.records[0].idCard").value("320102********1234"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.message").value("success"));

        verify(patientService).listPatients(any(), eq("DOCTOR"));
    }

    @Test
    @DisplayName("getById should pass request role to service")
    void getById_shouldPassRequestRoleToService() throws Exception {
        when(patientService.getPatientById(1L, "ADMIN")).thenReturn(createPatientVO());

        mockMvc().perform(get("/api/patients/1").requestAttr("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("\u5f20\u4e09"))
                .andExpect(jsonPath("$.data.phone").value("138****5678"));

        verify(patientService).getPatientById(1L, "ADMIN");
    }

    @Test
    @DisplayName("add should accept patient save request")
    void add_shouldAcceptPatientSaveRequest() throws Exception {
        mockMvc().perform(post("/api/patients")
                        .contentType("application/json")
                        .content(patientJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("success"));

        verify(patientService).addPatient(any());
    }

    @Test
    @DisplayName("update should use path id and patient save request")
    void update_shouldUsePathIdAndPatientSaveRequest() throws Exception {
        mockMvc().perform(put("/api/patients/9")
                        .contentType("application/json")
                        .content(patientJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));

        verify(patientService).updatePatient(eq(9L), any());
    }

    @Test
    @DisplayName("delete should soft delete through service")
    void delete_shouldSoftDeleteThroughService() throws Exception {
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
        PatientVO patient = new PatientVO();
        patient.setId(1L);
        patient.setName("\u5f20\u4e09");
        patient.setGender("\u7537");
        patient.setAge(65);
        patient.setPhone("138****5678");
        patient.setIdCard("320102********1234");
        patient.setAddress("\u5357\u4eac\u5e02\u9f13\u697c\u533a****");
        patient.setDiseaseType("HYPERTENSION");
        patient.setMedicalHistory("\u9ad8\u8840\u538b\u75c5\u53f210\u5e74");
        patient.setMedicationInfo("\u785d\u82ef\u5730\u5e73 30mg qd");
        patient.setDoctorId(1L);
        patient.setStatus(1);
        return patient;
    }

    private String patientJson() {
        return "{"
                + "\"name\":\"\u5f20\u4e09\","
                + "\"gender\":\"\u7537\","
                + "\"age\":65,"
                + "\"phone\":\"13812345678\","
                + "\"idCard\":\"320102199001011234\","
                + "\"address\":\"\u5357\u4eac\u5e02\u9f13\u697c\u533a\u6c49\u53e3\u8def22\u53f7\","
                + "\"diseaseType\":\"HYPERTENSION\","
                + "\"medicalHistory\":\"\u9ad8\u8840\u538b\u75c5\u53f210\u5e74\","
                + "\"medicationInfo\":\"\u785d\u82ef\u5730\u5e73 30mg qd\","
                + "\"doctorId\":1"
                + "}";
    }
}
