/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.followup.dto.response.DoctorStats;
import com.example.followup.dto.response.StatsOverview;
import com.example.followup.entity.Alert;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.FollowUpTask;
import com.example.followup.entity.Patient;
import com.example.followup.entity.SysUser;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.FollowUpTaskMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.security.CurrentUser;
import com.example.followup.service.impl.StatsServiceImpl;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private FollowUpMapper followUpMapper;
    @Mock
    private AlertMapper alertMapper;
    @Mock
    private SysUserMapper sysUserMapper;
    @Mock
    private FollowUpTaskMapper followUpTaskMapper;
    @InjectMocks
    private StatsServiceImpl statsService;

    @BeforeAll
    static void initTableMetadata() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), Patient.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), FollowUp.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), FollowUpTask.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), Alert.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), SysUser.class);
    }

    @BeforeEach
    void authAsAdmin() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new CurrentUser(1L, "admin", "ADMIN"),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("总览统计按去重患者计算随访完成率")
    void overviewCalculatesCompletionRate() {
        Patient patient1 = new Patient();
        patient1.setId(1L);
        patient1.setStatus(1);
        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setStatus(1);

        FollowUp first = new FollowUp();
        first.setPatientId(1L);
        first.setFollowUpDate(LocalDate.now().withDayOfMonth(1));
        FollowUp second = new FollowUp();
        second.setPatientId(1L);
        second.setFollowUpDate(LocalDate.now().withDayOfMonth(2));

        when(patientMapper.selectCount(any())).thenReturn(2L);
        when(patientMapper.selectList(any())).thenReturn(List.of(patient1, patient2));
        when(followUpMapper.selectList(any())).thenReturn(List.of(first, second));
        when(followUpTaskMapper.selectCount(any())).thenReturn(0L);
        when(alertMapper.selectList(any())).thenReturn(List.of());

        StatsOverview result = statsService.getOverview();

        assertEquals(2L, result.getTotalPatients());
        assertEquals(1, result.getMonthlyCompleted());
        assertEquals(2, result.getMonthlyExpected());
        assertEquals("50.0%", result.getCompletionRate());
        assertEquals(0L, result.getHighRiskCount());
        assertEquals(0L, result.getLostFollowUpCount());
    }

    @Test
    @DisplayName("医生总览统计只查询自己名下患者")
    void doctorOverviewUsesDoctorFilter() {
        authAsDoctor(7L);
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setDoctorId(7L);
        patient.setStatus(1);

        when(patientMapper.selectCount(any())).thenReturn(2L);
        when(patientMapper.selectList(any())).thenReturn(List.of(patient));
        when(followUpMapper.selectList(any())).thenReturn(List.of());
        when(followUpTaskMapper.selectCount(any())).thenReturn(0L);
        when(alertMapper.selectList(any())).thenReturn(List.of());

        StatsOverview result = statsService.getOverview();

        assertEquals(2L, result.getTotalPatients());

        ArgumentCaptor<Wrapper<Patient>> captor = ArgumentCaptor.forClass(Wrapper.class);
        verify(patientMapper, atLeast(1)).selectCount(captor.capture());
        assertTrue(captor.getAllValues().stream()
                .anyMatch(wrapper -> wrapper.getSqlSegment().contains("doctor_id")));

        ArgumentCaptor<Wrapper<FollowUpTask>> taskCaptor = ArgumentCaptor.forClass(Wrapper.class);
        verify(followUpTaskMapper, atLeast(1)).selectCount(taskCaptor.capture());
        assertTrue(taskCaptor.getAllValues().stream()
                .anyMatch(wrapper -> wrapper.getSqlSegment().contains("owner_id")));
    }

    @Test
    @DisplayName("医生对比高危患者数不超过在管患者数")
    void doctorComparisonCapsHighRiskToManagedPatients() {
        SysUser doctor = new SysUser();
        doctor.setId(2L);
        doctor.setRealName("李医生");
        doctor.setRole("DOCTOR");
        doctor.setStatus(1);

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setDoctorId(2L);
        patient.setStatus(1);

        Alert firstAlert = new Alert();
        firstAlert.setPatientId(1L);
        firstAlert.setAlertLevel("RED");
        firstAlert.setIsResolved(0);
        Alert secondAlert = new Alert();
        secondAlert.setPatientId(1L);
        secondAlert.setAlertLevel("RED");
        secondAlert.setIsResolved(0);

        when(sysUserMapper.selectList(any())).thenReturn(List.of(doctor));
        when(patientMapper.selectList(any())).thenReturn(List.of(patient));
        when(followUpMapper.selectList(any())).thenReturn(List.of());
        when(alertMapper.selectList(any())).thenReturn(List.of(firstAlert, secondAlert));

        List<DoctorStats> result = statsService.getDoctorComparison();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPatientCount());
        assertEquals(1L, result.get(0).getHighRiskCount());
    }

    private void authAsDoctor(Long userId) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new CurrentUser(userId, "doctor", "DOCTOR"),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                )
        );
    }
}
