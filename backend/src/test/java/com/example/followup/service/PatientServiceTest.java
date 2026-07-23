package com.example.followup.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.request.PatientSaveRequest;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PatientVO;
import com.example.followup.entity.Patient;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientServiceImpl patientService;

    @Test
    @DisplayName("listPatients should return records total page and size")
    void listPatients_shouldReturnPageResponse() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("\u5f20\u4e09");
        patient.setDiseaseType("HYPERTENSION");
        patient.setStatus(1);
        mockPageResult(patient);

        PatientQuery query = new PatientQuery();
        query.setPage(1);
        query.setSize(20);
        query.setName("\u5f20");
        query.setDiseaseType("HYPERTENSION");

        PageResponse<PatientVO> response = patientService.listPatients(query, "ADMIN");

        assertEquals(1, response.getRecords().size());
        assertEquals(1L, response.getTotal());
        assertEquals(1, response.getPage());
        assertEquals(20, response.getSize());
        assertEquals("\u5f20\u4e09", response.getRecords().get(0).getName());
    }

    @Test
    @DisplayName("listPatients should mask sensitive fields for doctor role")
    void listPatients_shouldMaskSensitiveFieldsForDoctorRole() {
        Patient patient = createSensitivePatient();
        mockPageResult(patient);

        PatientQuery query = new PatientQuery();
        query.setPage(1);
        query.setSize(20);

        PageResponse<PatientVO> response = patientService.listPatients(query, "DOCTOR");
        PatientVO masked = response.getRecords().get(0);

        assertEquals("\u5f20**", masked.getName());
        assertEquals("138****5678", masked.getPhone());
        assertEquals("320102********1234", masked.getIdCard());
        assertEquals("\u5357\u4eac\u5e02\u9f13\u697c\u533a****", masked.getAddress());
        assertEquals("\u5f20\u4e09\u4e30", patient.getName());
    }

    @Test
    @DisplayName("getPatientById should keep sensitive fields for admin role")
    void getPatientById_shouldKeepSensitiveFieldsForAdminRole() {
        Patient patient = createSensitivePatient();
        when(patientMapper.selectById(1L)).thenReturn(patient);

        PatientVO result = patientService.getPatientById(1L, "ADMIN");

        assertEquals("\u5f20\u4e09\u4e30", result.getName());
        assertEquals("13812345678", result.getPhone());
        assertEquals("320102199001011234", result.getIdCard());
        assertEquals("\u5357\u4eac\u5e02\u9f13\u697c\u533a\u6c49\u53e3\u8def22\u53f7", result.getAddress());
    }

    @Test
    @DisplayName("getPatientById should throw when patient is missing")
    void getPatientById_shouldThrowWhenPatientMissing() {
        when(patientMapper.selectById(404L)).thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> patientService.getPatientById(404L, "ADMIN")
        );

        assertEquals(404, exception.getCode());
    }

    @Test
    @DisplayName("getPatientById should throw when patient is deleted")
    void getPatientById_shouldThrowWhenPatientDeleted() {
        Patient patient = new Patient();
        patient.setId(3L);
        patient.setStatus(0);
        when(patientMapper.selectById(3L)).thenReturn(patient);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> patientService.getPatientById(3L, "ADMIN")
        );

        assertEquals(404, exception.getCode());
    }

    @Test
    @DisplayName("addPatient should clear id and set active status")
    void addPatient_shouldClearIdAndSetActiveStatus() {
        PatientSaveRequest request = createSaveRequest();

        patientService.addPatient(request);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientMapper).insert(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals(1, captor.getValue().getStatus());
        assertEquals("\u674e\u56db", captor.getValue().getName());
    }

    @Test
    @DisplayName("updatePatient should use path id")
    void updatePatient_shouldUsePathId() {
        Patient existing = createSensitivePatient();
        when(patientMapper.selectById(9L)).thenReturn(existing);

        PatientSaveRequest request = new PatientSaveRequest();
        request.setPhone("13912345678");
        request.setMedicationInfo("\u65b0\u836f\u7269\u4fe1\u606f");

        patientService.updatePatient(9L, request);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientMapper).updateById(captor.capture());
        assertEquals(9L, captor.getValue().getId());
        assertEquals("\u5f20\u4e09\u4e30", captor.getValue().getName());
        assertEquals("13912345678", captor.getValue().getPhone());
        assertEquals("\u65b0\u836f\u7269\u4fe1\u606f", captor.getValue().getMedicationInfo());
    }

    @Test
    @DisplayName("deletePatient should soft delete patient")
    void deletePatient_shouldSoftDeletePatient() {
        Patient patient = new Patient();
        patient.setId(2L);
        patient.setStatus(1);
        when(patientMapper.selectById(2L)).thenReturn(patient);

        patientService.deletePatient(2L);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientMapper).updateById(captor.capture());
        assertEquals(0, captor.getValue().getStatus());
    }

    private void mockPageResult(Patient patient) {
        doAnswer(invocation -> {
            Page<Patient> page = invocation.getArgument(0);
            page.setRecords(List.of(patient));
            page.setTotal(1);
            return page;
        }).when(patientMapper).selectPage(any(Page.class), any());
    }

    private Patient createSensitivePatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("\u5f20\u4e09\u4e30");
        patient.setPhone("13812345678");
        patient.setIdCard("320102199001011234");
        patient.setAddress("\u5357\u4eac\u5e02\u9f13\u697c\u533a\u6c49\u53e3\u8def22\u53f7");
        patient.setDiseaseType("HYPERTENSION");
        patient.setStatus(1);
        return patient;
    }

    private PatientSaveRequest createSaveRequest() {
        PatientSaveRequest request = new PatientSaveRequest();
        request.setName("\u674e\u56db");
        request.setGender("\u7537");
        request.setAge(60);
        request.setPhone("13912345678");
        request.setIdCard("320102196601011234");
        request.setAddress("\u5357\u4eac\u5e02\u9f13\u697c\u533a\u4e2d\u5c71\u8def1\u53f7");
        request.setDiseaseType("HYPERTENSION");
        request.setDoctorId(1L);
        return request;
    }
}
