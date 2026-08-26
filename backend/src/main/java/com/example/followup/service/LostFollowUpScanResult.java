package com.example.followup.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LostFollowUpScanResult {
    private final int scannedCount;
    private final int yellowCreated;
    private final int redCreated;
    private final int yellowResolved;
    private final int skippedCount;
}
