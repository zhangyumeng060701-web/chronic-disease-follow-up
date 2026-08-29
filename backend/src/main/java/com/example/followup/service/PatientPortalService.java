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
    /**
     * 执行myPlans操作。
     *
     * @return 返回值
     */
    List<FollowUpPlanVO> myPlans();
    /**
     * 执行myVitals操作。
     *
     * @return 返回值
     */
    List<PatientVital> myVitals();
    /**
     * 执行reportVital操作。
     *
     * @param request 参数说明
     * @return 返回值
     */
    PatientVital reportVital(VitalReportRequest request);
    /**
     * 执行activeQuestionnaires操作。
     *
     * @return 返回值
     */
    List<Questionnaire> activeQuestionnaires();
    /**
     * 提交submitQuestionnaire。
     *
     * @param questionnaireId 参数说明
     * @param request 参数说明
     */
    void submitQuestionnaire(Long questionnaireId, QuestionnaireSubmitRequest request);
    /**
     * 执行myMessages操作。
     *
     * @return 返回值
     */
    List<Message> myMessages();
    /**
     * 执行unreadMessageCount操作。
     *
     * @return 返回值
     */
    long unreadMessageCount();
    /**
     * 执行markMessageRead操作。
     *
     * @param id 参数说明
     */
    void markMessageRead(Long id);
    /**
     * 执行myFollowUps操作。
     *
     * @return 返回值
     */
    List<FollowUpVO> myFollowUps();
    /**
     * 新增createPatientFollowUp。
     *
     * @param request 参数说明
     * @return 返回值
     */
    FollowUpVO createPatientFollowUp(PatientFollowUpRequest request);
    /**
     * 执行myRiskLevel操作。
     *
     * @return 返回值
     */
    Map<String, Object> myRiskLevel();
}
