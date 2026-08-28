/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service;

import com.example.followup.dto.request.QuestionnaireQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.entity.Questionnaire;

/**
 * QuestionnaireService 业务接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
public interface QuestionnaireService {
    PageResponse<Questionnaire> listQuestionnaires(QuestionnaireQuery query);
    Questionnaire createQuestionnaire(Questionnaire questionnaire);
    Questionnaire updateQuestionnaire(Long id, Questionnaire questionnaire);
    void toggleQuestionnaire(Long id);
}
