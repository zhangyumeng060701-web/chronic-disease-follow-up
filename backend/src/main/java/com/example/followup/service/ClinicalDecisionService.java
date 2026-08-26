package com.example.followup.service;

import com.example.followup.dto.request.AISuggestionRequest;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.FollowUpSuggestion;
import com.example.followup.entity.PatientRiskAssessment;

public interface ClinicalDecisionService {
    PatientRiskAssessment assessPatientRisk(Long patientId);
    FollowUpSuggestion generateSuggestion(Long patientId);
    FollowUpSuggestion generateAISuggestion(AISuggestionRequest request);
    PageResponse<FollowUpSuggestion> listSuggestions(Integer page, Integer size, String status);
    void confirmSuggestion(Long id);
    void rejectSuggestion(Long id);
}
