/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import com.example.followup.dto.request.PatientFollowUpRequest;
import com.example.followup.dto.request.QuestionnaireSubmitRequest;
import com.example.followup.dto.request.VitalReportRequest;
import com.example.followup.dto.response.FollowUpPlanVO;
import com.example.followup.dto.response.FollowUpVO;
import com.example.followup.entity.Message;
import com.example.followup.entity.PatientVital;
import com.example.followup.entity.Questionnaire;

import java.util.List;
import java.util.Map;

/**
 * PatientPortalService 业务接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public interface PatientPortalService {
    List<FollowUpPlanVO> myPlans();
    List<PatientVital> myVitals();
    PatientVital reportVital(VitalReportRequest request);
    List<Questionnaire> activeQuestionnaires();
    void submitQuestionnaire(Long questionnaireId, QuestionnaireSubmitRequest request);
    List<Message> myMessages();
    long unreadMessageCount();
    void markMessageRead(Long id);
    List<FollowUpVO> myFollowUps();
    FollowUpVO createPatientFollowUp(PatientFollowUpRequest request);
    Map<String, Object> myRiskLevel();
}
