/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * FollowUpPlanVO 返回模型。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class FollowUpPlanVO {
    private Long id;
    private Long patientId;
    private String patientName;
    private String riskLevel;
    private Integer followUpFrequencyDays;
    private String followUpType;
    private LocalDate nextFollowUpDate;
    private String status;
    private Long doctorId;
    private String doctorName;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
