package com.example.followup.service;

import com.example.followup.annotation.OperationLog;
import com.example.followup.controller.PatientController;
import com.example.followup.entity.Patient;
import com.example.followup.entity.SysUser;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.security.CurrentUser;
import com.example.followup.service.impl.PatientServiceImpl;
import com.example.followup.util.SensitiveDataCipher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientExportServiceTest {
    @Mock PatientMapper patientMapper;
    @Mock FollowUpMapper followUpMapper;
    @Mock SysUserMapper sysUserMapper;
    @Mock SensitiveDataCipher cipher;
    @InjectMocks PatientServiceImpl service;

    @AfterEach void clear() { SecurityContextHolder.clearContext(); }

    @Test void adminExportContainsDecryptedOriginalValues() {
        auth(1L, "ADMIN"); stubData();
        String csv = service.exportPatientsCsv();
        assertTrue(csv.contains("\"张三\""));
        assertTrue(csv.contains("\"13812345678\""));
        assertFalse(csv.contains("enc:"));
    }

    @Test void doctorExportIsMaskedAndScopedByCurrentDoctor() {
        auth(7L, "DOCTOR"); stubData();
        String csv = service.exportPatientsCsv();
        assertTrue(csv.contains("\"张*\""));
        assertTrue(csv.contains("\"138****5678\""));
        assertFalse(csv.contains("13812345678"));
    }

    @Test void exportEndpointCarriesAuditMetadata() throws Exception {
        Method method = PatientController.class.getMethod("export");
        OperationLog annotation = method.getAnnotation(OperationLog.class);
        assertEquals("导出患者", annotation.operation());
        assertEquals("Patient", annotation.targetType());
    }

    private void stubData() {
        Patient patient = new Patient(); patient.setId(1L); patient.setName("张三"); patient.setGender("男"); patient.setAge(52);
        patient.setPhone("enc:phone"); patient.setDiseaseType("HYPERTENSION"); patient.setDoctorId(7L); patient.setStatus(1);
        SysUser doctor = new SysUser(); doctor.setId(7L); doctor.setRealName("李医生");
        when(patientMapper.selectList(any())).thenReturn(List.of(patient));
        when(sysUserMapper.selectBatchIds(any())).thenReturn(List.of(doctor));
        when(cipher.decrypt("enc:phone")).thenReturn("13812345678");
        when(cipher.decrypt(null)).thenReturn(null);
    }

    private void auth(Long id, String role) {
        CurrentUser user = new CurrentUser(id, role.toLowerCase(), role);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))));
    }
}
