/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import com.example.followup.dto.request.AISuggestionRequest;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.FollowUpSuggestion;
import com.example.followup.entity.PatientRiskAssessment;

/**
 * ClinicalDecisionService 业务接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public interface ClinicalDecisionService {
    PatientRiskAssessment assessPatientRisk(Long patientId);
    FollowUpSuggestion generateSuggestion(Long patientId);
    FollowUpSuggestion generateAISuggestion(AISuggestionRequest request);
    PageResponse<FollowUpSuggestion> listSuggestions(Integer page, Integer size, String status);
    void confirmSuggestion(Long id);
    void rejectSuggestion(Long id);
}
