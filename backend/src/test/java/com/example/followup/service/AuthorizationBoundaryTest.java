package com.example.followup.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.followup.dto.request.AlertQuery;
import com.example.followup.dto.request.LogQuery;
import com.example.followup.entity.Alert;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.Patient;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.AlertRuleMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.OperationLogMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.security.CurrentUser;
import com.example.followup.service.impl.AlertServiceImpl;
import com.example.followup.service.impl.FollowUpServiceImpl;
import com.example.followup.service.impl.OperationLogServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationBoundaryTest {
    @Mock AlertMapper alertMapper;
    @Mock PatientMapper patientMapper;
    @Mock FollowUpMapper followUpMapper;
    @Mock AlertRuleMapper alertRuleMapper;
    @Mock OperationLogMapper operationLogMapper;
    @Mock com.example.followup.engine.AlertRuleEngine alertRuleEngine;
    @InjectMocks AlertServiceImpl alertService;
    @InjectMocks FollowUpServiceImpl followUpService;
    @InjectMocks OperationLogServiceImpl operationLogService;

    @BeforeAll
    static void metadata() {
        MybatisConfiguration config = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(config, "auth-patient"), Patient.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(config, "auth-alert"), Alert.class);
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doctorCannotResolveAnotherDoctorsAlert() {
        authDoctor(7L);
        Alert alert = new Alert();
        alert.setId(3L);
        alert.setPatientId(20L);
        when(alertMapper.selectById(3L)).thenReturn(alert);
        when(patientMapper.selectById(20L)).thenReturn(patient(20L, 8L));

        BusinessException exception = assertThrows(BusinessException.class, () -> alertService.resolveAlert(3L));

        assertEquals(403, exception.getHttpStatus());
        verify(alertMapper, never()).updateById(any());
    }

    @Test
    void doctorAlertListIsScopedToOwnedPatients() {
        authDoctor(7L);
        when(patientMapper.selectList(any(Wrapper.class))).thenReturn(List.of(patient(10L, 7L)));

        alertService.listAlerts(new AlertQuery());

        verify(patientMapper).selectList(any(Wrapper.class));
        verify(alertMapper).selectPage(any(), any(Wrapper.class));
    }

    @Test
    void doctorCannotListGlobalOperationLogs() {
        authDoctor(7L);

        BusinessException exception = assertThrows(
                BusinessException.class, () -> operationLogService.listLogs(new LogQuery()));

        assertEquals(403, exception.getHttpStatus());
        verify(operationLogMapper, never()).selectPage(any(), any());
    }

    @Test
    void doctorCannotCreateFollowUpForAnotherDoctorsPatient() {
        authDoctor(7L);
        FollowUp followUp = new FollowUp();
        followUp.setPatientId(20L);
        followUp.setDoctorId(8L);
        when(patientMapper.selectById(20L)).thenReturn(patient(20L, 8L));

        BusinessException exception = assertThrows(BusinessException.class, () -> followUpService.addFollowUp(followUp));

        assertEquals(403, exception.getHttpStatus());
        verify(followUpMapper, never()).insert(any());
    }

    @Test
    void doctorIdFromRequestIsOverriddenByAuthenticatedDoctor() {
        authDoctor(7L);
        FollowUp followUp = new FollowUp();
        followUp.setPatientId(10L);
        followUp.setDoctorId(99L);
        when(patientMapper.selectById(10L)).thenReturn(patient(10L, 7L));
        when(followUpMapper.selectList(any(Wrapper.class))).thenReturn(List.of());

        followUpService.addFollowUp(followUp);

        assertEquals(7L, followUp.getDoctorId());
        verify(followUpMapper).insert(followUp);
    }

    private void authDoctor(Long id) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new CurrentUser(id, "doctor" + id, "DOCTOR"), null,
                List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))));
    }

    private Patient patient(Long id, Long doctorId) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setDoctorId(doctorId);
        patient.setStatus(1);
        return patient;
    }
}
