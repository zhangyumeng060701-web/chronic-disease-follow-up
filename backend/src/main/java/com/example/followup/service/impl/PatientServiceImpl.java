package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.request.PatientSaveRequest;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PatientVO;
import com.example.followup.entity.Patient;
import com.example.followup.exception.BusinessException;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.service.PatientService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientMapper patientMapper;

    @Override
    public PageResponse<PatientVO> listPatients(PatientQuery query, String role) {
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

        PageResponse<PatientVO> response = new PageResponse<>();
        response.setRecords(page.getRecords().stream()
                .map(patient -> PatientVO.from(patient, role))
                .collect(Collectors.toList()));
        response.setTotal(page.getTotal());
        response.setPage(query.getPage());
        response.setSize(query.getSize());
        return response;
    }

    @Override
    public PatientVO getPatientById(Long id, String role) {
        return PatientVO.from(getPatientEntityById(id), role);
    }

    @Override
    public void addPatient(PatientSaveRequest request) {
        Patient patient = new Patient();
        BeanUtils.copyProperties(request, patient);
        patient.setId(null);
        patient.setStatus(1);
        patientMapper.insert(patient);
    }

    @Override
    public void updatePatient(Long id, PatientSaveRequest request) {
        Patient patient = getPatientEntityById(id);
        copyNonNullProperties(request, patient);
        patient.setId(id);
        patientMapper.updateById(patient);
    }

    @Override
    public void deletePatient(Long id) {
        Patient patient = getPatientEntityById(id);
        patient.setStatus(0);
        patientMapper.updateById(patient);
    }

    private Patient getPatientEntityById(Long id) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null || patient.getStatus() == 0) {
            throw new BusinessException(404, "patient not found");
        }
        return patient;
    }

    private void copyNonNullProperties(PatientSaveRequest request, Patient patient) {
        if (request.getName() != null) {
            patient.setName(request.getName());
        }
        if (request.getGender() != null) {
            patient.setGender(request.getGender());
        }
        if (request.getAge() != null) {
            patient.setAge(request.getAge());
        }
        if (request.getPhone() != null) {
            patient.setPhone(request.getPhone());
        }
        if (request.getIdCard() != null) {
            patient.setIdCard(request.getIdCard());
        }
        if (request.getAddress() != null) {
            patient.setAddress(request.getAddress());
        }
        if (request.getDiseaseType() != null) {
            patient.setDiseaseType(request.getDiseaseType());
        }
        if (request.getMedicalHistory() != null) {
            patient.setMedicalHistory(request.getMedicalHistory());
        }
        if (request.getMedicationInfo() != null) {
            patient.setMedicationInfo(request.getMedicationInfo());
        }
        if (request.getDoctorId() != null) {
            patient.setDoctorId(request.getDoctorId());
        }
    }
}
