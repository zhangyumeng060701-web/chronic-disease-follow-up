/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotNull;

/**
 * PatientFollowUpRequest 请求参数。
 *
 * @since 2026-07-27
 * @version 1.0.0
 */
@Data
public class PatientFollowUpRequest {
    @NotNull(message = "随访日期不能为空")
    private LocalDate followUpDate;

    private Integer systolicBp;
    private Integer diastolicBp;
    private BigDecimal fastingGlucose;
    private BigDecimal postprandialGlucose;
    private String medicationAdherence;
    private String symptoms;
    private String advice;
    private LocalDate nextFollowUpDate;
}
