package com.example.followup.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PatientVO;
import com.example.followup.entity.Patient;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.service.impl.PatientServiceImpl;
import com.example.followup.service.PatientMaskingService;
import com.example.followup.security.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock PatientMapper mapper;
    @Mock PatientMaskingService masking;
    @InjectMocks PatientServiceImpl service;

    @BeforeEach void authenticateAdmin() {
        CurrentUser user=new CurrentUser(1L,"admin","ADMIN");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user,null,List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        lenient().when(masking.toVO(any(Patient.class), anyBoolean())).thenAnswer(i -> { Patient p=i.getArgument(0); PatientVO v=new PatientVO(); v.setId(p.getId()); v.setName(p.getName()); return v; });
    }
    @AfterEach void clearContext(){ SecurityContextHolder.clearContext(); }

    @Test void listReturnsPagination() {
        doAnswer(i -> { Page<Patient> p=i.getArgument(0); p.setRecords(List.of(active(1L))); p.setTotal(1); return p; })
                .when(mapper).selectPage(any(Page.class), any());
        PatientQuery q=new PatientQuery(); q.setPage(1); q.setSize(20);
        PageResponse<PatientVO> response = service.listPatients(q);
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
        assertEquals(p.getId(), service.getPatientById(1L).getId());
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
