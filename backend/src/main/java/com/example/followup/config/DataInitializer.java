/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.config;

import com.example.followup.constant.DomainConstants;
import com.example.followup.entity.Patient;
import com.example.followup.entity.SysUser;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.SysUserMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Component
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 执行 run 操作。
     */
    @Override
    public void run(ApplicationArguments args) {
        createDefaultUserIfMissing("admin", "管理员", DomainConstants.ROLE_ADMIN);
        createDefaultUserIfMissing("doctor", "李医生", DomainConstants.ROLE_DOCTOR);
        createDefaultPatientIfMissing();
    }

    private void createDefaultUserIfMissing(String username, String realName, String role) {
        if (sysUserMapper.findByUsername(username) != null) {
            return;
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRealName(realName);
        user.setRole(role);
        user.setPhone("");
        user.setStatus(1);
        sysUserMapper.insert(user);
        log.info("Initialized default user: {}", username);
    }

    private void createDefaultPatientIfMissing() {
        SysUser doctor = sysUserMapper.findByUsername("doctor");
        Long doctorId = doctor == null ? null : doctor.getId();
        if (patientMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Patient>()
                        .eq(Patient::getPhone, "13800138000")) > 0) {
            return;
        }
        Patient patient = new Patient();
        patient.setName("王小明");
        patient.setGender("男");
        patient.setAge(52);
        patient.setPhone("13800138000");
        patient.setIdCard("110101199001011234");
        patient.setDiseaseType(DomainConstants.DISEASE_HYPERTENSION);
        patient.setDoctorId(doctorId);
        patient.setStatus(1);
        patientMapper.insert(patient);
        log.info("Initialized default patient: {}", patient.getName());
    }
}
