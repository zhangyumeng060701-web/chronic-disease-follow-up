package com.example.followup.service;

import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.request.PatientSaveRequest;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PatientVO;

public interface PatientService {
    PageResponse<PatientVO> listPatients(PatientQuery query, String role);
    PatientVO getPatientById(Long id, String role);
    void addPatient(PatientSaveRequest request);
    void updatePatient(Long id, PatientSaveRequest request);
    void deletePatient(Long id);
}
