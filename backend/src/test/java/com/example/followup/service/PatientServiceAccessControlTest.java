package com.example.followup.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.example.followup.entity.Patient;
import com.example.followup.entity.FollowUp;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.security.CurrentUser;
import com.example.followup.service.impl.PatientServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceAccessControlTest {

    @Mock
    private PatientMapper patientMapper;
    @Mock
    private FollowUpMapper followUpMapper;
    @Mock
    private SysUserMapper sysUserMapper;
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
