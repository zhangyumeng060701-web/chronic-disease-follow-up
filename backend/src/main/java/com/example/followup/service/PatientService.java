package com.example.followup.service;

import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.request.PatientSaveRequest;
import com.example.followup.dto.request.PatientUpdateRequest;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PatientVO;

public interface PatientService {
    PageResponse<PatientVO> listPatients(PatientQuery query);
    PatientVO getPatientById(Long id);
    void addPatient(PatientSaveRequest request);
    void updatePatient(Long id, PatientUpdateRequest request);
    void deletePatient(Long id);
    String exportPatientsCsv();
}
