/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.controller;

import com.example.followup.dto.request.PatientFollowUpRequest;
import com.example.followup.dto.request.QuestionnaireSubmitRequest;
import com.example.followup.dto.request.VitalReportRequest;
import com.example.followup.dto.response.FollowUpPlanVO;
import com.example.followup.dto.response.FollowUpVO;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.Message;
import com.example.followup.entity.PatientVital;
import com.example.followup.entity.Questionnaire;
import com.example.followup.service.PatientPortalService;

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

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

/**
 * PatientPortalController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/patient")
@Api(tags = "患者端")
public class PatientPortalController {
    @Autowired
    private PatientPortalService patientPortalService;

    @GetMapping("/plans")
    @ApiOperation(value = "查看我的随访计划")
    /**
     * 查看我的随访计划
     *
     * @return 返回值
     */
    public Result<List<FollowUpPlanVO>> plans() {
        return Result.success(patientPortalService.myPlans());
    }

    /**
     * 执行 riskLevel 操作。
     */
    @GetMapping("/risk-level")
    @ApiOperation(value = "查看我的风险等级")
    public Result<Map<String, Object>> riskLevel() {
        return Result.success(patientPortalService.myRiskLevel());
    }

    @GetMapping("/vitals")
    @ApiOperation(value = "查看我的指标记录")
    public Result<List<PatientVital>> vitals() {
        return Result.success(patientPortalService.myVitals());
    }

    /**
     * 执行 reportVital 操作。
     */
    @PostMapping("/vitals")
    @ApiOperation(value = "上报血压血糖")
    public Result<PatientVital> reportVital(@Valid @RequestBody VitalReportRequest request) {
        return Result.success(patientPortalService.reportVital(request));
    }

    @GetMapping("/questionnaires")
    @ApiOperation(value = "获取可用问卷")
    public Result<List<Questionnaire>> questionnaires() {
        return Result.success(patientPortalService.activeQuestionnaires());
    }

    /**
     * 执行 submitQuestionnaire 操作。
     */
    @PostMapping("/questionnaires/{id}/submit")
    @ApiOperation(value = "提交问卷")
    public Result<Void> submitQuestionnaire(@PathVariable Long id,
                                            @Valid @RequestBody QuestionnaireSubmitRequest request) {
        patientPortalService.submitQuestionnaire(id, request);
        return Result.success();
    }

    @GetMapping("/messages")
    @ApiOperation(value = "获取我的消息")
    public Result<List<Message>> messages() {
        return Result.success(patientPortalService.myMessages());
    }

    /**
     * 执行 unreadCount 操作。
     */
    @GetMapping("/messages/unread-count")
    @ApiOperation(value = "未读消息数")
    public Result<Map<String, Long>> unreadCount() {
        return Result.success(Map.of("count", patientPortalService.unreadMessageCount()));
    }

    @PutMapping("/messages/{id}/read")
    @ApiOperation(value = "标记消息已读")
    public Result<Void> markRead(@PathVariable Long id) {
        patientPortalService.markMessageRead(id);
        return Result.success();
    }

    /**
     * 执行 followUps 操作。
     */
    @GetMapping("/follow-ups")
    @ApiOperation(value = "查看我的随访记录")
    public Result<List<FollowUpVO>> followUps() {
        return Result.success(patientPortalService.myFollowUps());
    }

    @PostMapping("/follow-ups")
    @ApiOperation(value = "患者端自报随访")
    public Result<FollowUpVO> createFollowUp(@Valid @RequestBody PatientFollowUpRequest request) {
        return Result.success(patientPortalService.createPatientFollowUp(request));
    }
}
