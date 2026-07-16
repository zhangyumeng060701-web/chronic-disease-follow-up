package com.example.followup.service;

import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.Patient;

public interface PatientService {
    PageResponse<Patient> listPatients(PatientQuery query);
    Patient getPatientById(Long id);
    void addPatient(Patient patient);
    void updatePatient(Patient patient);
    void deletePatient(Long id);
}