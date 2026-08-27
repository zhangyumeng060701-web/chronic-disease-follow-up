package com.example.followup.aop;

import com.example.followup.annotation.OperationLog;
import com.example.followup.entity.FollowUp;
import com.example.followup.security.CurrentUser;
import com.example.followup.security.SecurityUtils;
import com.example.followup.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            Long targetId = resolveTargetId(joinPoint.getArgs());
            operationLogService.log(
                    currentUser.getUserId(),
                    currentUser.getUsername(),
                    operationLog.operation(),
                    operationLog.targetType(),
                    targetId,
                    operationLog.detail()
            );
        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        }
    }

    private Long resolveTargetId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
            if (arg instanceof FollowUp) {
                return ((FollowUp) arg).getId();
            }
        }
        return null;
    }
}
