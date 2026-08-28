/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.followup.dto.request.PatientUpdateRequest;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.Patient;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.security.CurrentUser;
import com.example.followup.service.impl.PatientServiceImpl;
import com.example.followup.util.SensitiveDataCipher;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PatientServiceAccessControlTest {
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private FollowUpMapper followUpMapper;
    @Mock
    private SysUserMapper sysUserMapper;
    @Mock
    private SensitiveDataCipher sensitiveDataCipher;
    @InjectMocks
    private PatientServiceImpl patientService;

    @BeforeAll
    static void initTableMetadata() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), Patient.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, "test"), FollowUp.class);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @BeforeEach
    void stubCipher() {
        lenient().when(sensitiveDataCipher.decrypt(any())).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(sensitiveDataCipher.encrypt(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("医生可以读取自己名下的患者")
    void doctorCanReadOwnPatient() {
        authAsDoctor(7L);
        when(patientMapper.selectById(1L)).thenReturn(patient(1L, 7L));

        assertEquals(1L, patientService.getPatientById(1L).getId());
    }

    @Test
    @DisplayName("医生不能读取其他医生名下的患者")
    void doctorCannotReadOtherDoctorsPatient() {
        authAsDoctor(7L);
        when(patientMapper.selectById(1L)).thenReturn(patient(1L, 8L));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> patientService.getPatientById(1L)
        );
        assertEquals(403, exception.getHttpStatus());
    }

    @Test
    @DisplayName("管理员可以读取任意医生名下的患者")
    void adminCanReadAnyPatient() {
        authAsAdmin();
        when(patientMapper.selectById(1L)).thenReturn(patient(1L, 8L));

        assertEquals("张三", patientService.getPatientById(1L).getName());
    }

    @Test
    @DisplayName("医生编辑患者时不能转移责任医生")
    void doctorCannotTransferPatientViaUpdate() {
        authAsDoctor(7L);
        Patient existing = patient(1L, 7L);
        when(patientMapper.selectById(1L)).thenReturn(existing);

        PatientUpdateRequest request = new PatientUpdateRequest();
        request.setName("张三");
        request.setGender("男");
        request.setDiseaseType("HYPERTENSION");
        request.setDoctorId(99L);

        patientService.updatePatient(1L, request);

        verify(patientMapper).updateById(existing);
        assertEquals(7L, existing.getDoctorId());
    }

    @Test
    @DisplayName("更新时不能提交脱敏后的敏感数据")
    void updateRejectsMaskedSensitiveData() {
        authAsDoctor(7L);
        when(patientMapper.selectById(1L)).thenReturn(patient(1L, 7L));

        PatientUpdateRequest request = new PatientUpdateRequest();
        request.setName("张三");
        request.setGender("男");
        request.setDiseaseType("HYPERTENSION");
        request.setPhone("138****5678");

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> patientService.updatePatient(1L, request)
        );
        assertEquals(400, exception.getHttpStatus());
        verify(patientMapper, never()).updateById(any());
    }

    private void authAsDoctor(Long userId) {
        auth(new CurrentUser(userId, "doctor", "DOCTOR"), "ROLE_DOCTOR");
    }

    private void authAsAdmin() {
        auth(new CurrentUser(1L, "admin", "ADMIN"), "ROLE_ADMIN");
    }

    private void auth(CurrentUser user, String authority) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority(authority)))
        );
    }

    private Patient patient(Long id, Long doctorId) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setName("张三");
        patient.setGender("男");
        patient.setDiseaseType("HYPERTENSION");
        patient.setDoctorId(doctorId);
        patient.setStatus(1);
        return patient;
    }
}
