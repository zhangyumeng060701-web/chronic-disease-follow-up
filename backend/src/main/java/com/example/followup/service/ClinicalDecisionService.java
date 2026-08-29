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

    /**
     * 执行assessPatientRisk操作。
     *
     * @param patientId 参数说明
     * @return 返回值
     */
    PatientRiskAssessment assessPatientRisk(Long patientId);

    /**
     * 执行generateSuggestion操作。
     *
     * @param patientId 参数说明
     * @return 返回值
     */
    FollowUpSuggestion generateSuggestion(Long patientId);

    /**
     * 执行generateAISuggestion操作。
     *
     * @param request 参数说明
     * @return 返回值
     */
    FollowUpSuggestion generateAISuggestion(AISuggestionRequest request);

    /**
     * 查询listSuggestions。
     *
     * @param page 参数说明
     * @param size 参数说明
     * @param status 参数说明
     * @return 返回值
     */
    PageResponse<FollowUpSuggestion> listSuggestions(Integer page, Integer size, String status);

    /**
     * 执行confirmSuggestion操作。
     *
     * @param id 参数说明
     */
    void confirmSuggestion(Long id);

    /**
     * 执行rejectSuggestion操作。
     *
     * @param id 参数说明
     */
    void rejectSuggestion(Long id);
}
