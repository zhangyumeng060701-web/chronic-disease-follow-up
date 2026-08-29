/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import com.example.followup.annotation.OperationLog;
import com.example.followup.dto.request.AISuggestionRequest;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.FollowUpSuggestion;
import com.example.followup.entity.PatientRiskAssessment;
import com.example.followup.service.ClinicalDecisionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * ClinicalDecisionController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/clinical")
@Api(tags = "临床决策支持")
public class ClinicalDecisionController {
    @Autowired
    private ClinicalDecisionService clinicalDecisionService;

    /**
     * 评估患者风险分层
     *
     * @param patientId 参数说明
     * @return 返回值
     */
    @PostMapping("/patients/{patientId}/risk-assessment")
    @ApiOperation(value = "评估患者风险分层")
    public Result<PatientRiskAssessment> assess(@PathVariable Long patientId) {
        return Result.success(clinicalDecisionService.assessPatientRisk(patientId));
    }

    /**
     * 生成AI随访建议（待医生确认）
     *
     * @param patientId 参数说明
     * @return 返回值
     */
    @PostMapping("/patients/{patientId}/suggestions")
    @ApiOperation(value = "生成AI随访建议（待医生确认）")
    public Result<FollowUpSuggestion> generate(@PathVariable Long patientId) {
        return Result.success(clinicalDecisionService.generateSuggestion(patientId));
    }

    /**
     * AI随访建议接口：输入最近随访记录，输出风险判断与建议
     *
     * @param request 参数说明
     * @return 返回值
     */
    @PostMapping("/ai-suggestion")
    @ApiOperation(value = "AI随访建议接口：输入最近随访记录，输出风险判断与建议")
    public Result<FollowUpSuggestion> aiSuggestion(@Valid @RequestBody AISuggestionRequest request) {
        return Result.success(clinicalDecisionService.generateAISuggestion(request));
    }

    /**
     * 分页查询随访建议
     *
     * @param page 参数说明
     * @param size 参数说明
     * @param status 参数说明
     * @return 返回值
     */
    @GetMapping("/suggestions")
    @ApiOperation(value = "分页查询随访建议")
    public Result<PageResponse<FollowUpSuggestion>> suggestions(@RequestParam(defaultValue = "1") Integer page,
                                                                @RequestParam(defaultValue = "20") Integer size,
                                                                @RequestParam(required = false) String status) {
        return Result.success(clinicalDecisionService.listSuggestions(page, size, status));
    }

    /**
     * 医生确认AI建议并落库
     *
     * @param id 参数说明
     * @return 返回值
     */
    @PutMapping("/suggestions/{id}/confirm")
    @ApiOperation(value = "医生确认AI建议并落库")
    @OperationLog(operation = "确认AI随访建议", targetType = "FollowUpSuggestion")
    public Result<Void> confirm(@PathVariable Long id) {
        clinicalDecisionService.confirmSuggestion(id);
        return Result.success();
    }

    /**
     * 医生驳回AI建议
     *
     * @param id 参数说明
     * @return 返回值
     */
    @PutMapping("/suggestions/{id}/reject")
    @ApiOperation(value = "医生驳回AI建议")
    @OperationLog(operation = "驳回AI随访建议", targetType = "FollowUpSuggestion")
    public Result<Void> reject(@PathVariable Long id) {
        clinicalDecisionService.rejectSuggestion(id);
        return Result.success();
    }
}
