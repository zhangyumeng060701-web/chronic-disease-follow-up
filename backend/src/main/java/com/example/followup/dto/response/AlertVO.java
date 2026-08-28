/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */
package com.example.followup.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AlertVO 返回模型。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class AlertVO {
    private Long id;
    private Long patientId;
    private String patientName;
    private String alertType;
    private String alertLevel;
    private String alertReason;
    private Integer isResolved;
    private String alertStatus;
    private LocalDateTime contactTime;
    private String referralReason;
    private String recommendedActions;
    private String recheckItems;
    private String referralConditions;
    private String evidenceSource;
    private String riskLevel;
    private LocalDateTime resolveTime;
    private LocalDateTime createTime;
}
