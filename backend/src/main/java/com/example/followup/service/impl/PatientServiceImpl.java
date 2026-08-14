package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.PatientQuery;
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
    public void addPatient(Patient patient) {
        CurrentUser currentUser = SecurityUtils.currentUser();
        patient.setId(null);
        patient.setStatus(1);
        if (!currentUser.isAdmin()) patient.setDoctorId(currentUser.getUserId());
        patientMapper.insert(patient);
    }

    @Override
    public void updatePatient(Patient patient) {
        CurrentUser currentUser = SecurityUtils.currentUser();
        Patient existing = getAccessiblePatient(patient.getId(), currentUser);
        if (!currentUser.isAdmin()) patient.setDoctorId(existing.getDoctorId());
        patient.setStatus(existing.getStatus());
        patientMapper.updateById(patient);
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
}
