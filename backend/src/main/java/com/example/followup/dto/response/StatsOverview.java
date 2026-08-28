/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * StatsOverview 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsOverview {
    private Long totalPatients;
    private Integer monthlyCompleted;
    private Integer monthlyExpected;
    private String completionRate;
    private Long highRiskCount;
    private Long lostFollowUpCount;
    private String planCompletionRate;
    private String followUpTaskCompletionRate;
    private String avgAlertResponseHours;
}
