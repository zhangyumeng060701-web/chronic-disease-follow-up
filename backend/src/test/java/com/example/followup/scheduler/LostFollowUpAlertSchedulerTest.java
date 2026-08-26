package com.example.followup.scheduler;

import com.example.followup.service.LostFollowUpAlertService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LostFollowUpAlertSchedulerTest {
    @Test
    void delegatesScheduledExecutionToService() {
        LostFollowUpAlertService service = mock(LostFollowUpAlertService.class);
        LostFollowUpAlertScheduler scheduler = new LostFollowUpAlertScheduler(service);

        scheduler.generateLostFollowUpAlerts();

        verify(service).scanAndGenerateAlerts();
    }
}
