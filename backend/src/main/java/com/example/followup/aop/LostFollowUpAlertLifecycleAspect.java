package com.example.followup.aop;

import com.example.followup.entity.FollowUp;
import com.example.followup.service.LostFollowUpAlertService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LostFollowUpAlertLifecycleAspect {
    private final LostFollowUpAlertService lostFollowUpAlertService;

    @AfterReturning(
            pointcut = "execution(* com.example.followup.service.impl.FollowUpServiceImpl.addFollowUp(..)) && args(followUp)")
    public void resolvePreviousCycleAfterFollowUp(FollowUp followUp) {
        if (followUp != null && followUp.getPatientId() != null) {
            lostFollowUpAlertService.resolveOutstandingAlerts(followUp.getPatientId());
        }
    }
}
