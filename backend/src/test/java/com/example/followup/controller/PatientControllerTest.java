package com.example.followup.controller;

import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.Patient;
import com.example.followup.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {
    @Mock PatientService patientService;
    @InjectMocks PatientController patientController;

    private MockMvc mvc() { return MockMvcBuilders.standaloneSetup(patientController).build(); }

    @Test void listReturnsPage() throws Exception {
        PageResponse<Patient> page = new PageResponse<>();
        page.setRecords(List.of(patient())); page.setTotal(1L); page.setPage(1); page.setSize(20);
        when(patientService.listPatients(any())).thenReturn(page);
        mvc().perform(get("/api/patients").param("page", "1").param("size", "20"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.total").value(1));
    }
    @Test void getReturnsPatient() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(patient());
        mvc().perform(get("/api/patients/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }
    @Test void addDelegates() throws Exception {
        mvc().perform(post("/api/patients").contentType("application/json").content(json()))
                .andExpect(status().isOk());
        verify(patientService).addPatient(any());
    }
    @Test void updateDelegates() throws Exception {
        mvc().perform(put("/api/patients/9").contentType("application/json").content(json()))
                .andExpect(status().isOk());
        verify(patientService).updatePatient(any());
    }
    @Test void deleteDelegates() throws Exception {
        mvc().perform(delete("/api/patients/9")).andExpect(status().isOk());
        verify(patientService).deletePatient(9L);
    }
    private Patient patient() { Patient p = new Patient(); p.setId(1L); p.setName("张三"); p.setStatus(1); return p; }
    private String json() { return "{\"name\":\"张三\",\"gender\":\"男\",\"age\":65,\"phone\":\"13812345678\",\"idCard\":\"320102199001011234\",\"address\":\"南京市鼓楼区\",\"diseaseType\":\"HYPERTENSION\",\"doctorId\":1}"; }
}
