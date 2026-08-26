package com.example.followup.scheduler;

import com.example.followup.service.LostFollowUpAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "follow-up.lost-alert", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LostFollowUpAlertScheduler {
    private final LostFollowUpAlertService lostFollowUpAlertService;

    @Scheduled(cron = "${follow-up.lost-alert.cron:0 0 2 * * ?}",
            zone = "${follow-up.lost-alert.zone:Asia/Shanghai}")
    public void generateLostFollowUpAlerts() {
        lostFollowUpAlertService.scanAndGenerateAlerts();
    }
}
