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
import java.util.Objects;
import java.util.Optional;

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
    public void afterReturning(JoinPoint joinPoint, OperationLog operationLog, Object result) {
        try {
            CurrentUser currentUser = SecurityUtils.currentUser();
            Long targetId = resolveTargetId(Arrays.asList(joinPoint.getArgs())).orElse(null);
            operationLogService.log(
                    currentUser.getUserId(),
                    currentUser.getUsername(),
                    operationLog.operation(),
                    operationLog.targetType(),
                    targetId,
                    operationLog.detail()
            );
        } catch (BusinessException | DataAccessException e) {
            log.warn("Failed to record operation log: {}", e.getMessage());
        }
    }

    private Optional<Long> resolveTargetId(List<?> args) {
        return args.stream()
                .filter(arg -> arg instanceof Long || arg instanceof FollowUp)
                .map(arg -> arg instanceof Long ? (Long) arg : ((FollowUp) arg).getId())
                .filter(Objects::nonNull)
                .findFirst();
    }
}
