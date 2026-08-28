/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * FollowUpInput 业务组件。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class FollowUpInput {
    private LocalDate followUpDate;
    private Integer systolicBp;
    private Integer diastolicBp;
    private BigDecimal fastingGlucose;
    private BigDecimal postprandialGlucose;
    private String medicationAdherence;
    private String symptoms;
}
