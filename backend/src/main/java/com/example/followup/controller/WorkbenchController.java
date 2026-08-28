/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.followup.constant.DomainConstants;
import com.example.followup.dto.response.Result;
import com.example.followup.entity.Alert;
import com.example.followup.entity.FollowUpSuggestion;
import com.example.followup.entity.FollowUpTask;
import com.example.followup.entity.Patient;
import com.example.followup.mapper.AlertMapper;
import com.example.followup.mapper.FollowUpSuggestionMapper;
import com.example.followup.mapper.FollowUpTaskMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.security.SecurityUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * WorkbenchController HTTP 接口。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/dashboard")
@Api(tags = "医生工作台")
public class WorkbenchController {

    @Autowired
    private FollowUpTaskMapper taskMapper;
    @Autowired
    private AlertMapper alertMapper;
    @Autowired
    private FollowUpSuggestionMapper suggestionMapper;
    @Autowired
    private PatientMapper patientMapper;

/**
 * 执行 workbench 操作。
 */
    @GetMapping("/workbench")
    @ApiOperation(value = "医生工作台：今日任务、待处理预警、待确认AI建议")
    public Result<Map<String, Object>> workbench() {
        boolean admin = SecurityUtils.isAdmin();
        Long currentUserId = admin ? null : SecurityUtils.currentUser().getUserId();

        List<Long> managedPatientIds = currentUserId == null
                ? List.of()
                : patientMapper.selectList(new LambdaQueryWrapper<Patient>()
                        .select(Patient::getId)
                        .eq(Patient::getStatus, 1)
                        .eq(Patient::getDoctorId, currentUserId))
                .stream()
                .map(Patient::getId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (currentUserId != null && managedPatientIds.isEmpty()) {
            return Result.success(emptyWorkbench());
        }

        LambdaQueryWrapper<FollowUpTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(FollowUpTask::getDueDate, LocalDate.now())
                .in(FollowUpTask::getStatus,
                        DomainConstants.TASK_STATUS_PENDING,
                        DomainConstants.TASK_STATUS_IN_PROGRESS,
                        DomainConstants.TASK_STATUS_CONTACTED);
        if (!admin) {
            taskWrapper.eq(FollowUpTask::getOwnerId, currentUserId);
        }
        taskWrapper.orderByAsc(FollowUpTask::getDueDate).last("LIMIT 5");

        LambdaQueryWrapper<Alert> alertWrapper = new LambdaQueryWrapper<>();
        alertWrapper.eq(Alert::getIsResolved, 0)
                .orderByDesc(Alert::getCreateTime)
                .last("LIMIT 5");
        if (currentUserId != null) {
            alertWrapper.in(Alert::getPatientId, managedPatientIds);
        }

        LambdaQueryWrapper<FollowUpSuggestion> suggestionWrapper = new LambdaQueryWrapper<>();
        suggestionWrapper.eq(FollowUpSuggestion::getStatus, DomainConstants.SUGGESTION_STATUS_PENDING)
                .orderByDesc(FollowUpSuggestion::getCreateTime)
                .last("LIMIT 5");
        if (currentUserId != null) {
            suggestionWrapper.in(FollowUpSuggestion::getPatientId, managedPatientIds);
        }

        List<FollowUpTask> tasks = taskMapper.selectList(taskWrapper);
        List<Alert> alerts = alertMapper.selectList(alertWrapper);
        List<FollowUpSuggestion> suggestions = suggestionMapper.selectList(suggestionWrapper);

        Map<Long, String> patientNames = loadPatientNames(
                tasks.stream().map(FollowUpTask::getPatientId),
                alerts.stream().map(Alert::getPatientId),
                suggestions.stream().map(FollowUpSuggestion::getPatientId));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todayTasks", toTaskItems(tasks, patientNames));
        result.put("pendingAlerts", toAlertItems(alerts, patientNames));
        result.put("pendingSuggestions", toSuggestionItems(suggestions, patientNames));
        return Result.success(result);
    }

    private Map<String, Object> emptyWorkbench() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todayTasks", List.of());
        result.put("pendingAlerts", List.of());
        result.put("pendingSuggestions", List.of());
        return result;
    }

    private Map<Long, String> loadPatientNames(
            java.util.stream.Stream<Long> taskIds,
            java.util.stream.Stream<Long> alertIds,
            java.util.stream.Stream<Long> suggestionIds) {
        List<Long> patientIds = java.util.stream.Stream.concat(
                        java.util.stream.Stream.concat(taskIds, alertIds), suggestionIds)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (patientIds.isEmpty()) {
            return Map.of();
        }
        return patientMapper.selectBatchIds(patientIds).stream()
                .collect(Collectors.toMap(Patient::getId, Patient::getName, (a, b) -> a));
    }

    private List<Map<String, Object>> toTaskItems(List<FollowUpTask> tasks, Map<Long, String> patientNames) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (FollowUpTask task : tasks) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", task.getId());
            item.put("patientId", task.getPatientId());
            item.put("patientName", patientNames.getOrDefault(task.getPatientId(), ""));
            item.put("channel", task.getChannel());
            item.put("dueDate", task.getDueDate());
            item.put("status", task.getStatus());
            items.add(item);
        }
        return items;
    }

    private List<Map<String, Object>> toAlertItems(List<Alert> alerts, Map<Long, String> patientNames) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (Alert alert : alerts) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", alert.getId());
            item.put("patientId", alert.getPatientId());
            item.put("patientName", patientNames.getOrDefault(alert.getPatientId(), ""));
            item.put("alertLevel", alert.getAlertLevel());
            item.put("alertReason", alert.getAlertReason());
            item.put("createTime", alert.getCreateTime());
            items.add(item);
        }
        return items;
    }

    private List<Map<String, Object>> toSuggestionItems(List<FollowUpSuggestion> suggestions,
                                                        Map<Long, String> patientNames) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (FollowUpSuggestion suggestion : suggestions) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", suggestion.getId());
            item.put("patientId", suggestion.getPatientId());
            item.put("patientName", patientNames.getOrDefault(suggestion.getPatientId(), ""));
            item.put("riskLevel", suggestion.getRiskLevel());
            item.put("content", suggestion.getContent());
            items.add(item);
        }
        return items;
    }
}
