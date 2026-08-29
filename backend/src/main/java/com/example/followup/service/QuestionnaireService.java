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
    /**
     * 查询listQuestionnaires。
     *
     * @param query 参数说明
     * @return 返回值
     */
    PageResponse<Questionnaire> listQuestionnaires(QuestionnaireQuery query);
    /**
     * 新增createQuestionnaire。
     *
     * @param questionnaire 参数说明
     * @return 返回值
     */
    Questionnaire createQuestionnaire(Questionnaire questionnaire);
    /**
     * 更新updateQuestionnaire。
     *
     * @param id 参数说明
     * @param questionnaire 参数说明
     * @return 返回值
     */
    Questionnaire updateQuestionnaire(Long id, Questionnaire questionnaire);
    /**
     * 执行toggleQuestionnaire操作。
     *
     * @param id 参数说明
     */
    void toggleQuestionnaire(Long id);
}
