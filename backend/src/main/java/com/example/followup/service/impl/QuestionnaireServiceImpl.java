/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.QuestionnaireQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PageResponseUtil;
import com.example.followup.entity.Questionnaire;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.QuestionnaireMapper;
import com.example.followup.service.QuestionnaireService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * QuestionnaireServiceImpl 业务实现。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {
    @Autowired
    private QuestionnaireMapper questionnaireMapper;

    /**
     * 查询listQuestionnaires。
     *
     * @param query 参数说明
     * @return 返回值
     */
    @Override
    public PageResponse<Questionnaire> listQuestionnaires(QuestionnaireQuery query) {
        LambdaQueryWrapper<Questionnaire> wrapper = new LambdaQueryWrapper<>();
        if (query.getIsActive() != null) {
            wrapper.eq(Questionnaire::getIsActive, query.getIsActive());
        }
        wrapper.orderByDesc(Questionnaire::getCreateTime);
        Page<Questionnaire> page = new Page<>(query.getPage(), query.getSize());
        questionnaireMapper.selectPage(page, wrapper);
        return PageResponseUtil.of(page, page.getRecords(), query.getPage(), query.getSize());
    }

    /**
     * 新增createQuestionnaire。
     *
     * @param questionnaire 参数说明
     * @return 返回值
     */
    @Override
    public Questionnaire createQuestionnaire(Questionnaire questionnaire) {
        if (!StringUtils.hasText(questionnaire.getCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "问卷编码不能为空");
        }
        if (questionnaire.getIsActive() == null) {
            questionnaire.setIsActive(1);
        }
        questionnaireMapper.insert(questionnaire);
        return questionnaire;
    }

    /**
     * 更新updateQuestionnaire。
     *
     * @param id 参数说明
     * @param questionnaire 参数说明
     * @return 返回值
     */
    @Override
    public Questionnaire updateQuestionnaire(Long id, Questionnaire questionnaire) {
        Questionnaire existing = getOrThrow(id);
        existing.setCode(questionnaire.getCode());
        existing.setTitle(questionnaire.getTitle());
        existing.setDescription(questionnaire.getDescription());
        existing.setContent(questionnaire.getContent());
        if (questionnaire.getIsActive() != null) {
            existing.setIsActive(questionnaire.getIsActive());
        }
        questionnaireMapper.updateById(existing);
        return existing;
    }

    /**
     * 执行toggleQuestionnaire操作。
     *
     * @param id 参数说明
     */
    @Override
    public void toggleQuestionnaire(Long id) {
        Questionnaire questionnaire = getOrThrow(id);
        questionnaire.setIsActive(questionnaire.getIsActive() != null && questionnaire.getIsActive() == 1 ? 0 : 1);
        questionnaireMapper.updateById(questionnaire);
    }

    private Questionnaire getOrThrow(Long id) {
        Questionnaire questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "问卷不存在");
        }
        return questionnaire;
    }
}
