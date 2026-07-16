package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.Patient;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientMapper patientMapper;

    @Override
    public PageResponse<Patient> listPatients(PatientQuery query) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getStatus, 1);
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Patient::getName, query.getName());
        }
        if (StringUtils.hasText(query.getDiseaseType())) {
            wrapper.eq(Patient::getDiseaseType, query.getDiseaseType());
        }
        wrapper.orderByDesc(Patient::getCreateTime);

        Page<Patient> page = new Page<>(query.getPage(), query.getSize());
        patientMapper.selectPage(page, wrapper);

        PageResponse<Patient> response = new PageResponse<>();
        response.setRecords(page.getRecords());
        response.setTotal(page.getTotal());
        response.setPage(query.getPage());
        response.setSize(query.getSize());
        return response;
    }

    @Override
    public Patient getPatientById(Long id) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null || patient.getStatus() == 0) {
            throw new BusinessException(404, "患者不存在");
        }
        return patient;
    }

    @Override
    public void addPatient(Patient patient) {
        patient.setId(null);
        patient.setStatus(1);
        patientMapper.insert(patient);
    }

    @Override
    public void updatePatient(Patient patient) {
        Patient existing = getPatientById(patient.getId());
        patientMapper.updateById(patient);
    }

    @Override
    public void deletePatient(Long id) {
        Patient patient = getPatientById(id);
        patient.setStatus(0);
        patientMapper.updateById(patient);
    }
}