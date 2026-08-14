package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.request.PatientSaveRequest;
import com.example.followup.dto.request.PatientUpdateRequest;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PatientVO;
import com.example.followup.entity.Patient;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.service.PatientService;
import com.example.followup.service.PatientMaskingService;
import com.example.followup.security.CurrentUser;
import com.example.followup.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private PatientMaskingService maskingService;

    @Override
    public PageResponse<PatientVO> listPatients(PatientQuery query) {
        CurrentUser currentUser = SecurityUtils.currentUser();
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getStatus, 1);
        if (!currentUser.isAdmin()) {
            wrapper.eq(Patient::getDoctorId, currentUser.getUserId());
        }
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Patient::getName, query.getName());
        }
        if (StringUtils.hasText(query.getDiseaseType())) {
            wrapper.eq(Patient::getDiseaseType, query.getDiseaseType());
        }
        wrapper.orderByDesc(Patient::getCreateTime);

        Page<Patient> page = new Page<>(query.getPage(), query.getSize());
        patientMapper.selectPage(page, wrapper);

        PageResponse<PatientVO> response = new PageResponse<>();
        response.setRecords(page.getRecords().stream()
                .map(patient -> maskingService.toVO(patient, currentUser.isAdmin()))
                .collect(Collectors.toList()));
        response.setTotal(page.getTotal());
        response.setPage(query.getPage());
        response.setSize(query.getSize());
        return response;
    }

    @Override
    public PatientVO getPatientById(Long id) {
        CurrentUser currentUser = SecurityUtils.currentUser();
        Patient patient = getAccessiblePatient(id, currentUser);
        return maskingService.toVO(patient, currentUser.isAdmin());
    }

    @Override
    public void addPatient(PatientSaveRequest request) {
        CurrentUser currentUser = SecurityUtils.currentUser();
        Patient patient = new Patient();
        applyEditableFields(patient, request, true);
        patient.setStatus(1);
        patient.setDoctorId(currentUser.isAdmin() ? request.getDoctorId() : currentUser.getUserId());
        patientMapper.insert(patient);
    }

    @Override
    public void updatePatient(Long id, PatientUpdateRequest request) {
        CurrentUser currentUser = SecurityUtils.currentUser();
        Patient existing = getAccessiblePatient(id, currentUser);
        applyUpdateFields(existing, request, currentUser.isAdmin());
        if (currentUser.isAdmin() && request.getDoctorId() != null) existing.setDoctorId(request.getDoctorId());
        patientMapper.updateById(existing);
    }

    @Override
    public void deletePatient(Long id) {
        CurrentUser currentUser = SecurityUtils.currentUser();
        Patient patient = getAccessiblePatient(id, currentUser);
        patient.setStatus(0);
        patientMapper.updateById(patient);
    }

    private Patient getAccessiblePatient(Long id, CurrentUser currentUser) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null || Integer.valueOf(0).equals(patient.getStatus())
                || (!currentUser.isAdmin() && !currentUser.getUserId().equals(patient.getDoctorId()))) {
            throw new BusinessException(404, "患者不存在");
        }
        return patient;
    }

    private void applyEditableFields(Patient target, PatientSaveRequest request, boolean allowSensitiveFields) {
        if (allowSensitiveFields) {
            rejectMaskedValue(request.getName(), "姓名");
            rejectMaskedValue(request.getPhone(), "手机号");
            rejectMaskedValue(request.getIdCard(), "身份证号");
            rejectMaskedValue(request.getAddress(), "地址");
            target.setName(request.getName());
            target.setPhone(request.getPhone());
            target.setIdCard(request.getIdCard());
            target.setAddress(request.getAddress());
        }
        target.setGender(request.getGender());
        target.setAge(request.getAge());
        target.setDiseaseType(request.getDiseaseType());
        target.setMedicalHistory(request.getMedicalHistory());
        target.setMedicationInfo(request.getMedicationInfo());
    }

    private void rejectMaskedValue(String value, String field) {
        if (value != null && value.contains("*")) {
            throw new BusinessException(400, field + "不能包含脱敏占位符");
        }
    }

    private void applyUpdateFields(Patient target, PatientUpdateRequest request, boolean admin) {
        if (admin) {
            rejectMaskedValue(request.getName(), "姓名");
            rejectMaskedValue(request.getPhone(), "手机号");
            rejectMaskedValue(request.getIdCard(), "身份证号");
            rejectMaskedValue(request.getAddress(), "地址");
            if (request.getName() != null) target.setName(request.getName());
            if (request.getPhone() != null) target.setPhone(request.getPhone());
            if (request.getIdCard() != null) target.setIdCard(request.getIdCard());
            if (request.getAddress() != null) target.setAddress(request.getAddress());
        }
        if (request.getGender() != null) target.setGender(request.getGender());
        if (request.getAge() != null) target.setAge(request.getAge());
        if (request.getDiseaseType() != null) target.setDiseaseType(request.getDiseaseType());
        if (request.getMedicalHistory() != null) target.setMedicalHistory(request.getMedicalHistory());
        if (request.getMedicationInfo() != null) target.setMedicationInfo(request.getMedicationInfo());
    }
}
