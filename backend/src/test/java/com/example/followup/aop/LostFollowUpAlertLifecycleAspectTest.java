package com.example.followup.aop;

import com.example.followup.entity.FollowUp;
import com.example.followup.service.LostFollowUpAlertService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LostFollowUpAlertLifecycleAspectTest {
    @Test
    void resolvesOutstandingAlertAfterNewFollowUp() {
        LostFollowUpAlertService service = mock(LostFollowUpAlertService.class);
        LostFollowUpAlertLifecycleAspect aspect = new LostFollowUpAlertLifecycleAspect(service);
        FollowUp followUp = new FollowUp();
        followUp.setPatientId(42L);

        aspect.resolvePreviousCycleAfterFollowUp(followUp);

        verify(service).resolveOutstandingAlerts(42L);
    }
}
