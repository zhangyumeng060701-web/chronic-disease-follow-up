/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.aop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.followup.annotation.OperationLog;
import com.example.followup.controller.FollowUpController;
import com.example.followup.controller.PatientController;
import com.example.followup.controller.UserController;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

class OperationLogAnnotationTest {
    @Test
    @DisplayName("随访新增/编辑/删除接口已配置自动日志注解")
    void followUpMethodsAreAnnotated() throws Exception {
        assertLog(FollowUpController.class.getMethod("add", com.example.followup.entity.FollowUp.class),
                "新增随访记录", "FollowUp");
        assertLog(FollowUpController.class.getMethod("update", Long.class, com.example.followup.entity.FollowUp.class),
                "编辑随访记录", "FollowUp");
        assertLog(FollowUpController.class.getMethod("delete", Long.class),
                "删除随访记录", "FollowUp");
    }

    @Test
    @DisplayName("患者新增/编辑/删除接口已配置自动日志注解")
    void patientMethodsAreAnnotated() throws Exception {
        assertLog(PatientController.class.getMethod("add", com.example.followup.dto.request.PatientSaveRequest.class),
                "新增患者", "Patient");
        assertLog(PatientController.class.getMethod("update", Long.class, com.example.followup.dto.request.PatientUpdateRequest.class),
                "编辑患者", "Patient");
        assertLog(PatientController.class.getMethod("delete", Long.class),
                "删除患者", "Patient");
    }

    @Test
    @DisplayName("用户新增/编辑/状态切换接口已配置自动日志注解")
    void userMethodsAreAnnotated() throws Exception {
        assertLog(UserController.class.getMethod("create", com.example.followup.dto.request.CreateUserRequest.class),
                "新增用户", "User");
        assertLog(UserController.class.getMethod("update", Long.class, com.example.followup.dto.request.UpdateUserRequest.class),
                "编辑用户", "User");
        assertLog(UserController.class.getMethod("toggleStatus", Long.class),
                "切换用户状态", "User");
    }

    private void assertLog(Method method, String operation, String targetType) {
        OperationLog annotation = method.getAnnotation(OperationLog.class);
        assertNotNull(annotation);
        assertEquals(operation, annotation.operation());
        assertEquals(targetType, annotation.targetType());
    }
}
