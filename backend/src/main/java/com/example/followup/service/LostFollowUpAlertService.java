package com.example.followup.service;

public interface LostFollowUpAlertService {
    LostFollowUpScanResult scanAndGenerateAlerts();

    int resolveOutstandingAlerts(Long patientId);
}
