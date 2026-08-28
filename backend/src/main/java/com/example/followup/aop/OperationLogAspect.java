/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.aop;

import com.example.followup.annotation.OperationLog;
import com.example.followup.entity.FollowUp;
import com.example.followup.exception.BusinessException;
import com.example.followup.security.CurrentUser;
import com.example.followup.security.SecurityUtils;
import com.example.followup.service.OperationLogService;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalLong;

/**
 * OperationLogAspect 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {
    @Autowired
    private OperationLogService operationLogService;

    @AfterReturning(
            pointcut = "@annotation(operationLog)",
            returning = "result"
    )
    /**
    * 执行 afterReturning 操作。
    */
    public void afterReturning(JoinPoint joinPoint, OperationLog operationLog, Object result) {
        try {
            CurrentUser currentUser = SecurityUtils.currentUser();
            OptionalLong targetId = resolveTargetId(Arrays.asList(joinPoint.getArgs()));
            operationLogService.log(
                    currentUser.getUserId(),
                    currentUser.getUsername(),
                    operationLog.operation(),
                    operationLog.targetType(),
                    targetId.isPresent() ? targetId.getAsLong() : null,
                    operationLog.detail()
            );
        } catch (BusinessException | DataAccessException e) {
            log.warn("Failed to record operation log: {}", e.getMessage());
        }
    }

    private OptionalLong resolveTargetId(List<?> args) {
        return args.stream()
                .filter(arg -> arg instanceof Long || arg instanceof FollowUp)
                .mapToLong(arg -> arg instanceof Long ? (Long) arg : ((FollowUp) arg).getId())
                .findFirst();
    }
}
