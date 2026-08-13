package com.example.followup.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.Patient;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock PatientMapper mapper;
    @InjectMocks PatientServiceImpl service;

    @Test void listReturnsPagination() {
        doAnswer(i -> { Page<Patient> p=i.getArgument(0); p.setRecords(List.of(active(1L))); p.setTotal(1); return p; })
                .when(mapper).selectPage(any(Page.class), any());
        PatientQuery q=new PatientQuery(); q.setPage(1); q.setSize(20);
        PageResponse<Patient> response = service.listPatients(q);
        assertAll(() -> assertEquals(1, response.getRecords().size()),
                () -> assertEquals(1L, response.getTotal()));
    }
    @Test void missingPatientThrows404() {
        when(mapper.selectById(404L)).thenReturn(null);
        assertEquals(404, assertThrows(BusinessException.class, () -> service.getPatientById(404L)).getCode());
    }
    @Test void deletedPatientThrows404() {
        Patient p=active(3L); p.setStatus(0); when(mapper.selectById(3L)).thenReturn(p);
        assertThrows(BusinessException.class, () -> service.getPatientById(3L));
    }
    @Test void getReturnsActivePatient() {
        Patient p=active(1L); when(mapper.selectById(1L)).thenReturn(p);
        assertSame(p, service.getPatientById(1L));
    }
    @Test void addClearsIdAndActivates() {
        Patient p=active(8L); service.addPatient(p);
        assertNull(p.getId()); assertEquals(1, p.getStatus()); verify(mapper).insert(p);
    }
    @Test void updateDelegates() {
        Patient existing=active(2L); when(mapper.selectById(2L)).thenReturn(existing);
        Patient update=active(2L); update.setName("李四"); service.updatePatient(update);
        verify(mapper).updateById(update);
    }
    @Test void deleteSoftDeletes() {
        Patient p=active(2L); when(mapper.selectById(2L)).thenReturn(p); service.deletePatient(2L);
        ArgumentCaptor<Patient> c=ArgumentCaptor.forClass(Patient.class); verify(mapper).updateById(c.capture());
        assertEquals(0, c.getValue().getStatus());
    }
    private Patient active(Long id) { Patient p=new Patient(); p.setId(id); p.setStatus(1); p.setName("张三"); return p; }
}
