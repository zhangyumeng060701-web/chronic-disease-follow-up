/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.request.PatientSaveRequest;
import com.example.followup.dto.request.PatientUpdateRequest;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PageResponseUtil;
import com.example.followup.dto.response.PatientVO;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.Patient;
import com.example.followup.entity.SysUser;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.security.SecurityUtils;
import com.example.followup.service.OperationLogService;
import com.example.followup.service.PatientService;
import com.example.followup.util.DesensitizationUtil;
import com.example.followup.util.SensitiveDataCipher;
import com.example.followup.util.VoMappers;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.stream.Collectors;

/**
 * PatientServiceImpl 业务实现。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Service
public class PatientServiceImpl implements PatientService {
    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private FollowUpMapper followUpMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SensitiveDataCipher sensitiveDataCipher;
    @Autowired
    private OperationLogService operationLogService;

/**
 * 执行 listPatients 操作。
 */
    @Override
    public PageResponse<PatientVO> listPatients(PatientQuery query) {
        long start = System.currentTimeMillis();
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getStatus, 1);
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Patient::getName, query.getName());
        }
        if (StringUtils.hasText(query.getDiseaseType())) {
            wrapper.eq(Patient::getDiseaseType, query.getDiseaseType());
        }
        if (!SecurityUtils.isAdmin()) {
            wrapper.eq(Patient::getDoctorId, SecurityUtils.currentUser().getUserId());
        }
        wrapper.orderByDesc(Patient::getCreateTime);

        Page<Patient> page = new Page<>(query.getPage(), query.getSize());
        patientMapper.selectPage(page, wrapper);

        List<PatientVO> vos = page.getRecords().stream()
                .map(VoMappers::toPatientVO)
                .collect(Collectors.toList());
        vos.forEach(this::decryptSensitive);
        enrich(vos);

        boolean admin = SecurityUtils.isAdmin();
        if (!admin) {
            vos.forEach(this::desensitize);
        }

        log.info("listPatients userId={} total={} cost={}ms",
                currentUserIdSafely().orElse(-1L), page.getTotal(), System.currentTimeMillis() - start);
        return PageResponseUtil.of(page, vos, query.getPage(), query.getSize());
    }

/**
 * 执行 getPatientById 操作。
 */
    @Override
    public PatientVO getPatientById(Long id) {
        long start = System.currentTimeMillis();
        Patient patient = patientMapper.selectById(id);
        if (patient == null || patient.getStatus() == 0) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }
        if (!SecurityUtils.isAdmin() && !Objects.equals(patient.getDoctorId(), SecurityUtils.currentUser().getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        PatientVO vo = VoMappers.toPatientVO(patient);
        decryptSensitive(vo);
        enrich(List.of(vo));
        if (!SecurityUtils.isAdmin()) {
            desensitize(vo);
        }
        log.info("getPatientById id={} cost={}ms", id, System.currentTimeMillis() - start);
        return vo;
    }

/**
 * 执行 addPatient 操作。
 */
    @Override
    public void addPatient(PatientSaveRequest request) {
        long start = System.currentTimeMillis();
        Patient patient = new Patient();
        BeanUtils.copyProperties(request, patient, "id", "status", "createTime", "updateTime");
        patient.setId(null);
        patient.setStatus(1);
        encryptSensitive(patient);
        if (!SecurityUtils.isAdmin()) {
            patient.setDoctorId(SecurityUtils.currentUser().getUserId());
        }
        applyBmi(patient);
        patientMapper.insert(patient);
        log.info("addPatient id={} cost={}ms", patient.getId(), System.currentTimeMillis() - start);
    }

/**
 * 执行 updatePatient 操作。
 */
    @Override
    @Transactional
    public void updatePatient(Long id, PatientUpdateRequest request) {
        long start = System.currentTimeMillis();
        Patient patient = getExistingPatient(id);
        assertNotMasked(request);
        BeanUtils.copyProperties(request, patient, "id", "status", "createTime", "updateTime", "doctorId");
        if (SecurityUtils.isAdmin()) {
            patient.setDoctorId(request.getDoctorId());
        }
        encryptSensitive(patient);
        applyBmi(patient);
        patientMapper.updateById(patient);
        log.info("updatePatient id={} cost={}ms", id, System.currentTimeMillis() - start);
    }

/**
 * 执行 deletePatient 操作。
 */
    @Override
    @Transactional
    public void deletePatient(Long id) {
        long start = System.currentTimeMillis();
        Patient patient = getExistingPatient(id);
        patient.setStatus(0);
        patientMapper.updateById(patient);
        log.info("deletePatient id={} cost={}ms", id, System.currentTimeMillis() - start);
    }

    private Patient getExistingPatient(Long id) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null || patient.getStatus() == 0) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }
        if (!SecurityUtils.isAdmin() && !Objects.equals(patient.getDoctorId(), SecurityUtils.currentUser().getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return patient;
    }

    private void assertNotMasked(PatientUpdateRequest request) {
        if (containsMask(request.getName()) || containsMask(request.getPhone())
                || containsMask(request.getIdCard()) || containsMask(request.getAddress())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能提交脱敏后的患者敏感数据");
        }
    }

    private boolean containsMask(String value) {
        return value != null && value.contains("*");
    }

    private void applyBmi(Patient patient) {
        if (patient.getHeightCm() != null && patient.getHeightCm().compareTo(BigDecimal.ZERO) > 0
                && patient.getWeightKg() != null && patient.getWeightKg().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightMeter = patient.getHeightCm().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal bmi = patient.getWeightKg()
                    .divide(heightMeter.multiply(heightMeter), 1, RoundingMode.HALF_UP);
            patient.setBmi(bmi);
        } else {
            patient.setBmi(null);
        }
    }

    private void encryptSensitive(Patient patient) {
        patient.setPhone(sensitiveDataCipher.encrypt(patient.getPhone()));
        patient.setIdCard(sensitiveDataCipher.encrypt(patient.getIdCard()));
    }

    private void decryptSensitive(PatientVO vo) {
        vo.setPhone(sensitiveDataCipher.decrypt(vo.getPhone()));
        vo.setIdCard(sensitiveDataCipher.decrypt(vo.getIdCard()));
    }

/**
 * 执行 exportPatientsCsv 操作。
 */
    @Override
    public String exportPatientsCsv() {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getStatus, 1);
        if (!SecurityUtils.isAdmin()) {
            wrapper.eq(Patient::getDoctorId, SecurityUtils.currentUser().getUserId());
        }
        List<PatientVO> vos = patientMapper.selectList(wrapper).stream()
                .map(VoMappers::toPatientVO)
                .collect(Collectors.toList());
        vos.forEach(this::decryptSensitive);
        vos.forEach(this::desensitize);
        StringBuilder csv = new StringBuilder("姓名,性别,年龄,手机号,慢病类型,责任医生\n");
        List<Long> doctorIds = vos.stream().map(PatientVO::getDoctorId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> doctorNames = doctorIds.isEmpty() ? Map.of() :
                sysUserMapper.selectBatchIds(doctorIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName, (a, b) -> a));
        for (PatientVO vo : vos) {
            csv.append(csvCell(vo.getName())).append(',')
                    .append(csvCell(vo.getGender())).append(',')
                    .append(csvCell(vo.getAge() == null ? "" : String.valueOf(vo.getAge()))).append(',')
                    .append(csvCell(vo.getPhone())).append(',')
                    .append(csvCell(vo.getDiseaseType())).append(',')
                    .append(csvCell(doctorNames.getOrDefault(vo.getDoctorId(), ""))).append('\n');
        }
        operationLogService.log(
                SecurityUtils.currentUser().getUserId(),
                SecurityUtils.currentUser().getUsername(),
                "导出患者",
                "Patient",
                null,
                "导出患者脱敏CSV，行数" + vos.size()
        );
        log.info("exportPatientsCsv rows={}", vos.size());
        return csv.toString();
    }

    private String csvCell(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private OptionalLong currentUserIdSafely() {
        try {
            Long userId = SecurityUtils.currentUser().getUserId();
            return userId == null ? OptionalLong.empty() : OptionalLong.of(userId);
        } catch (BusinessException e) {
            return OptionalLong.empty();
        }
    }

    private void enrich(List<PatientVO> vos) {
        if (vos.isEmpty()) {
            return;
        }

        List<Long> patientIds = vos.stream()
                .map(PatientVO::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (!patientIds.isEmpty()) {
            Map<Long, LocalDate> latestFollowUpMap = followUpMapper.selectList(
                            new LambdaQueryWrapper<FollowUp>()
                                    .in(FollowUp::getPatientId, patientIds)
                                    .isNotNull(FollowUp::getFollowUpDate)
                                    .select(FollowUp::getPatientId, FollowUp::getFollowUpDate))
                    .stream()
                    .collect(Collectors.toMap(
                            FollowUp::getPatientId,
                            FollowUp::getFollowUpDate,
                            (a, b) -> a.isAfter(b) ? a : b
                    ));
            vos.forEach(vo -> vo.setLastFollowUpDate(latestFollowUpMap.get(vo.getId())));
        }

        List<Long> doctorIds = vos.stream()
                .map(PatientVO::getDoctorId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (!doctorIds.isEmpty()) {
            Map<Long, String> doctorNameMap = sysUserMapper.selectBatchIds(doctorIds).stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName, (a, b) -> a));
            vos.forEach(vo -> vo.setDoctorName(doctorNameMap.get(vo.getDoctorId())));
        }
    }

    private void desensitize(PatientVO vo) {
        vo.setName(DesensitizationUtil.maskName(vo.getName()));
        vo.setPhone(DesensitizationUtil.maskPhone(vo.getPhone()));
        vo.setIdCard(DesensitizationUtil.maskIdCard(vo.getIdCard()));
        vo.setAddress(DesensitizationUtil.maskAddress(vo.getAddress()));
    }
}
