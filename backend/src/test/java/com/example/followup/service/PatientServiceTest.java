package com.example.followup.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.response.PageResponse;
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

        doAnswer(invocation -> {
            Page<Patient> page = invocation.getArgument(0);
            page.setRecords(List.of(patient));
            page.setTotal(1);
            return page;
        }).when(patientMapper).selectPage(any(Page.class), any());

        PatientQuery query = new PatientQuery();
        query.setPage(1);
        query.setSize(20);
        query.setName("\u5f20");
        query.setDiseaseType("HYPERTENSION");

        PageResponse<Patient> response = patientService.listPatients(query);

        assertEquals(1, response.getRecords().size());
        assertEquals(1L, response.getTotal());
        assertEquals(1, response.getPage());
        assertEquals(20, response.getSize());
        assertEquals("\u5f20\u4e09", response.getRecords().get(0).getName());
    }

    @Test
    @DisplayName("getPatientById should throw when patient is missing")
    void getPatientById_shouldThrowWhenPatientMissing() {
        when(patientMapper.selectById(404L)).thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> patientService.getPatientById(404L)
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
                () -> patientService.getPatientById(3L)
        );

        assertEquals(404, exception.getCode());
    }

    @Test
    @DisplayName("addPatient should clear id and set active status")
    void addPatient_shouldClearIdAndSetActiveStatus() {
        Patient patient = new Patient();
        patient.setId(10L);
        patient.setName("\u674e\u56db");

        patientService.addPatient(patient);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientMapper).insert(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals(1, captor.getValue().getStatus());
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
}

