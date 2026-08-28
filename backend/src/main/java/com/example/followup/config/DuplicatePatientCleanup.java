/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.followup.entity.Patient;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.util.SensitiveDataCipher;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DuplicatePatientCleanup 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Component
public class DuplicatePatientCleanup implements ApplicationRunner {
    private static final List<String> REFERENCED_TABLES = List.of(
            "t_follow_up",
            "t_alert",
            "t_follow_up_plan",
            "t_follow_up_task",
            "t_patient_vitals",
            "t_questionnaire_submission",
            "t_patient_risk_assessment",
            "t_follow_up_suggestion"
    );

    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private SensitiveDataCipher sensitiveDataCipher;
    @Autowired
    private JdbcTemplate jdbcTemplate;

/**
 * 执行 run 操作。
 */
    @Override
    public void run(ApplicationArguments args) {
        List<Patient> patients = patientMapper.selectList(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getStatus, 1));
        Map<String, List<Patient>> groups = new LinkedHashMap<>();
        for (Patient patient : patients) {
            patientKey(patient).ifPresent(key ->
                    groups.computeIfAbsent(key, ignored -> new ArrayList<>()).add(patient));
        }

        int mergedGroups = 0;
        for (List<Patient> group : groups.values()) {
            if (group.size() < 2) {
                continue;
            }
            group.sort(Comparator.comparing(Patient::getId));
            Patient keep = group.get(0);
            for (int i = 1; i < group.size(); i++) {
                Long duplicateId = group.get(i).getId();
                for (String table : REFERENCED_TABLES) {
                    jdbcTemplate.update("UPDATE " + table + " SET patient_id = ? WHERE patient_id = ?",
                            keep.getId(), duplicateId);
                }
                patientMapper.deleteById(duplicateId);
                log.info("Merged duplicate patient: {} -> {}", duplicateId, keep.getId());
            }
            mergedGroups++;
        }

        addUniqueIndex("uk_patient_phone", "phone");
        addUniqueIndex("uk_patient_id_card", "id_card");
        log.info("Patient deduplication finished: {} group(s) merged, {} patient(s) remaining",
                mergedGroups, patientMapper.selectCount(new LambdaQueryWrapper<>()));
    }

    private Optional<String> patientKey(Patient patient) {
        String phone = sensitiveDataCipher.decrypt(patient.getPhone());
        String idCard = sensitiveDataCipher.decrypt(patient.getIdCard());
        String phonePart = StringUtils.hasText(phone) ? phone : "";
        String idCardPart = StringUtils.hasText(idCard) ? idCard : "";
        if (!StringUtils.hasText(phonePart) && !StringUtils.hasText(idCardPart)) {
            return Optional.empty();
        }
        return Optional.of(phonePart + "|" + idCardPart);
    }

    private void addUniqueIndex(String indexName, String column) {
        try {
            jdbcTemplate.execute("ALTER TABLE t_patient ADD UNIQUE KEY " + indexName + " (" + column + ")");
        } catch (DataAccessException ignored) {
            log.info("Unique index {} already exists or is not required", indexName);
        }
    }
}
