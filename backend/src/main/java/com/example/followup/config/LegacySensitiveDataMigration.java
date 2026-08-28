package com.example.followup.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.followup.entity.Patient;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.util.SensitiveDataCipher;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Component
public class LegacySensitiveDataMigration implements ApplicationRunner {

    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private SensitiveDataCipher sensitiveDataCipher;

    @Override
    public void run(ApplicationArguments args) {
        if (!sensitiveDataCipher.isConfigured()) {
            log.info("DATA_ENCRYPTION_KEY is not configured, skip plaintext migration");
            return;
        }

        List<Patient> patients = patientMapper.selectList(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getStatus, 1));
        int migrated = 0;
        for (Patient patient : patients) {
            boolean changed = false;
            if (StringUtils.hasText(patient.getPhone()) && !patient.getPhone().startsWith("enc:")) {
                patient.setPhone(sensitiveDataCipher.encrypt(patient.getPhone()));
                changed = true;
            }
            if (StringUtils.hasText(patient.getIdCard()) && !patient.getIdCard().startsWith("enc:")) {
                patient.setIdCard(sensitiveDataCipher.encrypt(patient.getIdCard()));
                changed = true;
            }
            if (changed) {
                patientMapper.updateById(patient);
                migrated++;
            }
        }
        log.info("Sensitive field migration completed, migrated {} patient(s)", migrated);
    }
}
