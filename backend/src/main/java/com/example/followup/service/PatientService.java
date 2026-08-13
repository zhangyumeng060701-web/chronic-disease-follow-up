package com.example.followup.service;

import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PatientVO;
import com.example.followup.entity.Patient;

public interface PatientService {
    PageResponse<PatientVO> listPatients(PatientQuery query);
    PatientVO getPatientById(Long id);
    void addPatient(Patient patient);
    void updatePatient(Patient patient);
    void deletePatient(Long id);
}
