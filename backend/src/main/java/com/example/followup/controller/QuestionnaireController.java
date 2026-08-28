/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.controller;

import com.example.followup.dto.request.QuestionnaireQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.Questionnaire;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.security.SecurityUtils;
import com.example.followup.service.QuestionnaireService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * QuestionnaireController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/questionnaires")
@Api(tags = "问卷管理")
public class QuestionnaireController {
    @Autowired
    private QuestionnaireService questionnaireService;

    @GetMapping
    @ApiOperation(value = "分页查询问卷")
    public Result<PageResponse<Questionnaire>> list(@Valid QuestionnaireQuery query) {
        return Result.success(questionnaireService.listQuestionnaires(query));
    }

/**
 * 执行 create 操作。
 */
    @PostMapping
    @ApiOperation(value = "新增问卷")
    public Result<Questionnaire> create(@RequestBody Questionnaire questionnaire) {
        requireAdmin();
        return Result.success(questionnaireService.createQuestionnaire(questionnaire));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "更新问卷")
    public Result<Questionnaire> update(@PathVariable Long id, @RequestBody Questionnaire questionnaire) {
        requireAdmin();
        return Result.success(questionnaireService.updateQuestionnaire(id, questionnaire));
    }

/**
 * 执行 toggle 操作。
 */
    @PutMapping("/{id}/toggle")
    @ApiOperation(value = "启用/停用问卷")
    public Result<Void> toggle(@PathVariable Long id) {
        requireAdmin();
        questionnaireService.toggleQuestionnaire(id);
        return Result.success();
    }

    private void requireAdmin() {
        if (!SecurityUtils.isAdmin()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
